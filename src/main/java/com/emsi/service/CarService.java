package com.emsi.service;

import com.emsi.dao.CarDao;
import com.emsi.dao.impl.CarDaoImp;
import com.emsi.entities.Car;

import java.util.List;

public class CarService {
    private CarDao carDao = new CarDaoImp();

    public List<Car> findAll() {
        return carDao.findAll();
    }

    public void save(Car car) {

        carDao.insert(car);

    }
    public void update(Car car) {

        carDao.update(car);

    }
    public void remove(Car car) {
        carDao.deleteById(car.getId());
    }
}
