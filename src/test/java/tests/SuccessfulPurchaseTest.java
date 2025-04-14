package tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.*; // Import needed Page Objects

import static org.junit.jupiter.api.Assertions.*; // Keep static import for assertions

public class SuccessfulPurchaseTest extends BaseTest { // Inherit from BaseTest

    // Page objects can be initialized directly within the test method when needed
    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutStepOnePage checkoutStepOnePage;
    private CheckoutStepTwoPage checkoutStepTwoPage;
    private CheckoutCompletePage checkoutCompletePage;

    @Test
    @DisplayName("Should complete a purchase successfully with standard user")
    void successfulPurchaseFlow() {
        String targetProduct = "Sauce Labs Backpack";
        String firstName = "Test";
        String lastName = "User";
        String postalCode = "12345";
        String expectedCompletionMessage = "Thank you for your order!";

        // --- Test Steps & Assertions ---

        // 1. Login (Navigation to login page is handled by BaseTest.setUp)
        loginPage = new LoginPage();
        assertTrue(loginPage.isLoginButtonDisplayed(), "Login page initial load verification failed.");
        inventoryPage = loginPage.loginAs("standard_user", "secret_sauce");

        // 2. Verify Inventory Page & Add Product
        assertTrue(inventoryPage.isPageDisplayed(), "Inventory page verification failed after login.");
        inventoryPage.addProductToCart(targetProduct);
        assertEquals(1, inventoryPage.getCartItemCount(), "Cart item count mismatch after adding product.");

        // 3. Go to Cart & Verify Product
        cartPage = inventoryPage.goToCart();
        assertTrue(cartPage.isPageDisplayed(), "Cart page verification failed.");
        assertTrue(cartPage.isProductDisplayed(targetProduct), "Product missing in cart.");

        // 4. Proceed to Checkout Step 1 & Fill Info
        checkoutStepOnePage = cartPage.clickCheckout();
        assertTrue(checkoutStepOnePage.isPageDisplayed(), "Checkout Step One page verification failed.");
        checkoutStepTwoPage = checkoutStepOnePage.fillShippingInfoAndContinue(firstName, lastName, postalCode);

        // 5. Verify Checkout Step 2 (Overview) & Product
        assertTrue(checkoutStepTwoPage.isPageDisplayed(), "Checkout Step Two page verification failed.");
        assertTrue(checkoutStepTwoPage.isProductDisplayed(targetProduct), "Product missing in checkout overview.");

        // 6. Finish Purchase & Verify Completion
        checkoutCompletePage = checkoutStepTwoPage.clickFinish();
        assertTrue(checkoutCompletePage.isPageDisplayed(), "Checkout Complete page verification failed.");
        assertEquals(expectedCompletionMessage, checkoutCompletePage.getCompletionHeader(), "Final completion message mismatch.");
    }
}