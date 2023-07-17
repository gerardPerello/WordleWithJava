package ProtocolServer;

import ComUtilsExceptions.ComUtilsException;
import ComUtilsExceptions.ProtocolException;
import utils.ComUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ProtocolServer {

    private ComUtils comUtils;


    /**
     * CONSTRUCTOR
     * @param inputStream
     * @param outputStream
     * @throws IOException
     */
    public ProtocolServer(InputStream inputStream, OutputStream outputStream) throws IOException {
        comUtils = new ComUtils(inputStream, outputStream);
    }

    /**
     *
     * @return Retorna el número d'operació que llegirà el servidor.
     * @throws IOException
     * @throws ProtocolException Excepció llançada en cas que es rebi una operació que el servidor no ha de llegir.
     */
    public int read_op_server() throws IOException, ProtocolException {
        int op = comUtils.read_byte();
        if(op != 1 && op != 3 && op != 5 && op != 8){
            throw new ProtocolException(2);
        }
        return op;
    }

    /**
     * PROTOCOL HELLO
     * @return retorna una llista d'objectes amb:
     *      [0] ==> (int) ID de sessió del client.
     *      [1] ==> (String) Nom del client.
     * @throws IOException
     * @throws ComUtilsException
     */
    public List<Object> read_hello() throws IOException, ComUtilsException {
        List<Object> message = new ArrayList<Object>();
        int sessionId = comUtils.read_int32();
        String name = comUtils.read_string();
        message.add(sessionId);
        message.add(name);
        return message;
    }

    /**
     * PROTOCOL PLAY
     * @return (int) ID de Sessió del client.
     * @throws IOException
     */
    public int read_play() throws IOException{
        int sessionId = comUtils.read_int32();
        return sessionId;
    }

    /**
     * PROTOCOL WORD
     * @return (String) Retorna l'String que ha enviat el client per a jugar.
     * @throws IOException
     * @throws ComUtilsException
     * @throws ProtocolException Salta si la "word" no te una quantitat de 5 bytes.
     */
    public String read_word() throws IOException, ComUtilsException, ProtocolException {
        String word = comUtils.read_string_5bytes();
        if(word.matches("^[a-zA-Z]*$")){
            return word;
        }
        else
        {
            throw new ProtocolException(1);
        }
    }

    /**
     * PROTOCOL ERROR
     * @return retorna una llista d'objectes amb:
     *      [0] ==> (int) CODI D'ERROR.
     *      [1] ==> (String) Missatge d'error.
     * @throws IOException
     * @throws ComUtilsException
     */
    public List<Object> read_error() throws IOException, ComUtilsException {
        List<Object> message = new ArrayList<Object>();
        int errCode = comUtils.read_byte();
        String msg = comUtils.read_string();
        message.add(errCode);
        message.add(msg);
        return message;
    }

    /**
     * PROTOCOL READY
     * @param session_id (int) ID de sessió del client que informe que esta disponible.
     * @throws IOException
     * @throws ComUtilsException
     */
    public void write_ready(int session_id) throws IOException, ComUtilsException {
        comUtils.write_byte(2);
        comUtils.write_int32(session_id);
    }


    /**
     * PROTOCOL ADMIT
     * @param admit (boolean) que informa al client que la partida esta preparada.
     * @throws IOException
     * @throws ComUtilsException
     */
    public void write_admit(boolean admit) throws IOException, ComUtilsException {
        int bool;
        bool = admit ? 1 : 0;
        comUtils.write_byte(4);
        comUtils.write_byte(bool);
    }


    /**
     * PROTOCOL RESULT
     * @param result (String) de 5 signes que representa el resultat segons la lògica del joc.
     * @throws IOException
     * @throws ComUtilsException
     * @throws ProtocolException Saltarà si el resultat no té mida 5 o si el resultat no té el format del protocol.
     */
    public void write_result(String result) throws IOException, ComUtilsException, ProtocolException {
        result = result.trim();
        if(result.length() != 5){
            throw new ProtocolException("El tamaño del resultado debe de ser de 5 bytes, no de "+result.length());
        }
        if(comUtils.checkResult(result) == false){
            throw new ProtocolException("El resultado no esta en el formato del protocolo, es decir, contiene signos distintos de '*', '?' o '^'");
        }
        comUtils.write_byte(6);
        comUtils.write_string(result,false);
    }

    /**
     * PROTOCOL WORD
     * @param word (String) Paraula que s'envia al client al finalitzar la partida.
     * @throws IOException
     * @throws ComUtilsException
     * @throws ProtocolException Excepció que saltarà si la paraula no té mida 5.
     */
    public void write_word(String word) throws IOException, ComUtilsException, ProtocolException {
        word = word.trim();
        if(word.length() != 5){
            throw new ProtocolException("El tamaño de la palabra debe de ser de 5 bytes, no de "+word.length());
        }
        comUtils.write_byte(5);
        comUtils.write_string(word,false);
    }

    /**
     * PROTOCOL STATS
     * @param json (String) String d'un fitxer JSON que envia les estadistiques del jugador.
     * @throws IOException
     * @throws ComUtilsException
     */
    public void write_stats(String json) throws IOException, ComUtilsException {
        comUtils.write_byte(7);
        comUtils.write_string(json,true);
    }

    /**
     * PROTOCOL ERROR
     * @param errCode (int) Codi de l'error que s'ha produït.
     * @param msg (String) Missatge de l'error que s'ha produït.
     * @throws IOException
     * @throws ComUtilsException
     */
    public void write_error(int errCode, String msg) throws IOException, ComUtilsException {
        comUtils.write_byte(8);
        comUtils.write_byte(errCode);
        comUtils.write_string(msg,true);
    }

}