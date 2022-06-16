public class Skore {
    private int hrac1Body;
    private int hrac2Body;
    /**
     * Konštruktor
     */
    public Skore() {
        this.hrac1Body = 0;
        this.hrac2Body = 0;
    }

    /**
     * Pripočítava bod hráčovy, ktorý vyhral kolo.
     */
    public void pripocitajSkore(boolean hrac1) {
        // ak hrac1 je true, pripise sa bod prvemu hracovi, ak false, pripise sa druhemu
        if (hrac1) {
            this.hrac1Body++;
        } else {
            this.hrac2Body++;
        } 
    }

    /**
     * Getter pre body prvého hráča.
     */
    public int getHrac1() {
        return this.hrac1Body;
    }

    /**
     * Getter pre body druhého hráča.
     */
    public int getHrac2() {
        return this.hrac2Body;
    }
}
