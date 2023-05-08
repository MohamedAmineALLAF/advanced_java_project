package com.emsi;

import com.emsi.entities.Car;
import com.emsi.entities.Owner;
import com.emsi.service.CarService;
import com.emsi.service.OwnerService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Cell;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void readFromTextFile(ArrayList<Car> list) {

        try {
            BufferedReader br = new BufferedReader(new FileReader("src/main/resources/InputData.txt"));
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
                c.setFuelType(car[5].trim());
                list.add(c);
                readLine = br.readLine();
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    static XSSFRow row;
    public static void writeInOutputFile(ArrayList<Car> list){
        try( FileOutputStream fout = new FileOutputStream("src/main/resources/outputData.txt"))
        {

            for(Car car : list){
                fout.write(car.toString().getBytes());
                fout.write('\n');

                System.out.println("car :"+car.toString());
            }
        }
        catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }

    public static void CreateAndWriteInStyleSheet() throws Exception{
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
                cell.setCellValue((String)obj);
            }
        }

        //Ecrire les données dans un FileOutputStream
        FileOutputStream out = new FileOutputStream(new File("src/main/resources/inputDataX.xlsx"));
        workbook.write(out);
        out.close();
        System.out.println("Travail bien fait!!!");
    }

    public static void readJSONFromTxtFile() throws FileNotFoundException, IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("src/main/resources/inputDataJson.txt"));
        JSONObject jsonObject = (JSONObject) obj;

        String jsonObjectList = (String)jsonObject.get("carList").toString();
        System.out.println(jsonObjectList);
        Gson gson = new Gson();
        Car[] carArray = gson.fromJson(jsonObjectList, Car[].class);


        for(Car obj2 : carArray) {
            System.out.println(obj2.toString());
        }
    }

    public static void readFromStyleSheet(){
        try(FileInputStream fis = new FileInputStream(new File("src/main/resources/carInfo.xlsx")))
        {
            XSSFWorkbook workbook1 = new XSSFWorkbook(fis);
            XSSFSheet spreadsheet1 = workbook1.getSheetAt(0);
            Iterator <Row>  rowIterator = spreadsheet1.iterator();

            while (rowIterator.hasNext()) {
                row = (XSSFRow) rowIterator.next();
                Iterator < Cell >  cellIterator = row.cellIterator();

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

    public static void main( String[] args ) throws Exception {
        ArrayList<Car> list = new ArrayList<Car>();
        System.out.println(list);
        System.out.println("----text----");
        readFromTextFile(list);
        System.out.println(list);
        writeInOutputFile(list);
        System.out.println("----xlsx----");
        CreateAndWriteInStyleSheet();
        readFromStyleSheet();
        System.out.println("----json----");
        readJSONFromTxtFile();
        System.out.println("----JDBC----");

        OwnerService ownerService = new OwnerService();
        for( Owner owner :ownerService.findAll())
            System.out.println(owner);

        Owner owner1 = new Owner(6,"amine","allaf","Tangier",0674);
        ownerService.save(owner1);
        System.out.println("added");
    }
}
