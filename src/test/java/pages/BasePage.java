package pages;

import core.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration; // For WebDriverWait timeout

/**
 * Base class for all Page Objects.
 * Provides common functionalities like WebDriver access, explicit waits, and basic element interactions.
 */
public abstract class BasePage {

    // Logger for this base page
    private static final Logger log = LoggerFactory.getLogger(BasePage.class);

    // WebDriverWait instance - initialized in the constructor
    protected WebDriverWait wait;

    // Default wait timeout in seconds
    private static final long DEFAULT_WAIT_TIMEOUT = 10; // 10 seconds

    /**
     * Constructor for BasePage.
     * Initializes the WebDriverWait instance for the page.
     * Subclasses must call this constructor (implicitly or explicitly via super()).
     */
    public BasePage() {
        // Initialize WebDriverWait using the driver from DriverManager for the current thread
        // The driver instance is retrieved when needed, ensuring thread safety
        this.wait = new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_WAIT_TIMEOUT));
        log.trace("BasePage initialized with WebDriverWait ({} seconds timeout)", DEFAULT_WAIT_TIMEOUT);
    }

    /**
     * Provides access to the WebDriver instance for the current thread.
     * Primarily intended for internal use within BasePage and subclasses (Page Objects).
     *
     * @return WebDriver instance for the current thread.
     */
    protected WebDriver getDriver() {
        // Retrieve the driver instance managed by DriverManager
        return DriverManager.getDriver();
    }

    // --- Common Interaction Methods ---
    // These methods provide a layer over basic Selenium commands,
    // incorporating logging and explicit waits.

    /**
     * Clicks on a web element located by the given locator after ensuring it's clickable.
     *
     * @param locator The By locator strategy to find the element.
     */
    protected void clickElement(By locator) {
        try {
            log.debug("Attempting to click element located by: {}", locator);
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
            log.info("Clicked element located by: {}", locator);
        } catch (Exception e) {
            log.error("Error clicking element located by: {}", locator, e);
            // Re-throw the exception to fail the test, ensuring visibility of the error
            throw new RuntimeException("Failed to click element: " + locator, e);
        }
    }

    /**
     * Sends keys (types text) into a web element located by the given locator
     * after ensuring it's visible and clearing its content first.
     *
     * @param locator The By locator strategy to find the element.
     * @param text    The text to send to the element.
     */
    protected void sendKeysToElement(By locator, String text) {
        if (text == null) {
            log.warn("Attempting to send null text to element: {}. Skipping.", locator);
            return; // Avoid NullPointerException
        }
        try {
            log.debug("Attempting to send keys '{}' to element located by: {}", text, locator);
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            element.clear(); // Clear the field before sending keys
            element.sendKeys(text);
            log.info("Sent keys '{}' to element located by: {}", text, locator);
        } catch (Exception e) {
            log.error("Error sending keys '{}' to element located by: {}", text, locator, e);
            throw new RuntimeException("Failed to send keys to element: " + locator, e);
        }
    }

    /**
     * Gets the visible text content of a web element located by the given locator
     * after ensuring it's visible.
     *
     * @param locator The By locator strategy to find the element.
     * @return The visible text of the element.
     */
    protected String getElementText(By locator) {
        try {
            log.debug("Attempting to get text from element located by: {}", locator);
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            String text = element.getText();
            log.info("Retrieved text '{}' from element located by: {}", text, locator);
            return text;
        } catch (Exception e) {
            log.error("Error getting text from element located by: {}", locator, e);
            throw new RuntimeException("Failed to get text from element: " + locator, e);
        }
    }

    /**
     * Checks if an element located by the given locator is displayed on the page.
     * Uses an explicit wait for visibility.
     *
     * @param locator The By locator strategy to find the element.
     * @return true if the element is visible within the timeout, false otherwise.
     */
    protected boolean isElementDisplayed(By locator) {
        try {
            log.debug("Checking visibility of element located by: {}", locator);
            // Wait specifically for visibility, returns the element if visible, throws timeout if not
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            log.info("Element located by {} is displayed.", locator);
            return true; // If wait succeeds, the element is visible
        } catch (org.openqa.selenium.TimeoutException e) {
            log.warn("Element located by {} was not visible within {} seconds.", locator, DEFAULT_WAIT_TIMEOUT);
            return false; // If wait times out, the element is not visible
        } catch (Exception e) {
            log.error("Error checking visibility for element located by: {}", locator, e);
            // Decide if other exceptions should also return false or re-throw
            // For robustness, often better to return false unless it's an unexpected error
            return false;
        }
    }

}