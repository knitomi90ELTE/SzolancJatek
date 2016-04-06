package szolancjatek;

/*
Játékszimuláció
Készíts egy SzolancSzimulacio nevű főosztályt, amely elindít egy játékszervert és négy játékost. A játékosok neve legyen "Jatekos1", "Jatekos2", stb. 
Az első három játékos szókincse legyen a szokincs1.txt fájl, míg a negyediké a szokincs2.txt. A szókincseket tartalmazó fájlok 
és a minta kimeneti fájlok letölthetők innen.
 */
public class SzolancSzimulacio {

    public static void main(String[] args) {

        new Thread() {
            @Override
            public void run() {
                GameServer server = new GameServer(32123);
                if (server != null) {
                    server.handleClients();
                }
            }
        }.start();
        synchronized (GameServer.class) {
            new Thread() {
                @Override
                public void run() {
                    GepiJatekos jatekos1 = new GepiJatekos("Jatekos1", "szokincs1.txt");
                }
            }.start();

            new Thread() {
                @Override
                public void run() {
                    GepiJatekos jatekos2 = new GepiJatekos("Jatekos2", "szokincs1.txt");
                }
            }.start();
            
            new Thread() {
                @Override
                public void run() {
                    GepiJatekos jatekos3 = new GepiJatekos("Jatekos3", "szokincs1.txt");
                }
            }.start();

            new Thread() {
                @Override
                public void run() {
                    GepiJatekos jatekos4 = new GepiJatekos("Jatekos4", "szokincs2.txt");
                }
            }.start();
        }

    }
}
