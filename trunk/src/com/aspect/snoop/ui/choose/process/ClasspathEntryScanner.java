package com.aspect.snoop.ui.choose.process;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


class ClasspathEntryScanner implements Runnable {

	private final LoadDialog load;
	private final List<String> sourceRoots;
	private boolean willScanMethods = false;
	ClasspathTreeChangeListener classpathTreeChangeListener;
	ChangeListener statusListener;


	public boolean willScanMethods()
	{
		return willScanMethods;
	}

	public void setWillScanMethods(boolean willScanMethods)
	{
		this.willScanMethods = willScanMethods;
	}

	ClasspathEntryScanner(LoadDialog loadDialog, List<String> sourceRoots, 
			ClasspathTreeChangeListener classpathTreeChangeListener, ChangeListener statusListener) {
		this.load = loadDialog;
		this.sourceRoots = sourceRoots;
		this.classpathTreeChangeListener = classpathTreeChangeListener;
		this.statusListener = statusListener;
		NewProcessInfoView.quitResolving = false;
	}

	public void run() {
		doLoadApply();
	}

	public synchronized void quit() {
		NewProcessInfoView.quitResolving = true;
	}

	// ---------------------------------------------------------------- doLoadApply

	/**
	 * Simple alphabetic comparator.
	 */
	private Comparator alphabeticComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			String s1 = (String) o1;
			String s2 = (String) o2;
			return s1.toLowerCase().compareTo(s2.toLowerCase());
		}
	};

	/**
	 * Load or Apply main method.
	 * FIXME: This only does a complete rebuild of the trees.  Performance can be much better if we can insert
	 * into the trees.
	 */
	void doLoadApply() {
		long time = System.currentTimeMillis();
		load.loadLabel.setText("Scanning source JARs");
		String[] jars = resolveJarsPaths(sourceRoots);
		if (jars == null) {
			load.dispose();
			return;
		}
		load.loadLabel.setText("Sorting JARs");
		Arrays.sort(jars, alphabeticComparator);

		DefaultMutableTreeNode jarsTreeRootNode = new DefaultMutableTreeNode(null);		// invisible root
		DefaultMutableTreeNode classesTreeRootNode = new DefaultMutableTreeNode(null);	// invisible root
		DefaultMutableTreeNode child;
		int count = 0;
		int errors = 0;
		int jarCount = 0;
		int i;
		for (i = 0; i < jars.length; i++) {
			if (NewProcessInfoView.quitResolving == true) {
				break;
			}
			load.loadLabel.setText("Examine JAR " + (i + 1) + '/' + jars.length);

			String[] paths = null;
			try {
				if (willScanMethods())
					paths = resolveMethodsInJar(jars[i], null);
				else
					paths = resolveClassesInJar(jars[i], null);
			} catch (JarScanningException jex) {
				jex.printStackTrace();
				errors++;			// count JAR errors
			}
			if (paths != null) {
				String value = jars[i];
				/*if (value.startsWith(userDir)) {
					value =	value.substring(userDir.length());
					if (value.charAt(0) == File.separatorChar) {
						value = value.substring(1);
					}
				}*/
				child = new DefaultMutableTreeNode(value);
				jarsTreeRootNode.add(child);
				jarCount++;
				filterJarClasses(value, child, paths);
				filterJarClasses(value, classesTreeRootNode, paths);
				count += paths.length;
			}
		}
		// FIXME: Better to have the NewProcessInfoView give some listeners to this object that perform these actions
		ClasspathTreeChangeEvent classpathTreeChangeEvent = new ClasspathTreeChangeEvent(
				new DefaultTreeModel(jarsTreeRootNode), new DefaultTreeModel(classesTreeRootNode));
		if (classpathTreeChangeListener != null) {
			classpathTreeChangeListener.stateChanged(classpathTreeChangeEvent);
		}
		time = System.currentTimeMillis() - time;
		StringBuffer sb = new StringBuffer(200);
		sb.append("<html><small>");
		sb.append("Total/Visible jars: ").append(i).append('/').append(jarCount);
		if (errors != 0) {
			sb.append("(errors: ").append(errors).append(") ");
		}
		sb.append("&nbsp; Total files: ").append(count);
//		sb.append("<br>Jarmination time: ").append(time).append("ms.");
		sb.append("</small></html>");
		if (statusListener != null) {
			statusListener.stateChanged(new ChangeEvent(sb.toString()));
		}
//		load.getPrimaryPanel().statusLabel.setText(sb.toString());
		load.dispose();
	}

	// ---------------------------------------------------------------- filter jar classes

	private void filterJarClasses(String jarName, DefaultMutableTreeNode node, String[] paths) {

		for (int i = 0; i < paths.length; i++) {

			DefaultMutableTreeNode parent = node;
			StringTokenizer st = new StringTokenizer(paths[i], "/");
			while (st.hasMoreTokens()) {
				String value = st.nextToken();
				boolean lastToken = !st.hasMoreTokens();	// is new element the last

				// [1] examine if children already exist
				boolean exist = false;
				boolean duplicate = false;
				Enumeration<DefaultMutableTreeNode> e = parent.children();
				while (e.hasMoreElements()) {
					DefaultMutableTreeNode dtm = e.nextElement();
					String dtmName = dtm.toString();
					if ((dtmName != null) && (dtmName.equals(value))) {
						if (lastToken == false) {		// allow duplicated leafs
							exist = true;				// so only skip duplicated folders
							parent = dtm;
							break;
						} else {
							duplicate = true;			// duplicate found
							break;
						}
					}
				}

				// [2] add children
				if (exist == false) {
					int index = 0;
					boolean found = false;
					e = parent.children();
					while (e.hasMoreElements()) {
						DefaultMutableTreeNode dtm = (DefaultMutableTreeNode) e.nextElement();
						String dtmName = dtm.toString();
						if (dtmName != null) {
							if (dtm.isLeaf() && !lastToken) {	// current element is leaf and new element is not
								index = parent.getIndex(dtm);	// therefore, insert folder above all leafs
								found = true;
								break;
							}
							if (!dtm.isLeaf() && lastToken) {	// current element is folder and new elemebt is leaf
								continue;						// therefore, skip all folders
							}
							if (alphabeticComparator.compare(dtmName, value) >= 0) {
								index = parent.getIndex(dtm);
								found = true;
								break;
							}
						}
						index++;
					}

					DefaultMutableTreeNode child;
					if (lastToken == false) {
						child = new DefaultMutableTreeNode(value);						// default class for folders
					} else {
						child = new DoubleStringTreeNode(value, jarName, duplicate);	// special class for leafs
					}
					if (found == false) {		// place where to insert was not found
						parent.add(child);
					} else {					// insertion place found
						if (duplicate) {		// move duplicates below first
							index++;
						}
						parent.insert(child, index);
					}
					parent = child;
				}
			}
		}
	}

	private static String[] jarsPaths;      // cached jar paths

	/**
	 * Returns a string array of founded jars paths. If root is <code>null</code>,
	 * cached data will be returned.
	 */
	public static String[] resolveJarsPaths(List<String> sourceRoots) {
		if (sourceRoots == null) {
			return jarsPaths;
		}

		List<String> jarList = new ArrayList<String>();
//		StringTokenizer st = new StringTokenizer(sourceRoots, ";");

		for (String fileName: sourceRoots) {
//		while (st.hasMoreTokens()) {
			if (NewProcessInfoView.quitResolving == true) {
				break;
			}
			//String fileName = st.nextToken().trim();
			File file = new File(fileName);
			if (file.exists() == true) {
				if ((file.isFile() == true) && (fileName.endsWith(".jar"))) {
					jarList.add(file.getAbsolutePath());
				} else if (file.isDirectory() == true) {
					createFileList(jarList, file);
				}
			}
		}
		if (jarList.isEmpty()) {
			jarsPaths = null;
			return null;
		}

		jarsPaths = new String[jarList.size()];
		for (int i = 0; i < jarList.size(); i++) {
			jarsPaths[i] = (String) jarList.get(i);
		}
		return jarsPaths;
	}

	/**
	 * Recursivly creates jars list in a folder.
	 */
	private static void createFileList(List<String> jarList, File folder) {
		File[] childs = folder.listFiles();
		for (int i = 0; i < childs.length; i++) {
			if (NewProcessInfoView.quitResolving == true) {
				break;
			}
			if (childs[i].isDirectory() == true) {
				createFileList(jarList, childs[i]);
			} else {
				String filename = childs[i].getAbsolutePath();
				if (filename.endsWith(".jar") == true) {
					jarList.add(filename);
				}
			}
		}
	}


	// ---------------------------------------------------------------- find class in jar

	/**
	 * Resolves jar content.
	 */
	public static String[] resolveClassesInJar(String jarName, String name) throws JarScanningException {
		ArrayList classNames = new ArrayList();
		if (name != null) {
			name = name.toLowerCase();
		}
		try {
			JarFile jar = new JarFile(jarName);
			Enumeration jarEntries = jar.entries();
			while (jarEntries.hasMoreElements()) {
				if (NewProcessInfoView.quitResolving == true) {
					break;
				}
				JarEntry entry = (JarEntry) jarEntries.nextElement();
				if (entry.isDirectory() == true) {
					continue;
				}
				if (name == null) {
					classNames.add(entry.getName());
				} else {
					if (entry.getName().toLowerCase().indexOf(name) != -1) {
						classNames.add(entry.getName());
					}
				}
			}
		} catch (Exception ex) {
			throw new JarScanningException("JAR examination error: " + jarName + '\n' + ex.toString());
		}
		if (classNames.isEmpty()) {
			return null;
		}
		String[] result = new String[classNames.size()];
		for (int i = 0; i < classNames.size(); i++) {
			result[i] = (String) classNames.get(i);
		}
		return result;
	}

	/**
	 * Resolves jar content.
	 */
	public static String[] resolveMethodsInJar(String jarName, String name) throws JarScanningException {
		ArrayList<String> methodNames = new ArrayList<String>();
		if (name != null) {
			name = name.toLowerCase();
		}
		try {
			JarFile jar = new JarFile(jarName);
			Enumeration jarEntries = jar.entries();
			while (jarEntries.hasMoreElements()) {
				if (NewProcessInfoView.quitResolving == true) {
					break;
				}
				JarEntry entry = (JarEntry) jarEntries.nextElement();
				if (entry.isDirectory() == true) {
					continue;
				}
				String entryName = entry.getName();
				System.out.println("Resolving JAR entry: " + entryName);
				if (name != null && entryName.toLowerCase().indexOf(name) == -1) {
					// Skip it
				}
				else {
					if (entryName.endsWith(".class")) {
						String className = entryName.replaceAll("/", "\\.");
						className = className.substring( 0, className.length() - 6 );

						// FIXME: Use Javassist instead of Reflection since actually loading the class might not be possible
						// at this time.
						ClassPool pool = ClassPool.getDefault();
						CtClass cc = pool.get(className);
						CtMethod[] methods = cc.getDeclaredMethods();
						for (CtMethod method : methods) {
							methodNames.add(entryName + "/" + method.toString());
						}

//						Original Code...
//						try {
//							Class clazz = getClass(new File(jarName), className);
//							if (clazz != null) {
//								Method[] methods = clazz.getDeclaredMethods();
//								for (Method method : methods) {
//									methodNames.add(entryName + "/" + method.toGenericString());
//								}
//							}
//						}
//						catch (Error e) {
//							e.printStackTrace();
//							//JOptionPane.showMessageDialog( null, e.getMessage(), "Missing Class", JOptionPane.ERROR_MESSAGE );
//						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new JarScanningException("JAR examination error: " + jarName + '\n' + ex.toString());
		}
		if (methodNames.isEmpty()) {
			return null;
		}
		String[] result = new String[methodNames.size()];
		for (int i = 0; i < methodNames.size(); i++) {
			result[i] = (String) methodNames.get(i);
		}
		return result;
	}

	public static Class getClass(File file, String name) {

		Class clazz = null;
		URLClassLoader clazzLoader;
		String filePath = file.getAbsolutePath();
		// Make sure the jarfile is added to our classpath
		try
		{
			addJarToClasspath(new File(filePath).toURI().toURL());
		}
		catch (MalformedURLException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Load the class
		filePath = "jar:file://" + filePath + "!/";
		URL url;
		try
		{
			url = new File(filePath).toURI().toURL();
			clazzLoader = new URLClassLoader(new URL[]{url});
			clazz = clazzLoader.loadClass(name);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Not a class: " + name);
			e.printStackTrace();
		}

		return clazz;
	}

	public static void addJarToClasspath(URL u) throws IOException {

		URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		URL urls[] = sysLoader.getURLs();
		for (int i = 0; i < urls.length; i++) {
			if (urls[i].toString().equalsIgnoreCase(u.toString())) {
				System.out.println("URL " + u + " is already in the CLASSPATH");
				return;
			}
		}
		Class<URLClassLoader> sysclass = URLClassLoader.class;
		try {
			Method method = sysclass.getDeclaredMethod("addURL", new Class[]{URL.class});
			method.setAccessible(true);
			method.invoke(sysLoader, new Object[]{u});
			System.out.println("Added URL " + u + " to the CLASSPATH");	// DEBUG
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}
	}

}


