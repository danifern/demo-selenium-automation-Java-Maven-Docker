package pages;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CheckoutCompletePage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(CheckoutCompletePage.class);

    // --- Locators ---
    private final By pageTitle = By.cssSelector("span.title");
    private final By completeHeader = By.cssSelector("h2.complete-header");
    private final By completeText = By.cssSelector("div.complete-text");
    private final By backHomeButton = By.id("back-to-products");

    // --- Page Actions ---

    public String getCompletionHeader() {
        log.info("Getting completion header text.");
        return getElementText(completeHeader);
    }

    public String getCompletionText() {
        log.info("Getting completion descriptive text.");
        return getElementText(completeText);
    }

    public InventoryPage clickBackHome() {
        log.info("Clicking 'Back Home' button.");
        clickElement(backHomeButton);
        return new InventoryPage();
    }

    public boolean isPageDisplayed() {
        try {
            // Check both title and header for confirmation
            String title = getElementText(pageTitle);
            boolean headerPresent = isElementDisplayed(completeHeader); // More reliable check
            return "Checkout: Complete!".equalsIgnoreCase(title) && headerPresent;
        } catch (Exception e) {
            log.error("Could not verify Checkout Complete page.", e);
            return false;
        }
    }
}