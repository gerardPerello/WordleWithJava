package Utils;

import ComUtilsExceptions.ProtocolException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.Random;

public class JsonnerToFile {

    /**
     * VARIABLES
     */
    String filePathStats;
    String filePathClientDB;
    File jsonFileStats;
    File jsonFileClientDB;
    private JSONObject allStats;
    private JSONObject clientdb;
    private static FileWriter file;
    /**
     * FI DE VARIABLES
     */

    /**
     * Constructor de la classe JsonnerToFile que s'encarrega de la gestió dels json, accés, lectura i escriptura.
     * @throws IOException
     */
    public JsonnerToFile() throws IOException {
        allStats = new JSONObject();
        clientdb = new JSONObject();
        setRuta("/Server/db/stats.json");
        setRuta("/Server/db/client_db.json");

        if(!filePathClientDB.isEmpty()){
            getAllClientsFromFile();
        }
        if(!filePathStats.isEmpty()) {
            getAllStatsFromFile();
        }
    }


    /**
     * Mètode que porta el control de l'IdSeason del client amb el nom i tira una excepció de protocol de tipus 4 en cas que l'usuari no estigui a la DB.
     * @param idSeason Int id de sessió de l'usuari
     * @param nameClient String nom de l'usuari.
     * @throws ProtocolException
     */
    public void checkIdSeason(int idSeason, String nameClient) throws ProtocolException {
        if(clientdb.containsKey(Integer.toString(idSeason))){
            if(!clientdb.get(Integer.toString(idSeason)).equals(nameClient)){
                throw new ProtocolException(4);
            }
        }
        else
        {
            throw new ProtocolException(4);
        }
    }

    /**
     * Mètode que escriu la base de dades de clients a l'arxiu json.
     * @return un booleà que ens diu si s'ha completat amb exit.
     * @throws IOException
     */
    public boolean wrotedbclientsToFile() throws IOException {
        try {
            file = new FileWriter(filePathClientDB);
            file.write(clientdb.toJSONString());
            file.flush();
            file.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            file.flush();
            file.close();
        }
        return false;
    }

    /**
     * Mètode que escriu els estats de clients a l'arxiu json.
     * @return un booleà que ens diu si s'ha completat amb exit.
     * @throws IOException
     */
    public boolean writeAllStatsToFile() throws IOException {
        try {
            file = new FileWriter(filePathStats);
            file.write(allStats.toJSONString());
            file.flush();
            file.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            file.flush();
            file.close();
        }
        return false;
    }

    /**
     * Mètode que crea un nou client amb un nou idseason que nestigui disponible. Per tant, assigna aquest idSeason tant
     * al nom del client com a uns nous stats que s'actualitzaran quan aquest jugui partides.
     * @param nameClient Nom del client en questió.
     * @return l'id del client que s'ha creat.
     * @throws IOException
     */
    public int CreateNewClient(String nameClient) throws IOException {
        int newId =  new Random().nextInt(100000);
        while (clientdb.containsKey(newId)){
            newId =  new Random().nextInt(100000);
        }
        clientdb.put(Integer.toString(newId),nameClient);
        wrotedbclientsToFile();
        Stats stats = new Stats();
        JSONObject newStats = stats.StatsToJson();
        allStats.put(Integer.toString(newId),newStats);
        writeAllStatsToFile();
        return newId;
    }


    /**
     * Mètode que carrega la db de client/nom del json corresponent.
     */
    public void getAllClientsFromFile() {
        JSONParser parser = new JSONParser();

        try {
            BufferedReader obj = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(filePathClientDB)));

            clientdb = (JSONObject) parser.parse(obj);
            obj.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mètode que carrega la db de client/estadístiques del json corresponent.
     */
    private void getAllStatsFromFile(){
        JSONParser parser = new JSONParser();

        try {
            BufferedReader obj = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(filePathStats)));
            allStats = (JSONObject) parser.parse(obj);
            obj.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mètode que actualitza les estadístiques d'un client determinat.
     * @param idSeason del client a editar-li les estadístiques
     * @param stats estadístiques del client actualitzades.
     * @throws IOException
     */
    public void updateStats(int idSeason,Stats stats) throws IOException {
        if(stats != null){
            JSONObject jsonStats = stats.StatsToJson();
            allStats.put(Integer.toString(idSeason), jsonStats);
        }
        writeAllStatsToFile();
    }

    /**
     * Mètode que extreu les stats d'un client determinat.
     * @param idSeason del client al qual volem obtenir les seves estadístiques.
     * @return les estadístiques del client en qüestió.
     * @throws FileNotFoundException
     */
    public Stats getStatsPlayer(int idSeason) throws FileNotFoundException {
        JSONObject jsonObject = (JSONObject) allStats.get(Integer.toString(idSeason));
        JSONObject stats = (JSONObject) jsonObject.get("Stats");
        long jugades = ((long) stats.get("Jugades"));
        double exits = ((double) stats.get("Exits %"));
        long ratxaActual = ((long) stats.get("Ratxa Actual"));
        long ratxaMaxima = ((long) stats.get("Ratxa Maxima"));
        HashMap victories = (HashMap) stats.get("Victories");

        return new Stats(jugades, exits, ratxaActual, ratxaMaxima, victories);

    }


    /**
     * Mètode que crea els directoris i arxius de json i estableix les rutes en questió.
     * @param filename
     * @throws IOException
     */
    public void setRuta(String filename) throws IOException {

        if(filename.equals("/Server/db/stats.json")){
            filePathStats = "stats.json";
        }else if(filename.equals("/Server/db/client_db.json")){
            filePathClientDB = "client_db.json";
        }else{
            filePathClientDB = "TESTING.json";
        }
        /**
        String s = System.getProperty("user.dir");
        if(s.endsWith("Server")){
            s = s.substring(0,s.length()-7);
        }
        s = s + filename;
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
        if(filename.equals("/Server/db/stats.json")){
            filePathStats = s;
            jsonFileStats = new File(filePathStats);
            if(!jsonFileStats.exists()){
                if(jsonFileStats.createNewFile()){
                    FileWriter fileWriter = new FileWriter(jsonFileStats);
                    fileWriter.write("{}");
                    System.out.println("File created");
                }else{
                    System.out.println("File not created");
                }
            }
        }else{
            filePathClientDB = s;
            jsonFileClientDB = new File(filePathClientDB);
            if(!jsonFileClientDB.exists()){
                if(jsonFileClientDB.createNewFile()){
                    FileWriter fileWriter = new FileWriter(jsonFileClientDB);
                    fileWriter.write("{}");
                    System.out.println("File created");
                }else{
                    System.out.println("File not created");
                }
            }
        }**/

    }




}
