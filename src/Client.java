import java.net.*;
import java.util.*;
import java.io.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;

import jdk.jshell.Snippet.SubKind;


public class Client {
    // Socket s args
    private static final String hostname = "localhost";
    private static final int serverPort = 50000;

    // streams
    private static InputStreamReader din;
    private static DataOutputStream dout;

    // commands
    private static final String HELO = "HELO";
    private static final String OK = "OK";
    private static final String AUTH = "AUTH";
    private static final String username = "group_55";
    private static final String AUTH_username = AUTH + " " + username;
    private static final String REDY = "REDY";
    private static final String JOBN = "JOBN";
    private static final String JCPL = "JCPL";
    private static final String SCHD = "SCHD";
    private static final String NONE = "NONE";
    private static final String QUIT = "QUIT";

    // other fields
    private static byte[] byteBuffer; // will hold the current message from the server stored as bytes
    private static char[] charBuffer; // will hold the current message from the server stored as chars (casted to char from the bytes in byteArray)
    private static String stringBuffer; // the String instance in which we will store the server's message, created from charBuffer
    private static String[] fieldBuffer; // the String array which will contain the server's message as individual Strings, created from stringBuffer

    private static final int CHAR_BUFFER_LENGTH = 50; // will hold the current amount of "available" bytes from s.getInputStream()

    private static List<Server> serverList; // create server object List to store server information
    private static File DSsystemXML = new File("/home/amir/Documents/ds-sim/src/pre-compiled/ds-system.xml"); // create file object

    public static void main(String[] args) throws IOException {
        serverList = new ArrayList<>(); // initialise list of servers

        Socket s = new Socket(hostname, serverPort); // socket with host IP of 127.0.0.1 (localhost), server port of 50000
        din = new InputStreamReader(s.getInputStream());
        dout = new DataOutputStream(s.getOutputStream());

        try {
            System.out.println("sent HELO");
            byteBuffer = HELO.getBytes();
            dout.write (byteBuffer);
            dout.flush();

            // server replies with OK

            System.out.println("sent AUTH username");
            byteBuffer = AUTH_username.getBytes();
            dout.write(byteBuffer);
            dout.flush();

            // replies with OK after printing

            readXML ();

            Server largestServer = getLargestServer(serverList); // get largest server
            System.out.println("Largest Server:" + largestServer.type + " with " + largestServer.core + " cores");

            System.out.println("XML file successfully read. Sending REDY ...");
            byteBuffer = REDY.getBytes();
            dout.write(byteBuffer);
            dout.flush();

            // server sends JOBN

            System.out.println("receiving JOBN ...");

            din.skip(4); // skip the first two OK commands sent by server
            charBuffer = new char[CHAR_BUFFER_LENGTH];
            din.read(charBuffer); // read from din into charBuffer
            System.out.println("JOB successfully received.");

            while(!(stringBuffer = String.valueOf(charBuffer)).contains(NONE)) {
                fieldBuffer = stringBuffer.split(" "); // split String into array of strings (each string being a field of JOBN)

                Job job = new Job(fieldBuffer); // create new Job object with data from fieldBuffer
                job.printFields();

                /* SCHEDULE JOB */
                System.out.println ("hello");
                String scheduleString = SCHD + " " + job.id + " " + largestServer.type + " " + largestServer.id;
                byteBuffer = scheduleString.getBytes();
                dout.write(byteBuffer);
                dout.flush();
                System.out.println ("hello");

                // send REDY again for next job and reset buffers
                byteBuffer = REDY.getBytes();
                dout.write(byteBuffer);
                dout.flush();

                din.skip(2); // skip the first two OK commands sent by server
                charBuffer = new char[CHAR_BUFFER_LENGTH];
                din.read(charBuffer); // read from din into charBuffer

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
                // *make a method for the above
                // for testing
            }

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

                    Server dss = new Server(j, type, limit, bootupTime, hourlyRate, core, memory, disk); // create server
                    serverList.add(dss); // add server object to ServerList

                    // print out the server information we read from ds-system.xml
                    System.out.printf("%s %s %s %s %s %s %s %s", dss.id, dss.type, dss.limit, dss.bootUpTime, dss.hourlyRate, dss.core, dss.memory, dss.disk);
                    System.out.println();

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Server getLargestServer(List<Server> s) {
        Server largestServer = s.get(0);
            
        for (int i = 1; i < s.size(); i++) {
            if (s.get(i).core > largestServer.core) {
                largestServer = s.get(i);
            }
        }
        return largestServer;
    }

}
