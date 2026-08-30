package com.amitsaini.qa.tests;

import com.amitsaini.qa.base.BaseTest;
import com.amitsaini.qa.pages.InventoryPage;
import com.amitsaini.qa.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Comparator;
import java.util.List;

public class InventoryTests extends BaseTest {

    private InventoryPage inventory;

    /**
     * Runs after BaseTest.setUp because TestNG executes the parent class
     * @BeforeMethod first. Every test in this class starts logged in.
     */
    @BeforeMethod(alwaysRun = true, dependsOnMethods = "setUp")
    public void loginFirst() {
        inventory = loginPage.loginAs(
                ConfigReader.get("standard.user"),
                ConfigReader.get("valid.password"));
    }

    @Test(groups = {"smoke", "regression"},
          description = "TC-INV-01: The catalogue shows all six products")
    public void catalogueShowsSixProducts() {
        Assert.assertEquals(inventory.getProductCount(), 6, "Expected six products in the catalogue");
    }

    @Test(groups = "regression",
          description = "TC-INV-02: Sorting by Name (A to Z) orders products alphabetically")
    public void sortByNameAscending() {
        inventory.sortBy("Name (A to Z)");
        List<String> actual = inventory.getProductNames();
        List<String> expected = actual.stream().sorted().toList();
        Assert.assertEquals(actual, expected, "Products are not in ascending name order");
    }

    @Test(groups = "regression",
          description = "TC-INV-03: Sorting by Name (Z to A) reverses the alphabetical order")
    public void sortByNameDescending() {
        inventory.sortBy("Name (Z to A)");
        List<String> actual = inventory.getProductNames();
        List<String> expected = actual.stream().sorted(Comparator.reverseOrder()).toList();
        Assert.assertEquals(actual, expected, "Products are not in descending name order");
    }

    @Test(groups = "regression",
          description = "TC-INV-04: Sorting by Price (low to high) orders products by ascending price")
    public void sortByPriceLowToHigh() {
        inventory.sortBy("Price (low to high)");
        List<Double> actual = inventory.getProductPrices();
        List<Double> expected = actual.stream().sorted().toList();
        Assert.assertEquals(actual, expected, "Prices are not in ascending order");
    }

    @Test(groups = "regression",
          description = "TC-INV-05: Sorting by Price (high to low) orders products by descending price")
    public void sortByPriceHighToLow() {
        inventory.sortBy("Price (high to low)");
        List<Double> actual = inventory.getProductPrices();
        List<Double> expected = actual.stream().sorted(Comparator.reverseOrder()).toList();
        Assert.assertEquals(actual, expected, "Prices are not in descending order");
    }

    @Test(groups = {"smoke", "regression"},
          description = "TC-INV-06: The cart badge appears and counts a single added product")
    public void cartBadgeCountsOneProduct() {
        Assert.assertEquals(inventory.getCartCount(), 0, "Cart should start empty");

        inventory.addProductToCart("Sauce Labs Backpack");
        Assert.assertEquals(inventory.getCartCount(), 1, "Cart badge should show 1");
    }

    @Test(groups = "regression",
          description = "TC-INV-07: The cart badge accumulates across several added products")
    public void cartBadgeCountsMultipleProducts() {
        inventory.addProductToCart("Sauce Labs Backpack");
        inventory.addProductToCart("Sauce Labs Bike Light");
        inventory.addProductToCart("Sauce Labs Onesie");

        Assert.assertEquals(inventory.getCartCount(), 3, "Cart badge should show 3");
    }

    @Test(groups = "regression",
          description = "TC-INV-08: Removing a product from the listing decrements the cart badge")
    public void removingProductDecrementsBadge() {
        inventory.addProductToCart("Sauce Labs Backpack");
        inventory.addProductToCart("Sauce Labs Bike Light");
        Assert.assertEquals(inventory.getCartCount(), 2, "Precondition: two items in cart");

        inventory.removeProductFromCart("Sauce Labs Backpack");
        Assert.assertEquals(inventory.getCartCount(), 1, "Cart badge should drop to 1");
    }

    @Test(groups = "regression",
          description = "TC-INV-09: Reset App State empties the cart")
    public void resetAppStateClearsCart() {
        inventory.addProductToCart("Sauce Labs Backpack");
        inventory.addProductToCart("Sauce Labs Onesie");
        Assert.assertEquals(inventory.getCartCount(), 2, "Precondition: two items in cart");

        inventory.resetAppState();
        Assert.assertEquals(inventory.getCartCount(), 0, "Cart should be empty after reset");
    }
}
