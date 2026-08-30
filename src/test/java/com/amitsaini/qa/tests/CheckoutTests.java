package com.amitsaini.qa.tests;

import com.amitsaini.qa.base.BaseTest;
import com.amitsaini.qa.pages.CartPage;
import com.amitsaini.qa.pages.CheckoutPage;
import com.amitsaini.qa.pages.InventoryPage;
import com.amitsaini.qa.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class CheckoutTests extends BaseTest {

    private InventoryPage inventory;

    @BeforeMethod(alwaysRun = true, dependsOnMethods = "setUp")
    public void loginFirst() {
        inventory = loginPage.loginAs(
                ConfigReader.get("standard.user"),
                ConfigReader.get("valid.password"));
    }

    @Test(groups = "regression",
          description = "TC-CART-01: Products added on the listing appear in the cart")
    public void addedProductsAppearInCart() {
        inventory.addProductToCart("Sauce Labs Backpack");
        inventory.addProductToCart("Sauce Labs Bike Light");

        CartPage cart = inventory.openCart();
        Assert.assertTrue(cart.isLoaded(), "Cart page should load");
        Assert.assertEquals(cart.getItemCount(), 2, "Cart should hold two line items");

        List<String> names = cart.getItemNames();
        Assert.assertTrue(names.contains("Sauce Labs Backpack"), "Backpack missing from cart");
        Assert.assertTrue(names.contains("Sauce Labs Bike Light"), "Bike Light missing from cart");
    }

    @Test(groups = "regression",
          description = "TC-CART-02: Removing a line item from the cart updates the cart contents")
    public void removingItemInCartUpdatesContents() {
        inventory.addProductToCart("Sauce Labs Backpack");
        inventory.addProductToCart("Sauce Labs Bike Light");

        CartPage cart = inventory.openCart().removeItem("Sauce Labs Backpack");
        Assert.assertEquals(cart.getItemCount(), 1, "One item should remain");
        Assert.assertFalse(cart.getItemNames().contains("Sauce Labs Backpack"),
                "Removed product should not be listed");
    }

    @Test(groups = "regression",
          description = "TC-CART-03: Continue Shopping returns to the product listing with the cart intact")
    public void continueShoppingKeepsCart() {
        inventory.addProductToCart("Sauce Labs Backpack");

        InventoryPage back = inventory.openCart().continueShopping();
        Assert.assertTrue(back.isLoaded(), "Should be back on the inventory page");
        Assert.assertEquals(back.getCartCount(), 1, "Cart should still hold the product");
    }

    @Test(groups = {"smoke", "regression"},
          description = "TC-CHK-01: A complete checkout ends on the order confirmation screen")
    public void happyPathCheckoutCompletes() {
        CheckoutPage checkout = inventory
                .addProductToCart("Sauce Labs Backpack")
                .openCart()
                .proceedToCheckout()
                .enterCustomerInformation("Amit", "Saini", "141001")
                .clickContinue()
                .clickFinish();

        Assert.assertTrue(checkout.isOrderComplete(), "Confirmation header should be visible");
        Assert.assertEquals(checkout.getConfirmationMessage(), "Thank you for your order!",
                "Confirmation message did not match");
    }

    @Test(groups = "regression",
          description = "TC-CHK-02: Checkout is blocked when the first name is missing")
    public void checkoutRequiresFirstName() {
        CheckoutPage checkout = inventory
                .addProductToCart("Sauce Labs Backpack")
                .openCart()
                .proceedToCheckout()
                .enterCustomerInformation("", "Saini", "141001")
                .clickContinue();

        Assert.assertTrue(checkout.isErrorDisplayed(), "Validation error should appear");
        Assert.assertEquals(checkout.getErrorMessage(), "Error: First Name is required",
                "First-name validation message did not match");
    }

    @Test(groups = "regression",
          description = "TC-CHK-03: Checkout is blocked when the postal code is missing")
    public void checkoutRequiresPostalCode() {
        CheckoutPage checkout = inventory
                .addProductToCart("Sauce Labs Backpack")
                .openCart()
                .proceedToCheckout()
                .enterCustomerInformation("Amit", "Saini", "")
                .clickContinue();

        Assert.assertTrue(checkout.isErrorDisplayed(), "Validation error should appear");
        Assert.assertEquals(checkout.getErrorMessage(), "Error: Postal Code is required",
                "Postal-code validation message did not match");
    }

    @Test(groups = "regression",
          description = "TC-CHK-04: The order total equals the item subtotal plus the displayed tax")
    public void orderTotalEqualsSubtotalPlusTax() {
        CheckoutPage checkout = inventory
                .addProductToCart("Sauce Labs Backpack")
                .addProductToCart("Sauce Labs Bike Light")
                .openCart()
                .proceedToCheckout()
                .enterCustomerInformation("Amit", "Saini", "141001")
                .clickContinue();

        double subtotal = checkout.getSubtotal();
        double tax = checkout.getTax();
        double total = checkout.getTotal();

        // Delta of 0.01 because these are currency values parsed from text.
        Assert.assertEquals(total, subtotal + tax, 0.01,
                "Total (" + total + ") should equal subtotal (" + subtotal + ") + tax (" + tax + ")");
    }

    @Test(groups = "regression",
          description = "TC-CHK-05: Back Home after an order returns to the listing with an empty cart")
    public void backHomeAfterOrderClearsCart() {
        InventoryPage back = inventory
                .addProductToCart("Sauce Labs Backpack")
                .openCart()
                .proceedToCheckout()
                .enterCustomerInformation("Amit", "Saini", "141001")
                .clickContinue()
                .clickFinish()
                .backToProducts();

        Assert.assertTrue(back.isLoaded(), "Should be back on the inventory page");
        Assert.assertEquals(back.getCartCount(), 0, "Cart should be empty after completing an order");
    }
}
