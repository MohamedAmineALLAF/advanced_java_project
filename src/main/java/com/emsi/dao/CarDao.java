package com.emsi.dao;

import com.emsi.entities.Car;
import com.emsi.entities.Car;
import com.emsi.entities.Owner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.List;

public interface CarDao {
    void insert(Car car);

    void update(Car car);

    void deleteById(Integer id);

    Car findById(Integer id);
    List<Car> findAll();

    List<Car> findByOwner(Owner owner);

    List<Car> readFromTextFile(FileReader fileReader);

    void writeInOutputFile(String path);

    void readFromStyleSheet(File file);

    void CreateAndWriteInStyleSheet(File file);

    void readJsonFromTextFile(String path);

    void readFromDatabaseToTextFile(String path);


}
