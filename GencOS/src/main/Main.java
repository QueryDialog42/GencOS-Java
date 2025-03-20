package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import static main.GenNoteApp.newnote;
import static main.GenMusicApp.openMusicApp;

public class Main {
	static int WIDTH = 70, HEIGHT = 70;
	static String JAVA_DESKTOPpath;
	static String main = "GencOS 1.1.0";
	static String mainMusic = "GenMusic 1.1.0";
	static String mainNote = "GenNote 1.5.0";
	static Dimension dimension = new Dimension(WIDTH, HEIGHT + 20);
	public static void main(String[] args) {
		JPanel desktop, folderdesktop;
		String desktopPath = null;
		desktop = new JPanel();
		desktop.setLayout(new FlowLayout(FlowLayout.LEFT));
		desktop.setBackground(Color.LIGHT_GRAY);
		
		folderdesktop = new JPanel();
		folderdesktop.setBackground(Color.getHSBColor(166, 166, 54));
		folderdesktop.setPreferredSize(new Dimension(80, 0));
		folderdesktop.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		checkConfig(desktopPath, desktop, folderdesktop);
		
		createwindow(new JFrame("GencOS main"), JAVA_DESKTOPpath, desktop, folderdesktop);
	}
	
	public static void createwindow(JFrame frame, String desktopPath, JPanel desktop, JPanel folderdesktop) {
		JPanel safari = new JPanel();
		safari.setPreferredSize(new Dimension(0, 80));
		safari.setBackground(Color.DARK_GRAY);
		safari.setLayout(new BoxLayout(safari, BoxLayout.X_AXIS));
		if (frame.getTitle().equals("GencOS main")) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		frame.setBounds(300, 200, 1000, 500);
		frame.setIconImage(createImage("gencosLogo.png", 90, 90));
		
		JMenuBar menubar = new JMenuBar();
		
		JMenu filemenu = new JMenu("Desktop"),
			  newitem = new JMenu("New"),
			  settingsmenu = new JMenu("Settings"),
			  chbackg = new JMenu("Change Background"),
			  chfont = new JMenu("Change Fonts");
		
		JMenuItem importfile = new JMenuItem("Import File"), 
				  newfolder = new JMenuItem("New Folder"), 
				  newnote = new JMenuItem("New Note"), 
				  reloaditem = new JMenuItem("Reload"),
				  deleteitem = new JMenuItem("Delete current folder"),
				  quititem = new JMenuItem("Quit"),
				  arial = new JMenuItem("Arial"),
				  timesnewroman = new JMenuItem("Times New Roman"),
				  about = new JMenuItem("About GencOS");
		
		JButton gennote = new JButton(), genmusic = new JButton();
		
		JButton[] defaultapps = {genmusic, gennote};
		
		for (JButton defaultapp : defaultapps) {
			defaultapp.setPreferredSize(dimension);
			defaultapp.setFocusable(false);
			defaultapp.setBackground(null);
			defaultapp.setBorderPainted(false);
			defaultapp.setHorizontalAlignment(JButton.CENTER);
		}
		genmusic.setIcon(new ImageIcon(createImage("Genmusic.png", WIDTH, HEIGHT)));
		gennote.setIcon(new ImageIcon(createImage("gennote.png", WIDTH, HEIGHT)));
		gennote.setToolTipText(mainNote);
		genmusic.setToolTipText(mainMusic);
		
		filemenu.setMnemonic(KeyEvent.VK_D);
		importfile.setMnemonic(KeyEvent.VK_N);
		newfolder.setMnemonic(KeyEvent.VK_F);
		reloaditem.setMnemonic(KeyEvent.VK_R);
		
		JMenuItem newitemitems[] = {importfile, newfolder, newnote};
		for (JMenuItem item : newitemitems) {
			newitem.add(item);
		}
		
		JMenuItem fileitems[] = {newitem, reloaditem, deleteitem, quititem, about};
		for (JMenuItem item : fileitems) {
			filemenu.add(item);
		}
		
		JMenu settingsmenumenus[] = {chbackg, chfont};
		for (JMenu menu : settingsmenumenus) {
			settingsmenu.add(menu);
		}
		
		JMenuItem chfontitems[] = {arial, timesnewroman};
		for (JMenuItem item : chfontitems) {
			chfont.add(item);
		}
		
		JMenu mainmenus[] = {filemenu, settingsmenu};
		for (JMenu menu : mainmenus) {
			menubar.add(menu);
		}
		
		safari.add(gennote);
		safari.add(genmusic);
		
		frame.setJMenuBar(menubar);
		frame.add(safari, BorderLayout.SOUTH);
		frame.add(desktop, BorderLayout.CENTER);
		frame.add(folderdesktop, BorderLayout.EAST);
		
		frame.setVisible(true);
		
		if (JAVA_DESKTOPpath == null) { // if path not entered
			JMenuItem desktopmenuitems[] = {reloaditem, importfile, newfolder, newnote, deleteitem};
			for (JMenuItem item : desktopmenuitems) {
				item.setEnabled(false);
			}
			gennote.addActionListener(_ -> newnote(JAVA_DESKTOPpath, desktopPath, "null", "null", desktop, folderdesktop));
			newnote.addActionListener(_ -> newnote(JAVA_DESKTOPpath, desktopPath, "null", "null", desktop, folderdesktop));
			quititem.addActionListener(_ -> System.exit(0));
			
			JOptionPane.showMessageDialog(null, "Path is not entered. Minimal operating system\nwill be used.", "No Path is recognized", JOptionPane.INFORMATION_MESSAGE);
			JMenu enterPath = new JMenu("Enter path");
			JMenuItem enterpath = new JMenuItem("Enter path");
			
			menubar.add(enterPath);
			enterPath.add(enterpath);
			
			enterpath.addActionListener(_ -> JAVA_DESKTOPpath = configurepath(desktopPath, desktop, folderdesktop));
			frame.revalidate();
			frame.repaint();
		}
		//if invalid path entered or opened
		else if (!reloadDesktop(desktopPath, desktop, folderdesktop, false)){
			frame.dispose();
		}
		// if valid path entered
		else {
			gennote.addActionListener(_ -> newnote(JAVA_DESKTOPpath, desktopPath, "null", "null", desktop, folderdesktop));
			genmusic.addActionListener(_ -> openMusicApp(JAVA_DESKTOPpath, null));
			newnote.addActionListener(_ -> newnote(JAVA_DESKTOPpath, desktopPath, "null", "null", desktop, folderdesktop));
			newfolder.addActionListener(_ -> newfolder(desktopPath, desktop, folderdesktop));
			reloaditem.addActionListener(_ -> reloadDesktop(desktopPath, desktop, folderdesktop, true));
			quititem.addActionListener(_ -> System.exit(0));
			importfile.addActionListener(_ -> importFile(desktopPath, desktop, folderdesktop, false));
			deleteitem.addActionListener(_ -> deletefolder(frame, desktopPath));
		}
	
		about.addActionListener(_ -> aboutMenu(main, "Made with Oracle Java jdk-23", "Made by Batu Genç", "gencosLogo.png", "gencosLogo512x512.png"));
	}

	public static boolean reloadDesktop(String desktopPath, JPanel desktop, JPanel folderdesktop, boolean reconfig) {
		//Check the files of desktop and create them for GUI
		try {
			desktop.removeAll();
			folderdesktop.removeAll();
			File desktopFolder = new File(desktopPath);
			File[] files = desktopFolder.listFiles(); // this array has string paths
			for (File thefile : files) {
				//changes to avoid string escape character problems           get the absolute filename from path
				String path = thefile.getAbsolutePath(), filename = thefile.getName();
				JButton app = new JButton(filename);
				app.setPreferredSize(dimension);
				app.setBorderPainted(false);
				app.setFocusable(false);
				app.setBackground(null);
				app.setHorizontalTextPosition(JButton.CENTER);
				app.setVerticalTextPosition(JButton.BOTTOM);
				app.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
				app.setToolTipText(filename);
				
				// checks which file type is the file
				if (filename.endsWith(".txt")) {
					app.setIcon(new ImageIcon(createImage("txtfile.png", WIDTH, HEIGHT)));
					desktop.add(app);
					app.addActionListener(_ -> newnote(JAVA_DESKTOPpath, desktopPath, filename, path, desktop, folderdesktop));
				}
				
				else if(filename.endsWith(".rtf")) {
					app.setIcon(new ImageIcon(createImage("rtffile.png", WIDTH, HEIGHT)));
					desktop.add(app);
					app.addActionListener(_ -> newnote(JAVA_DESKTOPpath, desktopPath, filename, path, desktop, folderdesktop));
				}

				else if (filename.endsWith(".wav")) {
					app.setIcon(new ImageIcon(createImage("sound128x128.png", WIDTH, HEIGHT)));
					desktop.add(app);
					app.addActionListener(_ -> openMusicApp(desktopPath, thefile));
				}
				else if (filename.endsWith(".exe")) {
					app.setIcon(new ImageIcon(createImage("exe.png", WIDTH, HEIGHT)));
					desktop.add(app);
					app.addActionListener(_ -> runEXE(path, filename, desktopPath, desktop, folderdesktop));
				}
				else if (filename.endsWith("$")) {
					//files that ends with $ are hidden files
				}
				else if (thefile.isDirectory()) {
					app.setIcon(new ImageIcon(createImage("folder.png", WIDTH, HEIGHT)));
					folderdesktop.add(app);
					JPanel folddesk = new JPanel(), desk = new JPanel();
					desk.setLayout(new FlowLayout(FlowLayout.LEFT)); // desktop for new folder's GUI
					desk.setBackground(Color.LIGHT_GRAY);
					folddesk.setBackground(Color.getHSBColor(166, 166, 54)); // folder desktop for new folder's GUI
					folddesk.setPreferredSize(new Dimension(80, 0));
					folddesk.setLayout(new FlowLayout(FlowLayout.CENTER));
					app.addActionListener(_ -> createwindow(new JFrame("GencOS " + app.getText()), desktopPath + File.separator + app.getText(), desk, folddesk));
				}
				// if it is a invalid file type
				else {
					JOptionPane.showMessageDialog(null, filename + " is invalid type for GencOS Operating System\n.wav\n.txt\n.rtf\nis supported for now.", "Invalid file name or type", JOptionPane.ERROR_MESSAGE);
					thefile.delete();
				}
			}
			// refresh frame
			desktop.revalidate();
			desktop.repaint();
			folderdesktop.revalidate();
			folderdesktop.repaint();
		} catch(NullPointerException e) {
			if(reconfig) { // if the desktop path is invalid
				JOptionPane.showMessageDialog(null, "Desktop could not reloaded.\nMay be JAVA_DESKTOP path is not recognized\nor not entered. Please re-enter the path.", "Desktop reload failed", JOptionPane.ERROR_MESSAGE);
				JAVA_DESKTOPpath = configurepath(desktopPath, desktop, folderdesktop);
			}
			else { // if the file is invalid
				JOptionPane.showMessageDialog(null, "File or folder not found. It may be deleted.\nPlease reload the desktop.", "Not Found", JOptionPane.WARNING_MESSAGE);
			}
			return false; // reconfiguration not needed
		}
		return true; // reconfiguration needed. if there is not an exception, always return true 
	}
	
	private static void newfolder(String desktopPath, JPanel desktop, JPanel folderdesktop) {
	    // Prompt the user for the name of the new folder
	    String folderName = JOptionPane.showInputDialog(null, "Enter the name of the new folder:", "New Folder", JOptionPane.QUESTION_MESSAGE);
	    if (desktopPath == null) {
	    	JOptionPane.showMessageDialog(null, "Failed to create folder. JAVA_DESKTOP path is not recognized\nor is not entered.", "Folder Creation Failed", JOptionPane.WARNING_MESSAGE);
		}
		else if (folderName != null) {
		    // Create a File object for the new folder
		    File newFolder = new File(desktopPath, folderName.replaceAll(" ", ""));
		        
		    // Attempt to create the folder
		    if (newFolder.mkdir()) {
		        reloadDesktop(desktopPath, desktop, folderdesktop, true); // Reload the desktop to show the new folder
		    } else {
		    	JOptionPane.showMessageDialog(null, "Failed to create folder. It may already exist.", "Folder Creation Failed", JOptionPane.WARNING_MESSAGE);
		    }
		}
	}
	
	private static void deletefolder(JFrame frame, String desktopPath) {
		if (desktopPath.endsWith("JAVA_DESKTOP")) { // if user tried to delete JAVA_DESKTOP
			JOptionPane.showMessageDialog(null, "You cannot delete JAVA_DESKTOP folder.", "Folder Deletion Failed", JOptionPane.ERROR_MESSAGE);
		}
		else {
			File currentfolder = new File(desktopPath);
			if (currentfolder.delete()) {
				frame.dispose();
			}
			else {
				JOptionPane.showMessageDialog(null, "Folder can not deleted.\nFolder must be empty", "Folder Deletion Failed", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	private static void checkConfig(String desktopPath, JPanel desktop, JPanel folderdesktop) {
		File config = new File(System.getProperty("user.home") + File.separator + "config.txt");
		if (config.exists()) {
            String configFile = config.getAbsolutePath();
            StringBuilder content = new StringBuilder();
            try(BufferedReader reader = new BufferedReader(new FileReader(configFile))){
            	String line;
            	// reads the JAVA_DESKTOP path
            	while ((line = reader.readLine()) != null) {
            		content.append(line);
            	}
            	JAVA_DESKTOPpath = content.toString();
            } catch(IOException ex) {
            	String responses[] = {"Reset", "Cancel"};
            	if(JOptionPane.showOptionDialog(null, 
            			"Something went wrong while reading path.\nPlease check config.txt in your home directory\nif the file is usable or reset now.", 
            			"Failed to read config.txt", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, responses, responses[0]) == 0) {
            		JAVA_DESKTOPpath = configurepath(desktopPath, desktop, folderdesktop);
            	}
            }
            reloadDesktop(JAVA_DESKTOPpath, desktop, folderdesktop, true);
        // if not exists config file
        } else {
        	String responses[] = {"Set path", "Cancel"};
        	if (JOptionPane.showOptionDialog(null, "config.txt could not found.", "Missing configuration", JOptionPane.YES_NO_OPTION,
        			JOptionPane.WARNING_MESSAGE, null, responses, responses[0]) == 0) {
        		JAVA_DESKTOPpath = configurepath(desktopPath, desktop, folderdesktop);
        	}
        }
	}
	
	private static String configurepath(String desktoppath, JPanel desktop, JPanel folderdesktop) {
		String actualPath; // the path we want
		desktoppath = JOptionPane.showInputDialog(null, "Please Enter the path of JAVA_DESKTOP folder.", "Desktop Path Needed", JOptionPane.QUESTION_MESSAGE);
		while(true) { // continues until valid path is entered or path is not entered
			try {
				// if path is not entered
				if (desktoppath == null){ 
					String options[] = {"Continue", "Cancel", "Quit"};
					int response = JOptionPane.showOptionDialog(null, "If you continue now, your files will not loaded into desktop.\nDo you want to continue?", "Path is not entered", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, 0);
					if(response == 0) { // continue
						actualPath = null;
						break;
					}
					else if(response == 2) { //quit
						System.exit(0);
					}
					else { // cancel
						desktoppath = JOptionPane.showInputDialog(null, "Please Enter the path of JAVA_DESKTOP folder.", "Desktop Path Needed", JOptionPane.QUESTION_MESSAGE);
						continue;
					}
				}
				actualPath = desktoppath.replace("\"", ""); // changes to avoid string escape character problems
				if (actualPath.endsWith("JAVA_DESKTOP")) {// if valid
					// write config.txt into the home directory
					String home = System.getProperty("user.home");
					try(BufferedWriter writer = new BufferedWriter(new FileWriter(home + File.separator + "config.txt"))){
						writer.write(actualPath);
						File hiddenaudiosfolder = new File(actualPath, ".audios$"); // .audios$ folder is hidden
						if (!hiddenaudiosfolder.exists()) {
							if(!hiddenaudiosfolder.mkdir()) {
								JOptionPane.showMessageDialog(null, ".audios$ file could not created. Please create a folder\ninto JAVA_DESKTOP that named .audios$ manually", ".audios$ could not be created", JOptionPane.ERROR_MESSAGE);
							}
						}
						else {
							JOptionPane.showMessageDialog(null, hiddenaudiosfolder.getName() + " found in JAVA_DESKTOP.", "Hidden Folder Found", JOptionPane.INFORMATION_MESSAGE);
						}
					} catch(IOException ex) {
						JOptionPane.showMessageDialog(null, "ERROR:\n" + ex.getMessage(), "Unexpected Error Occured", JOptionPane.ERROR_MESSAGE);
					}
					reloadDesktop(actualPath, desktop, folderdesktop, true);
					break;
				}
				else { // if invalid
					desktoppath = JOptionPane.showInputDialog(null, "Path is does not recognized. Check if the path is correct or if JAVA_DESKTOP folder does not exists,\ncreate one and enter the path of the folder.", "No JAVA_DESKTOP founded", JOptionPane.WARNING_MESSAGE);
				}
			}catch(NullPointerException e) { // if path ends with JAVA_DESKTOP but path is incorrect anyway
				desktoppath = JOptionPane.showInputDialog(null, "Path to JAVA_DESKTOP is incorrect.\nPlease check if path is correct written.", "Path Is Incorrect", JOptionPane.WARNING_MESSAGE);
			}
        }
		return actualPath;
	}
	
	private static void importFile(String desktopPath, JPanel desktop, JPanel folderdesktop, boolean reconfig) {
		JFileChooser filechooser = new JFileChooser();
		filechooser.setDialogTitle("Select file to import");
		filechooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		if (filechooser.showDialog(null, "Import") == JFileChooser.APPROVE_OPTION) {
			File selectedfile = filechooser.getSelectedFile();
			String filename = selectedfile.getName();
			try {
				Files.copy(selectedfile.toPath(), Paths.get(desktopPath + File.separator + filename));
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, filename + " could not be copied. It may already exists.", "File copy failed.", JOptionPane.WARNING_MESSAGE);
			}
			reloadDesktop(desktopPath, desktop, folderdesktop, reconfig);
		}
	}
	
	private static void runEXE(String filepath, String filename, String desktopPath, JPanel desktop, JPanel folderdesktop) {
		String[] options = {"Run", "Delete"};
		int response = JOptionPane.showOptionDialog(null, filename, null, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(createImage("exe.png", 70, 70)), options, null);
		if (response == 0){
			try {
	            // Create a ProcessBuilder instance
	            ProcessBuilder processBuilder = new ProcessBuilder(filepath);
	
	            // Optionally, set the working directory
	            // processBuilder.directory(new File("C:\\path\\to\\working\\directory"));
	
	            // Start the process
	            Process process = processBuilder.start();
	
	            // Optionally, wait for the process to complete
	            process.waitFor();
	
	        } catch (Exception ex) {
	            JOptionPane.showMessageDialog(null, "Something went wrong while running file.\nError: " + ex.getMessage(), "File Running Failed", JOptionPane.ERROR_MESSAGE);
	        }
		}
		else if (response == 1) {
			if(!new File(filepath).delete()) {
				JOptionPane.showMessageDialog(null, filename + " could not be deleted. It may be deleted.\nPlease reload the desktop", "Deletion Failed", JOptionPane.WARNING_MESSAGE);
			}
			else {
				reloadDesktop(desktopPath, desktop, folderdesktop, false);
			}
		}
	}
	
	public static void aboutMenu(String title, String madewith, String madeby, String titleIcon, String logoIcon) {
		JFrame aboutframe = new JFrame();
		
		aboutframe.setLayout(new FlowLayout());			
		aboutframe.setBounds(200, 200, 225, 200);
		aboutframe.setResizable(false);
		aboutframe.setIconImage(createImage(titleIcon, 90, 90));
		aboutframe.add(new JLabel(new ImageIcon(createImage(logoIcon, 90, 90))));
		aboutframe.add(new JLabel(title));
		aboutframe.add(new JLabel(madewith));
		aboutframe.add(new JLabel(madeby));
		
		aboutframe.setVisible(true);
	}
	
	public static Image createImage(String ImageIcon, int width, int height) {
		return new ImageIcon(Main.class.getResource(ImageIcon)).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
	}
}