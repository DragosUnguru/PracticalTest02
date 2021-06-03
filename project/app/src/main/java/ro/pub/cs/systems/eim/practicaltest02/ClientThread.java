package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private Socket socket;
    private String address;
    private String requestType;
    private String hour;
    private String minute;
    private TextView textView;
    private int port;

    public ClientThread(int port, String address, String requestType, TextView textView, String hour, String minute) {
        this.port = port;
        this.address = address;
        this.requestType = requestType;
        this.hour = hour;
        this.minute = minute;
        this.textView = textView;
    }

    public ClientThread(int port, String address, String requestType, TextView textView) {
        this.port = port;
        this.address = address;
        this.requestType = requestType;
        this.hour = null;
        this.minute = null;
        this.textView = textView;
    }

    public void run() {
        try {
            // Open socket.
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }

            // Get reader and writer.
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            // Send data to server.
            String toSend = requestType;
            if (requestType.equals(Constants.REQUEST_SET)) {
                toSend = requestType + "," + hour + "," + minute;
            }

            printWriter.println(toSend);
            printWriter.flush();

            String response;
            while ((response = bufferedReader.readLine()) != null) {
                final String textViewValue = response;

                // Thread update UI thread's TextView.
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(textViewValue);
                    }
                });
            }
        } catch (IOException e) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + e.getMessage());
                }
            }
        }
    }
}
