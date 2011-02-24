package com.aspect.snoop.ui.components;

import com.aspect.snoop.ui.choose.process.LoadDialog;
import com.aspect.snoop.ui.choose.process.NewProcessInfoView;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

public class DroppableTabbedPane extends JTabbedPane implements DropTargetListener
{
	DropTarget dt = new DropTarget( this, this );

	NewProcessInfoView parent;


	public DroppableTabbedPane(NewProcessInfoView parent)
	{
		this.parent = parent;
	}


	public void dragEnter(DropTargetDragEvent dtde)
	{
		// System.out.println( "Drag Enter" );
		setBorder( BorderFactory.createLineBorder( Color.GRAY ) );
	}


	public void dragExit(DropTargetEvent dte)
	{
		// System.out.println( "Drag Exit" );
		setBorder( BorderFactory.createEmptyBorder() );
	}


	public void dragOver(DropTargetDragEvent dtde)
	{
		// System.out.println( "Drag Over" );
	}


	public void dropActionChanged(DropTargetDragEvent dtde)
	{
		// System.out.println( "Drop Action Changed" );
	}


	public void drop(DropTargetDropEvent dtde)
	{
		try
		{
			final DataFlavor FLAVOR_URILIST_READER = new DataFlavor( "text/uri-list;class=java.io.Reader" );

			// Ok, get the dropped object and try to figure out what it is
			Transferable tr = dtde.getTransferable();
			DataFlavor[] flavors = tr.getTransferDataFlavors();

			// Select best data flavor
			flavors = ( flavors.length == 0 ) ? dtde.getCurrentDataFlavors() : flavors; // Why?
			DataFlavor flavor = selectBestDataFlavor( flavors );
			System.out.println( "All flavors: " + Arrays.asList( flavors ) );
			System.out.println( "Selected flavor is " + flavor );

//			if ( flavor.equals( FLAVOR_URILIST_READER ) )
//			{
//				dtde.acceptDrop( DnDConstants.ACTION_COPY_OR_MOVE );
//
//				BufferedReader reader = new BufferedReader( flavor.getReaderForText( tr ) );
//				// Remove 'file://' from file name
//				String line = null;
//				while ( ( line = reader.readLine() ) != null )
//				{
//					System.out.println( "Line for file Dragged:" + line );
//
//					String fileName = line.substring( 7 ).replace( "%20", " " );
//
//					// Remove 'localhost' from OS X file names
//					if ( fileName.substring( 0, 9 ).equals( "localhost" ) )
//					{
//						fileName = fileName.substring( 9 );
//					}
//					System.out.println( "File Dragged:" + fileName );
//					if ( fileName.length() != 0 )
//					{
////						fileNames.add( fileName );
//						parent.addSourceRoot( fileName );
//					}
//					// mainWindow.openFile(fileName);
//				}
//				reader.close();
//				dtde.dropComplete( true );
//			}
//			// Check for file lists specifically
//			else if ( flavor.isFlavorJavaFileListType() )
//			{
//				// Great! Accept copy drops...
//				dtde.acceptDrop( DnDConstants.ACTION_COPY_OR_MOVE );
//				System.out.println( "Successful file list drop.\n\n" );
//
//				// And add the list of file names to our text area
//				List<File> list = (List<File>) tr.getTransferData( flavor );
//				for ( int j = 0; j < list.size(); j++ )
//				{
//					System.out.print( list.get( j ) + "\n" );
//					// FIXME: We need to append to the current
//					// JavaMethodBrowser.sourceRoots
//					// I think we recursively call JTree.getNextMatch() for the
//					// node value to be inserted, removing an
//					// element from the path each time. Then, call
//					// Model.insertNodeInto(newNode, matcheNode,
//					// matcheNode.getChildCount()).
//
//					parent.addSourceRoot( list.get( j ).getAbsolutePath() );
//				}
//				// If we made it this far, everything worked.
//				dtde.dropComplete( true );
//			}
//			// Ok, is it another Java object?
//			else if ( flavor.isFlavorSerializedObjectType() )
//			{
//				dtde.acceptDrop( DnDConstants.ACTION_COPY_OR_MOVE );
//				System.out.println( "Successful text drop.\n\n" );
//				Object o = tr.getTransferData( flavor );
//				System.out.print( "Object: " + o );
//				dtde.dropComplete( true );
//			}
//			// How about an input stream?
//			else if ( flavor.isRepresentationClassInputStream() )
//			{
//				dtde.acceptDrop( DnDConstants.ACTION_COPY_OR_MOVE );
//				System.out.println( "Successful text drop.\n\n" );
//				// ta.read( new InputStreamReader( (InputStream)
//				// tr.getTransferData( flavors[i] ) ),
//				// "from system clipboard" );
//				dtde.dropComplete( true );
//			}
//			else
//			{
//				// Hmm, the user must not have dropped a file list
//				System.out.println( "Drop failed: " + dtde );
//				dtde.rejectDrop();
//			}
//
//			if ( parent.getSourceRoots().size() > 0 )
//			{
//				new LoadDialog( parent, parent.getSourceRoots(), null, null );
//			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			dtde.rejectDrop();
		}
		setBorder( BorderFactory.createEmptyBorder() );
	}


	private DataFlavor selectBestDataFlavor(DataFlavor[] flavors) throws ClassNotFoundException
	{
		// Try the best text flavor
		DataFlavor textFlavor = DataFlavor.selectBestTextFlavor( flavors );
		if (textFlavor != null)
			return textFlavor;

		// Try java.util.List<File>
		for (DataFlavor flavor: flavors)
		{
			if (flavor.isFlavorJavaFileListType())
			{
				return flavor;
			}
		}

		// Try Serializable
		for (DataFlavor flavor: flavors)
		{
			if (flavor.isFlavorSerializedObjectType())
			{
				return flavor;
			}
		}

		// Try InputStream
		for (DataFlavor flavor: flavors)
		{
			if ( flavor.isRepresentationClassInputStream() )
			{
				return flavor;
			}
		}

		return null;
	}

}
