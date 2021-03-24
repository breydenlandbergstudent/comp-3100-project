import java.io.*;
import java.net.*;

public class Client {
    private static final String HELO = "HELO";
    private static final String AUTH = "AUTH group55";
    private static final String QUIT = "QUIT";

    public static void main(String[] args) {
        
        try {
            Socket s = new Socket("localhost", 50000); // initialise Socket

            DataOutputStream dout = new DataOutputStream(s.getOutputStream());

            /* HANDSHAKE PHASE */
            byte[] buffer = HELO.getBytes();
            dout.write(buffer); // send HELO to server
            dout.flush();

            buffer = AUTH.getBytes();
            dout.write(buffer); // send AUTH [username] to server
            dout.flush();

            
            buffer = QUIT.getBytes();
            dout.write(buffer); // terminate connection
            dout.flush();

            dout.close();
            s.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

}
