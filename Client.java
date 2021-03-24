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
            dout.write (HELO.getBytes());
            dout.flush();


            count = s.getInputStream().available();

            byteArray = new byte[count];
            charArray = new char[count];

            for(int i = 0; i < count; i++) {
                charArray[i] = (char)byteArray[i];
            }

            inputStreamString = new String(charArray);

            System.out.println (inputStreamString);

            if(inputStreamString.equals(OK)) {
                System.out.println("sent AUTH username");
                dout.write(AUTH_username.getBytes());
                dout.flush();
            }
            else {
                throw new Exception("Server did not respond with OK");
            }

            // readXML ();
            System.out.println("read XML and send REDY");
            dout.write (REDY.getBytes());
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