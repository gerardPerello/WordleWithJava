import ComUtilsExceptions.ProtocolException;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ServerTest {

    @Mock
    Socket client;

    Server server;
    Server.ClientConnected clientSock;
    public void initServerTest() throws IOException {
        server = new Server();
        clientSock = new Server.ClientConnected(client);
    }

    @Test
    public void test_creating_file() throws IOException {
        String s = System.getProperty("user.dir");
        s = s + "/stats_db";
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
        String filePath = s+"/stats.json";
        FileWriter file = new FileWriter(filePath, true);

        JSONObject jsonStats = new JSONObject();
        jsonStats.put("Jugades",1);
        jsonStats.put("Exits %",2);
        jsonStats.put("Ratxa Actual",3);
        jsonStats.put("Ratxa Maxima",4);
        jsonStats.put("Vicories",5);
        System.out.println(jsonStats);
        try {

            file.write(jsonStats.toJSONString());
            file.write(",");
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
            file.flush();
            file.close();
        }

    }

    @Test
    public void cargarDiccionarioTest() throws IOException {
        server.cargarDiccionari();
        assertTrue(!server.diccionari.isEmpty());
        assertTrue(server.diccionari.contains("REINA"));
    }

    @Test
    public void setOperacionActualTest_OperacionErronea() {
        try {
            initServerTest();
            clientSock.setOperacioActual(0);
        }catch(ProtocolException e){
            assertTrue(e.getMessage().contains("MISSATGE DESCONEGUT"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setOperacionActualTest_OperacionActualNula() throws IOException, ProtocolException {
        try {
            initServerTest();
            clientSock.operacioActual = null;
            clientSock.setOperacioActual(2);
        }catch(ProtocolException e){
            assertTrue(e.getMessage().contains("MISSATGE FORA DE PROTOCOL"));
        }
    }

    @Test
    public void setOperacionActualTest_OperacionActualNoEsCorrecta() throws IOException, ProtocolException {
        initServerTest();
        ArrayList<Integer> operationsHello = new ArrayList<>();
        operationsHello.add(2);
        operationsHello.add(4);
        operationsHello.add(5);
        operationsHello.add(6);
        operationsHello.add(7);

        clientSock.operacioActual = OPERACIO.HELLO;
        for(int i=1; i<operationsHello.size(); i++) {
            try {
                clientSock.setOperacioActual(operationsHello.get(i));
            } catch (ProtocolException e) {
                assertTrue(e.getMessage().contains("MISSATGE FORA DE PROTOCOL"));
            }
        }

        ArrayList<Integer> operationsReady = new ArrayList<>();
        operationsReady.add(1);
        operationsReady.add(2);
        operationsReady.add(4);
        operationsReady.add(6);
        operationsReady.add(7);

        clientSock.operacioActual = OPERACIO.PLAY;
        for(int i=1; i<operationsReady.size(); i++) {
            try {
                clientSock.setOperacioActual(operationsReady.get(i));
            } catch (ProtocolException e) {
                assertTrue(e.getMessage().contains("MISSATGE FORA DE PROTOCOL"));
            }
        }

        ArrayList<Integer> operationsWord = new ArrayList<>();
        operationsWord.add(2);
        operationsWord.add(4);
        operationsWord.add(6);
        operationsWord.add(7);
        clientSock.operacioActual = OPERACIO.WORD;
        for(int i=1; i<operationsWord.size(); i++) {
            try {
                clientSock.setOperacioActual(operationsWord.get(i));
            } catch (ProtocolException e) {
                assertTrue(e.getMessage().contains("MISSATGE FORA DE PROTOCOL"));
            }
        }
    }

    @Test
    public void isWinTest() throws IOException {
        initServerTest();
        clientSock.resultWord = "^^^^^";
        assertTrue(clientSock.esVictoria());
        clientSock.resultWord = "^*^^^";
        assertFalse(clientSock.esVictoria());
    }

    @Test
    public void checkWordTest() throws IOException, ProtocolException {
        initServerTest();
        Set<String> stringSet = new HashSet<>();
        stringSet.add("CLEDA");
        stringSet.add("CLICA");
        stringSet.add("ABDIC");
        stringSet.add("GABRE");
        stringSet.add("CLAVO");
        stringSet.add("MUSEM");
        stringSet.add("PILLA");
        server.diccionari = stringSet;

        clientSock.clientWord = "CLEDA";
        clientSock.secretWord = "CLICA";
        clientSock.checkWord();
        String result = clientSock.resultWord;
        assertEquals(result, "^^**^");

        clientSock.clientWord = "ABDIC";
        clientSock.secretWord = "PILLA";
        clientSock.checkWord();
        String result2 = clientSock.resultWord;
        assertEquals(result2, "?**?*");
    }

    @Test
    public void chooseRandomWordTest() throws IOException {
        initServerTest();

        Set<String> stringSet = new HashSet<>();
        stringSet.add("CLEDA");
        stringSet.add("CLICA");
        stringSet.add("ABDIC");
        stringSet.add("GABRE");
        stringSet.add("CLAVO");
        stringSet.add("MUSEM");
        stringSet.add("PILLA");
        server.diccionari = stringSet;

        clientSock.chooseRandomWord();
        assertFalse(clientSock.secretWord.isEmpty());
        clientSock.chooseRandomWord();
        assertFalse(clientSock.secretWord.isEmpty());
        clientSock.chooseRandomWord();
        assertFalse(clientSock.secretWord.isEmpty());
        clientSock.chooseRandomWord();
        assertFalse(clientSock.secretWord.isEmpty());
        clientSock.chooseRandomWord();
        assertFalse(clientSock.secretWord.isEmpty());
    }


}
