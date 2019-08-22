package com.example.messanger.RoomClasses;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DialogModel.class}, version = 1)
public abstract class DatabaseClass extends RoomDatabase {
    public abstract RoomDao roomDao();

}
