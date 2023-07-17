package ProtocolClient;

import ComUtilsExceptions.ComUtilsException;
import ComUtilsExceptions.ProtocolException;
import utils.ComUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class ProtocolClient {

    private ComUtils comUtils;

    /**
     * CONSCTRUCTOR
     * @param inputStream
     * @param outputStream
     * @throws IOException
     */
    public ProtocolClient(InputStream inputStream, OutputStream outputStream) throws IOException {
        comUtils = new ComUtils(inputStream, outputStream);
    }

    /**
     *
     * @return Retorna el número d'operació que llegirà el client.
     * @throws IOException
     * @throws ProtocolException Excepció llançada en cas que es rebi una operació que el client no ha de llegir.
     */
    public int read_op_client() throws IOException, ProtocolException {
        int op = comUtils.read_byte();
        if(op != 2 && op != 4 && op != 5 && op != 6 && op != 7 && op != 8){
            throw new ProtocolException(2);
        }
        return op;
    }

    /**
     * PROTOCOL READY
     * @return retorna el SessionID que ha de llegir per a l'operació READY
     * @throws IOException
     */
    public int read_ready() throws IOException{
        int sessionId = comUtils.read_int32();
        return sessionId;
    }

    /**
     * PROTOCOL ADMID
     * @return retorna un booleà que defineix si ha estat admès o no pel servidor segons l'operació ADMIT.
     * @throws IOException
     */
    public boolean read_admit() throws IOException, ProtocolException {
        int bool = comUtils.read_byte();
        if(bool == 0){
            return false;
        }
        else if(bool == 1){
            return true;
        }
        else{
            throw new ProtocolException(1);
        }
    }

    /**
     * PROTOCOL RESULT
     * @return retorna un String que conté la informació sobre el resultat que dona el servidor per part de la paraula enviada, operació RESULT.
     * @throws IOException
     * @throws ComUtilsException
     * @throws ProtocolException Saltarà si el resultat no té mida 5 o si el resultat no té el format del protocol.
     */
    public String read_result() throws IOException, ProtocolException {

        String result = comUtils.read_string_5bytes();
        if(comUtils.checkResult(result) == false){
            throw new ProtocolException(6);
        }

        return result;
    }

    /**
     * PROTOCOL WORD
     * @return (String) Retorna l'String que ha enviat el servidor al acabar.
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
     * PROTOCOL STATS
     * @return retorna un String en format JSON per llegir les estadístiques del jugador.
     * @throws IOException
     * @throws ComUtilsException
     */
    public String read_stats() throws IOException, ComUtilsException {
        String json = comUtils.read_string();
        //TODO TIENE QUE IR LA CLASSE DE JSONER Y STATS EN COMUTILS PARA QUE TANTO CLIENTE COMO SERVER PUEDAN VERLO Y PODER COMPROBAR AQUI, AL MAS BAJO NIVEL.
        return json;
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
     * PROTOCOL HELLO
     * @param session_id (int) ID del client que s'envia a servidor per a connectar-se.
     * @param name (String) nom del client que s'envia a servidor per a connectar-se.
     * @throws IOException
     * @throws ComUtilsException
     */
    public void write_hello(int session_id, String name) throws IOException, ComUtilsException {
        comUtils.write_byte(1);
        comUtils.write_int32(session_id);
        comUtils.write_string(name,true);
    }

    /**
     * PROTOCOL PLAY
     * @param session_id (int) ID del client que s'envia a servidor per a començar la partida.
     * @throws IOException
     * @throws ComUtilsException
     */
    public void write_play(int session_id) throws IOException, ComUtilsException {
        comUtils.write_byte(3);
        comUtils.write_int32(session_id);
    }

    /**
     * PROTOCOL WORD
     * @param word (String) Paraula que s'envia al servidor per a jugar.
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