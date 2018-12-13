/**
 * @author Berwout A.J. Kruit
 */

import utils.NetworkHelper;
import utils.QueueHelper;

import java.util.concurrent.ConcurrentLinkedQueue;

public class JavaClient {
    private ConcurrentLinkedQueue   feedQueue;
    private NetworkHelper           networkHelper;
    private QueueHelper             queueHelper;
    private Runtime                 runtime;

    public JavaClient() {
        this.feedQueue      = new ConcurrentLinkedQueue();
        this.networkHelper  = new NetworkHelper(this.feedQueue);
        this.queueHelper    = new QueueHelper(this.feedQueue);
        this.runtime        = Runtime.getRuntime();

        this.runtime.gc(); // Call garbage cleanup.
    }

    public void main() throws InterruptedException {
        int ticks           = 0;
        long memoryTotal    = 0;

        this.networkHelper.prepare();
        this.queueHelper.prepare();

        while (true) {
            ++ticks;
            memoryTotal = (this.runtime.totalMemory() - this.runtime.freeMemory());

            System.out.print(String.format("Average Runtime Memory Usage: %s | ", getMemoryUsage(memoryTotal)));
            System.out.print(String.format("Active Threads: %d | ", this.networkHelper.getActiveThreadNr()));
            System.out.println(String.format("Current Que Size: %d", this.feedQueue.size()));

            Thread.sleep(1000);

            if ((ticks % 10) == 0) {
                this.runtime.gc();
            }
        }
    }

    private String getMemoryUsage(long memory) {
        memory = memory / (1024L*1024L); // Convert to MB.

        return String.format("%d MB", memory);
    }

    public static void main(String[] args) {
        JavaClient jc = new JavaClient();

        try {
            jc.main();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
