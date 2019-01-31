package com.example.messanger.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.messanger.Constants;
import com.example.messanger.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Date;
import java.util.Random;

import static java.lang.String.valueOf;

public class DialogsActivity extends AppCompatActivity {

    EditText inputDialogID; //we will need that in a dialog activity
    EditText inputDialogName;

    //getting constants object
    Constants constants = new Constants();

    // Initialize Firebase Auth
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser(); // mFirebaseUser - user object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogs_activity);

        if (mFirebaseUser == null) { //if user object is null, user is not signed in or not signed up
            SignIn();
        }

    }

    public void createNewDialog(View view) {
        startNewDialog();
    }

    public void connectToDialog(View view) {
        startAlertPopUpDialog(constants.REQUEST_CODE_CONNECT_TO_DIALOG);
    }


    private void startNewDialog() {

        startAlertPopUpDialog(constants.REQUEST_CODE_NEW_DIALOG);
    }

    private String createDialogId() { //random int + replace random position with a-z and again with A-Z
        boolean used = true;
        Random r = new Random();
        String dialogID = null;
        while (used) {
            r.setSeed(new Date().getTime());
            int number = r.nextInt(100000000);
            String randomNumeralString = valueOf(number);
            int insertCharIndex = r.nextInt(randomNumeralString.length() - 1);
            dialogID = randomNumeralString.replace(randomNumeralString.substring(insertCharIndex, insertCharIndex + 1), valueOf((char) (64 + r.nextInt(25)))); //replace random position with A-Z
            insertCharIndex = r.nextInt(randomNumeralString.length() - 1);
            dialogID = randomNumeralString.replace(randomNumeralString.substring(insertCharIndex, insertCharIndex + 1), valueOf((char) (96 + r.nextInt(25))));//with a-z
            used = checkCodeForUsage(dialogID); //If dialog we find same dialogID - making new id again
        }
        return dialogID;
    }

    private boolean checkCodeForUsage(final String dialogID) { //имя чата шифруется. если имя  зашифровано - оно в виде NnjJBFjkebfkjfbef а значит уникально
        final boolean[] used = {false};
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("upload"); //getting reference on upload(there is all users and messages)
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(dialogID)) {
                    used[0] = true;
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                used[0] = false;
            }
        });
        return used[0];
    }

    protected void SignIn() {
        startActivityForResult(AuthUI.getInstance() //starting signin activity from google
                .createSignInIntentBuilder()
                .build(), constants.RequestCode_SignedIn
        );

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == constants.RequestCode_SignedIn) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,"вход выполнен", Toast.LENGTH_SHORT).show(); // if result code is the same as our result code - creating toast "logged in"
            } else {
                Toast.makeText(this,"вход не выполнен", Toast.LENGTH_SHORT).show(); //if not - toast that's we're not in
                finish();
            }
        }
    }

    protected void startAlertPopUpDialog(final int request_code) {
        LayoutInflater inflater = getLayoutInflater();
        String alertTitle = null;
        View view;

        if (request_code == constants.REQUEST_CODE_NEW_DIALOG) { //if user wants to create dialog - getting layout of dialog and edittext from there
            alertTitle = "Введите имя диалога";
            view = inflater.inflate(R.layout.create_new_dialog_dialog, null);
            inputDialogName = view.findViewById(R.id.dialogName);
        }
        else { //if there was button "connect to dialog" getting layout for dialog and edittext's from there
            alertTitle = "Введите ID диалога и его имя";
            view = inflater.inflate(R.layout.connect_to_dialog_dialog, null);
            inputDialogName = view.findViewById(R.id.dialogName);
            inputDialogID = view.findViewById(R.id.dialogID);

        }

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(DialogsActivity.this);
        alertDialog.setTitle(alertTitle)
                .setView(view);




        alertDialog.setPositiveButton("Добавить",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String dialogID;

                        String dialogName = inputDialogName.getText().toString();

                        if ((dialogName.length() == 0)) {
                            Toast.makeText(getApplicationContext(),
                                    "Неверное имя диалога", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            if (request_code == constants.REQUEST_CODE_NEW_DIALOG) {
                                dialogID = createDialogId();
                                Intent intent = new Intent(DialogsActivity.this, ChatActivity.class);
                                intent.putExtra("dialogName", dialogName);
                                intent.putExtra("dialogID", dialogID);
                                startActivity(intent);
                            }
                            else {
                                dialogID = inputDialogID.getText().toString();
                                if (!dialogID.equals("")) {
                                    Intent intent = new Intent(DialogsActivity.this, ChatActivity.class);
                                    intent.putExtra("dialogName", dialogName);
                                    intent.putExtra("dialogID", dialogID);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Неверное ID диалога", Toast.LENGTH_SHORT).show();

                                }

                            }
                        }
                    }
                });


        alertDialog.show();
    }

}