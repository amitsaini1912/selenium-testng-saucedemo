package com.amitsaini.qa.tests;

import com.amitsaini.qa.base.BaseTest;
import com.amitsaini.qa.pages.InventoryPage;
import com.amitsaini.qa.pages.LoginPage;
import com.amitsaini.qa.utils.ConfigReader;
import com.amitsaini.qa.utils.CsvDataReader;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {

    @DataProvider(name = "loginData")
    public Object[][] loginData() {
        return CsvDataReader.read("testdata/login_users.csv");
    }

    @Test(groups = {"smoke", "regression"},
          description = "TC-LOGIN-01: A standard user with valid credentials reaches the product listing")
    public void validLoginLandsOnInventory() {
        InventoryPage inventory = loginPage.loginAs(
                ConfigReader.get("standard.user"),
                ConfigReader.get("valid.password"));

        Assert.assertTrue(inventory.isLoaded(), "Inventory container should be visible after login");
        Assert.assertTrue(inventory.getCurrentUrl().contains("inventory.html"),
                "Expected to land on inventory.html but was: " + inventory.getCurrentUrl());
    }

    @Test(groups = "regression",
          description = "TC-LOGIN-02: A locked-out user is refused with the correct message")
    public void lockedOutUserIsRefused() {
        LoginPage result = loginPage.loginExpectingFailure(
                ConfigReader.get("locked.user"),
                ConfigReader.get("valid.password"));

        Assert.assertTrue(result.isErrorDisplayed(), "An error banner should be shown");
        Assert.assertEquals(result.getErrorMessage(),
                "Epic sadface: Sorry, this user has been locked out.",
                "Locked-out message did not match");
    }

    @Test(dataProvider = "loginData",
          groups = "regression",
          description = "TC-LOGIN-03: Login behaves correctly across a data set of valid and invalid credentials")
    public void loginIsDataDriven(String username, String password, String expectedResult, String expectedMessage) {
        if ("SUCCESS".equalsIgnoreCase(expectedResult)) {
            InventoryPage inventory = loginPage.loginAs(username, password);
            Assert.assertTrue(inventory.isLoaded(),
                    "Expected a successful login for user: " + username);
        } else {
            LoginPage result = loginPage.loginExpectingFailure(username, password);
            Assert.assertTrue(result.isErrorDisplayed(),
                    "Expected an error for user: '" + username + "' / password: '" + password + "'");
            Assert.assertEquals(result.getErrorMessage(), expectedMessage,
                    "Error text did not match for user: " + username);
        }
    }

    @Test(groups = "regression",
          description = "TC-LOGIN-04: The error banner can be dismissed with its close button")
    public void errorBannerCanBeDismissed() {
        LoginPage result = loginPage.loginExpectingFailure("invalid_user", "wrong_password");
        Assert.assertTrue(result.isErrorDisplayed(), "Error should appear first");

        result.dismissError();
        Assert.assertFalse(result.isErrorDisplayed(), "Error should be gone after dismissing");
    }

    @Test(groups = "regression",
          description = "TC-LOGIN-05: Logging out returns the user to the login screen")
    public void logoutReturnsToLoginPage() {
        InventoryPage inventory = loginPage.loginAs(
                ConfigReader.get("standard.user"),
                ConfigReader.get("valid.password"));

        LoginPage afterLogout = inventory.logout();
        Assert.assertTrue(afterLogout.isLoginButtonDisplayed(), "Login button should be visible again");
        Assert.assertFalse(afterLogout.getCurrentUrl().contains("inventory.html"),
                "Should have left the inventory page");
    }
}
