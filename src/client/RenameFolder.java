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

public class RenameFolder extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblFolderName;
	private JTextField textNewFolderName;
	
	Client parent;
	
	// Constructor
	public RenameFolder(Client parent) {
		this();
		
		this.parent = parent;
	}

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			RenameFolder dialog = new RenameFolder();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public RenameFolder() {
		setBounds(500, 250, 337, 210);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.add(getLblFolderName());
		contentPanel.add(getTextNewFolderName());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						if (textNewFolderName.getText().equals("")) {
							JOptionPane.showMessageDialog(
									RenameFolder.this,
									"Please eneter field", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						String newFolderName = textNewFolderName.getText();
						
						if (valid(newFolderName)) {
							parent.renameFolder(newFolderName.trim());
							
							dispose();
						}
						else {
							JOptionPane.showMessageDialog(
									RenameFolder.this,
									"Invalid name!", 
									"Error",
									JOptionPane.ERROR_MESSAGE);
						}
						
						
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
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						dispose();
						
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	private JLabel getLblFolderName() {
		if (lblFolderName == null) {
			lblFolderName = new JLabel("New folder name");
			lblFolderName.setFont(new Font("Tahoma", Font.PLAIN, 16));
			lblFolderName.setHorizontalAlignment(SwingConstants.CENTER);
			lblFolderName.setBounds(10, 24, 301, 32);
		}
		return lblFolderName;
	}
	private JTextField getTextNewFolderName() {
		if (textNewFolderName == null) {
			textNewFolderName = new JTextField();
			textNewFolderName.setBounds(97, 68, 124, 20);
			textNewFolderName.setColumns(10);
		}
		return textNewFolderName;
	}
}
