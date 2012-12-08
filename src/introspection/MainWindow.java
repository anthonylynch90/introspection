package introspection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.miginfocom.swing.MigLayout;

public class MainWindow extends JFrame {
	private JScrollPane methodHeaderScrollPane;
	private JScrollPane propertyScrollPane;
	private JScrollPane eventSetScrollPane;
	private JLabel classNameLabel;
	private JTextField classNameTextField;
	private JTextArea methodHeaderTextArea;
	private JTextArea propertyTextArea;
	private JTabbedPane tabbedPane;
	private JButton goButton;
	private DefaultMutableTreeNode root;
	private DefaultTreeModel treeModel;
	private JTree eventSetTree;
	
	public MainWindow() {
		super("Introspection assignment");
		this.setLayout(new MigLayout("insets 7 7 7 7"));
		intialize();
		goButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					Class<?> classObject = Class.forName(classNameTextField.getText());
					
					new Introspector(classObject, methodHeaderTextArea, 
							propertyTextArea, eventSetTree);
				} 
				catch (ClassNotFoundException e1) {
					JOptionPane.showMessageDialog(MainWindow.this, "There is no such class","Message", JOptionPane.PLAIN_MESSAGE);	
				}
			}
			
		});
	}
	
	private void intialize() {
		tabbedPane = new JTabbedPane();
		methodHeaderTextArea = new JTextArea(20, 80);
		methodHeaderScrollPane = new JScrollPane(methodHeaderTextArea);
		tabbedPane.addTab("Methods", methodHeaderScrollPane);
		propertyTextArea = new JTextArea(20, 80);
		propertyScrollPane = new JScrollPane(propertyTextArea);
		tabbedPane.addTab("Properties", propertyScrollPane);
		classNameLabel = new JLabel("Class name:");
		classNameTextField = new JTextField(30);
		goButton = new JButton("Go");
		root = new DefaultMutableTreeNode("root");
		treeModel = new DefaultTreeModel(root);
		eventSetTree = new JTree(treeModel);
		eventSetTree.setRootVisible(false);
		eventSetTree.setShowsRootHandles(true);
		eventSetScrollPane= new JScrollPane(eventSetTree);
		tabbedPane.addTab("Event sets", eventSetScrollPane);
		this.add(classNameLabel, "span 3, split 3");
		classNameTextField.setText("javax.swing.JButton");
		this.add(classNameTextField);
		this.add(goButton, "wrap");
		this.add(tabbedPane, "wrap");
	}
	
	public static void main(String[] args) {
		String systemLookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
		try {
			UIManager.setLookAndFeel(systemLookAndFeelClassName);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		MainWindow window = new MainWindow();
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
}


