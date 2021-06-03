package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread extends Thread {

    private ServerSocket serverSocket;
    private HashMap<String, String> alarms;

    public String getAlarm(String ip) {
        return this.alarms.containsKey(ip) ? alarms.get(ip) : null;
    }

    public void putAlarm(String ip, String hourMinute) {
        this.alarms.put(ip, hourMinute);
    }

    public void removeAlarm(String ip) {
        this.alarms.remove(ip);
    }

    /**
     * Constructor
     * @param port listening port
     */
    public ServerThread(int port) {
        alarms = new HashMap<>();
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
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
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
