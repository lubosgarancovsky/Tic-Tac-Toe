import java.util.Random;
public class Bot {
    private int[][] priorita = new int[3][3]; // pole, v ktorom bot vypocitava do ktoreho policka chce vlozit svoj znak
    private int[][] plocha = new int[3][3]; // pole pre hodnoty policok v ploche
    private int cisloVpoli; //cislo bota v poli "1" alebo "4"
    private int cisloProtivnika; //ak bot ma cislo "1" tak protivnik bude mat cislo "4" a naopak
    
    //riadok a stlpec v ktorom sa vybrane policko na hracej ploche nachadza
    private int riadok; 
    private int stlpec;

    //prvy tah bota sa nahodne generuje
    private Random prvyTah = new Random();

    /**
     * Konštruktor triedy.
     */
    public Bot(int cisloVpoli) {
        this.priorita = priorita;
        this.cisloVpoli = cisloVpoli;
        this.cisloProtivnika = cisloProtivnika;
        
        //ak bot ma cislo 1, hrac ma 4 a naopak
        if (this.cisloVpoli == 1) {
            this.cisloProtivnika = 4;
        }
        if (this.cisloVpoli == 4) {
            this.cisloProtivnika = 1;
        }

        this.nastavBota(); // pri vytvoreni instancie sa vypocita priorita prazdnej hracej plochy
    }

    /**
     * Nastavuje hodnoty políčok na hracej ploche, podľa ktorých bot robí svoje ťahy.
     * Metóda sa volá na začiatku každého kola.
     */
    public void nastavBota() {
        //kazde policko ma prioritu 1
        for (int i = 0;i < 3;i++) {
            for (int j = 0;j < 3;j++) {
                this.priorita[i][j] = 1;
            } 
        }

        //Do rohov hracej plochy sa nastavuje vyssia priorita
        this.priorita[0][0] = 2;
        this.priorita[0][2] = 2;
        this.priorita[2][0] = 2;
        this.priorita[2][2] = 2;

        //nahodne sa vygeneruje polick pre prvy tah bota
        this.priorita[this.prvyTah.nextInt(3)][this.prvyTah.nextInt(3)] = 3;
    }

    /**
     * Po každom ťahu metóda zisťuje stav hracej plochy a upravuje prioritu políčok na nej.
     * Ak bot môže v ďalšom kole vyhrať, na poličko, ktoré potrebuje obsadiť sa nastaví najvyšsia priorita, teda 4.
     * Ak bot musí brániť aby neprehral, na políčko sa nastavuje hodnota 3.
     * Obsadené políčka sú označené nulou.
     * Ostatné majú prioritu 1 alebo 2, ktoré sa už nastavili v metóde "nastavBota".
     */
    public void zmenPrioritu(int[][] plocha) {
        this.plocha = plocha; // stav plochy prevzaný z triedy Hra
        int sucet = 0; //sucet hodnot v kontrolovanom, riadku, stlpci alebo diagonale

        //nastavuje prioritu v riadkoch
        for (int i = 0;i < 3;i++) {
            for (int j = 0;j < 3;j++) {
                sucet += plocha[i][j];
                //ak uz ma v riadku dva svoje znaky, chce vyhrat a nastavi najvyssiu prioritu na tento riadok
                if (sucet == (2 * this.cisloVpoli)) {
                    this.priorita[i][0] = 4;
                    this.priorita[i][1] = 4;
                    this.priorita[i][2] = 4;
                }
                
                //ak ma protivnik v riadku dva svoje znaky, bot sa bude branit a zvisi prioritu tohto radka
                if (sucet == (2 * this.cisloProtivnika)) {
                    this.priorita[i][0] = 3;
                    this.priorita[i][1] = 3;
                    this.priorita[i][2] = 3;
                }
            }
            sucet = 0;
        }

        //to iste sa opakuje pre stlpce prioritu v stlpcoch
        for (int i = 0;i < 3;i++) {
            for (int j = 0;j < 3;j++) {
                sucet += plocha[j][i];
                if (sucet == (2 * this.cisloVpoli)) {
                    this.priorita[0][i] = 4;
                    this.priorita[1][i] = 4;
                    this.priorita[2][i] = 4;
                }
                if (sucet == (2 * this.cisloProtivnika)) {
                    this.priorita[0][i] = 3;
                    this.priorita[1][i] = 3;
                    this.priorita[2][i] = 3;
                }
            }
            sucet = 0;
        }

        //hlavna diagonala
        for (int i = 0;i < 3;i++) {
            sucet += plocha[i][i];
            if (sucet == (2 * this.cisloVpoli)) {
                this.priorita[0][0] = 4;
                this.priorita[1][1] = 4;
                this.priorita[2][2] = 4;
            }
            if (sucet == (2 * this.cisloProtivnika)) {
                this.priorita[0][0] = 3;
                this.priorita[1][1] = 3;
                this.priorita[2][2] = 3;
            }
        }

        //vedlajsia diagonala
        sucet = 0;
        int k = 2;
        for (int i = 0;i < 3;i++) {
            sucet += plocha[i][k];
            k--;
            if (sucet == (2 * this.cisloVpoli)) {
                this.priorita[0][2] = 4;
                this.priorita[1][1] = 4;
                this.priorita[2][0] = 4;
            }
            if (sucet == (2 * this.cisloProtivnika)) {
                this.priorita[0][2] = 3;
                this.priorita[1][1] = 3;
                this.priorita[2][0] = 3;
            }
        }

        //vynuluje prioritu na zabranych poliach, tam kde uz znak je sa nastavi priorita 0
        //ak zvysil prioritu na celom prvom riadku a dve policka su uz zabrane bude mat riadok prioritu napr. 0,4,0
        for (int i = 0;i < 3;i++) {
            for (int j = 0;j < 3;j++) {
                if (this.plocha[i][j] == 1 || this.plocha[i][j] == 4) {
                    this.priorita[i][j] = 0;
                }
            }
        }
    }

    /**
     * Pomocou dvojitého cyklu zisťuje, ktoré políčko má najvyšsiu prioritu.
     * Súradnice tohto políčka si uloží a v triede Hra sa na toto políčko ukladá znak, ktorý botovi prislúcha.
     */
    public void nastavTah() {
        int maxPriorita = 0; //najvyssia priorita aku bot v poli nasiel
        
        //cyklus prehladava pole s prioritami policok
        for (int i = 0;i < 3;i++) {
            for (int j = 0;j < 3;j++) {
                //vzdy ked najde bot vyssiu prioritu ako naposledy, zapise si ju do premennej
                if (this.priorita[i][j] > maxPriorita) {
                    maxPriorita = this.priorita[i][j]; 
                    //ak uz nenasiel vacsie cislo, zapise si suradnice policka s najvyssou prioritou
                    this.riadok = i;
                    this.stlpec = j;
                }
            }
        }
    }

    /**
     * Getter pre súradnicu y políčka s najvyššou prioritou.
     */
    public int getRiadok() {
        return this.riadok;
    }

    /**
     * Getter pre súradnicu x políčka s najvyššou prioritou.
     */
    public int getStlpec() {
        return this.stlpec;
    }
}
