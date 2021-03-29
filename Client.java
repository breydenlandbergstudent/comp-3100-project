import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;



public class Client {
    // Socket s args
    private static final String hostname = "localhost";
    private static final int serverPort = 50000;
    
    // create server object List to store server information
    private static List < Server > ServerList;

    // streams
    private static DataInputStream din;
    private static DataOutputStream dout;

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
    private static final String GETS = "GETS All";


    // other fields
    private static int count; // will hold the current amount of "available" bytes from s.getInputStream()
    private static byte[] byteBuffer; // will hold the current message from the server stored as bytes
    private static char[] charBuffer; // will hold the current message from the server stored as chars (casted to char from the bytes in byteArray)
    private static String stringBuffer; // the String instance in which we will store the server's message, created from charBuffer
    private static String[] fieldBuffer; // the String array which will contain the server's message as individual Strings, created from stringBuffer

    public static void main(String[] args) throws IOException {
    
    	ServerList = new ArrayList < Server > ();
        Socket s = new Socket(hostname, serverPort); // socket with host IP of 127.0.0.1 (localhost), server port of 50000
        din = new DataInputStream(s.getInputStream());
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
            System.out.println("read XML and send REDY");
            byteBuffer = REDY.getBytes();
            dout.write(byteBuffer);
            dout.flush();

            // server sends JOBN

            System.out.println("receiving JOBN");

            din.skipBytes(OK.length() * 2); // skip the first two OK commands sent by server
            count = din.available(); // get length of byte buffer from din's number of bytes that can be read

            byteBuffer = new byte[count];
            din.read(byteBuffer); // read from din into byte buffer
            charBuffer = new char[count];

            // cast byte array into char array
            for(int i = 0; i < count; i++) {
                charBuffer[i] = (char)byteBuffer[i];
            }

            stringBuffer = new String(charBuffer); // cast char array into String
            System.out.println(stringBuffer);

            while (!stringBuffer.contains(NONE)) {
                fieldBuffer = stringBuffer.split(" "); // split String into array of strings (each string being a field of JOBN)

                Job job = new Job(fieldBuffer); // create new Job object with data from fieldBuffer
                job.printFields();
                
                byteBuffer = OK.getBytes();
                dout.write(byteBuffer);
                dout.flush();
                
             byteBuffer = GETS.getBytes();
            dout.write(byteBuffer);
            dout.flush();
               

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

                // make a method for the above

                
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
            File DSsystemXML = new File("/home/tanayg/ds-sim/src/pre-compiled/ds-system.xml"); // create file object

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
                ServerList.add(dss); // add server object to ServerList

                // print out the server information we read from ds-system.xml
                System.out.printf("'%s %s %s %s %s %s %s", type, limit, bootupTime, hourlyRate, core, memory, disk);
                System.out.println();

            }
            Server s = largServer(ServerList);
            System.out.println("Largest Server: " + s.t + " with" + s.c + " cores");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Server largServer(List < Server > s) {
        Server largestServer = new Server(s.get(0).id, s.get(0).t, s.get(0).l, s.get(0).b, s.get(0).hr, s.get(0).c,
            s.get(0).m, s.get(0).d);
        for (int i = 1; i < s.size(); i++) {
            if (s.get(i).c > largestServer.c) {
                largestServer = s.get(i);
            }
        }
        return largestServer;
    }

}
