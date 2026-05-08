package tests;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.*;
import java.time.Duration;

public class FirTests {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(60));

        String appUrl = System.getProperty("app.url", "http://44.212.91.126:8090/");
        driver.get(appUrl);

        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("[data-testid='stAppViewContainer']")
        ));

        try { Thread.sleep(3000); } catch (InterruptedException e) {}
    }

    // 1
    @Test
    public void testPageLoads() {
        Assert.assertTrue(driver.getTitle().length() > 0);
    }

    // 2
    @Test
    public void testAppContainerLoads() {
        Assert.assertTrue(driver.getPageSource().contains("stAppViewContainer"));
    }

    // 3
    @Test
    public void testMainTitle() {
        Assert.assertTrue(driver.getPageSource().contains("FIR Record Management System"));
    }

    // 4
    @Test
    public void testSubtitle() {
        Assert.assertTrue(driver.getPageSource().contains("Punjab Police"));
    }

    // 5
    @Test
    public void testSidebarTitle() {
        Assert.assertTrue(driver.getPageSource().contains("FIR Management"));
    }

    // 6
    @Test
    public void testSidebarDescription() {
        Assert.assertTrue(driver.getPageSource().contains("Digital FIR Portal"));
    }

    // 7
    @Test
    public void testOperationSelect() {
        Assert.assertTrue(driver.getPageSource().contains("Select Operation"));
    }

    // 8
    @Test
    public void testViewFIROption() {
        Assert.assertTrue(driver.getPageSource().contains("View All FIRs"));
    }

    // 9
    @Test
    public void testRegisterOption() {
        Assert.assertTrue(driver.getPageSource().contains("Register New FIR"));
    }

    // 10
    @Test
    public void testUpdateOption() {
        Assert.assertTrue(driver.getPageSource().contains("Update FIR Status"));
    }

    // 11
    @Test
    public void testDeleteOption() {
        Assert.assertTrue(driver.getPageSource().contains("Delete FIR Record"));
    }

    // 12
    @Test
    public void testCrimeTypeTheft() {
        Assert.assertTrue(driver.getPageSource().contains("Theft"));
    }

    // 13
    @Test
    public void testCrimeTypeRobbery() {
        Assert.assertTrue(driver.getPageSource().contains("Robbery"));
    }

    // 14
    @Test
    public void testCrimeTypeFraud() {
        Assert.assertTrue(driver.getPageSource().contains("Fraud"));
    }

    // 15
    @Test
    public void testFooter() {
        Assert.assertTrue(driver.getPageSource().contains("Spring 2026"));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}