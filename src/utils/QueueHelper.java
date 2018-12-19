package utils;

import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueHelper {
    private ConcurrentLinkedQueue   feedQueue;

    public QueueHelper(ConcurrentLinkedQueue queue) {
        this.feedQueue      = queue;
        //this.queueHandler   = new QueueHandler(this.feedQueue);
    }

    public void prepare() {
        // 2 threads can run ~ 900
        // 5 can run ~ 1500
        // Tradeoff speed vs memory. 5 do ~ 330MB.
        int limiter                     = 5;
        Thread[] queueThreads           = new Thread[limiter];
        QueueHandler[] queueHandlers    = new QueueHandler[limiter];

        for (int i=0;i<limiter;i++) {
            queueHandlers[i] = new QueueHandler(this.feedQueue);
            queueHandlers[i].setCanRun(true);
            queueThreads[i] = new Thread(queueHandlers[i]);
            queueThreads[i].start();
        }
    }
}
