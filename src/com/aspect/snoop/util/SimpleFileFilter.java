package com.aspect.snoop.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Simple file filter.
 */
public class SimpleFileFilter extends FileFilter {

	private Map filters;
	private String description;
	private String fullDescription;
	private boolean useExtensionsInDescription = true;

	/**
	 * Creates a file filter. If no filters are added, then all
	 * files are accepted.
	 *
	 * @see #addExtension
	 */
	public SimpleFileFilter() {
		this.filters = new HashMap();
	}

	/**
	 * Creates a file filter that accepts files with the given extension.
	 * Example: new SimpleFileFilter("jpg");
	 *
	 * @see #addExtension
	 */
	public SimpleFileFilter(String extension) {
		this(extension, null);
	}

	/**
	 * Creates a file filter that accepts the given file type.
	 * Example: new SimpleFileFilter("jpg", "JPEG Image Images");
	 * <p/>
	 * Note that the "." before the extension is not needed. If
	 * provided, it will be ignored.
	 *
	 * @see #addExtension
	 */
	public SimpleFileFilter(String extension, String description) {
		this();
		if (extension != null) {
			addExtension(extension);
		}
		if (description != null) {
			setDescription(description);
		}
	}

	/**
	 * Creates a file filter from the given string array.
	 * Example: new SimpleFileFilter(String {"gif", "jpg"});
	 * <p/>
	 * Note that the "." before the extension is not needed adn
	 * will be ignored.
	 *
	 * @see #addExtension
	 */
	public SimpleFileFilter(String[] filters) {
		this(filters, null);
	}

	/**
	 * Creates a file filter from the given string array and description.
	 * Example: new SimpleFileFilter(String {"gif", "jpg"}, "Gif and JPG Images");
	 * <p/>
	 * Note that the "." before the extension is not needed and will be ignored.
	 *
	 * @see #addExtension
	 */
	public SimpleFileFilter(String[] filters, String description) {
		this();
		for (int i = 0; i < filters.length; i++) {
			// add filters one by one
			addExtension(filters[i]);
		}
		if (description != null) {
			setDescription(description);
		}
	}

	/**
	 * Return true if this file should be shown in the directory pane,
	 * false if it shouldn't.
	 * <p/>
	 * Files that begin with "." are ignored.
	 *
	 * @see #getExtension
	 */
	public boolean accept(File f) {
		if (f != null) {
			if (f.isDirectory()) {
				return true;
			}
			String extension = getExtension(f);
			if (extension != null && filters.get(getExtension(f)) != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return the extension portion of the file's name .
	 *
	 * @see #getExtension
	 * @see FileFilter#accept
	 */
	public String getExtension(File f) {
		if (f != null) {
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1) {
				return filename.substring(i + 1).toLowerCase();
			}
		}
		return null;
	}

	/**
	 * Adds a filetype "dot" extension to filter against.
	 * <p/>
	 * For example: the following code will create a filter that filters
	 * out all files except those that end in ".jpg" and ".tif":
	 * <p/>
	 * SimpleFileFilter filter = new SimpleFileFilter();
	 * filter.addExtension("jpg");
	 * filter.addExtension("tif");
	 * <p/>
	 * Note that the "." before the extension is not needed and will be ignored.
	 */
	public void addExtension(String extension) {
		if (filters == null) {
			filters = new HashMap(5);
		}
		filters.put(extension.toLowerCase(), this);
		fullDescription = null;
	}


	/**
	 * Returns the human readable description of this filter. For
	 * example: "JPEG and GIF Image Files (*.jpg, *.gif)"
	 *
	 * @see #setDescription
	 * @see #setExtensionListInDescription
	 * @see #isExtensionListInDescription
	 * @see FileFilter#getDescription
	 */
	public String getDescription() {
		if (fullDescription == null) {
			if (description == null || isExtensionListInDescription()) {
				fullDescription = description == null ? "(" : description + " (";
				// build the description from the extension list
				Iterator extensions = filters.keySet().iterator();
				fullDescription += "." + extensions.next();
				while (extensions.hasNext()) {
					fullDescription += ", ." + extensions.next();
				}
				fullDescription += ")";
			} else {
				fullDescription = description;
			}
		}
		return fullDescription;
	}

	/**
	 * Sets the human readable description of this filter. For
	 * example: filter.setDescription("Gif and JPG Images");
	 *
	 * @see #setDescription
	 * @see #setExtensionListInDescription
	 * @see #isExtensionListInDescription
	 */
	public void setDescription(String description) {
		this.description = description;
		fullDescription = null;
	}

	/**
	 * Determines whether the extension list (.jpg, .gif, etc) should
	 * show up in the human readable description.
	 * <p/>
	 * Only relevent if a description was provided in the constructor
	 * or using setDescription();
	 *
	 * @see #getDescription
	 * @see #setDescription
	 * @see #isExtensionListInDescription
	 */
	public void setExtensionListInDescription(boolean b) {
		useExtensionsInDescription = b;
		fullDescription = null;
	}

	/**
	 * Returns whether the extension list (.jpg, .gif, etc) should
	 * show up in the human readable description.
	 * <p/>
	 * Only relevent if a description was provided in the constructor
	 * or using setDescription();
	 *
	 * @see #getDescription
	 * @see #setDescription
	 * @see #setExtensionListInDescription
	 */
	public boolean isExtensionListInDescription() {
		return useExtensionsInDescription;
	}
}
