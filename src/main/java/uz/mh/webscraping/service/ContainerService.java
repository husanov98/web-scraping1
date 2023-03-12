package uz.mh.webscraping.service;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class ContainerService {
    public void getData() throws InterruptedException{
        String url = "/html/body/div[1]/div[1]/div[2]/div[2]/div[2]/div[1]/div[2]/a";

        int s = 0;
        int k = 0;
        int l = 0;
        WebElement need = null;
        System.setProperty("webdriver.gecko.driver","/usr/local/bin/geckodriver");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.addArguments("--headless");
        firefoxOptions.addArguments("--no-sandbox");
        firefoxOptions.addArguments("disable-gpu");

        WebDriver driver = new FirefoxDriver();
        try {

            driver.get("https://id.egov.uz/uz");
//            driver.close();
            driver.manage().window().maximize();
            String parentWindow = driver.getWindowHandle();
            driver.findElement(By.id("login")).sendKeys("Shirinoy16012004");

            driver.findElement(By.id("password")).sendKeys("Shirinoy16012004");
            WebElement element = driver.findElement(By.xpath("//form[@novalidate='']"));
            System.out.println(element.getText());

            List<WebElement> elements = element.findElements(By.tagName("div"));
            for (WebElement webElement : elements) {

                if (s == 7) {
                    need = webElement;
                    break;
                }
                s++;
            }
            assert need != null;
            WebElement button = need.findElement(By.tagName("button"));
            button.submit();
            Thread.sleep(3000);

            WebElement jss10 = driver.findElement(By.tagName("main"));

            WebElement next = jss10.findElement(By.xpath("//a[@href='https://my.gov.uz/uz/auth/login']"));

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            wait.until(ExpectedConditions.elementToBeClickable(next));
            next.click();
            Thread.sleep(3000);

            navigate(driver,parentWindow);
            driver.findElement(By.xpath("/html/body/div[1]/div[5]/div/div[1]/div/div[3]/ul/li[15]/a")).click();
            Thread.sleep(1000);

            navigate(driver,parentWindow);
            driver.findElement(By.xpath(url)).click();

            navigate(driver,parentWindow);
            driver.findElement(By.xpath("/html/body/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/a[1]")).click();

            navigate(driver,parentWindow);
            WebElement element1 = enterData(driver, "95221693", parentWindow);
            assert element1 != null;
            System.out.println(element1.getText());
        }catch (NoSuchElementException e){
            System.out.println("Xo'jayin afsuski bunaqa narsa yo'q ekan");
        }
    }

    private WebElement enterData(WebDriver driver,String number,String parentWindow) throws InterruptedException {
        int i = 1;
        WebElement element = driver.findElement(By.xpath("//*[@id=\"select2-gtkrail-wcflag-container\"]"));
        element.click();
        WebElement element1 = driver.findElement(By.xpath("//*[@id=\"select2-gtkrail-wcflag-results\"]"));
        element1.click();

        WebElement transportNumber = driver.findElement(By.xpath("//*[@id=\"gtkrail-number\"]"));
        transportNumber.sendKeys(number);




        while (i <= 10) {
            WebElement sum = driver.findElement(By.cssSelector("#gtkrail-verifycode"));
            System.out.println(i);
            sum.sendKeys("" + i + "");
//            Set<String> windowHandles = driver.getWindowHandles();
//            System.out.println(windowHandles.size());
            WebElement check = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div[3]/form/div/div[4]/button"));
            check.submit();
            Thread.sleep(1000);
//            driver.navigate().refresh();
            try {
                WebElement result = driver.findElement(By.cssSelector(".panel"));
//            System.out.println(driver.getWindowHandles());
                if (result != null) return result;
            }catch (NoSuchElementException e){
                System.out.println("hali topilmadi");
                i++;
                if (i == 10) i = 1;
            }

        }
        return null;
    }
    private void navigate(WebDriver driver,String parentWindow) throws InterruptedException {
        Set<String> windowHandles = driver.getWindowHandles();
        String window = "";
        for (String windowHandle : windowHandles) {
            if (!Objects.equals(windowHandle, parentWindow)){
                window = windowHandle;
                break;
            }
        }
        driver.switchTo().window(window);
        Thread.sleep(2000);
    }
}
