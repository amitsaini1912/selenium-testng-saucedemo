# SauceDemo UI Automation — Selenium + TestNG

A UI regression suite for [saucedemo.com](https://www.saucedemo.com/), built with Java 17, Selenium 4 and TestNG using the Page Object Model. Runs locally and on every push through GitHub Actions.

## What it covers

20 tests across login, catalogue, cart and checkout — including negative and validation cases, data-driven credentials from CSV, and a calculation check on the order total.

| Area | Tests | Notable cases |
|---|---|---|
| Login | 5 (+6 data rows) | Locked-out user, wrong password, empty username, empty password, error dismissal, logout |
| Inventory | 9 | Product count, four sort orders, cart badge increment/decrement, reset app state |
| Cart & Checkout | 8 | Cart contents, item removal, happy-path order, missing first name, missing postal code, total = subtotal + tax |

## Stack

| Purpose | Choice |
|---|---|
| Language | Java 17 |
| Browser automation | Selenium WebDriver 4.25 |
| Test runner | TestNG 7.10 |
| Build | Maven |
| Reporting | ExtentReports 5 (Spark) |
| Test data | OpenCSV |
| CI | GitHub Actions (Chrome + Firefox matrix) |

## Running it

Requires JDK 17+ and Maven 3.8+. Chrome or Firefox must be installed; Selenium Manager fetches the matching driver binary automatically, so nothing is checked into the repo.

```bash
# Full regression suite, visible browser
mvn clean test

# Headless, as CI runs it
mvn clean test -Dheadless=true

# A different browser
mvn clean test -Dbrowser=firefox

# Smoke subset only
mvn clean test -DsuiteXmlFile=src/test/resources/smoke.xml
```

**Reports** land in `target/extent-report/index.html`. **Screenshots** of any failure land in `target/screenshots/` and are embedded in the report.

## Structure

```
src/test/java/com/amitsaini/qa/
├── base/
│   ├── BasePage.java         Wait-wrapped click/type/getText — no raw findElement anywhere else
│   ├── BaseTest.java         Fresh browser per test method
│   ├── DriverFactory.java    Chrome/Firefox/Edge construction and options
│   └── DriverManager.java    ThreadLocal driver, which is what makes parallel runs safe
├── pages/
│   ├── LoginPage.java
│   ├── InventoryPage.java
│   ├── CartPage.java
│   └── CheckoutPage.java
├── tests/
│   ├── LoginTests.java
│   ├── InventoryTests.java
│   └── CheckoutTests.java
├── listeners/
│   └── TestListener.java     Extent reporting + screenshot on failure
└── utils/
    ├── ConfigReader.java     -D system properties override config.properties
    ├── CsvDataReader.java    CSV to TestNG DataProvider
    └── ScreenshotUtil.java
```

## Design decisions

**No implicit waits.** Mixing implicit and explicit waits makes timeouts unpredictable. Every wait here is an explicit `WebDriverWait` on a specific condition.

**A fresh browser per test method.** Slower than reusing one session, but it removes the class of bugs where a test only passes because of the state a previous test left behind.

**Page objects return page objects.** `loginAs()` returns an `InventoryPage`; `loginExpectingFailure()` returns a `LoginPage`. The types make invalid flows fail to compile.

**Assertions carry messages.** A failure should say what was expected without anyone having to open the code.

## Traceability

Each `@Test` has a `description` holding its test case ID (`TC-LOGIN-01`, `TC-CHK-04` and so on), so the Extent report doubles as a test execution record.
