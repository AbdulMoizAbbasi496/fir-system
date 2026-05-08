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
        Assert.assertTrue(driver.findElements(
            By.cssSelector("[data-testid='stAppViewContainer']")
        ).size() > 0);
    }

    // 3
    @Test
    public void testMainTitle() {
        Assert.assertTrue(driver.findElements(
            By.xpath("//*[contains(text(),'FIR Record Management System')]")
        ).size() > 0);
    }

    // 4
    @Test
    public void testSubtitle() {
        Assert.assertTrue(driver.findElements(
            By.xpath("//*[contains(text(),'Punjab Police')]")
        ).size() > 0);
    }

    // 5
    @Test
    public void testSidebarTitle() {
        Assert.assertTrue(driver.findElements(
            By.xpath("//*[contains(text(),'FIR Management')]")
        ).size() > 0);
    }

    // 6
    @Test
    public void testSidebarDescription() {
        Assert.assertTrue(driver.findElements(
            By.xpath("//*[contains(text(),'Digital FIR Portal')]")
        ).size() > 0);
    }

    // 7
    @Test
    public void testOperationSelect() {
        Assert.assertTrue(driver.findElements(
            By.xpath("//*[contains(text(),'Select Operation')]")
        ).size() > 0);
    }

    // 8
    @Test
    public void testViewFIROption() {
        Assert.assertTrue(driver.findElements(
            By.xpath("//*[contains(text(),'View All FIRs')]")
        ).size() > 0);
    }

    // 9
    @Test
    public void testRegisterOption() {
        Assert.assertTrue(driver.findElements(
            By.xpath("//*[contains(text(),'Register New FIR')]")
        ).size() > 0);
    }

    // 10
    @Test
    public void testUpdateOption() {
        Assert.assertTrue(driver.findElements(
            By.xpath("//*[contains(text(),'Update FIR Status')]")
        ).size() > 0);
    }

    // 11
    @Test
    public void testDeleteOption() {
        Assert.assertTrue(driver.findElements(
            By.xpath("//*[contains(text(),'Delete FIR Record')]")
        ).size() > 0);
    }

    // 12
    @Test
    public void testCrimeTypeTheft() {
        Assert.assertTrue(driver.findElements(
            By.xpath("//*[contains(text(),'Theft')]")
        ).size() > 0);
    }

    // 13
    @Test
    public void testCrimeTypeRobbery() {
        Assert.assertTrue(driver.findElements(
            By.xpath("//*[contains(text(),'Robbery')]")
        ).size() > 0);
    }

    // 14
    @Test
    public void testCrimeTypeFraud() {
        Assert.assertTrue(driver.findElements(
            By.xpath("//*[contains(text(),'Fraud')]")
        ).size() > 0);
    }

    // 15
    @Test
    public void testFooter() {
        Assert.assertTrue(driver.findElements(
            By.xpath("//*[contains(text(),'Spring 2026')]")
        ).size() > 0);
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}