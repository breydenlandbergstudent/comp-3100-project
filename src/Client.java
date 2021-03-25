import java.io.*;
import java.net.*;

public class Client {
    private static final String HELO = "HELO";
    private static final String AUTH = "AUTH amirlingcoran";
    private static final String REDY = "REDY";
    private static final String QUIT = "QUIT";

    private static final String NONE = "NONE";
    // private static final String JOBN = "JOBN";
    // private static final String JOBP = "JOBP";
    // private static final String JCPL = "JCPL";
    // private static final String RESF = "RESF";
    // private static final String RESR = "RESR";

    // private static final String SCHD = "SCHD 0 joon 0";
    // private static final String GETS = "GETS ";


    public static void main(String[] args) {
        
        try {
            Socket s = new Socket("localhost", 50000);

            InputStreamReader isr = new InputStreamReader(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());

            /**** HANDSHAKE PHASE ****/
            byte[] buffer = HELO.getBytes();
            dout.write(buffer); 
            // System.out.println("HELO sent.");
            dout.flush();

            buffer = AUTH.getBytes();
            dout.write(buffer);
            // System.out.println("AUTH xxx sent.");
            dout.flush();
            

            /**** JOB SCHEDULING PHASE ****/
            buffer = REDY.getBytes();
            dout.write(buffer);
            // System.out.println("REDY sent.");
            dout.flush();


            char[] cbuf = new char[50];            // create char array to store job from server
            isr.skip(4);                           // offset by 4 characters to skip "OKOK"
            isr.read(cbuf, 0, cbuf.length);        // read input stream data into char array (cbuf)

            String jobString;        // convert char array to String
            String[] strBuffer;      // split String between spaces

            while (!(jobString = String.valueOf(cbuf)).contains(NONE)) {
                strBuffer = jobString.split(" "); // split job data into separate Strings

                // initialise a Job object with data from jobString
                Job job = new Job(strBuffer);

                // String jobCMD = job.core + " " + job.memory + " " + job.disk;
                // byte[] GETSbuffer = (GETS + "Capable" + " " + jobCMD).getBytes();
                // dout.write(GETSbuffer);
                // dout.flush();

                // Schedule job
                // buffer = SCHD.getBytes();
                // dout.write(buffer);
                // dout.flush();

                // RESET buffers for next loop iteration (next job scheduling)
                // send REDY to server for next job
                buffer = REDY.getBytes();
                dout.write(buffer);
                dout.flush();

                // read new input into cbuf
                cbuf = new char[50];
                isr.read(cbuf, 0, cbuf.length);
            }

            buffer = QUIT.getBytes();
            dout.write(buffer);
            dout.flush();

            isr.close();
            dout.close();
            s.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

}
