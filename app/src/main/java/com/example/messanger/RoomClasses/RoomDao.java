package com.example.messanger.RoomClasses;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.messanger.RoomClasses.DialogModel;

import java.util.List;

@Dao
public interface RoomDao {

    @Query("SELECT * FROM dialogs")
    LiveData<List<DialogModel>> getAllDialogs();

    @Insert
    void insert(DialogModel dialog);


}
