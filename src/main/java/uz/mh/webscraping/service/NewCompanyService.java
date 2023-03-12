package uz.mh.webscraping.service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class NewCompanyService {


    public XSSFWorkbook writeExcel(List<String> nameFromDoubleQuotes) throws IOException {

        String excelFile = "Clients1.xlsx";

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("9000-10000");
        System.out.println(sheet.getSheetName());

        int s = 0;

        for (String finalResult : nameFromDoubleQuotes) {
            XSSFRow row = sheet.createRow(s);
            Cell cell = row.createCell(0);
            cell.setCellValue(finalResult);
            s++;
        }

        FileOutputStream file = new FileOutputStream(excelFile);
        workbook.write(file);
        file.flush();
        file.close();
        return workbook;
    }


    public XSSFWorkbook getNewCompanies(MultipartFile file) throws IOException {
        List<String> oldCompaniesBetweenDoubleQuotes = new ArrayList<>();
        InputStream inputStream = file.getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()){
            Row currentRow = rows.next();
            Iterator<Cell> cellsInRow = currentRow.cellIterator();
            int s = 0;

            while (cellsInRow.hasNext()){
                Cell cellInRow = cellsInRow.next();
                if (s == 1){
                    String companyName = cellInRow.getStringCellValue();
                    oldCompaniesBetweenDoubleQuotes.add(companyName);
                }
                s++;
            }
        }

        List<String> nameFromDoubleQuotes = getNameFromDoubleQuotes(oldCompaniesBetweenDoubleQuotes);

        return writeExcel(nameFromDoubleQuotes);
    }
    private List<String> getNameFromDoubleQuotes(List<String> companies){

        List<String> newCompanies = new ArrayList<>();
        int zaybal = 0;
        int zaybal1 = 1;
        int am = 0;
        for (String newCompany : companies) {
            if (zaybal == 1 && zaybal1 == 0 && am == 0){
                am++;
                continue;
            }
            zaybal++;
            zaybal1--;
            if (zaybal1 == 0){
                continue;
            }
            int end = 0;
            int beg = 0;
            int k = 0;
            int s = 0;
            for (int i = 0; i < newCompany.length(); i++) {
                if (k == 0 && newCompany.charAt(i) == '"'){
                    k++;
                    beg = i;
                    continue;
                }
                if (s == 0 && newCompany.charAt(i) == '"'){
                    s++;
                    end = i;

                }
//                if ( s == 0 && k == 0) newCompanies.add(newCompany);

            }
            if (beg == 0 && end == 0) newCompanies.add(newCompany);

            try {
                String result = newCompany.substring(beg + 1,end);
                newCompanies.add(result);
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }

        }
        System.out.println(newCompanies.size());
        return newCompanies;
    }


}
