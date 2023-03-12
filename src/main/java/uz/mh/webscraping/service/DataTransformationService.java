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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class DataTransformationService {

    private final NewCompanyService newCompanyService;

    public DataTransformationService(NewCompanyService newCompanyService) {
        this.newCompanyService = newCompanyService;
    }

//    public void getWebPageFromUzOrginfo() throws IOException {
//        WebClient client = new WebClient();
//        client.getOptions().setJavaScriptEnabled(false);
//        client.getOptions().setCssEnabled(false);
//
//        String url = "https://uzorg.info/";
//        HtmlPage page = client.getPage(url);
//    }
    public XSSFWorkbook getCompanies(MultipartFile file,MultipartFile file1) throws IOException, InvocationTargetException {
        int count = 1;
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);

        List<String> companies = new ArrayList<>();

        List<String> newcompanies = new ArrayList<>();
        InputStream inputStream = file.getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        System.out.println(workbook.getNumberOfSheets());
        System.out.println(sheet.getSheetName());
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            Row currentRow = rows.next();

            Iterator<Cell> cellsInRow = currentRow.cellIterator();
            int s = 0;
            while (cellsInRow.hasNext()) {
                Cell cellInRow = cellsInRow.next();
                if ( s==6 || s==7 ) {
                    if (!cellInRow.getStringCellValue().equals("")) {
                        String companyName = cellInRow.getStringCellValue();
                        companies.add(companyName);
                    }
                }
                s++;
            }



        }
        List<String> nameFromDoubleQuotes = getNameFromDoubleQuotes(companies);
        List<String> oldCompanies = oldCompanies(file1);
//        List<String> oldCompanies1 = getNameFromDoubleQuotes(oldCompanies);
        List<String> finalResults = checkTwoLists(oldCompanies, nameFromDoubleQuotes);

        for (String finalResult : finalResults) {
            String newCompany = getNewCompanies(finalResult,client);
            if (newCompany != null) {
                newcompanies.add(newCompany);
                System.out.println(count);
                count++;
            }
        }
        return writeExcel(newcompanies);
    }

//    private List<String> getDates(List<String> newCompanies,List<String> dates){
//
//    }
    private XSSFWorkbook writeExcel(List<String> finalResults) throws IOException {

        String excelFile = "Result.xlsx";

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("9000-10000");
        System.out.println(sheet.getSheetName());
        Iterator<Row> rowIterator = sheet.rowIterator();
        int s = 0;
        for (String finalResult : finalResults) {
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

    public List<String> oldCompanies(MultipartFile file) throws IOException {
        List<String> companies1 = new ArrayList<>();
        InputStream inputStream = file.getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        System.out.println(sheet.getSheetName());
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            Row currentRow = rows.next();

            Iterator<Cell> cellsInRow = currentRow.cellIterator();
            int s = 0;
            while (cellsInRow.hasNext()) {
                Cell cellInRow = cellsInRow.next();
                if ( s==1 ) {
                    if (!cellInRow.getStringCellValue().equals("")) {
                        String companyName = cellInRow.getStringCellValue();
                        companies1.add(companyName);
                    }
                }
                s++;
            }
        }
        return companies1;

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

    private List<String> checkTwoLists(List<String> list1,List<String> list2){
        List<String> resultList = new ArrayList<>();
        for (String s2 : list2) {
            int n = 0;
            for (String s1 : list1) {
                if (s2.toUpperCase().equals(s1.toUpperCase())) {
                    n++;
                }
            }
            if (n == 0) {
                resultList.add(s2);
                list1.add(s2);
            }
        }
        return resultList;
    }

    public String getNewCompanies(String companyName,WebClient client) throws IOException, InvocationTargetException {

        String url = "https://uzorg.info/search/main/" + companyName + "/";
        HtmlPage page = client.getPage(url);

        try {

            int s = 0;
            HtmlElement divisions = (HtmlElement) page.getByXPath("//div[@class='list-group']").get(0);
            List<HtmlElement> anchors = divisions.getByXPath("//a[@class='list-group-item list-group-item-action flex-column align-items-start']");
            for (HtmlElement element : anchors) {
                HtmlElement status = (HtmlElement) element.getByXPath("//font").get(0);
                HtmlElement name = (HtmlElement) element.getByXPath("//b").get(0);
                String fromBetweenQuotes = getFromBetweenQuotes(name.asNormalizedText());
                if (status.asNormalizedText().equals("Работающий") && companyName.toUpperCase().equals(fromBetweenQuotes)) {
                    return fromBetweenQuotes;
                } else if (status.asNormalizedText().equals("Работающий") && companyName.toUpperCase().contains(fromBetweenQuotes)) {
                    return fromBetweenQuotes;
                } else if (status.asNormalizedText().equals("Работающий") && fromBetweenQuotes.contains(companyName.toUpperCase())) {
                    return fromBetweenQuotes;
                }

            }
        }catch (IndexOutOfBoundsException e ){
            e.printStackTrace();
        }

        return null;
    }
    private String getFromBetweenQuotes(String name){
        int end = 0;
        int beg = 0;
        int k = 0;
        int s = 0;
        for (int i = 0; i < name.length(); i++) {
            if (k == 0 && name.charAt(i) == '"'){
                k++;
                beg = i;
                continue;
            }
            if (s == 0 && name.charAt(i) == '"'){
                s++;
                end = i;

            }

        }
        if (beg == 0 && end == 0) return name;
        String result = "";
        try {
            result = name.substring(beg + 1,end);

        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return result;
    }
}
