package com.example.messanger.RoomClasses;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "dialogs")
public class DialogModel {

//    @ColumnInfo(name = "name")
    public String dialog_name;

    @PrimaryKey
//    @ColumnInfo(name = "dialogID")
    @NonNull public String dialog_id;


//    @ColumnInfo(name = "key")
    public String dialog_key;

    public int dialog_color;

}
