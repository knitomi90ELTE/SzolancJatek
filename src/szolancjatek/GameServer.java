package szolancjatek;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/*
A játékszervert a 32123 porthoz rendeljük. A szerver egy játékmenetet a következőképpen bonyolít le:

Várakozik két játékos csatlakozására, akik a csatlakozás után megküldik a szervernek nevüket.

A szerver létrehoz egy fájlt, amibe játékmenet során összeálló szóláncot fogja rögzíteni, a következő névvel: <jatekos1>_<jatekos2>_<idobelyeg>.txt

Amint a második játékos is csatlakozott, egy speciális start üzenettel jelzi az először csatlakozottnak, hogy ő a kezdőjátékos, 
tehát először neki kell egy tetszőleges szót mondania.

A szerver innentől kezdve mindig fogad egy egy szavas üzenetet az egyik játékostól, majd (ellenőrzés nélkül) továbbítja a másik játékos felé, 
aki válaszként elküldi a szólánc következő elemét, amit a szerver továbbít, stb.

A szerver rögzíti a játék során összeálló szóláncot a játékmenethez létrehozott fájlba. Egy sorban a beküldő játékos neve, 
majd attól szóközzel elválasztva az általa beküldött szó szerepeljen.

Ha valamelyik játékos az exit üzenetet küldi (ez a szóláncban tiltott szó lesz), vagy váratlanul lecsatlakozik, a játékmenet véget ér, és a másik játékos nyert. 
A nyertest a szerver a nyert üzenettel értesítse, majd mindkét játékossal bontsa a kapcsolatot.

A szervert készítsük fel több játékmenet egy időben történő kezelésére: tehát minden két, egymás után csatlakozott játékoshoz indítson el egy játékmenetet, 
majd azonnal legyen képes újabb két játékos fogadására. A szerver álljon le, ha 30 másodpercen keresztül nem csatlakozik egy játékos sem 
(tipp: ServerSocket osztály setSoTimout metódusa), és már nincsen folyamatban lévő játék.
*/
public class GameServer {
    
    public static final int PORT = 32123;
    public static int state = 0;
    
    public static void main(String[] args) throws IOException {
        
        ServerSocket ss = new ServerSocket(PORT);
        while (true) {
            try {

                Socket s = ss.accept();
                System.out.println("a client connected");

                Scanner sc = new Scanner(s.getInputStream());
                PrintWriter pw = new PrintWriter(s.getOutputStream(), true);

                while (true) {

                    int i = sc.nextInt();
                    if (i == 0) {
                        System.out.println("Szerver leall");
                        pw.println(0);
                        break;
                    } else {
                        System.out.println("input number: " + i);
                        state += i;
                        pw.println(state);

                    }
                }
                s.close();

            } catch (Exception e) {
                System.err.println("Hiba a klienssel valo kommunikacioban.");
            }

        }
        
    }
    
}
