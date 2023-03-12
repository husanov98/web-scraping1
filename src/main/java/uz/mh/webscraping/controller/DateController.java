package uz.mh.webscraping.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uz.mh.webscraping.service.DateService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
@RestController
public class DateController {
    private final DateService dateService;

    public DateController(DateService dateService) {
        this.dateService = dateService;
    }

    @PostMapping(value = "/writeDates", consumes = {"multipart/form-data"})
    public void getData(@RequestParam(name = "newCompanies") MultipartFile file, @RequestParam(name = "companiesWithDate") MultipartFile file1) throws IOException, InvocationTargetException {
        dateService.writeDate(file,file1);
    }
}
