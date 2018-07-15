package com.toksaitov.doodles.dummy;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "doodle_table")
public class Doodle {

    @PrimaryKey
    @NonNull
    private String path;

//    @NonNull
//    @ColumnInfo(name = "creation_date")
//    private Date creationDate;

    public Doodle(String path) {
        this.path = path;
//        this.creationDate = new Date();
    }

    public String getPath(){
        return this.path;
    }

//    @NonNull
//    public Date getCreationDate() {
//        return creationDate;
//    }

}
