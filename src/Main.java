import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        System.out.println("Automatic HILANET check-in for WIX employers.\n" +
                "Version 1.02.\n" +
                "(C) Olga Chertkov, olga@chertkov.info, 2020\n" +
                "https://github.com/olgachertkova/DailyCheckInHilanet\n");
        if(args.length!=2){
            System.out.println("ERROR: Required parameters not found.\n" +
                    "Usage: java -jar DailyCheckInHilanet.jar [Employee_number] [Password]\n");
            System.exit(127);
        }
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        options.addArguments("window-size=1800x900");

        WebDriver driver = new ChromeDriver(options);
        System.out.println("--- Initialize driver  ---");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        String userNum = args[0];
        System.out.println("User number is: " + userNum);
        String pass = args[1];
        System.out.println("Password is: **masked**");

        driver.get("https://wix.net.hilan.co.il/login?lang=en");
        System.out.println("--- Open site ---");

//        log in to system
        WebElement loginField = driver.findElement(By.id("user_nm"));
        loginField.sendKeys(userNum);
        System.out.println("--- Enter user number ---");
        WebElement passwordField = driver.findElement(By.id("password_nm"));
        passwordField.sendKeys(pass);
        System.out.println("--- Enter password ---");
        WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(),'Login')]"));
        loginButton.click();
        System.out.println("--- Click submit ---");

//        checkbox page - click on checkbox and submit
        try {
            WebElement checkBox = driver.findElement(By.id("chkApprove"));
            WebElement labelCheckBox = driver.findElement(By.xpath("//label[@class='custom-control-label']"));
            if (!checkBox.isSelected()){
                labelCheckBox.click();
                System.out.println("--- Click on check box ---");
            }
            WebElement explicitWait = (new WebDriverWait(driver, 30))
                    .until(ExpectedConditions.elementToBeClickable(By.id("btnSubmit")));

            WebElement submitButton = driver.findElement(By.id("btnSubmit"));
            submitButton.click();
            System.out.println("--- Click submit ---");
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());

        }

        try {
            driver.findElement(By.id("btnContinue")).click();
            System.out.println("--- Click Continue button ---");
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }


//        select on attendence menu
        WebElement attendanceMenu = driver.findElement(By.id("tabItem_9_3_SpanBackground"));
        attendanceMenu.click();

        WebElement reportUpdate= driver.findElement(By.id("innerNavBarItem_47"));
        reportUpdate.click();
        System.out.println("--- Select in attendence menu ---");

//        select type of work and save
        driver.switchTo().frame("mainIFrame");

        Select workTypeSelector = new Select(driver.findElement(By.cssSelector("td>select")));
        workTypeSelector.selectByVisibleText("Work from home");

        WebElement saveButton = driver.findElement(By.cssSelector("input[value='Save']"));
        saveButton.click();
        System.out.println("--- Select type of work and save ---");

//        switch to alert window and submit
        driver.switchTo().frame("alertFrame");
        String message = driver.findElement(By.id("messagePlace")).getText();
        WebElement okButton = driver.findElement(By.xpath("//td[contains(text(),'OK')]"));
        okButton.click();
        System.out.println("--- Switch to alert window and submit ---");

        System.out.println("Message in alert: " + message);

//        asserting
        assert message.contains("Data saved successfully"): "SUCCESSFULLY";
        driver.quit();
    }
}
