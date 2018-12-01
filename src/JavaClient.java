/**
 * @author Berwout A.J. Kruit
 */

import utils.NetworkHelper;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JavaClient {
    private ConcurrentLinkedQueue   feedQueue;
    private NetworkHelper           networkHelper;
    private Runtime                 runtime;

    public JavaClient() {
        this.feedQueue      = new ConcurrentLinkedQueue();
        this.networkHelper  = new NetworkHelper(this.feedQueue);
        this.runtime        = Runtime.getRuntime();

        this.runtime.gc(); // Call garbage cleanup.
    }

    public void main() throws InterruptedException {
        this.networkHelper.prepare();

        while (true) {
            System.out.println(String.format("Current memory usage: %s", getMemoryUsage()));
            System.out.println(String.format("Current queue size: %d", this.feedQueue.size()));
            System.out.println(String.format("Current active threads: %d", this.networkHelper.getActiveThreadNr()));

            Thread.sleep(1000);
        }
    }

    private String getMemoryUsage() {
        long memory = this.runtime.totalMemory() - this.runtime.freeMemory();
        memory = memory / (1024L*1024L); // Convert to MB.

        return String.format("%d MB.", memory);
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
