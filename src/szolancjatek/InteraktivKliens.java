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

    private String name;
    private final int PORT = 32123;
    private final boolean debug = false;
    private PrintWriter pw;
    private Scanner serverOutput;
    private Scanner userInput;
    private List<String> words;

    public InteraktivKliens() {
        try {
            words = new ArrayList<>();
            //Socket s = new Socket("84.236.115.29", PORT);
            Socket s = new Socket("localhost", PORT);
            pw = new PrintWriter(s.getOutputStream(), true);
            serverOutput = new Scanner(s.getInputStream());
            userInput = new Scanner(System.in);
            System.out.println("USERCL-LOG: Adja meg a nevet");
            this.name = userInput.nextLine();
            pw.println(name);
        } catch (IOException ex) {
            //System.out.println("GepiJatekos init hiba");
        }

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (process() == 0) {
                        break;
                    }
                }
            }
        }.start();
    }

    private int process() {
        int status = 1;
        String fromServer = serverOutput.nextLine();
        System.out.println("USERCL-LOG: " + fromServer);
        switch (fromServer) {
            case "nyert":
                System.out.println("USERCL-LOG: " + name + " nyert");
                status = 0;
                break;
            case "looser":
                status = 0;
                break;
            default:
                debug("USERCL-LOG: Irjon be egy szot!");
                String input = userInput.nextLine();
                if (!input.equals("exit") && !fromServer.equals("start")) {
                    //TODO: exit validálása nem első inputként
                    while (isWrongInput(input, fromServer)) {
                        input = userInput.nextLine();
                        if("exit".equals(input)){
                            break;
                        }
                    }
                    words.add(input);
                }
                pw.println(input);
                debug("USERCL-LOG: Szo elkuldve, varakozas...");
                break;
        }
        return status;
    }

    private boolean isWrongInput(String input, String fromServer) {
        boolean b = false;
        if(input.length() == 0){
            System.out.println("ures szo");
            b = true;
        }
        if (words.contains(input)) {
            System.out.println("mar mondtad");
            b = true;
        }

        if (fromServer.charAt(fromServer.length() - 1) != input.charAt(0)) {
            System.out.println("nem stimmel a karakter");
            b = true;
        }

        if (!input.chars().allMatch(x -> Character.isLetter(x))) {
            System.out.println("nem csupa betu");
            b = true;
        }
        return b;
    }

    private void debug(String s) {
        if (debug) {
            System.out.println(s);
        }
    }

    public static void main(String[] args) {
        new InteraktivKliens();
    }

}
