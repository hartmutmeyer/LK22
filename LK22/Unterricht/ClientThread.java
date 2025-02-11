import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Random;

public class ClientThread extends Thread {

	private Socket s;
	private InputStreamReader inNet;
	private OutputStreamWriter outNet;
	private String name = "";
	private Random zufall = new Random();
	private int zeichen;
	private char c;
	private int a;
	private int b;
	private int loesung;
	private String aufgabe = "";
	private long anfangszeit;

	public ClientThread(Socket s) {
		this.s = s;
	}

	@Override
	public void run() {
		try {
			inNet = new InputStreamReader(s.getInputStream(), "UTF-8");
			outNet = new OutputStreamWriter(s.getOutputStream(), "UTF-8");

			// Name vom Client einlesen und abspeichern
			while ((zeichen = inNet.read()) != '$') {
				c = (char) zeichen;
				name += c;
			}

			// Zeitname starten
			anfangszeit = System.currentTimeMillis();

			// Nacheinander 5 Aufgaben erstellen und an den Client senden
			// und Antworten auswerten
			for (int i = 0; i < 5; i++) {
				aufgabeErstellen();
				aufgabeSenden();
				loesungBewerten();
			}

			// Leistung bewerten und Verbindung beenden
			leistungBewerten();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void leistungBewerten() throws IOException {
		// Anmerkung: Die Methode leistungBewerten() ist zu groß - und damit
		//            unübersichtlich. Besser (übersichtlicher) wäre es den
		//            Inhalt dieser Methode in mehrere kleiner funktionale
		//            Einheiten auszulagern. Also beispielsweise eine Methode
		//            altenHighscoreAuslesen() zu erstellen und diese dann in
		//            leistungBewerten() aufzurufen.
		long endzeit = System.currentTimeMillis();
		double zeit = endzeit - anfangszeit;
		zeit = zeit / 1000; // Umrechnung Millisekunden in Sekunden
		// Alten Highscore auslesen
		URL url = getClass().getResource("highscore.txt"); // import java.net.URL
		InputStream fileIn = new FileInputStream(URLDecoder.decode(url.getFile(), "UTF-8"));
		InputStreamReader inFile = new InputStreamReader(fileIn, "UTF-8");
		String eingelesen = "";
		String highscoreZeit = "";
		String highscoreInhaber = "";
		while ((zeichen = inFile.read()) != '$') {
			c = (char) zeichen;
			highscoreZeit += c;
		}
		double bestzeit = Double.parseDouble(highscoreZeit);
		while ((zeichen = inFile.read()) != -1) {
			c = (char) zeichen;
			highscoreInhaber += c;
		}
		System.out.println("Bestzeit: " + bestzeit);
		System.out.println("von: " + highscoreInhaber);
		if (zeit < bestzeit) {
			outNet.write("%Gratuliere " + name + "! Du hast nur " + zeit + " Sekunden gebraucht."
					+ System.lineSeparator() + "Das ist die neue Bestzeit!$");
			outNet.flush();
			OutputStream fileOut = new FileOutputStream(URLDecoder.decode(url.getFile(), "UTF-8"));
			OutputStreamWriter outFile = new OutputStreamWriter(fileOut, "UTF-8");
			outFile.write("" + zeit + '$' + name);
			outFile.flush();
		} else {
			outNet.write("%Du hast die Aufgaben in " + zeit + " Sekunden gelöst." + System.lineSeparator()
					+ "Die aktuelle Bestzeit von " + highscoreInhaber + " beträgt" + System.lineSeparator() + bestzeit
					+ " Sekunden.$");
			outNet.flush();
		}
	}

	private void aufgabeErstellen() {
		a = zufall.nextInt(8) + 2;
		b = zufall.nextInt(90) + 10;
		loesung = a * b;
	}

	private void aufgabeSenden() throws IOException {
		aufgabe = "?" + a + " * " + b + "$";
		System.out.println("neueAufgabe: " + aufgabe);
		outNet.write(aufgabe);
		outNet.flush();
	}

	private void loesungBewerten() throws IOException {
		String empfangeneLoesung = "";
		while (!empfangeneLoesung.equals("" + loesung)) {
			empfangeneLoesung = "";
			while ((zeichen = inNet.read()) != '$') {
				c = (char) zeichen;
				empfangeneLoesung += c;
			}
			System.out.println("Lösung vom Client: " + empfangeneLoesung);
			if (empfangeneLoesung.equals("" + loesung)) {
				System.out.println("RICHTIG");
			} else {
				System.out.println("FALSCH");
				outNet.write("%Falsche Antwort. Probier es noch einmal.$");
				outNet.flush();
			}
		}
	}

}
