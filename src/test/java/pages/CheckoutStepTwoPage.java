package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List; // Required for findElements

/**
 * Page Object for the second step of the checkout process (Order Overview).
 */
public class CheckoutStepTwoPage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(CheckoutStepTwoPage.class);

    // --- Locators ---
    private final By pageTitle = By.cssSelector("span.title");
    private final By cartItem = By.cssSelector("div.cart_item");
    private final By itemNameLink = By.cssSelector("div.inventory_item_name");
    private final By finishButton = By.id("finish");
    private final By cancelButton = By.id("cancel");
    // Locators for summary info if needed (e.g., item total, tax, total price)
    // private final By itemTotalPriceLabel = By.cssSelector("div.summary_subtotal_label");
    // private final By taxLabel = By.cssSelector("div.summary_tax_label");
    // private final By totalLabel = By.cssSelector("div.summary_total_label");

    // --- Page Actions ---

    /**
     * Clicks the 'Finish' button to complete the purchase.
     *
     * @return A new CheckoutCompletePage object.
     */
    public CheckoutCompletePage clickFinish() {
        log.info("Clicking the finish button.");
        clickElement(finishButton);
        return new CheckoutCompletePage();
    }

    public InventoryPage clickCancel() {
        log.info("Cancelling checkout step two.");
        clickElement(cancelButton);
        return new InventoryPage(); // Navigate back to inventory
    }

    private WebElement findSummaryItemByName(String productName) {
        log.debug("Attempting to find summary item by name: {}", productName);
        List<WebElement> items = getDriver().findElements(cartItem);
        log.debug("Found {} items in the summary.", items.size());
        for (WebElement item : items) {
            try {
                WebElement nameElement = item.findElement(itemNameLink);
                if (productName.equals(nameElement.getText())) {
                    log.info("Found summary item: {}", productName);
                    return item;
                }
            } catch (Exception e) {
                log.warn("Could not find item name within a summary row, continuing search.", e);
            }
        }
        log.warn("Summary item not found: {}", productName);
        return null;
    }

    public boolean isProductDisplayed(String productName) {
        boolean isFound = findSummaryItemByName(productName) != null;
        log.info("Is product '{}' displayed in summary? {}", productName, isFound);
        return isFound;
    }


    public boolean isPageDisplayed() {
        try {
            String title = getElementText(pageTitle);
            return "Checkout: Overview".equalsIgnoreCase(title);
        } catch (Exception e) {
            log.error("Could not verify Checkout Step Two page title.", e);
            return false;
        }
    }

    // Add methods to get total price, tax, etc., if needed for assertions
    // public String getItemTotal() { return getElementText(itemTotalPriceLabel); }
    // public String getTax() { return getElementText(taxLabel); }
    // public String getTotalPrice() { return getElementText(totalLabel); }

}