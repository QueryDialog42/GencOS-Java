package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
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
import javax.swing.JScrollPane;

import static main.Main.aboutMenu;
import static main.Main.mainMusic;

public class GenMusicApp {
	static Clip clip = null;
	static JLabel currentfile = null;
	static ArrayList<JButton> buttons;
	public static void openMusicApp(String JAVA_DESKTOPpath, File outsideFile) {
        JFrame frame = new JFrame("GenMusic");
        JPanel music = new JPanel(),
        	   currentmusic = new JPanel(),
        	   controlpanel = new JPanel(new BorderLayout());
        JScrollPane scrollpane = new JScrollPane(music);
        
        JMenuBar menubar = new JMenuBar();
        
        JMenu file = new JMenu("File");
        
        JMenuItem addsound = new JMenuItem("Add sound"),
        		  removesound = new JMenuItem("Delete sound"),
        		  opensound = new JMenuItem("Open sound"),
        		  about = new JMenuItem("About GenMusic");
        
        file.setMnemonic(KeyEvent.VK_F);
        addsound.setMnemonic(KeyEvent.VK_A);
        removesound.setMnemonic(KeyEvent.VK_D);
        opensound.setMnemonic(KeyEvent.VK_O);
        
        scrollpane.setBorder(null);
        
        currentmusic.setLayout(new BoxLayout(currentmusic, BoxLayout.Y_AXIS));
        currentmusic.setPreferredSize(new Dimension(250, 0));
        currentmusic.setBackground(Color.DARK_GRAY);
        controlpanel.setPreferredSize(new Dimension(0, 70));
        
        music.setLayout(new BoxLayout(music, BoxLayout.Y_AXIS));
        music.setBackground(Color.BLACK);
        music.setBorder(null);
        
        frame.setIconImage(new ImageIcon(Main.class.getResource("Genmusic.png")).getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH));
        frame.setBounds(200, 200, 500, 400);
        frame.add(scrollpane);
        
        menubar.setBackground(Color.BLACK);
        menubar.setBorderPainted(false);
        file.setBackground(Color.BLACK);
        file.setForeground(Color.WHITE);
        file.setBorderPainted(false);
        
        JMenuItem[] fileitems = {addsound, removesound, opensound, about}; 
        for (JMenuItem item : fileitems) {
        	item.setBackground(Color.BLACK);
	        item.setForeground(Color.WHITE);
	        item.setBorderPainted(false);
	        file.add(item);
        }
        
        menubar.add(file);
        
        frame.setJMenuBar(menubar);
        frame.setVisible(true);
        
        addsound.addActionListener(_ -> addSound(JAVA_DESKTOPpath, frame, currentmusic, controlpanel, music));
        removesound.addActionListener(_ -> removeSound(JAVA_DESKTOPpath, frame, currentmusic, controlpanel, music));
        opensound.addActionListener(_ -> openSound(JAVA_DESKTOPpath, frame, currentmusic, controlpanel, music, null));
        about.addActionListener(_ -> aboutMenu(mainMusic, "Made with Oracle Java jdk-23", "Made by Batu Genç", "Genmusic.png", "Genmusic.png"));
        
        reloadGenMusic(JAVA_DESKTOPpath, frame, currentmusic, controlpanel, music);
        
        if (outsideFile != null) {
        	openSound(JAVA_DESKTOPpath, frame, currentmusic, controlpanel, music, outsideFile);
        }
        if (currentfile != null) createEnvoriment(frame, currentmusic, controlpanel, currentfile.getText());
    }
	
	private static void reloadGenMusic(String JAVA_DESKTOPpath, JFrame frame, JPanel currentmusic, JPanel controlpanel, JPanel music) {
		music.removeAll();
		
		File audiosfolder = new File(JAVA_DESKTOPpath + "\\" + ".audios$"); // get files from .audios$ folder
		File[] wavfiles = audiosfolder.listFiles();
		buttons = new ArrayList<JButton>();
		for (File thewavfile : wavfiles) {
			String filename = thewavfile.getName();
			if (filename.endsWith(".wav")) {
				JButton button = new JButton(filename);
				button.setHorizontalAlignment(JLabel.LEFT);
				button.setFocusable(false);
				button.setBorderPainted(false);
				button.setIcon(new ImageIcon(new ImageIcon(GenMusicApp.class.getResource("sound128x128.png")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
		        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height)); // Set max size to fill width
		        music.add(button);
		        buttons.add(button);
		        button.addActionListener(_ -> {
		        	for (JButton buttonn : buttons) {
		        		buttonn.setBackground(Color.BLACK);
		        		buttonn.setForeground(Color.WHITE);
		        	}
		        	button.setBackground(Color.GRAY);
		        	button.setForeground(Color.BLACK);
		        	createEnvoriment(frame, currentmusic, controlpanel, thewavfile.getName());
		        	clip = playMusic(thewavfile);
		        });
			}
			else {
				JOptionPane.showMessageDialog(null, filename + " is invalid file type for GenMusic.\n.wav\nis supported for now.", "Invalid file name or type", JOptionPane.ERROR_MESSAGE);
				thewavfile.delete();
			}
		}
		setSelectedSound(music);
	}
	
	private static void createEnvoriment(JFrame frame, JPanel currentmusic, JPanel controlpanel, String thewavfilename) {
		currentmusic.removeAll();
		
		JLabel logo = new JLabel(new ImageIcon(new ImageIcon(GenMusicApp.class.getResource("Genmusic.png")).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
		
		currentfile = new JLabel(thewavfilename);
		currentfile.setFont(new Font("Arial", Font.BOLD, 15));
		currentfile.setForeground(Color.WHITE);
		logo.setAlignmentX(JLabel.CENTER_ALIGNMENT);
	    currentfile.setAlignmentX(JLabel.CENTER_ALIGNMENT);
	    
	    currentmusic.add(logo);
		currentmusic.add(currentfile);
		
		if (!controlpanel.isValid()) { // if control panel is not created
			JPanel movepanel = new JPanel(),
				   skippanel = new JPanel();
			
			Color color = Color.BLACK;
			
			movepanel.setPreferredSize(new Dimension(0, 35));
			skippanel.setPreferredSize(new Dimension(0, 35));
			movepanel.setBackground(color);
			skippanel.setBackground(color);
			
			JButton start = new JButton("Start"),
				stop = new JButton("Stop"),
				restart = new JButton("Restart"),
				back = new JButton("10s back"),
				front = new JButton("10s front");
			
			start.setEnabled(false);
			
			JButton[] movepanelbuttons = {start, stop, restart};
			for (JButton buttons : movepanelbuttons) {
				buttons.setFocusable(false);
				buttons.setBackground(Color.DARK_GRAY);
				buttons.setForeground(Color.WHITE);
				buttons.setBorderPainted(false);
				movepanel.add(buttons);
			}
			
			JButton[] skippanelbuttons = {back, front};
			for (JButton buttons : skippanelbuttons) {
				buttons.setFocusable(false);
				buttons.setBackground(Color.DARK_GRAY);
				buttons.setForeground(Color.WHITE);
				buttons.setBorderPainted(false);
				skippanel.add(buttons);
			}
			
			controlpanel.add(movepanel, BorderLayout.NORTH);
			controlpanel.add(skippanel, BorderLayout.SOUTH);
			
			frame.add(controlpanel, BorderLayout.SOUTH);
			frame.add(currentmusic, BorderLayout.EAST);
		
			start.addActionListener(_ -> {
				clip.start();
				start.setEnabled(false);
				stop.setEnabled(true);
			});
			
			stop.addActionListener(_ -> {
				clip.stop();
				stop.setEnabled(false);
				start.setEnabled(true);
			});
			
			restart.addActionListener(_ -> {
				clip.setMicrosecondPosition(0);
				clip.start();
				if (start.isEnabled()) start.setEnabled(false);
				if (!stop.isEnabled()) stop.setEnabled(true);
			});
			
			back.addActionListener(_ -> {
				clip.setMicrosecondPosition(clip.getMicrosecondPosition() - 10000000);
			});
			
			front.addActionListener(_ -> {
				clip.setMicrosecondPosition(clip.getMicrosecondPosition() + 10000000);
			});
			
			//if music stopped and reopened the window
			if (clip != null && !clip.isRunning()) {
				stop.setEnabled(false);
				start.setEnabled(true);
			}
		}
		frame.revalidate();
		frame.repaint();
	}
	
	private static Clip playMusic(File thewavfile) {
		try {
			if (clip != null) clip.close();
			AudioInputStream audiostream = AudioSystem.getAudioInputStream(thewavfile);
			clip = AudioSystem.getClip();
			clip.open(audiostream);
			clip.start();
			return clip;
	
		} catch (UnsupportedAudioFileException ex) {
			JOptionPane.showMessageDialog(null, thewavfile.getName() + " is not supported for java jdk-23", "Unsupported Audio File", JOptionPane.ERROR_MESSAGE);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	private static void addSound(String JAVA_DESKTOPpath, JFrame frame, JPanel currentmusic, JPanel controlpanel, JPanel music) {
		JFileChooser filechooser = new JFileChooser();
		filechooser.setDialogTitle("Add Sound");
		filechooser.setCurrentDirectory(new File(JAVA_DESKTOPpath));
		if (filechooser.showDialog(null, "Add") == JFileChooser.APPROVE_OPTION) {
			File selectedfile = filechooser.getSelectedFile();
			String filename = selectedfile.getName();
			if (filename.endsWith(".wav")) {
	            try {
	                // Copy the selected file to the destination directory
	                Files.copy(selectedfile.toPath(), Paths.get(new File(JAVA_DESKTOPpath + File.separator + ".audios$").getAbsolutePath(), filename));
	                reloadGenMusic(JAVA_DESKTOPpath, frame, currentmusic, controlpanel, music);
	            } catch (IOException e) {
	                JOptionPane.showMessageDialog(null, filename + " could not be copied to .audios$ folder.\nIt may already exist.", "File Copy Error", JOptionPane.WARNING_MESSAGE);
	            }
			}
			// if user opened the non .wav file
			else {
				JOptionPane.showMessageDialog(null, filename + " is not supported for GenMusic for now.\n.wav\nfiles are supported", "Unsupported file", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private static void removeSound(String JAVA_DESKTOPpath, JFrame frame, JPanel currentmusic, JPanel controlpanel, JPanel music) {
		JFileChooser filechooser = new JFileChooser();
		filechooser.setDialogTitle("Delete Sound");
		filechooser.setCurrentDirectory(new File(JAVA_DESKTOPpath + File.separator + ".audios$"));
		if (filechooser.showDialog(null, "Delete") == JFileChooser.APPROVE_OPTION) {
			File selectedfile = filechooser.getSelectedFile().getAbsoluteFile();
			String filename = selectedfile.getName();
			if (filename.endsWith(".wav")) {
				if (selectedfile.delete()) {
					reloadGenMusic(JAVA_DESKTOPpath, frame, currentmusic, controlpanel, music);
				}
				else {
					JOptionPane.showMessageDialog(null, selectedfile.getName() + " could not deleted. It may be already deleted.", "Deletion failed", JOptionPane.ERROR_MESSAGE);
				}
			}
			else {
				JOptionPane.showMessageDialog(null, "Please select a .wav file to delete.", "Selection is wrong", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	public static void openSound(String JAVA_DESKTOPpath, JFrame frame, JPanel currentmusic, JPanel controlpanel, JPanel music, File outsideFile) {
		JFileChooser filechooser = new JFileChooser();
		filechooser.setDialogTitle("Open sound");
		filechooser.setCurrentDirectory(new File(JAVA_DESKTOPpath));
		if (outsideFile == null) {
			if (filechooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File selectedfile = filechooser.getSelectedFile();
				String filename = selectedfile.getName();
				if (filename.endsWith(".wav")) {
					createEnvoriment(frame, currentmusic, controlpanel, selectedfile.getName());
					clip = playMusic(selectedfile);
					setSelectedSound(music);
				}
				else {
					JOptionPane.showMessageDialog(null, filename + " is not supported audio file for GenMusic.\n.wav\nis supported for now.", "Unsupported file", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		// if user clicked a .wav file button in JAVA_DESKTOP
		else {
			createEnvoriment(frame, currentmusic, controlpanel, outsideFile.getName());
			clip = playMusic(outsideFile);
			setSelectedSound(music);
		}
	}
	
	private static void setSelectedSound(JPanel music) {
		for (JButton buttonn : buttons) {
     		if (currentfile != null && currentfile.getText().equals(buttonn.getText())) {
     			buttonn.setBackground(Color.GRAY);
		        buttonn.setForeground(Color.BLACK);
     		}
     		else {
     			buttonn.setBackground(Color.BLACK);
        		buttonn.setForeground(Color.WHITE);
     		}
     	}
		music.revalidate();
		music.repaint();
	}
}
