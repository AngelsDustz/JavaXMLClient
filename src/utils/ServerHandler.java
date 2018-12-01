package utils;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerHandler implements Runnable {
    private ServerSocket    serverSocket = null;
    private FeedHandler[]   clientInstances;
    private Thread[]        clientThreads;
    private boolean         canRun;

    public ServerHandler(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (this.canRun) {
            if (this.serverSocket != null) {
                Socket connection = null;

                try {
                    connection = this.serverSocket.accept();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (connection != null) {
                    for (int i=0;i<this.clientInstances.length;i++) {
                        if (this.clientInstances[i].getClientSocket() == null) {
                            System.out.println(String.format("Found free thread spot at %d !", i));

                            this.clientInstances[i].setClientSocket(connection);
                            this.clientInstances[i].setCanRun(true);


                            if (this.clientThreads[i].getState() == Thread.State.NEW){
                                this.clientThreads[i].start();
                            }

                            break;
                        }
                    }
                }
            }
        }
    }

    public FeedHandler[] getClientInstances() {
        return clientInstances;
    }

    public void setClientInstances(FeedHandler[] clientInstances) {
        this.clientInstances = clientInstances;
    }

    public Thread[] getClientThreads() {
        return clientThreads;
    }

    public void setClientThreads(Thread[] clientThreads) {
        this.clientThreads = clientThreads;
    }

    public boolean isCanRun() {
        return canRun;
    }

    public void setCanRun(boolean canRun) {
        this.canRun = canRun;
    }
}
