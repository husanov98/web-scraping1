package uz.mh.webscraping.service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.mh.webscraping.model.TruckData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExtractNumberService {
    public XSSFWorkbook getDataByNumber(MultipartFile file) throws IOException {

        int s = 0;

        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        List<String> numbers = readFromExcel(file);


        List<TruckData> truckDataList = new ArrayList<>();
        for (String number : numbers) {
            TruckData truckData = getTruckData(number, client);
            s++;
            truckDataList.add(truckData);
            System.out.println(s);
        }
        return writeExcel(truckDataList,file.getName());
    }

    private List<String> readFromExcel(MultipartFile file) throws IOException {

        List<String> numbers = new ArrayList<>();

        InputStream inputStream = file.getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        System.out.println(sheet.getSheetName());
        Iterator<Row> rows = sheet.rowIterator();

        while (rows.hasNext()) {
            Row currentRow = rows.next();

            Iterator<Cell> cellsInRow = currentRow.cellIterator();
            while (cellsInRow.hasNext()) {
                Cell cellInRow = cellsInRow.next();
                String number = cellInRow.getStringCellValue();
                numbers.add(number);
            }
        }
        return numbers;
    }

    private TruckData getTruckData(String number, WebClient client) throws IOException {
        String url = "https://my.gov.uz/oz/gtk-auto";
        HtmlPage page = client.getPage(url);
        HtmlForm form = (HtmlForm) page.getByXPath("//form").get(0);
//        page.getByXPath("");
        HtmlInput inputNumber = form.getInputByName("GtkAuto[number]");

        inputNumber.type(number);
        HtmlButton submitButton = (HtmlButton) form.getByXPath("//button").get(0);
        HtmlPage resultPage = submitButton.click();
        TruckData truckData = parseResult(resultPage);

        truckData.setTruckNumber(number);

        System.out.println(truckData);
        return truckData;
    }

    private TruckData parseResult(HtmlPage resultPage) {
        TruckData data = new TruckData();

        try {

            HtmlTable table = (HtmlTable) resultPage.getByXPath("//table").get(0);
            List<HtmlTableRow> rows = table.getBodies().get(0).getRows();

            for (HtmlTableRow row : rows) {
                if (row.getCell(0).getTextContent().equals("Avtotransport raqami")) {
                    data.setTruckNumber(row.getCell(1).getTextContent());
                    continue;
                }
                if (row.getCell(0).getTextContent().contains("Chiqish")) {
                    data.setExitDate(row.getCell(1).getTextContent());
                    continue;
                }
                if (row.getCell(0).getTextContent().contains("Bojxona")) {
                    data.setCustomEnterPost(row.getCell(1).getTextContent());
                    continue;
                }
                if (row.getCell(0).getTextContent().contains("KKDG")) {
                    data.setBookNumber(row.getCell(1).getTextContent());
                    continue;
                }
                if (row.getCell(0).getTextContent().contains("Belgilangan")) {
                    data.setCustomsExitPost(row.getCell(1).getTextContent());
                    continue;
                }
                if (row.getCell(0).getTextContent().contains("Kelish")) {
                    data.setEnterDate(row.getCell(1).getTextContent());
                    continue;
                }
                if (row.getCell(0).getTextContent().equals("Yuk jo‘natuvchining nomi")) {
                    data.setSenderName(row.getCell(1).getTextContent());
                    continue;
                }
                if (row.getCell(0).getTextContent().equals("Yuk qabul qiluvchining nomi")) {
                    data.setReceiverName(row.getCell(1).getTextContent());

                }

            }


        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }


        return data;
    }

    private XSSFWorkbook writeExcel(List<TruckData> data,String fileName) throws IOException {
        if (data == null) {
            System.out.println("Error : No data to write.");
            return null;
        }

        String excelFile = fileName + ".xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Truck data");

        XSSFRow row1 = sheet.createRow(0);

        int rowId = 1;

        try {

            Cell cell11 = row1.createCell(0);
            cell11.setCellValue("Номер автотранспорта");

            Cell cell12 = row1.createCell(1);
            cell12.setCellValue("Дата и время отправления (въезда)");

            Cell cell13 = row1.createCell(2);
            cell13.setCellValue("Таможенный пост отправления (въезда)");

            Cell cell14 = row1.createCell(3);
            cell14.setCellValue("номер ККДГ или книжки МДП");

            Cell cell15 = row1.createCell(4);
            cell15.setCellValue("Таможенный пост назначения (выезда)");

            Cell cell16 = row1.createCell(5);
            cell16.setCellValue("Дата и время прибытия");

            Cell cell17 = row1.createCell(6);
            cell17.setCellValue("Наименование грузоотправителя");

            Cell cell18 = row1.createCell(7);
            cell18.setCellValue("Наименование грузополучателя");

            for (TruckData datum : data) {
                XSSFRow row = sheet.createRow(rowId++);

                Cell cell1 = row.createCell(0);
                cell1.setCellValue(datum.getTruckNumber());

                Cell cell2 = row.createCell(1);
                cell2.setCellValue(datum.getExitDate());

                Cell cell3 = row.createCell(2);
                cell3.setCellValue(datum.getCustomEnterPost());

                Cell cell4 = row.createCell(3);
                cell4.setCellValue(datum.getBookNumber());

                Cell cell5 = row.createCell(4);
                cell5.setCellValue(datum.getCustomsExitPost());

                Cell cell6 = row.createCell(5);
                cell6.setCellValue(datum.getEnterDate());

                Cell cell7 = row.createCell(6);
                cell7.setCellValue(datum.getSenderName());

                Cell cell8 = row.createCell(7);
                cell8.setCellValue(datum.getReceiverName());

            }

            FileOutputStream file = new FileOutputStream(excelFile);
            workbook.write(file);
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }
}