package com.example.messanger.Activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messanger.Constants;
import com.example.messanger.Message;
import com.example.messanger.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import static java.lang.String.valueOf;

public class ChatActivity extends AppCompatActivity {

    private FirebaseListAdapter<Message> adapter;
    Constants constants = new Constants();

    String dialogName;

    ListView listMessages;

    private GoogleApiClient mGoogleApiClient;
    String username;
    String dialogID;
    EditText userMessage;

    // Initialize Firebase Auth
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        userMessage = findViewById(R.id.userMessage);
        TextView dialogNameText =  findViewById(R.id.dialogName);
        TextView dialogIdText = findViewById(R.id.dialog_id_text);

        dialogName = getIntent().getExtras().getString("dialogName","defaultKey");
        if (mFirebaseUser == null) {
            Log.e("USER", "null");
        }
        username = mFirebaseUser.getDisplayName();
        dialogID = getIntent().getExtras().getString("dialogID","defaultKey");

        dialogIdText.setText(dialogID);
        dialogNameText.setText(dialogName);
        displayChat();

    }

    private void displayChat() {
        try {
            listMessages = findViewById(R.id.messagesList);
            adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.message_activity, FirebaseDatabase.getInstance().getReference("dialogs/".concat(dialogID))) {
                @Override
                protected void populateView(View v, Message model, int position) {

                    TextView textMessage, autor, timeMessage;
                    LinearLayout template = v.findViewById(R.id.messageTemplate);

                    textMessage = v.findViewById(R.id.messageTextView);
                    autor = v.findViewById(R.id.authorTextView);
                    timeMessage = v.findViewById(R.id.timeTextView);

                    textMessage.setText(model.getTextMessage());
                    autor.setText(model.getAutor());
                    timeMessage.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTimeMessage()));
                    if (model.getAutor().equals(username)) {
                        template.setGravity(Gravity.END);
                        timeMessage.setPadding(10, 0, 0, 0);
                    } else {
                        template.setGravity(Gravity.START);
                    }
                }
            };

            listMessages.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Сообщений нет", Toast.LENGTH_SHORT).show();

        }
    }



    public void sendMessage(View view) {
        if (!userMessage.getText().toString().equals("")) {
            FirebaseDatabase.getInstance().getReference("dialogs/".concat(dialogID)).push().setValue(new Message(userMessage.getText().toString(),username));
            userMessage.setText("");
            listMessages.smoothScrollToPosition(listMessages.getCount() -1);
        }
    }



    public void SignOut(View view) {
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"выход выполнен", Toast.LENGTH_LONG).show(); }
                });
    }

}
