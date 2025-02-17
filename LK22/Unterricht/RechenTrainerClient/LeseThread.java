import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

public class LeseThread extends Thread {

	private InputStreamReader in;
	private RechentrainerClient main;

	public LeseThread(InputStreamReader in, RechentrainerClient main) {
		this.in = in;
		this.main = main; 
	}

	@Override
	public void run() {
		int zeichen;
		char c;
		try {
			while ((zeichen = in.read()) != -1 ) {
				c = (char) zeichen;
				switch (c) {
				case '?':
					neueAufgabe();
					break;
				case '%':
					meldung();
					break;
				default:
					System.out.println("DAS DARF NICHT PASSIEREN");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void neueAufgabe() throws IOException {
		int zeichen;
		char c;
		String neueAufgabe = "";
		while ((zeichen = in.read()) != '$') {
			c = (char) zeichen;
			neueAufgabe += c;
		}
		System.out.println(neueAufgabe);
		main.tfAufgabe.setText(neueAufgabe);
	}
	
	private void meldung() throws IOException {
		int zeichen;
		char c;
		String meldung = "";
		while ((zeichen = in.read()) != '$') {
			c = (char) zeichen;
			meldung += c;
		}
		System.out.println(meldung);
		JOptionPane.showMessageDialog(main, meldung);
	}
}
