package szolancjatek;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*

Készítsünk egy főosztályt a gépi játékosoknak. A program első parancssori argumentuma a játékos neve, második pedig egy fájlnév. 
Ebben a szöveges fájlban található a játékos szókincse: soronként egy-egy szó. Csatlakozzon a gépi játékos a szerverhez és küldje el a nevét. 
A szervertől kapott első üzenetben vagy a kezdőjátékosnak szóló speciális start szó lesz, vagy pedig a szólánc első szava.
OK

Amennyiben a start üzenet érkezett, a gépi játékos válassza ki és küldje el a szókincse legelső szavát (a szavak sorrendje az a sorrend, ahogyan a fájlban szerepeltek).
OK

Ha már a szólánc első szava érkezett, akkor válassza ki és küldje el a szókincséből azt a legelső szót, ami a kapott szó utolsó betűjével kezdődik. 
A többi lépésben is ugyanígy küldjön válaszüzenetet. Fontos, hogy egy szót egy játékmenet során csak egyszer küldhet el, 
tehát a már elküldött szót érdemes eltávolítani a szókincsből! Minden elküldött szónál a standard outputra írja ki a következő szöveget: "<név>: <küldött_szó>"
OK

Ha a gépi játékosnak már nincs a leírt szabály szerint küldhető szava, küldje el az exit üzenetet a szervernek, majd fejezze be működését.
OK

Ha a gépi játékos még játékban van, és a nyerést jelző nyert üzenetet kapja, a standard outputra írja ki a következő szöveget: "<név> nyert", 
majd fejezze be a működését.
OK

 */
public class GepiJatekos {

    private static String name;
    private static final int PORT = 32123;
    private static List<String> words;
    private static final boolean DEBUG = true;
    private static PrintWriter pw;
    private static Scanner sc;

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.out.println("Nem megfelelo szamu parameter");
            return;
        }
        GepiJatekos.name = args[0];
        debug(name);

        words = readFile(args[1]);
        debug(words.toString());

        Socket s = new Socket("localhost", PORT);
        System.out.println("Socket init");
        pw = new PrintWriter(s.getOutputStream(), true);
        sc = new Scanner(s.getInputStream());
        
        pw.println(name);
        pw.flush();
        System.out.println("Nev elkuldve");
        while (true) {
            if (process() == 0) break;
        }
        //s.close();
    }

    public static int process() {
        int status = 1;
        String fromServer = sc.nextLine();
        debug("From server: " + fromServer);
        switch (fromServer) {
            case "start":
                sendMessage(0);
                break;
            case "nyert":
                System.out.println(name + " nyert");
                status = 0;
                break;
            case "looser":
                status = 0;
                break;
            default:
                int index = getFirstMatch(fromServer);
                debug("match " + index);
                if (index != -1) {
                    sendMessage(index);
                } else {
                    debug("No match, exiting...");
                    pw.println("exit");
                    pw.flush();
                    status = 0;
                }
                break;
        }
        return status;
    }

    public static int getFirstMatch(String input) {
        int index = -1;
        String last = input.substring(input.length() - 1);
        for (int i = 0; i < words.size(); i++) {
            String first = words.get(i).substring(0, 1);
            if (last.equals(first)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static void sendMessage(int index) {
        String m = words.get(index);
        debug(name + " " + m);
        pw.println(m);
        pw.flush();
        words.remove(index);
    }

    public static List<String> readFile(String filename) throws IOException {
        List<String> ls = new ArrayList<>();
        try (Reader reader = new FileReader(filename)) {
            BufferedReader br = new BufferedReader(reader);
            String line;
            while (br.ready()) {
                line = br.readLine();
                ls.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No such file " + filename);
        }
        return ls;
    }

    public static void debug(String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }

}
