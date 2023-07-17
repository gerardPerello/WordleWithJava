package ProtocolTest;

import ComUtilsExceptions.ComUtilsException;
import ComUtilsExceptions.ProtocolException;
import ProtocolClient.ProtocolClient;
import ProtocolServer.ProtocolServer;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProtocolTest {

    @Test
    public void write_hello_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            ProtocolClient protocolClient  = new ProtocolClient(new FileInputStream(file), new FileOutputStream(file));
            ProtocolServer protocolServer  = new ProtocolServer(new FileInputStream(file), new FileOutputStream(file));
            protocolClient.write_hello(2456,"ObiwanKenovi");
            int op = protocolServer.read_op_server();
            List<Object> message = protocolServer.read_hello();
            assertEquals(op,1);
            assertEquals(message.get(0),2456);
            assertEquals(message.get(1),"ObiwanKenovi");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ComUtilsException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void write_ready_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            ProtocolClient protocolClient  = new ProtocolClient(new FileInputStream(file), new FileOutputStream(file));
            ProtocolServer protocolServer  = new ProtocolServer(new FileInputStream(file), new FileOutputStream(file));
            protocolServer.write_ready(2456);
            int op = protocolClient.read_op_client();
            int message = protocolClient.read_ready();
            assertEquals(op,2);
            assertEquals(message,2456);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ComUtilsException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void write_play_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            ProtocolClient protocolClient  = new ProtocolClient(new FileInputStream(file), new FileOutputStream(file));
            ProtocolServer protocolServer  = new ProtocolServer(new FileInputStream(file), new FileOutputStream(file));
            protocolClient.write_play(2456);
            int op = protocolServer.read_op_server();
            int message = protocolServer.read_play();
            assertEquals(op,3);
            assertEquals(message,2456);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ComUtilsException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void write_admit_test() throws ComUtilsException {
        File file = new File("test");
        try {
            file.createNewFile();
            ProtocolClient protocolClient  = new ProtocolClient(new FileInputStream(file), new FileOutputStream(file));
            ProtocolServer protocolServer  = new ProtocolServer(new FileInputStream(file), new FileOutputStream(file));
            protocolServer.write_admit(true);
            int op = protocolClient.read_op_client();
            boolean admit = protocolClient.read_admit();
            assertEquals(op,4);
            assertEquals(admit,true);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void write_word_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            ProtocolClient protocolClient  = new ProtocolClient(new FileInputStream(file), new FileOutputStream(file));
            ProtocolServer protocolServer  = new ProtocolServer(new FileInputStream(file), new FileOutputStream(file));
            protocolClient.write_word("movie");
            int op = protocolServer.read_op_server();
            String message = protocolServer.read_word();
            assertEquals(op,5);
            assertEquals(message,"movie");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ComUtilsException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void write_result_test() throws ComUtilsException {
        File file = new File("test");
        try {
            file.createNewFile();
            ProtocolClient protocolClient  = new ProtocolClient(new FileInputStream(file), new FileOutputStream(file));
            ProtocolServer protocolServer  = new ProtocolServer(new FileInputStream(file), new FileOutputStream(file));
            protocolServer.write_result("*****");
            int op = protocolClient.read_op_client();
            String message = protocolClient.read_result();
            assertEquals(op,6);
            assertEquals(message,"*****");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void write_stats_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            ProtocolClient protocolClient  = new ProtocolClient(new FileInputStream(file), new FileOutputStream(file));
            ProtocolServer protocolServer  = new ProtocolServer(new FileInputStream(file), new FileOutputStream(file));
            protocolServer.write_stats("{\"name\":\"Nacho\", \"age\":30, \"car\":null}");
            int op = protocolClient.read_op_client();
            String stats = protocolClient.read_stats();
            assertEquals(op,7);
            assertEquals(stats,"{\"name\":\"Nacho\", \"age\":30, \"car\":null}");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ComUtilsException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void write_error_test() throws ComUtilsException {
        File file = new File("test");
        try {
            file.createNewFile();
            ProtocolClient protocolClient  = new ProtocolClient(new FileInputStream(file), new FileOutputStream(file));
            ProtocolServer protocolServer  = new ProtocolServer(new FileInputStream(file), new FileOutputStream(file));
            protocolServer.write_error(1, "CARACTER NO RECONEGUT");
            int op = protocolClient.read_op_client();
            List<Object> message = protocolClient.read_error();
            assertEquals(op, 8);
            assertEquals(message.get(0),1);
            assertEquals(message.get(1),"CARACTER NO RECONEGUT");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }
}