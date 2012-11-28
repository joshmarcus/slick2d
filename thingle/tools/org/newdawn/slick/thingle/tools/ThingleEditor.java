
package org.newdawn.slick.thingle.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.apache.batik.util.gui.xmleditor.XMLEditorKit;
import org.apache.batik.util.gui.xmleditor.XMLTextEditor;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.CanvasGameContainer;
import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Theme;
import org.newdawn.slick.thingle.Thingle;
import org.newdawn.slick.thingle.Widget;
import org.newdawn.slick.thingle.internal.slick.SlickThinletFactory;
import org.newdawn.slick.thingle.spi.ThingleColor;

/**
 * Displays a visual representation of thingle XML as it is typed.
 * 
 * @author Nathan Sweet <misc@n4te.com>
 */
public class ThingleEditor extends JFrame {
	private Page page;
	private Set slickTasks = new HashSet();
	private SlickThinletFactory thingleContext;
	private Theme theme;
	private JFileChooser fileChooser;
	private boolean isLoaded, isXmlFileDirty;
	private File currentFile;
	private String originalXML, currentDir = ".";
	private UndoManager undoManager = new UndoManager();
	private UndoAction undoAction = new UndoAction();
	private RedoAction redoAction = new RedoAction();

	private Game game = new BasicGame("Thingle Editor") {
		private int width, height;

		public void init (GameContainer container) throws SlickException {
			container.setShowFPS(false);
			container.setTargetFrameRate(200);
			thingleContext = new SlickThinletFactory(container);
			Thingle.init(thingleContext);
			page = new Page();
			page.setDrawDesktop(false);
			page.enable();
			theme = new Theme();
			page.setTheme(theme);
			hookEvents();
			EventQueue.invokeLater(new Runnable() {
				public void run () {
					loadSettings();
				}
			});
		}

		public void render (GameContainer container, Graphics g) throws SlickException {
			page.render();
		}

		public void update (GameContainer container, int delta) throws SlickException {
			Object[] tasks;
			synchronized (slickTasks) {
				tasks = slickTasks.toArray();
			}
			for (int i = 0; i < tasks.length; i++)
				((Runnable)tasks[i]).run();
			synchronized (slickTasks) {
				for (int i = 0; i < tasks.length; i++)
					slickTasks.remove(tasks[i]);
			}

			if (width != container.getWidth() || height != container.getHeight()) {
				width = container.getWidth();
				height = container.getHeight();
				page.layout();
			}
		}
	};

	/**
	 * Creates and displays the editor.
	 * @throws SlickException Thrown if the editor fails to initialize.
	 */
	public ThingleEditor () throws SlickException {
		super("Thingle Editor");

		initialize();

		CanvasGameContainer container = new CanvasGameContainer(game) {
			private Dimension dimension = new Dimension(1, 1);

			public int getWidth () {
				int width = super.getWidth();
				if (width <= 0) width = 1;
				return width;
			}

			public int getHeight () {
				int height = super.getHeight();
				if (height <= 0) height = 1;
				return height;
			}

			public Dimension getMinimumSize () {
				return dimension;
			}

			public Dimension getPreferredSize () {
				return dimension;
			}
		};
		previewPanel.add(container, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
			GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

		setVisible(true);
		container.start();
	}

	private void hookEvents () {
		abstract class ChangeListener implements DocumentListener {
			public void removeUpdate (DocumentEvent evt) {
				addChangedTask();
			}

			public void insertUpdate (DocumentEvent evt) {
				addChangedTask();
			}

			public void changedUpdate (DocumentEvent evt) {
				addChangedTask();
			}

			private void addChangedTask () {
				synchronized (slickTasks) {
					slickTasks.add(new Runnable() {
						public void run () {
							changed();
						}
					});
				}
			}

			abstract void changed ();
		}

		final ChangeListener xmlChangeListener = new ChangeListener() {
			void changed () {
				((TitledBorder)xmlPanel.getBorder()).setTitle("XML");
				setXmlFileDirty(!xmlEditor.getText().equals(originalXML));
				try {
					if (xmlEditor.getText().length() != 0) {
						Widget widget = page.parse(xmlEditor.getText(), null);
						page.getDesktop().removeChildren();
						page.add(widget);
						page.layout();
					}
					saveSettings();
				} catch (Exception ex) {
					((TitledBorder)xmlPanel.getBorder()).setTitle("XML (error)");
				}
				xmlPanel.repaint();
			}
		};
		xmlEditor.getDocument().addDocumentListener(xmlChangeListener);

		drawDesktopCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged (ChangeEvent evt) {
				page.setDrawDesktop(drawDesktopCheckBox.isSelected());
				saveSettings();
			}
		});

		final ChangeListener fontChangedListener = new ChangeListener() {
			void changed () {
				configTabs.setTitleAt(1, "Page");
				try {
					if (fontTextField.getText().length() == 0) {
						page.setFont(thingleContext.getDefaultFont());
					} else {
						String[] text = fontTextField.getText().toLowerCase().split(",");
						if (text.length == 2) {
							page.setFont(thingleContext.createBitmapFont(text[0].trim(), text[1].trim()));
						} else if (text.length == 3) {
							int style = 0;
							if (text[1].contains("plain")) style |= Font.PLAIN;
							if (text[1].equalsIgnoreCase("bold")) style |= Font.BOLD;
							if (text[1].equalsIgnoreCase("italic")) style |= Font.ITALIC;
							page.setFont(thingleContext.createFont(text[0].trim(), style, Integer.parseInt(text[2].trim())));
						} else
							throw new IllegalArgumentException();
					}
					xmlChangeListener.changed();
				} catch (Exception ex) {
					configTabs.setTitleAt(1, "Page (error)");
				}
				configTabs.repaint();
			}
		};
		fontTextField.getDocument().addDocumentListener(fontChangedListener);

		skinTextField.getDocument().addDocumentListener(new ChangeListener() {
			void changed () {
				configTabs.setTitleAt(1, "Page");
				try {
					Page newPage = new Page();
					newPage.setDrawDesktop(drawDesktopCheckBox.isSelected());
					newPage.enable();
					newPage.setTheme(theme);
					if (skinTextField.getText().length() != 0) newPage.loadSkin(skinTextField.getText());
					page = newPage;
					fontChangedListener.changed();
				} catch (Exception ex) {
					configTabs.setTitleAt(1, "Page (error)");
				}
				configTabs.repaint();
			}
		});

		class ThemeChangeListener extends ChangeListener {
			private JTextField textField;
			private Method setMethod, getMethod;

			public ThemeChangeListener (JTextField textField, String methodName) {
				this.textField = textField;
				textField.getDocument().addDocumentListener(this);
				try {
					setMethod = theme.getClass().getMethod("set" + methodName, new Class[] {ThingleColor.class});
					getMethod = theme.getClass().getMethod("get" + methodName, new Class[0]);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}

			void changed () {
				configTabs.setTitleAt(2, "Theme");
				try {
					ThingleColor color = null;
					String value = textField.getText();
					if (value.length() == 0) {
						setMethod.invoke(theme, new Object[] {getMethod.invoke(new Theme(), new Object[0])});
					} else {
						int colorValue;
						if (value.startsWith("#")) {
							colorValue = Integer.parseInt(value.substring(1), 16);
						} else if (value.startsWith("0x")) {
							colorValue = Integer.parseInt(value.substring(2), 16);
						} else {
							StringTokenizer st = new StringTokenizer(value, " \r\n\t,");
							colorValue = 0xff000000 | ((Integer.parseInt(st.nextToken()) & 0xff) << 16)
								| ((Integer.parseInt(st.nextToken()) & 0xff) << 8) | (Integer.parseInt(st.nextToken()) & 0xff);
						}
						color = thingleContext.createColor(colorValue);
						setMethod.invoke(theme, new Object[] {color});
					}
					page.setTheme(theme);
					saveSettings();
				} catch (Exception ex) {
					configTabs.setTitleAt(2, "Theme (error)");
				}
				configTabs.repaint();
			}
		}
		new ThemeChangeListener(themeTextBackgroundTextField, "TextBackground");
		new ThemeChangeListener(themeTextTextField, "Text");
		new ThemeChangeListener(themeHoverTextField, "Hover");
		new ThemeChangeListener(themeSelectedTextField, "Selected");
		new ThemeChangeListener(themeFocusTextField, "Focus");
		new ThemeChangeListener(themeBackgroundTextField, "Background");
		new ThemeChangeListener(themePressedTextField, "Pressed");
		new ThemeChangeListener(themeDisabledTextField, "Disabled");
		new ThemeChangeListener(themeBorderTextField, "Border");

		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				ThingleEditor.this.dispose();
			}
		});

		openMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				if (fileChooser == null) createFileChooser();
				if (fileChooser.showOpenDialog(ThingleEditor.this) != JFileChooser.APPROVE_OPTION) return;
				openFile(fileChooser.getSelectedFile());
			}
		});

		newMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				if (isXmlFileDirty) {
					int result = JOptionPane.showConfirmDialog(ThingleEditor.this,
						"The current XML has been modified.\nDo you want to save the changes?", "Save XML",
						JOptionPane.YES_NO_CANCEL_OPTION);
					if (result == JOptionPane.YES_OPTION)
						saveFile(currentFile);
					else if (result != JOptionPane.NO_OPTION) return;
				}
				originalXML = "";
				xmlEditor.setText("");
				currentFile = null;
				setXmlFileDirty(false);
			}
		});

		saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				if (currentFile == null) {
					saveAsMenuItem.doClick();
					return;
				}
				saveFile(currentFile);
			}
		});

		saveAsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				if (fileChooser == null) createFileChooser();
				if (fileChooser.showSaveDialog(ThingleEditor.this) != JFileChooser.APPROVE_OPTION) return;
				if (fileChooser.getSelectedFile().exists()) {
					int result = JOptionPane.showConfirmDialog(ThingleEditor.this, "Overwrite existing file?", "Confirm Overwrite",
						JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.NO_OPTION) return;
				}
				saveFile(fileChooser.getSelectedFile());
			}
		});

		recentFilesRemoveButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				RecentFile recentFile = (RecentFile)recentFilesList.getSelectedValue();
				recentFilesListModel.removeElement(recentFile);
				saveSettings();
			}
		});

		recentFilesOpenButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				RecentFile recentFile = (RecentFile)recentFilesList.getSelectedValue();
				openFile(recentFile.file);
			}
		});

		recentFilesList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged (ListSelectionEvent evt) {
				if (evt.getValueIsAdjusting()) return;
				RecentFile recentFile = (RecentFile)recentFilesList.getSelectedValue();
				boolean enabled = recentFile != null;
				recentFilesRemoveButton.setEnabled(enabled);
				recentFilesOpenButton.setEnabled(enabled);
			}
		});

		xmlEditor.addKeyListener(new KeyListener() {
			public void keyTyped (KeyEvent e) {
			}

			public void keyReleased (KeyEvent e) {
			}

			public void keyPressed (KeyEvent evt) {
				if (evt.getKeyCode() != 9) return; // tab
				if (xmlEditor.getSelectedText() == null) return;
				evt.consume();
				try {
					String xml = xmlEditor.getDocument().getText(0, xmlEditor.getDocument().getLength());
					int start = xml.lastIndexOf('\n', xmlEditor.getSelectionStart());
					if (start == -1) start = 0;
					int end = xmlEditor.getSelectionEnd();
					if (xml.charAt(end - 1) == '\n')
						end--;
					else {
						end = xml.indexOf('\n', end);
						if (end == -1) end = xmlEditor.getDocument().getLength();
					}
					xml = xml.substring(start, end);
					if (evt.isShiftDown())
						xml = xml.replaceAll("\n\t", "\n");
					else
						xml = xml.replaceAll("\n", "\n\t");
					((AbstractDocument)xmlEditor.getDocument()).replace(start, end - start, xml, null);
					xmlEditor.setSelectionStart(start);
					xmlEditor.setSelectionEnd(start + xml.length());
				} catch (BadLocationException ex) {
					ex.printStackTrace();
				}
			}
		});

		formatMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				// Good enough for thinlet XML!
				final int scrollPosition = xmlScrollPane.getVerticalScrollBar().getValue();
				String xml = xmlEditor.getText();
				StringBuilder buffer = new StringBuilder(xml.length() + 256);
				int indent = 0;
				Matcher nextTag = Pattern.compile("<\\/?\\s*([^>\\s]+)\\s*([^>]*)>").matcher(xml);
				Pattern nextAttribute = Pattern.compile("([^=]+)=('[^']*'|\"[^\"]*\")");
				while (nextTag.find()) {
					String match = nextTag.group();
					String tag = nextTag.group(1).replaceAll("[\\/\\\\]", "").trim();

					String attributes = nextTag.group(2);
					boolean isEndTag = match.startsWith("</");
					boolean isClosedTag = match.endsWith("/>");
					if (isEndTag) indent--;
					for (int i = 0; i < indent; i++)
						buffer.append('\t');

					if (tag.startsWith("!--")) {
						buffer.append(match);
						buffer.append('\n');
						continue;
					}

					buffer.append('<');
					if (isEndTag) buffer.append('/');
					buffer.append(tag);

					Matcher nextAttributeMatcher = nextAttribute.matcher(attributes);
					while (nextAttributeMatcher.find()) {
						buffer.append(' ');
						buffer.append(nextAttributeMatcher.group(1).trim());
						buffer.append('=');
						String value = nextAttributeMatcher.group(2);
						if (value.endsWith("/")) value = value.substring(0, value.length() - 1);
						buffer.append(value.trim());
					}

					if (isClosedTag) buffer.append('/');
					buffer.append('>');
					buffer.append('\n');
					if (!isEndTag && !isClosedTag && !match.startsWith("<?")) indent++;
				}
				xmlEditor.setText(buffer.toString());
				// Swing sucks.
				new Thread(new Runnable() {
					public void run () {
						EventQueue.invokeLater(new Runnable() {
							public void run () {
								xmlScrollPane.getVerticalScrollBar().setValue(scrollPosition);
							}
						});
					}
				}).start();
			}
		});

		splitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent evt) {
				if (splitMenuItem.isSelected())
					splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
				else
					splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

				saveSettings();
			}
		});
	}

	void createFileChooser () {
		fileChooser = new JFileChooser(currentDir);
		fileChooser.setAcceptAllFileFilterUsed(true);
		fileChooser.setFileFilter(new FileFilter() {
			public String getDescription () {
				return "Thingle XML (*.xml)";
			}

			public boolean accept (File file) {
				return file.isDirectory() || file.getName().endsWith(".xml");
			}
		});
	}

	void saveFile (File file) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			String xml = xmlEditor.getText();
			writer.write(xml);
			originalXML = xml;
			currentFile = file;
			setXmlFileDirty(false);
			RecentFile recentFile = new RecentFile(file);
			recentFilesListModel.removeElement(recentFile);
			recentFilesListModel.add(0, recentFile);
			recentFilesList.setSelectedValue(recentFile, true);
			saveSettings();
			writer.close();
		} catch (IOException ex) {
			System.out.println("Error writing file: " + file);
			ex.printStackTrace();
		}
	}

	void openFile (File file) {
		if (isXmlFileDirty) {
			int result = JOptionPane.showConfirmDialog(this, "The current XML has been modified.\nDo you want to save the changes?",
				"Save XML", JOptionPane.YES_NO_CANCEL_OPTION);
			if (result == JOptionPane.YES_OPTION)
				saveFile(currentFile);
			else if (result != JOptionPane.NO_OPTION) return;
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			StringBuilder buffer = new StringBuilder(512);
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
				buffer.append(System.getProperty("line.separator"));
			}
			originalXML = buffer.toString();
			xmlEditor.setText(originalXML);
			currentFile = file;
			setXmlFileDirty(false);
			RecentFile recentFile = new RecentFile(file);
			recentFilesListModel.removeElement(recentFile);
			recentFilesListModel.add(0, recentFile);
			recentFilesList.setSelectedValue(recentFile, true);
			saveSettings();
			reader.close();
		} catch (IOException ex) {
			System.out.println("Error reading file: " + file);
			ex.printStackTrace();
		}
	}

	private void setXmlFileDirty (boolean dirty) {
		isXmlFileDirty = dirty;
		String title = "Thingle Editor - ";
		if (currentFile != null)
			title += currentFile.getName();
		else
			title += "Untitled";
		title += (isXmlFileDirty ? "*" : "");
		setTitle(title);
	}

	private void saveSettings () {
		if (!isLoaded) return;
		Properties props = new Properties();

		props.setProperty("divider", String.valueOf(splitPane.getDividerLocation()));
		props.setProperty("divider.vertical", splitMenuItem.isSelected() ? "true" : "false");

		int extendedState = getExtendedState();
		if (extendedState == JFrame.ICONIFIED) extendedState = JFrame.NORMAL;
		props.setProperty("extendedState", String.valueOf(extendedState));
		if (extendedState != JFrame.MAXIMIZED_BOTH) {
			if (extendedState == JFrame.MAXIMIZED_BOTH) setExtendedState(JFrame.NORMAL);
			Dimension size = getSize();
			if (size.width > 0 && size.height > 0) {
				props.setProperty("width", String.valueOf(size.width));
				props.setProperty("height", String.valueOf(size.height));
			}
		}

		props.setProperty("drawDesktopCheckBox", drawDesktopCheckBox.isSelected() ? "1" : "");
		props.setProperty("skinTextField", skinTextField.getText());
		props.setProperty("fontTextField", fontTextField.getText());
		props.setProperty("themeTextBackgroundTextField", themeTextBackgroundTextField.getText());
		props.setProperty("themeTextTextField", themeTextTextField.getText());
		props.setProperty("themeHoverTextField", themeHoverTextField.getText());
		props.setProperty("themeSelectedTextField", themeSelectedTextField.getText());
		props.setProperty("themeFocusTextField", themeFocusTextField.getText());
		props.setProperty("themeBackgroundTextField", themeBackgroundTextField.getText());
		props.setProperty("themePressedTextField", themePressedTextField.getText());
		props.setProperty("themeDisabledTextField", themeDisabledTextField.getText());
		props.setProperty("themeBorderTextField", themeBorderTextField.getText());
		props.setProperty("themeBorderTextField", themeBorderTextField.getText());

		if (fileChooser != null) props.setProperty("currentDir", fileChooser.getCurrentDirectory().getAbsolutePath());

		StringBuilder buffer = new StringBuilder(512);
		for (int i = 0, n = recentFilesListModel.size(); i < n; i++) {
			buffer.append(((RecentFile)recentFilesListModel.getElementAt(i)).file.getAbsolutePath());
			buffer.append(';');
		}
		props.setProperty("recentFiles", buffer.toString());

		try {
			props.store(new FileOutputStream("thingleEditor.properties"), "ThingleEditor");
		} catch (IOException ex) {
			System.out.println("Error saving properties.");
			ex.printStackTrace();
		}
	}

	private void loadSettings () {
		if (new File("thingleEditor.properties").exists()) {
			Properties props = new Properties();
			try {
				props.load(new FileInputStream("thingleEditor.properties"));

				if (props.getProperty("divider") != null)
					splitPane.setDividerLocation(Integer.parseInt(props.getProperty("divider")));
				if (props.getProperty("divider.vertical", "false").equals("true")) {
					splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
					splitMenuItem.setSelected(true);
				} else
					splitMenuItem.setSelected(false);
				doLayout();

				if (props.getProperty("width") != null && props.getProperty("height") != null)
					setSize(Integer.parseInt(props.getProperty("width")), Integer.parseInt(props.getProperty("height")));
				setLocationRelativeTo(null);
				if (props.getProperty("extendedState") != null)
					setExtendedState(Integer.parseInt(props.getProperty("extendedState")));

				drawDesktopCheckBox.setSelected(props.getProperty("drawDesktopCheckBox") != null);
				skinTextField.setText(props.getProperty("skinTextField"));
				fontTextField.setText(props.getProperty("fontTextField"));
				themeTextBackgroundTextField.setText(props.getProperty("themeTextBackgroundTextField"));
				themeTextTextField.setText(props.getProperty("themeTextTextField"));
				themeHoverTextField.setText(props.getProperty("themeHoverTextField"));
				themeSelectedTextField.setText(props.getProperty("themeSelectedTextField"));
				themeFocusTextField.setText(props.getProperty("themeFocusTextField"));
				themeBackgroundTextField.setText(props.getProperty("themeBackgroundTextField"));
				themePressedTextField.setText(props.getProperty("themePressedTextField"));
				themeDisabledTextField.setText(props.getProperty("themeDisabledTextField"));
				themeBorderTextField.setText(props.getProperty("themeBorderTextField"));

				if (props.getProperty("currentDir") != null) currentDir = props.getProperty("currentDir");

				String[] recentFiles = props.getProperty("recentFiles", "").split(";");
				for (int i = 0; i < recentFiles.length; i++) {
					File file = new File(recentFiles[i].trim());
					if (!file.exists()) continue;
					RecentFile recentFile = new RecentFile(file);
					recentFilesListModel.removeElement(recentFile);
					recentFilesListModel.addElement(recentFile);
					if (currentFile == null) openFile(file);
				}

			} catch (IOException ex) {
				System.out.println("Error loading properties.");
				ex.printStackTrace();
			}
		}
		isLoaded = true;
	}

	private void resetUndos () {
		undoManager.discardAllEdits();
		undoAction.updateUndoState();
		redoAction.updateRedoState();
	}

	private class UndoAction extends AbstractAction {
		public UndoAction () {
			super("Undo");
			setEnabled(false);
		}

		public void actionPerformed (ActionEvent e) {
			try {
				undoManager.undo();
			} catch (CannotUndoException ex) {
				System.out.println("Unable to undo.");
				ex.printStackTrace();
			}
			updateUndoState();
			redoAction.updateRedoState();
		}

		protected void updateUndoState () {
			setEnabled(undoManager.canUndo());
			putValue(Action.NAME, "Undo");
		}
	}

	private class RedoAction extends AbstractAction {
		public RedoAction () {
			super("Redo");
			setEnabled(false);
		}

		public void actionPerformed (ActionEvent e) {
			try {
				undoManager.redo();
			} catch (CannotUndoException ex) {
				System.out.println("Unable to redo.");
				ex.printStackTrace();
			}
			updateRedoState();
			undoAction.updateUndoState();
		}

		protected void updateRedoState () {
			setEnabled(undoManager.canRedo());
			putValue(Action.NAME, "Redo");
		}
	}

	private class ThingleEditorUndoEditListener implements UndoableEditListener {
		public void undoableEditHappened (UndoableEditEvent uee) {
			undoManager.addEdit(uee.getEdit());
			undoAction.updateUndoState();
			redoAction.updateRedoState();
		}
	}

	private void initialize () {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		{
			JMenuBar menuBar = new JMenuBar();
			setJMenuBar(menuBar);
			{
				JMenu fileMenu = new JMenu();
				menuBar.add(fileMenu);
				fileMenu.setText("File");
				fileMenu.setMnemonic(KeyEvent.VK_F);
				{
					newMenuItem = new JMenuItem("New XML File", KeyEvent.VK_N);
					fileMenu.add(newMenuItem);
					newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
				}
				{
					openMenuItem = new JMenuItem("Open XML File...", KeyEvent.VK_O);
					fileMenu.add(openMenuItem);
					openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
				}
				fileMenu.addSeparator();
				{
					saveMenuItem = new JMenuItem("Save XML File", KeyEvent.VK_S);
					fileMenu.add(saveMenuItem);
					saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
				}
				{
					saveAsMenuItem = new JMenuItem("Save XML File As...");
					fileMenu.add(saveAsMenuItem);
				}
				fileMenu.addSeparator();
				{
					exitMenuItem = new JMenuItem();
					fileMenu.add(exitMenuItem);
					exitMenuItem.setText("Exit");
				}
			}
			{
				JMenu editMenu = new JMenu();
				menuBar.add(editMenu);
				editMenu.setText("Edit");
				editMenu.setMnemonic(KeyEvent.VK_E);
				{
					JMenuItem undoMenuItem = new JMenuItem(undoAction);
					editMenu.add(undoMenuItem);
					undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK));
				}
				{
					JMenuItem redoMenuItem = new JMenuItem(redoAction);
					editMenu.add(redoMenuItem);
					redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK));
				}
				editMenu.addSeparator();
				{
					formatMenuItem = new JMenuItem("Format XML");
					editMenu.add(formatMenuItem);
					formatMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK));
				}
			}
			{
				JMenu viewMenu = new JMenu();
				menuBar.add(viewMenu);
				viewMenu.setText("View");
				viewMenu.setMnemonic(KeyEvent.VK_V);
				{
					splitMenuItem = new JCheckBoxMenuItem("Split vertically");
					viewMenu.add(splitMenuItem);
				}
			}
		}
		{
			splitPane = new JSplitPane();
			splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			{
				JPanel rightPanel = new JPanel();
				splitPane.add(rightPanel, JSplitPane.RIGHT);
				rightPanel.setLayout(new GridBagLayout());
				{
					previewPanel = new JPanel();
					previewPanel.setLayout(new GridBagLayout());
					rightPanel.add(previewPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
					previewPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
				}
			}
			{
				JPanel leftPanel = new JPanel();
				splitPane.add(leftPanel, JSplitPane.LEFT);
				leftPanel.setLayout(new GridBagLayout());
				{
					xmlPanel = new JPanel();
					xmlPanel.setLayout(new GridBagLayout());
					leftPanel.add(xmlPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
					xmlPanel.setBorder(BorderFactory.createTitledBorder("XML"));
					{
						xmlScrollPane = new JScrollPane();
						xmlPanel.add(xmlScrollPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
						{
							xmlEditor = new XMLTextEditor();
							xmlEditor.getDocument().addUndoableEditListener(new ThingleEditorUndoEditListener());
							xmlScrollPane.setViewportView(xmlEditor);
						}
					}
				}
				{
					configTabs = new JTabbedPane();
					leftPanel.add(configTabs, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
					{
						JPanel recentFilesTab = new JPanel();
						recentFilesTab.setLayout(new GridBagLayout());
						configTabs.addTab("Recent Files", null, recentFilesTab, null);
						{
							JScrollPane recentFilesScroll = new JScrollPane();
							recentFilesTab.add(recentFilesScroll, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
								GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 0, 5), 0, 0));
							{
								recentFilesList = new JList();
								recentFilesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
								recentFilesListModel = new DefaultListModel();
								recentFilesList.setModel(recentFilesListModel);
								recentFilesScroll.setViewportView(recentFilesList);
							}
						}
						{
							JPanel recentFilesButtonPanel = new JPanel();
							recentFilesTab.add(recentFilesButtonPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
								GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							{
								recentFilesRemoveButton = new JButton();
								recentFilesButtonPanel.add(recentFilesRemoveButton);
								recentFilesRemoveButton.setText("Remove");
							}
							{
								recentFilesOpenButton = new JButton();
								recentFilesButtonPanel.add(recentFilesOpenButton);
								recentFilesOpenButton.setText("Open");
							}
						}
					}
					{
						JPanel pagePanel = new JPanel();
						configTabs.addTab("Page", null, pagePanel, null);
						pagePanel.setLayout(new GridBagLayout());
						{
							JLabel skinLabel = new JLabel();
							pagePanel.add(skinLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
								GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
							skinLabel.setText("Skin:");
						}
						{
							skinTextField = new JTextField();
							skinTextField.setToolTipText("Skin name, eg: clearwater");
							pagePanel.add(skinTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
								GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
						}
						{
							drawDesktopCheckBox = new JCheckBox("Draw desktop");
							pagePanel.add(drawDesktopCheckBox, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
								GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
						}
						{
							fontTextField = new JTextField();
							fontTextField.setToolTipText("family,plain/bold/italic,size OR font.fnt,font.png");
							pagePanel.add(fontTextField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
								GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
						}
						{
							JLabel fontLabel = new JLabel();
							pagePanel.add(fontLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
								GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
							fontLabel.setText("Font:");
						}
						pagePanel.add(new JPanel(), new GridBagConstraints(0, 3, 1, 1, 0.0, 1.0, GridBagConstraints.EAST,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					}
					{
						JPanel themePanel = new JPanel();
						configTabs.addTab("Theme", null, themePanel, null);
						themePanel.setLayout(new GridBagLayout());
						{
							themeTextTextField = new JTextField();
							themeTextTextField.setToolTipText("#00ffff00 OR 0,255,0");
							themePanel.add(themeTextTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
								GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
						}
						{
							JLabel label = new JLabel();
							themePanel.add(label, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
								GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
							label.setText("Background:");
						}
						{
							JLabel label = new JLabel();
							themePanel.add(label, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
								GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
							label.setText("Disabled:");
						}
						{
							themeTextBackgroundTextField = new JTextField();
							themeTextBackgroundTextField.setToolTipText("#00ffff00 OR 0,255,0");
							themePanel.add(themeTextBackgroundTextField, new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0,
								GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
						}
						{
							JLabel label = new JLabel();
							themePanel.add(label, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
								GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
							label.setText("Selected:");
						}
						{
							themeBorderTextField = new JTextField();
							themeBorderTextField.setToolTipText("#00ffff00 OR 0,255,0");
							themePanel.add(themeBorderTextField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
								GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
						}
						{
							JLabel label = new JLabel();
							themePanel.add(label, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
								GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
							label.setText("Focus:");
						}
						{
							themeDisabledTextField = new JTextField();
							themeDisabledTextField.setToolTipText("#00ffff00 OR 0,255,0");
							themePanel.add(themeDisabledTextField, new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0,
								GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
						}
						{
							JLabel label = new JLabel();
							themePanel.add(label, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
								GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
							label.setText("Pressed:");
						}
						{
							themePressedTextField = new JTextField();
							themePressedTextField.setToolTipText("#00ffff00 OR 0,255,0");
							themePanel.add(themePressedTextField, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,
								GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
						}
						{
							JLabel label = new JLabel();
							themePanel.add(label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
								GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
							label.setText("Border:");
						}
						{
							themeBackgroundTextField = new JTextField();
							themeBackgroundTextField.setToolTipText("#00ffff00 OR 0,255,0");
							themePanel.add(themeBackgroundTextField, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0,
								GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
						}
						{
							JLabel label = new JLabel();
							themePanel.add(label, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
								GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
							label.setText("Text bground:");
						}
						{
							themeFocusTextField = new JTextField();
							themeFocusTextField.setToolTipText("#00ffff00 OR 0,255,0");
							themePanel.add(themeFocusTextField, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
								GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
						}
						{
							JLabel label = new JLabel();
							themePanel.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
								GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
							label.setText("Text:");
						}
						{
							themeSelectedTextField = new JTextField();
							themeSelectedTextField.setToolTipText("#00ffff00 OR 0,255,0");
							themePanel.add(themeSelectedTextField, new GridBagConstraints(3, 3, 1, 1, 1.0, 0.0,
								GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
						}
						{
							JLabel label = new JLabel();
							themePanel.add(label, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
								GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
							label.setText("Hover:");
						}
						{
							themeHoverTextField = new JTextField();
							themeHoverTextField.setToolTipText("#00ffff00 OR 0,255,0");
							themePanel.add(themeHoverTextField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
								GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
						}
						{
							themePanel.add(new JPanel(), new GridBagConstraints(0, 5, 1, 1, 0.0, 1.0, GridBagConstraints.EAST,
								GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
						}
					}
				}
			}
			getContentPane().add(splitPane, BorderLayout.CENTER);
			splitPane.setDividerLocation(300);
		}
		setSize(700, 525);
		setLocationRelativeTo(null);
	}

	private JSplitPane splitPane;
	private JTabbedPane configTabs;
	private JTextField themeTextBackgroundTextField;
	private JTextField themeTextTextField;
	private JTextField themeHoverTextField;
	private JTextField themeSelectedTextField;
	private JTextField themeFocusTextField;
	private JTextField themeBackgroundTextField;
	private JTextField themePressedTextField;
	private JTextField themeDisabledTextField;
	private JTextField themeBorderTextField;
	private JTextField fontTextField;
	private JCheckBox drawDesktopCheckBox;
	private JTextField skinTextField;
	private JMenuItem exitMenuItem, openMenuItem, newMenuItem, saveAsMenuItem;
	private JCheckBoxMenuItem splitMenuItem;
	private XMLTextEditor xmlEditor;
	private JButton recentFilesRemoveButton;
	private JButton recentFilesOpenButton;
	private JMenuItem saveMenuItem;
	private JList recentFilesList;
	private DefaultListModel recentFilesListModel;
	private JMenuItem formatMenuItem;
	private JPanel previewPanel;
	private JPanel xmlPanel;
	private JScrollPane xmlScrollPane;

	static class RecentFile {
		public File file;

		public RecentFile (File file) {
			this.file = file;
		}

		public boolean equals (Object obj) {
			if (!(obj instanceof RecentFile)) return false;
			return file.equals(((RecentFile)obj).file);
		}

		public String toString () {
			return file.getName() + " - " + file.getParent();
		}
	}

	public static void main (String[] args) throws SlickException {
		new ThingleEditor();
	}
}
