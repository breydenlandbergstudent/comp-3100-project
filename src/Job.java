public class Job {
    protected static String type;
    protected static Integer submitTime;
    protected static Integer id;
    protected static Integer estRuntime;
    protected static Integer core;
    protected static Integer memory;
    protected static Integer disk;

    public Job(String[] fieldBuffer) {
        type = fieldBuffer[0];
        submitTime = Integer.parseInt(fieldBuffer[1]);
        id = Integer.parseInt(fieldBuffer[2]);
        estRuntime = Integer.parseInt(fieldBuffer[3]);
        core = Integer.parseInt(fieldBuffer[4]);
        memory = Integer.parseInt(fieldBuffer[5]);
        disk = Integer.parseInt(fieldBuffer[6].trim()); // remove whitespace
    }

    public void printFields() {
        System.out.println("-- JOB DATA --");
        System.out.println("Type : " + type);
        System.out.println("Submit Time : " + submitTime);
        System.out.println("ID : " + id);
        System.out.println("Est. Runtime : " + estRuntime);
        System.out.println("Core : " + core);
        System.out.println("Memory : " + memory);
        System.out.println("Disk : " + disk);
    }
}