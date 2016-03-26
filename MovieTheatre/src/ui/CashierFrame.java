package ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import dao.DBManager;

import java.awt.CardLayout;

import net.miginfocom.swing.MigLayout;

import javax.swing.JSplitPane;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;


public class CashierFrame extends JFrame {
	private JTextField textEIDField;
	private String eid;
	private String name;
	private DBManager db;
	
	private JPanel panelLeft;
	private JPanel panelRight;
	/**
	 * Create the frame.
	 */
	public CashierFrame(DBManager db) {
		this.db = db;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 583, 433);
		setTitle("Cashier View");
		getContentPane().setLayout(new MigLayout("", "[][][][][][][][][][][][grow]", "[][][][][][][][][grow]"));
		
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
               db.quit();
               System.out.println("db quit");
               System.exit(0);
            }
          });		
		
		JPanel mainPanel = new JPanel();
		getContentPane().add(mainPanel, "cell 0 0 12 9,grow");
		mainPanel.setLayout(new CardLayout(0, 0));
		
		JPanel panelLogin = new JPanel();
		
		JLabel lblEnterEid = new JLabel("Enter EID:");
		panelLogin.add(lblEnterEid);
		
		textEIDField = new JTextField();
		panelLogin.add(textEIDField);
		textEIDField.setColumns(10);
		
		JButton btnNewButton = new JButton("Login");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
				cardLayout.show(mainPanel, "Cashier view");
				try {
					String[] eidEnamePair =db.getEIDEname(textEIDField.getText());
					eid = eidEnamePair[0];
					name = eidEnamePair[1];
					JLabel lblEid = new JLabel("EID: "+eid);
					panelLeft.add(lblEid, "cell 0 9,aligny bottom");
					JLabel lblName = new JLabel("Name:"+name);
					panelLeft.add(lblName, "cell 0 10,aligny bottom");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(mainPanel, "Invalid SQL Operation");
				}
			}
		});
		panelLogin.add(btnNewButton);
		
		JPanel panelCashier = new JPanel();
		
		mainPanel.add(panelLogin, "Login");
		mainPanel.add(panelCashier, "Cashier view");
		panelCashier.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		JSplitPane splitPane = new JSplitPane();
		panelCashier.add(splitPane, "cell 0 0,grow");
		
		//Left Panel Contain Button and eid,ename (created in Login ActionListener)
		panelLeft = new JPanel();
		splitPane.setLeftComponent(panelLeft);
		panelLeft.setLayout(new MigLayout("wrap 1", "[grow,fill]", "[][][]"));
		
		JButton btnMovieInfo = new JButton("Movie Info");
		btnMovieInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CardLayout cardLayout = (CardLayout) panelRight.getLayout();
				cardLayout.show(panelRight, "Movie Info");
			}
		});
		panelLeft.add(btnMovieInfo, "cell 0 0,alignx left,aligny top");
		
		JButton btnSellTicket = new JButton("Sell Ticket");
		btnSellTicket.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cardLayout = (CardLayout) panelRight.getLayout();
				cardLayout.show(panelRight, "Sell Tickets");
			}
		});
		panelLeft.add(btnSellTicket, "cell 0 1");
		
		JButton btnAddHours = new JButton("Add Hours");
		panelLeft.add(btnAddHours, "cell 0 2");
		

		//Right Panel should show correct information correspond to the pushed button
		panelRight = new JPanel();
		splitPane.setRightComponent(panelRight);
		panelRight.setLayout(new CardLayout(0, 0));
		
		JPanel panelMovieInfo = new MovieInfoPanel(db);
		JPanel panelSellTicket = new TicketPanel();
		panelRight.add(panelMovieInfo,"Movie Info");
		panelRight.add(panelSellTicket, "Sell Tickets");
		pack();
	}
}