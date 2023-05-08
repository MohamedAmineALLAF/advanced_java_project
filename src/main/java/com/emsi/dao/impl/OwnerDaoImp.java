package com.emsi.dao.impl;

import com.emsi.dao.OwnerDao;
import com.emsi.entities.Owner;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OwnerDaoImp implements OwnerDao {

    private Connection conn= DB.getConnection();
    @Override
    public void insert(Owner owner) {

        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("INSERT INTO owner (Name,CIN,Adress,PhoneNumber) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, owner.getName());
            ps.setString(2, owner.getCIN());
            ps.setString(3, owner.getAddress());
            ps.setInt(4, owner.getPhoneNumber());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = ps.getGeneratedKeys();

                if (rs.next()) {
                    int id = rs.getInt(1);

                    owner.setId(id);
                }
                DB.closeResultSet(rs);
            } else {
                System.out.println("Aucune ligne renvoyée");
            }
        } catch (SQLException e) {
            System.err.println("problème d'insertion d'un propriétaire");;
        } finally {
            DB.closeStatement(ps);
        }

    }

    @Override
    public void update(Owner owner) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("UPDATE owner SET Name = ?,Cin = ?,Adress = ?,PhoneNumber = ?  WHERE Id = ?");
            ps.setString(1, owner.getName());
            ps.setString(2,owner.getCIN());
            ps.setString(3,owner.getAddress());
            ps.setInt(4,owner.getPhoneNumber());
            ps.setInt(5, owner.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("problème de mise à jour d'un propriétaire");;
        } finally {
            DB.closeStatement(ps);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("DELETE FROM owner WHERE id = ?");

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("problème de suppression d'un propriétaire");;
        } finally {
            DB.closeStatement(ps);
        }
    }

    @Override
    public Owner findById(Integer id) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("SELECT * FROM owner WHERE id = ?");

            ps.setInt(1, id);

            rs = ps.executeQuery();

            if (rs.next()) {
                Owner owner = new Owner();

                owner.setId(rs.getInt("Id"));
                owner.setName(rs.getString("Name"));
                owner.setCIN(rs.getString("Cin"));
                owner.setAddress(rs.getString("Adress"));
                owner.setPhoneNumber(rs.getInt("PhoneNumber"));

                return owner;
            }

            return null;
        } catch (SQLException e) {
            System.err.println("problème de requête pour trouver un propriétaire");;
            return null;
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(ps);
        }
    }

    @Override
    public List<Owner> findAll() {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("SELECT * FROM owner");
            rs = ps.executeQuery();

            List<Owner> listOwner = new ArrayList<>();

            while (rs.next()) {
                Owner owner = new Owner();

                owner.setId(rs.getInt("Id"));
                owner.setName(rs.getString("Name"));
                owner.setCIN(rs.getString("Cin"));
                owner.setAddress(rs.getString("Adress"));
                owner.setPhoneNumber(rs.getInt("PhoneNumber"));

                listOwner.add(owner);
            }

            return listOwner;
        } catch (SQLException e) {
            System.err.println("problème de requête pour sélectionner un propriétaire");;
            return null;
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(ps);
        }
    }
}
