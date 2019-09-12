package com.example.messanger.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.example.messanger.Constants;
import com.example.messanger.DialogsListAdapter;
import com.example.messanger.DialogsViewModel;
import com.example.messanger.R;
import com.example.messanger.RoomClasses.DatabaseClass;
import com.example.messanger.RoomClasses.DatabaseInstance;
import com.example.messanger.RoomClasses.DialogModel;
import com.example.messanger.RoomClasses.RoomDao;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Date;
import java.util.List;
import java.util.Random;


import static java.lang.String.valueOf;

public class DialogsActivity extends AppCompatActivity implements DialogsListAdapter.ItemClickListener {

    EditText inputDialogID; //we will need that in a dialog activity
    EditText inputDialogName;

    DialogModel dialogModel;
    List<DialogModel> dialogsList;
    private DialogsViewModel dialogsViewModel;

    //getting constants object
    Constants constants = new Constants();

    // Initialize Firebase Auth
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser(); // mFirebaseUser - user object




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogs_activity);

        dialogsViewModel = ViewModelProviders.of(this).get(DialogsViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.dialogs_list);
        final DialogsListAdapter adapter = new DialogsListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnClick(this);

        dialogsViewModel.getAllDialogs().observe(this, new Observer<List<DialogModel>>() {
            @Override
            public void onChanged(List<DialogModel> dialogModels) {
                dialogsList = dialogModels;
                adapter.setData(dialogModels);
            }
        });

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
            used = checkIDForUsage(dialogID); //If dialog we find same dialogID - making new id again
        }
        return dialogID;
    }

    private boolean checkIDForUsage(final String dialogID) { //имя чата шифруется. если имя  зашифровано - оно в виде NnjJBFjkebfkjfbef а значит уникально
        final boolean[] used = {true};
        Log.e("dialog id is ", dialogID);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("dialogs"); //getting reference on dialogs(there is all users and messages)
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(dialogID)) {
                    used[0] = true;
                    Log.e("exist in", " exist");
                    Log.e("exist in boolean", valueOf(used[0]));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                used[0] = true;
                Log.e("not exist", "  not exist");
            }
        });
        Log.e("exist out", valueOf(used[0]));

        return used[0];
    }

    protected void SignIn() {
        startActivityForResult(AuthUI.getInstance() //starting signin activity from google
                .createSignInIntentBuilder()
                .build(), constants.RequestCode_SignedIn
        );

    }



    //acc work
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
            inputDialogID = view.findViewById(R.id.dialogIDInput);
            inputDialogName = view.findViewById(R.id.dialogNameInput);
        }

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(DialogsActivity.this); //time to build alertDialog.
        alertDialog.setTitle(alertTitle) //title from if, 2 steps below
                .setView(view); //if this is dialog creating - view with 1 edittext, if connecting to the existing dialog - 2, for id and for name




        alertDialog.setPositiveButton("Добавить",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String dialogName = inputDialogName.getText().toString(); //get dialog  name from edittext

                        if ((dialogName.length() == 0)) { //if user didnt entered dialog's name - notice him
                            Toast.makeText(getApplicationContext(),
                                    "Введите имя диалога", Toast.LENGTH_SHORT).show();
                        }
                        else { //if dialog's creating - create new dialogID and put that in chatActivity
                            if (request_code == constants.REQUEST_CODE_NEW_DIALOG) {
                                createNewDialog(dialogName);
                            }
                            else {//if connecting to the new dialog - get dialogID from editText
                                connectToDialog(dialogName);
                            }
                        }
                    }
                });


        alertDialog.show();
    }

    public void createNewDialog( String dialogName){
        String dialogID = createDialogId();
        Intent intent = new Intent(DialogsActivity.this, ChatActivity.class);
        intent.putExtra("dialogName", dialogName);
        intent.putExtra("dialogID", dialogID);

        startActivity(intent);
        insertDialogIntoDatabase(dialogID, dialogName);
    }

    private void insertDialogIntoDatabase(String dialogID, String dialogName) {
        dialogModel = new DialogModel();
        dialogModel.dialog_id = dialogID;
        dialogModel.dialog_name = dialogName;
        dialogModel.dialog_key = "";
        dialogModel.dialog_color = createRandomColor();
        dialogsViewModel.insert(dialogModel);

    }

    private int createRandomColor() {
        Random random=new Random(); // Probably really put this somewhere where it gets executed only once
        int red=random.nextInt(256);
        int green=random.nextInt(256);
        int blue=random.nextInt(256);
        return Color.rgb(red, green, blue);
    }



    public void connectToDialog(String dialogName) {
        final String dialogID = inputDialogID.getText().toString();

        boolean dialogExists = checkIDForUsage(dialogID);
        Toast.makeText(getApplicationContext(), valueOf(dialogExists), Toast.LENGTH_SHORT).show();
        if (!dialogID.equals("") && dialogExists) { //if id is entered
            Intent intent = new Intent(DialogsActivity.this, ChatActivity.class);
            intent.putExtra("dialogName", dialogName);
            intent.putExtra("dialogID", dialogID);
            startActivity(intent);
            insertDialogIntoDatabase(dialogID, dialogName);

        } else {
            Toast.makeText(getApplicationContext(),
                    "Неверное ID диалога", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(DialogsActivity.this, ChatActivity.class);
        intent.putExtra("dialogName", dialogsList.get(position).dialog_name);
        intent.putExtra("dialogID", dialogsList.get(position).dialog_id);
        startActivity(intent);
    }
}