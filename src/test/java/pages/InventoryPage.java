package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object representing the Sauce Demo Inventory Page.
 * This page is displayed after a successful login.
 */
public class InventoryPage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(InventoryPage.class);

    // --- Locators ---
    private final By pageTitle = By.cssSelector("span.title");
    private final By inventoryItem = By.cssSelector("div.inventory_item");
    private final By inventoryItemName = By.cssSelector("div.inventory_item_name");
    private final By addToCartButton = By.xpath(".//button[contains(text(),'Add to cart')]"); // Relative XPath to find Add button within an item
    // Note: Using XPath starts with "." to search within the context of a parent element later
    private final By shoppingCartLink = By.id("shopping_cart_container");
    private final By shoppingCartBadge = By.cssSelector("span.shopping_cart_badge");

    // --- Page Actions ---

    public boolean isPageDisplayed() {
        try {
            String titleText = getElementText(pageTitle);
            boolean isDisplayed = "Products".equalsIgnoreCase(titleText);
            log.info("Inventory page title is displayed: {}", isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            log.error("Could not verify inventory page title.", e);
            return false;
        }
    }

    private WebElement findProductItemByName(String productName) {
        log.debug("Attempting to find product item by name: {}", productName);

        List<WebElement> items = getDriver().findElements(inventoryItem);
        log.debug("Found {} inventory items on the page.", items.size());
        for (WebElement item : items) {
            // Find the name element *within* the context of the current item
            WebElement nameElement = item.findElement(inventoryItemName);
            if (productName.equals(nameElement.getText())) {
                log.info("Found product item: {}", productName);
                return item;
            }
        }
        log.warn("Product item not found: {}", productName);
        return null;
    }

    public InventoryPage addProductToCart(String productName) {
        log.info("Attempting to add product '{}' to cart.", productName);
        WebElement productItem = findProductItemByName(productName);
        if (productItem != null) {
            // Find the Add to Cart button *within* the product item's context
            try {
                WebElement addButton = productItem.findElement(addToCartButton); // Find relative to productItem
                addButton.click(); // Direct click here, could wrap in a helper if needed
                log.info("Clicked 'Add to cart' for product: {}", productName);
            } catch (Exception e) {
                log.error("Could not find or click 'Add to cart' button for product: {}", productName, e);
                throw new RuntimeException("Failed to find or click Add to Cart button for " + productName, e);
            }
        } else {
            log.error("Product '{}' not found on inventory page.", productName);
            throw new RuntimeException("Product not found: " + productName);
        }
        return this;
    }

    public CartPage goToCart() {
        log.info("Navigating to the shopping cart.");
        clickElement(shoppingCartLink);
        return new CartPage();
    }

    /**
     * Gets the number displayed on the shopping cart badge.
     * Returns 0 if the badge is not displayed (cart is empty).
     *
     * @return The number of items in the cart as an int.
     */
    public int getCartItemCount() {
        if (isElementDisplayed(shoppingCartBadge)) {
            try {
                String countText = getElementText(shoppingCartBadge);
                int count = Integer.parseInt(countText);
                log.info("Cart badge count: {}", count);
                return count;
            } catch (NumberFormatException e) {
                log.error("Could not parse cart badge text '{}' to integer.", getElementText(shoppingCartBadge), e);
                return 0; // Or handle error differently
            }
        }
        log.info("Shopping cart badge not displayed (cart likely empty).");
        return 0; // Badge not visible, assume 0 items
    }

    public List<String> getDisplayedProductNames() {
        List<WebElement> nameElements = getDriver().findElements(inventoryItemName);
        List<String> names = nameElements.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
        log.info("Found product names: {}", names);
        return names;
    }
}