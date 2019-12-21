package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.BoxLayout;

public class MoveFile extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblFolderName;
	
	Client parent;
	String[] folders;
	String foldersSemic;
	private JComboBox comboBox;
	private JButton cancelButton;
	
	// Constructor
	public MoveFile(Client parent, String foldersSemic) {
		this.parent = parent;
		this.foldersSemic = foldersSemic;
		
		if (foldersSemic != null) {
			this.folders = foldersSemic.split(";");
		}
		
		
		constructor();
	}

	
	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		try {
//			MoveFile dialog = new MoveFile();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Create the dialog.
	 */
	public void constructor() {
		setBounds(500, 250, 338, 271);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.add(getLblFolderName());
		{
			JButton btnMoveBack = new JButton("Move back");
			btnMoveBack.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					parent.moveFile("move back", "");
					
					dispose();
				}
			});
			btnMoveBack.setBounds(10, 130, 89, 23);
			contentPanel.add(btnMoveBack);
		}
		{
			JLabel lblMoveOneFolder = new JLabel("Move one folder back");
			lblMoveOneFolder.setBounds(10, 105, 126, 14);
			contentPanel.add(lblMoveOneFolder);
		}
		{
			JLabel lblMoveTo = new JLabel("Move to:");
			lblMoveTo.setHorizontalAlignment(SwingConstants.RIGHT);
			lblMoveTo.setBounds(193, 68, 118, 14);
			contentPanel.add(lblMoveTo);
		}
		
		
		
		{
			JButton okButton = new JButton("Move to ");
			okButton.setBounds(203, 130, 108, 23);
			contentPanel.add(okButton);
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					String selectedFolder = (String) comboBox.getSelectedItem();
					
					parent.moveFile("move to", selectedFolder);
					
					dispose();
				}

				private boolean valid(String newFolderName) {
					
					if (newFolderName.charAt(0) == ' ') {
						return false;
					}
					
					for (int i = 0; i<newFolderName.length(); i++) {
						if (newFolderName.charAt(i) == '/' ||
							newFolderName.charAt(i) == '.' ||
							newFolderName.charAt(i) == '\\') 
						{
							return false;
						}
						
						
						
					}
					
			
					return true;
					
				}
			});
			okButton.setActionCommand("OK");
			getRootPane().setDefaultButton(okButton);
			
			
			
			
			
			
			
			
		}
		
		// COMBO BOX
		contentPanel.add(getComboBox());
		
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						dispose();
						
					}
				});
				cancelButton.setActionCommand("Cancel");
			}
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			buttonPane.add(cancelButton);
		}
	}
	private JLabel getLblFolderName() {
		if (lblFolderName == null) {
			lblFolderName = new JLabel("Move file");
			lblFolderName.setFont(new Font("Tahoma", Font.PLAIN, 16));
			lblFolderName.setHorizontalAlignment(SwingConstants.CENTER);
			lblFolderName.setBounds(10, 11, 301, 32);
		}
		return lblFolderName;
	}
	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox();
//			comboBox.setModel(new DefaultComboBoxModel(new String[] {"Select folder"}));
			
			
			if (folders != null) {
				comboBox.setModel(new DefaultComboBoxModel(folders));
			}
			else {
				comboBox.setModel(new DefaultComboBoxModel(new String[] {"No folders"}));
				comboBox.setEnabled(false);
			}
			
			comboBox.setBounds(203, 99, 108, 20);
		}
		return comboBox;
	}
}
