package core;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages WebDriver instances using ThreadLocal to ensure thread safety for parallel execution.
 * Each thread running a test will get its own separate WebDriver instance.
 */
public class DriverManager {

    // Logger instance for this class using SLF4j
    private static final Logger log = LoggerFactory.getLogger(DriverManager.class);

    // ThreadLocal variable to store WebDriver instances. <WebDriver> specifies the type of object it holds.
    private static final ThreadLocal<WebDriver> webDriverThreadLocal = new ThreadLocal<>();

    /**
     * Returns the WebDriver instance for the current thread.
     *
     * @return WebDriver instance.
     * @throws IllegalStateException if getDriver() is called before setDriver() for the current thread.
     */
    public static WebDriver getDriver() {
        WebDriver driver = webDriverThreadLocal.get();
        if (driver == null) {
            // This situation should ideally not happen if setup logic in BaseTest is correct
            log.error("WebDriver instance is null for the current thread: {}. Ensure setDriver was called.", Thread.currentThread().getName());
            throw new IllegalStateException("WebDriver has not been set for the current thread: " + Thread.currentThread().getName());
        }
        log.trace("Retrieved WebDriver instance for thread: {}", Thread.currentThread().getName());
        return driver;
    }

    /**
     * Sets the WebDriver instance for the current thread.
     * Should be called once per test execution thread, typically in a @BeforeEach/@BeforeMethod block.
     *
     * @param driver The WebDriver instance to associate with the current thread.
     */
    public static void setDriver(WebDriver driver) {
        if (driver != null) {
            log.debug("Setting WebDriver instance for thread: {}", Thread.currentThread().getName());
            webDriverThreadLocal.set(driver);
        } else {
            log.warn("Attempted to set a null WebDriver instance for thread: {}", Thread.currentThread().getName());
        }
    }

    /**
     * Quits the WebDriver instance associated with the current thread and removes it from ThreadLocal.
     * Should be called once per test execution thread, typically in an @AfterEach/@AfterMethod block.
     */
    public static void quitDriver() {
        WebDriver driver = webDriverThreadLocal.get();
        if (driver != null) {
            log.debug("Quitting WebDriver instance for thread: {}", Thread.currentThread().getName());
            try {
                driver.quit(); // Closes all browser windows and ends the WebDriver session.
            } catch (Exception e) {
                log.error("Error occurred while quitting WebDriver for thread: {}", Thread.currentThread().getName(), e);
            } finally {
                // Crucial: Remove the WebDriver instance from ThreadLocal to prevent memory leaks
                webDriverThreadLocal.remove();
                log.debug("Removed WebDriver instance from ThreadLocal for thread: {}", Thread.currentThread().getName());
            }
        } else {
            log.warn("Attempted to quit a WebDriver instance, but none was found for thread: {}", Thread.currentThread().getName());
        }
    }

    // Private constructor to prevent instantiation of this utility class
    private DriverManager() {
        throw new IllegalStateException("Utility class - Do not instantiate");
    }
}