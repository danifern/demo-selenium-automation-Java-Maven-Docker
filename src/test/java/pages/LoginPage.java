package pages;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Page Object representing the Sauce Demo Login Page (https://www.saucedemo.com/).
 * Inherits common functionalities from BasePage.
 */
public class LoginPage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(LoginPage.class);

    // --- Locators ---
    private final By usernameInput = By.id("user-name");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessageContainer = By.cssSelector("div.error-message-container h3[data-test='error']"); // Example for error message

    // --- Page Actions ---

    public LoginPage enterUsername(String username) {
        log.info("Entering username: '{}'", username);
        sendKeysToElement(usernameInput, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        log.info("Entering password: '{}'", "****"); // Log password masked
        sendKeysToElement(passwordInput, password);
        return this;
    }

    public InventoryPage clickLoginButton() {
        log.info("Clicking login button");
        clickElement(loginButton);
        return new InventoryPage();
    }


    public InventoryPage loginAs(String username, String password) {
        log.info("Attempting to login as user: {}", username);
        enterUsername(username);
        enterPassword(password);
        return clickLoginButton();
    }

    /**
     * Gets the text of the error message displayed on the login page.
     * Returns null if the error message element is not displayed.
     *
     * @return The error message text, or null if not found/displayed.
     */
    public Optional<String> getErrorMessage() {
        if (isElementDisplayed(errorMessageContainer)) {
            String error = getElementText(errorMessageContainer);
            log.warn("Login error message displayed: {}", error);
            return Optional.of(error);
        }
        log.info("No login error message was displayed.");
        return Optional.empty();
    }

    public boolean isLoginButtonDisplayed() {
        return isElementDisplayed(loginButton);
    }

}