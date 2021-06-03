package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            // Fetch reader and writer.
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            // Read request from client just as it sent it.
            String data = bufferedReader.readLine();
            if (data == null || data.isEmpty() || (data.startsWith(Constants.REQUEST_SET) && data.split(",").length != 3)) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Invalid data received from client.");
                return;
            }

            String ip = socket.getInetAddress().toString();
            if (data.startsWith(Constants.REQUEST_SET)) {
                String hourMinute = data.substring(data.indexOf(","));
                serverThread.putAlarm(ip, hourMinute);

                // Write through socket to client.
                printWriter.println("Alarm set!");
                printWriter.flush();
            }
            else if (data.startsWith(Constants.REQUEST_RESET)) {
                serverThread.removeAlarm(ip);

                // Write through socket to client.
                printWriter.println("Alarm removed!");
                printWriter.flush();
            }
            else if (data.startsWith(Constants.REQUEST_POLL)) {
//                // Create HTTP client for GET request.
//                HttpClient httpClient = new DefaultHttpClient();
//                HttpGet httpPost = new HttpGet(Constants.WEB_SERVICE_ADDRESS);

//                // Add query params.
//                List<NameValuePair> params = new ArrayList<>();
//                params.add(new BasicNameValuePair(Constants.QUERY_ATTRIBUTE, city));
//                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
//                httpPost.setEntity(urlEncodedFormEntity);

//                // Get response.
//                ResponseHandler<String> responseHandler = new BasicResponseHandler();
//                String pageSourceCode = httpClient.execute(httpPost, responseHandler);
//                if (pageSourceCode == null) {
//                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
//                    return;
//                }

                Socket timeSocket = new Socket(Constants.WEB_SERVICE_ADDRESS, Constants.WEB_SERVICE_PORT);
                bufferedReader = Utilities.getReader(timeSocket);
                bufferedReader.readLine();
                String dayTimeProtocol = bufferedReader.readLine();

                // Write through socket to client.
                printWriter.println(dayTimeProtocol);
                printWriter.flush();
            }

        } catch (IOException e) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + e.getMessage());
                }
            }
        }
    }
}
