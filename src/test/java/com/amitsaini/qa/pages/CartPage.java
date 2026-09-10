package com.amitsaini.qa.pages;

import com.amitsaini.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CartPage extends BasePage {

    private final By cartList = By.className("cart_list");
    private final By cartItems = By.className("cart_item");
    private final By itemNames = By.className("inventory_item_name");
    private final By checkoutButton = By.id("checkout");
    private final By continueShoppingButton = By.id("continue-shopping");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isEventuallyVisible(cartList);
    }

    public int getItemCount() {
        waitForVisible(cartList);
        return countOf(cartItems);
    }

    public List<String> getItemNames() {
        waitForVisible(cartList);
        return driver.findElements(itemNames).stream()
                .map(WebElement::getText)
                .map(String::trim)
                .toList();
    }

    public CartPage removeItem(String productName) {
        String id = "remove-" + productName.toLowerCase().replace(" ", "-");
        By removeButton = By.id(id);
        click(removeButton);
        // Wait for the row to actually leave the DOM before the caller reads
        // the cart again, otherwise the count is read while it is still stale.
        waitForGone(removeButton);
        return this;
    }

    public CheckoutPage proceedToCheckout() {
        click(checkoutButton);
        return new CheckoutPage(driver);
    }

    public InventoryPage continueShopping() {
        click(continueShoppingButton);
        return new InventoryPage(driver);
    }
}
