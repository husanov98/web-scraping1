package uz.mh.webscraping.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class DateService {
    public void writeDate(MultipartFile file, MultipartFile file1) throws IOException {
        List<String> newCompanies = new ArrayList<>();
        InputStream inputStream = file.getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            Row currentRow = rows.next();

            Iterator<Cell> cellsInRow = currentRow.cellIterator();
            int s = 0;
            while (cellsInRow.hasNext()) {
                Cell cellInRow = cellsInRow.next();
                if (s == 0) {
                    if (!cellInRow.getStringCellValue().equals("")) {
                        String companyName = cellInRow.getStringCellValue();
                        newCompanies.add(companyName);
                    }
                }
                s++;
            }
        }

        InputStream inputStream1 = file1.getInputStream();
        XSSFWorkbook workbook1 = new XSSFWorkbook(inputStream1);
        Sheet sheet1 = workbook1.getSheetAt(0);
        Iterator<Row> rows1 = sheet1.rowIterator();
        List<String> senders = new ArrayList<>();
        List<String> receivers = new ArrayList<>();
        Map<Integer,String> dates = new HashMap<>();


        int k = 0;
            while (rows1.hasNext()) {
                Row currentRow = rows1.next();
                Iterator<Cell> cellsInRow = currentRow.cellIterator();
                int s = 0;
                while (cellsInRow.hasNext()) {
                    Cell cellInRow = cellsInRow.next();
                    if (s == 6) {
                            String senderName = cellInRow.getStringCellValue();
                            senders.add(senderName);
                    }else if (s == 7){
                        String receiverName = cellInRow.getStringCellValue();
                        receivers.add(receiverName);
                    }
                    if (s == 5) {
                           String date = cellInRow.getStringCellValue();
                            dates.put(k,date);
                    }

                    s++;
                }
                k++;
            }


        for (String newCompany : newCompanies) {
            int index = 0;
            for (String sender : senders) {
                if (newCompany.equals(sender)){
                    String date = dates.get(index);
                    write(file,date,newCompany);
                }
                index++;
            }
        }

        for (String newCompany : newCompanies) {
            int index = 0;
            for (String receiver : receivers) {
                if (newCompany.equals(receiver)){
                    String date = dates.get(index);
                    write(file,date,newCompany);
                }
                index++;
            }
        }

    }

    private void write(MultipartFile file,String date,String company) throws IOException {
        int s = 0;
        String result = "ResultWithDate.xlsx";
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet xssfSheet = xssfWorkbook.createSheet("withDate");

        InputStream inputStream = file.getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            Row currentRow = rows.next();
            int k = 0;
            Iterator<Cell> cellsInRow = currentRow.cellIterator();
            String zaybalDate = "";
            while (cellsInRow.hasNext()) {
                Cell cellInRow = cellsInRow.next();
                if (k == 0) {
                    if (!cellInRow.getStringCellValue().equals(company)) {
                        zaybalDate = cellInRow.getStringCellValue();
                        XSSFRow xssfRow = xssfSheet.createRow(s);
                        XSSFCell companyName = xssfRow.createCell(0);
                        companyName.setCellValue(company);
                        XSSFCell cameDate = xssfRow.createCell(1);
                        cameDate.setCellValue(date);
                    }
                }
                k++;
                Cell cellDate = currentRow.createCell(s);
                cellDate.setCellValue(zaybalDate);
            }
            s++;
        }

        FileOutputStream file1 = new FileOutputStream(result);
        xssfWorkbook.write(file1);
        file1.flush();
        file1.close();
    }
}
