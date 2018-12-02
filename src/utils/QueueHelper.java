package utils;

import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueHelper {
    private QueueHandler            queueHandler;
    private Thread                  queueThread;
    private ConcurrentLinkedQueue   feedQueue;

    public QueueHelper(ConcurrentLinkedQueue queue) {
        this.feedQueue      = queue;
        this.queueHandler   = new QueueHandler(this.feedQueue);
    }

    public void prepare() {
        this.queueHandler.setCanRun(true);
        this.queueThread = new Thread(this.queueHandler);
        this.queueThread.start();
    }
}
