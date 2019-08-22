package com.example.messanger.RoomClasses;

import android.app.Application;

import androidx.room.Room;

public class DatabaseInstance extends Application {

        public static DatabaseInstance instance;

        private DatabaseClass database;

        @Override
        public void onCreate() {
            super.onCreate();
            instance = this;
            database = Room.databaseBuilder(this, DatabaseClass.class, "dialogs_database")
                    .build();
        }

        public static DatabaseInstance getInstance() {
            return instance;
        }

        public DatabaseClass getDatabase() {
            return database;
        }

}
