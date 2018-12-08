package utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * FeedHandler
 *
 * This file will handle data from/to the feed.
 */

public class FeedHandler implements Runnable {
    private ConcurrentLinkedQueue   feedQueue;      // Contains a reference to the queue.
    private int                     queueLimit;     // Maximum amount of items allowed in queue.
    private Socket                  clientSocket;   // The received socket from the generator.
    private boolean                 canRun;         // Run state.

    public FeedHandler(ConcurrentLinkedQueue queue, int limit) {
        this.feedQueue  = queue;
        this.queueLimit = limit;
        this.canRun     = false;
    }

    @Override
    public void run() {
        while (this.canRun) {
            if (this.clientSocket != null) {
                // If we have a client socket.
                if (this.clientSocket.isConnected()) {
                    // If we have a client connection.
                    byte dataByte[] = null;
                    int res;

                    try {
                        int dataCount       = this.clientSocket.getInputStream().available();
                        dataByte            = new byte[dataCount];

                        this.clientSocket.getInputStream().read(dataByte);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }

                    if (dataByte != null && dataByte.length > 0) {
                        String dataString       = new String(dataByte);

                        if (!dataString.equals("")) {
                            if (this.feedQueue.size() < this.queueLimit) {
                                this.feedQueue.add(dataString);
                            }
                        }
                    }

                    // Check if connection is alive.
                    try {
                        this.clientSocket.getOutputStream().write(1);
                    } catch (IOException ioe) {
                        try {
                            this.clientSocket.close();
                            this.clientSocket = null;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            try {
                Thread.sleep(300);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean getCanRun() {
        return canRun;
    }

    public void setCanRun(boolean canRun) {
        this.canRun = canRun;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
}
