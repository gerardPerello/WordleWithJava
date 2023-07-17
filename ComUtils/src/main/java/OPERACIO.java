/**
 * Enum OPERACION que retorna dona un valor enter a cada un dels valors de l'enum.
 */
public enum OPERACIO {
    HELLO(1), READY(2), PLAY(3), ADMIT(4), WORD(5), RESULT(6), STATS(7), ERROR(8);

    private int opNum;

    /**
     * Getter del opNum
     * @return
     */
    public int getOpNum(){
        return opNum;
    }

    /**
     * Consctructor de l'enum
     * @param opNum nombre corresponent que determinara quin tipus d'enum és.
     */
    OPERACIO(int opNum) {
        this.opNum = opNum;
    }
}