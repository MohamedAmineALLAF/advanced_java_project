package com.emsi.dao;

import com.emsi.entities.Car;
import com.emsi.entities.Car;
import com.emsi.entities.Owner;

import java.util.List;

public interface CarDao {
    void insert(Car car);

    void update(Car car);

    void deleteById(Integer id);

    Car findById(Integer id);
    List<Car> findAll();

    List<Car> findByOwner(Owner owner);

}
