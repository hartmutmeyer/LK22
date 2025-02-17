import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class RechentrainerClient extends JFrame {

	private JPanel contentPane;
	private JTextField tfServer;
	private JTextField tfName;
	JTextField tfAufgabe;
	private JTextField tfLoesung;
	private JButton btnStarten;
	private JButton btnLoesungSenden;
	private OutputStreamWriter out;

	public RechentrainerClient() {
		super("Rechentrainer Client");
		createGUI();
	}

	public void createGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 384, 193);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblServer = new JLabel("Server: ");
		lblServer.setBounds(12, 12, 55, 15);
		contentPane.add(lblServer);

		JLabel lblName = new JLabel("Name: ");
		lblName.setBounds(12, 39, 55, 15);
		contentPane.add(lblName);

		JLabel lblAufgabe = new JLabel("Aufgabe: ");
		lblAufgabe.setBounds(12, 99, 55, 15);
		contentPane.add(lblAufgabe);

		JLabel lblLoesung = new JLabel("Lösung: ");
		lblLoesung.setBounds(12, 126, 55, 15);
		contentPane.add(lblLoesung);

		tfServer = new JTextField();
		tfServer.setText("localhost");
		tfServer.setBounds(85, 10, 114, 19);
		contentPane.add(tfServer);
		tfServer.setColumns(10);

		tfName = new JTextField();
		tfName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				verbinden();
			}
		});
		tfName.setBounds(85, 37, 114, 19);
		contentPane.add(tfName);
		tfName.setColumns(10);

		tfAufgabe = new JTextField();
		tfAufgabe.setEditable(false);
		tfAufgabe.setBounds(85, 97, 114, 19);
		contentPane.add(tfAufgabe);
		tfAufgabe.setColumns(10);

		tfLoesung = new JTextField();
		tfLoesung.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loesungSenden();
			}
		});
		tfLoesung.setBounds(85, 124, 114, 19);
		contentPane.add(tfLoesung);
		tfLoesung.setColumns(10);

		btnStarten = new JButton("starten");
		btnStarten.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				verbinden();
			}
		});
		btnStarten.setBounds(238, 34, 121, 25);
		contentPane.add(btnStarten);

		btnLoesungSenden = new JButton("Lösung senden");
		btnLoesungSenden.setEnabled(false);
		btnLoesungSenden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loesungSenden();
			}
		});
		btnLoesungSenden.setBounds(238, 121, 121, 25);
		contentPane.add(btnLoesungSenden);

		setResizable(false);
	}

	public void verbinden() {
		Socket s;
		try {
			String serverName = tfServer.getText();
			String name = tfName.getText();
			if (name.equals("")) {
				JOptionPane.showMessageDialog(this, "Du musst einen Namen angeben.");
			} else {
				s = new Socket(serverName, 33333);
				InputStreamReader in = new InputStreamReader(s.getInputStream(), "UTF-8");
				out = new OutputStreamWriter(s.getOutputStream(), "UTF-8");
				LeseThread lt = new LeseThread(in, this);
				lt.start();
				out.write(name + '$');
				out.flush();
				btnStarten.setEnabled(false);
				btnLoesungSenden.setEnabled(true);
			}
		} catch (IOException e) {
			System.out.println("IOException");
		}
	}

	public void loesungSenden() {
		String loesung = tfLoesung.getText();
		try {
			out.write(loesung + '$');
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					RechentrainerClient frame = new RechentrainerClient();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
