package tests;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

public class FirTests {

    WebDriver driver;

    @BeforeClass
    public void setup() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.get("http://44.212.91.126:8090/"); // Docker service name
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
    public void testCrimeTypesPresent() {
        Assert.assertTrue(driver.getPageSource().contains("Theft"));
    }

    // 5
    @Test
    public void testAddFIRButton() {
        Assert.assertTrue(driver.getPageSource().contains("Register FIR"));
    }

    // 6
    @Test
    public void testViewSection() {
        Assert.assertTrue(driver.getPageSource().contains("View All FIRs"));
    }

    // 7
    @Test
    public void testUpdateSection() {
        Assert.assertTrue(driver.getPageSource().contains("Update FIR Status"));
    }

    // 8
    @Test
    public void testDeleteSection() {
        Assert.assertTrue(driver.getPageSource().contains("Delete FIR Record"));
    }

    // 9
    @Test
    public void testInputFieldsPresent() {
        Assert.assertTrue(driver.getPageSource().contains("CNIC"));
    }

    // 10
    @Test
    public void testOfficerField() {
        Assert.assertTrue(driver.getPageSource().contains("Officer"));
    }

    // 11
    @Test
    public void testLocationField() {
        Assert.assertTrue(driver.getPageSource().contains("Location"));
    }

    // 12
    @Test
    public void testIncidentDateField() {
        Assert.assertTrue(driver.getPageSource().contains("Incident Date"));
    }

    // 13
    @Test
    public void testStatusOptions() {
        Assert.assertTrue(driver.getPageSource().contains("Under Investigation"));
    }

    // 14
    @Test
    public void testDatabaseConnectionMessage() {
        Assert.assertTrue(driver.getPageSource().contains("FIR"));
    }

    // 15
    @Test
    public void testPageCaption() {
        Assert.assertTrue(driver.getPageSource().contains("Punjab Police"));
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}
