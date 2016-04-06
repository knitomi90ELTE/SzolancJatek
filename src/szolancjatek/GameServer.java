package szolancjatek;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;

/*
A játékszervert a 32123 porthoz rendeljük. A szerver egy játékmenetet a következőképpen bonyolít le:
OK

Várakozik két játékos csatlakozására, akik a csatlakozás után megküldik a szervernek nevüket.
OK

A szerver létrehoz egy fájlt, amibe játékmenet során összeálló szóláncot fogja rögzíteni, a következő névvel: <jatekos1>_<jatekos2>_<idobelyeg>.txt
OK

Amint a második játékos is csatlakozott, egy speciális start üzenettel jelzi az először csatlakozottnak, hogy ő a kezdőjátékos, 
tehát először neki kell egy tetszőleges szót mondania.
OK

A szerver innentől kezdve mindig fogad egy egy szavas üzenetet az egyik játékostól, majd (ellenőrzés nélkül) továbbítja a másik játékos felé, 
aki válaszként elküldi a szólánc következő elemét, amit a szerver továbbít, stb.
OK

A szerver rögzíti a játék során összeálló szóláncot a játékmenethez létrehozott fájlba. Egy sorban a beküldő játékos neve, 
majd attól szóközzel elválasztva az általa beküldött szó szerepeljen.
OK

Ha valamelyik játékos az exit üzenetet küldi (ez a szóláncban tiltott szó lesz), vagy váratlanul lecsatlakozik, a játékmenet véget ér, és a másik játékos nyert. 
A nyertest a szerver a nyert üzenettel értesítse, majd mindkét játékossal bontsa a kapcsolatot.
OK

A szervert készítsük fel több játékmenet egy időben történő kezelésére: tehát minden két, egymás után csatlakozott játékoshoz indítson el egy játékmenetet, 
majd azonnal legyen képes újabb két játékos fogadására. A szerver álljon le, ha 30 másodpercen keresztül nem csatlakozik egy játékos sem 
(tipp: ServerSocket osztály setSoTimout metódusa), és már nincsen folyamatban lévő játék.
OK
 */
public class GameServer {

    public static final int PORT = 32123;
    public static int state = 0;
    public static final int TIMEOUT = 30000;

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(PORT);
        System.out.println("Server listening on port " + PORT);
        ss.setSoTimeout(TIMEOUT);
        try {
            while (true) {
                Socket s1 = ss.accept();
                Socket s2 = ss.accept();
                new Handler(s1, s2).start();
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Szerver leall, nem tortent kapcsolatfelveltel " + TIMEOUT / 1000 + " masodpercig");
            ss.close();
        }
    }

    private static class Handler extends Thread {

        private final Player player1;
        private final Player player2;
        private final File logFile;
        private final Writer logWriter;

        public Handler(Socket s1, Socket s2) throws IOException {
            this.player1 = new Player(s1);
            this.player2 = new Player(s2);
            String s = player1.name + "_" + player2.name + "_" + getTimeStamp() + ".txt";
            System.out.println(getTimeStamp());
            this.logFile = new File(s);
            logFile.createNewFile();
            logWriter = new FileWriter(logFile);
            System.out.println("Handler created");
        }

        private String getTimeStamp() {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h-mm-ss");
            String formattedDate = sdf.format(date);
            return formattedDate;
        }

        private void logToFile(String log) throws IOException {
            BufferedWriter bw = new BufferedWriter(logWriter);
            bw.write(log);
            bw.newLine();
            bw.flush();
        }

        @Override
        public void run() {
            Player playerOnTurn = player1;
            try {
                playerOnTurn.sendMessage("start");
                while (true) {
                    String s = playerOnTurn.getMessage();
                    synchronized (GameServer.class) {
                        System.out.println(playerOnTurn.name + " kuldte: " + s);
                        if (s.equals("exit")) {
                            playerOnTurn.sendMessage("looser");
                            playerOnTurn = (playerOnTurn.equals(player1)) ? player2 : player1;
                            playerOnTurn.sendMessage("nyert");                            
                            break;
                        }
                        logToFile(playerOnTurn.name + " " + s);
                        playerOnTurn = (playerOnTurn.equals(player1)) ? player2 : player1;
                        playerOnTurn.sendMessage(s);
                        System.out.println("A kuldott ertek: " + s);
                    }
                }
                player1.closeConnection();
                player2.closeConnection();
            } catch (Exception e) {
                System.err.println("Hiba a klienessel valo kommunikacioban.");
            }
        }

    }

    private static class Player {

        private final Socket socket;
        private final String name;
        private final PrintWriter pw;
        private final Scanner sc;

        public Player(Socket socket) throws IOException {
            this.socket = socket;
            pw = new PrintWriter(socket.getOutputStream(), true);
            sc = new Scanner(socket.getInputStream());
            this.name = sc.nextLine();
            System.out.println("Player created");
        }

        public void sendMessage(String s) {
            pw.println(s);
        }

        public String getMessage() {
            return sc.nextLine();
        }

        public void closeConnection() throws IOException {
            socket.close();
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 67 * hash + Objects.hashCode(this.socket);
            hash = 67 * hash + Objects.hashCode(this.name);
            hash = 67 * hash + Objects.hashCode(this.pw);
            hash = 67 * hash + Objects.hashCode(this.sc);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Player other = (Player) obj;
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            if (!Objects.equals(this.socket, other.socket)) {
                return false;
            }
            if (!Objects.equals(this.pw, other.pw)) {
                return false;
            }
            if (!Objects.equals(this.sc, other.sc)) {
                return false;
            }
            return true;
        }

    }

}
