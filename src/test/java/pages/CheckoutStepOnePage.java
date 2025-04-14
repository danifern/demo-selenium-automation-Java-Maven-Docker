package pages;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CheckoutStepOnePage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(CheckoutStepOnePage.class);

    // --- Locators ---
    private final By firstNameInput = By.id("first-name");
    private final By lastNameInput = By.id("last-name");
    private final By postalCodeInput = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By cancelButton = By.id("cancel");
    private final By pageTitle = By.cssSelector("span.title");

    // --- Page Actions ---

    public CheckoutStepOnePage enterFirstName(String firstName) {
        log.debug("Entering first name: {}", firstName);
        sendKeysToElement(firstNameInput, firstName);
        return this;
    }

    public CheckoutStepOnePage enterLastName(String lastName) {
        log.debug("Entering last name: {}", lastName);
        sendKeysToElement(lastNameInput, lastName);
        return this;
    }

    public CheckoutStepOnePage enterPostalCode(String postalCode) {
        log.debug("Entering postal code: {}", postalCode);
        sendKeysToElement(postalCodeInput, postalCode);
        return this;
    }

    /**
     * Fills all buyer information fields and proceeds to the next step.
     */
    public CheckoutStepTwoPage fillShippingInfoAndContinue(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
        log.info("Submitting shipping information.");
        clickElement(continueButton);
        return new CheckoutStepTwoPage(); // Navigate to next checkout step
    }

    /**
     * Clicks the cancel button, returning to the cart.
     */
    public CartPage clickCancel() {
        log.info("Cancelling checkout step one.");
        clickElement(cancelButton);
        return new CartPage(); // Navigate back to cart
    }

    public boolean isPageDisplayed() {
        try {
            String title = getElementText(pageTitle);
            return "Checkout: Your Information".equalsIgnoreCase(title);
        } catch (Exception e) {
            log.error("Could not verify Checkout Step One page title.", e);
            return false;
        }
    }
}