package uz.mh.webscraping.controller;


import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
//import uz.mh.webscraping.service.ExtractContainerDataService;
//import uz.mh.webscraping.service.ExtractDataOzInfoService;
import uz.mh.webscraping.service.ContainerService;
import uz.mh.webscraping.service.DataTransformationService;
import uz.mh.webscraping.service.ExtractNumberService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

//import static org.springframework.core.env.ProfilesParser.not;

@RestController
public class ExtractDataController {
    private final ExtractNumberService numberService;
    private final ContainerService containerService;
    private DataTransformationService transformationService;
//    private final ExtractDataOzInfoService infoService;
//    private final ExtractContainerDataService containerDataService;

    public ExtractDataController(ExtractNumberService numberService, ContainerService containerService,
                                 DataTransformationService transformationService) {
        this.numberService = numberService;
//        this.infoService = infoService;
//        this.containerDataService = containerDataService;
        this.containerService = containerService;
        this.transformationService = transformationService;
    }


    @PostMapping(value = "/getTruckData", consumes = {"multipart/form-data"})

    public ResponseEntity getSomething(@RequestPart(name = "file") MultipartFile file, HttpServletResponse response) throws IOException {
        XSSFWorkbook dataByNumber = numberService.getDataByNumber(file);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        dataByNumber.write(bos);
        bos.close();

        return ResponseEntity.ok().body(bos.toByteArray());
    }

//    @PostMapping(value = "/getPhoneNumbers", consumes = {"multipart/form-data"})
//    public void getPhoneNumbers(@RequestPart(name = "file") MultipartFile file) throws IOException {
//        infoService.getPhoneNumbers(file);
//    }

//    @GetMapping("/getWagonData")
//    public void getWagonData(@RequestParam(name = "wagonNumber") String wagonNumber) throws IOException {
//        containerDataService.getDataByNumber(wagonNumber);
//    }

    @PostMapping(value = "/login")
    public void login()throws InterruptedException{
        containerService.getData();
    }

    @PostMapping(value = "/checkCompanies", consumes = {"multipart/form-data"})

    public void getData(@RequestParam(name = "file") MultipartFile file,@RequestParam(name = "file1") MultipartFile file1) throws IOException, InvocationTargetException {
        transformationService.getCompanies(file,file1);

    }

//    @PostMapping(value = "/oldCompanies", consumes = {"multipart/form-data"})
//
//    public void getOldData(@RequestPart(name = "file") MultipartFile file) throws IOException {
//        transformationService.oldCompanies(file);
//
//    }
}