import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class Client {
    private static final String HELO = "HELO";
    private static final String AUTH = "AUTH group_55";
    private static final String REDY = "REDY";
    private static final String SCHD = "SCHD";
    private static final String QUIT = "QUIT";

    private static final String OK = "OK";

    private static final String NONE = "NONE";
    private static final String JOBN = "JOBN";
    private static final String JOBP = "JOBP";
    private static final String JCPL = "JCPL";
    private static final String RESF = "RESF";
    private static final String RESR = "RESR";

    private static final String GETS = "GETS";
    private static final String GETS_All = GETS + " All";
    private static final String GETS_Capable = GETS + " Capable";
    private static final String GETS_Avail = GETS + " Avail";

    private static final int BUFFER_LENGTH = 50;

    public static List<Server> serverList;     // store Servers read from ds-system.xml

    public static void main(String[] args) {
        byte[] buffer;               // store Client messages to be sent to ds-server

        try {
            Socket socket = new Socket("localhost", 50000);

            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());


            /**** HANDSHAKE PHASE ****/
            buffer = HELO.getBytes();
            dout.write(buffer); 
            dout.flush();
            System.out.println("HELO sent.\n");

            buffer = AUTH.getBytes();
            dout.write(buffer);
            dout.flush();
            System.out.println("AUTH group_55 sent.\n");
            

            // READ XML 
            readXML();
            System.out.println("-- SERVER LIST --");
            for (Server s : serverList) {
                System.out.println(s.id + " : " + s.type);
            }
            System.out.println("ds-system.xml successfully read. All Server info stored.\n");


            /**** JOB SCHEDULING PHASE ****/
            buffer = REDY.getBytes();
            dout.write(buffer);
            dout.flush();
            System.out.println("REDY sent.\n");

            char[] cbuf = new char[BUFFER_LENGTH];           // create char array to store job from server
            isr.skip(4);                                     // offset to skip "OKOK"
            isr.read(cbuf, 0, cbuf.length);                  // read input stream data into char array (cbuf)
            System.out.println("Job successfully read into char buffer.");

            String jobString;
            String[] fieldBuffer;

            if (!(jobString = String.valueOf(cbuf)).contains(NONE)) {
                fieldBuffer = jobString.split(" "); // split job data into separate Strings

                // initialise a Job object with data from jobString
                Job job = new Job(fieldBuffer);

                // String jobCMD = job.core + " " + job.memory + " " + job.disk;
                // // request for Server capable of handling current job
                // byte[] GETSbuffer = (GETS_Capable + " " + jobCMD).getBytes();
                // dout.write(GETSbuffer);
                // dout.flush();

                buffer = OK.getBytes();
                dout.write(buffer);
                dout.flush();

                /* SCHEDULE JOB */

                Server capable = serverList.get(1);
                String scheduleString = SCHD + " " + job.id + " " + capable.type + " " + capable.id;
                buffer = scheduleString.getBytes();
                dout.write(buffer);
                dout.flush();

                /* FOR TESTING
                Server capable = serverList.get(0);
                for (Server s : serverList) {
                    if (s.core >= job.core && s.memory >= job.memory && s.disk >= job.disk) {
                        capable = s;
                        break;
                    }
                }

                String scheduleString = SCHD + " " + job.id + " " + capable.type + " " + capable.id;
                buffer = scheduleString.getBytes();
                dout.write(buffer);
                dout.flush();
                */

                // RESET buffers for next loop iteration (next job scheduling)
                // send REDY to server for next job
                // buffer = REDY.getBytes();
                // dout.write(buffer);
                // dout.flush();

                // read new input into cbuf
                // cbuf = new char[BUFFER_LENGTH];
                // isr.read(cbuf, 0, cbuf.length);
            }

            buffer = QUIT.getBytes();
            dout.write(buffer);
            dout.flush();

            isr.close();
            dout.close();
            socket.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void readXML() {
        serverList = new ArrayList<Server>(); // initialise server object List to store server information

        try {
            File DSsystemXML = new File("/home/amir/Documents/ds-sim/src/pre-compiled/ds-system.xml"); // create file object

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(DSsystemXML);

            doc.getDocumentElement().normalize();
            NodeList servers = doc.getElementsByTagName("server");

            for (int i = 0; i < servers.getLength(); i++) { // loop through xml file and input data into appropriate
                                                            // variables
                Element server = (Element) servers.item(i);
                String type = server.getAttribute("type");
                int limit = Integer.parseInt(server.getAttribute("limit"));
                int bootupTime = Integer.parseInt(server.getAttribute("bootupTime"));
                float hourlyRate = Float.parseFloat(server.getAttribute("hourlyRate"));
                int core = Integer.parseInt(server.getAttribute("coreCount"));
                int memory = Integer.parseInt(server.getAttribute("memory"));
                int disk = Integer.parseInt(server.getAttribute("disk"));

                Server dss = new Server(i, type, limit, bootupTime, hourlyRate, core, memory, disk); // create server
                                                                                                     // object we read
                                                                                                     // from xml
                serverList.add(dss); // add server object to ServerList

                // print out the server information we read from ds-system.xml
                // System.out.printf("'%s %s %s %s %s %s %s", type, limit, bootupTime, hourlyRate, core, memory, disk);
                // System.out.println();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
