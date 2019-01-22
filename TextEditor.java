//Java Text Editor - By William Thomas
//v0.1 - Stable Build

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;

class TextEditor {

	JFrame window;
	static JFileChooser fc;
	ArrayList<JTextArea> codeAreasList = new ArrayList<JTextArea>();
	ArrayList<String> tabAddressesList = new ArrayList<String>();
	JTabbedPane codePane;
	JTextPane outputArea;
	Container content;

	public TextEditor() {

		//window and content pane setup
		window = new JFrame("Will's TxtEdit");
		content = window.getContentPane();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		Listener listener = new Listener();

		//text area panel
		codePane = new JTabbedPane();
		codePane.setPreferredSize(new Dimension(1200, 525));
		codePane.setMinimumSize(new Dimension(400, 300));
		codePane.addTab("New Tab", createTextPanel());
		codePane.setBackground(new Color(130,130,130));
		codePane.setBackgroundAt(codeAreasList.size() - 1, new Color(75,75,75));
		tabAddressesList.add("UNSAVED");

		//text area
		outputArea = new JTextPane();
		outputArea.setBorder(new EmptyBorder(5,5,5,5));
		outputArea.setEditable(false);
		outputArea.setBackground(new Color(50, 50, 50));
		outputArea.setForeground(Color.WHITE);
		outputArea.setFont(outputArea.getFont().deriveFont(14f));

		//text area panel
		JPanel outputPanel = new JPanel();
		outputPanel.setLayout(new GridLayout(1,1));
		outputPanel.setBorder(BorderFactory.createMatteBorder(5,0,0,0, new Color(130,130,130)));
		outputPanel.setPreferredSize(new Dimension(1200, 150));
		outputPanel.setMinimumSize(new Dimension(400, 100));
		outputPanel.add(outputArea);

		//menu containers
		JMenuBar mb = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu javaMenu = new JMenu("Java");

		//menu content
		JMenuItem file1 = new JMenuItem("New Tab");
		JMenuItem file2 = new JMenuItem("Open Tab");
		JMenuItem file3 = new JMenuItem("Save As");
		JMenuItem file4 = new JMenuItem("Close Tab");
		JMenuItem java1 = new JMenuItem("Compile");
		JMenuItem java2 = new JMenuItem("Compile & Run");

		//add action listeners
		file1.addActionListener(listener);
		file2.addActionListener(listener);
		file3.addActionListener(listener);
		file4.addActionListener(listener);
		java1.addActionListener(listener);
		java2.addActionListener(listener);

		//set menu
		fileMenu.add(file1);
		fileMenu.add(file2);
		fileMenu.add(file3);
		fileMenu.add(file4);
		javaMenu.add(java1);
		javaMenu.add(java2);

		//add file menus to menu bar
		mb.add(fileMenu);
		mb.add(javaMenu);

		//colors and backgrounds
		content.setBackground(new Color(130, 130, 130));
		codePane.setBackground(new Color(60, 60, 60));

		//add stuff to window
		content.add(codePane);
		content.add(outputPanel);
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
				//load file
				int value = fc.showOpenDialog(window);

				//check if file was selected
				if(value == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					String text = "";

					//try to read in the text
					try {

						FileReader fr = new FileReader(file);
						BufferedReader br = new BufferedReader(fr);

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
				}

			}
			if(action.equals("Save As")) {
				int value = fc.showSaveDialog(window);

				if(value == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();

					try {

						FileWriter fw = new FileWriter(file, false);
						BufferedWriter bw = new BufferedWriter(fw);

						bw.write( codeAreasList.get( codePane.getSelectedIndex() ).getText() );
						bw.flush();
						bw.close();

					} catch(Exception exc) {
						JOptionPane.showMessageDialog(window, exc.getMessage());
					}

					setNewPath(codePane.getSelectedIndex(), file.getAbsolutePath());

				}

			}
			if(action.equals("Close Tab")) {
				codeAreasList.remove(codePane.getSelectedIndex());
				codePane.remove(codePane.getSelectedIndex());
				try{
					tabAddressesList.remove(codePane.getSelectedIndex());
				}catch(Exception exc){

				}
			}

			//java menu commands
			if(action.equals("Compile")) {

				try {
					String fullPath = tabAddressesList.get(codePane.getSelectedIndex());
					String filePath = fullPath.substring(0, cutPathIndex(fullPath));
					String fileName = fullPath.substring(cutPathIndex(fullPath), fullPath.length());

					Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"pushd " + filePath + " && javac " + fileName + "\"");

				} catch(Exception exc) {
					JOptionPane.showMessageDialog(window, "Compilation Error: Please be sure to save the program first; " + exc.getMessage());
				}

			}
			if(action.equals("Compile & Run")) {

				try {
					String fullPath = tabAddressesList.get(codePane.getSelectedIndex());
					String filePath = fullPath.substring(0, cutPathIndex(fullPath));
					String fileName = fullPath.substring(cutPathIndex(fullPath), fullPath.length());

					int extensionIndex = 0;
					for(int i = 0; i < fileName.length(); i++)
						if(fileName.charAt(i) == '.')
							extensionIndex = i;


					Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"pushd " + filePath + " && javac " + fileName + " && java " + fileName.substring(0, extensionIndex) + "\"");

				} catch(Exception exc) {
					JOptionPane.showMessageDialog(window, "Compilation Error: Please be sure to save the program first; " + exc.getMessage());
				}

			}

		}
	}

	public void setNewPath(int index, String path) {

		if(tabAddressesList.size() > index) {
			tabAddressesList.set(index, path);
		} else {
			if(index == tabAddressesList.size())
				tabAddressesList.add(path);
			else {
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

	//for new tabs
	public JPanel createTextPanel() {

		//text area
		JTextArea codeArea = new JTextArea();
		codeArea.setBorder(new EmptyBorder(5,5,5,5));
		codeArea.setTabSize(4);
		codeArea.setBackground(new Color(50, 50, 50));
		codeArea.setForeground(Color.WHITE);
		codeArea.setFont(codeArea.getFont().deriveFont(14f));
		codeArea.setCaretColor(Color.WHITE);
		codeAreasList.add(codeArea);

		//scroll pane
		JScrollPane scrollPane = new JScrollPane(codeArea);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		//panel
		JPanel codePanel = new JPanel();
		codePanel.setLayout(new GridLayout(1,1));
		codePanel.setBorder(BorderFactory.createMatteBorder(0,0,5,0, new Color(130,130,130)));
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

		//creates JFileChooser and the Java filter
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Java", "java");
		fc = new JFileChooser();
		fc.setFileFilter(filter);
		fc.setAcceptAllFileFilterUsed(false);

		//remove default borders from tabbed panes
		UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
		UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);

		new TextEditor();

	}

}