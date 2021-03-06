package ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import dao.DBManager;

import java.awt.Color;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends JFrame {

	private JPanel contentPane;
	public DBManager db;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
					frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setTitle("Select User Group");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
	    addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
	           db.quit();
	           System.out.println("db quit");
	           System.exit(0);
	        }
	      });
		
		db = new DBManager();
		
		JButton btnCustomer = new JButton("Customer");
		btnCustomer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new CustomerFrame(db).setVisible(true);
				dispose();
			}
		});
		btnCustomer.setBounds(157, 22, 138, 60);
		contentPane.add(btnCustomer);
		
		JButton btnCashier = new JButton("Cashier");
		btnCashier.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CashierFrame(db).setVisible(true);
				dispose();
			}
		});
		btnCashier.setBounds(157, 104, 138, 60);
		contentPane.add(btnCashier);
		
		JButton btnManager = new JButton("Manager");
		btnManager.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ManagerFrame(db).setVisible(true);
				dispose();
			}
		});
		btnManager.setBounds(157, 181, 138, 60);
		contentPane.add(btnManager);

	}
	

}
