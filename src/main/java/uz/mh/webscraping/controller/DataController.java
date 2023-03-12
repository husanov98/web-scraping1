package uz.mh.webscraping.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uz.mh.webscraping.service.DataTransformationService;
import uz.mh.webscraping.service.NewCompanyService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
public class DataController {
    private final NewCompanyService newCompanyService;

    public DataController(NewCompanyService newCompanyService) {
        this.newCompanyService = newCompanyService;
    }

    @PostMapping(value = "/getOldCompaniesBetweenDoubleQuotes", consumes = {"multipart/form-data"})

    public void getNewCompanies(@RequestPart(name = "file") MultipartFile file) throws IOException {

        newCompanyService.getNewCompanies(file);

    }

}
