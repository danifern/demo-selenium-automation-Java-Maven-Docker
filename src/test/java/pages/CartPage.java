package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Page Object representing the Sauce Demo Shopping Cart Page.
 */
public class CartPage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(CartPage.class);

    // --- Locators ---
    private final By pageTitle = By.cssSelector("span.title");
    private final By cartItem = By.cssSelector("div.cart_item");
    private final By itemNameLink = By.cssSelector("div.inventory_item_name");
    private final By itemPrice = By.cssSelector("div.inventory_item_price");
    private final By removeButton = By.xpath(".//button[contains(text(),'Remove')]");
    private final By checkoutButton = By.id("checkout");
    private final By continueShoppingButton = By.id("continue-shopping");


    // --- Page Actions ---
    public boolean isPageDisplayed() {
        try {
            // Explicit wait for title visibility might be good here if loading is slow
            // wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
            String titleText = getElementText(pageTitle); // Use helper from BasePage
            boolean isDisplayed = "Your Cart".equalsIgnoreCase(titleText);
            log.info("Cart page title is displayed: {}", isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            log.error("Could not verify cart page title.", e);
            return false;
        }
    }

    private WebElement findCartItemByName(String productName) {
        log.debug("Attempting to find cart item by name: {}", productName);
        List<WebElement> items = getDriver().findElements(cartItem); // Get all item rows
        log.debug("Found {} items in the cart.", items.size());
        for (WebElement item : items) {
            try {
                WebElement nameElement = item.findElement(itemNameLink); // Find name within the row
                if (productName.equals(nameElement.getText())) {
                    log.info("Found cart item: {}", productName);
                    return item; // Return the row element
                }
            } catch (Exception e) {
                // Log error if name element not found within an item row, but continue searching others
                log.warn("Could not find item name within a cart row, continuing search.", e);
            }
        }
        log.warn("Cart item not found: {}", productName);
        return null; // Return null if no match after checking all items
    }

    public boolean isProductDisplayed(String productName) {
        boolean isFound = findCartItemByName(productName) != null;
        log.info("Is product '{}' displayed in cart? {}", productName, isFound);
        return isFound;
    }

    public String getProductPrice(String productName) {
        WebElement itemRow = findCartItemByName(productName);
        if (itemRow != null) {
            try {
                WebElement priceElement = itemRow.findElement(itemPrice);
                String price = priceElement.getText();
                log.info("Price for product '{}' is: {}", productName, price);
                return price;
            } catch (Exception e) {
                log.error("Could not find price element for product: {}", productName, e);
                return null;
            }
        }
        return null;
    }

    public CartPage removeProduct(String productName) {
        log.info("Attempting to remove product '{}' from cart.", productName);
        WebElement itemRow = findCartItemByName(productName);
        if (itemRow != null) {
            try {
                WebElement removeBtn = itemRow.findElement(removeButton);
                removeBtn.click(); // Direct click, could wrap in helper
                log.info("Clicked 'Remove' for product: {}", productName);
                // Consider adding a short wait here if the UI takes time to update
                // E.g., wait.until(ExpectedConditions.stalenessOf(itemRow));
            } catch (Exception e) {
                log.error("Could not find or click 'Remove' button for product: {}", productName, e);
                throw new RuntimeException("Failed to find or click Remove button for " + productName, e);
            }
        } else {
            log.error("Product '{}' not found in cart to remove.", productName);
            throw new RuntimeException("Product not found to remove: " + productName);
        }
        return this;
    }

    public CheckoutStepOnePage clickCheckout() {
        log.info("Clicking the checkout button.");
        clickElement(checkoutButton);
        return new CheckoutStepOnePage();
    }

    public InventoryPage clickContinueShopping() {
        log.info("Clicking the continue shopping button.");
        clickElement(continueShoppingButton);
        return new InventoryPage();
    }
}