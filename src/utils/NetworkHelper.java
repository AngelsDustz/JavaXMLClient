/**
 * @package utils
 * @author Berwout A.J. Kruit
 *
 * This class is a helper class for network related actions.
 */

package utils;

import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkHelper {
    private int                     port;
    private int                     clientCount;
    private FeedHandler[]           clientInstances;
    private ConcurrentLinkedQueue   feedQueue;
    private Thread[]                feedThreads;
    private ServerHandler           serverHandler;  // Listens to incoming connections.
    private Thread                  serverThread;   // The thread in which it runs.
    private int                     queueLimit;

    public NetworkHelper(ConcurrentLinkedQueue queue) {
        this.port           = 7789;
        this.clientCount    = 800;
        this.feedQueue      = queue;
        this.serverHandler  = new ServerHandler(this.port);
    }

    /**
     * Prepare all FeedHandlers.
     */
    public void prepare() {
        this.clientInstances    = new FeedHandler[this.clientCount]; // Initialize all clients.
        this.feedThreads        = new Thread[this.clientCount];
        this.queueLimit         = this.clientCount*10;

        for (int i = 0; i < this.clientCount; i++) {
            this.clientInstances[i]     = new FeedHandler(this.feedQueue, this.queueLimit); // Create new FeedHandler for client {i}.
            this.feedThreads[i]         = new Thread(this.clientInstances[i]);
        }

        this.serverHandler.setClientInstances(this.clientInstances);
        this.serverHandler.setClientThreads(this.feedThreads);
        this.serverHandler.setCanRun(true);

        this.serverThread = new Thread(this.serverHandler);
        this.serverThread.start();
    }

    public int getActiveThreadNr() {
        int active = 0;

        for (int i = 0; i < this.clientInstances.length; i++) {
            if (this.clientInstances[i].getClientSocket() != null) {
                active++;
            }
        }

        return active;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getClientCount() {
        return clientCount;
    }

    public void setClientCount(int clientCount) {
        this.clientCount = clientCount;
    }

    public int getQueueLimit() {
        return queueLimit;
    }
}
