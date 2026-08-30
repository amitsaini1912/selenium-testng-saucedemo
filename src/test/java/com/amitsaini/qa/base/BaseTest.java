package com.amitsaini.qa.base;

import com.amitsaini.qa.pages.LoginPage;
import com.amitsaini.qa.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * Test lifecycle.
 *
 * A fresh browser per test method costs a little time but removes the whole
 * class of failures where test B only passes because test A happened to leave
 * the app in the right state. Independent tests can also run in any order and
 * in parallel.
 */
public abstract class BaseTest {

    protected WebDriver driver;
    protected LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        driver = DriverFactory.create();
        DriverManager.setDriver(driver);
        driver.get(ConfigReader.get("base.url"));
        loginPage = new LoginPage(driver);
        System.out.println("[START] " + method.getName());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(Method method) {
        // The listener takes the screenshot before this runs, so the browser
        // is still alive at the moment of failure.
        DriverManager.quitDriver();
        driver = null;
        System.out.println("[END]   " + method.getName());
    }
}
