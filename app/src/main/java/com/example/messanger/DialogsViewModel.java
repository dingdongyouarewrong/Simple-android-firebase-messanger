package com.example.messanger;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.messanger.RoomClasses.DialogModel;

import java.util.List;

public class DialogsViewModel extends AndroidViewModel {

    private DialogsRepository mRepository;

    private LiveData<List<DialogModel>> dialogsList;

    public DialogsViewModel(@NonNull Application application) {
        super(application);
        mRepository = new DialogsRepository(application);
        dialogsList = mRepository.getAllDialogs();

    }

    public LiveData<List<DialogModel>> getAllDialogs() {
        return dialogsList;
    }

    public void insert(DialogModel dialogModel) {
        mRepository.insert(dialogModel);
    }






}
