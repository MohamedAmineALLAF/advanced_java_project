package com.emsi.dao.impl;

import com.emsi.dao.CarDao;
import com.emsi.entities.Car;
import com.emsi.entities.Owner;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarDaoImp implements CarDao {

    private Connection conn= DB.getConnection();

    @Override
    public void insert(Car car) {

        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("INSERT INTO car (Brand,Model,RegistrationNumber,RegistrationDate,Color,fuelType,OwnerId) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, car.getBrand());
            ps.setString(2, car.getModel());
            ps.setString(3, car.getRegistrationNumber());
            ps.setDate(4, new java.sql.Date(car.getRegistrationDate().getTime()));
            ps.setString(5, car.getColor());
            ps.setString(6, car.getFuelType());
            ps.setInt(7, car.getOwner().getId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = ps.getGeneratedKeys();

                if (rs.next()) {
                    int id = rs.getInt(1);

                    car.setId(id);
                }

                DB.closeResultSet(rs);
            } else {
                System.out.println("Aucune ligne renvoyée");
            }
        } catch (SQLException e) {
            System.err.println("problème d'insertion d'un département");;
        } finally {
            DB.closeStatement(ps);
        }

    }

    @Override
    public void update(Car car) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(
                    "UPDATE car SET Brand = ?, Model = ?, RegistrationNumber = ?, RegistrationDate = ?,Color = ? , FuelType = ? , OwnerId = ? WHERE Id = ?");

            ps.setString(1, car.getBrand());
            ps.setString(2, car.getModel());
            ps.setString(3, car.getRegistrationNumber());
            ps.setDate(4, new java.sql.Date(car.getRegistrationDate().getTime()));
            ps.setString(5, car.getColor());
            ps.setString(6, car.getFuelType());
            ps.setInt(7, car.getOwner().getId());
            ps.setInt(8, car.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("problème de mise à jour d'une voiture");;
        } finally {
            DB.closeStatement(ps);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("DELETE FROM car WHERE id = ?");

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("problème de suppression d'une voiture");;
        } finally {
            DB.closeStatement(ps);
        }
    }

    @Override
    public Car findById(Integer id) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(
                    "SELECT c.*, o.name AS OwnerName FROM Car c INNER JOIN Owner o ON c.OwnerId = o.Id WHERE c.id = ?");

            ps.setInt(1, id);

            rs = ps.executeQuery();

            if (rs.next()) {
                Owner owner = instantiateOwner(rs);
                Car Car = instantiateCar(rs, owner);

                return Car;
            }

            return null;
        } catch (SQLException e) {
            System.err.println("problème de requête pour trouver le vendeur");
            return null;
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Car> findAll() {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(
                    "SELECT c.*, o.Name as OwnerName FROM Car c INNER JOIN Owner o ON c.OwnerId = o.Id ORDER BY c.Name");
            rs = ps.executeQuery();
            List<Car> list = new ArrayList<>();
            Map<Integer, Owner> map = new HashMap<>();

            while (rs.next()) {
                Owner owner = map.get(rs.getInt("OwnerId"));

                if (owner == null) {
                    owner = instantiateOwner(rs);

                    map.put(rs.getInt("OwnerId"), owner);
                }

                Car car = instantiateCar(rs, owner);

                list.add(car);
            }

            return list;
        } catch (SQLException e) {
            System.err.println("problème de requête pour sélectionner les voitures");
            return null;
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Car> findByOwner(Owner owner) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(
                    "SELECT c.*, o.Name as OwnerName FROM Car c INNER JOIN Owner o ON c.OwnerId = o.Id WHERE c.OwnerId = ? ORDER BY c.Name");

            ps.setInt(1, owner.getId());

            rs = ps.executeQuery();
            List<Car> list = new ArrayList<>();
            Map<Integer, Owner> map = new HashMap<>();

            while (rs.next()) {
                Owner own = map.get(rs.getInt("OwnerId"));

                if (own == null) {
                    own = instantiateOwner(rs);

                    map.put(rs.getInt("OwnerId"), own);
                }

                Car car = instantiateCar(rs, own);

                list.add(car);
            }

            return list;
        } catch (SQLException e) {
            System.err.println("problème de requête pour sélectionner les voitures d'un propriétaire donné");
            return null;
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    }

    private Car instantiateCar(ResultSet rs, Owner owner) throws SQLException {
        Car car = new Car();

        car.setId(rs.getInt("Id"));
        car.setBrand(rs.getString("Brand"));
        car.setModel(rs.getString("Model"));
        car.setRegistrationNumber(rs.getString("RegistrationNumber"));
        car.setRegistrationDate(new java.util.Date(rs.getTimestamp("RegistrationDate").getTime()));
        car.setColor(rs.getString("Color"));
        car.setFuelType(rs.getString("FuelType"));
        car.setOwner(owner);

        return car;
    }

    private Owner instantiateOwner(ResultSet rs) throws SQLException {
        Owner owner = new Owner();

        owner.setId(rs.getInt("OwnerId"));
        owner.setName(rs.getString("Name"));
        owner.setCIN(rs.getString("Cin"));
        owner.setAddress(rs.getString("Adress"));
        owner.setPhoneNumber(rs.getInt("PhoneNumber"));
        return owner;
    }
}
