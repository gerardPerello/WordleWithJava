import ComUtilsExceptions.ComUtilsException;
import ComUtilsExceptions.ErrorProtocol;
import ComUtilsExceptions.ProtocolException;
import ProtocolClient.ProtocolClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

// Client class
public class Client{

    static String hostname;
    static String clientName;
    static int port;
    static boolean canStart;
    static boolean auto;
    static Set<String> diccionariComplet;
    public static void main(String[] args) throws IOException {

        try{

            lecturaParametres(args);
            carregarDiccionari();
            ClientConnected client = new ClientConnected(clientName,hostname,port,auto);

        }catch (IOException e) {
            e.printStackTrace();
        }finally {

        }

    }

    /**
     * Función que busca en la DB el diccionario de palabras y lo mete en la variable diccionariComplet.
     * @throws IOException
     */
    private static void carregarDiccionari() throws IOException {
        diccionariComplet = new HashSet<>();
        if(diccionariComplet.isEmpty()){
            BufferedReader obj = new BufferedReader(new InputStreamReader(Client.class.getClassLoader().getResourceAsStream("DISC2-LP-WORDLE.txt")));


            String strng;
            while ((strng = obj.readLine()) != null) {
                if(strng.matches("^[a-zA-Z]*$")){
                    diccionariComplet.add(strng);
                }
            }
        }
    }

    /**
     * Mètode que es fa servir per a la lectura dels paràmetres inicial
     * @param args Array d'strings on ens venen tots els paràmetres d'entrada.
     */
    private static void lecturaParametres(String[] args) {
        HashMap<String, String> options = new HashMap<>();
        for (int i=0; i< args.length; i=i+2){
            options.put(args[i],args[i+1]);
        }
        try{
            if(options.containsKey("-s")){
                hostname = options.get("-s");
            }else{
                hostname = "localhost";
            }
            if(options.containsKey("-p")){
                port = Integer.parseInt(options.get("-p"));
            }
            else
            {
                port = 1234;
            }
            if(options.containsKey("-n")){
                clientName = options.get("-n");
            }else{
                clientName = "GerardNachoClient";
            }
            if(options.containsKey("-a")){
                auto = true;
            }else{
                auto = false;
            }
            canStart = true;
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Classe embedida que contiene toda la estructura del Cliente, el cual sigue la dinámica
     * Cliente-Servidor.
     */
    public static class ClientConnected{

            /**
             * VARIABLES
             */
            ProtocolClient protocol;
            Socket socket;
            OPERACIO operacioActual = null;
            int comptaIntents = 0;
            int idSeason = 0;
            String name = "";
            boolean nextSelected = false;
            ErrorProtocol errorReceived;
            Scanner sc = new Scanner(System.in);
            String line = "null";
            Set<String> diccionari;
            Set<String> diccionariReduit;
            boolean clientAutomaticBool = false;
            boolean automaticDirecte;
            String lastWord;
            HashSet<Character> charsContingutsEnParaulaSet;
            HashSet<Character>  charsNoContingutsEnParaulaSet;
            HashMap<Integer, Character> charContingutEnPosicioIHash;
            HashMap<Integer, Set<Character>> charNoContingutPosicioIHashContainingSet;
            /**
             * FI DE VARIABLES
             */

            /**
             * Constructor del client.
             * @param name
             * @param host
             * @param port
             * @throws IOException
             */
            public ClientConnected(String name, String host, int port, boolean auto) throws IOException {
                this.name = name;
                try{
                    automaticDirecte = auto;
                    socket = new Socket(host, port);
                    protocol = new ProtocolClient(socket.getInputStream(), socket.getOutputStream());
                    RunClient();
                }catch (SocketException ex){
                    System.out.println(ex.getMessage());
                }


            }

            /**
             * comprobamos si la operación que viene a continuación es correcta. En ese caso se devolverá
             * la operación.
             * @param i identificador operación seleccionada
             * @return operación selecionada
             * @throws ComUtilsExceptions.ProtocolException
             */
            public OPERACIO setOperacioActual(int i) throws ComUtilsExceptions.ProtocolException {
                for (OPERACIO OPERACIO : OPERACIO.values()) {
                    if (OPERACIO.getOpNum() == i) {

                        checkSiguienteOperacion(OPERACIO);
                        return OPERACIO;

                    }
                }
                throw new ProtocolException(2);
            }

            /**
             * Método que comprueba que la siguiente operación es correcta.
             * @param opNueva operación que se debe comprobar si es correcta.
             * @throws ProtocolException
             */
            private void checkSiguienteOperacion(OPERACIO opNueva) throws ProtocolException {
                if(operacioActual == null){
                    if(opNueva != OPERACIO.READY && opNueva != OPERACIO.ERROR){
                        throw new ProtocolException(3);
                    }
                }
                else{
                    switch (operacioActual){
                        case READY:
                            if(opNueva != OPERACIO.ADMIT && opNueva != OPERACIO.ERROR){
                                throw new ProtocolException(3);
                            }
                            break;
                        case ADMIT:
                            if(opNueva != OPERACIO.RESULT && opNueva != OPERACIO.ERROR){
                                throw new ProtocolException(3);
                            }
                            break;
                        case WORD:
                            if(opNueva != OPERACIO.STATS && opNueva != OPERACIO.ERROR){
                                throw new ProtocolException(3);
                            }
                            break;
                        case RESULT:
                            if(opNueva != OPERACIO.RESULT && opNueva != OPERACIO.STATS && opNueva != OPERACIO.WORD && opNueva != OPERACIO.ERROR){
                                throw new ProtocolException(3);
                            }
                            break;
                        case STATS:
                            if(opNueva != OPERACIO.RESULT && opNueva != OPERACIO.ADMIT && opNueva != OPERACIO.ERROR){
                                throw new ProtocolException(3);
                            }
                            break;
                        case ERROR:
                            break;
                    }
                }




            }

            /**
             * Método que lee la siguiente operación.
             * @throws ProtocolException
             * @throws IOException
             */
            public void readNextOp() throws ProtocolException, IOException {
                operacioActual = setOperacioActual(protocol.read_op_client());

            }

            /**
             * Método principal del cliente. Es la ejecución del intercambio de información con el
             * servidor.
             */
            public void RunClient() {
                try {

                    if(automaticDirecte){
                        iniciPartidaAuto();
                    }
                    else
                    {
                        iniciPartida();
                    }
                    nextSelected = false;


                    while(socket.isConnected() && !socket.isClosed()){
                        if(nextSelected == false){
                            try{
                                readNextOp();
                                System.out.println(operacioActual);
                                nextSelected = true;
                            }catch (ProtocolException ex){
                                protocol.write_error(ex.errorProtocol.getErrorCode(), ex.errorProtocol.getErrorMessage());
                            }
                        }
                        if(operacioActual != null){

                        }

                        switch (operacioActual){
                            case READY:
                                ReadyReceived();
                                nextSelected = false;
                                break;
                            case ADMIT:
                                AdmitRecived();
                                break;
                            case WORD:
                                WordReceived();
                                nextSelected = false;
                                break;
                            case RESULT:
                                ResultReceived();
                                nextSelected = false;
                                break;
                            case STATS:
                                StatsReceived();
                                nextSelected = false;
                                if(automaticDirecte)
                                {
                                    començarNovaPartidaAuto();
                                }
                                else
                                {
                                    començarNovaPartida();
                                }
                                break;
                            case ERROR:
                                ErrorReceived();
                                nextSelected = false;
                                break;
                        }
                    }
                } catch (ComUtilsException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } finally {

                }

            }




            /**
             * Método que se encarga de recibir el mensaje de READY del servidor y de escribir
             * como contestación un mensaje de PLAY.
              * @throws IOException
             * @throws ComUtilsException
             */
            private void ReadyReceived() throws IOException, ComUtilsException {
                int ready = protocol.read_ready();
                idSeason = ready;
                protocol.write_play(idSeason);
            }

            /**
             * Método que recibe los stats del servidor y los procesa. Devolverá un mensaje de ERROR
             * en caso de ser encesario.
             * @throws ComUtilsException
             * @throws IOException
             */
            private void StatsReceived() throws ComUtilsException, IOException {
                String statsUnparsed = protocol.read_stats();
                try{
                    try{
                        JSONParser parser = new JSONParser();
                        JSONObject stats = (JSONObject) parser.parse(statsUnparsed);
                        if(stats==null)
                        {
                            throw new ProtocolException(6);
                        }
                        else
                        {
                            System.out.println(statsUnparsed);
                        }
                    }catch (ParseException ex){
                        throw new ProtocolException(6);
                    }
                }catch (ProtocolException ex){
                    protocol.write_error(ex.errorProtocol.getErrorCode(), ex.errorProtocol.getErrorMessage());
                }



            }



            /**
             *  Método encargado de recibir un mensaje de WORD. Devolverá un mensaje de ERROR en caso
             *  de ser necesario.
             * @throws ComUtilsException
             * @throws IOException
             */
            private void WordReceived() throws ComUtilsException, IOException {
                try{
                    String word = protocol.read_word();
                    System.out.println(word + "\n");

                }catch (ProtocolException ex){
                    protocol.write_error(ex.errorProtocol.getErrorCode(), ex.errorProtocol.getErrorMessage());
                }
            }

            /**
             *  Método encargado de recibir un mensaje de ADMIT y controla si la partida la juega el
             *  usuario o el client automàtic.
             * @throws ProtocolException
             * @throws IOException
             * @throws ComUtilsException
             */
            private void AdmitRecived() throws ProtocolException, IOException, ComUtilsException {
                diccionari = new HashSet<>();
                diccionariReduit = new HashSet<>();
                boolean admited = protocol.read_admit();
                if(admited){
                    if(!clientAutomaticBool)
                    {
                        while (line.length() != 5 && line.matches("^[a-zA-Z]*$")) {
                            System.out.println("Escriu una paraula de 5 lletres o posa exit per sortir.\n");
                            line = sc.nextLine();
                            if (line.equals("exit")) {
                                socket.close();

                            }
                        }
                    }
                    else
                    {
                        initClientAutomatic();
                        line = "REINA";
                        lastWord = line;
                    }
                    if(!socket.isClosed()){
                        protocol.write_word(line);
                        line = "";
                    }
                    nextSelected = false;
                }else{
                    System.out.println("DESCONNECTAT\n");

                    socket.close();
                }
            }

            /**
             *  Método encargado de recibir un mensaje de RESULT. Controla si la partida la juega el usuario
             *  o el client automàtic. En caso de no finalizar la partida se mandará un mensaje de WORD.
             * @throws ProtocolException
             * @throws IOException
             * @throws ComUtilsException
             */
            private void ResultReceived() throws ProtocolException, IOException, ComUtilsException {
                String result = protocol.read_result();

                comptaIntents++;
                if(result.equals("^^^^^"))
                {
                    System.out.println(result + "\n");
                }
                else
                {
                    if(comptaIntents < 6)
                    {
                        if(!clientAutomaticBool) {
                            while (line.length() != 5) {
                                System.out.println(result + "\n");
                                System.out.println("Escriu una paraula de 5 lletres \n");
                                line = sc.nextLine();
                            }
                        }
                        else {
                            analyzeResult(result);
                            line = clientAutomatic();
                            lastWord = line;
                        }
                        protocol.write_word(line);
                        line = "";
                    }

                }
            }

            /**
             * Método que recibe un mensaje de ERROR. Distingue qué tipo de error es entre todas las opciones
             * y decide si se vuelve a preguntar la palabra al usuario, se reinicia la partida o se cierra el
             * socket.
             * @throws IOException
             * @throws ProtocolException
             * @throws ComUtilsException
             */
            private void ErrorReceived() throws IOException, ProtocolException, ComUtilsException {
                List<Object> error = protocol.read_error();
                errorReceived = new ErrorProtocol((Integer) error.get(0));
                if(!errorReceived.getErrorMessage().equals(error.get(1))){

                }
                else
                {


                }
                System.out.println(errorReceived.getErrorMessage());
                switch (errorReceived.getErrorCode()) {
                    case 1: //CARACTER NO RECONEGUT
                    case 5: //PARAULA DESCONEGUDA
                        if (!clientAutomaticBool){
                            while (line.length() != 5) {
                                System.out.println("Escriu una paraula de 5 lletres \n");
                                line = sc.nextLine();
                            }
                        }
                        else {line = clientAutomatic(); lastWord = line;}
                        protocol.write_word(line);
                        line = "";
                        break;
                    case 2: //MISSATGE DESCONEGUT
                    case 3: //MISSATGE FORA DE CONTROL
                    case 99: //ERROR DESCONEGUT
                    case 6: //MISSATGE MAL FORMAT
                        socket.close();
                        break;
                    case 4:
                        iniciPartida();
                        break;
                }
            }


            /**
             * Método que inicializa los hashes que se utilizarán en el client automàtic.
             */
            public void initClientAutomatic(){
                    charsContingutsEnParaulaSet = new HashSet<Character>();
                    charsNoContingutsEnParaulaSet = new HashSet<Character>();
                    charContingutEnPosicioIHash = new HashMap<Integer, Character>();
                    charNoContingutPosicioIHashContainingSet = new HashMap<Integer, Set<Character>>();
                    for (int i=0; i<5; i++){
                        charNoContingutPosicioIHashContainingSet.put(i, new HashSet<Character>());
                    }
                }

            /**
             * Método principal del client automàtic. Crea un diccionario reducido del que se
             * eligirá una palabra aleatoria que se mandará al servidor.
             * @return palabra candidata del client automàtic.
             * @throws IOException
             */
            public String clientAutomatic() throws IOException {
                if(diccionari.isEmpty())
                {
                    diccionari = diccionariComplet;
                }
                else
                {
                    diccionari = diccionariReduit;
                }
                crearDiccionariReduit();

                String paraulaCandidata = null;
                int size = diccionariReduit.size();
                int item = new Random().nextInt(size);
                int i = 0;
                for(String str : diccionariReduit) {
                    if (i == item){
                        paraulaCandidata = (String) str;
                        break;
                    }
                    i++;
                }
                System.out.println("La paraula candidata es: " + paraulaCandidata + "\n");
                return paraulaCandidata;//TODO ¿Qué devuelvo si no encuentra ninguna?
            }





            /**
             * Método que crea el diccionario reducido. Introduce en este diccioanrio
             * únicamente las palabras que sigan la estructura que han ido dictando los
             * mensajes de RESULT del servidor.
             * @throws IOException
             */
            private void crearDiccionariReduit() throws IOException
            {
                Set<String> fora = new HashSet<>();
                String paraulaCandidata;
                for(Object obj : diccionari)
                {
                    paraulaCandidata = (String) obj;
                    if(checkIfWordIsValid(paraulaCandidata)) {
                        diccionariReduit.add(paraulaCandidata);
                    }else{
                        if(diccionariReduit.contains(paraulaCandidata)){
                            fora.add(paraulaCandidata);
                        }
                    }
                }

                diccionariReduit.removeAll(fora);
            }

            /**
             *  Chequea si la palabra elegida cumple las condicciones impuestas por el servidor.
             * @param paraulaCandidata palabra candidata
             * @return
             */
            private boolean checkIfWordIsValid(String paraulaCandidata){
                for (int i=0; i<5; i++){
                    char c = paraulaCandidata.charAt(i);
                    if (charsNoContingutsEnParaulaSet.contains(c)){
                        return false;
                    }
                    else if(charNoContingutPosicioIHashContainingSet.get(i).contains(c)){
                        return false;
                    }
                    else if(charContingutEnPosicioIHash.containsKey(i) && charContingutEnPosicioIHash.get(i)!=c){
                        return false;
                    }
                }
                for(char c : charsContingutsEnParaulaSet){
                    if (!paraulaCandidata.contains(String.valueOf(c))){
                        return false;
                    }
                }
                return true;
            }

            /**
             * Deduce las condiciones que ha impuesto el servidor con el último mensaje de RESULT
             * para luego aplicarlas a las palabras contenidas en el diccioanrio reducido.
             * @param result String del mensaje RESULT
             */
            public void analyzeResult(String result){
                for(int i = 0; i<5; i++){
                    char c = result.charAt(i);
                    char charLastWord = lastWord.charAt(i);
                    if(c == '?'){
                        charsContingutsEnParaulaSet.add(charLastWord);
                        charNoContingutPosicioIHashContainingSet.get(i).add(charLastWord);
                    }else if(c == '^'){
                        charContingutEnPosicioIHash.put(i, charLastWord);
                    }else{
                        int countMatches = lastWord.length() - lastWord.replaceAll(String.valueOf(charLastWord),"").length();
                        if (countMatches > 1) {
                            boolean charInWord = false;
                            int index = lastWord.indexOf(charLastWord);
                            while (index >= 0) {
                                if (index >=0 && result.charAt(index)!='*'){
                                    charInWord = true;
                                }
                                index = lastWord.indexOf(charLastWord, index + 1);
                            }
                            if (!charInWord) {
                                charsNoContingutsEnParaulaSet.add(charLastWord);
                            }
                        }else{
                            charsNoContingutsEnParaulaSet.add(charLastWord);
                        }
                    }
                }
            }

            /**
             * Método que se llama al inicializar una partida. Guarda los parámetros seleccionados por
             * el jugador, que decidirán como se jugará la partida.
             * @throws ComUtilsException
             * @throws IOException
             */
            public void iniciPartida() throws ComUtilsException, IOException {
                while (!line.equalsIgnoreCase("s") && !line.equalsIgnoreCase("n")){
                    System.out.println("Vols jugar una partida? s/n \n");
                    line = sc.nextLine();
                }
                if(line.equalsIgnoreCase("s")){
                    line = "";
                    System.out.println("Vols seguir amb la sessió anterior? La qual té: \n");
                    System.out.println("ID = "+idSeason+"\n");
                    System.out.println("Nom = "+name+"\n");
                    while (!line.equalsIgnoreCase("s") && !line.equalsIgnoreCase("n")){
                        System.out.println("s/n\n");
                        line = sc.nextLine();
                    }
                    if(line.equalsIgnoreCase("n")){
                        System.out.println("Escriu una ID: \n");
                        line = sc.nextLine();
                        while(!line.matches("[+-]?\\d*(\\.\\d+)?")){
                            System.out.println("Aixo no es un nombre\n, Escriu una ID: \n");
                            line = sc.nextLine();
                        }
                        idSeason = Integer.parseInt(line);
                        System.out.println("Escriu un nom: \n");
                        name = sc.nextLine();

                    }
                    System.out.println("Comunicant amb el Servidor amb: \n");
                    System.out.println("ID = "+idSeason+"\n");
                    System.out.println("Nom = "+name+"\n");

                    protocol.write_hello(idSeason, name);

                    System.out.println("Vols jugar en mode manual o prefereixes observar com ho fa la nostre IA? m/a \n");
                    line = sc.nextLine();
                    while (!line.equalsIgnoreCase("a") && !line.equalsIgnoreCase("m")) {
                        System.out.println("a/o \n");
                        line = sc.nextLine();
                    }
                    if(line.equalsIgnoreCase("a")){
                        clientAutomaticBool = true;
                    }else{
                        clientAutomaticBool = false;
                    }
                }else{
                    System.out.println("DESCONNECTAT\n");
                    socket.close();
                }
            }



            /**
             * Método que se llama al iniciar la partida con el client automàtic. Pone el idSeason a cero
             * y manda un mensaje de HELLO.
             * @throws ComUtilsException
             * @throws IOException
             */
            private void iniciPartidaAuto() throws ComUtilsException, IOException {
                clientAutomaticBool = true;
                idSeason = 0;
                name = "autoClientNachoGerardIA";
                protocol.write_hello(idSeason, name);
            }

            /**
             * Método que se llama al finalizar la partida con el client automàtic. Resetea los
             * intentos y manda un mensaje de PLAY.
             * @throws ComUtilsException
             * @throws IOException
             */
            private void començarNovaPartidaAuto() throws ComUtilsException, IOException {
                comptaIntents = 0;
                protocol.write_play(idSeason);
                nextSelected = false;
            }

            /**
             * Método que se llama al comenzae una nueva partida. Manda unos logs al usuario con opciones
             * para que elija como jugar la siguiente partida.
             * @throws ComUtilsException
             * @throws IOException
             */
            private void començarNovaPartida() throws ComUtilsException, IOException {
                comptaIntents = 0;
                while (!line.equalsIgnoreCase("d") && !line.equalsIgnoreCase("m") && !line.equalsIgnoreCase("a")){
                    System.out.println("m ==> Juga una altre partida en mode manual \n");
                    System.out.println("a ==> Juga una altre partida en mode automàtic \n");
                    System.out.println("d ==> Desconnectat \n");

                    line = sc.nextLine();
                }
                if(line.equalsIgnoreCase("m") || line.equalsIgnoreCase("a")){
                    if(line.equalsIgnoreCase("m"))
                    {
                        clientAutomaticBool = false;
                    }
                    else
                    {
                        clientAutomaticBool = true;
                    }
                    System.out.println("Comunicant amb el Servidor amb: \n");
                    System.out.println("ID = "+idSeason+"\n");
                    System.out.println("Nom = "+name+"\n");

                    protocol.write_play(idSeason);
                    nextSelected = false;
                }
                else
                {
                    System.out.println("DESCONNECTAT\n");
                    socket.close();
                }
            }

        }

}
