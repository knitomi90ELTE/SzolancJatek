package szolancjatek;

import java.io.IOException;

/*
Játékszimuláció
Készíts egy SzolancSzimulacio nevű főosztályt, amely elindít egy játékszervert és négy játékost. A játékosok neve legyen "Jatekos1", "Jatekos2", stb. 
Az első három játékos szókincse legyen a szokincs1.txt fájl, míg a negyediké a szokincs2.txt. A szókincseket tartalmazó fájlok 
és a minta kimeneti fájlok letölthetők innen.
 */
public class SzolancSzimulacio {

    public static void main(String[] args) {
        try {
            GameServer.main(null);
            GepiJatekos.main(new String[]{"Jatekos1", "szokincs1.txt"});
            GepiJatekos.main(new String[]{"Jatekos2", "szokincs1.txt"});
            GepiJatekos.main(new String[]{"Jatekos3", "szokincs1.txt"});
            GepiJatekos.main(new String[]{"Jatekos4", "szokincs2.txt"});
        } catch (IOException e) {
            System.out.println("Hiba az inicializalas soran");
        }

    }
}
