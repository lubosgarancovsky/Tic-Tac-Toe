import java.util.ArrayList;

public class Hra {
    private Stvorec stvorec;
    private Manazer manazer;
    private Terminal terminal;
    private Bot bot;
    private Skore skore;
    private static Hra instancia;
    private static String hrac1;    //meno prveho hraca
    private static String hrac2;    //meno druheho hraca
    private boolean hrac1naRade; // striedanie hracov
    private boolean koniecHry;  //skoncilo sa kolo alebo sa este hra
    private int pocetTahov;
    private boolean zacalPrvy; //hrac c. 1 zacal kolo (true/false) kvoli striedaniu po kolach

    //zapisuje tahy v piskvorkach cislami do dvojrozmerneho pola, potrebne pre kontrolu vyhry
    private int[][] plocha = new int[3][3];

    //pole stvorcov v hracej ploche
    private Stvorec[][] stvorce = new Stvorec[3][3];

    //do arraylistov sa zapisuju vsetky kruhy a trojuholniky polozene do hracej plochy
    private ArrayList<Kruh> kruhy = new ArrayList();
    private ArrayList<Trojuholnik> trojuholniky = new ArrayList();

    //suradnice riadka a stlpca do ktoreho hrac klikol
    private int riadok;
    private int stlpec;

    /**
     * Konštruktor triedy hra.
     * Vykreslí hraciu plochu, nastaví premenné.
     */
    public Hra(String hrac1, String hrac2) {
        //Na cele platno nakresli cierny stvorec, vytvori tym pozadie
        Stvorec pozadie = new Stvorec();
        pozadie.posunVodorovne(-60);
        pozadie.posunZvisle(-50);
        pozadie.zmenStranu(300);
        pozadie.zmenFarbu("black");
        pozadie.zobraz();

        //inicializuje manazera, kvoli klikaniu mysou
        this.manazer = new Manazer();
        this.manazer.spravujObjekt(this);

        this.skore = new Skore();

        this.hrac1 = hrac1;
        this.hrac2 = hrac2;
        this.terminal = new Terminal(this.hrac1, this.hrac2);

        this.hrac1naRade = true; //hrac c. 1 vzdy zacina hru pri spusteni
        this.zacalPrvy = true;
        this.koniecHry = false;
        this.pocetTahov = 0;

        //dvojity cyklus pre vykreslovanie hracej plochy (stvorcova siet 3x3)
        for (int i = 0;i < 3;i++) {
            for (int j = 0;j < 3;j++) {
                Stvorec policko = new Stvorec();
                policko.zmenFarbu("white");
                policko.zmenStranu(95);
                policko.posunZvisle(i * 100 - 48);
                policko.posunVodorovne(j * 100 - 58);
                policko.zobraz();

                //prazdne stvorce su zapisane v poli plocha ako "0"
                this.plocha[i][j] = 0;
                this.stvorce[i][j] = policko;
            }
        }

        //ak sa niektory z hracov vola "Bot" vytvori sa instancia tejto triedy s prislusnou hodnotou pre pocitanie vyhry
        if (hrac1.equals("Bot")) {
            this.bot = new Bot(1);
            this.tahBota(true); // ak je bot prvy hrac, zacina hru a hned po vytvoreni instancie sa urobi jeho tah
        }
        if (hrac2.equals("Bot")) {
            this.bot = new Bot(4);
        }
    }

    /**
     * Metóda, umiestnuje znaky hracov na hraciu plochu
     * ak prvyHrac = true - vloží sa kruh
     * ak prvyHrac = false - vloží sa trojuholník
     */
    private void tahHraca(boolean prvyHrac) {
        if (prvyHrac) {
            this.umiestniKruh(this.riadok, this.stlpec);
            this.plocha[this.riadok][this.stlpec] = 1;
        } else {
            this.umiestniTrojuholnik(this.riadok, this.stlpec);
            this.plocha[this.riadok][this.stlpec] = 4;
        }
        //pripocita sa tah a kontroluje sa, ci na ploche nie je vitazna kombinacia alebo remiza
        this.pocetTahov++;
        this.kontrola();
    }

    /**
     * Metoda ovláda tahy počítača.
     * Do ktorého políčka chce bot vložiť svoj znak rozhodne pomocou metód v triede "Bot".
     */
    private void tahBota (boolean bot) {
        this.bot.zmenPrioritu(this.plocha);
        this.bot.nastavTah();

        //riadok a stlpec sa neriadia kliknutim hraca ale vypoctom bota
        this.riadok = this.bot.getRiadok();
        this.stlpec = this.bot.getStlpec();

        //ak bot = true, hra bot ako hrac c.1 ak false, hra ako hrac c.2
        if (bot) {
            this.umiestniKruh(this.riadok, this.stlpec);
            this.plocha[this.riadok][this.stlpec] = 1;
        }
        if (!bot) {
            this.umiestniTrojuholnik(this.riadok, this.stlpec);
            this.plocha[this.riadok][this.stlpec] = 4;
        }
        this.pocetTahov++;
        this.kontrola();
    }

    /**
     * Metóda slúži na rozoznávanie, či hrajú proti sebe dvaja hráči alebo hráč a bot
     * Následne sa podla toho volajú metódy, ktoré vkladajú do hracej plochy znaky
     */
    private void tah() {
        //prva podmienka zistuje, ci hrac klikol do prazdneho policka
        if (this.plocha[this.riadok][this.stlpec] == 0) {
            //rozoznava sa, ktory hrac je na rade a ci je to bot alebo nie
            if (this.hrac1naRade) {
                this.tahHraca(true); //tah prveho hraca (vklada kruh)
                if (this.hrac2.equals("Bot") && !this.koniecHry) {
                    this.tahBota(false);
                }
            } else {
                this.tahHraca(false); //tah druheho hraca (vklada trojuholnik)
                if (this.hrac1.equals("Bot") && !this.koniecHry) {
                    this.tahBota(true);
                }
            }
        }
    }

    /**
     * Metóda pomocou triedy "Manazer" po kliknutí myšou zistuje, do ktorého políčka hráč klikol a následne to zapíše do premenných.
     * Potom sa volajú metódy tah a resetuj hru, na základe toho, či hra už skončila alebo nie.
     */
    public void vyberSuradnice (int x, int y) {
        //vypocet suradnic kliknuteho policka (suradnice 140,248 je policko 1,2)
        this.stlpec = x / 100;
        this.riadok = y / 100;

        //ak hra este neskoncila a hrac klikol do policka, vola sa metoda tah, ak hra uz skoncila, kliknutim sa hra resetuje a moze sa hrat dalsie kolo
        if (!this.koniecHry) {
            this.tah();
        } else {
            this.resetujHru();
        }  
    }

    /**
     * Metóda vykresľuje modrý kruh na príslušné súradnice, podľa toho, kam hráč (alebo bot) klikol.
     * Kruh prislúcha hráčovi číslo 1
     */
    private void umiestniKruh (int riadok, int stlpec) {
        Kruh kruh = new Kruh();
        kruh.zmenPriemer(80);
        //posuva kruh do stredu stvorca
        kruh.posunVodorovne((this.stlpec * 100) - 10);
        kruh.posunZvisle((this.riadok * 100) - 50);
        //kruh je pridany do arraylistu a zobrazeny na hracej ploche
        this.kruhy.add(kruh);
        kruh.zobraz();

        //hrac cislo jedna sa v poli zapisuje cislom "1"
        this.plocha[this.riadok][this.stlpec] = 1;
        this.hrac1naRade = false;
    }

    /**
     * Metóda vykresľuje červený trojuholník na príslušné súradnice, podľa toho, kam hráč (alebo bot) klikol. 
     * Trojuholník prislúcha hráčovi číslo 2
     */
    private void umiestniTrojuholnik (int riadok, int stlpec) {
        Trojuholnik trojuholnik = new Trojuholnik();
        trojuholnik.zmenRozmery(80, 80);
        trojuholnik.zmenFarbu("red");
        trojuholnik.posunVodorovne(this.stlpec * 100);
        trojuholnik.posunZvisle((this.riadok * 100) - 5);
        this.trojuholniky.add(trojuholnik);
        trojuholnik.zobraz();

        //hrac cislo dva sa v poli zapisuje cislom "4"
        this.plocha[this.riadok][this.stlpec] = 4;
        this.hrac1naRade = true;
    }

    /**
     * Kontroluje každý stĺpec, riadok aj obe diagonály a zisťuje, či niektorý z hráčov nemá na hracej ploche víťaznú kombináciu, alebo či hra neskončila
     * remízou.
     * Následne výsledok posiela metóde "oznacVitaza".
     */
    private void kontrola () {
        int sucet;
        sucet = 0;

        //cyklus kontroluje sucet v kazdom z riadkov v poli, ak je sucet 3, vyhral hrac c.1, ak je sucet 12. vyhrava hrac c.2
        for (int i = 0; i < 3;i++) {
            for (int j = 0;j < 3;j++) {
                sucet += this.plocha[i][j];
                if (sucet == 3) {
                    this.koniecHry = true;
                    this.terminal.vypisVysledok(1); //posiela spravu triede Terminal o tom, ze vyhral hrac cislo 1
                    this.skore.pripocitajSkore(true); //v triede skore sa pripocita bod pre tohto hraca
                    this.oznacVitaza("riadok", i); //parameter "riadok" znamena ze kombinacia sa nachadza v riadku, i je cislo riadku

                    break;
                }
                if (sucet == 12) {
                    this.koniecHry = true;
                    this.terminal.vypisVysledok(2); //posiela spravu triede Terminal o tom, ze vyhral hrac cislo 2
                    this.skore.pripocitajSkore(false);
                    this.oznacVitaza("riadok", i);

                    break;
                }
            }
            sucet = 0;
        }

        //cyklus kontroluje sucet v kazdom zo stlpcov       
        for (int i = 0; i < 3;i++) {
            for (int j = 0;j < 3;j++) {
                sucet += this.plocha[j][i];
                if (sucet == 3) {
                    this.koniecHry = true;
                    this.terminal.vypisVysledok(1);
                    this.skore.pripocitajSkore(true);
                    this.oznacVitaza("stlpec", i);

                    break;
                }
                if (sucet == 12) {
                    this.koniecHry = true;
                    this.terminal.vypisVysledok(2);
                    this.skore.pripocitajSkore(false);
                    this.oznacVitaza("stlpec", i);

                    break;
                }
            }
            sucet = 0;
        }

        sucet = 0;
        //Kontrola hlavnej diagonaly, teda policka 0,0 1,1 a 2,2
        for (int i = 0;i < 3;i++) {
            sucet += this.plocha[i][i];
            if (sucet == 3) {
                this.koniecHry = true;
                this.terminal.vypisVysledok(1);
                this.skore.pripocitajSkore(true);
                this.oznacVitaza("diagonala", 1); //kombinacia sa nachazda na prvej diagonale (hlavnej)

                break;
            }
            if (sucet == 12) {
                this.koniecHry = true;
                this.terminal.vypisVysledok(2);
                this.skore.pripocitajSkore(false);
                this.oznacVitaza("diagonala", 1);

                break;
            }
        }

        //kontrola vedlajsej diagonaly, 0,2 1,1 2,0
        sucet = 0;
        int j = 2;
        for (int i = 0;i < 3;i++) {
            sucet += this.plocha[i][j];
            j--;
            if (sucet == 3) {
                this.koniecHry = true;
                this.terminal.vypisVysledok(1);
                this.skore.pripocitajSkore(true);
                this.oznacVitaza("diagonala", 2); //kombinacia sa nachazda na druhej diagonale

                break;
            }
            if (sucet == 12) {
                this.koniecHry = true;
                this.terminal.vypisVysledok(2);
                this.skore.pripocitajSkore(false);
                this.oznacVitaza("diagonala", 2);

                break;
            }
        }

        //Ak je pocet tahov 9 a nikto nevyhral, znamena to remizu.
        if (this.pocetTahov == 9 && !this.koniecHry) {
            this.koniecHry = true;
            this.terminal.vypisVysledok(3);
            this.oznacVitaza("remiza", 0);
        }
    }

    /**
     * Na základe výsledku hry sa prefarbujú políčka.
     * Políčka pod víťaznou kombináciou sa prefarbia na zeleno.
     * Pri remíze sa všetkých 9 políčok prefarbí na žlto.
     */
    private void oznacVitaza (String pozicia, int poradie) {
        //v terminali sa vypise skore, ktore obaja hraci zatial dosiahli
        this.terminal.vypisSkore(this.skore.getHrac1(), this.skore.getHrac2());
        //vsetky znakz na hracej ploche sa skryju, neskor sa zobrazia aby neostali pod stvorcami hracej plochy
        this.viditelnostZnakov(false);
        
        //meni farbu vitazneho riadka na zelenu
        if (pozicia.equals("riadok")) {
            for (int i = 0;i < 3;i++) {
                this.stvorce[poradie][i].zmenFarbu("green");
            }
        }

        //meni farbu vitazneho stlpca na zelenu
        if (pozicia.equals("stlpec")) {
            for (int i = 0;i < 3;i++) {
                this.stvorce[i][poradie].zmenFarbu("green");
            }
        }

        //meni farbu vitaznej diagonaly
        if (pozicia.equals("diagonala") && poradie == 1) {
            for (int i = 0;i < 3;i++) {
                this.stvorce[i][i].zmenFarbu("green");
            }
        }

        if (pozicia.equals("diagonala") && poradie == 2) {
            for (int i = 0;i < 3; i++) {
                this.stvorce[i][poradie - i].zmenFarbu("green");
            }
        }

        //pri remize sa vsetkych 9 policok prefarbi na zlto
        if (pozicia.equals("remiza")) {
            for (int i = 0;i < 3; i++) {
                for (int j = 0;j < 3;j++) {
                    this.stvorce[i][j].zmenFarbu("yellow");
                }
            }
        }

        //vsetky znaky sa znova zobrazia
        this.viditelnostZnakov(true);
    }

    /**
     * Táto metóda sa využíva pri označovaní víťaznej kombinácie.
     * Zobrazuje a skrýva znaky v hracej ploche, aby boli viditeľné pri prefarbení políčok pod nimi.
     * Používa na to foreach cykly.
     */
    private void viditelnostZnakov(boolean zobrazit) {
        if (zobrazit) {
            for (Kruh kruh : this.kruhy) {
                kruh.zobraz();
            }

            for (Trojuholnik trojuholnik : this.trojuholniky) {
                trojuholnik.zobraz();
            }
        } else {
            for (Kruh kruh : this.kruhy) {
                kruh.skry();
            }

            for (Trojuholnik trojuholnik : this.trojuholniky) {
                trojuholnik.skry();
            }
        }  
    }

    /**
     * Vytvára inštanciu tejto triedy ako jedináčika (singleton), teda je možné vytvoriť iba jednu inštanciu.
     * Tak isto volá metódu Vitaj z triedy Terminal, ktorá v termínály vypisuje údaje o hre.
     * Vyžaduje dva vstupné premenné typu String.
     * Prvý hráč má modré kruhy a začína ako prvý, druhý hráč má červené trojuholníky.
     * Ak je meno jedného z hráčov "Bot". Hráč bude hrať proti počítaču.
     */
    public static Hra spusti (String hrac1, String hrac2) {
        if (Hra.instancia == null) {
            Hra.instancia = new Hra(hrac1, hrac2);
            Terminal.vitaj();
        }   
        return Hra.instancia;
    }

    /**
     * Metóda sa volá po kliknutí do hracej plochy ak hra skončila a umožnuje hráčom hrať ďalšie kolo.
     * Vyčistí hraciu plochu.
     */
    private void resetujHru() {
        //skrývajú sa znaky na ploche a vymazujú sa arraylisty s týmito znakmi
        this.viditelnostZnakov(false);
        this.trojuholniky.clear();
        this.kruhy.clear();

        //vsetky stvorce sa znova zafarbia na bielo, pred tym na nich bol vyznaceny vysledok kola
        for (int i = 0;i < 3; i++) {
            for (int j = 0;j < 3;j++) {
                this.stvorce[i][j].zmenFarbu("white");
                this.plocha[i][j] = 0;
            }
        }
        
        //vynuluje sa pocet tahov aby sa mohol znova kontrolovat pre pripad remizy
        this.pocetTahov = 0;
        this.koniecHry = false;

        this.bot.nastavBota();
        
        //vystriedaju sa hraci
        //hrac ktory kolo zacinal, pojde teraz ako druhy a naopak, to iste plati pre hru s botom
        if (this.zacalPrvy) {
            this.hrac1naRade = false;
            this.zacalPrvy = false;
            
            //ak je na hrac c.2 bot a je na rade, urobi svoj tah
            if (this.hrac2.equals("Bot")) {
                this.tahBota(false);
            }
        } else {
            this.hrac1naRade = true;
            this.zacalPrvy = true;
            
            //ak je na hrac c.1 bot a je na rade, urobi svoj tah
            if (this.hrac1.equals("Bot")) {
                this.tahBota(true);
            }
        }
    }

    /**
     * Ukončí piškvorky po stlačení klávesu ESC.
     */
    public void zrus() {
        System.exit(0);
    }
}
