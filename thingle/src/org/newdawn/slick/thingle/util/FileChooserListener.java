package org.newdawn.slick.thingle.util;

import java.io.File;

/**
 * Description of a class responding to events occuring within the file chooser. The 
 * implementation is notified of file selection.
 * 
 * @author kevin
 */
public interface FileChooserListener {
	/**
	 * Notification that a given file was selected
	 * 
	 * @param file The file that was selected
	 */
	public void fileSelected(File file);
	
	/**
	 * Notification that the user cancelled the dialog
	 */
	public void chooserCanceled();
}
