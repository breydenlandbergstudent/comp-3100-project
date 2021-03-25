import java.net.*;
import java.io.*;

public class Client {
    // Socket s and args
    private static final String hostname = "localhost";
    private static final int serverPort = 50000;

    // streams

    // commands
    private static final String HELO = "HELO";
    private static final String OK = "OK";
    private static final String AUTH = "AUTH";
    private static final String username = "group_55";
    private static final String AUTH_username = AUTH + " " + username;
    private static final String REDY = "REDY";
    private static final String JOBN = "JOBN";
    private static final String NONE = "NONE";
    private static final String QUIT = "QUIT";

    // other fields
    private static int count; // will hold the current amount of "available" bytes from s.getInputStream()
    private static byte[] byteArray; // will hold the current message from the server stored as bytes
    private static char[] charArray; // will hold the current message from the server stored as chars (casted to char from the bytes in byteArray)
    private static String inputStreamString; // the String instance in which we will store the server's commands

    public static void main(String[] args) throws IOException {
        Socket s = new Socket(hostname, serverPort); // socket with host IP of 127.0.0.1 (localhost), server port of 50000
        DataInputStream din = new DataInputStream(s.getInputStream());
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());

        try {
            System.out.println("sent HELO");
            byte[] byteBuffer = HELO.getBytes();
            dout.write (byteBuffer);
            dout.flush();

            // server replies with OK

            System.out.println("sent AUTH username");
            byteBuffer = AUTH_username.getBytes();
            dout.write(byteBuffer);
            dout.flush();

            // replies with OK after printing

            Thread.sleep (3333);

            // readXML ();
            System.out.println("read XML and send REDY");
            byteBuffer = REDY.getBytes();
            dout.write(byteBuffer);
            dout.flush();

            // server sends JOBN

            System.out.println("receiving JOBN");

            din.skipBytes(OK.length() * 2); // skip the first two OK commands sent by server
            int count = din.available(); // get length of byte buffer from din's number of bytes that can be read

            byteBuffer = new byte[count];
            din.read(byteBuffer); // read from din into byte buffer
            char[] charBuffer = new char[count];

            // cast byte array into char array
            for(int i = 0; i < count; i++) {
                charBuffer[i] = (char)byteBuffer[i];
            }

            String stringBuffer = new String(charBuffer); // cast char array into String
            System.out.println(stringBuffer);

            while(!stringBuffer.contains(NONE)) {
                String[] fieldBuffer = stringBuffer.split(" "); // split String into array of strings (each string being a field of JOBN)
                for(String string : fieldBuffer) {
                    System.out.println(string);
                }

                // scheduling occurs

                // count = din.available();

                // byteBuffer = new byte[count];
                // din.read(byteBuffer);

                // charBuffer = new char[count];

                // // cast byte array into char array
                // for(int i = 0; i < count; i++) {
                // charBuffer[i] = (char)byteBuffer[i];
                // }

                // stringBuffer = new String(charBuffer); // cast char array into String
                // System.out.println(stringBuffer);
            }

            // make a method for the above

            // map String array to Job class values

            
            byteBuffer = QUIT.getBytes();
            dout.write(byteBuffer);
            dout.flush();

            dout.close();
            s.close();
        }
        catch(UnknownHostException e) {
            System.out.println("Unknown Host Exception: " + e.getMessage());
        }
        catch(EOFException e) {
            System.out.println("End of File Exception: " + e.getMessage());
        }
        catch(IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
        catch(Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}