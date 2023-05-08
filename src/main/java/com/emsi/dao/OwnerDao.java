package com.emsi.dao;

import com.emsi.entities.Owner;

import java.util.List;

public interface OwnerDao {
    void insert(Owner owner);

    void update(Owner owner);

    void deleteById(Integer id);

    Owner findById(Integer id);

    List<Owner> findAll();


}
