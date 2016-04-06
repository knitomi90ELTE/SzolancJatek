package szolancjatek;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
Interaktív kliens
Készíts egy konzolos klienst! A kliens csatlakozzon a játékszervehez, majd kérjen be a felhasználótól egy játékos nevet, amit elküld a szervernek.
OK

Amennyiben a start üzenet érkezett elsőként a szervertől, kérjen be egy tetszőleges szót a felhasználótól, amit továbbít a szerver felé.
OK

Ha már a szólánc első szava érkezett, írja ki a kapott szót a felhasználónak, és kérje be a szólánc következő elemét. 
OK

A kliensprogram ellenőrizze, hogy tényleg a kapott szó utolsó betűjével kezdődő szót gépelt-e be a felhasználó, ha nem, kérjen be egy újabb szót. 
Ha rendben van a begépelt szó, továbbítsa a szerver felé.

A felhasználó akármelyik lépésben begépelheti az "exit" szót, ekkor a kliensprogram ezt továbbítsa a szerver felé, majd fejeződjön be a kliens futása.

Ha valamelyik körben a szervertől a nyerést jelző nyert üzenet érkezett, akkor írja ki a felhasználónak, hogy ő nyert, majd fejeződjön be a kliens program futása.

A szólánc helyességén kívül a kliensprogram ellenőrizze minden lépésben azt is, hogy a küldendő String tényleg egyetlen szó-e (csak betűket tartalmaz), 
és nem küldtük már korábban ugyanezt a szót!

 */
public class InteraktivKliens {

    private static String name;
    private static final int PORT = 32123;
    private static final boolean debug = true;
    private static PrintWriter pw;
    private static Scanner serverOutput;
    private static Scanner userInput;
    private static List<String> words;

    public static void main(String[] args) throws IOException {

        words = new ArrayList<>();
        Socket s = new Socket("localhost", PORT);
        System.out.println("InteraktivKliens init");

        pw = new PrintWriter(s.getOutputStream(), true);
        serverOutput = new Scanner(s.getInputStream());
        userInput = new Scanner(System.in);

        System.out.println("Adja meg a nevet");
        InteraktivKliens.name = userInput.nextLine();

        System.out.println("Nev beolvasva " + InteraktivKliens.name);
        pw.println(name);
        pw.flush();

        System.out.println("Nev elkuldve");
        while (true) {
            if (process() == 0) {
                break;
            }
        }
        //s.close();
    }

    private static int process() {
        int status = 1;
        String fromServer = serverOutput.nextLine();
        debug("From server: " + fromServer);
        switch (fromServer) {
            case "nyert":
                System.out.println(name + " nyert");
                status = 0;
                break;
            case "looser":
                status = 0;
                break;
            default:
                String input = userInput.nextLine();
                if (!input.equals("exit")) {
                    while (validInput(input, fromServer)) {
                        input = userInput.nextLine();
                    }
                    words.add(input);
                }
                pw.println(input);
                pw.flush();
                break;
        }
        return status;
    }

    private static boolean validInput(String input, String fromServer) {
        return words.contains(input) || fromServer.charAt(fromServer.length() - 1) != input.charAt(0);
    }

    private static void debug(String s) {
        if (debug) {
            System.out.println(s);
        }
    }

}
