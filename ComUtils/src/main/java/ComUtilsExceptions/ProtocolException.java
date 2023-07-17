package ComUtilsExceptions;


public class ProtocolException extends Exception {

    public static ErrorProtocol errorProtocol;

    /**
     * Consctructor que s'utilitza per a que s'obtingui un errorProtocol
     * @param i Nombre d'error protocol corresponent.
     */
    public ProtocolException(int i) {


        super(CreateStringProtocolException(i));

    }

    /**
     * Consctructor amb missatge directe.
     * @param errorMessage missatge d'error en questió.
     */
    public ProtocolException(String errorMessage) {

        super(errorMessage);

    }

    /**
     * Metode estatic que s'utilitza amb el constructor del ProtocolException per a passar-li el missatge d'error
     * de l'error que es crea.
     * @param i int que ens donara el tipus d'error segons l'objecte ErrorProtocol
     * @return el missatge d'error corresponent.
     */
    public static String CreateStringProtocolException(int i){
        errorProtocol = new ErrorProtocol(i);
        return errorProtocol.getErrorMessage();
    }
}