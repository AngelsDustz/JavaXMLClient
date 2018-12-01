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
        int tickSinceLastMessage = 0;


        while (this.canRun) {
            if (this.clientSocket != null) {
                // If we have a client socket.
                if (this.clientSocket.isConnected()) {
                    // If we have a client connection.
                    byte dataByte[] = null;

                    try {
                        InputStream stream  = this.clientSocket.getInputStream();
                        int dataCount       = stream.available();
                        dataByte            = new byte[dataCount];

                        if (dataCount == 0) {
                            //No data.
                            tickSinceLastMessage++;
                        }

                        stream.read(dataByte);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                        //this.canRun = false;
                    }

                    if (dataByte.length > 0) {
                        tickSinceLastMessage    = 0;
                        String dataString       = new String(dataByte);

                        if (!dataString.equals("")) {
                            if (this.feedQueue.size() < this.queueLimit) {
                                this.feedQueue.add(dataString);
                            }
                        }
                    }
                }
            }

            if (tickSinceLastMessage >= 7) {
                // No message for ~2 seconds, assume dead.

                try {
                    this.clientSocket.close();
                    this.clientSocket = null;
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                tickSinceLastMessage = 0;
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
