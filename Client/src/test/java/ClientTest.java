import ComUtilsExceptions.ComUtilsException;
import ComUtilsExceptions.ProtocolException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.Socket;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientTest {
    Client.ClientConnected client;

    @Mock
    Socket socket;
    public void initClient() throws IOException {
        String inputPlayGame = "n\n";
        InputStream inP = new ByteArrayInputStream(inputPlayGame.getBytes());
        System.setIn(inP);

        client = new Client.ClientConnected("Gerard","localhost",1234, false);
        client.socket = socket;
    }
    @Test(expected = Test.None.class)//no exception expected
    public void iniciPartidaStreamTest() throws ComUtilsException, IOException {
        initClient();

        String inputPlayGame = "s\n";
        String inputContinueSession = "s\n";
        String inputQuieresAdivinar = "a\n";

        InputStream inP = new ByteArrayInputStream(inputPlayGame.getBytes());
        InputStream inC = new ByteArrayInputStream(inputContinueSession.getBytes());
        InputStream inA = new ByteArrayInputStream(inputQuieresAdivinar.getBytes());

        List<InputStream> streams = Arrays.asList(inP, inC, inA);
        InputStream in = new SequenceInputStream(Collections.enumeration(streams));

        System.setIn(in);

        client.iniciPartida();
    }

    @Test
    public void setOperacionActualTest_OperacionErronea() throws IOException {
        initClient();
        try {
            client.setOperacioActual(0);
        }catch(ProtocolException e){
            assertTrue(e.getMessage().contains("MISSATGE DESCONEGUT"));
        }
    }

    @Test
    public void setOperacionActualTest_OperacionActualNula() throws IOException {
        initClient();
        client.operacioActual = null;
        try {
            client.setOperacioActual(1);
        }catch(ProtocolException e){
            assertTrue(e.getMessage().contains("MISSATGE FORA DE PROTOCOL"));
        }
    }

    @Test
    public void setOperacionActualTest_OperacionActualNoEsCorrecta() throws IOException, ProtocolException {
        initClient();
        ArrayList<Integer> operationsReady = new ArrayList<>();
        operationsReady.add(1);
        operationsReady.add(2);
        operationsReady.add(3);
        operationsReady.add(5);
        operationsReady.add(6);
        operationsReady.add(7);
        for(int i=1; i<operationsReady.size(); i++) {
            try {
                client.operacioActual = OPERACIO.READY;
                client.setOperacioActual(operationsReady.get(i));
            } catch (ProtocolException e) {
                assertTrue(e.getMessage().contains("MISSATGE FORA DE PROTOCOL"));
            }
        }

        ArrayList<Integer> operationsAdmit = new ArrayList<>();
        operationsAdmit.add(1);
        operationsAdmit.add(2);
        operationsAdmit.add(3);
        operationsAdmit.add(4);
        operationsAdmit.add(5);
        operationsAdmit.add(7);
        for(int i=1; i<operationsAdmit.size(); i++) {
            try {
                client.operacioActual = OPERACIO.ADMIT;
                client.setOperacioActual(operationsAdmit.get(i));
            } catch (ProtocolException e) {
                assertTrue(e.getMessage().contains("MISSATGE FORA DE PROTOCOL"));
            }
        }

        ArrayList<Integer> operationsWord = new ArrayList<>();
        operationsWord.add(1);
        operationsWord.add(2);
        operationsWord.add(3);
        operationsWord.add(5);
        operationsWord.add(6);
        operationsWord.add(7);
        for(int i=1; i<operationsWord.size(); i++) {
            try {
                client.operacioActual = OPERACIO.WORD;
                client.setOperacioActual(operationsWord.get(i));
            } catch (ProtocolException e) {
                assertTrue(e.getMessage().contains("MISSATGE FORA DE PROTOCOL"));
            }
        }

        ArrayList<Integer> operationsResult = new ArrayList<>();
        operationsResult.add(1);
        operationsResult.add(2);
        operationsResult.add(3);
        operationsResult.add(4);
        operationsResult.add(5);
        for(int i=1; i<operationsResult.size(); i++) {
            try {
                client.operacioActual = OPERACIO.RESULT;
                client.setOperacioActual(operationsResult.get(i));
            } catch (ProtocolException e) {
                assertTrue(e.getMessage().contains("MISSATGE FORA DE PROTOCOL"));
            }
        }

        ArrayList<Integer> operationsStats = new ArrayList<>();
        operationsStats.add(1);
        operationsStats.add(2);
        operationsStats.add(3);
        operationsStats.add(5);
        operationsStats.add(7);
        for(int i=1; i<operationsStats.size(); i++) {
            try {
                client.operacioActual = OPERACIO.STATS;
                client.setOperacioActual(operationsStats.get(i));
            } catch (ProtocolException e) {
                assertTrue(e.getMessage().contains("MISSATGE FORA DE PROTOCOL"));
            }
        }

        ArrayList<Integer> operationsError = new ArrayList<>();
        operationsError.add(1);
        operationsError.add(2);
        operationsError.add(3);
        operationsError.add(4);
        operationsError.add(5);
        for(int i=1; i<operationsError.size(); i++) {
            try {
                client.operacioActual = OPERACIO.ERROR;
                client.setOperacioActual(operationsError.get(i));
            } catch (ProtocolException e) {
                assertTrue(e.getMessage().contains("MISSATGE FORA DE PROTOCOL"));
            }
        }
    }

    @Test
    public void clientAutomaticTest() throws IOException {
        initClient();
        Set<String> stringSet = new HashSet<>();
        stringSet.add("ABDIC");
        stringSet.add("GABRE");
        stringSet.add("CLAVO");
        stringSet.add("MUSEM");
        stringSet.add("PILLA");
        client.initClientAutomatic();
        client.charsNoContingutsEnParaulaSet.add('B');
        client.charsNoContingutsEnParaulaSet.add('C');
        client.charNoContingutPosicioIHashContainingSet.get(0).add('A');
        client.charContingutEnPosicioIHash.put(1, 'I');
        client.charsContingutsEnParaulaSet.add('A');

        client.diccionari = stringSet;
        client.diccionariReduit = client.diccionari;

        String palabraElegida = client.clientAutomatic();
        assertEquals(palabraElegida, "PILLA");
    }

    @Test
    public void analyzeResultTest() throws IOException {
        initClient();
        client.initClientAutomatic();
        client.lastWord = "CLAVO";//CLEDA
        client.analyzeResult("^^?**");

        assertTrue(!client.charsContingutsEnParaulaSet.isEmpty());
        assertTrue(client.charsNoContingutsEnParaulaSet.contains('O'));
        assertTrue(client.charsNoContingutsEnParaulaSet.contains('V'));
        assertTrue(client.charContingutEnPosicioIHash.get(0) == 'C');
        assertTrue(client.charContingutEnPosicioIHash.get(1) == 'L');
        assertTrue(client.charNoContingutPosicioIHashContainingSet.get(2).contains('A'));
    }
}
