package com.amitsaini.qa.listeners;

import com.amitsaini.qa.base.DriverManager;
import com.amitsaini.qa.utils.ScreenshotUtil;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Hooks into the TestNG lifecycle to build the HTML report and attach a
 * screenshot to every failure.
 *
 * Registered in testng.xml, so no test class needs to know it exists.
 */
public class TestListener implements ITestListener {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        ExtentSparkReporter reporter = new ExtentSparkReporter("target/extent-report/index.html");
        reporter.config().setTheme(Theme.STANDARD);
        reporter.config().setDocumentTitle("SauceDemo Automation Report");
        reporter.config().setReportName("Selenium + TestNG Regression Suite");

        extent = new ExtentReports();
        extent.attachReporter(reporter);
        extent.setSystemInfo("Application", "SauceDemo");
        extent.setSystemInfo("Browser", System.getProperty("browser", "chrome"));
        extent.setSystemInfo("Headless", System.getProperty("headless", "false"));
        extent.setSystemInfo("Java", System.getProperty("java.version"));
    }

    @Override
    public void onTestStart(ITestResult result) {
        String description = result.getMethod().getDescription();
        ExtentTest test = extent.createTest(
                result.getTestClass().getRealClass().getSimpleName() + " :: " + result.getName(),
                description == null ? "" : description);
        CURRENT_TEST.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        CURRENT_TEST.get().log(Status.PASS, "Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = CURRENT_TEST.get();
        test.log(Status.FAIL, result.getThrowable());

        String path = ScreenshotUtil.capture(DriverManager.getDriver(), result.getName());
        if (path != null) {
            try {
                test.addScreenCaptureFromPath(path, "Screenshot at failure");
            } catch (Exception e) {
                test.log(Status.WARNING, "Screenshot saved to " + path + " but could not be embedded");
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        CURRENT_TEST.get().log(Status.SKIP, "Skipped: " + result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
        CURRENT_TEST.remove();
    }
}
