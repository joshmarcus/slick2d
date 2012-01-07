package org.newdawn.slick.thingle.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.filechooser.FileSystemView;

import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Theme;
import org.newdawn.slick.thingle.Thingle;
import org.newdawn.slick.thingle.Widget;
import org.newdawn.slick.thingle.internal.Thinlet;
import org.newdawn.slick.thingle.spi.ThingleException;

/**
 * A file chooser emulating the swing one but rendered using the Thinlet implementation
 * for slick.
 * 
 * @author kevin
 */
public class FileChooser {
	/** The theme applied to the dialog */
	private Theme theme = new Theme();
	/** The page used to render the dialog */
	private Page page;
	/** True if the GUI is visible */
	private boolean visible = false;
	/** The file system view used to safely interegate the file system */
	private FileSystemView view = FileSystemView.getFileSystemView();
	/** The directory the file chooser is pointing at */
	private File currentDir = view.getDefaultDirectory();
	/** The thinlet component for the dialog */
	private Widget chooserDialog;
	/** The thinlet component for the list of files */
	private Widget filesList;
	/** The thinlet component for the path combo */
	private Widget pathCombo;
	/** The thinlet component for the text field */
	private Widget field;
	
	/** The list of files shown in the path selection */
	private ArrayList pathShown = new ArrayList();
	/** The list of files shown in the central list view */
	private ArrayList filesListed = new ArrayList();
	
	/** The title to be given to the dialog */
	private String title = "Save";
	/** The listener to be notified of dialog events */
	private FileChooserListener listener;
	/** True if you're allowed to select directories as well as files */
	private boolean directorySelectionAllowed = false;
	/** The label to place on the select button */
	private String select = "Open";
	
	/**
	 * Create a new file chooser
	 * 
	 * @param title The title to display in the dialog 
	 * @param select The label to place on the OK button
	 * @param listener The listener to be notified of dialog events
 	 * @throws ThingleException Indicates a failure to load required resources
	 */
	public FileChooser(String title, String select, FileChooserListener listener) throws ThingleException {
		this(title, select, FileSystemView.getFileSystemView().getDefaultDirectory(), listener);
	}

	/**
	 * Create a new file chooser
	 * 
	 * @param currentDir The directory to display the dialog focused on
	 * @param title The title to display in the dialog 
	 * @param select The label to place on the OK button
	 * @param listener The listener to be notified of dialog events
 	 * @throws ThingleException Indicates a failure to load required resources
	 */
	public FileChooser(String title, String select, File currentDir, FileChooserListener listener) throws ThingleException {
		this.listener = listener;
		this.title = title;
		this.currentDir = currentDir;
		
		page = new Page("utilres/filechooser.xml", this);
		page.setTheme(theme);
		page.setDrawDesktop(false);
	}
	
	/**
	 * Set the theme to be used
	 * 
	 * @param theme The theme to be used
	 */
	public void setTheme(Theme theme) {
		this.theme = theme;
		page.setTheme(theme);
	}
	
	/**
	 * Indicate whether the user can select directories
	 * 
	 * @param allowed True if they can select directories
	 */
	public void setDirectorySelectionAllowed(boolean allowed) {
		directorySelectionAllowed = allowed;
	}
	
	/**
	 * THINLET CALLBACK - Don't use.
	 * 
	 * Initialises the select button
	 * 
	 * @param button The button being selected
	 */
	public void initSelect(Widget button) {
		button.setText(select);
	}
	
	/**
	 * THINLET CALLBACK - Don't use.
	 * 
	 * Initialise the list 
	 * 
	 * @param list The list to initialise
	 */
	public void initList(Widget list) {
		this.filesList = list;
	}

	/**
	 * THINLET CALLBACK - Don't use.
	 * 
	 * Initialise the dialog 
	 * 
	 * @param page The page displaying the chooser
	 * @param dialog The dialog to initialise
	 */
	public void initDialog(Page page, Widget dialog) {
		this.chooserDialog = dialog;
		this.page = page;
		
		dialog.setText(title);
	}

	/**
	 * THINLET CALLBACK - Don't use.
	 * 
	 * Initialise the field 
	 * 
	 * @param field The field to initialise
	 */
	public void initField(Widget field) {
		this.field = field;
	}

	/**
	 * THINLET CALLBACK - Don't use.
	 * 
	 * Invoke the cancel operation, clearing the dialog and
	 * reporting the cancellation.
	 */
	public void cancel() {
		page.disable();
		visible = false;
		page.remove(chooserDialog);
		
		listener.chooserCanceled();
	}
	
	/**
	 * Invoke selection of the file notifying the listener
	 * 
	 * @param file The file that has been selected
	 */
	private void selectFile(File file) {
		page.disable();
		visible = false;
		page.remove(chooserDialog);
		
		listener.fileSelected(file);
	}
	
	/**
	 * THINLET CALLBACK - Don't use.
	 * 
	 * An entry has been selected from the GUI or by typing it in
	 * 
	 * @param source The component the event came from
	 */
	public void selectEntry(Widget source) {
		boolean doubleClick = source == filesList;
		
		for (int i = filesList.getChildrenCount() - 1; i >= 0; i--) {
			Widget item = filesList.getChild(i);
			if (item.getBoolean("selected")) {
				// selected item
				File file = (File) filesListed.get(i);
				if (file.isDirectory() && (doubleClick || !directorySelectionAllowed)) {
					currentDir = file;
					populateChooser(pathCombo);
				} else {
					selectFile(file);
				}
				return;
			}
		}
	}
	

	/**
	 * THINLET CALLBACK - Don't use.
	 * 
	 * Fill the file list with data
	 */
	public void populateList() {
		Object fileList = filesList;
		filesList.removeChildren();
		
		File[] files = currentDir.listFiles();
		filesListed.clear();
		
		ArrayList allFiles = new ArrayList();
		for (int i=0;i<files.length;i++) {
			allFiles.add(files[i]);
		}
		
		Collections.sort(allFiles, new Comparator() {

			public int compare(Object arg0, Object arg1) {
				File file1 = (File) arg0;
				File file2 = (File) arg1;
				
				if (file1.isDirectory() && !file2.isDirectory()) {
					return -1;
				}
				if (file2.isDirectory() && !file1.isDirectory()) {
					return 1;
				}
				
				String name1 = view.getSystemDisplayName(file1);
				String name2 = view.getSystemDisplayName(file2);
				
				if (name1.startsWith(".") && !name2.startsWith(".")) {
					return -1;
				}
				if (!name1.startsWith(".") && name2.startsWith(".")) {
					return 1;
				}
				if ((file1.getName().length() == 0) && (file2.getName().length() != 0)) {
					return -1;
				}
				if ((file2.getName().length() == 0) && (file1.getName().length() != 0)) {
					return 1;
				}
				
				return name1.compareToIgnoreCase(name2);
			}
			
		});
			
		for (int i=0;i<allFiles.size();i++) {
//			if (view.isHiddenFile(files[i])) {
//				continue;
//			}
			File file = (File) allFiles.get(i);
			
			Widget item = page.createWidget("item");
			String text = view.getSystemDisplayName(file);
			item.setText(text);
			filesListed.add(file);
				
			if (file.isDirectory()) {
				if (file.getName().equals("")) {
					item.setIcon("utilres/disk.gif");
				} else {
					item.setIcon("utilres/folder.gif");
				}
			} else {
				item.setIcon("utilres/document.gif");
			}
			
			filesList.add(item);
		}
	}
	

	/**
	 * THINLET CALLBACK - Don't use.
	 * 
	 * Go up a directory.
	 */
	public void goUp() {
		File parent = view.getParentDirectory(currentDir);
		if (parent != null) {
			currentDir = parent;
			populateChooser(pathCombo);
		}
	}

	/**
	 * THINLET CALLBACK - Don't use.
	 * 
	 * Notification that an file has been selected from the list
	 * 
	 * @param list The list selected from
	 */
	public void enterEntry(Widget list) {
		for (int i = filesList.getChildrenCount() - 1; i >= 0; i--) {
			Widget item = filesList.getChild(i);
			if (item.getBoolean("selected")) {
				File file = (File) filesListed.get(i);
				if ((!file.isDirectory() || directorySelectionAllowed)) {
					field.setText(file.getName());
				}
			}
		}
	}

	/**
	 * Provess the entry of a file location
	 * 
	 * @param file The location provided
	 */
	private void processFileEntry(File file) {
		if (file.isDirectory()) {
			currentDir = file;
			populateChooser(pathCombo);
			field.setText("");
		} else {
			selectFile(file);
			field.setText("");
		}
	}

	/**
	 * THINLET CALLBACK - Don't use.
	 * 
	 * Notification that an file has been selected by entering it's name
	 * 
	 * @param field The field that the text was entered into
	 */
	public void enterText(Widget field) {
		String text = field.getText();
		if (text.trim().length() == 0) {
			return;
		}
		
		File local = new File(currentDir, text);
		if (local.exists()) {
			processFileEntry(local);
			return;
		}
		File abs = new File(text);
		if (abs.exists()) {
			processFileEntry(abs);
			return;
		}
	}
	
	/**
	 * THINLET CALLBACK - Don't use.
	 * 
	 * Update the chooser combo
	 * 
	 * @param combo The combo to be updated
	 */
	public void changeChooser(Widget combo) {
		int index = combo.getSelectedIndex(); 
		currentDir = (File) pathShown.get(index);
		
		populateChooser(combo);
	}

	/**
	 * THINLET CALLBACK - Don't use.
	 * 
	 * Fill the combo with fresh data based on the current directory
	 * 
	 * @param combo The combo to be updated
	 */
	public void populateChooser(Widget combo) {
		this.pathCombo = combo;
		
		pathShown.clear();
		combo.removeChildren();
		
		File[] files = view.getRoots();
		
		ArrayList path = new ArrayList();
		File temp = currentDir;
		while (!view.isRoot(temp)) {
			path.add(0, temp);
			temp = view.getParentDirectory(temp);
			if (temp == null) {
				break;
			}
		}
		
		if (temp != null) {
			path.add(0, temp);
		}
		
		int index = 0;
		boolean displayed = false;
		for (int i=0;i<files.length;i++) {
			Widget item = page.createWidget("choice");
			item.setText(view.getSystemDisplayName(files[i]));
			
			combo.add(item);
			pathShown.add(files[i]);
			if (currentDir.equals(files[i])) {
				combo.setInteger("selected", index);
			}
			index++;

			if (path.get(0).equals(files[i])) {
				displayed = true;
				String prefix = "  ";
				for (int j=1;j<path.size();j++) {
					item = page.createWidget("choice");
					String entry = view.getSystemDisplayName((File) path.get(j));
					item.setText(prefix + entry);
					combo.add(item);
					pathShown.add((File) path.get(j));
					prefix += "  ";
					
					combo.setInteger("selected", index);
					combo.setText(entry);
					index++;
				}
			}
		}
		
		if (!displayed) {
			String prefix = "  ";
			for (int j=0;j<path.size();j++) {
				Widget item = page.createWidget("choice");
				String entry = view.getSystemDisplayName((File) path.get(j));
				item.setText(prefix + entry);
				combo.add(item);
				pathShown.add((File) path.get(j));
				prefix += "  ";
				
				combo.setInteger("selected", index);
				combo.setText(entry);
				index++;
			}
		}
		
		populateList();
	}
	
	/**
	 * Show the dialog
	 */
	public void show() {
		visible = true;
		page.enable();
	}
	
	/**
	 * Render the dialog - should be called in the game loop
	 */
	public void render() {
		if (visible) {
			page.render();
		}
	}
	
	/**
	 * Check if the dialog is visible
	 * 
	 * @return True if the dialog is visible
	 */
	public boolean isVisible() {
		return visible;
	}
}
