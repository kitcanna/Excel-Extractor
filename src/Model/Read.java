package Model;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import Controller.Controller;
import Model.Enums.Conveyors;
import Model.Enums.Kits;
import Model.Enums.Machines;

/**
 * @author Anna Selstam, 2022-06-30
 * 
 * 
 */
public class Read {
    private WriteTable tt = new WriteTable(this);

    private static Object[][] firstList;
    private static Object[][] finalList;

    private static int rowCounter;
    
    private Controller controller;
    private String supplier = "";
    private String wantedDate = "";

    private LocalDate date = LocalDate.now();


    public Read(Controller controller, List<String> files) throws IOException {

        this.controller = controller;
        
        rowCounter = 0;
        
        firstList = new Object[50][14]; 
        
        for (String s : files) {

            File file = new File(s);

            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);

            int numOfSheets = workbook.getNumberOfSheets();

            for (int i = 0; i < numOfSheets; i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);

                if (sheet.getSheetName().contains("Daily")) {
                    
                    String wanted = sheet.getRow(0).getCell(0).getStringCellValue();     
                    supplier = findSupplier(file.getAbsolutePath(), wanted);

                    if (supplier == "SPECIAL_FORMAT_SUPPLIER") {

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        wantedDate = date.format(formatter);

                    }
                    else {

                        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("M/d/yy");
                        wantedDate = date.format(formatter2);

                    }
            
                    Iterator<Row> rowIterator = sheet.iterator();
        
                    while (rowIterator.hasNext() && rowIterator != null) {
                        Row row = rowIterator.next();
                        Iterator<Cell> cellIterator = row.cellIterator();
                        
                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();
        
                            switch (cell.getCellType()) {
        
                                case NUMERIC:
                                String cellValue = new DataFormatter().formatCellValue(row.getCell(cell.getColumnIndex()));
        
                                //if todays date is found, save it to an array
                                if (cellValue.equals(wantedDate)) {

                                    firstList[rowCounter][0] = supplier;
        
                                    for (Cell c : row) {
        
                                        if (c.getCellType() == CellType.NUMERIC) {
                                            
                                            String cellV = new DataFormatter().formatCellValue(c, workbook.getCreationHelper().createFormulaEvaluator());
                                            firstList[rowCounter][c.getColumnIndex() + 1] = cellV;
        
                                        } else {
        
                                            firstList[rowCounter][c.getColumnIndex() + 1] = c.toString();
        
                                        }
                                    }
                                    rowCounter++;
                                }
                                    break;
        
                                default:
                                    break;
                            }
                        }
                    }
                }
            }

            fis.close();

        }

        /**
         * If there is any data that matched the date,
         * transfer it and print it to a new table.
         */
        if (rowCounter > 0) {

            finalList = new Object[rowCounter][14];

            for (int i = 0; i < firstList.length; i++) { // for every row
                    
                for (int j = 0; j < firstList[i].length; j++) { // for every column

                    if (firstList[i][j] != null) { // if the value isn't null
    
                        String type = checkType((String) firstList[i][1]);
                        finalList[i][13] = type;

                        finalList[i][j] = firstList[i][j];
                    }
                }
            }
                
        tt.writeTable(finalList);

        }

        else {
            setInfoText("No data matching the date.");
        }
    }


    private String checkType(String text) {
        String t = text.toUpperCase().replace(("/"), " ").replace("®", "").replace(",", "").replace("(", "").replace(")", "");
        String type = "";
        
        for (Conveyors c : Conveyors.values()) {
            if (t.equals(c.toString().replace("_", " "))) {
                type = "Conveyor";
            }
        }       
        
        for (Machines m : Machines.values()) {
            if (t.contains(m.toString().replace("_", " "))) {
                if (t.contains("KIT")) {
                    break;
                }
                else {
                    type = "Machine";
                }
            }
        }

        for (Kits k : Kits.values()) {
            if (t.contains(k.toString().replace("_", " "))) {
                type = "Kit";
            }
        }

        if (type.isEmpty()) {
            type = "N/A";
        }

        return type;
    }

    /**
     * 
     * @param wanted
     * @param firstList2 - filename
     * @param wanted - R0C0 (header) of the excel
     * @return supplier name
     */
    private String findSupplier(String id, String wanted) {
        String supplier = null; 
        if (wanted == null) {
            System.out.println("Sheet empty.");
        }
        else {
            String i = id.toUpperCase();
            String w = wanted.toUpperCase();

            if (i.contains("Supplier 1") || w.contains("1")) {
                supplier = "Supplier 1";
            } 
            else if (i.contains("Supplier 2") && !i.contains("2"))  {
                supplier = "Supplier 2";
            }
            else if (i.contains("Supplier 3") && i.contains("3")) {
                supplier = "Supplier 3";
            }
            else {
                supplier = "Other";
            }
        }
        
        return supplier;
    }

    public void setInfoText (String text) {
        controller.writeStatusInfo(text);
    }

}
