package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {

    ServerSocket serverSocket;

    /**
     * Constructor
     * @param port listening port
     */
    public ServerThread(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            Log.e(Constants.TAG, "[SERVER THREAD]: An exception has occurred: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                Log.i(Constants.TAG, "[SERVER THREAD] A connection was established with " +
                        socket.getInetAddress() + ":" + socket.getLocalPort());

                // Spawn new thread to handle the connection.
//                CommunicationThread communicationThread = new CommunicationThread(this, socket);
//                communicationThread.start();
            }
        } catch (IOException e) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + e.getMessage());
        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + e.getMessage());
            }
        }
    }
}
