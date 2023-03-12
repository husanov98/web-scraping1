//package uz.mh.webscraping.service;
//
//import jakarta.annotation.PostConstruct;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@AllArgsConstructor
//@Service
//public class WordsService {
//    private static final String url = "https://relatedwords.org/relatedto/";
//
//    private final WebDriver driver;
//
//    @PostConstruct
//    void postContract(){
//        wordService("Fishsticks");
//    }
//
//
//    public void wordService(final String value){
//        driver.get(url + value);
//        final WebElement words = driver.findElement(By.className("words"));
//        final List<WebElement> wrodList = words.findElements(By.tagName("a"));
//        wrodList.forEach(word -> System.out.println(word.getText()));
//        driver.quit();
//    }
//}
