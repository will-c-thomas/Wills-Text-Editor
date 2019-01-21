//Java Text Editor - By William Thomas

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
		codePane.setBackgroundAt(codeAreasList.size() - 1, new Color(75,75,75));

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
		JMenu tabMenu = new JMenu("Tab");
		JMenu javaMenu = new JMenu("Java");

		//menu content
		JMenuItem file1 = new JMenuItem("New");
		JMenuItem file2 = new JMenuItem("Open");
		JMenuItem file3 = new JMenuItem("Save");
		JMenuItem java1 = new JMenuItem("Compile");
		JMenuItem java2 = new JMenuItem("Compile & Run");
		JMenuItem tab1 = new JMenuItem("New Tab");
		JMenuItem tab2 = new JMenuItem("Open Tab");
		JMenuItem tab3 = new JMenuItem("Save As");
		JMenuItem tab4 = new JMenuItem("Close Tab");

		//add action listeners
		file1.addActionListener(listener);
		file2.addActionListener(listener);
		file3.addActionListener(listener);
		java1.addActionListener(listener);
		java2.addActionListener(listener);
		tab1.addActionListener(listener);
		tab2.addActionListener(listener);
		tab3.addActionListener(listener);
		tab4.addActionListener(listener);

		//set menu
		fileMenu.add(file1);
		fileMenu.add(file2);
		fileMenu.add(file3);
		javaMenu.add(java1);
		javaMenu.add(java2);
		tabMenu.add(tab1);
		tabMenu.add(tab2);
		tabMenu.add(tab3);
		tabMenu.add(tab4);

		//add file menus to menu bar
		mb.add(fileMenu);
		mb.add(tabMenu);
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

						System.out.println("test");

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

				}

			}
			if(action.equals("Close Tab")) {
				codePane.remove( codePane.getSelectedIndex() );
			}

		}
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