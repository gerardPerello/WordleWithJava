package utilsTest;

import ComUtilsExceptions.ComUtilsException;
import jdk.jfr.StackTrace;
import org.junit.Test;
import static org.junit.Assert.*;
import utils.ComUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ComUtilsTest {

    @Test
    public void example_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            comUtils.write_int32(2);
            int readedInt = comUtils.read_int32();

            assertEquals(2, readedInt);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void bytesToInt32_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            comUtils.write_byte(37);
            int readedInt = comUtils.read_byte();
            byte numero2 = Byte.parseByte("00100101", 2); // De esta manera demostramos que esta en BIG_ENDIANN (si no, 37 sería 10100100)
            assertEquals(readedInt,numero2);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ComUtilsException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void read_write_byte_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            comUtils.write_byte(37);
            int readedInt = comUtils.read_byte();
            byte numero2 = Byte.parseByte("00100101", 2); // De esta manera demostramos que esta en BIG_ENDIANN (si no, 37 sería 10100100)
            assertEquals(readedInt,numero2);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ComUtilsException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void read_write_string() {
        File file = new File("test");
        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            String prueba = "Hola esto es un String de PRUEBA!!!!!!!!!!!!!!!!!!!!!!!";
            comUtils.write_string(prueba,true);
            String readedString = comUtils.read_string();
            assertEquals(prueba,readedString);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ComUtilsException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void read_write_int() {
        File file = new File("test");
        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            int prueba = 123456789;
            comUtils.write_int32(prueba);
            int readedInt= comUtils.read_int32();
            assertEquals(prueba,readedInt);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}