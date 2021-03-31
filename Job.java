public class Job {
    String type;
    Integer submitTime;
    Integer id;
    Integer estRuntime;
    Integer core;
    Integer memory;
    Integer disk;

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
        System.out.println("Type : " + this.type);
        System.out.println("Submit Time : " + this.submitTime);
        System.out.println("ID : " + this.id);
        System.out.println("Est. Runtime : " + this.estRuntime);
        System.out.println("Core : " + this.core);
        System.out.println("Memory : " + this.memory);
        System.out.println("Disk : " + this.disk);
    }
}