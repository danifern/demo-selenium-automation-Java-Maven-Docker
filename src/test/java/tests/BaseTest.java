package tests;

import core.DriverManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Base class for all UI tests.
 * Handles WebDriver setup before each test and teardown after each test.
 * Uses DriverManager to ensure thread safety.
 */
public abstract class BaseTest { // Abstract: cannot be instantiated directly

    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    // Default browser, can be overridden by system property
    private static final String DEFAULT_BROWSER = "chrome";
    private static final String BASE_URL = "https://www.saucedemo.com/"; // Define base URL


    @BeforeEach // This method runs before each @Test method in subclasses
    void setUp() {
        log.info("==================== Setting up WebDriver ====================");
        String browser = System.getProperty("browser", DEFAULT_BROWSER).toLowerCase();
        WebDriver driver;

        log.info("Requested browser: {}", browser);

        try {
            switch (browser) {
                case "firefox":
                    WebDriverManager.firefoxdriver().setup(); // Setup Firefox driver
                    // FirefoxOptions options = new FirefoxOptions(); // Add options if needed
                    // driver = new FirefoxDriver(options);
                    driver = new FirefoxDriver();
                    break;
                case "chrome":
                default: // Default to Chrome if browser property is invalid or not set
                    WebDriverManager.chromedriver().setup(); // Setup Chrome driver
                    ChromeOptions chromeOptions = new ChromeOptions();
                    // Example options (uncomment/add as needed):
                    // chromeOptions.addArguments("--headless"); // Run headless (no UI)
                    // chromeOptions.addArguments("--disable-gpu"); // Often needed for headless
                    // chromeOptions.addArguments("--window-size=1920,1080"); // Set window size
                    // chromeOptions.addArguments("--no-sandbox"); // May be needed in Docker/Linux
                    // chromeOptions.addArguments("--disable-dev-shm-usage"); // May be needed in Docker/Linux
                    driver = new ChromeDriver(chromeOptions);
                    break;
                // Open to extension by adding cases for other browsers (Edge, Safari) if needed
            }

            // Store the created driver instance in DriverManager for the current thread
            DriverManager.setDriver(driver);
            log.info("WebDriver instance created and set for thread: {}", Thread.currentThread().getName());

            // Basic configurations applied to the driver instance

            DriverManager.getDriver().manage().window().maximize(); // Maximize browser window

            // Implicit waits are generally discouraged when using explicit waits properly.
            // Set to 0 to rely solely on explicit waits.
            DriverManager.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(1));

            log.info("WebDriver basic configuration applied.");


            log.info("Navigating to base URL: {}", BASE_URL);
            DriverManager.getDriver().get(BASE_URL);
            log.info("Navigation to base URL complete.");
            DriverManager.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(1));


        } catch (Exception e) {
            log.error("!!! WebDriver setup failed !!!", e);
            throw new RuntimeException("WebDriver setup failed", e); // Fail fast
        }
        log.info("==================== WebDriver Setup Complete ====================");
    }

    @AfterEach // This method runs after each @Test method in subclasses
    void tearDown() {
        log.info("==================== Tearing down WebDriver ====================");
        // Quit the driver and remove from ThreadLocal using DriverManager
        DriverManager.quitDriver();
        log.info("================== WebDriver Teardown Complete ==================");
    }
}