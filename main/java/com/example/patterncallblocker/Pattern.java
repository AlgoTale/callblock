package com.example.patterncallblocker;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Pattern {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "regex_pattern")
    public String regexPattern;

}
