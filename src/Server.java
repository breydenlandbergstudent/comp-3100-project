public class Server {
    int id;
    String type;
    int limit;
    int bootupTime;
    float hourlyRate;
    int core;
    int memory;
    int disk;

    public Server(int id, String t, int l, int b, float hr, int c, int m, int d) {
        this.id = id;
        this.type = t;
        this.limit = l;
        this.bootupTime = b;
        this.hourlyRate = hr;
        this.core = c;
        this.memory = m;
        this.disk = d;

    }
}