import java.net.*;
import java.util.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class Client {

    // s
    private static Socket s;

    // s args
    private static final String hostname = "localhost";
    private static final int serverPort = 50000;

    // streams
    private static InputStreamReader din;
    private static DataOutputStream dout;

    // commands
    private static final String HELO = "HELO";
    private static final String OK = "OK";
    private static final String AUTH = "AUTH";
    private static final String REDY = "REDY";
    private static final String JOBN = "JOBN";
    private static final String JCPL = "JCPL";
    private static final String SCHD = "SCHD";
    private static final String NONE = "NONE";
    private static final String QUIT = "QUIT";

    // buffer fields
    private static char[] charBuffer;
    private static byte[] byteBuffer; // will hold the current message from the server stored as bytes

    private static String stringBuffer; /* will hold the current message from the server stored in a string
                                                                       (created from charArray)        */
    private static String[] fieldBuffer; /* will hold the current message from the server as an array of strings
                                                                       (created from stringBuffer)     */

    private static String scheduleString; // string to be scheduled

    private static final int CHAR_BUFFER_LENGTH = 80;

    // create server/list objects
    private static List<Server> serverList;
    private static Server largestServer;

    // create file object
    private static File DSsystemXML;

    public static void main(String[] args) throws IOException {
        setup();

        try {
            writeBytes(HELO); // client sends HELO

            // server replies with OK

            System.out.println("sent AUTH username");
            writeBytes(AUTH + " " + System.getProperty("user.name"));

            // server replies with OK after printing out a welcome message and writing system info

            setLargestServer();

            System.out.println("XML file successfully read and servers extracted. Sending REDY ...");
            writeBytes(REDY);

            readStringBuffer(); // reset stringBuffer & read job

            while (!(stringBuffer = String.valueOf(charBuffer)).contains(NONE)) {
                System.out.println(stringBuffer);

                if (stringBuffer.contains(JOBN)) {
                    fieldBuffer = stringBuffer.split(" "); /* split String into array of strings
                                                              (each string being a field of JOBN) */

                    Job job = new Job(fieldBuffer); // create new Job object with data from fieldBuffer

                    /* SCHEDULE JOB */
                    scheduleString = SCHD + " " + job.id + " " + largestServer.type + " " + largestServer.id;
                    writeBytes(scheduleString);

                    writeBytes(REDY); // send REDY for the next job

                    readStringBuffer(); // reset stringBuffer & read next job
                } 
                else if (stringBuffer.contains(JCPL)) {
                    writeBytes(REDY); // send REDY for the next job

                    readStringBuffer(); // reset stringBuffer & read next job
                } else if (stringBuffer.contains(OK)) {
                    readStringBuffer(); // reset stringBuffer & read next job
                } 

            }

            System.out.println("TERMINATING CONNECTION ...");
            
            writeBytes(QUIT);

            System.out.println("CONNECTION TERMINATED.");

            close();
        } catch (UnknownHostException e) {
            System.out.println("Unknown Host Exception: " + e.getMessage());
        } catch (EOFException e) {
            System.out.println("End of File Exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    public static void setup() throws IOException {
        serverList = new ArrayList<>(); // initialise list of servers

        s = new Socket(hostname, serverPort); // socket with host IP of 127.0.0.1 (localhost), server port of 50000

        din = new InputStreamReader(s.getInputStream());
        dout = new DataOutputStream(s.getOutputStream());

        setSystemXML();
    }

    public static void setSystemXML() {
        String dir = System.getProperty("user.dir") + "/ds-system.xml";
        DSsystemXML = new File(dir);
    }

    public static void writeBytes(String command) throws IOException {
        byteBuffer = command .getBytes();
        dout.write(byteBuffer);
        dout.flush();
    }

    public static void readXML() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(DSsystemXML);

            doc.getDocumentElement().normalize();
            NodeList servers = doc.getElementsByTagName("server");

            for (int i = 0; i < servers.getLength(); i++) { // loop through xml file and input data into appropriate
                                                            // variables
                Element server = (Element) servers.item(i);
                for (int j = 0; j < Integer.parseInt(server.getAttribute("limit")); j++) {
                    String type = server.getAttribute("type");
                    int limit = Integer.parseInt(server.getAttribute("limit"));
                    int bootupTime = Integer.parseInt(server.getAttribute("bootupTime"));
                    float hourlyRate = Float.parseFloat(server.getAttribute("hourlyRate"));
                    int core = Integer.parseInt(server.getAttribute("coreCount"));
                    int memory = Integer.parseInt(server.getAttribute("memory"));
                    int disk = Integer.parseInt(server.getAttribute("disk"));

                    Server dss = new Server(j, type, limit, bootupTime, hourlyRate, core, memory, disk); // create
                                                                                                         // server
                                                                                                         // object we read
                                                                                                         // from xml
                    serverList.add(dss); // add server object to ServerList

                    // print out the server information we read from ds-system.xml
                    System.out.printf("%s %s %s %s %s %s %s %s", dss.id, dss.type, dss.limit, dss.bootUpTime,
                                                                 dss.hourlyRate, dss.core, dss.memory, dss.disk + "\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void setLargestServer() {
        readXML(); // get list of servers
        largestServer = getLargestServer(serverList); // get largest server
    }

    public static void readStringBuffer() throws IOException {
        charBuffer = new char[CHAR_BUFFER_LENGTH];
        din.read(charBuffer);
    }

    public static Server getLargestServer(List<Server> s) {
        largestServer = s.get(0);

        for (int i = 1; i < s.size(); i++) {
            if (s.get(i).core > largestServer.core) {
                largestServer = s.get(i);
            }
        }
        
        return largestServer;
    }
    
    public static void close() throws IOException {
        din.close();
        dout.close();
        s.close();
    }

}