package Logger;

import ComUtilsExceptions.ErrorProtocol;
import Utils.Logger;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoggerTest {

    Logger logger;
    String threadName;
    public void initLogger() throws IOException {
        threadName = "TESTING";
        logger = new Logger(threadName);
    }

    @Test
    public void clientSocketTest() throws IOException {
        initLogger();

        String _rutaLog = System.getProperty("user.dir") + "/log" + "/Server_" + threadName + ".log";
        logger.clientSocketLog(true);
        logger.clientSocketLog(false);
        FileReader reader = new FileReader(_rutaLog);
        BufferedReader inStream = new BufferedReader(reader);

        String checkLine;
        String trueLine = null;
        String falseLine =null;
        do {
            checkLine = inStream.readLine();
            if(checkLine != null){
                trueLine = checkLine;
            }
            checkLine = inStream.readLine();
            if(checkLine != null){
                falseLine = checkLine;
            }
        }
        while (checkLine != null);
        reader.close();

        assertTrue(trueLine.equals("C- [TCP Connect]") && falseLine.equals("C- [conexion closed]")
        || trueLine.equals("C- [conexion closed]") && falseLine.equals("C- [TCP Connect]"));
    }

    @Test
    public void serverSocketTest() throws IOException {
        initLogger();

        String _rutaLog = System.getProperty("user.dir") + "/log" + "/Server_" + threadName + ".log";
        logger.serverSocketLog(true);
        logger.serverSocketLog(false);
        FileReader reader = new FileReader(_rutaLog);
        BufferedReader inStream = new BufferedReader(reader);

        String checkLine;
        String trueLine = null;
        String falseLine =null;
        do {
            checkLine = inStream.readLine();
            if(checkLine != null){
                trueLine = checkLine;
            }
            checkLine = inStream.readLine();
            if(checkLine != null){
                falseLine = checkLine;
            }
        }
        while (checkLine != null);
        reader.close();

        assertTrue(trueLine.equals("S- [TCP Accept]") && falseLine.equals("S- [conexion closed]")
                || trueLine.equals("S- [conexion closed]") && falseLine.equals("S- [TCP Accept]"));
    }

    public String lineReturn(String _rutaLog) throws IOException {
        FileReader reader = new FileReader(_rutaLog);
        BufferedReader inStream = new BufferedReader(reader);

        String line = null;
        String checkLine = null;
        do {
            checkLine = inStream.readLine();
            if(checkLine != null){
                line = checkLine;
            }
        }
        while (checkLine != null);
        reader.close();

        return line;
    }

    @Test
    public void HELLO_test() throws IOException {
        initLogger();

        String _rutaLog = System.getProperty("user.dir") + "/log" + "/Server_" + threadName + ".log";
        logger.HELLO(1, "Nacho");
        assertEquals(lineReturn(_rutaLog), "HELLO   C -------1 1 Nacho 1------------> S");
    }

    @Test
    public void READY_test() throws IOException {
        initLogger();

        String _rutaLog = System.getProperty("user.dir") + "/log" + "/Server_" + threadName + ".log";
        logger.READY(1);
        assertEquals(lineReturn(_rutaLog), "READY   C <------2 1--------------------- S");
    }

    @Test
    public void PLAY_test() throws IOException {
        initLogger();

        String _rutaLog = System.getProperty("user.dir") + "/log" + "/Server_" + threadName + ".log";
        logger.PLAY(1);
        assertEquals(lineReturn(_rutaLog), "PLAY    C -------3 1--------------------> S");
    }

    @Test
    public void ADMIT_true_test() throws IOException {
        initLogger();

        String _rutaLog = System.getProperty("user.dir") + "/log" + "/Server_" + threadName + ".log";
        logger.ADMIT(true);
        assertEquals(lineReturn(_rutaLog), "ADMIT   C <------4 1--------------------- S");
    }

    @Test
    public void ADMIT_false_test() throws IOException {
        initLogger();

        String _rutaLog = System.getProperty("user.dir") + "/log" + "/Server_" + threadName + ".log";
        logger.ADMIT(false);
        assertEquals(lineReturn(_rutaLog), "ADMIT   C <------4 0--------------------- S");
    }

    @Test
    public void WORD_true_test() throws IOException {
        initLogger();

        String _rutaLog = System.getProperty("user.dir") + "/log" + "/Server_" + threadName + ".log";
        logger.WORD("Nacho", true);
        assertEquals(lineReturn(_rutaLog), "WORD    C <------5 Nacho----------------- S");
    }

    @Test
    public void WORD_false_test() throws IOException {
        initLogger();

        String _rutaLog = System.getProperty("user.dir") + "/log" + "/Server_" + threadName + ".log";
        logger.WORD("Nacho", false);
        assertEquals(lineReturn(_rutaLog), "WORD    C -------5 Nacho----------------> S");
    }

    @Test
    public void RESULT_test() throws IOException {
        initLogger();

        String _rutaLog = System.getProperty("user.dir") + "/log" + "/Server_" + threadName + ".log";
        logger.RESULT("Manzana");
        assertEquals(lineReturn(_rutaLog), "RESULT  C <------6 Manzana--------------- S");
    }

    @Test
    public void STATS_test() throws IOException {
        initLogger();

        String _rutaLog = System.getProperty("user.dir") + "/log" + "/Server_" + threadName + ".log";
        logger.STATS("Manzana");
        assertEquals(lineReturn(_rutaLog), "STATS   C <------7 Manzana--------------- S");
    }

    @Test
    public void ERROR_true_test() throws IOException {
        initLogger();

        String _rutaLog = System.getProperty("user.dir") + "/log" + "/Server_" + threadName + ".log";
        ErrorProtocol errorProtocol = new ErrorProtocol(1);
        logger.ERROR(errorProtocol, true);
        assertEquals(lineReturn(_rutaLog), "ERROR   C <------8 1 CARACTER NO RECONEGUT-------- S");
    }

    @Test
    public void ERROR_false_test() throws IOException {
        initLogger();

        String _rutaLog = System.getProperty("user.dir") + "/log" + "/Server_" + threadName + ".log";
        ErrorProtocol errorProtocol = new ErrorProtocol(1);
        logger.ERROR(errorProtocol, false);
        assertEquals(lineReturn(_rutaLog), "ERROR   C -------8 1 CARACTER NO RECONEGUT-------> S");
    }
}
