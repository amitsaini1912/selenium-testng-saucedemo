package com.amitsaini.qa.pages;

import com.amitsaini.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * The product listing shown after a successful login.
 */
public class InventoryPage extends BasePage {

    private final By inventoryContainer = By.id("inventory_container");
    private final By productItems = By.className("inventory_item");
    private final By productNames = By.className("inventory_item_name");
    private final By productPrices = By.className("inventory_item_price");
    private final By sortDropdown = By.className("product_sort_container");
    private final By cartBadge = By.className("shopping_cart_badge");
    private final By cartLink = By.className("shopping_cart_link");
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By logoutLink = By.id("logout_sidebar_link");
    private final By resetAppStateLink = By.id("reset_sidebar_link");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isEventuallyVisible(inventoryContainer);
    }

    public int getProductCount() {
        return waitForAllVisible(productItems).size();
    }

    public List<String> getProductNames() {
        return waitForAllVisible(productNames).stream()
                .map(WebElement::getText)
                .map(String::trim)
                .toList();
    }

    /**
     * Prices as doubles, with the leading "$" stripped, so they can be
     * compared numerically rather than as strings ("$10.99" < "$9.99" as text).
     */
    public List<Double> getProductPrices() {
        return waitForAllVisible(productPrices).stream()
                .map(WebElement::getText)
                .map(text -> text.replace("$", "").trim())
                .map(Double::parseDouble)
                .toList();
    }

    public InventoryPage sortBy(String visibleText) {
        selectByVisibleText(sortDropdown, visibleText);
        return this;
    }

    /**
     * SauceDemo builds the button id from the product name:
     * "Sauce Labs Backpack" -> add-to-cart-sauce-labs-backpack
     */
    public InventoryPage addProductToCart(String productName) {
        String slug = toButtonId(productName);
        click(By.id("add-to-cart-" + slug));
        // The button flips to "Remove" once the add registers. Waiting for it
        // keeps the next action (or a cart-badge read) from racing the click.
        waitForVisible(By.id("remove-" + slug));
        return this;
    }

    public InventoryPage removeProductFromCart(String productName) {
        String slug = toButtonId(productName);
        click(By.id("remove-" + slug));
        waitForVisible(By.id("add-to-cart-" + slug));
        return this;
    }

    private String toButtonId(String productName) {
        return productName.toLowerCase().replace(" ", "-").replace("(", "").replace(")", "");
    }

    /**
     * @return the number on the cart badge, or 0 when no badge is rendered.
     */
    public int getCartCount() {
        if (!isDisplayed(cartBadge)) {
            return 0;
        }
        return Integer.parseInt(getText(cartBadge));
    }

    public CartPage openCart() {
        click(cartLink);
        return new CartPage(driver);
    }

    public LoginPage logout() {
        click(menuButton);
        click(logoutLink);
        return new LoginPage(driver);
    }

    public InventoryPage resetAppState() {
        click(menuButton);
        click(resetAppStateLink);
        return this;
    }
}
