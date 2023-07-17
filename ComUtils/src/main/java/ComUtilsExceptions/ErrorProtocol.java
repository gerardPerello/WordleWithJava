package ComUtilsExceptions;


public class ErrorProtocol{



    public int errorCode;
    private String errorMessage;
    public ErrorProtocol(int i) {
        CreateMessageErrorProtocolException(i);
    }

    /**
     * Metode que crea l'String del missatge segons el codi d'ERROR.
     * @param i
     * @return
     */
    private String CreateMessageErrorProtocolException(int i){
        errorCode = i;
        switch (i){
            case 1:
                errorMessage = "CARACTER NO RECONEGUT";
                break;
            case 2:
                errorMessage = "MISSATGE DESCONEGUT";
                break;
            case 3:
                errorMessage = "MISSATGE FORA DE PROTOCOL";
                break;
            case 4:
                errorMessage = "INICI DE SESSIO INCORRECTE";
                break;
            case 5:
                errorMessage = "PARAULA DESCONEGUDA";
                break;
            case 6:
                errorMessage = "MISSATGE MAL FORMAT";
                break;
            case 99:
                errorMessage = "ERROR DESCONEGUT";
                break;
            default:
                errorCode = 99;
                errorMessage = "ERROR DESCONEGUT";
        }
        return errorMessage;
    }

    /**
     * Getter de l'error code.
     * @return el codi d'error de l'error actual.
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Setter de l'error code.
     * @param errorCode el codi d'error.
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return El missatge d'error corresponent.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage El missatge d'error corresponent.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
