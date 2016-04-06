package szolancjatek;

/*
Főprogram egy gépi és egy interaktív klienssel
Készíts egy SzolancJatek nevű főosztályt, amely lehetővé teszi a gépi játékos ellen való játékot: elindít egy szervert; 
egy "Robot" nevű, szokincs1.txt szókinccsel rendelkező gépi játékost; és egy interaktív klienst. A játék addig megy, amíg a robot már nem tud megfelelő szót küldeni, 
vagy a felhasználó feladja a játékot.

 */
public class SzolancJatek {

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

        synchronized (SzolancJatek.class) {
            new Thread() {
                @Override
                public void run() {
                    GepiJatekos robot = new GepiJatekos("Robot", "szokincs1.txt");
                }
            }.start();

            new Thread() {
                @Override
                public void run() {
                    InteraktivKliens client = new InteraktivKliens();
                }
            }.start();
        }

    }

}
