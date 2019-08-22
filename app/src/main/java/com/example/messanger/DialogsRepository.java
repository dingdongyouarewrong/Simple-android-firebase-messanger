package com.example.messanger;

import android.app.Application;
import android.app.Dialog;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.example.messanger.RoomClasses.DatabaseClass;
import com.example.messanger.RoomClasses.DatabaseInstance;
import com.example.messanger.RoomClasses.DialogModel;
import com.example.messanger.RoomClasses.RoomDao;

import java.util.List;

public class DialogsRepository {

    private LiveData<List<DialogModel>> dialogsList;
    private RoomDao roomDao;

    DialogsRepository(Application application) {
//        db = Room.databaseBuilder(context,
//                DatabaseClass.class, "dialogs_database").build();
        DatabaseClass db = DatabaseInstance.getInstance().getDatabase();

        roomDao = db.roomDao();
        dialogsList = roomDao.getAllDialogs();

    }

    LiveData<List<DialogModel>> getAllDialogs() {
        return dialogsList;
    }

    public void insert (DialogModel dialogModel) {
        new insertAsyncTask(roomDao).execute(dialogModel);
    }

    private static class insertAsyncTask extends AsyncTask<DialogModel, Void, Void> {

        private RoomDao mAsyncTaskDao;

        insertAsyncTask(RoomDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final DialogModel... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

}
