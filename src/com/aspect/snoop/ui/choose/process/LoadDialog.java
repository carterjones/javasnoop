package com.aspect.snoop.ui.choose.process;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

/**
 * Simple 'in progress' dialog.  This is really lame, but at least it allows the user to cancel classpath scanning.
 */
public class LoadDialog extends JDialog {

	JPanel loadPanel;
	JLabel loadLabel;

	JPanel primaryPanel;

	public JPanel getPrimaryPanel() {
		return primaryPanel;
	}

	public void setPrimaryPanel(JPanel primaryPanel) {
		this.primaryPanel = primaryPanel;
	}

	private ClasspathEntryScanner work;

	public LoadDialog(JPanel primaryPanel, List<String> sourceRoots, 
			ClasspathTreeChangeListener classpathTreeChangeListener, ChangeListener statusListener) {
		super(SwingUtilities.getWindowAncestor(primaryPanel), "Jarmination in progress...", Dialog.ModalityType.APPLICATION_MODAL);

		this.primaryPanel = primaryPanel;

		loadPanel = new JPanel();
		GridBagLayout gbLoad = new GridBagLayout();
		loadPanel.setLayout(gbLoad);

		loadLabel = new JLabel("");
		loadPanel.add(loadLabel);
		gbLoad.setConstraints(loadLabel,
				new GridBagConstraints(0, 0, 1, 1, 1, 1,
						GridBagConstraints.NORTH, GridBagConstraints.BOTH,
						new Insets(5, 5, 5, 5), 0, 0));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setContentPane(loadPanel);

		setSize(200, 80);
        setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		Dimension size = this.getSize();
		Dimension parentSize = primaryPanel.getSize();
		Point parentLocation = primaryPanel.getLocation();
		setLocation(parentLocation.x + (parentSize.width - size.width) / 2, parentLocation.y + (parentSize.height - size.height) / 2);
		loadLabel.setHorizontalAlignment(JLabel.CENTER);

		addWindowListener(new WindowAdapter() {
				/**
				 * Invoked when a window is in the process of being closed.
				 * The close operation can be overridden at this point.
				 */
				public void windowClosing(WindowEvent e) {
					int result = JOptionPane.showConfirmDialog(LoadDialog.this, "Do you want to quit the examination?", "Info", JOptionPane.YES_NO_OPTION);
					if (result == 0) {
						work.quit();
						dispose();
					}
				}
			}
		);
		work = new ClasspathEntryScanner(LoadDialog.this, sourceRoots, classpathTreeChangeListener, statusListener);
		work.setWillScanMethods( true );		// FIXME: Make the caller set this
		new Thread(work).start();
		pack();
		show();
	}
}
