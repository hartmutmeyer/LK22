import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

public class FahrzeugeHinzufuegen extends JFrame {

	private JPanel contentPane;
	private JTextField textFieldKFZKennzeichen;
	private JTextField textFieldTyp;
	private JTextField textFieldFarbe;
	private JTextField textFieldBaujahr;
	private Connection conn;
	private Statement stmt;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					FahrzeugeHinzufuegen frame = new FahrzeugeHinzufuegen();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FahrzeugeHinzufuegen() {
		createGUI();
		datenbankOeffnen();
	}

	private void createGUI() {
		setTitle("Fahrzeuge Hinzufügen");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 295, 202);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 5));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(0, 2, 10, 5));

		JLabel lblKFZKennzeichen = new JLabel("KFZ-Kennzeichen:");
		lblKFZKennzeichen.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblKFZKennzeichen);

		textFieldKFZKennzeichen = new JTextField();
		textFieldKFZKennzeichen.setText("HB - EM 468");
		lblKFZKennzeichen.setLabelFor(textFieldKFZKennzeichen);
		panel.add(textFieldKFZKennzeichen);
		textFieldKFZKennzeichen.setColumns(10);

		JLabel lblTyp = new JLabel("Typ:");
		lblTyp.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblTyp);

		textFieldTyp = new JTextField();
		lblTyp.setLabelFor(textFieldTyp);
		textFieldTyp.setText("Mercedes Limo");
		panel.add(textFieldTyp);
		textFieldTyp.setColumns(10);

		JLabel lblFarbe = new JLabel("Farbe:");
		lblFarbe.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblFarbe);

		textFieldFarbe = new JTextField();
		lblFarbe.setLabelFor(textFieldFarbe);
		textFieldFarbe.setText("schwarz");
		panel.add(textFieldFarbe);
		textFieldFarbe.setColumns(10);

		JLabel lblBaujahr = new JLabel("Baujahr:");
		lblBaujahr.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblBaujahr);

		textFieldBaujahr = new JTextField();
		lblBaujahr.setLabelFor(textFieldBaujahr);
		textFieldBaujahr.setText("2012");
		panel.add(textFieldBaujahr);
		textFieldBaujahr.setColumns(10);

		JButton btnFahrzeugHinzufgen = new JButton("Fahrzeug hinzufügen");
		btnFahrzeugHinzufgen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				datenbankFahrzeugHinzufuegen();
			}
		});
		contentPane.add(btnFahrzeugHinzufgen, BorderLayout.SOUTH);
	}

	private void datenbankOeffnen() {
		Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/fahrzeuge"
					+ "?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true", "root", "root");
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void datenbankFahrzeugHinzufuegen() {
		String kennzeichen = textFieldKFZKennzeichen.getText();
		String typ = textFieldTyp.getText();
		String farbe = textFieldFarbe.getText();
		String baujahr = textFieldBaujahr.getText();
		String sql;
		try {
			if (baujahr.isBlank()) {
				JOptionPane.showMessageDialog(this, "Das Baujahr muss angegeben werden!");
			} else {
				sql = "INSERT INTO fahrzeug VALUES ('" + kennzeichen + "', '" + typ + "', '" + farbe + "', '" + baujahr + "')";
				System.out.println("datenbankFahrzeugHinzufuegen(): " + sql);
				int anzahl = stmt.executeUpdate(sql);
				// Falls erfolgreich Meldung geben
				if (anzahl == 1) { // ERFOLG
					JOptionPane.showMessageDialog(this, "Fahreug " + kennzeichen + " erfolgreich hinzugefügt.");
				}
			}
		} catch (SQLIntegrityConstraintViolationException e) {
			// Falls Kennzeichen bereits existierte: Meldung geben
			JOptionPane.showMessageDialog(this, "Fahreug " + kennzeichen + " gibt es bereits!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
