package Model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;

public class WriteTable {
        private static XSSFWorkbook workbook;
        private static XSSFSheet sheet;

        /**
         * TODO - make the length of the list static,
         * but it needs to be initialized just once.
         */

        private static int listLength = 0;

        private static Object[][] finalList = new Object[50][14]; //14 columns

        private static String[] headers = {"SUPPLIER", "MACHINE TYPE", "MACHINE NR", "SALES NR", "MARKET", "DELIVERY STATUS", "PLANNED DATE", "NEW DATE", "WARNING", "WARNING REASON", "RISK OF DELAY", "EXPLANATORY TEXT", "SUPPLIER ACTION TAKEN", "LABEL"};

        private static LocalDate date = LocalDate.now();
        private static String filePath = date+"_orders.xlsx";

        private static int cols = 14;
        private static int rows = 0;
        private static int listPlace = 0;

        private static Read read;

        public WriteTable (Read read) throws FileNotFoundException, IOException {
            this.read = read; 
            workbook = new XSSFWorkbook();
        }

        public void compileList (Object[][] data) {
            
            for (int i = 0; i < data.length; i ++) {
            
                for (int j = 0; j < data[i].length; j++) {
    
                    finalList[listPlace][j] = data[i][j];
                    
                }

                listPlace++;
            }

        }


        public void writeTable (Object[][] data) throws FileNotFoundException, IOException {

            compileList(data);

            rows = finalList.length + 1; // +1 for headers
            
            sheet = workbook.createSheet("New Orders");

            XSSFTable table = sheet.createTable(null);
            CTTable cttable = table.getCTTable();

            cttable.setDisplayName("Table1");
            cttable.setId(1);
            cttable.setName("Test");
            cttable.setRef("A1:N"+rows);
            cttable.setTotalsRowShown(false);

            CTTableStyleInfo styleInfo = cttable.addNewTableStyleInfo();
            styleInfo.setName("TableStyleMedium2");
            styleInfo.setShowColumnStripes(false);
            styleInfo.setShowRowStripes(true);
        
            CTTableColumns columns = cttable.addNewTableColumns();
            columns.setCount(cols);

            for (int i = 1; i <= cols; i++) {
                CTTableColumn column = columns.addNewTableColumn();
                column.setId(i);
                column.setName(headers[i-1]);
            }
        
            for (int r = 0; r < rows; r++) {

                XSSFRow row = sheet.createRow(r);

                for(int c = 0; c < cols; c++) {

                    XSSFCell cell = row.createCell(c);

                    if(r == 0) { //first row is for column headers

                        cell.setCellValue(headers[c]); //column names

                    } 

                    else {

                        Object value = finalList[r-1][c];

                        if (value instanceof String) {

                            cell.setCellValue((String) value);  

                        }
                        if (value instanceof Integer) {

                            cell.setCellValue((int) value);    

                        }
                    }
                    sheet.autoSizeColumn(c);
                }
            }    
        
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
                workbook.close();
                outputStream.close();

                read.setInfoText("Success in writing "+ data.length +" items");
            }
        }
}
