package config;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Screen;
import org.testng.Assert;

import com.relevantcodes.extentreports.LogStatus;

import executionEngine.DriverScript;
import utility.ExcelUtils;
import utility.Log;
import utility.Zip;

public class ActionKeywords {

	public static WebDriver driver;
	public static HashMap<String, String> hMelements = new HashMap<>();

	/**
	 * @Purpose Open the Browser like Mozilla/Firefox/IE/Chrome
	 * @param objectType
	 * @param objectName
	 * @param data
	 *            (Mozilla/Firefox/IE/Chrome)
	 */
	public static void openBrowser(String objectType, String objectName, String data) {
		try {
			if (data.equalsIgnoreCase("Mozilla") || data.equalsIgnoreCase("Firefox")) {
				System.setProperty("webdriver.gecko.driver", DriverScript.settings.get("Firefox_DriverPath"));
				DesiredCapabilities capabilities = DesiredCapabilities.firefox();
				capabilities.setCapability("marionette", true);
				driver = new FirefoxDriver();
				DriverScript.testStepsResult = true;
				Log.info("Mozilla browser started");
			} else if (data.equalsIgnoreCase("IE")) {
				System.setProperty("webdriver.ie.driver", DriverScript.settings.get("IE_DriverPath"));
				driver = new InternetExplorerDriver();
				Log.info("IE browser started");
				DriverScript.testStepsResult = true;
			} else if (data.equalsIgnoreCase("Chrome")) {
				ChromeOptions options = new ChromeOptions();
				System.setProperty("webdriver.chrome.driver", DriverScript.settings.get("Chrome_DriverPath"));
				// options.addArguments("test-type");
				// options.addArguments("start-maximized");
				// options.addArguments("--enable-automation");
				// options.addArguments("test-type=browser");
				// options.addArguments("disable-infobars");
				// options.addArguments("disable-extensions");

				options.addArguments("disable-extensions");
				options.addArguments("--start-maximized");

				driver = new ChromeDriver(options);
				Log.info("Chrome browser started");
				DriverScript.testStepsResult = true;

			} else {
				Log.error(
						"Class ActionKeywords | Method openBrowser | Exception desc : Incorrect browser Name=" + data);
				DriverScript.test.log(LogStatus.FAIL,
						"Class ActionKeywords | Method openBrowser | Exception desc : Incorrect browser Name=" + data);
				DriverScript.testStepList.add(
						"Class ActionKeywords | Method openBrowser | Exception desc : Incorrect browser Name=" + data);
				DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());
				DriverScript.testStepsResult = false;
				Assert.fail();
			}

			int implicitWaitTime = (Integer.parseInt(DriverScript.settings.get("IMPLICITE_WAIT").trim()));
			driver.manage().timeouts().implicitlyWait(implicitWaitTime, TimeUnit.SECONDS);
			driver.manage().window().maximize();
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method openBrowser | Exception desc : " + e.getMessage());
			// DriverScript.test.log(LogStatus.FAIL,
			// "Class ActionKeywords | Method openBrowser | Exception desc : " +
			// e.getMessage());
			// DriverScript.testStepList
			// .add("Class ActionKeywords | Method openBrowser | Exception desc:
			// " + e.getMessage());
			// DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());
			DriverScript.testStepsResult = true;
		}
	}

	public static void logReports(String objectType, String objectName, String data) {
		Log.info(data);
		DriverScript.test.log(LogStatus.INFO, data);
		DriverScript.testStepList.add(data);
		DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_PASS").trim());
		DriverScript.testStepsResult = true;
	}

	public static void fullScreen(String objectType, String objectName, String data) {
		try {
			// Log.info("Full Screen Browser");
			driver.manage().window().fullscreen();
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method fullScreen | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method fullScreen | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method fullScreen | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void launchNativeMobileApp(String objectType, String objectName, String data) {

		try {
			File appdir = new File(DriverScript.settings.get("EMULATOR_PATH"));
			File app = new File(appdir, DriverScript.settings.get("APK_NAME"));

			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("platformName", "Android");
			capabilities.setCapability("deviceName", DriverScript.settings.get("DEVICE_NAME"));
			capabilities.setCapability("newCommandTimeout", DriverScript.settings.get("MOBILE_APP_ELEMENT_WAIT"));
			capabilities.setCapability("app", app.getAbsolutePath());
			// Log.info("Launching Native Mobile App...");
			driver = new RemoteWebDriver(new URL(DriverScript.settings.get("HUB_APP_URL")), capabilities);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method launchNativeMobileApp | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method launchNativeMobileApp | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method launchNativeMobileApp | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void setBrowserStack(String objectType, String objectName, String data) {
		try {
			String USERNAME = DriverScript.settings.get("BROWSERSTACK_USERNAME").trim();
			String AUTOMATE_KEY = DriverScript.settings.get("BROWSERSTACK_AUTOMATE_KEY").trim();
			String BROWSER_SERVER = DriverScript.settings.get("BROWSERSTACK_SERVER").trim();
			String URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@" + BROWSER_SERVER + "/wd/hub";
			// Log.info("URL=" + URL);
			DesiredCapabilities caps = new DesiredCapabilities();
			caps.setCapability("browser", DriverScript.settings.get("BROWSERSTACK_BROWSER_NAME"));
			caps.setCapability("browserstack.debug", "true");
			caps.setCapability("build", "First build");

			// For Web
			// caps.setCapability("browser", "IE");
			// caps.setCapability("browser_version", "7.0");
			// caps.setCapability("os", "Windows");
			// caps.setCapability("os_version", "XP");
			// caps.setCapability("browserstack.debug", "true");

			// For Mobile
			// caps.setCapability("browserName", "iPhone");
			// caps.setPlatform(Platform.MAC);
			// caps.setCapability("device", "iPhone 5");

			driver = new RemoteWebDriver(new URL(URL), caps);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method setBrowserStack | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method setBrowserStack | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method setBrowserStack | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	// Create Report Folder
	public static void createReportFolder() {
		String getDate = getCurrentDate();
		String getTime = getCurrentTime("HH_mm_ss");
		Constants.BUILD_NAME = getBuildName(getDate, getTime);

		// To create Current date folder
		checkFolder(getDate);

		Constants.BUILD_PATH = getDate + "/" + Constants.BUILD_NAME;

		checkFolder(Constants.BUILD_PATH);

		Constants.PROJECT_PATH = Constants.BUILD_PATH;
		Constants.TESTCASE_PATH = Constants.PROJECT_PATH;

		// Screenshot folder
		Constants.SCREENSHOTDESCDIR = Constants.TESTCASE_PATH;
	}

	// Create Build Name
	public static String getBuildName(String getDate, String getTime) {
		return (Constants.BUILD_NAME.trim() + getDate + getTime);
	}

	public static void verifyText(String objectType, String objectName, String data) {
		try {
			if (objectName.contains("temp")) {
				objectName = objectName.replaceAll("temp", data.trim());
			}

			checkElementVisible(objectType, objectName, data);

			Assert.assertTrue(findElement(objectType, objectName).getText().trim().equals(data.trim()),
					"Expected Text=" + data + " not found on the screen.");
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method verifyText | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method verifyText | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method verifyText | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void verifyPartialText(String objectType, String objectName, String data) {
		try {
			if (objectName.contains("temp")) {
				objectName = objectName.replaceAll("temp", data.trim());
			}

			checkElementVisible(objectType, objectName, data);

			Log.info("Actual Data=" + findElement(objectType, objectName).getText().trim());
			Log.info("Expected Data=" + data);

			DriverScript.testStepsResult = true;

			Assert.assertTrue(findElement(objectType, objectName).getText().trim().contains(data.trim()),
					"Expected Text=" + data + " not found on the screen.");
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method verifyText | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method verifyText | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method verifyText | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void executeScriptClick(String objectType, String objectName, String data) {
		try {
			WebElement element = findElement(objectType, objectName);
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].click();", element);
			waitForPageLoad(driver);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method executeScriptClick | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method executeScriptClick | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method executeScriptClick | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void executeScriptTextClick(String objectType, String objectName, String data) {
		try {

			if (objectName.contains("temp")) {
				objectName = objectName.replaceAll("temp", data.trim());
			}

			WebElement element = findElement(objectType, objectName);
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].click();", element);
			waitForPageLoad(driver);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method executeScriptClick | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method executeScriptClick | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method executeScriptClick | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void actionMoveToText(String objectType, String objectName, String data) {
		try {

			if (objectName.contains("temp")) {
				objectName = objectName.replaceAll("temp", data.trim());
			}

			WebElement element = findElement(objectType, objectName);
			Actions action = new Actions(driver);
			action.moveToElement(element).click().build().perform();
			waitForPageLoad(driver);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method actionMoveToElement | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method actionMoveToElement | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method actionMoveToElement | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void clearResults(String excelPath, String sheetName, int resultColumn, String data) {
		int rowNum = ExcelUtils.getRowCount(excelPath, sheetName);
		for (int iRowNum = 1; iRowNum < rowNum; iRowNum++) {
			ExcelUtils.setCellDataValue(data, iRowNum, resultColumn, excelPath, sheetName);
		}
	}

	public static void GetQuestionIDAndVerify(String objectType, String objectName, String data) {
		try {
			String getValue;
			getValue = getText(objectType, objectName, data).trim();
			String tempGetValue = getValue.substring(getValue.indexOf("Quesiton"), getValue.lastIndexOf("is")).trim();
			Log.info("Question Id=" + tempGetValue);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method GetQuestionIDAndVerify | Exception desc : " + e.getMessage());
			DriverScript.testStepsResult = false;
		}
	}

	public static void selectCheckBox(String objectType, String objectName, String data) {
		try {
			if (!(findElement(objectType, objectName).isSelected())) {
				click(objectType, objectName, data);
			}
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method selectCheckBox | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method selectCheckBox | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method selectCheckBox | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void selectCheckBoxByText(String objectType, String objectName, String data) {
		try {

			if (objectName.contains("temp")) {
				objectName = objectName.replaceAll("temp", data);
			}

			if (!(findElement(objectType, objectName).isSelected())) {
				click(objectType, objectName, data);
			}
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method selectCheckBox | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method selectCheckBox | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method selectCheckBox | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void deSelectCheckBoxByText(String objectType, String objectName, String data) {
		try {

			if (objectName.contains("temp")) {
				objectName = objectName.replaceAll("temp", data);
			}

			if (findElement(objectType, objectName).isSelected()) {
				click(objectType, objectName, data);
			}
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method deSelectCheckBox | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method deSelectCheckBox | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method deSelectCheckBox | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}

	}

	/**
	 * @purpose Open the specified URL in browser
	 * @param objectType
	 * @param objectName
	 * @param data
	 */

	public static void getUrl(String objectType, String objectName, String data) {
		try {
			// Log.info("Get URL");
			driver.get(data);
			waitForPageLoad(driver);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method getUrl | Exception desc : " + e.getMessage());
			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method getUrl | Exception desc : " + e.getMessage());
			DriverScript.testStepList.add("Class ActionKeywords | Method getUrl | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());
			DriverScript.testStepsResult = false;
		}
	}

	public static void maximizeBrowser(String objectType, String objectName, String data) {
		try {
			// Log.info("Maximize Browser");
			driver.manage().window().maximize();
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method maximizeBrowser | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method maximizeBrowser | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method maximizeBrowser | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void navigateBack(String objectType, String objectName, String data) {
		try {
			// Log.info("Navigate Back " + objectType);
			driver.navigate().back();
			waitForPageLoad(driver);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method navigateBack | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method navigateBack | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method navigateBack | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void navigateTo(String objectType, String objectName, String data) {
		try {
			// Log.info("Navigate To " + objectType);
			driver.navigate().to(data);
			waitForPageLoad(driver);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method navigateTo | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method navigateTo | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method navigateTo | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void navigateForward(String objectType, String objectName, String data) {
		try {
			// Log.info("Navigate Forward " + objectType);
			driver.navigate().forward();
			waitForPageLoad(driver);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method navigateForward | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method navigateForward | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method navigateForward | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void pageRefresh(String objectType, String objectName, String data) {
		try {
			// Log.info("Page Refresh " + objectType);
			driver.navigate().refresh();
			waitForPageLoad(driver);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method pageRefresh | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method pageRefresh | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method pageRefresh | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void click(String objectType, String objectName, String data) {
		try {
			// Log.info("Clicking on Webelement " + objectType);
			if (objectType.equalsIgnoreCase("linktext")) {
				objectName = data;
			}

			waitForElement(objectType, objectName, data);
			waitForElementToBeClickable(objectType, objectName, data);
			if (!(isElementClickable(objectType, objectName, data))) {
				scrollToElement(objectType, objectName, data);
			}
			findElement(objectType, objectName).click();

			waitForPageLoad(driver);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method click | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method click | Exception desc : " + e.getMessage());
			DriverScript.testStepList.add("Class ActionKeywords | Method click | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void clickOnText(String objectType, String objectName, String data) {
		try {

			if (objectName.contains("temp")) {
				objectName = objectName.replaceAll("temp", data);
			}

			waitForElement(objectType, objectName, data);
			waitForElementToBeClickable(objectType, objectName, data);
			scrollToElement(objectType, objectName, data);
			findElement(objectType, objectName).click();

			waitForPageLoad(driver);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method click | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method click | Exception desc : " + e.getMessage());
			DriverScript.testStepList.add("Class ActionKeywords | Method click | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void submit(String objectType, String objectName, String data) {
		try {
			// Log.info("Clicking on Submit " + objectType);
			waitForElement(objectType, objectName, data);
			waitForElementToBeClickable(objectType, objectName, data);
			findElement(objectType, objectName).submit();
			waitForPageLoad(driver);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method submit | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method submit | Exception desc : " + e.getMessage());
			DriverScript.testStepList.add("Class ActionKeywords | Method submit | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void clear(String objectType, String objectName, String data) {
		try {
			// Log.info("Clearing the text in " + objectType);
			waitForElement(objectType, objectName, data);
			findElement(objectType, objectName).clear();
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method clear | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method clear | Exception desc : " + e.getMessage());
			DriverScript.testStepList.add("Class ActionKeywords | Method clear | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void impliciteWait(String objectType, String objectName, String data) {
		try {
			// Log.info("Implicite Wait " + objectType);
			driver.manage().timeouts().implicitlyWait(Integer.parseInt(data), TimeUnit.SECONDS);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method impliciteWait | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method impliciteWait | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method impliciteWait | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void switchToWindow(String objectType, String objectName, String data) {
		try {
			// Log.info("Switch to Window " + objectType);
			String currentWindow = driver.getWindowHandle();
			HashMap<String, String> outputData = new HashMap<String, String>();
			outputData.put(data, currentWindow);
			ActionKeywords.UpdatePropertyFile(DriverScript.settings.get("RuntimeOutputfile").trim(), outputData);
			Log.info("Parent Window " + outputData);
			Set<String> allWindows = driver.getWindowHandles();
			for (String curWindow : allWindows) {
				if (!currentWindow.equals(curWindow)) {
					driver.switchTo().window(curWindow);
					Log.info("Child Window " + curWindow);
				}
			}
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method switchToWindow | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method switchToWindow | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method switchToWindow | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void switchToDefaultContent(String objectType, String objectName, String data) {
		try {
			// Log.info("Switch to default contents " + objectType);

			driver.switchTo().defaultContent();

			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method switchToDefaultContent | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method switchToDefaultContent | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method switchToDefaultContent | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void switchToFrame(String objectType, String objectName, String data) {
		try {
			// Log.info("Switch to Frame " + objectType);

			if (objectType.equalsIgnoreCase("index")) {
				driver.switchTo().frame(Integer.parseInt(data));
			} else if (objectType.equalsIgnoreCase("name")) {
				driver.switchTo().frame(data);
			} else {
				driver.switchTo().frame(0);
			}

			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method switchToFrame | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method switchToFrame | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method switchToFrame | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void uploadFileFromFolder(String objectType, String objectName, String data) {

		// creating instance of Robot class
		try {
			StringSelection ss = new StringSelection(data);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
			Thread.sleep(500);
			// paste file path in windows pop-up
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);

			robot.delay(2000);

		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void uploadFileUsingAutoIT(String objectType, String objectName, String data) {

		try {

			WebElement importButton = findElement(objectType, objectName);
			importButton.click();
			Thread.sleep(2000L);
			Runtime.getRuntime().exec(data);

			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method click | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method click | Exception desc : " + e.getMessage());
			DriverScript.testStepList.add("Class ActionKeywords | Method click | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}

	}

	public static void uploadFile(String objectType, String objectName, String data) {
		try {

			data = randomValues(data.trim());
			// Log.info("Entering the text in " + objectType);
			// waitForElement(objectType, objectName, data);
			findElement(objectType, objectName).sendKeys(data);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method uploadFile | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method uploadFile | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method uploadFile | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void findElementUsingSikuli(String objectType, String srcElement, String data)
			throws IOException, FindFailed {

		System.out.println("findElementUsingSikuli Function called");
		Screen s = new Screen();
		s.find("D:\\TaskDesignIcon\\Decision.PNG");
		s.doubleClick("D:\\TaskDesignIcon\\Decision.PNG");
		System.out.println("File icon clicked");

	}

	/**
	 * This method drags and drops elements from the panel to the grid
	 * 
	 * @param objectType
	 * @param srcElement
	 * @param data
	 */
	public static void dragDropElementsToGrid(String objectType, String srcElement, String location) {
		try {

			String gridXpath = "//div[@class='joint-paper joint-theme-modern']//*[local-name()='svg']/*[local-name()='g']/*[local-name()='g'][contains(@id,'j_')]";

			// Split string to get x,y co-ordinates
			String[] loc = location.split(",");
			int x = Integer.parseInt(loc[0].trim());
			int y = Integer.parseInt(loc[1].trim());

			// Fetch the elements before doing the operation
			List<WebElement> elements = driver.findElements(By.xpath(gridXpath));
			// You would be getting the last value present by using getAttribute
			String lastElement = elements.get(elements.size() - 1).getAttribute("id");

			WebElement drag = findElement(objectType, srcElement);
			// Create object of actions class
			Actions act = new Actions(driver);
			// Click & Hold drag Webelement
			// act.clickAndHold(drag).build().perform();
			act.dragAndDropBy(drag, x, y).build().perform();

			List<WebElement> newelements = driver.findElements(By.xpath(gridXpath));
			// You would be getting the last value present by using getAttribute
			String newLastElement = newelements.get(newelements.size() - 1).getAttribute("id");

			// on
			// Your new element can be found using
			WebElement requiredElement = newelements.get(elements.size());

			System.out.println("Element name " + requiredElement.getText());
			String modelId = requiredElement.getAttribute("model-id");
			System.out.println("MODEL ID: " + modelId);

			act.moveToElement(requiredElement).click().build().perform();
			// clickAndConnectElements("xpath", "", "");
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method enterValue | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method enterValue | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method enterValue | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}

	}

	/**
	 * This method connects two elements in the workflow using an arrow
	 * 
	 * @param objectType
	 * @param srcElement
	 * @param data
	 */
	public static void clickAndConnectElements(String objectType, String srcElement, String data) {

		try {
			String[] loc = data.split(",");
			int x = Integer.parseInt(loc[0].trim());
			int y = Integer.parseInt(loc[1].trim());
			Actions act = new Actions(driver);
			WebElement arrow = findElement(objectType, srcElement);
			act.dragAndDropBy(arrow, x, y).build().perform();

			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method enterValue | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method enterValue | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method enterValue | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void enterValue(String objectType, String objectName, String data) {
		try {

			// data = randomValues(data.trim());
			// Log.info("Entering the text in " + objectType);
			waitForElement(objectType, objectName, data);
			clear(objectType, objectName, data);
			WebElement textBox = findElement(objectType, objectName);
			Actions actions = new Actions(driver);
			actions.moveToElement(textBox);
			actions.click();
			actions.sendKeys(data);
			actions.build().perform();
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method enterValue | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method enterValue | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method enterValue | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	/**
	 * The purpose of this method is to send multiple special keyboard keys
	 * simultaneously as input to textbox eg. ctrl+space
	 * 
	 * @param objectType
	 * @param objectName
	 * @param data
	 */
	public static void enterKeysAsValue(String objectType, String objectName, String data) {
		try {
			String keys = null;
			// Log.info("Entering the text in " + objectType);
			waitForElement(objectType, objectName, data);
			clear(objectType, objectName, data);

			keys = selectKeys(data);
			WebElement textBox = findElement(objectType, objectName);
			Actions actions = new Actions(driver);
			actions.moveToElement(textBox);
			actions.click();
			actions.sendKeys(Keys.chord(Keys.CONTROL, Keys.SPACE));
			// actions.sendKeys(keys);
			actions.build().perform();
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method enterValue | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method enterValue | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method enterValue | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void enterMultipleLinesValue(String objectType, String objectName, String data) {
		try {

			// data = randomValues(data.trim());
			// Log.info("Entering the text in " + objectType);
			waitForElement(objectType, objectName, data);
			clear(objectType, objectName, data);
			String newData = processString(data, ",", Keys.ENTER);
			findElement(objectType, objectName).sendKeys(newData);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method enterValue | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method enterValue | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method enterValue | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static String processString(String data, CharSequence findChar, CharSequence replaceChar) {
		if (data != null) {
			data = data.trim();
			if (data.contains(",")) {
				data = data.replace(findChar, replaceChar);
			}
		}
		return data;
	}

	/**
	 * The purpose of this method is to accept multiple keyboard keys and
	 * convert into string to pass as key chord parameter to text area
	 * 
	 * @param data
	 * @return chordKeys
	 */
	public static String selectKeys(String data) {
		String s[] = null;
		CharSequence keys[] = null;

		if (data != null) {
			data = data.trim();
			if (data.contains(",")) {
				s = data.split(",");
				keys = new String[s.length];
			}

			for (int i = 0; i < s.length; i++) {
				switch (s[i].trim().toLowerCase()) {
				case "ctrl":
					keys[i] = "Keys.CONTROL";
					break;

				case "space":
					keys[i] = "Keys.SPACE";
					break;

				default:
					break;
				}
			}
		}

		// converting string array to string
		String str = String.join(", ", keys);

		String chordKeys = "Keys.chord(" + str + ")";
		return chordKeys;
	}

	public static void enterDate(String objectType, String objectName, String data) {
		try {

			data = randomValues(data.trim());
			// Log.info("Entering the text in " + objectType);
			waitForElement(objectType, objectName, data);

			// if (driver instanceof JavascriptExecutor) {
			// WebElement element = driver.findElement(By.xpath(objectName));
			// JavascriptExecutor jse = (JavascriptExecutor) driver;
			// jse.executeScript("document.getElementById('" + objectName +
			// "').value = 09092017");
			// jse.executeScript("document.getElementById('contactDate').value =
			// 09092017");
			// jse.executeScript("document.frmConductReview.addLogForm.contactDate.value
			// = '2017-09-09'");
			// Log.error("1");
			// jse.executeScript("document.getElementById('contactDate').value =
			// '09092017'");
			// Log.error("2");
			// jse.executeScript("document.getElementById('contactDate').value =
			// '09-09-2017'");
			// Log.error("3");
			// jse.executeScript("document.getElementById('contactDate').value =
			// '" + new Date() + "'");
			// Log.error("4");

			//
			// //((JavascriptExecutor)
			// driver).executeScript("arguments[0].val‌​ue=arguments[1]",element,
			// "05-05-2017");
			// }

			// ((JavascriptExecutor)driver).executeScript
			// ("document.getElementById('dateofbirth').removeAttribute('readonly',0);");
			// ((JavascriptExecutor)
			// driver).executeScript("document.getElementsByName('date'[0]).removeAttribute('readonly');");

			findElement(objectType, objectName).sendKeys(data);

			// ((JavascriptExecutor)
			// driver).executeScript("arguments[0].val‌​ue=arguments[1]",findElement(objectType,
			// objectName), data);

			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method enterDate | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method enterDate | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method enterDate | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static String getText(String objectType, String objectName, String data) {
		try {
			// Log.info("Read text " + objectType);
			waitForElement(objectType, objectName, data);
			scrollToElement(objectType, objectName, data);
			String readValue = findElement(objectType, objectName).getText();
			Log.info("Read Value= " + readValue);
			DriverScript.test.log(LogStatus.INFO, "ReadValue=" + readValue);
			HashMap<String, String> outputData = new HashMap<String, String>();
			outputData.put(data, readValue);
			ActionKeywords.UpdatePropertyFile(DriverScript.settings.get("RuntimeOutputfile").trim(), outputData);

			// DriverScript.outputInExcel.clear();
			DriverScript.testStepsResult = true;
			return readValue;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method getText | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method getText | Exception desc : " + e.getMessage());
			DriverScript.testStepList.add("Class ActionKeywords | Method getText | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
			return null;
		}
	}

	public static String getAttributeValue(String objectType, String objectName, String data) {
		try {
			// Log.info("Read text " + objectType);
			waitForElement(objectType, objectName, data);
			scrollToElement(objectType, objectName, data);
			String readValue = findElement(objectType, objectName).getAttribute("value");
			Log.info("Read Value= " + readValue);
			DriverScript.test.log(LogStatus.INFO, "ReadValue=" + readValue);
			HashMap<String, String> outputData = new HashMap<String, String>();
			outputData.put(data, readValue);
			ActionKeywords.UpdatePropertyFile(DriverScript.settings.get("RuntimeOutputfile").trim(), outputData);
			DriverScript.testStepsResult = true;
			return readValue;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method getAttributeValue | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method getAttributeValue | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method getAttributeValue | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
			return null;
		}
	}

	public static String getAndCompaireAttributeValue(String objectType, String objectName, String data) {
		try {
			// Log.info("Read text " + objectType);
			waitForElement(objectType, objectName, data);
			scrollToElement(objectType, objectName, data);
			String readValue = findElement(objectType, objectName).getAttribute("value");
			Log.info("Read Value= " + readValue);
			DriverScript.test.log(LogStatus.INFO, "ReadValue=" + readValue);

			// HashMap<String, String> outputData = new HashMap<String,
			// String>();
			// outputData.put(data, readValue);
			// ActionKeywords.UpdatePropertyFile(DriverScript.settings.get("RuntimeOutputfile").trim(),
			// outputData);

			Assert.assertTrue(readValue.trim().equals(data.trim()),
					"Expected Value=" + data + " not found, Instead Actual Value=" + readValue + " Found.");

			DriverScript.testStepsResult = true;
			return readValue;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method getAttributeValue | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method getAttributeValue | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method getAttributeValue | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
			return null;
		}
	}

	public static void storeOutput(String objectType, String objectName, String data) {
		try {
			HashMap<String, String> dataFromDile = new HashMap<String, String>();
			dataFromDile = readProperty(DriverScript.settings.get("RuntimeOutputfile").trim());
			String readValue = dataFromDile.get(data);
			// output in excel
			DriverScript.outputInExcel.put(data, readValue);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method storeOutput | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method storeOutput | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method storeOutput | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void readAndClickOnValueFromFile(String objectType, String objectName, String data) {
		try {
			// HashMap<String, String> dataFromDile = new HashMap<String,
			// String>();
			// dataFromDile =
			// readProperty(DriverScript.settings.get("RuntimeOutputfile").trim());
			// String readValue = dataFromDile.get(data);
			// output in excel

			// waitForElement(objectType, objectName,
			// DriverScript.outputInExcel.get(data));
			// scrollToElement(objectType, objectName,
			// DriverScript.outputInExcel.get(data));
			click(objectType, objectName, DriverScript.outputInExcel.get(data));

			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method storeOutput | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method storeOutput | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method storeOutput | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void readAndVerifyValueFromFile(String objectType, String objectName, String data) {
		try {

			// HashMap<String, String> dataFromDile = new HashMap<String,
			// String>();
			// dataFromDile =
			// readProperty(DriverScript.settings.get("RuntimeOutputfile").trim());
			// String readValue = dataFromDile.get(data);
			// output in excel

			// waitForElement(objectType, objectName,
			// DriverScript.outputInExcel.get(data));
			// scrollToElement(objectType, objectName,
			// DriverScript.outputInExcel.get(data));
			verifyText(objectType, objectName, DriverScript.outputInExcel.get(data));

			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method storeOutput | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method storeOutput | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method storeOutput | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static String setBingoCode(String objectType, String objectName, String data) {
		try {
			waitForElement(objectType, objectName, data);
			scrollToElement(objectType, objectName, data);

			HashMap<String, String> dataFromDile = new HashMap<String, String>();
			dataFromDile = readProperty(DriverScript.settings.get("RuntimeOutputfile").trim());
			String readValue = dataFromDile.get(data);
			String bingoLastDigit = readValue.substring(readValue.length() - 1, readValue.length()).trim();

			readValue = bingoLastDigit + "" + bingoLastDigit + "" + bingoLastDigit;

			Log.info("Bingo Value= " + readValue);
			DriverScript.test.log(LogStatus.INFO, "Bingo Value=" + readValue);
			HashMap<String, String> outputData = new HashMap<String, String>();
			outputData.put(data, readValue);
			ActionKeywords.UpdatePropertyFile(DriverScript.settings.get("RuntimeOutputfile").trim(), outputData);

			findElement(objectType, objectName).sendKeys(readValue);

			DriverScript.testStepsResult = true;
			return readValue;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method setBingoCode | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method setBingoCode | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method setBingoCode | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
			return null;
		}
	}

	public static String getTitle(String objectType, String objectName, String data) {
		try {
			// Log.info("Page Title " + objectType);
			String title = driver.getTitle();
			Log.info("Page Title= " + title);
			DriverScript.test.log(LogStatus.INFO, "PageTitle=" + title);
			HashMap<String, String> outputData = new HashMap<String, String>();
			outputData.put(data, title);
			ActionKeywords.UpdatePropertyFile(DriverScript.settings.get("RuntimeOutputfile").toString().trim(),
					outputData);
			DriverScript.testStepsResult = true;
			return title;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method getTitle | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method getTitle | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method getTitle | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
			return null;
		}

	}

	public static String verifyTitle(String objectType, String objectName, String data) {
		try {
			// Log.info("Page Title " + objectType);
			String title = driver.getTitle();
			Assert.assertTrue(title.trim().equals(data.trim()),
					"Expected Title=" + data + " not found, Instead Actual Title=" + title + " Found.");
			DriverScript.testStepsResult = true;
			return title;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method verifyTitle | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method verifyTitle | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method verifyTitle | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
			return null;
		}

	}

	public static String getCurrentUrl(String objectType, String objectName, String data) {
		try {
			// Log.info("Get Current URL" + objectType);
			String currentUrl = driver.getCurrentUrl();
			// Log.info("Current URL= " + currentUrl);
			DriverScript.testStepsResult = true;
			return currentUrl;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method getCurrentUrl | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method getCurrentUrl | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method getCurrentUrl | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
			return null;
		}

	}

	public static void deselectByVisibleText(String objectType, String objectName, String data) {
		try {
			// Log.info("Deselect By Visible Text" + objectType);
			waitForElement(objectType, objectName, data);
			findElement(objectType, objectName).click();
			new Select(findElement(objectType, objectName)).deselectByVisibleText(data);
			waitForPageLoad(driver);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method deselectByVisibleText | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method deselectByVisibleText | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method deselectByVisibleText | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void deselectByValue(String objectType, String objectName, String data) {
		try {
			// Log.info("Deselect By Value" + objectType);
			waitForElement(objectType, objectName, data);
			findElement(objectType, objectName).click();
			new Select(findElement(objectType, objectName)).deselectByValue(data);
			waitForPageLoad(driver);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method deselectByValue | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method deselectByValue | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method deselectByValue | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void deselectByIndex(String objectType, String objectName, String data) {
		try {
			// Log.info("Deselect By Index" + objectType);
			waitForElement(objectType, objectName, data);
			findElement(objectType, objectName).click();
			new Select(findElement(objectType, objectName)).deselectByIndex(Integer.parseInt(data));
			waitForPageLoad(driver);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method deselectByIndex | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method deselectByIndex | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method deselectByIndex | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void deselectAll(String objectType, String objectName, String data) {
		try {
			// Log.info("Deselect All" + objectType);
			waitForElement(objectType, objectName, data);
			findElement(objectType, objectName).click();
			new Select(findElement(objectType, objectName)).deselectAll();
			waitForPageLoad(driver);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method deselectAll | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method deselectAll | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method deselectAll | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void waitForSeconds(String objectType, String objectName, String data) {
		try {
			Log.info("Wait for " + Integer.parseInt(data) + " seconds");
			Thread.sleep(Integer.parseInt(data) * 1000);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method waitForSeconds | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method waitForSeconds | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method waitForSeconds | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void closeCurrentBrowser(String objectType, String objectName, String data) {
		try {
			// Log.info("Closing the current browser tab");
			driver.close();
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method closeCurrentBrowser | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method closeCurrentBrowser | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method closeCurrentBrowser | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void closeBrowser(String objectType, String objectName, String data) {
		try {
			// Log.info("Closing the all open browser tabs");
			driver.close();
			driver.quit();
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method closeBrowser | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method closeBrowser | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method closeBrowser | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static boolean isAlertPresent() {
		try {
			driver.switchTo().alert();
			DriverScript.testStepsResult = true;
			return true;
		} catch (Exception Ex) {
			DriverScript.testStepsResult = false;
			return false;
		}
	}

	public static void switchToAlert(String objectType, String objectName, String data) {
		try {
			// Log.info("Switch To Alert " + objectType);
			if (isAlertPresent()) {
				Alert alert = driver.switchTo().alert();
				alert.getText();
			} else {
				// Log.error("Alert not present.");
				DriverScript.testStepsResult = false;
			}
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method switchToAlert | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method switchToAlert | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method switchToAlert | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static String getAlertText(String objectType, String objectName, String data) {
		try {
			// Log.info("Get Alert Text " + objectType);
			String alertText = null;
			if (isAlertPresent()) {
				Alert alert = driver.switchTo().alert();
				alertText = alert.getText();
				// Log.info("Alert Text=" + alertText);

				HashMap<String, String> outputData = new HashMap<String, String>();
				outputData.put(data, alertText);
				ActionKeywords.UpdatePropertyFile(DriverScript.settings.get("RuntimeOutputfile").toString().trim(),
						outputData);

				DriverScript.testStepsResult = true;

			} else {
				Log.error("Class ActionKeywords | Method getAlertText | Exception desc : Alert not present.");

				DriverScript.test.log(LogStatus.FAIL,
						"Class ActionKeywords | Method getAlertText | Exception desc : Alert not present.");
				DriverScript.testStepList
						.add("Class ActionKeywords | Method getAlertText | Exception desc : Alert not present.");
				DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

				DriverScript.testStepsResult = false;
			}

			return alertText;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method getAlertText | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method getAlertText | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method getAlertText | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
			return null;
		}
	}

	public static void alertAccept(String objectType, String objectName, String data) {
		try {
			// Log.info("Alert Accept " + objectType);

			if (isAlertPresent()) {
				Alert alert = driver.switchTo().alert();
				alert.accept();
				DriverScript.testStepsResult = true;
			} else {
				Log.error("Class ActionKeywords | Method alertAccept | Exception desc : Alert not present.");

				DriverScript.test.log(LogStatus.FAIL,
						"Class ActionKeywords | Method alertAccept | Exception desc : Alert not present.");
				DriverScript.testStepList
						.add("Class ActionKeywords | Method alertAccept | Exception desc : Alert not present.");
				DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

				DriverScript.testStepsResult = false;
			}

		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method alertAccept | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method alertAccept | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method alertAccept | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void alertDismiss(String objectType, String objectName, String data) {
		try {
			// Log.info("Alert Dismiss " + objectType);

			if (isAlertPresent()) {
				Alert alert = driver.switchTo().alert();
				alert.dismiss();
				DriverScript.testStepsResult = true;
			} else {
				Log.error("Class ActionKeywords | Method alertDismiss | Exception desc : Alert not present.");

				DriverScript.test.log(LogStatus.FAIL,
						"Class ActionKeywords | Method alertDismiss | Exception desc : Alert not present.");
				DriverScript.testStepList
						.add("Class ActionKeywords | Method alertDismiss | Exception desc : Alert not present.");
				DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

				DriverScript.testStepsResult = false;
			}

		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method alertDismiss | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method alertDismiss | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method alertDismiss | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void alertEnterValue(String objectType, String objectName, String data) {
		try {
			// Log.info("Alert Sendkeys");
			Alert alert = driver.switchTo().alert();
			alert.sendKeys(data);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method alertSendKeys | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method alertSendKeys | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method alertSendKeys | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void getBrowserName(String objectType, String objectName, String data) {
		try {
			// Log.info("Get Browser Name");
			Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
			String browserName = cap.getBrowserName().toLowerCase();
			Log.info("Browser Name=" + browserName);
			String os = cap.getPlatform().toString();
			Log.info("OS=" + os);
			String v = cap.getVersion().toString();
			Log.info("Version=" + v);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method getBrowserName | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method getBrowserName | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method getBrowserName | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void getPlatform(String objectType, String objectName, String data) {
		try {
			// Log.info("Get Platform");
			Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
			String platform = cap.getPlatform().toString();
			Log.info("Platform=" + platform);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method getPlatform | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method getPlatform | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method getPlatform | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void getBrowserVersion(String objectType, String objectName, String data) {
		try {
			// Log.info("Get Browser Version");
			Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();

			String browserVersion = cap.getVersion().toString();
			Log.info("Browser Version=" + browserVersion);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method getBrowserVersion | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method getBrowserVersion | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method getBrowserVersion | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void scrollToElement(String objectType, String objectName, String data) {
		try {
			if (driver instanceof JavascriptExecutor) {
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
						findElement(objectType, objectName));
				DriverScript.testStepsResult = true;
			}
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method scrollToElement | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method scrollToElement | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method scrollToElement | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	/**
	 * @purpose Find element present
	 * @param objectType
	 * @param locator
	 * @return
	 */
	public static WebElement findElement(String objectType, String locator) {
		WebElement element = null;
		switch (objectType.trim().toLowerCase()) {
		case "id":
			element = driver.findElement(By.id(locator));
			break;
		case "name":
			element = driver.findElement(By.name(locator));
			break;
		case "xpath":
			element = driver.findElement(By.xpath(locator));
			break;
		case "css":
			element = driver.findElement(By.cssSelector(locator));
			break;
		case "linktext":
			element = driver.findElement(By.linkText(locator));
			break;
		case "partiallinktext":
			element = driver.findElement(By.partialLinkText(locator));
			break;
		case "tagname":
			element = driver.findElement(By.tagName(locator));
			break;
		case "classname":
			element = driver.findElement(By.className(locator));
			break;
		default:
			Log.error("Class ActionKeywords | Method findElement | Exception desc : Incorrect locator of type="
					+ objectType + " and locator=" + locator);

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method findElement | Exception desc : Incorrect locator of type="
							+ objectType + " and locator=" + locator);
			DriverScript.testStepList
					.add("Class ActionKeywords | Method findElement | Exception desc : Incorrect locator of type="
							+ objectType + " and locator=" + locator);
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			element = null;
			break;
		}
		return element;
	}

	public static int size(String objectType, String objectName, String data) {
		try {
			// Log.info("Num of elements in list");
			waitForElement(objectType, objectName, data);
			int count = findElements(objectType, objectName).size();
			// Log.info("size=" + count);
			DriverScript.testStepsResult = true;
			return count;

		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method size | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method size | Exception desc : " + e.getMessage());
			DriverScript.testStepList.add("Class ActionKeywords | Method size | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
			return 0;
		}
	}

	/**
	 * @purpose Find list of elements presetnt.
	 * @param objectType
	 * @param locator
	 * @return
	 */
	public static List<WebElement> findElements(String objectType, String locator) {
		List<WebElement> element = null;
		switch (objectType.trim().toLowerCase()) {
		case "id":
			element = driver.findElements(By.id(locator));
			break;
		case "name":
			element = driver.findElements(By.name(locator));
			break;
		case "xpath":
			element = driver.findElements(By.xpath(locator));
			break;
		case "css":
			element = driver.findElements(By.cssSelector(locator));
			break;
		case "linktext":
			element = driver.findElements(By.linkText(locator));
			break;
		case "partiallinktext":
			element = driver.findElements(By.partialLinkText(locator));
			break;
		case "tagname":
			element = driver.findElements(By.tagName(locator));
			break;
		case "classname":
			element = driver.findElements(By.className(locator));
			break;
		default:
			Log.error("Class ActionKeywords | Method size | Exception desc : Incorrect locator of type=" + objectType
					+ " and locator=" + locator);

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method findElements | Exception desc : Incorrect locator of type="
							+ objectType + " and locator=" + locator);
			DriverScript.testStepList
					.add("Class ActionKeywords | Method findElements | Exception desc : Incorrect locator of type="
							+ objectType + " and locator=" + locator);
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			element = null;
			break;
		}
		return element;
	}

	/**
	 * @purpose Wait for page to load completely
	 * @param browser
	 */
	public static void waitForPageLoad(WebDriver browser) {
		ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver browser) {
				return ((JavascriptExecutor) browser).executeScript("return document.readyState").equals("complete");
			}
		};

		WebDriverWait wait = new WebDriverWait(browser,
				Integer.parseInt(DriverScript.settings.get("WAITFORELEMENT_WAIT").trim()));
		wait.until(pageLoadCondition);

		// GridLoad("","","");
	}

	/**
	 * @author Pramod.Patil
	 * @Purpose: Wait for Element to be visible
	 */
	public static void waitForElement(String objectType, String objectName, String data) {

		try {
			// Log.info("Waiting for element=" + objectName);
			// driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			WebDriverWait wait = new WebDriverWait(driver,
					Integer.parseInt(DriverScript.settings.get("WAITFORELEMENT_WAIT").trim()));
			wait.until(ExpectedConditions.visibilityOfElementLocated(findBy(objectType, objectName)));
			// Log.info("Element " + objectName + " found on the Screen.");
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method waitForElement | Exception desc : " + e.getMessage());

			// DriverScript.test.log(LogStatus.FAIL,
			// "Class ActionKeywords | Method waitForElement | Exception desc :
			// " + e.getMessage());
			// DriverScript.testStepList
			// .add("Class ActionKeywords | Method waitForElement | Exception
			// desc : " + e.getMessage());
			// DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}

	}

	/**
	 * @author Pramod.Patil
	 * @Purpose: Wait for Element to visible
	 */
	public static void waitForElementToBeClickable(String objectType, String objectName, String data) {

		try {
			// Log.info("Waiting for element to be clickable=" + objectName);
			// driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			WebDriverWait wait = new WebDriverWait(driver,
					Integer.parseInt(DriverScript.settings.get("WAITFORELEMENT_WAIT").trim()));
			wait.until(ExpectedConditions.elementToBeClickable(findBy(objectType, objectName)));
			// Log.info("Element " + objectName + " is clickable.");
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method waitForElementToBeClickable | Exception desc : " + e.getMessage());

			// DriverScript.test.log(LogStatus.FAIL,
			// "Class ActionKeywords | Method waitForElementToBeClickable |
			// Exception desc : " + e.getMessage());
			// DriverScript.testStepList.add(
			// "Class ActionKeywords | Method waitForElementToBeClickable |
			// Exception desc : " + e.getMessage());
			// DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}

	}

	public static By findBy(String objectType, String locator) {
		By by = null;
		switch (objectType.trim().toLowerCase()) {
		case "id":
			by = By.id(locator);
			break;
		case "name":
			by = By.name(locator);
			break;
		case "xpath":
			by = By.xpath(locator);
			break;
		case "css":
			by = By.cssSelector(locator);
			break;
		case "linktext":
			by = By.linkText(locator);
			break;
		case "partiallinktext":
			by = By.partialLinkText(locator);
			break;
		case "tagname":
			by = By.tagName(locator);
			break;
		case "classname":
			by = By.className(locator);
			break;
		default:
			Log.error(
					"Class ActionKeywords | Method waitForElementToBeClickable | Exception desc : Incorrect locator of type="
							+ objectType + " and locator=" + locator);

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method waitForElementToBeClickable | Exception desc : Incorrect locator of type="
							+ objectType + " and locator=" + locator);
			DriverScript.testStepList
					.add("Class ActionKeywords | Method waitForElementToBeClickable | Exception desc : Incorrect locator of type="
							+ objectType + " and locator=" + locator);
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			by = null;
			break;
		}
		return by;
	}

	/**
	 * @Purpose To generate the random alha,num,alphanum data values
	 * @param flag
	 * @param length
	 */
	public static String randomValues(String data) {
		String dataValue = null;
		int length = 10;

		if (data.contains(",")) {
			String s[] = data.split(",");
			data = s[0];
			length = Integer.parseInt(s[1]);
		}

		switch (data.trim()) {
		case "NUMDATA":
			final String numString = "1234567890";
			dataValue = generateString(numString, length);
			Log.info("Numeric Value with length " + length + " is " + dataValue);
			break;

		case "ALPHADATA":
			final String alphaString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJLMNOPQRSTUVWXYZ";
			dataValue = generateString(alphaString, length);
			Log.info("Alphabetical Value with length " + length + " is " + dataValue);
			break;

		case "ALPHANUMDATA":
			final String alphaNumString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJLMNOPQRSTUVWXYZ1234567890";
			dataValue = generateString(alphaNumString, length);
			Log.info("Alpha-Numeric Value with length " + length + " is " + dataValue);
			break;

		case "ALPHANUMSPECIALDATA":
			final String alphaNumSpecialString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_+";
			dataValue = generateString(alphaNumSpecialString, length);
			Log.info("Alpha-Numeric-Specail Chars Value with length " + length + " is " + dataValue);
			break;

		case "MOBILEDATA":
			final String mobileString = "1234567890";
			length = 10;
			dataValue = generateString(mobileString, length);
			Log.info("Bobile number with length " + length + " is " + dataValue);
			break;

		case "EMAILDATA":
			final String emailString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJLMNOPQRSTUVWXYZ1234567890_";
			dataValue = generateString(emailString, length);
			dataValue = dataValue + "@gmail.com";
			Log.info("Email ID with length " + length + " is " + dataValue);
			break;

		case "SYS_DATE_MMDDYYYY":
			String sysDate = getCurrentDateMMDDYYYY();
			Log.info("System Date is " + sysDate);
			dataValue = sysDate;
			break;

		default:
			dataValue = data;
			break;

		}

		return dataValue;

	}

	/**
	 * @purpose generate random numeric/alpha/alph-numeric string of specified
	 *          length
	 * @param stringType
	 * @param length
	 * @return
	 */
	public static String generateString(String stringType, int length) {
		StringBuilder result = new StringBuilder();
		for (int count = 0; count < length; count++) {
			Random rand = new Random();
			result.append(stringType.charAt(rand.nextInt(stringType.length())));
		}
		return result.toString();
	}

	public static void launchMobileWeb(String objectType, String objectName, String data) {
		try {
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("platformName", DriverScript.settings.get("PLATFORM_NAME"));
			capabilities.setCapability("deviceName", DriverScript.settings.get("DEVICE_NAME"));
			// capabilities.setCapability("no", true);
			capabilities.setCapability("newCommandTimeout", DriverScript.settings.get("MOBILE_WEB_ELEMENT_WAIT"));
			// capabilities.setCapability("noReset", true);
			capabilities.setCapability(CapabilityType.BROWSER_NAME, DriverScript.settings.get("MOBILE_BROWSER_NAME"));
			capabilities.setCapability(CapabilityType.VERSION, DriverScript.settings.get("VERSION"));

			// Log.info("Launching Mobile App...");
			driver = new RemoteWebDriver(new URL(DriverScript.settings.get("HUB_WEB_URL")), capabilities);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method launchMobileWeb | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method launchMobileWeb | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method launchMobileWeb | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	/**
	 * @author Pramod.Patil
	 * @param testCase
	 * @param result
	 * @Purpose: To capture screenshot
	 * 
	 */
	// public static void captureScreenshot(String testCase, String result) {
	// //String reportScreenshotPath=projectName.trim()+"/"+testCase;
	// String filename = "";
	// File scrFile;
	// try {
	//
	// filename = getCurrentTime("ddMMMyyyyHHmmss");
	// scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
	// FileUtils.copyFile(scrFile, new File(Constants.SCREENSHOTDESCDIR + "/" +
	// filename + "_" + result + ".png"));
	// Log.info("Verify:" + "<a href=" + Constants.SCREENSHOTDESCDIR + "/" +
	// filename + "_" + result + ".png"
	// + ">Screenshot</a>");
	//
	// if (result.trim().equalsIgnoreCase("pass") ||
	// result.trim().equalsIgnoreCase("p")) {
	// DriverScript.test.log(LogStatus.PASS,
	// DriverScript.test.addScreenCapture(testCase + "/" + filename + "_" +
	// result + ".png"));
	// } else {
	// DriverScript.test.log(LogStatus.FAIL,
	// DriverScript.test.addScreenCapture(testCase + "/" + filename + "_" +
	// result + ".png"));
	// }
	//
	// DriverScript.testStepsResult = true;
	// } catch (Exception e) {
	// Log.error("Class ActionKeywords | Method captureScreenshot | Exception
	// desc : " + e.getMessage());
	// DriverScript.testStepsResult = false;
	// }
	// }

	/**
	 * @author Pramod.Patil
	 * @param testCase
	 * @param result
	 * @Purpose: To capture screenshot
	 * 
	 */
	public static void captureScreenshot(String projectName, String testCase, String result) {

		if (driver != null) {
			String reportScreenshotPath = "";
			if (projectName.trim().isEmpty()) {
				reportScreenshotPath = testCase.trim();
			} else {
				reportScreenshotPath = projectName.trim() + "/" + testCase.trim();
			}
			String filename = "";
			File scrFile;
			try {

				filename = getCurrentTime("ddMMMyyyyHHmmss");
				scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(scrFile,
						new File(Constants.SCREENSHOTDESCDIR + "/" + filename + "_" + result + ".png"));
				Log.info("Verify:" + "<a href=" + Constants.SCREENSHOTDESCDIR + "/" + filename + "_" + result + ".png"
						+ ">Screenshot</a>");

				if (result.trim().equalsIgnoreCase("pass") || result.trim().equalsIgnoreCase("p")) {
					DriverScript.test.log(LogStatus.INFO, DriverScript.test
							.addScreenCapture(reportScreenshotPath + "/" + filename + "_" + result + ".png"));
				} else {
					DriverScript.test.log(LogStatus.INFO, DriverScript.test
							.addScreenCapture(reportScreenshotPath + "/" + filename + "_" + result + ".png"));
					Constants.BUG_SCREENSHOTPATH = reportScreenshotPath + "/" + filename + "_" + result + ".png";
					Constants.BUG_SCREENSHOTPATH = new File(Constants.BUG_SCREENSHOTPATH).getAbsolutePath();
				}

				DriverScript.testStepsResult = true;
			} catch (Exception e) {
				Log.error(
						"Class ActionKeywords | Method captureScreenshot | Exception desc : Unable To Capture Screenshot due to error="
								+ e.getMessage());

				// DriverScript.test.log(LogStatus.WARNING,
				// "Class ActionKeywords | Method captureScreenshot | Exception
				// desc : Unable To Capture Screenshot.");
				//
				// DriverScript.testStepList.add(
				// "Class ActionKeywords | Method captureScreenshot | Exception
				// desc : Unable To Capture Screenshot.");
				// DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

				DriverScript.testStepsResult = true;
			}
		}
	}

	/**
	 * @author Pramod.Patil
	 * @param testCase
	 * @param result
	 * @Purpose: To capture screenshot
	 * 
	 */
	public static void takeScreenshot(String projectName, String testCase, String result) {

		if (driver != null) {
			String reportScreenshotPath = "";
			if (projectName.trim().isEmpty()) {
				reportScreenshotPath = testCase.trim();
			} else {
				reportScreenshotPath = projectName.trim() + "/" + testCase.trim();
			}

			String filename = "";
			File scrFile;
			try {

				filename = getCurrentTime("ddMMMyyyyHHmmss");
				scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(scrFile,
						new File(Constants.SCREENSHOTDESCDIR + "/" + filename + "_" + result + ".png"));
				Log.info("Verify:" + "<a href=" + Constants.SCREENSHOTDESCDIR + "/" + filename + "_" + result + ".png"
						+ ">Screenshot</a>");

				DriverScript.test.log(LogStatus.INFO, DriverScript.test
						.addScreenCapture(reportScreenshotPath + "/" + filename + "_" + result + ".png"));

				DriverScript.testStepsResult = true;
			} catch (Exception e) {
				Log.error(
						"Class ActionKeywords | Method takeScreenshot | Exception desc : Unable To Capture Screenshot due to error="
								+ e.getMessage());

				DriverScript.test.log(LogStatus.WARNING,
						"Class ActionKeywords | Method takeScreenshot | Exception desc : Unable To Capture Screenshot.");

				DriverScript.testStepList.add(
						"Class ActionKeywords | Method takeScreenshot | Exception desc : Unable To Capture Screenshot.");
				DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

				DriverScript.testStepsResult = true;
			}
		}
	}

	/**
	 * @purpose Get current system time
	 * @return Current Time in specified formate
	 */
	public static String getCurrentTime(String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		Date date = new Date();
		return dateFormat.format(date);
	}

	/**
	 * @purpose Get current system date
	 * @return Current Date in specified formate
	 */
	public static String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("ddMMMyyyy");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static String getCurrentDateMMDDYYYY() {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date date = new Date();
		return dateFormat.format(date);
	}

	// Check folder if not exist then create new folder
	public static void checkFolder(String directoryName) {
		// Log.info("Checking directory.." + directoryName);
		File theDir = new File(directoryName);
		if (theDir.exists()) {
			theDir.delete();
		}
		// Log.info("Creating new directory: " + directoryName);
		theDir.mkdir();
	}

	public static void startADBDevice(String a, String b, String c) {
		try {
			ProcessBuilder pb = new ProcessBuilder(DriverScript.settings.get("EMULATOR_PATH") + "//adb.exe");
			Process pc;
			pc = pb.start();
			pc.waitFor();
			Log.info("ADB Emulator Launched.");
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method startADBDevice | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method startADBDevice | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method startADBDevice | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void backupLogs(String buildName) {
		File logFile = new File("logfile.log");
		// File reportExcelFile = new
		// File(DriverScript.settings.get("Report_Excel_Path"));
		// File outputFile = new
		// File(DriverScript.settings.get("RuntimeOutputfile"));
		// File dataEngineExcel = new
		// File(DriverScript.settings.get("Path_TestData"));
		// File testDataExcel = new
		// File(DriverScript.settings.get("Test_Data_Excel_Path"));
		// File settingproperty = new
		// File(DriverScript.settings.get("Settings_Property"));
		// File bugExcel = new
		// File(DriverScript.settings.get("Bug_Excel_Path"));

		// To create Logs Folder
		checkFolder(Constants.BUILD_PATH + "\\Logs");

		// Copy all files
		try {
			FileUtils.copyFile(logFile, new File(Constants.BUILD_PATH + "\\Logs\\" + buildName + ".log"));
			// FileUtils.copyFile(reportExcelFile,
			// new File(Constants.BUILD_PATH + "\\" +
			// DriverScript.settings.get("Report_Excel_Path")));
			// FileUtils.copyFile(outputFile,
			// new File(Constants.BUILD_PATH + "\\" +
			// DriverScript.settings.get("RuntimeOutputfile")));
			// FileUtils.copyFile(dataEngineExcel,
			// new File(Constants.BUILD_PATH + "\\" +
			// DriverScript.settings.get("Path_TestData")));

			// FileUtils.copyFile(testDataExcel,
			// new File(Constants.BUILD_PATH + "\\" +
			// DriverScript.settings.get("Test_Data_Excel_Path")));

			// FileUtils.copyFile(settingproperty,
			// new File(Constants.BUILD_PATH + "\\" +
			// DriverScript.settings.get("Settings_Property")));
			//
			// FileUtils.copyFile(bugExcel,
			// new File(Constants.BUILD_PATH + "\\" +
			// DriverScript.settings.get("Bug_Excel_Path")));

		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method backupLogs | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method backupLogs | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method backupLogs | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void GetAndVerifyText(String objectType, String objectName, String data) {
		try {
			String getValue = findElement(objectType, objectName).getText();
			DriverScript.testStepsResult = true;
			Assert.assertTrue(getValue.trim().equals(data.trim()),
					"Expected Text=" + data + " not found, Instead Actual Text Found =" + getValue);
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method GetAndVerifyText | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method GetAndVerifyText | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method GetAndVerifyText | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;

		}
	}

	public static void GetAndVerifyPartialText(String objectType, String objectName, String data) {
		try {
			String getValue = findElement(objectType, objectName).getText();
			DriverScript.testStepsResult = true;
			Log.info("Read=" + getValue);
			Log.info("data=" + data);

			Assert.assertTrue(getValue.trim().contains(data.trim()),
					"Expected Text=" + data + " not found, Instead Actual Text Found =" + getValue);
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method GetAndVerifyText | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method GetAndVerifyText | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method GetAndVerifyText | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;

		}
	}

	public static void getVerifyAndContinueTextValidation(String objectType, String objectName, String data) {
		try {
			if (isElementVisible(objectType, objectName, data)) {
				String getValue;
				scrollToElement(objectType, objectName, data);
				getValue = getText(objectType, objectName, data).trim();
				if (getValue.trim().equals(data.trim())) {
					Assert.assertTrue(getValue.trim().equals(data.trim()),
							"Expected Text=" + data + " not found, Instead Actual Text Found =" + getValue);
					DriverScript.testStepsResult = true;
				} else {

					Log.warn("Validation Faild: Expected Text=" + data + " not found, Instead Actual Text Found ="
							+ getValue);
					DriverScript.test.log(LogStatus.WARNING, "Validation Faild: Expected Text=" + data
							+ " not found, Instead Actual Text Found =" + getValue);
					DriverScript.testStepsWarningResult = true;
					DriverScript.testStepsResult = false;
				}
			} else {
				Log.warn("Validation Faild: Expected Text=" + data + " not found.");
				DriverScript.test.log(LogStatus.WARNING, "Validation Faild: Expected Text=" + data + " not found.");
				DriverScript.testStepsWarningResult = true;
				DriverScript.testStepsResult = false;
			}

		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method getVerifyAndContinueTextValidation | Exception desc : "
					+ e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method getVerifyAndContinueTextValidation | Exception desc : "
							+ e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method getVerifyAndContinueTextValidation | Exception desc : "
							+ e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;

		}
	}

	public static void getVerifyAndContinuePartialTextValidation(String objectType, String objectName, String data) {
		try {
			checkElementVisible(objectType, objectName, data);
			String getValue;
			getValue = getText(objectType, objectName, data).trim();
			if (getValue.trim().contains(data.trim()) || data.trim().contains(getValue.trim())) {
				Assert.assertTrue(getValue.trim().contains(data.trim()),
						"Expected Text=" + data + " not found, Instead Actual Text Found =" + getValue);
				DriverScript.testStepsResult = true;
			} else {
				Log.info("Validation Faild: Expected Text=" + data + " not found, Instead Actual Text Found ="
						+ getValue);
				DriverScript.test.log(LogStatus.WARNING, "Validation Faild: Expected Text=" + data
						+ " not found, Instead Actual Text Found =" + getValue);
				DriverScript.testStepsWarningResult = true;
				DriverScript.testStepsResult = false;
			}

		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method getVerifyAndContinueTextValidation | Exception desc : "
					+ e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method getVerifyAndContinueTextValidation | Exception desc : "
							+ e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method getVerifyAndContinueTextValidation | Exception desc : "
							+ e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;

		}
	}

	public static void checkElementVisible(String objectType, String objectName, String data) {
		try {
			Assert.assertTrue(isElementVisible(objectType, objectName, data));
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method checkElementVisible | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method checkElementVisible | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method checkElementVisible | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void checkTextVisible(String objectType, String objectName, String data) {
		try {
			if (objectName.contains("temp")) {
				objectName = objectName.replaceAll("temp", data);
			}
			DriverScript.testStepsResult = true;
			Assert.assertTrue(isElementVisible(objectType, objectName, data));

		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method checkElementVisible | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method checkElementVisible | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method checkElementVisible | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static boolean isElementVisible(String objectType, String objectName, String data) {
		try {
			// Log.info("Waiting for element to be visible=" + objectName);
			// driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			WebDriverWait wait = new WebDriverWait(driver,
					Integer.parseInt(DriverScript.settings.get("WAITFORELEMENT_WAIT").trim()));
			wait.until(ExpectedConditions.presenceOfElementLocated(findBy(objectType, objectName)));
			// Log.info("Element " + objectName + " visible on the Screen.");
			DriverScript.testStepsResult = true;
			return true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method isElementVisible | Exception desc : " + e.getMessage());

			// DriverScript.test.log(LogStatus.FAIL,
			// "Class ActionKeywords | Method isElementVisible | Exception desc
			// : " + e.getMessage());
			// DriverScript.testStepList
			// .add("Class ActionKeywords | Method isElementVisible | Exception
			// desc : " + e.getMessage());
			// DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = true;
			return false;
		}
	}

	public static boolean elementFound(String objectType, String objectName, String data) {
		try {
			// Log.info("Waiting for element to be visible=" + objectName);
			// driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			WebDriverWait wait = new WebDriverWait(driver,
					Integer.parseInt(DriverScript.settings.get("ZERO_WAIT").trim()));
			wait.until(ExpectedConditions.visibilityOfElementLocated(findBy(objectType, objectName)));
			// Log.info("Element " + objectName + " visible on the Screen.");
			DriverScript.testStepsResult = true;
			return true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method isElementVisible | Exception desc : " + e.getMessage());

			// DriverScript.test.log(LogStatus.FAIL,
			// "Class ActionKeywords | Method isElementVisible | Exception desc
			// : " + e.getMessage());
			// DriverScript.testStepList
			// .add("Class ActionKeywords | Method isElementVisible | Exception
			// desc : " + e.getMessage());
			// DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = true;
			return false;
		}
	}

	public static void checkElementDisable(String objectType, String objectName, String data) {
		try {
			Assert.assertTrue(!isElementClickable(objectType, objectName, data));
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method checkElementDisable | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method checkElementDisable | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method checkElementDisable | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void verifyElementEnable(String objectType, String objectName, String data) {
		try {
			Boolean enableFlag = isElementClickable(objectType, objectName, data);
			if (enableFlag) {
				Assert.assertTrue(enableFlag,
						"Validation Failed:Expected Element is Enabled, Actual found Element is not Enabled.");
				DriverScript.testStepsResult = true;
			} else {
				Log.warn("Validation Failed:Expected Element is Enabled, Actual found Element is not Enabled.");
				DriverScript.test.log(LogStatus.WARNING,
						"Validation Failed:Expected Element is Enabled, Actual found Element is not Enabled.");
				DriverScript.testStepsWarningResult = true;
				DriverScript.testStepsResult = false;
			}

		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method verifyElementEnable | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method verifyElementEnable | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method verifyElementEnable | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());
			DriverScript.testStepsResult = false;
		}
	}

	public static void verifyElementDisable(String objectType, String objectName, String data) {

		try {
			Boolean disableFlag = isElementClickable(objectType, objectName, data);
			if (disableFlag) {
				Log.warn("Validation Failed:Expected Element is Disabled, Actual found Element is not Disabled.");
				DriverScript.test.log(LogStatus.WARNING,
						"Validation Failed:Expected Element is Disabled, Actual found Element is not Disabled.");
				DriverScript.testStepsWarningResult = true;
				DriverScript.testStepsResult = false;
			} else {
				Assert.assertTrue(!disableFlag,
						"Validation Failed:Expected Element is Disabled, Actual found Element is not Disabled.");
				DriverScript.testStepsResult = true;
			}

		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method verifyElementDisable | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method verifyElementDisable | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method verifyElementDisable | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());
			DriverScript.testStepsResult = false;
		}

	}

	public static void checkElementEnable(String objectType, String objectName, String data) {
		try {
			// Assert.assertTrue(isElementClickable(objectType, objectName,
			// data));
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method checkElementEnable | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method checkElementEnable | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method checkElementEnable | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static boolean isElementClickable(String objectType, String objectName, String data) {
		try {
			// Log.info("Waiting for element to be clickable=" + objectName);
			// driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			WebDriverWait wait = new WebDriverWait(driver,
					Integer.parseInt(DriverScript.settings.get("WAITFORELEMENT_WAIT").trim()));
			wait.until(ExpectedConditions.elementToBeClickable(findBy(objectType, objectName)));
			// Log.info("Element " + objectName + " is clickable.");
			// DriverScript.testStepsResult = true;
			return true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method isElementClickable | Exception desc : " + e.getMessage());

			// DriverScript.test.log(LogStatus.FAIL,
			// "Class ActionKeywords | Method isElementClickable | Exception
			// desc : " + e.getMessage());
			// DriverScript.testStepList
			// .add("Class ActionKeywords | Method isElementClickable |
			// Exception desc : " + e.getMessage());
			// DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			// DriverScript.testStepsResult = false;
			return false;
		}

	}

	public static boolean isElementPresent(String objectType, String objectName, String data) {
		try {
			// Log.info("Waiting for element to be present=" + objectName);
			// driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			WebDriverWait wait = new WebDriverWait(driver,
					Integer.parseInt(DriverScript.settings.get("WAITFORELEMENT_WAIT").trim()));
			wait.until(ExpectedConditions.presenceOfElementLocated(findBy(objectType, objectName)));
			// Log.info("Element " + objectName + " is Present.");
			DriverScript.testStepsResult = true;
			return true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method isElementPresent | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method isElementPresent | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method isElementPresent | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
			return false;
		}

	}

	public static void selectFromDropdown(String objectType, String objectName, String data) {
		DriverScript.testStepsResult = false;
		if (data.trim().equals("")) {
			findElement(objectType, objectName).click();
			DriverScript.testStepsResult = true;
		} else {
			try {
				DriverScript.testStepsResult = false;
				// Log.info("Select By Visible Text" + objectType);
				findElement(objectType, objectName).click();
				new Select(findElement(objectType, objectName)).selectByVisibleText(data);

				DriverScript.testStepsResult = true;
			} catch (Exception etext) {
				DriverScript.testStepsResult = false;
				try {
					// Log.info("Select By Value" + objectType);
					findElement(objectType, objectName).click();
					new Select(findElement(objectType, objectName)).selectByValue(data);
					DriverScript.testStepsResult = true;
				} catch (Exception eval) {
					try {
						DriverScript.testStepsResult = false;
						// Log.info("Select By Index" + objectType);
						findElement(objectType, objectName).click();
						new Select(findElement(objectType, objectName)).selectByIndex(Integer.parseInt(data));
						DriverScript.testStepsResult = true;
					} catch (Exception eindex) {
						Log.error("Class ActionKeywords | Method selectFromDropdown | Exception desc : "
								+ eindex.getMessage());
						DriverScript.testStepsResult = false;
					}
				}
			}
		}
	}

	public static void selectValueFromDropdown(String objectType, String objectName, String data) {
		DriverScript.testStepsResult = false;
		if (data.trim().equals("")) {
			findElement(objectType, objectName).click();
			DriverScript.testStepsResult = true;
		} else {
			try {
				DriverScript.testStepsResult = false;
				// Log.info("Select By Visible Text" + objectType);
				findElement(objectType, objectName).click();
				new Select(findElement(objectType, objectName)).selectByValue(data);

				DriverScript.testStepsResult = true;
			} catch (Exception etext) {
				DriverScript.testStepsResult = false;
				try {
					// Log.info("Select By Value" + objectType);
					findElement(objectType, objectName).click();
					new Select(findElement(objectType, objectName)).selectByVisibleText(data);
					DriverScript.testStepsResult = true;
				} catch (Exception eval) {
					try {
						DriverScript.testStepsResult = false;
						// Log.info("Select By Index" + objectType);
						findElement(objectType, objectName).click();
						new Select(findElement(objectType, objectName)).selectByIndex(Integer.parseInt(data));
						DriverScript.testStepsResult = true;
					} catch (Exception eindex) {
						Log.error("Class ActionKeywords | Method selectFromDropdown | Exception desc : "
								+ eindex.getMessage());
						DriverScript.testStepsResult = false;
					}
				}
			}
		}
	}

	/**
	 * This method handles div dropdown
	 * 
	 * @param objectType
	 * @param objectName
	 * @param data
	 *            - value to be selected from the list of options
	 */
	public static void selectFromList(String objectType, String objectName, String data) {
		boolean foundFlag = false;
		try {
			// Log.info("Select By Visible Text" + objectType);
			List<WebElement> listValues = findElements(objectType, objectName);

			for (WebElement ele : listValues) {
				if (ele.getText().trim().equals(data.trim())) {

					if (driver instanceof JavascriptExecutor) {
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", ele);
					}
					waitForSeconds(objectType, objectName, "4");
					ele.click();
					foundFlag = true;
					break;
				}

			}

			if (foundFlag) {
				DriverScript.testStepsResult = true;
			} else {
				DriverScript.testStepsResult = false;
			}
		} catch (Exception etext) {
			Log.error("Class ActionKeywords | Method selectFromList | Exception desc : " + etext.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method selectFromList | Exception desc : " + etext.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method selectFromList | Exception desc : " + etext.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void selectProject(String objectType, String objectName, String data) {
		boolean foundFlag = false;
		try {
			// Log.info("Select By Visible Text" + objectType);
			List<WebElement> listValues = findElements(objectType, objectName);

			for (WebElement ele : listValues) {
				if (ele.getText().trim().equals(data.trim())) {

					if (driver instanceof JavascriptExecutor) {
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", ele);
					}
					waitForSeconds(objectType, objectName, "4");
					ele.click();
					foundFlag = true;
					break;
				}

			}

			if (foundFlag) {
				DriverScript.testStepsResult = true;
			} else {
				DriverScript.testStepsResult = false;
			}
		} catch (Exception etext) {
			Log.error("Class ActionKeywords | Method selectFromList | Exception desc : " + etext.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method selectFromList | Exception desc : " + etext.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method selectFromList | Exception desc : " + etext.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}

	}

	public static void actionClickPerform(String objectType, String objectName, String data) {
		try {
			WebElement element = findElement(objectType, objectName);
			Actions action = new Actions(driver);
			action.moveToElement(element).click().build().perform();
			waitForPageLoad(driver);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method actionClickPerform | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method actionClickPerform | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method actionClickPerform | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	// Method added by Swapnil wandhe
	public static void actionDoubleClickPerform(String objectType, String objectName, String data) {
		try {
			WebElement element = findElement(objectType, objectName);
			Actions action = new Actions(driver);
			action.moveToElement(element).doubleClick().build().perform();
			waitForPageLoad(driver);
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method actionClickPerform | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method actionClickPerform | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method actionClickPerform | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public void dragAndDrop(WebElement sourceElement, WebElement destinationElement) {
		try {
			if (sourceElement.isDisplayed() && destinationElement.isDisplayed()) {
				Actions action = new Actions(driver);
				action.dragAndDrop(sourceElement, destinationElement).build().perform();
				DriverScript.testStepsResult = true;
			} else {
				Log.error(
						"Class ActionKeywords | Method dragAndDrop | Exception desc : Element was not displayed to drag");

				DriverScript.test.log(LogStatus.FAIL,
						"Class ActionKeywords | Method dragAndDrop | Exception desc : Element was not displayed to drag");
				DriverScript.testStepList.add(
						"Class ActionKeywords | Method dragAndDrop | Exception desc : Element was not displayed to drag");
				DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

				DriverScript.testStepsResult = false;
			}
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method dragAndDrop | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method dragAndDrop | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method dragAndDrop | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static HashMap<String, String> readProperty(String filename) {
		// Log.info("Reading propertis file.." + filename);
		HashMap<String, String> config = new HashMap<>();
		try {
			File file = new File(filename);
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();
			Enumeration<Object> enuKeys = properties.keys();
			while (enuKeys.hasMoreElements()) {
				String key = (String) enuKeys.nextElement();
				String value = properties.getProperty(key);
				config.put(key, value);
			}
		} catch (IOException e) {
			Log.error("Class ActionKeywords | Method readProperty | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method readProperty | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method readProperty | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
		return config;
	}

	/**
	 * @author Pramod.Patil
	 * @purpose To Update the property file
	 */
	public static void UpdatePropertyFile(String filepath, HashMap<String, String> table) {
		try {
			FileInputStream in = new FileInputStream(filepath);
			Properties props = new Properties();
			props.load(in);
			in.close();
			FileOutputStream out = new FileOutputStream(filepath);
			Set<String> keys = table.keySet();
			for (String key : keys) {
				props.setProperty(key, table.get(key));
			}
			props.store(out, null);
			out.close();
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method UpdatePropertyFile | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method UpdatePropertyFile | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method UpdatePropertyFile | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void setValueFromFile(String objectType, String objectName, String data) {
		try {
			HashMap<String, String> dataFromDile = new HashMap<String, String>();
			dataFromDile = readProperty(DriverScript.settings.get("RuntimeOutputfile").trim());
			enterValue(objectType, objectName, dataFromDile.get(data));
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method setValueFromFile | Exception desc : " + e.getMessage());

			// DriverScript.test.log(LogStatus.FAIL,
			// "Class ActionKeywords | Method setValueFromFile | Exception desc
			// : " + e.getMessage());
			// DriverScript.testStepList
			// .add("Class ActionKeywords | Method setValueFromFile | Exception
			// desc : " + e.getMessage());
			// DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static String getIdFromPopupMessage(String objectType, String objectName, String data) {
		try {
			// String str="Question Q472 is saved successfully to library";
			// String str="Template T472 saved successfully to library";
			// String str="Review Id: 2335 with party number: 50008171";

			HashMap<String, String> dataFromDile = new HashMap<String, String>();
			dataFromDile = readProperty(DriverScript.settings.get("RuntimeOutputfile").trim());
			String dataValue = dataFromDile.get(data);
			String Id;
			if (dataValue.contains("is") && dataValue.contains("saved")) {
				Id = dataValue.substring(dataValue.indexOf(" "), dataValue.lastIndexOf("is")).trim();
			} else if (dataValue.contains("saved")) {
				Id = dataValue.substring(dataValue.indexOf(" "), dataValue.lastIndexOf("saved")).trim();
			} else if (dataValue.contains("Id:")) {
				Id = dataValue.substring((dataValue.indexOf(":") + 1), dataValue.indexOf("with")).trim();
			} else {
				Log.error("Class ActionKeywords | Method setValueFromFile | Exception desc : Id not found.");
				Id = "";
			}
			Log.info("Generated Id=" + Id);
			DriverScript.test.log(LogStatus.INFO, "Id=" + Id);
			HashMap<String, String> outputData = new HashMap<String, String>();
			outputData.put(data, Id);
			ActionKeywords.UpdatePropertyFile(DriverScript.settings.get("RuntimeOutputfile").trim(), outputData);

			DriverScript.testStepsResult = true;
			return Id;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method getIdFromPopupMessage | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method getIdFromPopupMessage | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method getIdFromPopupMessage | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
			return null;
		}
	}

	// public static void storeOutputInExcel(String objectType, String
	// objectName, String data){
	// HashMap<String, String> dataFromDile = new HashMap<String, String>();
	// dataFromDile =
	// readProperty(DriverScript.settings.get("RuntimeOutputfile").trim());
	// String dataValue = dataFromDile.get(data);
	// int outputCol=ExcelUtils.getColumnCount(objectType,objectName);
	// ExcelUtils.setCellDataValue(dataValue, RowNum, outputCol,
	// objectType,objectName);
	//
	// d
	// }
	public static String retriveValueFromOutputString(int startPosition, int endPosition, String data) {

		try {

			// String Outputdata="Question Q472 is saved successfully to
			// library";
			// String Outputdata="Template T472 saved successfully to library";
			// String Outputdata="Review Id: 2335 with party number: 50008171";

			HashMap<String, String> dataFromDile = new HashMap<String, String>();
			dataFromDile = readProperty(DriverScript.settings.get("RuntimeOutputfile").trim());
			String dataValue = dataFromDile.get(data);
			String Id;
			Id = dataValue.substring(startPosition, endPosition).trim();
			Log.info("Generated Id=" + Id);
			DriverScript.test.log(LogStatus.INFO, "Id=" + Id);
			HashMap<String, String> outputData = new HashMap<String, String>();
			outputData.put(data, Id);
			ActionKeywords.UpdatePropertyFile(DriverScript.settings.get("RuntimeOutputfile").trim(), outputData);

			DriverScript.testStepsResult = true;
			return Id;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method getIdFromPopupMessage | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method getIdFromPopupMessage | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method getIdFromPopupMessage | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
			return null;
		}

	}

	public static void selectRadioOption(String objectType, String objectName, String data) {
		try {

			if (!(findElement(objectType, objectName).isSelected())) {
				click(objectType, objectName, data);
			}
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method selectRadioOption | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method selectRadioOption | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method selectRadioOption | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;

		}

	}

	public static void deSelectRadioOption(String objectType, String objectName, String data) {
		try {
			if (objectName.contains("temp")) {
				objectName = objectName.replaceAll("temp", data);
			}

			if (objectType.trim().equalsIgnoreCase("xpath") && !data.trim().equalsIgnoreCase("")) {
				objectName = objectName + "[@value='" + data + "']";
			}

			if (findElement(objectType, objectName).isSelected()) {
				click(objectType, objectName, data);
			}
			DriverScript.testStepsResult = true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method deSelectRadioOption | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method deSelectRadioOption | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method deSelectRadioOption | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;

		}
	}

	final public static boolean waitForElementInvisible(String objectType, String objectName, String data) {
		try {
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);

			WebDriverWait wait = new WebDriverWait(driver,
					Integer.parseInt(DriverScript.settings.get("IMPLICITE_WAIT").trim()));

			boolean present = wait.ignoring(StaleElementReferenceException.class).ignoring(NoSuchElementException.class)
					.until(ExpectedConditions.invisibilityOfElementLocated(findBy(objectType, objectName)));
			DriverScript.testStepsResult = true;
			return present;
		} catch (Exception e) {
			DriverScript.testStepsResult = true;
			return false;
		} finally {
			DriverScript.testStepsResult = true;
			driver.manage().timeouts().implicitlyWait(
					Integer.parseInt(DriverScript.settings.get("IMPLICITE_WAIT").trim()), TimeUnit.SECONDS);
		}
	}

	public static void getAndCompaireAttributeLength(String objectType, String objectName, String data) {
		try {
			// Log.info("Read text " + objectType);
			waitForElement(objectType, objectName, data);
			scrollToElement(objectType, objectName, data);
			String readValue = findElement(objectType, objectName).getAttribute("value");
			Log.info("Read Value= " + readValue);
			DriverScript.test.log(LogStatus.INFO, "ReadValue=" + readValue);

			// HashMap<String, String> outputData = new HashMap<String,
			// String>();
			// outputData.put(data, readValue);
			// ActionKeywords.UpdatePropertyFile(DriverScript.settings.get("RuntimeOutputfile").trim(),
			// outputData);

			int strLength = readValue.trim().length();
			Log.info("Read Length= " + strLength);
			DriverScript.test.log(LogStatus.INFO, "Read Length= " + strLength);

			// Assert.assertTrue((strLength==data.trim().length()), "Expected
			// Length=" + data + " not found, Instead Actual Length=" +
			// strLength + " Found.");

			Assert.assertTrue(String.valueOf(strLength).equals(data.trim()),
					"Expected Value=" + data + " not found, Instead Actual Value=" + readValue + " Found.");

			DriverScript.testStepsResult = true;

		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method getAttributeValue | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method getAttributeValue | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method getAttributeValue | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;

		}

	}

	public static void getVerifyAndContinueTextPresentInList(String objectType, String objectName, String data) {
		try {

			data = randomValues(data.trim());

			if (isElementVisible(objectType, objectName, data)) {
				String getValue;
				scrollToElement(objectType, objectName, data);

				boolean findFlag = false;

				List<WebElement> listElements = findElements(objectType, objectName);
				for (WebElement ele : listElements) {
					getValue = ele.getText().trim();
					if (getValue.trim().equals(data.trim())) {
						findFlag = true;
						break;
					}
				}

				if (findFlag == true) {
					Assert.assertTrue(findFlag, "Expected Text=" + data + " not found in the list");
					DriverScript.testStepsResult = true;
				} else {

					Log.warn("Validation Faild: Expected Text=" + data + " not found found in the list.");
					DriverScript.test.log(LogStatus.WARNING,
							"Validation Faild: Expected Text=" + data + " not found in the list");
					DriverScript.testStepsWarningResult = true;
					DriverScript.testStepsResult = false;
				}
			} else {
				Log.warn("Validation Faild: Expected Text=" + data + " not found.");
				DriverScript.test.log(LogStatus.WARNING, "Validation Faild: Expected Text=" + data + " not found.");
				DriverScript.testStepsWarningResult = true;
				DriverScript.testStepsResult = false;
			}

		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method getVerifyAndContinueTextPresentInList | Exception desc : "
					+ e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method getVerifyAndContinueTextPresentInList | Exception desc : "
							+ e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method getVerifyAndContinueTextPresentInList | Exception desc : "
							+ e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;

		}
	}

	public static void getVerifyAndContinueTextNotPresentInList(String objectType, String objectName, String data) {
		try {
			if (isElementVisible(objectType, objectName, data)) {
				String getValue;
				scrollToElement(objectType, objectName, data);

				boolean findFlag = false;

				List<WebElement> listElements = findElements(objectType, objectName);
				for (WebElement ele : listElements) {
					getValue = ele.getText().trim();
					if (getValue.trim().equals(data.trim())) {
						findFlag = true;
						break;
					}
				}

				if (findFlag == false) {
					DriverScript.testStepsResult = true;
				} else {

					Log.warn("Validation Faild: Expected Text=" + data + " found found in the list.");
					DriverScript.test.log(LogStatus.WARNING,
							"Validation Faild: Expected Text=" + data + " found in the list");
					DriverScript.testStepsWarningResult = true;
					DriverScript.testStepsResult = false;
				}
			} else {
				Log.info("Element not found.");
				DriverScript.test.log(LogStatus.INFO, "Element not found.");
				// DriverScript.testStepsWarningResult = true;
				// DriverScript.testStepsResult = false;
				DriverScript.testStepsResult = true;
			}

		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method getVerifyAndContinueTextPresentInList | Exception desc : "
					+ e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method getVerifyAndContinueTextPresentInList | Exception desc : "
							+ e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method getVerifyAndContinueTextPresentInList | Exception desc : "
							+ e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;

		}
	}

	public static void clickEditInList(String objectType, String objectName, String data) {
		try {
			objectName = objectName.replaceAll("Row_Num", String.valueOf(Constants.EditListindex));
			findElement(objectType, objectName).click();
			waitForPageLoad(driver);
			DriverScript.testStepsResult = true;

		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method clickEditInList | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method clickEditInList | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method clickEditInList | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;

		}
	}

	public static void clickIfExist(String objectType, String objectName, String data) {
		try {
			if (elementFound(objectType, objectName, data)) {
				// Log.info("Clicking on Webelement " + objectType);
				if (objectType.equalsIgnoreCase("linktext")) {
					objectName = data;
				}

				waitForElement(objectType, objectName, data);
				waitForElementToBeClickable(objectType, objectName, data);
				scrollToElement(objectType, objectName, data);
				findElement(objectType, objectName).click();

				waitForPageLoad(driver);
				DriverScript.testStepsResult = true;
			} else {
				DriverScript.testStepsResult = true;
			}

		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method click | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method click | Exception desc : " + e.getMessage());
			DriverScript.testStepList.add("Class ActionKeywords | Method click | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;
		}
	}

	public static void getVerifyTextInEditList(String objectType, String objectName, String data) {
		try {
			if (isElementVisible(objectType, objectName, data)) {
				String getValue;
				scrollToElement(objectType, objectName, data);

				boolean findFlag = false;

				List<WebElement> listElements = findElements(objectType, objectName);
				int i = 1;
				for (WebElement ele : listElements) {
					getValue = ele.getText().trim();
					if (getValue.trim().equals(data.trim())) {
						findFlag = true;
						break;
					}
					i++;
				}

				Constants.EditListindex = i;

				if (findFlag == true) {
					Assert.assertTrue(findFlag, "Expected Text=" + data + " not found in the list");
					DriverScript.testStepsResult = true;
				} else {

					Log.warn("Expected Text=" + data + " not found in list.");
					DriverScript.test.log(LogStatus.WARNING, "Expected Text=" + data + " not found in list.");
					DriverScript.testStepsWarningResult = true;
					DriverScript.testStepsResult = false;
				}
			} else {
				Log.warn("Expected Text=" + data + " not found in list.");
				DriverScript.test.log(LogStatus.WARNING, "Expected Text=" + data + " not found in list");
				DriverScript.testStepsWarningResult = true;
				DriverScript.testStepsResult = false;
			}

		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method getVerifyAndClickEditInList | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method getVerifyAndClickEditInList | Exception desc : " + e.getMessage());
			DriverScript.testStepList.add(
					"Class ActionKeywords | Method getVerifyAndClickEditInList | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;

		}
	}

	public static void GridLoad(String objectType, String objectName, String data) {
		objectType = "xpath";
		objectName = "//label[contains(text(),'Please wait while system')]";
		data = "";
		try {
			if (isElementInVisible(objectType, objectName, data)) {
				DriverScript.testStepsResult = true;
			} else {
				GridLoad(objectType, objectName, data);
			}

		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method GridLoad | Exception desc : " + e.getMessage());

			DriverScript.test.log(LogStatus.FAIL,
					"Class ActionKeywords | Method GridLoad | Exception desc : " + e.getMessage());
			DriverScript.testStepList
					.add("Class ActionKeywords | Method GridLoad | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = false;

		}

	}

	public static boolean isElementInVisible(String objectType, String objectName, String data) {
		try {
			// Log.info("Waiting for element to be visible=" + objectName);
			// driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			WebDriverWait wait = new WebDriverWait(driver,
					Integer.parseInt(DriverScript.settings.get("FLUENT_WAIT").trim()));
			wait.until(ExpectedConditions.invisibilityOfElementLocated(findBy(objectType, objectName)));
			// Log.info("Element " + objectName + " visible on the Screen.");
			DriverScript.testStepsResult = true;
			return true;
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method isElementVisible | Exception desc : " + e.getMessage());

			// DriverScript.test.log(LogStatus.FAIL,
			// "Class ActionKeywords | Method isElementVisible | Exception desc
			// : " + e.getMessage());
			// DriverScript.testStepList
			// .add("Class ActionKeywords | Method isElementVisible | Exception
			// desc : " + e.getMessage());
			// DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			DriverScript.testStepsResult = true;
			return false;
		}
	}

	public static void copyReport() {
		// Copy all files
		try {
			Zip.copyFolder(new File(Constants.BUILD_PATH + "\\"), new File("Report"));
		} catch (Exception e) {
			Log.error("Class ActionKeywords | Method copyReport | Exception desc : " + e.getMessage());

			// DriverScript.test.log(LogStatus.FAIL,
			// "Class ActionKeywords | Method backupLogs | Exception desc : " +
			// e.getMessage());
			// DriverScript.testStepList
			// .add("Class ActionKeywords | Method backupLogs | Exception desc :
			// " + e.getMessage());
			// DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());
			//
			// DriverScript.testStepsResult = false;
		}
	}

}