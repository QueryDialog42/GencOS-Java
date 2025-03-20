package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

import static main.Main.reloadDesktop;
import static main.Main.aboutMenu;
import static main.Main.mainNote;

public class GenNoteApp{
	public static void newnote(String redesktoppath, String currentpath, String filename, String filepath, JPanel desktop, JPanel folderdesktop) {	
		JFrame note = new JFrame("GenNote");
		
		JMenuBar menubarnote = new JMenuBar();

		JToolBar toolbar = new JToolBar();
		
		JTextPane textpane = new JTextPane();
		JTextArea textarea = new JTextArea();
		
		//Buttons for RTF Toolbar
		JButton save = createButton("save32x32.png"),
				replace = createButton("replace64x64.png"),
        		bold = createButton("bold.png"),
        		italic = createButton("italic32x32.png"),
        		underline = createButton("underline.png"),
        		changecolor = createButton("Color.png"),
        		upfont = createButton("up.png"),
        		downfont = createButton("down.png"),
        		removestyle = createButton("removestyle.png");
		
		JButton toolbarbuttons[] = {save, replace, bold, italic, underline, changecolor, upfont, downfont, removestyle};
		
		JMenuItem newnote = new JMenuItem("New note"),
				  opennote = new JMenuItem("Open note"),
				  savenote = new JMenuItem("Save"),
				  saveasnote = new JMenuItem("Save as"),
				  movenote = new JMenuItem("Move"),
				  copynote = new JMenuItem("Copy"),
				  deletenote = new JMenuItem("Delete"),
				  quitnote = new JMenuItem("Quit GenNote"),
				  about = new JMenuItem("About GenNote");
		
		save.setToolTipText("Save");
		replace.setToolTipText("Replace text");
		bold.setToolTipText("Bold text");
		italic.setToolTipText("Italic text");
		underline.setToolTipText("Underline text");
		changecolor.setToolTipText("Change text color");
		upfont.setToolTipText("Increase font size");
		downfont.setToolTipText("Reduce font size");
		removestyle.setToolTipText("Remove style");
	
		JMenu filemenunote = new JMenu("File");
		
		filemenunote.setMnemonic(KeyEvent.VK_F);
		newnote.setMnemonic(KeyEvent.VK_N);
		opennote.setMnemonic(KeyEvent.VK_O);
		savenote.setMnemonic(KeyEvent.VK_S);
		saveasnote.setMnemonic(KeyEvent.VK_A);
		movenote.setMnemonic(KeyEvent.VK_M);
		copynote.setMnemonic(KeyEvent.VK_C);
		deletenote.setMnemonic(KeyEvent.VK_D);
		quitnote.setMnemonic(KeyEvent.VK_Q);
		
		JMenuItem filemenunoteitems[] = {newnote, opennote, savenote, saveasnote, movenote, copynote, deletenote, quitnote, about};
		for (JMenuItem item : filemenunoteitems) {
			filemenunote.add(item);
		}
		
		JMenu menubarnotemenus[] = {filemenunote};
		for (JMenu menu : menubarnotemenus) {
			menubarnote.add(menu);
		}
		
		note.setJMenuBar(menubarnote);
		note.setBounds(100, 100, 300, 400);
		note.setIconImage(new ImageIcon(Main.class.getResource("gennote.png")).getImage());
		
		// About menu for GenNote
		about.addActionListener(_ -> aboutMenu(mainNote, "Made with Oracle Java jdk-23", "Made by Batu Genç", "gennote.png", "gennote.png"));
		
		// Save Actions
		ActionListener saveTXTAction = _ -> savenote(note, currentpath, null, textarea);
		ActionListener saveRTFAction = _ -> savenote(note, currentpath, textpane, null);
		ActionListener saveAsTXTAction = _ -> saveasnote(note, currentpath, null, textarea, desktop, folderdesktop);
		ActionListener saveAsRTFAction = _ -> saveasnote(note, currentpath, textpane, null, desktop, folderdesktop);
		ActionListener saveTXTBeforeQuitAction = _ -> {
		    if (JOptionPane.showConfirmDialog(null, "Do you want to save your note?", "Save note", JOptionPane.YES_NO_OPTION) == 0) {
		        saveasnote(note, currentpath, null, textarea, desktop, folderdesktop);
		    }
		};
		ActionListener saveRTFBeforeQuitAction = _ -> {
		    if (JOptionPane.showConfirmDialog(null, "Do you want to save your note?", "Save note", JOptionPane.YES_NO_OPTION) == 0) {
		        saveasnote(note, currentpath, textpane, null, desktop, folderdesktop);
		    }
		};

		// Replace Action
		ActionListener replaceAction = _ -> {
		    if (textpane.getSelectedText() != null) {
		        String newtext = JOptionPane.showInputDialog(null, "Replace to:", "Replace Highlighted Text", JOptionPane.QUESTION_MESSAGE);
		        textpane.replaceSelection(newtext);
		    } else {
		        showMessageIfNoSelection();
		    }
		};

		// Style Actions
		ActionListener boldAction = createStyleAction(attrs -> StyleConstants.setBold(attrs, true), textpane);
		ActionListener italicAction = createStyleAction(attrs -> StyleConstants.setItalic(attrs, true), textpane);
		ActionListener underlineAction = createStyleAction(attrs -> StyleConstants.setUnderline(attrs, true), textpane);
		ActionListener changeColorAction = _ -> {
		    final int start = textpane.getSelectionStart(), end = textpane.getSelectionEnd();
		    if (start != end) {
		        Color newcolor = JColorChooser.showDialog(null, "Choose selected text color", null);
		        StyledDocument doc = textpane.getStyledDocument();
		        SimpleAttributeSet attrs = new SimpleAttributeSet();
		        StyleConstants.setForeground(attrs, newcolor);
		        applyStyleToSelection(doc, start, end, attrs);
		    } else {
		        showMessageIfNoSelection();
		    }
		};

		// Font Size Actions
		ActionListener upFontAction = _ -> changeFontSize(1, textpane);
		ActionListener downFontAction = _ -> changeFontSize(-1, textpane);
		
		// Clean Styles Action
		ActionListener removeStylesAction = _ -> {
		    int start = textpane.getSelectionStart(), end = textpane.getSelectionEnd();
		    if (start != end) {
		        StyledDocument doc = textpane.getStyledDocument();
		        String selectedText;
		        try {
		            selectedText = doc.getText(start, end - start);
		            doc.remove(start, end - start);
		            doc.insertString(start, selectedText, null);
		        } catch (BadLocationException ex) {
		            ex.printStackTrace();
		        }
		    } else {
		        showMessageIfNoSelection();
		    }
		};
			
		if(redesktoppath == null) { // if path is not entered
			JMenuItem whenPathNotEntered[] = {saveasnote, movenote, copynote, deletenote}; 
			for (JMenuItem item : whenPathNotEntered) {
				item.setEnabled(false);
			}
		}
		
		savenote.setEnabled(false);
		
		// checks if user clicked a existing file or not (if not, that means user clicked GenNote App)
		if (!filepath.equals("null")) {
			note.setTitle(filename);
			savenote.setEnabled(true);
			if (filepath.endsWith(".rtf")) {
				formatRTF(note, toolbar, toolbarbuttons, textarea, textpane);
				try(FileInputStream fis = new FileInputStream(new File(filepath))){
					textpane.setText("");
					RTFEditorKit rtfeditor = new RTFEditorKit();
					rtfeditor.read(fis, textpane.getStyledDocument(), 0);
				} catch(IOException | BadLocationException ex){
					JOptionPane.showMessageDialog(null, "Can not read " + filename + ". It may be deleted.\nPlease reload the desktop.", "File read failed.", JOptionPane.WARNING_MESSAGE);
				}
				toolsForRTFNote(
						savenote,
						saveasnote,
						quitnote,
						save,
						replace,
						bold,
						italic,
						underline,
						changecolor,
						upfont,
						downfont,
						removestyle, redesktoppath,
						saveRTFAction,
						saveAsRTFAction,
						saveRTFBeforeQuitAction,
						replaceAction,
						boldAction,
						italicAction,
						underlineAction,
						changeColorAction,
						upFontAction,
						downFontAction,
						removeStylesAction);
				
				// RTF and TXT note will have this methods
				toolsForGeneral(
						note,
						redesktoppath,
						currentpath,
						desktop,
						folderdesktop,
						movenote,
						copynote,
						deletenote,
						newnote,
						opennote,
						filepath, filename);
				
				note.setVisible(true);
			}
			else if (filepath.endsWith(".txt")) {
				note.add(new JScrollPane(textarea));
				StringBuilder content = new StringBuilder();
				try(BufferedReader reader = new BufferedReader(new FileReader(filepath))){
					String line;
					while((line = reader.readLine()) != null) {
						content.append(line).append("\n");
					}
					textarea.setText(content.toString());
					
				} catch (IOException ex){
					JOptionPane.showMessageDialog(null, "File could not readed. May be it is deleted.\nPlease reload the desktop", "File Reading Failed", JOptionPane.WARNING_MESSAGE);
				}
				
				toolsForTXTNote(savenote, saveasnote, quitnote, redesktoppath,
						saveTXTAction,
						saveAsTXTAction,
						saveTXTBeforeQuitAction);
				
				// RTF and TXT note will have this methods
				toolsForGeneral(
						note,
						redesktoppath,
						currentpath,
						desktop,
						folderdesktop,
						movenote,
						copynote,
						deletenote,
						newnote,
						opennote,
						filepath, filename);
				
				note.setVisible(true);
			}
			// if user tried to open an unsupported txt file
			else {
				JOptionPane.showMessageDialog(null, filename + " is not supported for " + mainNote + ".\n.txt\n.rtf\nis supported for now.", "Unsupported File Type", JOptionPane.ERROR_MESSAGE);
			}
		}
		//if user clicked GenNote app
		else {
			String types[] = {"txt", "rtf"};
			int response = JOptionPane.showOptionDialog(null, "Select the text type", "TXT or RTF", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					new ImageIcon(GenNoteApp.class.getResource("noteadd.png")), types, types[0]);
			if (response != -1) { // if user did not click cancel
				//ready for TXT
				if(response == 0) {
					note.add(new JScrollPane(textarea));
					toolsForTXTNote(savenote, saveasnote, quitnote, redesktoppath,
							saveTXTAction,
							saveAsTXTAction,
							saveTXTBeforeQuitAction);
				}
				//ready for RTF
				else if (response == 1) {
					formatRTF(note, toolbar, toolbarbuttons, textarea, textpane);
					toolsForRTFNote(
							savenote,
							saveasnote,
							quitnote,
							save,
							replace,
							bold,
							italic,
							underline,
							changecolor,
							upfont,
							downfont,
							removestyle, redesktoppath,
							saveRTFAction,
							saveAsRTFAction,
							saveRTFBeforeQuitAction,
							replaceAction,
							boldAction,
							italicAction,
							underlineAction,
							changeColorAction,
							upFontAction,
							downFontAction,
							removeStylesAction);
				}
				save.setEnabled(false);
				movenote.setEnabled(false);
				copynote.setEnabled(false);
				deletenote.setEnabled(false);
				
				// RTF and TXT note will have this methods
				toolsForGeneral(
						note,
						redesktoppath,
						currentpath,
						desktop,
						folderdesktop,
						movenote,
						copynote,
						deletenote,
						newnote,
						opennote,
						filepath,
						filename);
				
				note.setVisible(true);
			}
		}
		note.revalidate();
		note.repaint();
	}
	

	private static void saveasnote(JFrame note, String path, JTextPane textpane, JTextArea textarea, JPanel desktop, JPanel folderdesktop) {
		String filename = note.getTitle(), filepath = path + "/" + filename;
		if (!filepath.endsWith(".txt") && !filepath.endsWith(".rtf")) { // if file created new, create default type
			if (textpane != null) filepath += ".rtf";
			else if (textarea != null) filepath += ".txt";
		}
		JFileChooser filechooser = new JFileChooser();
		filechooser.setDialogTitle("Save File As");
		filechooser.setSelectedFile(new File(filepath));
	    if (filechooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
	    	File selectedFile = filechooser.getSelectedFile();
	    	filename = selectedFile.getName();
	    	 // Write RTF file
	    	if (textpane != null) {
	    		if (filename.endsWith(".rtf")) {
	    			try (FileOutputStream fos = new FileOutputStream(selectedFile)) {
			    		RTFEditorKit rtfeditor = new RTFEditorKit();
			    	    rtfeditor.write(fos, textpane.getStyledDocument(), 0, textpane.getStyledDocument().getLength());
			    	} catch (IOException | BadLocationException ex) {
			    	    JOptionPane.showMessageDialog(null, filename + " could not saved. Please try again later.", "Saving unsuccesful", JOptionPane.ERROR_MESSAGE);
			    	}
	    			reloadDesktop(path, desktop, folderdesktop, true);
	    		}
	    		else {
	    			JOptionPane.showMessageDialog(null, "You can not save you rtf file as non rtf file.\nPlease save your file that finishes with .rtf", "Wrong Saving Type", JOptionPane.WARNING_MESSAGE);
	    		}
	    		
	    	}
	    	// Write TXT file
	    	else if(textarea != null){
	    		if (filename.endsWith(".txt")) {
	    			try(BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))){
		    			writer.write(textarea.getText());
		    		} catch (IOException ex) {
		    			JOptionPane.showMessageDialog(null, filename + " could not saved. Please try again later.", "Saving unsuccesful", JOptionPane.ERROR_MESSAGE);
		    		}
	    			reloadDesktop(path, desktop, folderdesktop, true);
	    		}
	    		else {
	    			JOptionPane.showMessageDialog(null, "You can not save you txt file as non txt file.\nPlease save your file that finishes with .txt", "Wrong Saving Type", JOptionPane.WARNING_MESSAGE);
	    		}
	    		
	    	}
	    }
	}
	
	private static void savenote(JFrame note, String currentpath, JTextPane textpane, JTextArea textarea) {
		String filename = note.getTitle(), filepath = currentpath + "/" + filename;
		if (textpane != null) {
			//Save the file RTF format
			try(FileOutputStream fos = new FileOutputStream(filepath)) {
				RTFEditorKit rtfeditor = new RTFEditorKit();
				rtfeditor.write(fos, textpane.getStyledDocument(), 0, textpane.getStyledDocument().getLength());
			} catch(IOException | BadLocationException ex) {
				JOptionPane.showMessageDialog(null, filename + " could not saved. Please try again later.", "Saving unsuccesful", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (textarea != null) {
			//Save the file TXT format
			try(BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))){
				writer.write(textarea.getText());
			} catch(IOException ex) {
				JOptionPane.showMessageDialog(null, filename + " could not saved. Please try again later.", "Saving unsuccesful", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private static void movefile(String desktopPath, JPanel desktop, JPanel folderdesktop, String filename) {
		String filepath = desktopPath + "/" + filename;
		JFileChooser filechooser = new JFileChooser();
		filechooser.setDialogTitle("Move File");
		filechooser.setSelectedFile(new File(filepath));
		Path currentPath = Paths.get(filepath);
		
		if(filechooser.showDialog(null, "Move") == JFileChooser.APPROVE_OPTION) {	
			String selectedfile = filechooser.getSelectedFile().getAbsolutePath();
			if (selectedfile.contains(".audios$")) {
				JOptionPane.showMessageDialog(null, "You can not copy or move files into hidden folders.", "File Could Not Copied", JOptionPane.WARNING_MESSAGE);
			}
			else {
				try {
					Files.move(currentPath, filechooser.getSelectedFile().toPath());
					reloadDesktop(desktopPath, desktop, folderdesktop, false);
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(null, filename + " could not moved.\nPlease check if you selected a folder\nor the file already exist.", "File Could Not Moved", JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}
	
	private static void openfile(
			String redesktoppath,
			String desktopPath, 
			JPanel desktop, 
			JPanel folderdesktop) {
		
		JFileChooser filechooser = new JFileChooser();
		if (desktopPath != null) {
			filechooser.setCurrentDirectory(new File(desktopPath));
		}
		if (filechooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = filechooser.getSelectedFile();
			newnote(redesktoppath, desktopPath, selectedFile.getName(), selectedFile.getAbsolutePath(), desktop, folderdesktop);
		}
	}
	
	private static void copyfile(String desktopPath, String filename) {
		String filepath = desktopPath + "/" + filename;
		JFileChooser filechooser = new JFileChooser();
		filechooser.setSelectedFile(new File(filepath));
		filechooser.setDialogTitle("Copy File");
		Path currentPath = Paths.get(filepath);
		
		if(filechooser.showDialog(null, "Copy") == JFileChooser.APPROVE_OPTION) {
			String selectedpath = filechooser.getSelectedFile().getAbsolutePath();
			if (selectedpath.contains(".audios$")) { // if user selected a hidden folder
				JOptionPane.showMessageDialog(null, "You can not copy or move files into hidden folders.", "File Could Not Copied", JOptionPane.WARNING_MESSAGE);
			}
			else { // if user selected a valid folder
				try {
					Files.copy(currentPath, filechooser.getSelectedFile().toPath());
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(null, "Could not copied " + filename + ".\nMay be file does not exist\nor there is another file that has same name.", "File Could Not Copied", JOptionPane.WARNING_MESSAGE);
				}
			}			
		}
	}
	
	//functions that TXT file will use
	private static void toolsForTXTNote(JMenuItem savenote, JMenuItem saveasnote, JMenuItem quitnote, String redesktoppath,
			ActionListener saveTXTAction,
			ActionListener saveAsTXTAction,
			ActionListener saveTXTBeforeQuitAction
			) {
		
		//add them again
		if (redesktoppath != null) {
			if(savenote.isEnabled()) savenote.addActionListener(saveTXTAction);
			saveasnote.addActionListener(saveAsTXTAction);
		}
		quitnote.addActionListener(saveTXTBeforeQuitAction);
	}
	
	//functions that RTF file will use
	private static void toolsForRTFNote(
			JMenuItem savenote,
			JMenuItem saveasnote,
			JMenuItem quitnote,
			JButton save,
			JButton replace,
			JButton bold,
			JButton italic,
			JButton underline,
			JButton changecolor,
			JButton upfont,
			JButton downfont,
			JButton removestyle, String redesktoppath,
			ActionListener saveRTFAction,
			ActionListener saveAsRTFAction,
			ActionListener saveRTFBeforeQuitAction,
			ActionListener replaceAction,
			ActionListener boldAction,
			ActionListener italicAction,
			ActionListener underlineAction,
			ActionListener changeColorAction,
			ActionListener upFontAction,
			ActionListener downFontAction,
			ActionListener removeStylesAction) {
		
		//add them again
		if (redesktoppath != null) {
			if (save.isEnabled()) save.addActionListener(saveRTFAction);
			if (savenote.isEnabled()) savenote.addActionListener(saveRTFAction);
			saveasnote.addActionListener(saveAsRTFAction);
		}
		quitnote.addActionListener(saveRTFBeforeQuitAction);
		replace.addActionListener(replaceAction);
		bold.addActionListener(boldAction);
		italic.addActionListener(italicAction);
		underline.addActionListener(underlineAction);
		changecolor.addActionListener(changeColorAction);
		upfont.addActionListener(upFontAction);
		downfont.addActionListener(downFontAction);
		removestyle.addActionListener(removeStylesAction);
	}
	
	//functions that all files will use
	private static void toolsForGeneral(
			JFrame note, 
			String redesktoppath,
			String currentpath,
			JPanel desktop, 
			JPanel folderdesktop, 
			JMenuItem movenote,
			JMenuItem copynote,
			JMenuItem deletenote,
			JMenuItem newnote,
			JMenuItem opennote,
			String filepath, String filename) {
		
		//Look if Actions are really usable for general
		if (redesktoppath != null) {
			movenote.addActionListener(_ -> {
				note.dispose();
				movefile(currentpath, desktop, folderdesktop, filename);
			});
			
			copynote.addActionListener(_ -> copyfile(currentpath, filename));
			deletenote.addActionListener(_ -> {
				if (JOptionPane.showOptionDialog(null, "Do you really want to delete " + filename + "?", "Are you sure?", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE, new ImageIcon(GenNoteApp.class.getResource("deletenote.png")), null, null) == 0) {
					
					File fileToDelete = new File(filepath); // Create a File object for the file to delete
				    if (fileToDelete.delete()) { // Attempt to delete the file
				    	note.dispose(); // Close the note window
				        reloadDesktop(currentpath, desktop, folderdesktop, false); // Reload the desktop to reflect the changes
				    } else {
				        JOptionPane.showMessageDialog(null, "Failed to delete " + filename + ".\nMay be file already deleted or moved\nPlease reload the desktop.", "Deletion Failed", JOptionPane.WARNING_MESSAGE);
				    }
				}
			});
		}
		
		newnote.addActionListener(_ -> {
			newnote(redesktoppath, currentpath,"null", "null", desktop, folderdesktop);
		});
			
		opennote.addActionListener(_ -> {
			openfile(redesktoppath, currentpath, desktop, folderdesktop);
		});
	}
	
	private static void formatRTF(JFrame note, JToolBar toolbar, JButton[] toolbarbuttons, JTextArea textarea, JTextPane textpane) {
		Dimension toolbardimension = new Dimension(15, 15);
		toolbar.setFloatable(false);
		for (JButton button : toolbarbuttons) {
			button.setBorderPainted(false);
			button.setFocusable(false);
			button.setPreferredSize(toolbardimension);
			toolbar.add(button);
		}
		note.getContentPane().add(toolbar, "North");
		note.add(new JScrollPane(textpane));
	}
	
	private static JButton createButton(String iconPath) {
		final int WIDTH = 15, HEIGHT = 15;
		return new JButton(new ImageIcon(new ImageIcon(Main.class.getResource(iconPath)).getImage().getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH)));
	}
	 
	private static void showMessageIfNoSelection() {
		JOptionPane.showMessageDialog(null, "No text selected", "Selection needed", JOptionPane.WARNING_MESSAGE);
	}

	private static void applyStyleToSelection(StyledDocument doc, int start, int end, SimpleAttributeSet attrs) {
		doc.setCharacterAttributes(start, end - start, attrs, false);
	}

	private static ActionListener createStyleAction(Consumer<SimpleAttributeSet> styleConsumer, JTextPane textpane) {
		return _ -> {
			final int start = textpane.getSelectionStart(), end = textpane.getSelectionEnd();
			if (start != end) {
				StyledDocument doc = textpane.getStyledDocument();
		        SimpleAttributeSet attrs = new SimpleAttributeSet();
		        styleConsumer.accept(attrs);
		        applyStyleToSelection(doc, start, end, attrs);
		     } else {
		        showMessageIfNoSelection();
		    }
		};
	}
		
	private static void changeFontSize(int delta, JTextPane textpane) {
		final int start = textpane.getSelectionStart(), end = textpane.getSelectionEnd();
		if (start != end) {
			StyledDocument doc = textpane.getStyledDocument();
		    AttributeSet attr = doc.getCharacterElement(start).getAttributes();
		    int currentSize = StyleConstants.getFontSize(attr);
		    SimpleAttributeSet newAttr = new SimpleAttributeSet(attr);
		    StyleConstants.setFontSize(newAttr, currentSize + delta);
		    applyStyleToSelection(doc, start, end, newAttr);
		} else {
		    showMessageIfNoSelection();
		}
	}
}
