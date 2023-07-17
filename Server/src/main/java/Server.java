import ComUtilsExceptions.ComUtilsException;
import ComUtilsExceptions.ErrorProtocol;
import ComUtilsExceptions.ProtocolException;
import Utils.JsonnerToFile;
import Utils.Logger;
import ProtocolServer.ProtocolServer;
import Utils.Stats;

import java.io.*;
import java.net.*;
import java.util.*;

// Server class
public class Server {

    static Set<String > diccionari;
    static JsonnerToFile jsonner;
    static int port;
    static boolean canStart;
    public static void main(String[] args)
    {
        lecturaParametres(args);
        if(canStart)
        {
            //Creem un socket per al servidor
            ServerSocket server = null;
            try {
                //Carreguem el diccionari.
                cargarDiccionari();
                //CReem un nuevo jsonner
                jsonner = new JsonnerToFile();

                // Posem el servidor en espera al port indicat.
                server = new ServerSocket(port);
                server.setReuseAddress(true);

                // Creem un bucle infinit para escoltar las connexions de
                // los clientes
                while (true) {

                    // Creem un nou socket per al client amb el metode accept del socket server, el
                    // qual no avançarà fins que no arribi una nova connexió.
                    Socket client = server.accept();

                    // Quan un client es connecta, ho expressem.
                    System.out.println("Nou client connected"
                            + client.getInetAddress()
                            .getHostAddress());


                    // Creem un nou ClientConnected.
                    ClientConnected clientSock
                            = new ClientConnected(client);

                    // Creem un nou fil amb la classe clientStock que portara tota la lògica del servidor durant la partida.
                    new Thread(clientSock).start();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {

                    if(jsonner != null){
                        jsonner.writeAllStatsToFile();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                try{
                    if (server != null) {
                        server.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Metode que es fa servir per a la lectura dels parametres inicial
     * @param args Array d'strings on ens venen tots els parametres d'entrada.
     */
    private static void lecturaParametres(String[] args) {
        HashMap<String, String> options = new HashMap<>();
        for (int i=0; i< args.length; i=i+2){
            options.put(args[i],args[i+1]);
        }
        try{
            if(options.containsKey("-p")){
                port = Integer.parseInt(options.get("-p"));
            }
            else
            {
                port = 1234;
            }
            canStart = true;
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Mètode que carrega el diccionari (variable tipus hashset) amb totes les paraules per a tenirles carregades des del principi
     * i no haver de fer més vegades l'accés a l'arxiu.
     * @throws IOException Excepció tipus IO
     */
    public static void cargarDiccionari() throws IOException {
        diccionari = new HashSet<>();


        BufferedReader obj = new BufferedReader(new InputStreamReader(Server.class.getClassLoader().getResourceAsStream("DISC2-LP-WORDLE.txt")));

        String strng;
        while ((strng = obj.readLine()) != null){
            if(strng.matches("^[a-zA-Z]*$")){
                diccionari.add(strng);
            }


        }



    }

    // Classe de Client Connected
    public static class ClientConnected implements Runnable {

        /**
         * VARIABLES
         */
        //Socket de comunicació amb el client.
        private Socket clientSocket = null;
        //Protocol que fa servir el servidor.
        private final ProtocolServer protocol;
        //Estat del servidor.
        OPERACIO operacioActual = null;
        //ID de sessió del client
        int idSeason = 0;
        //ID sessió anterior
        int idSeasonAnterior = -1;
        //Nom del client
        String nameClient = "";
        //Booleà que indica si s'ha de passar a un nou estat.
        boolean nextSelected = false;
        //Paraula secreta
        String secretWord = "";
        //Paraula que envia el client
        String clientWord = "";
        //String que contindrà el resultat
        String resultWord = "";
        //Boole per a sortir o no del bucle
        boolean bucle = true;
        //Hashmap de suport per a la creació del resultat.
        Map<Character, Integer> charCont;
        //Utils.Logger del thread en questió.
        Logger log;
        //Comptador d'intents.
        int comptaIntents = 0;
        //Comptador del client.
        Stats statsPlayer;
        //Error
        ErrorProtocol errorReceived;
        /**
         *  FI DE VARIABLES
         */


        /**
         * Consctructor de client connected., inicialitza el protocol i crea un nou log amb el nom del thread.
         * @param socket Socket que s'utilitza per establir la comunicació amb el client.
         * @throws IOException Excepció tipus IO que salta del socket.
         */
        public ClientConnected(Socket socket) throws IOException {

            //Quan creem una nova partida, passem el socket de comunicació i posteriorment creem el logger.
            this.clientSocket = socket;
            this.protocol = new ProtocolServer(clientSocket.getInputStream(), clientSocket.getOutputStream());
            log = new Logger(Thread.currentThread().getName());
            log.clientSocketLog(true);
            log.serverSocketLog(true);

        }


        /**
         * Llegeix la següent operació fent servir el protocol.
         */
        public void readNextOp() throws ProtocolException, IOException {
                int operacio = protocol.read_op_server();
                operacioActual = setOperacioActual(operacio);
        }

        /**
         * Proporciona l'estat actual.
         * @param i opcode
         * @return Retorna el l'enum del tipus OPERACIO corresponent.
         * @throws ProtocolException Excepció de protocol tipus 2, missatge fora de protocol.
         */
        public OPERACIO setOperacioActual(int i) throws ProtocolException {

            for (OPERACIO OPERACIO : OPERACIO.values()) {
                if (OPERACIO.getOpNum() == i) {

                    checkSeguentOperacio(OPERACIO);
                    return OPERACIO;

                }
            }
            throw new ProtocolException(2);
        }

        /**
         * Comprova que l'operació rebuda és correcte i retorna un error de protocol tipus 3 en cas contrari.
         * @param opNova Nova operació que és checkeja.
         * @throws ProtocolException Excepció de protocol tipus 3 si l'operació no es la que toca.
         */
        public void checkSeguentOperacio(OPERACIO opNova) throws ProtocolException {
            if(operacioActual == null){
                if(opNova != OPERACIO.HELLO){
                    throw new ProtocolException(3);
                }
            }
            else{
                switch (operacioActual){
                    case HELLO:
                        if(opNova != OPERACIO.PLAY && opNova != OPERACIO.HELLO && opNova != OPERACIO.ERROR){
                           throw new ProtocolException(3);
                        }
                        break;
                    case PLAY:
                        if(opNova != OPERACIO.WORD && opNova != OPERACIO.PLAY && opNova != OPERACIO.ERROR){
                            throw new ProtocolException(3);
                        }
                        break;
                    case WORD:
                        if(opNova != OPERACIO.WORD && opNova != OPERACIO.PLAY && opNova != OPERACIO.HELLO && opNova != OPERACIO.ERROR){
                            throw new ProtocolException(3);
                        }
                        break;
                    case ERROR:
                        break;
                }
            }
        }



        /**
         * Metode principal del fil i maquina d'estats del servidor. El primer que fa és
         */
        public void run()
        {
            try {

                //Mentre la variable bucle segueixi sent true:
                while(bucle) {
                    //AGAFEM L'OPERACIÓ ACTUAL
                    if (!nextSelected) {
                        try{
                            readNextOp();
                            System.out.println(operacioActual);
                            nextSelected = true;
                        }catch (ProtocolException ex){
                            protocol.write_error(ex.errorProtocol.getErrorCode(), ex.errorProtocol.getErrorMessage());
                            log.ERROR(ex.errorProtocol,true);
                        }

                    }
                    //En cas que l'operació no sigui NULL, fem un commutador.
                    if (operacioActual != null){
                        switch (operacioActual) {
                            case HELLO:
                                try
                                {
                                    HelloReceived();

                                }catch (ProtocolException ex){
                                    protocol.write_error(ex.errorProtocol.getErrorCode(), ex.errorProtocol.getErrorMessage());
                                    log.ERROR(ex.errorProtocol,true);
                                }finally {
                                    nextSelected = false;
                                }
                                break;
                            case PLAY:
                                PlayReceived();
                                nextSelected = false;
                                break;
                            case WORD:
                                try
                                {
                                    WordReceived();

                                }catch (ProtocolException ex){
                                    protocol.write_error(ex.errorProtocol.getErrorCode(), ex.errorProtocol.getErrorMessage());
                                    log.ERROR(ex.errorProtocol,true);
                                }finally {
                                    nextSelected = false;
                                }
                                break;
                            case ERROR:
                                ErrorReceived();
                                nextSelected = false;
                                break;
                            default:
                                bucle = false;
                                break;
                        }
                    }
                }
            }
            catch (IOException | ComUtilsException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (!clientSocket.getKeepAlive()){
                        log.clientSocketLog(false);
                        clientSocket.close();
                        log.serverSocketLog(false);
                    }
                    synchronized (jsonner) {
                        jsonner.updateStats(idSeason, statsPlayer);
                        jsonner.writeAllStatsToFile();
                        jsonner.wrotedbclientsToFile();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Mètode que es crida en l'estat HELLO i gestiona la lògica del servidor en aquest moment.
         * @throws ComUtilsException Excepció de ComUtils.
         * @throws IOException Excepció tipus IO que prové del Socket.
         * @throws ProtocolException Excepció de Protocol que s'enviara en cas que el check de l'id i el nom sigui incorrecte.
         */
        private void HelloReceived() throws ComUtilsException, IOException, ProtocolException {
            List<Object> hello = protocol.read_hello();

            idSeason = (int) hello.get(0);
            nameClient = (String) hello.get(1);
            log.HELLO(idSeason, nameClient);
            synchronized (jsonner){

                if(idSeason == 0){
                    idSeason = jsonner.CreateNewClient(nameClient);
                }
                else
                {
                    jsonner.checkIdSeason(idSeason, nameClient);
                }
                this.statsPlayer = jsonner.getStatsPlayer(idSeason);
                idSeasonAnterior = idSeason;
            }

            protocol.write_ready(idSeason);
            log.READY(idSeason);
        }


        /**
         * Metode que es crida en l'estat de PLAY i gestiona la lògica del servidor en aquest moment.
         * @throws IOException Excepció tipus IO
         * @throws ComUtilsException Excepció de ComUtils.
         */
        private void PlayReceived() throws IOException, ComUtilsException {
            int newID = protocol.read_play();
            log.PLAY(newID);
            if (newID == idSeason) {
                comptaIntents = 0;
                chooseRandomWord();
                protocol.write_admit(true);
                log.ADMIT(true);
            } else {
                protocol.write_admit(false);
                log.ADMIT(false);
            }
        }

        /**
         * Funció que escull una paraula random del diccionari otorgat.
         * @throws IOException Excepció tipus IO
         */
        public void chooseRandomWord() throws IOException
        {
            synchronized (this){
                int size = diccionari.size();
                int randomInt = new Random().nextInt(size);
                int i = 0;
                for(String str : diccionari)
                {
                    if (i == randomInt){

                        if(randomInt<size-1){
                            randomInt++;
                        }

                        secretWord = (String) str;
                        System.out.println(secretWord);
                        break;
                    }
                    i++;
                }
            }

        }



        /**
         * Mètode que es crida en l'estat WORD i gestiona la lògica del servidor en aquest moment.
         * @throws IOException Excepció tipus IO
         * @throws ProtocolException Excepció de protocol que saltarà del checkWord en cas que la paraula no es trobi al diccionari o que s'hagi llegit una paraula amb un signe que no es reconeix
         * @throws ComUtilsException Excepció de ComUtils
         */
        private void WordReceived() throws IOException, ProtocolException, ComUtilsException {
            clientWord = protocol.read_word().toUpperCase();
            log.WORD(clientWord, false);
            checkWord();
            comptaIntents++;
            if(esVictoria()){
                controlVictoria();
            }
            else if(comptaIntents <= 5)
            {
                controlSegueixJugant();
            }
            else if(comptaIntents == 6)
            {
                controlDerrota();
            }
        }

        /**
         * Funció de suport per al control d'una derrota quan es rep una paraula.
         * @throws IOException Excepció tipus IO
         * @throws ProtocolException Excepció de Protocol que saltarà en cas que no s'envii correctament la paraula.
         * @throws ComUtilsException Excepció de ComUtils.
         */
        private void controlDerrota() throws IOException, ProtocolException, ComUtilsException {
            statsPlayer.addDerrota();
            synchronized (jsonner) {
                jsonner.updateStats(idSeason, statsPlayer);
            }
            protocol.write_result(resultWord);
            log.RESULT(resultWord);
            protocol.write_word(secretWord);
            log.WORD(secretWord, true);
            String json = statsPlayer.StatsToJson().toJSONString();
            protocol.write_stats(json);
            log.STATS(json);
        }

        /**
         * Funció de suport per al control de seguir jugant quan es rep una paraula.
         * @throws ProtocolException Excepció de protocol que saltarà en cas que no s'escrigui correctament el resultat.
         * @throws ComUtilsException Excepció de ComUtils.
         * @throws IOException Excepció tipus IO
         */
        private void controlSegueixJugant() throws ProtocolException, ComUtilsException, IOException {
            protocol.write_result(resultWord);
            log.RESULT(resultWord);
        }

        /**
         * Funció de suport per al control d'una victoria quan es rep una paraula.
         * @throws ProtocolException Excepció de protocol que saltarà en cas que no s'escrigui correctament la paraula result.
         * @throws ComUtilsException Excepció de ComUtils.
         * @throws IOException Excepció tipus IO
         */
        private void controlVictoria() throws ProtocolException, ComUtilsException, IOException {
            statsPlayer.addVictoria(comptaIntents);
            synchronized (jsonner) {
                jsonner.updateStats(idSeason, statsPlayer);
            }
            protocol.write_result(resultWord);
            log.RESULT(resultWord);
            String json = statsPlayer.StatsToJson().toJSONString();
            protocol.write_stats(json);
            log.STATS(json);
        }

        /**
         * Metode que ens diu si s'ha guanyat.
         * @return Retorna un boolea que diu si el resultword es igual a ^^^^^.
         */
        public boolean esVictoria() {
            return resultWord.equals("^^^^^");
        }

        /**
         * Funció que crea el resultat seguint la lògica del Wordle.
         * @throws ProtocolException Excepció de protocol que saltarà en cas que la paraula no estigui al diccionari.
         */
        public void checkWord() throws ProtocolException {

            char[] resultCharArray = new char[5];
            checkClientWord();
            setDictWord();

            for (int i = 0; i<5; i++){
                if(clientWord.charAt(i) == secretWord.charAt(i)){
                    resultCharArray[i] = '^';
                    int cont = charCont.get(clientWord.charAt(i));
                    charCont.replace(clientWord.charAt(i),cont-1);
                }
            }

            for (int i = 0; i<5; i++){
                if(resultCharArray[i] != '^'){
                    if(charCont.containsKey(clientWord.charAt(i))){
                        if(charCont.get(clientWord.charAt(i)) > 0){
                            resultCharArray[i] = '?';
                            int cont = charCont.get(clientWord.charAt(i));
                            charCont.replace(clientWord.charAt(i),cont-1);
                        }
                        else{resultCharArray[i] = '*';}
                    }
                    else{resultCharArray[i] = '*';}
                }
            }

            resultWord = String.valueOf(resultCharArray);
        }

        /**
         * Funció que fa un check de si la paraula del client es correcte.
         * @throws ProtocolException Es retorna una excepció de protcol del tipus 5 en cas contrari.
         */
        private void checkClientWord() throws ProtocolException {
            synchronized (this){
                if(!diccionari.contains(clientWord)){
                    throw new ProtocolException(5);
                }
            }

        }


        /**
         * Funció que crea el diccionari per a comptar el nombre de cops que hi ha cada lletra a la paraula.
         */
        private void setDictWord(){

            charCont = new HashMap<>();
            for(int i = 0; i<5; i++){
                if(charCont.isEmpty()){
                    charCont.put(secretWord.charAt(i),1);
                }
                else if(!charCont.containsKey(secretWord.charAt(i))){
                    charCont.put(secretWord.charAt(i),1);
                }
                else{
                    int cont = charCont.get(secretWord.charAt(i));
                    charCont.replace(secretWord.charAt(i),cont+1);
                }
            }
        }

        /**
         * Mètode que es crida en l'estat ERROR i gestiona la lògica del servidor en aquest moment.
         * @throws ComUtilsException Excepció tipus ComUtils
         * @throws IOException Excepció tipus IO
         */
        private void ErrorReceived() throws ComUtilsException, IOException {
            List<Object> error = protocol.read_error();
            errorReceived = new ErrorProtocol((Integer) error.get(0));
            log.ERROR(errorReceived,false);
            clientSocket.close();
            bucle = false;
        }





    }
}