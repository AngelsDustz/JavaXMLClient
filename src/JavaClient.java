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

        // Set maximum amount of clients, default 800.
        this.networkHelper.setClientCount(1600);

        this.networkHelper.prepare();
        this.queueHelper.prepare();

        int totalThreads    = this.networkHelper.getClientCount();
        int queueLimit      = this.networkHelper.getQueueLimit();

        while (true) {
            ++ticks;

            long memory         = this.runtime.totalMemory() - this.runtime.freeMemory();
            int queueSize       = this.feedQueue.size();
            int activeThreads   = this.networkHelper.getActiveThreadNr();

            System.out.println(String.format("Memory: %s\t| Queue Size: %d/%d\t|  Threads (Active/Total): %d/%d", getMemoryUsage(memory), queueSize, queueLimit, activeThreads, totalThreads));

            if (ticks >= 15) {
                this.runtime.gc();
                ticks = 0;
            }

            Thread.sleep(1000);
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
