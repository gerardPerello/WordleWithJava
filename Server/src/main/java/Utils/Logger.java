package Utils;

import ComUtilsExceptions.ErrorProtocol;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    /**
     * VARIABLES
     */
    private String _rutaLog;
    private int TAM_MAX_LOG = 52428880;
    private int DIAS_MAX_LOG = 30;
    private File file;
    private int linelenght = 40;
    private int Cposition = 8;
    /**
     * FI DE VARIABLES
     */

    /**
     * ENUM que ajuda a determinar el tipus de LOG.
     */
    public enum TYPE_LOG{
        HELLO,
        READY,
        PLAY,
        ADMIT,
        WORD,
        RESULT,
        STATS,
        ERROR,
        GENERALINFO
    }

    /**
     * Consctructor del logger.
     * @param threadName Nom del thread que utilitzarà aquest objecte logger.
     * @throws IOException
     */
    public Logger(String threadName) throws IOException {
        setRuta(threadName);

    }

    /**
     * Mètode que es crida quan es produeix la connexió o desconnexió per part del client.
     * @param connect true = connexió nova, false = desconnectat.
     * @throws IOException
     */
    public void clientSocketLog(boolean connect) throws IOException {
        String mesage;
        if(connect){
            mesage = "C- [TCP Connect]\n";
        }else{
            mesage = "C- [conexion closed]\n";
        }
        FileWriter writer = new FileWriter(_rutaLog,true);
        writer.write(mesage);
        writer.close();
    }

    /**
     * Mètode que es crida quan es produeix l'acceptació o desconnexió per part del servidor.
     * @param connect true = connexió acceptada, false = desconnectat.
     * @throws IOException
     */
    public void serverSocketLog(boolean connect) throws IOException {
        String mesage;
        if(connect){
            mesage = "S- [TCP Accept]\n";
        }else{
            mesage = "S- [conexion closed]\n";
        }
        FileWriter writer = new FileWriter(_rutaLog,true);
        writer.write(mesage);
        writer.close();
    }

    /**
     * Mètode que escriu la comanda HELLO al log.
     * @param id id de l'usuari de la comanda hello.
     * @param name nom de l'usuari de la comanda hello.
     * @throws IOException
     */
    public void HELLO(int id, String name) throws IOException {
        String mesage = "1 " + id + " " + name + " " +id;
        Add(TYPE_LOG.HELLO,false,mesage);
    }

    /**
     * Mètode que escriu la comanda READY al log.
     * @param id Id del client que li passem amb la comanda ready.
     * @throws IOException
     */
    public void READY(int id) throws IOException {
        String mesage = "2 "+Integer.toString(id);
        Add(TYPE_LOG.READY,true,mesage);
    }

    /**
     * Mètode que escriu la comanda PLAY al log.
     * @param id Id del client que vol començar una partida.
     * @throws IOException
     */
    public void PLAY(int id) throws IOException {
        String mesage = "3 "+Integer.toString(id);
        Add(TYPE_LOG.PLAY,false,mesage);
    }

    /**
     * Mètode que escriu la comanda ADMIT al log.
     * @param admitted booleà que ens diu si el client ha estat admès o no.
     * @throws IOException
     */
    public void ADMIT(boolean admitted) throws IOException {
        String mesage = "4";
        if(admitted)
             mesage= mesage + " 1";
        else
            mesage = mesage + " 0";
        Add(TYPE_LOG.ADMIT,true,mesage);
    }

    /**
     * Mètode que escriu la comanda WORD al log.
     * @param word paraula enviada pel client o pel servidor.
     * @param fromServer
     * @throws IOException
     */
    public void WORD(String word, boolean fromServer) throws IOException {
        Add(TYPE_LOG.WORD,fromServer,"5 "+word);
    }

    /**
     * Mètode que escriu la comanda RESULT al log.
     * @param result String resultant del check de la paraula enviada pel client.
     * @throws IOException
     */
    public void RESULT(String result) throws IOException {
        Add(TYPE_LOG.RESULT,true,"6 "+result);
    }

    /**
     * Mètode que escriu la comanda STATS al log.
     * @param stats estadístiques que son enviades al client.
     * @throws IOException
     */
    public void STATS(String stats) throws IOException {
        Add(TYPE_LOG.STATS,true,"7 "+stats);
    }

    /**
     * Mètode que escriu la comanda ERROR al log.
     * @param errorProtocol Objecte tipus protocolError que conté la informació sobre l'error en qüestió.
     * @param fromServer Booleà que estableix si l'error ve del servidor (true) o ve del client (false).
     * @throws IOException
     */
    public void ERROR(ErrorProtocol errorProtocol, boolean fromServer) throws IOException {
        Add(TYPE_LOG.ERROR,fromServer,"8 "+ errorProtocol.getErrorCode()+" "+errorProtocol.getErrorMessage());
    }

    /**
     * Mètode que crea l'String a escriure a l'arxiu .log i posteriorment l'escriu.
     * @param tipo Tipus de log segons l'enum.
     * @param fromServer Booleà que determina si la comanda l'ha fet el client o el servidor.
     * @param mesage Missatge ha escriure en la comanda.
     * @throws IOException
     */
    private void Add(TYPE_LOG tipo, boolean fromServer ,String mesage) throws IOException {

        StringBuilder log = new StringBuilder();

        log.append(formatLog(tipo));
        log.append(formatPreMissatge(fromServer));
        log.append(mesage);
        log.append(formatPostMissatge(fromServer, log.length()));

        FileWriter writer = new FileWriter(_rutaLog,true);
        writer.write(log.toString());
        writer.close();



    }

    /**
     * Mètode que retorna l'String segons el format POSTERIOR al missatge.
     * @param fromServer Booleà que determina si la comanda l'ha fet el client o el servidor.
     * @param posicioActual posició actual de la linea.
     * @return
     */
    private String formatPostMissatge(boolean fromServer, int posicioActual) {
        StringBuilder sb = new StringBuilder();
        if(posicioActual < linelenght){
            for (int i = posicioActual; i < linelenght; i++){
                sb.append("-");
            }

        }
        else{
            sb.append("-------");
        }

        if(fromServer)
        {
            sb.append("-");
        }
        else
        {
            sb.append(">");
        }
        sb.append(" S\n");
        return sb.toString();
    }

    /**
     * Mètode que retorna l'String segons el format PREVI al missatge.
     * @param fromServer Booleà que determina si la comanda l'ha fet el client o el servidor.
     * @return
     */
    private String formatPreMissatge(boolean fromServer) {
        StringBuilder sb = new StringBuilder();
        sb.append("C ");

        if(fromServer)
        {
            sb.append("<------");
        }
        else
        {
            sb.append("-------");
        }
        return sb.toString();
    }

    /**
     * Retorna una String amb el tipus de log escrit.
     * @param tipo
     * @return
     */
    private String formatLog(TYPE_LOG tipo) {
        StringBuilder sb = new StringBuilder();
        sb.append(tipo);
        for(int i = tipo.toString().length(); i<Cposition; i++){
            sb.append(" ");
        }
        return sb.toString();
    }


    /**
     * Mètode que crea els directoris i arxius de .log i estableix les rutes en questió.
     * @param threadName Nom del fil que serà el nom de l'arxiu .log.
     * @throws IOException
     */
    private void setRuta(String threadName) throws IOException {
        String s = System.getProperty("user.dir");
        if(s.endsWith("Server")){
            s = s.substring(0,s.length()-6);
        }
        s = s + "/Server/log";
        File directory = new File(s);
        if(!directory.exists()){
            if(directory.mkdirs())
            {
                System.out.println("Directorio creado");
            }
            else
            {
                System.out.println("Error al crear el directorio");
            }
        }
        _rutaLog = s+"/Server_"+threadName+".log";
        file = new File(_rutaLog);
        file.createNewFile();


    }




}