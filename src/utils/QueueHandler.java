package utils;

import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueHandler implements Runnable {
    private XMLHelper               xmlHelper;
    private ConcurrentLinkedQueue   queue;
    private DatabaseHelper          databaseHelper;
    private boolean                 canRun = false;

    public QueueHandler(ConcurrentLinkedQueue queue) {
        this.queue          = queue;
        this.xmlHelper      = new XMLHelper();
        this.databaseHelper = new DatabaseHelper();
    }

    @Override
    public void run() {
        while (this.canRun) {
            if (this.queue.size() > 0) {
                // If there is anything to pop.
                String data = (String) this.queue.poll(); //Force cast into string.

                this.xmlHelper.setData(data);
                this.databaseHelper.insert(this.xmlHelper);
            } else {
                // Nothing to do, sleep.
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setCanRun(boolean canRun) {
        this.canRun = canRun;
    }
}
