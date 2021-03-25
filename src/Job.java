public class Job {
    String type;
    String submitTime;
    String id;
    String estRuntime;
    String core;
    String memory;
    String disk;

    public Job(String[] strBuffer) {
        type = strBuffer[0];
        submitTime = strBuffer[1];
        id = strBuffer[2];
        estRuntime = strBuffer[3];
        core = strBuffer[4];
        memory = strBuffer[5];
        disk = strBuffer[6].trim(); // remove whitespace
    }

}
