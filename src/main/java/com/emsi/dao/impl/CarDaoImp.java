package com.emsi.dao.impl;

import com.emsi.dao.CarDao;
import com.emsi.entities.Car;
import com.emsi.entities.Owner;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
            ps.setString(6, car.getFuelType().name());
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
            ps.setString(6, car.getFuelType().name());
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

    @Override
    public List<Car> readFromTextFile(FileReader fileReader) {
        ArrayList<Car> list = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(fileReader);
            Car c = null;
            String readLine = br.readLine();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            while(readLine != null){
                String [] car  = readLine.split(",");
                c = new Car();
                c.setBrand(car[0].trim());
                c.setModel(car[1].trim());
                c.setRegistrationNumber(car[2].trim());
                c.setRegistrationDate(dateFormat.parse(car[3].trim()));
                c.getRegistrationDate();
                c.setColor(car[4].trim());
                c.setFuelType(Car.FuelType.valueOf(car[5].trim().toUpperCase()));;
                list.add(c);
                readLine = br.readLine();
            }
            System.out.println(list);

        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void writeInOutputFile(String path) {
        ArrayList<Car> list = new ArrayList<>();
        try( FileOutputStream fout = new FileOutputStream(path))
        {
            for(Car car : list){
                fout.write(car.toString().getBytes());
                fout.write('\n');
                System.out.println("car :"+car.toString());
            }
            System.out.println("Ecriture avec succès");
        }
        catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }

    @Override
    public void readFromStyleSheet(File file) {

        XSSFRow row;
        try(FileInputStream fis = new FileInputStream(file))
        {
            XSSFWorkbook workbook1 = new XSSFWorkbook(fis);
            XSSFSheet spreadsheet1 = workbook1.getSheetAt(0);
            Iterator<Row> rowIterator = spreadsheet1.iterator();

            while (rowIterator.hasNext()) {
                row = (XSSFRow) rowIterator.next();
                Iterator <Cell>  cellIterator = row.cellIterator();

                while ( cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    System.out.print(cell.toString()+"\t\t");

                }
                System.out.println();
            }
        }
        catch (FileNotFoundException e) {
            // TODO: handle exception
        }
        catch (IOException e) {
            // TODO: handle exception
        }
    }

    @Override
    public void CreateAndWriteInStyleSheet(File file) {

        try{
            XSSFWorkbook workbook = new XSSFWorkbook();

            //Création d'un objet de type feuille Excel
            XSSFSheet spreadsheet = workbook.createSheet(" Car Info ");

            //Création d'un objet row (ligne)
            XSSFRow row;

            //Les données à inserer;
            Map< String, Object[] > carinfo =
                    new TreeMap< String, Object[] >();
            carinfo.put( "1", new Object[] { "Brand", "Model", "registrationNumber","year","color","Fuel type" });
            carinfo.put( "2", new Object[] { "Honda", "Civic", "AB-123-CD","2020-10-31","Red","Gasoline" });
            carinfo.put( "3", new Object[] { "Ford", "Mustang", "EF-489-EZ","2016-10-31","Blue","Electric" });

            //parcourir les données pour les écrire dans le fichier Excel
            Set< String > keyid = carinfo.keySet();
            int rowid = 0;

            for (String key : keyid) {
                row = spreadsheet.createRow(rowid++);
                Object [] objectArr = carinfo.get(key);
                int cellid = 0;

                for (Object obj : objectArr) {
                    Cell cell = row.createCell(cellid++);
                    cell.setCellValue((String)obj);//fix
                }
            }

            //Ecrire les données dans un FileOutputStream
            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
            System.out.println("Travail bien fait!!!");

        }catch (IOException e){
            e.printStackTrace();
        }


    }

    @Override
    public void readJsonFromTextFile(String path) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(path));
            JSONObject jsonObject = (JSONObject) obj;

            String jsonObjectList = (String)jsonObject.get("carList").toString();
            System.out.println(jsonObjectList);
            Gson gson = new Gson();
            Car[] carArray = gson.fromJson(jsonObjectList, Car[].class);

            for(Car obj2 : carArray) {
                System.out.println(obj2.toString());
            }

        }catch (IOException | org.json.simple.parser.ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    public void readFromDatabaseToTextFile(String path) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Car> listCar = new ArrayList<>();
        try {
            ps = conn.prepareStatement("SELECT * FROM car");
            rs = ps.executeQuery();
            FileOutputStream fout = new FileOutputStream(path);

            while (rs.next()) {
                Owner owner = instantiateOwner(rs);

                Car car = instantiateCar(rs, owner);

                listCar.add(car);

            }
            for(Car car : listCar){
                fout.write(car.toString().getBytes());
                fout.write('\n');
                System.out.println("owner :"+car.toString());
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.err.println("problème de requête pour sélectionner un propriétaire");;
        }finally {
            DB.closeResultSet(rs);
            DB.closeStatement(ps);
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
        String fuelTypeString = rs.getString("FuelType");
        if (fuelTypeString != null) {
            try {
                Car.FuelType fuelType = Car.FuelType.valueOf(fuelTypeString.toUpperCase());
                car.setFuelType(fuelType);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        car.setOwner(owner);

        return car;
    }

    private Owner instantiateOwner(ResultSet rs) throws SQLException {
        Owner owner = new Owner();
        owner.setId(rs.getInt("OwnerId"));
        owner.setName(rs.getString("name"));
        owner.setCIN(rs.getString("cin"));
        owner.setAddress(rs.getString("adress"));
        owner.setPhoneNumber(rs.getInt("phoneNumber"));
        return owner;
    }
}
