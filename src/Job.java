public class Job {
    String type;
    String submitTime;
    String id;
    String estRuntime;
    String core;
    String memory;
    String disk;

    public Job(String[] fieldBuffer) {
        type = fieldBuffer[0];
        submitTime = fieldBuffer[1];
        id = fieldBuffer[2];
        estRuntime = fieldBuffer[3];
        core = fieldBuffer[4];
        memory = fieldBuffer[5];
        disk = fieldBuffer[6].trim(); // remove whitespace
    }

}
