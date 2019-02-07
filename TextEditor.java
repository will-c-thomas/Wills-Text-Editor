//Java Text Editor - By William Thomas
//v1.0 - Stable Build

import java.util.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import javax.swing.text.MutableAttributeSet.*;

class TextEditor {

	JFrame window;
	static JFileChooser fc;
	ArrayList<JTextPane> codeAreasList = new ArrayList<JTextPane>();
	ArrayList<String> tabAddressesList = new ArrayList<String>();
	ArrayList<StyledDocument> codeDocList = new ArrayList<StyledDocument>();
	JTabbedPane codePane;
	Container content;

	public TextEditor() {

		//window and content pane setup
		window = new JFrame("Will's TxtEdit");
		content = window.getContentPane();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		//text area panel
		codePane = new JTabbedPane();
		codePane.addTab("New Tab", createTextPanel());
		codePane.setBackground(new Color(130,130,130));
		codePane.setBackgroundAt(codeAreasList.size() - 1, new Color(75,75,75));
		tabAddressesList.add("UNSAVED");

		//menu containers
		JMenuBar mb = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu javaMenu = new JMenu("Java");
		JMenu bfMenu = new JMenu("BF");

		//menu content
		JMenuItem file1 = new JMenuItem("New Tab", KeyEvent.VK_N);
		JMenuItem file2 = new JMenuItem("Open Tab", KeyEvent.VK_O);
		JMenuItem file3 = new JMenuItem("Save As", KeyEvent.VK_S);
		JMenuItem file4 = new JMenuItem("Close Tab");
		JMenuItem file5 = new JMenuItem("Help");
		JMenuItem java1 = new JMenuItem("Compile", KeyEvent.VK_T);
		JMenuItem java2 = new JMenuItem("Compile & Run", KeyEvent.VK_R);
		JMenuItem bf1 = new JMenuItem("Interpret");

		//add action listeners & accelerators
		Listener listener = new Listener();
		file1.addActionListener(listener);
		file1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		file2.addActionListener(listener);
		file2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		file3.addActionListener(listener);
		file3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		file4.addActionListener(listener);
		file5.addActionListener(listener);
		java1.addActionListener(listener);
		java1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		java2.addActionListener(listener);
		java2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		bf1.addActionListener(listener);


		//set menu
		fileMenu.add(file1);
		fileMenu.add(file2);
		fileMenu.add(file3);
		fileMenu.add(file4);
		fileMenu.add(file5);
		javaMenu.add(java1);
		javaMenu.add(java2);
		bfMenu.add(bf1);

		//add file menus to menu bar
		mb.add(fileMenu);
		mb.add(javaMenu);
		mb.add(bfMenu);

		//creates JFileChooser and the Accepted Files filter for open and save windows
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Java & BF: .java, .dpj, .b, .bf", "java", "dpj", "b", "bf");
		fc = new JFileChooser();
		fc.setFileFilter(filter);
		fc.setAcceptAllFileFilterUsed(false);

		//colors and backgrounds
		content.setBackground(new Color(130, 130, 130));
		codePane.setBackground(new Color(60, 60, 60));

		//add stuff to window
		content.add(codePane);
		window.setJMenuBar(mb);

		window.setMinimumSize(new Dimension(400, 400));
		window.setSize(1200,675);
		window.setVisible(true);

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	class Listener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			String action = e.getActionCommand();

			//tab Actions
			if(action.equals("New Tab")) {
				codePane.addTab("New Tab", createTextPanel());
				codePane.setSelectedIndex(codeAreasList.size() - 1);
				codePane.setBackgroundAt(codeAreasList.size() - 1, new Color(75,75,75));
				setNewPath(codeAreasList.size() - 1, "UNSAVED");

			}
			if(action.equals("Open Tab")) {
				//opens an open window
				int value = fc.showOpenDialog(window);

				//check if file was selected
				if(value == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					String text = "";

					//try to read in the text
					try {

						FileReader fr = new FileReader(file);
						BufferedReader br = new BufferedReader(fr);

						//Reads in the file line by line
						String temp = "";
						while( (temp = br.readLine()) != null)
							text += temp + "\n";

						br.close();
					} catch(Exception exc) {
						JOptionPane.showMessageDialog(window, exc.getMessage());
					}

					//create new tab and set text
					codePane.addTab(file.getName(), createTextPanel());
					codePane.setSelectedIndex(codeAreasList.size() - 1);
					codeAreasList.get(codeAreasList.size() - 1).setText(text);
					setNewPath(codeAreasList.size() - 1, file.getAbsolutePath());
					codePane.setBackgroundAt(codeAreasList.size() - 1, new Color(75,75,75));
					setSyntax( codeAreasList.get(codePane.getSelectedIndex()) );

					//if the original new tab is still empty, delete it
					if(codeAreasList.get(0).getText().equals("")){

						//remove the tab from the arraylists
						codeAreasList.remove(0);
						codePane.remove(0);
						codeDocList.remove(0);
						try{
							tabAddressesList.remove(0);
						}catch(Exception exc){
							JOptionPane.showMessageDialog(window, e);
						}

					}

				}

			}
			if(action.equals("Save As")) {
				//opens a save window
				int value = fc.showSaveDialog(window);

				//check if file was selected
				if(value == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();

					try {

						FileWriter fw = new FileWriter(file, false);
						BufferedWriter bw = new BufferedWriter(fw);

						//saves the file to the selected path
						bw.write( codeAreasList.get( codePane.getSelectedIndex() ).getText() );
						bw.flush();
						bw.close();

					} catch(Exception exc) {
						JOptionPane.showMessageDialog(window, exc.getMessage());
					}

					//sets the new path for the tab
					setNewPath(codePane.getSelectedIndex(), file.getAbsolutePath());

				}

			}
			if(action.equals("Close Tab")) {

				//remove the tab from the arraylists
				codeAreasList.remove(codePane.getSelectedIndex());
				codePane.remove(codePane.getSelectedIndex());
				codeDocList.remove(codePane.getSelectedIndex());
				try{
					tabAddressesList.remove(codePane.getSelectedIndex());
				}catch(Exception exc){
					JOptionPane.showMessageDialog(window, e);
				}
			}

			//java menu commands
			if(action.equals("Compile")) {

				try {
					//get the file path and file name
					String fullPath = tabAddressesList.get(codePane.getSelectedIndex());
					String filePath = fullPath.substring(0, cutPathIndex(fullPath));
					String fileName = fullPath.substring(cutPathIndex(fullPath), fullPath.length());

					//Start cmd
					//run "pushd" on the file path to move to that path
					//run "javac" on the file name to compile the program
					Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"pushd " + filePath + " && javac " + fileName + "\"");

				} catch(Exception exc) {
					JOptionPane.showMessageDialog(window, "Compilation Error: Please be sure to save the program first; " + exc.getMessage());
				}

			}
			if(action.equals("Compile & Run")) {

				try {
					//get the file path and file name
					String fullPath = tabAddressesList.get(codePane.getSelectedIndex());
					String filePath = fullPath.substring(0, cutPathIndex(fullPath));
					String fileName = fullPath.substring(cutPathIndex(fullPath), fullPath.length());

					//get index of '.' to remove the extension from the file name
					int extensionIndex = 0;
					for(int i = 0; i < fileName.length(); i++)
						if(fileName.charAt(i) == '.')
							extensionIndex = i;

					//Start cmd
					//run "pushd" on the file path to move to that path
					//run "javac *.java" to compile all java files in the folder
					//run java on the file name without the extension to start the program
					Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"pushd " + filePath + " && javac *.java && java " + fileName.substring(0, extensionIndex) + "\"");

				} catch(Exception exc) {
					JOptionPane.showMessageDialog(window, "Compilation Error: Please be sure to save the program first; " + exc.getMessage());
				}

			}
			if(action.equals("Interpret")) {

				//get Text Component
				JTextComponent txtComp = codeAreasList.get(codePane.getSelectedIndex());

				//create and run BF interpreter
				BrainFkInterpreter bf = new BrainFkInterpreter(txtComp, window);
				String value = bf.interpret();

				//JOptionPane.showMessageDialog(window,value + "\nCheck console for additional details or output.");

			}
			if(action.equals("Help")) {
				//opens a help dialog box with the following message
				JOptionPane.showMessageDialog(window, "Compile: runs the \"javac\" command on the current file.\n\nCompile & Run: runs the command \"javac *.java\" in the parent folder of the current file, and then runs the \"java\" command on the current file.\n\nPlease check GitHub or ReadMe file for information on BF options.");
			}

		}
	}

	//keychecker for syntax highlighting
	class Keychecker extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {

			//if a special character is typed, check the syntax coloring
			//the syntax only  updates when special characters are used
			String specialChars = "@()*-=/+^?%!\\\"\';:.,><[]{} ";
			if(specialChars.contains(String.valueOf(e.getKeyChar())) || e.equals(KeyEvent.VK_BACK_SPACE))  {
				e.consume();
				setSyntax( codeAreasList.get(codePane.getSelectedIndex()) );
			}
		}

	}

	public void setSyntax(JTextPane currPane) {

		MutableAttributeSet attSet = currPane.getInputAttributes();
		StyledDocument doc = currPane.getStyledDocument();
		String text = currPane.getText();

		for(int i = 0; i < text.length(); i++){

			//Found " indicating string
			if(text.charAt(i) == '/' && text.charAt(i+1) == '/') {
				//found comment
				int start = i;

				do{
					i++;
				} while(text.charAt(i) != '\n');

				//set to grey
				if(text.charAt(i) == '\n') {
					StyleConstants.setForeground(attSet, new Color(160, 160, 160));
					doc.setCharacterAttributes(start, i-start, attSet, false);
					resetForeground(attSet, doc, i + 1);
				}

			} else if( text.charAt(i) == '\"' && text.charAt(i-1) != '\\'){
				int start = i;

				//find end of string
				do{
					i++;
				} while( (text.charAt(i) != '\"' || text.charAt(i-1) == '\\') && i+1<text.length());

				//set to yellow
				if(text.charAt(i) == '\"' || text.charAt(i) == '\''){
					StyleConstants.setForeground(attSet, Color.yellow);
					doc.setCharacterAttributes(start, i-start+1, attSet, false);
					++i;
					resetForeground(attSet, doc, i);
				} else {
					i = start;
				}

			} else if("=+-*^/<>?:%![]@".contains( String.valueOf(text.charAt(i)) )){ //found special character
				//set to red
				StyleConstants.setForeground(attSet, new Color(237, 86, 78));
				doc.setCharacterAttributes(i, 1, attSet, false);
				resetForeground(attSet, doc, i + 1);

			} else if("eidwnpstcrfOb".contains( String.valueOf(text.charAt(i)) ) && (i == 0 || "{}(); \n\t".contains( String.valueOf(text.charAt(i - 1)) ))){
				//found potential keyword
				int start = i;
				String[] keyWords = {"Override", "static", "return", "break", "else", "if", "do", "extends", "for", "while", "new", "import", "public", "private","static", "implements", "try", "catch", "instanceof", "false", "true"};
				Arrays.sort(keyWords);

				//find index of the end of the word
				while(!"{}().; \n\t".contains( String.valueOf(text.charAt(i)) )) {
					i++;
				}

				//if the word is found in keyword array,
				if(Arrays.binarySearch(keyWords, text.substring(start, i)) > 0) {
					StyleConstants.setForeground(attSet, new Color(237, 86, 78));
					doc.setCharacterAttributes(start, i-start, attSet, false);
					resetForeground(attSet, doc, i + 1);
				} else {
					i = start + 1;
				}

			} else if(text.charAt(i) == '(') {
				//found potential function call
				int end = i;

				//set i to the start of the function
				while(!"(.; ".contains( String.valueOf(text.charAt(i-1)) ) && i > 1) {
					i--;
				}

				//if the starting char is a function, set to cyan
				if(Character.isLowerCase(text.charAt(i))) {
					StyleConstants.setForeground(attSet, Color.cyan);
					doc.setCharacterAttributes(i, end - i, attSet, false);
					resetForeground(attSet, doc, end + 1);
				}

				//set i to the end again
				i = end;

			} else if(Character.isDigit(text.charAt(i))) {
				//set digit to magenta
				StyleConstants.setForeground(attSet, new Color(194, 83, 226));
				doc.setCharacterAttributes(i, 1, attSet, false);
				resetForeground(attSet, doc, i + 1);

			}

			if(Character.isUpperCase(text.charAt(i)) && (i == 0 || "; \t\n(,".contains( String.valueOf(text.charAt(i-1)) ))) {
				int start = i;

				do{
					i++;
				} while(!"{}())., \n\t".contains( String.valueOf(text.charAt(i)) ));

				if(!text.substring(start, i).equals("Override")) {
					StyleConstants.setForeground(attSet, Color.cyan);
					doc.setCharacterAttributes(start, i-start, attSet, false);
					resetForeground(attSet, doc, i + 1);
				}

			}


		}

	}

	public void resetForeground(MutableAttributeSet attSet, StyledDocument doc, int startIndex) {
		StyleConstants.setForeground(attSet, Color.white);
		doc.setCharacterAttributes(startIndex, startIndex + 1, attSet, false);
	}

	//adds a new path to the path array list
	public void setNewPath(int index, String path) {

		if(tabAddressesList.size() > index) {
			//if the path is already set and is being changed
			tabAddressesList.set(index, path);

		} else {
			if(index == tabAddressesList.size())
				//if the index requested is one higher than highest current
				tabAddressesList.add(path);

			else {
				//if the index requested is more than one higher than the highest current
				for(int i = tabAddressesList.size(); i < index; i++) {
					tabAddressesList.add("UNSAVED");
				}
				tabAddressesList.add(path);
			}

		}

	}

	//find the last \ in the path to get the folder destination
	public int cutPathIndex(String fullPath) {

		int cutIndex = 0;

		for(int i = 0; i < fullPath.length(); i++) {
			if(fullPath.charAt(i) == '\\')
				cutIndex = i + 1;
		}

		return cutIndex;
	}

	//create the text panel for new tabs
	public JPanel createTextPanel() {

		//text area
		JTextPane codeArea = new JTextPane();
		codeArea.setBorder(new EmptyBorder(5,5,5,5));
		codeArea.setBackground(new Color(50, 50, 50));
		codeArea.setForeground(Color.WHITE);
		codeArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
		codeArea.setCaretColor(Color.WHITE);
		codeArea.addKeyListener(new Keychecker());
		codeAreasList.add(codeArea);
		codeDocList.add(codeArea.getStyledDocument());

		//scroll pane
		JScrollPane scrollPane = new JScrollPane(codeArea);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		//panel
		JPanel codePanel = new JPanel();
		codePanel.setLayout(new GridLayout(1,1));
		codePanel.setPreferredSize(new Dimension(1200, 525));
		codePanel.add(scrollPane);
		
		return codePanel;
	}

	public static void main(String[] args) {
		//look and feel code
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {

		}

		//remove default borders from tabbed panes
		UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
		UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);

		new TextEditor();
	}

}