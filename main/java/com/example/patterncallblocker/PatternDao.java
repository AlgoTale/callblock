package com.example.patterncallblocker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PatternDao {
    @Query("SELECT * FROM pattern")
    List<Pattern> getAll();

//    @Query("SELECT * FROM pattern WHERE regex_pattern REGEXP :regex_Pattern")
//    Pattern findByRegexPattern(String regex_Pattern);

    @Query("DELETE FROM pattern WHERE regex_pattern = :regexPattern")
    void deleteByRegexPattern(String regexPattern);

    @Insert
    void insert(Pattern pattern);
}
