package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private Spinner requestTypeSpinner;

    private EditText serverPortEditText;
    private EditText clientPortEditText;
    private EditText hourEditText;
    private EditText minuteEditText;

    private Button serverConnectButton;
    private Button clientRequestButton;

    private TextView responseOutputTextView;

    private ServerThread serverThread;
    private ClientThread clientThread;


    private final ButtonHandler buttonHandler = new ButtonHandler();
    private class ButtonHandler implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Button button = (Button) v;

            if (v.getId() == R.id.server_start_btn) {
                // Start server button pressed.

                if (button.getText() == null || button.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Server port should be filled!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Start server thread.
                int port = Integer.parseInt(serverPortEditText.getText().toString());
                serverThread = new ServerThread(port);
                serverThread.start();
            }
            else if (v.getId() == R.id.client_button) {
                // Client request button pressed.
                Editable hour = hourEditText.getText();
                String clientPort = clientPortEditText.getText().toString();
                Editable minute = minuteEditText.getText();
                String requestType = requestTypeSpinner.getSelectedItem().toString();

                // Null & empty checks.
                if (requestType.equals(Constants.REQUEST_SET) && (hour == null || hour.toString().isEmpty() || minute == null || minute.toString().isEmpty())) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (serverThread == null || !serverThread.isAlive()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                    return;
                }

                responseOutputTextView.setText("");

                String hourText = (hour == null) ? null : hour.toString();
                String minuteText = (minute == null) ? null : minute.toString();

                clientThread = new ClientThread(Integer.parseInt(clientPort), Constants.LOCALHOST,
                        requestType, responseOutputTextView, hourText, minuteText);
                clientThread.start();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        requestTypeSpinner = findViewById(R.id.request_type_spinner);
        responseOutputTextView = findViewById(R.id.output_textview);

        serverPortEditText = findViewById(R.id.server_port);
        clientPortEditText = findViewById(R.id.client_port);
        hourEditText = findViewById(R.id.hour);
        minuteEditText = findViewById(R.id.minute);

        serverConnectButton = findViewById(R.id.server_start_btn);
        clientRequestButton = findViewById(R.id.client_button);

        serverConnectButton.setOnClickListener(buttonHandler);
        clientRequestButton.setOnClickListener(buttonHandler);
    }
}