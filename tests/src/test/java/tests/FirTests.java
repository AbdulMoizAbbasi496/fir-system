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

        // Step 1: wait for Streamlit root element
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("[data-testid='stAppViewContainer']")
        ));

        // Step 2: wait until the page source actually has real content
        wait.until(driver -> driver.getPageSource().contains("FIR"));

        // Step 3: extra buffer for remaining dynamic content
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
    }

    // 1
    @Test
    public void testPageLoads() {
        Assert.assertTrue(driver.getTitle().length() > 0);
    }

    // 2
    @Test
    public void testSidebarExists() {
        Assert.assertTrue(driver.getPageSource().contains("FIR Management"));
    }

    // 3
    @Test
    public void testRegisterFormVisible() {
        Assert.assertTrue(driver.getPageSource().contains("Register New FIR"));
    }
    // 4
    @Test
    public void testOptionValue1() {
        Assert.assertTrue(driver.getPageSource().contains("View All FIRs"));
    }
    // 5
    @Test
    public void testOptionValue2() {
        Assert.assertTrue(driver.getPageSource().contains("Update FIR Status"));
    }
    // 6
    @Test
    public void testOptionValue3() {
        Assert.assertTrue(driver.getPageSource().contains("Delete FIR Record"));
    }

    // 7
    @Test
    public void testCrimeTypesPresent() {
        Assert.assertTrue(driver.getPageSource().contains("Theft"));
    }
    //8
    @Test
    public void testCrimeTypesPresent2() {
        Assert.assertTrue(driver.getPageSource().contains("Roberry"));
    }
    //9
    @Test
    public void testCrimeTypesPresent3() {
        Assert.assertTrue(driver.getPageSource().contains("Assault"));
    }
    //10
    @Test
    public void testCrimeTypesPresent4() {
        Assert.assertTrue(driver.getPageSource().contains("Fraud"));
    }
    //11
    @Test
    public void testFilterStatus1() {
        Assert.assertTrue(driver.getPageSource().contains("Under Investigation"));
    }
    //12
    @Test
    public void testFilterStatus2() {
        Assert.assertTrue(driver.getPageSource().contains("Challan Submitted"));
    }
    //13
    @Test
    public void testFilterStatus3() {
        Assert.assertTrue(driver.getPageSource().contains("Case Closed"));
    }
    //14
    @Test
    public void footerTest(){
        Assert.assertTrue(driver.getPageSource().contains("Spring 2026"));
    }

    // 15
    @Test
    public void testPageCaption() {
        Assert.assertTrue(driver.getPageSource().contains("Punjab Police"));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}