import java.io.*;
import java.net.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;

public class client {

    // Strings to send to server
    private static final String HELO = "HELO";
    private static final String AUTH_username = "AUTH Tanay-Gandhi";
    private static final String REDY = "REDY";
    private static final String OK = "OK";
    private static final String GETS = "GETS All";

    // create server class and store its variables
    public static class Server {
        int id;
        String t;
        int l;
        int b;
        float hr;
        int c;
        int m;
        int d;

        public Server(int id, String t, int l, int b, float hr, int c, int m, int d) {
            this.id = id;
            this.t = t;
            this.l = l;
            this.b = b;
            this.hr = hr;
            this.c = c;
            this.m = m;
            this.d = d;

        }

    }

    public static void main(String args[]) throws Exception {

        // create a socket with port number 50000 to conmnect to ds-sim server
        Socket s = new Socket("localhost", 50000);

        // read incoming data from server
        InputStreamReader din = new InputStreamReader(s.getInputStream());

        // send data to server
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());

        byte[] buffer = HELO.getBytes();// sends HELO to server
        dout.write(buffer);
        dout.flush();

        buffer = AUTH_username.getBytes();// sends AUTH TANAY to server to get aunthentication
        dout.write(buffer);
        dout.flush();

        buffer = REDY.getBytes(); // sends REDY to server
        dout.write(buffer);

        char[] cbuff = new char[100]; // create a char array
        din.read(cbuff, 0, cbuff.length);

        String line = String.valueOf(cbuff); // convert char array to string
        System.out.println("Message:" + line); // print out message received from ds-server

        readXML(); // read the system-xml file and print server information

        while (!(line.contains("NONE"))) { // to keep on scheduling jobs until there are no more jbs to schedule

        }

        s.close(); // close the socket connection
    }

    public static void readXML() {
        try {
            File DSsystemXML = new File("/home/tanayg/ds-sim/src/pre-compiled/ds-system.xml"); // create file object

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(DSsystemXML);

            doc.getDocumentElement().normalize();
            NodeList servers = doc.getElementsByTagName("server");

            List<Server> ServerList = new ArrayList<Server>(); // create server object List to store server information

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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
