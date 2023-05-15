package com.emsi.dao;

import com.emsi.entities.Car;
import com.emsi.entities.Owner;

import java.io.File;
import java.io.FileReader;
import java.util.List;

public interface OwnerDao {
    void insert(Owner owner);

    void update(Owner owner);

    void deleteById(Integer id);

    Owner findById(Integer id);

    List<Owner> findAll();

    List<Owner> readFromTextFile(FileReader fileReader);

    void readFromDatabaseToTextFile();

    void readFromStylSheetAndInsertInDatabase();

}
