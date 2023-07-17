package utils;

import ComUtilsExceptions.ComUtilsException;

import java.io.*;

public class ComUtils {
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private int MAX_SIZE = 1024;

    public ComUtils(InputStream inputStream, OutputStream outputStream) throws IOException {
        dataInputStream = new DataInputStream(inputStream);
        dataOutputStream = new DataOutputStream(outputStream);
    }

    /**
     * @author Gerard
     *
     * @return Retorna un byte.
     *
     * */
    public int read_byte() throws IOException {

        byte byteReaded = dataInputStream.readByte();

        return byteReaded;
    }

    /**
     * @author Gerard
     *
     * @param number Numero que quieres transformar a byte.
     *
     * */
    public void write_byte(int number) throws IOException, ComUtilsException {

        if(0 <= number && number < 256){
            dataOutputStream.write(number);
        }
        else{
            throw new ComUtilsException("El número introducido no esta en el rango de 0 a 255, i.e. no es un byte");
        }
    }


    /**
     * @author Gerard
     *
     * @return Numero leído en formato BigEndian
     *
     * */
    public int read_int32() throws IOException {
        byte bytes[] = read_bytes(4);

        return bytesToInt32(bytes,Endianness.BIG_ENNDIAN);
    }

    /**
     * @author Gerard
     *
     * @param number Numero a escribir en formato BigEndian
     *
     * */
    public void write_int32(int number) throws IOException {
        byte bytes[] = int32ToBytes(number, Endianness.BIG_ENNDIAN);

        dataOutputStream.write(bytes, 0, 4);
    }

    /**
     *
     * @author Nacho
     *
     *
     * @return String leida
     * */
    public String read_string() throws IOException, ComUtilsException {

        String result;
        byte[] bStr = new byte[MAX_SIZE];


        int i = 0;
        byte control = 0x01;

        while(control != 0x00) {
            control = dataInputStream.readByte();
            if(control != 0x00){
                bStr[i] = control;
            }
            i++;
        }

        if(control != 0x00){
            throw new ComUtilsException("No hay un 0 despues de la String");
        }
        i = i - 1;
        char[] cStr = new char[i];
        for(int j = 0; j < i;j++)
            cStr[j]= (char) bStr[j];



        result = String.valueOf(cStr);
        return result.trim();

    }

    /**
     *
     * @author Gerard
     *
     *
     * @return String leida de 5 bytes y sin un 0 al final.
     * */
    public String read_string_5bytes() throws IOException {
        int  maxSize = 5;
        String result;
        byte[] bStr = new byte[maxSize];


        int i = 0;

        while(i<maxSize) {
            bStr[i] = dataInputStream.readByte();
            i++;
        }

        char[] cStr = new char[i];
        for(int j = 0; j < i;j++)
            cStr[j]= (char) bStr[j];



        result = String.valueOf(cStr);
        return result.trim();
    }



    /**
     * @author Gerard
     * @param str String que se quiere escribir en el dataOutputStream.
     * */
    public void write_string(String str, boolean variable) throws IOException, ComUtilsException {

        /*
         *
         *  Metodo de dataOutputStream que escribe una String de manera correcta. (Abajo implementación).
         *
         *  int len = s.length();
         *  for (int i = 0 ; i < len ; i++) {
         *      out.write((byte)s.charAt(i));
         *  }
         *  incCount(len);
         *
         * */
        dataOutputStream.writeBytes(str);

        if(variable){
            //EScribimos un 0 despues del String porque en nuestro protocolo los Strings variables siempre tienen que llevar un byte 0.
            write_byte(0);
        }

    }

    private byte[] int32ToBytes(int number, Endianness endianness) {
        byte[] bytes = new byte[4];

        if(Endianness.BIG_ENNDIAN == endianness) {
            bytes[0] = (byte)((number >> 24) & 0xFF);
            bytes[1] = (byte)((number >> 16) & 0xFF);
            bytes[2] = (byte)((number >> 8) & 0xFF);
            bytes[3] = (byte)(number & 0xFF);
        }
        else {
            bytes[0] = (byte)(number & 0xFF);
            bytes[1] = (byte)((number >> 8) & 0xFF);
            bytes[2] = (byte)((number >> 16) & 0xFF);
            bytes[3] = (byte)((number >> 24) & 0xFF);
        }
        return bytes;
    }

    /* Passar de bytes a enters */
    private int bytesToInt32(byte bytes[], Endianness endianness) {
        int number;

        if(Endianness.BIG_ENNDIAN == endianness) {
            number=((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
                    ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        }
        else {
            number=(bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) |
                    ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
        }
        return number;
    }

    //llegir bytes.
    private byte[] read_bytes(int numBytes) throws IOException {
        int len = 0;
        byte bStr[] = new byte[numBytes];
        int bytesread = 0;
        do {
            bytesread = dataInputStream.read(bStr, len, numBytes-len);
            if (bytesread == -1)
                throw new IOException("Broken Pipe");
            len += bytesread;
        } while (len < numBytes);
        return bStr;
    }

    /**
     *
     * @author Nacho
     *
     * @return El numero de posiciones restantes para leer en el dataInputStream
     *
     * */
    private int number_bytes_existing() throws IOException {
        return dataInputStream.available();
    }

    public enum Endianness {
        BIG_ENNDIAN,
        LITTLE_ENDIAN
    }


    /**
     * @author Gerard
     *
     * @param result String de entrada que tiene que tener el formato de resultado.
     */
    public boolean checkResult(String result){

        boolean check = true;

        if(result.length() == 5){
            for (char i:result.toCharArray())
            {
                if (i != '*' && i != '?' && i != '^'){
                    check = false;
                }
            }
            return check;
        }
        else
        {
            return false;
        }

    }




}

