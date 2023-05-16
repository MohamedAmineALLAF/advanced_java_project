package com.emsi.service;

import com.emsi.dao.OwnerDao;
import com.emsi.dao.impl.OwnerDaoImp;
import com.emsi.entities.Car;
import com.emsi.entities.Owner;

import java.io.FileReader;
import java.util.List;

public class OwnerService {
    private OwnerDao ownerDao =new OwnerDaoImp();

    public List<Owner> findAll() {
        return ownerDao.findAll();
    }

    public void save(Owner owner) {

        ownerDao.insert(owner);

    }
    public void update(Owner owner) {
        ownerDao.update(owner);
    }
    public void remove(Owner owner) {
        ownerDao.deleteById(owner.getId());
    }

    public List<Owner> readFromTextFile(FileReader fileReader){
        return ownerDao.readFromTextFile(fileReader);
    }

    public void readFromDatabaseToTextFile(){
        ownerDao.readFromDatabaseToTextFile();
    }

    public void readFromStyleSheetAndInsertInDatabase(String path){
        ownerDao.readFromStylSheetAndInsertInDatabase(path);
    }

}
