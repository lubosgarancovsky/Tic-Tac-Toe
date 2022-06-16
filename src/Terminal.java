/**
 * Trieda slúži iba na vypisovanie do terminálu.
 */
public class Terminal {
    //mena hracov
    private static String hrac1;
    private static String hrac2;
    private int kolo; //kolo, ktore sa prave ukoncilo
    private Skore skore;
    
    /**
     * Konštruktor triedy. Pri vytvorení inštancie sa získavajú mená hráčov.
     */
    public Terminal (String hrac1, String hrac2) {
        this.hrac1 = hrac1;
        this.hrac2 = hrac2;
        this.kolo = 1; // vypisuje sa po ukonceni prveho kola, preto 1
    }
    
    /**
     * Uvítacia správa, ktorá sa vypíše po vytvorení inštancie triedy Hra.
     * Obsahuje mená hráčov a inštrukciu k ukončeniu hry.
     */
    public static void vitaj() {
        System.out.println("Vitajte v hre piskvorky");
        System.out.println("1. ○ " + hrac1);
        System.out.println("2. ▲ " + hrac2);
        System.out.println("Pre odchod z hry stlacte klaves ESC");
        System.out.println("------------------------------------");
    }
    
    /**
     * Vypisuje víťaza kola do terminálu spolu s počtom odohratých kôl.
     */
    public void vypisVysledok (int vysledok) {
        System.out.println();
        System.out.println("------------------------------------");
        if (vysledok == 1) {
            System.out.println(this.kolo + ". kolo: " + "Vyhrava ► " + this.hrac1);
        }
        if (vysledok == 2) {
            System.out.println(this.kolo + ". kolo: " + "Vyhrava ► " + this.hrac2);
        }
        if (vysledok == 3) {
            System.out.println(this.kolo + ". kolo: " + "Remiza!");
        }
        this.kolo++; // zvyšuje pocet kôl
    }
    
    /**
     * Vypisuje do terminálu bodový stav hráčov.
     */
    public void vypisSkore (int hrac1Body, int hrac2Body) {
        System.out.println("► " + this.hrac1 + ": " + hrac1Body);
        System.out.println("► " + this.hrac2 + ": " + hrac2Body);
        System.out.print("Pokracuj kliknutim. Na rade je: » ");
        // podmienka naviguje hracov, ktory nasleduje ako prvy v dalsom kole na zaklade striedania parneho a neparneho kola
        // 1.kolo zacina prvy hrac, 2. kolo zacina druhy hrac, 3. kolo znova prvy hrac ...
        if (this.kolo % 2 != 0) {
            System.out.println(this.hrac1);
        } else {
            System.out.println(this.hrac2);
        }
        System.out.println("------------------------------------");
    }
}
