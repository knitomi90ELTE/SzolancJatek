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

Amennyiben a start üzenet érkezett elsőként a szervertől, kérjen be egy tetszőleges szót a felhasználótól, amit továbbít a szerver felé.

Ha már a szólánc első szava érkezett, írja ki a kapott szót a felhasználónak, és kérje be a szólánc következő elemét. 
A kliensprogram ellenőrizze, hogy tényleg a kapott szó utolsó betűjével kezdődő szót gépelt-e be a felhasználó, ha nem, kérjen be egy újabb szót. 
Ha rendben van a begépelt szó, továbbítsa a szerver felé.

A felhasználó akármelyik lépésben begépelheti az "exit" szót, ekkor a kliensprogram ezt továbbítsa a szerver felé, majd fejeződjön be a kliens futása.

Ha valamelyik körben a szervertől a nyerést jelző nyert üzenet érkezett, akkor írja ki a felhasználónak, hogy ő nyert, majd fejeződjön be a kliens program futása.

A szólánc helyességén kívül a kliensprogram ellenőrizze minden lépésben azt is, hogy a küldendő String tényleg egyetlen szó-e (csak betűket tartalmaz), 
és nem küldtük már korábban ugyanezt a szót!

 */
public class InteraktivKliens {

    public static String name;
    public static int PORT = 32123;
    public static boolean debug = true;
    public static PrintWriter pw;
    public static Scanner serverOutput;
    public static Scanner userInput;
    public static List<String> words;

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

        public static int process() {
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
                pw.println(input);
                pw.flush();
                break;
        }
        return status;
    }
    
    public static void debug(String s) {
        if (debug) {
            System.out.println(s);
        }
    }

}
