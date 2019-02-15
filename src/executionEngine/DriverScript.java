package executionEngine;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.xml.DOMConfigurator;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import config.ActionKeywords;
import config.Constants;
import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIResults;
import utility.BugzillaIntegration;
import utility.ExcelUtils;
import utility.Log;
import utility.SentMail;
import utility.TestLinkIntegration;
import utility.Zip;

public class DriverScript {

	// Global variables
	public static int invocationColumns;
	public static boolean testCaseResult = false;
	public static boolean testStepsResult = false;
	public static boolean testStepsWarningResult = false;
	public static HashMap<String, String> settings = new HashMap<>();

	public static ExtentReports extent;
	public static ExtentTest test;

	public static List<String> testStepList = new ArrayList<String>();
	public static List<String> testStepResultList = new ArrayList<String>();
	public static HashMap<String, String> outputInExcel = new HashMap<>();

	private static ActionKeywords actionKeywords;
	private static Method method[];

	public static String testLinkDEVKEY;
	public static String testLinkURL;

	public DriverScript() {
		actionKeywords = new ActionKeywords();
		method = actionKeywords.getClass().getMethods();
	}

	/*
	 * pramod patil Purpose:- Main method to start the framework execution
	 */
	public static void main(String[] args) {

		JVMShutdownHookThread jvmShutdownHook = new JVMShutdownHookThread();
		Runtime.getRuntime().addShutdownHook(jvmShutdownHook);

		// try {
		// //ActionKeywords.getCurrentDateMMDDYYYY();
		//
		// DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		// formatter.setTimeZone(TimeZone.getTimeZone("US/Dallas")); // Or
		// whatever IST is supposed to be
		// String istdate= formatter.format(new Date());
		// System.out.println(istdate);
		//
		// System.exit(0);
		// } catch (Exception e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		// GraphicsConfiguration gc =
		// GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
		// .getDefaultConfiguration();
		//
		// ScreenRecorder screenRecorder = null;

		// read settings.property
		settings = ExcelUtils.readProperty("Resources\\Settings.properties");
		if (settings.isEmpty()) {
			System.out.println(
					"Exception Desc : Unable to Read Settings.properties file from location= '\\Resources\\Settings.properties");
		} else {

			// execution screen recording
			if (settings.get("RecordExecution").toString().trim().equalsIgnoreCase("yes")) {
				// File file = new File("D:\\Videos");
				//
				// Dimension screenSize =
				// Toolkit.getDefaultToolkit().getScreenSize();
				// int width = screenSize.width;
				// int height = screenSize.height;
				//
				// Rectangle captureSize = new Rectangle(0,0, width, height);

				// screenRecorder = new ScreenRecorder(gc, new
				// Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
				// new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey,
				// ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
				// CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
				// DepthKey, 24, FrameRateKey,
				// Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey,
				// 15 * 60),
				// new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey,
				// "black", FrameRateKey,
				// Rational.valueOf(30)),
				// null);
				//
				// screenRecorder.start();
			}

			if (ExcelUtils.fileExistAtLocation(settings.get("Path_Log4j").toString().trim())) {
				DOMConfigurator.configure(settings.get("Path_Log4j").toString().trim());
			} else {
				System.out.println("Exception Desc : Unable to find log4j.xml file at location="
						+ settings.get("Path_Log4j").toString().trim()
						+ " Hence no logs will get generated for this execution.");
			}

			Log.info("--------------- Starting Automation Test Execution -------------------");
			Log.info("Loaded Settings detail successfully.");
			Log.info("Loaded logs detail successfully.");

			// To Create Report Folder
			ActionKeywords.createReportFolder();
			Log.info("Report folder created successfully.Report Folder location=" + Constants.BUILD_PATH);

			try {
				// create Report Excel
				ExcelUtils.createExcelFile(settings.get("Report_Excel_Path").trim());
				Log.info("Created Report Excel successfully. Location=" + settings.get("Report_Excel_Path"));

				// Create Output property file
				ExcelUtils.createPropertyFile(settings.get("RuntimeOutputfile").trim());
				Log.info("Created Output file successfully. Location=" + settings.get("RuntimeOutputfile"));

				// new instance for creating hte Extent Report
				extent = new ExtentReports(Constants.BUILD_PATH + "\\index.html", false);

				// Credentials For Test Link Integration
				testLinkURL = settings.get("TestLinkURL").toString().trim();
				testLinkDEVKEY = settings.get("TestLinkDEVKEY").toString().trim();

				DriverScript startEngine;

				startEngine = new DriverScript();
				// Start testcase execution
				if (settings.get("Testlink_integration").trim().equalsIgnoreCase("Yes")) {
					// Test cases execution from Test Link
					startEngine.execute_TestCase_TestLink();
				} else {
					// Test Case Execution without testlink (From Only Excel)
					startEngine.execute_TestCase_Excel();
				}
				// ActionKeywords.closeCurrentBrowser("", "", "");
				// Close the Extent report instance
				// extent.flush();
			} catch (Exception e) {
				Log.error("Class DriverScript | Method main | Exception desc : " + e.getMessage());
			}

			// //Copy Report
			// ActionKeywords.copyReport();
			//
			// // To backup the logs and reports to Build folder
			// ActionKeywords.backupLogs(Constants.BUILD_NAME);
			// Log.info("Logs and Report Backup successfull. Location=" +
			// Constants.BUILD_NAME);
			// // To Create the Zip file of the Execution report (helps when we
			// // sent
			// // mail attachement)
			// Zip.createZipReport(Constants.BUILD_PATH + ".zip",
			// Constants.BUILD_PATH + "\\");
			// Log.info("Converted Logs and Report to zip successfully.
			// Location=" + Constants.BUILD_PATH + ".zip");
			// // Log.info("Ending Automation Test Execution");
			//
			// // sent mail
			// if (settings.get("SentMail").trim().equalsIgnoreCase("Yes")) {
			// String subjectEmail = settings.get("subjectEmail").trim();
			// String mailBody = settings.get("mailBody").trim();
			// // String reportAttancment=new File(Constants.BUILD_PATH +
			// // ".zip").getAbsolutePath();
			// // String reportAttancment=new File(Constants.BUILD_PATH +
			// // "/index.html").getAbsolutePath();
			// String reportAttancment = Constants.BUILD_PATH + ".zip";
			// // String reportAttancment=Constants.BUILD_PATH + "/index.html";
			// String toMail = settings.get("toMail").trim();
			// SentMail.sentReport(subjectEmail, mailBody, reportAttancment,
			// toMail);
			//
			// }
			// Log.info("--------------- End Automation Test Execution
			// -------------------");

			if (settings.get("RecordExecution").toString().trim().equalsIgnoreCase("yes")) {
				// screenRecorder.stop();
			}
		}

	}

	/*
	 * pramod patil Purpose:- To execute the Testlink based script execution
	 */
	private void execute_TestCase_TestLink() {

		TestLinkAPIClient api = new TestLinkAPIClient(testLinkDEVKEY, testLinkURL);
		int iTotalTestRunner = ExcelUtils.getRowCount(settings.get("Path_TestData").trim(),
				settings.get("Sheet_TestRunner").trim());

		if (iTotalTestRunner > 1) {
			int iTestCaseCount = 1;
			String testPrj = "";
			String testPlan = "";
			String testBuild = "";
			// String testCase = "";

			// List of project and plan in excel
			String[][] listProjectAndPlan = ExcelUtils.getDataFromXlsxSheet(settings.get("Path_TestData").trim(),
					settings.get("Sheet_TestRunner").trim());

			String sTestCaseID;
			// String sRunMode;
			String sTestScenarioID;
			String sTestDataDriven;

			testCaseResult = false;
			for (int iTestRunner = 1; iTestRunner < iTotalTestRunner; iTestRunner++) {

				// Read Test cases from the Data excel

				testPrj = listProjectAndPlan[iTestRunner][Integer.parseInt(settings.get("Col_ProjectName").trim())]; // ExcelUtils.getCellData(iTestRunner,
																														// Integer.parseInt(settings.get("Col_ProjectName").trim()),settings.get("Sheet_TestRunner"));
				testPlan = listProjectAndPlan[iTestRunner][Integer.parseInt(settings.get("Col_PlanName").trim())]; // ExcelUtils.getCellData(iTestRunner,
																													// Integer.parseInt(settings.get("Col_PlanName").trim()),settings.get("Sheet_TestRunner"));
				String testRunFlag = listProjectAndPlan[iTestRunner][Integer
						.parseInt(settings.get("Col_Plan_RunMode").trim())]; // ExcelUtils.getCellData(iTestRunner,Integer.parseInt(settings.get("Col_Plan_RunMode").trim()),
																				// settings.get("Sheet_TestRunner"));
				sTestDataDriven = settings.get("Col_TestDataDriven").trim();

				if (testRunFlag.trim().equalsIgnoreCase("Yes") || testRunFlag.trim().equalsIgnoreCase("Y")) {

					sTestScenarioID = listProjectAndPlan[iTestRunner][Integer
							.parseInt(settings.get("Col_TestScenarioID").trim())]; // ExcelUtils.getCellData(iTestRunner,Integer.parseInt(settings.get("Col_TestScenarioID").trim()),settings.get("Sheet_TestRunner").trim());

					Log.info("Project Name in Data Excel=" + testPrj);

					String testProjectId = null;

					// Read Test cases from the testlink
					// To get Project name and iD
					List<String> projectNames = TestLinkIntegration.getProjectNames(api);
					List<String> projectIds = TestLinkIntegration.getProjectIds(api);
					Boolean projectFlag = false;

					for (int i = 0; i < projectNames.size(); i++) {
						Constants.REPORT_SCREENSHOTPATH = "";
						if (projectNames.get(i).trim().equals(testPrj.trim())) {

							Constants.PROJECT_PATH = Constants.BUILD_PATH + "/" + testPrj.trim();
							Constants.TESTCASE_PATH = Constants.PROJECT_PATH;

							ActionKeywords.checkFolder(Constants.PROJECT_PATH);

							Constants.REPORT_SCREENSHOTPATH = testPrj.trim();

							testProjectId = projectIds.get(i);
							Log.info("Project name in Testlink=" + testPrj);
							Log.info("Project Id in Testlink=" + testProjectId);
							projectFlag = true;

							// To get Project Plan and id
							List<String> planNames = TestLinkIntegration.getPlanNames(api, testPrj);
							List<String> planIds = TestLinkIntegration.getPlanIds(api, testPrj);

							Boolean planFlag = false;
							for (int j = 0; j < planNames.size(); j++) {

								if (planNames.get(j).trim().equals(testPlan.trim())) {

									Log.info("Plan Name in Testlink=" + testPlan);
									Log.info("Plan Id in Testlink=" + planIds.get(j));
									planFlag = true;

									// To get Test buid
									List<String> buildIds = TestLinkIntegration.getBuildIdsForTestPlan(api, testPrj,
											testPlan);
									List<String> buildnames = TestLinkIntegration.getBuildNamesForTestPlan(api, testPrj,
											testPlan);
									Boolean buildFlag = false;
									if (buildIds.size() > 0) {
										buildFlag = true;
										for (int bld = 0; bld < buildIds.size(); bld++) {
											Log.info("Build Id=" + buildIds.get(bld));
											Log.info("Build Name=" + buildnames.get(bld));
											testBuild = buildnames.get(bld);

											// To get suite name and id
											List<String> suiteIds = TestLinkIntegration.getProjectSuiteIds(api, testPrj,
													testPlan);
											List<String> suiteNames = TestLinkIntegration.getProjectSuiteNames(api,
													testPrj, testPlan);

											for (int c = 0; c < suiteNames.size(); c++) {
												Log.info("Suite Name=" + suiteNames.get(c));
												Log.info("Suite Id=" + suiteIds.get(c));

												// To get Testcase Name and id
												List<String> testcaseNames = TestLinkIntegration.getTestCaseNames(api,
														testProjectId, suiteIds.get(c));
												List<String> testcaseIds = TestLinkIntegration.getTestCaseIds(api,
														testProjectId, suiteIds.get(c));
												List<String> testcaseExternalIds = TestLinkIntegration
														.getTestCaseExternalIds(api, testProjectId, suiteIds.get(c));
												testCaseResult = false;
												for (int d = 0; d < testcaseNames.size(); d++) {
													testCaseResult = false;
													Log.info("Test Case Name=" + testcaseNames.get(d));
													Log.info("Test Case Id=" + testcaseIds.get(d));
													Log.info("Test Case External Ids=" + testcaseExternalIds.get(d));

													sTestCaseID = testcaseExternalIds.get(d);
													Log.info("TestCaseID: " + sTestCaseID);
													sTestScenarioID = sTestCaseID;
													executeSelectedTestCase(sTestCaseID, testPrj, testPlan, testBuild,
															sTestScenarioID, sTestDataDriven, (d + 1),
															"sTestDescription");

													ExcelUtils.setCellDataValue(testcaseExternalIds.get((d)).trim(),
															iTestCaseCount,
															Integer.parseInt(settings.get("Col_TestCaseID").trim()),
															settings.get("Path_TestData").trim(),
															settings.get("Sheet_TestCases").trim());
													ExcelUtils.setCellDataValue(testcaseNames.get(d).trim(),
															iTestCaseCount,
															Integer.parseInt(settings.get("Col_TestCaseName").trim()),
															settings.get("Path_TestData").trim(),
															settings.get("Sheet_TestCases").trim());
													ExcelUtils.setCellDataValue(sTestScenarioID, iTestCaseCount,
															Integer.parseInt(settings.get("Col_TestScenarioID").trim()),
															settings.get("Path_TestData").trim(),
															settings.get("Sheet_TestCases").trim());
													ExcelUtils.setCellDataValue("Yes", iTestCaseCount,
															Integer.parseInt(settings.get("Col_RunMode").trim()),
															settings.get("Path_TestData").trim(),
															settings.get("Sheet_TestCases").trim());

													iTestCaseCount++;

												}
											}

										}
									}
									if (!buildFlag) {
										Log.error("Test Build not found.");
									}
								}

							}

							if (!planFlag) {
								Log.error("Test Plan not found.");
							}
						}
					}
					if (!projectFlag) {
						Log.error("Test Project not found.");
					}
				} else {
					Log.error("Test Project=" + testPrj + " run mode is " + testRunFlag + " in Excel.");
				}
			}
		} else {
			Log.error("Test project and Test Plan not specified in excel.");
		}
	}

	/*
	 * pramod patil Purpose:- To execute the Test cases
	 */
	private void executeSelectedTestCase(String sTestCaseID, String testPrj, String testPlan, String testBuild,
			String sTestScenarioID, String sTestDataDriven, int tcRowCount, String sTestDescription) {

		Constants.TESTCASE_PATH = Constants.PROJECT_PATH + "/" + sTestCaseID;
		// test = extent.startTest(sTestCaseID);
		// Test case report folder
		ActionKeywords.checkFolder(Constants.TESTCASE_PATH);

		// Screenshot folder
		Constants.SCREENSHOTDESCDIR = Constants.TESTCASE_PATH;

		Constants.REPORT_SCREENSHOTPATH = Constants.REPORT_SCREENSHOTPATH + "/" + sTestCaseID;

		String[][] testStepsList = ExcelUtils.getDataFromXlsxSheet(settings.get("Path_TestData").trim(),
				sTestScenarioID);

		// int iTestStep =
		// ExcelUtils.getRowContains(settings.get("Path_TestData").trim(),
		// sTestCaseID,Integer.parseInt(settings.get("Col_TestCaseID").trim()),
		// sTestScenarioID.trim());

		int iTestStep = ExcelUtils.getRowContains(testStepsList, sTestCaseID,
				Integer.parseInt(settings.get("Col_TestCaseID").trim()), sTestScenarioID.trim());

		Log.info("Test Case First Row # :" + iTestStep);
		// int iTestLastStep =
		// ExcelUtils.getTestStepsCount(settings.get("Path_TestData").trim(),
		// sTestScenarioID.trim(),sTestCaseID, iTestStep);

		int iTestLastStep = ExcelUtils.getTestStepsCount(testStepsList, sTestCaseID, iTestStep);
		Log.info("Test Case Last Row # :" + iTestLastStep);

		if (iTestStep == iTestLastStep) {
			Log.startTestCase(sTestCaseID);
			test = extent.startTest(sTestCaseID);

			Log.info("Test steps are not defined for the test case=" + sTestCaseID);
			test.log(LogStatus.FAIL, "Test steps are not defined for the test case=" + sTestCaseID);

			// Create new report sheet for the Test case
			ExcelUtils.addNewSheet(settings.get("Report_Excel_Path").trim(), sTestCaseID);

			ExcelUtils.setCellDataValue("Test Steps", 0, Integer.parseInt(settings.get("Col_Report_Step").trim()),
					settings.get("Report_Excel_Path").trim(), sTestCaseID.trim());
			ExcelUtils.setCellDataValue("Status", 0, Integer.parseInt(settings.get("Col_Report_Status").trim()),
					settings.get("Report_Excel_Path").trim(), sTestCaseID.trim());

			ExcelUtils.setCellDataValue("Test steps are not defined for the test case=" + sTestCaseID, 1,
					Integer.parseInt(settings.get("Col_Report_Step").trim()), settings.get("Report_Excel_Path").trim(),
					sTestCaseID.trim());
			ExcelUtils.setCellDataValue("Fail", 1, Integer.parseInt(settings.get("Col_Report_Status").trim()),
					settings.get("Report_Excel_Path").trim(), sTestCaseID.trim());

			ExcelUtils.setCellDataValue(settings.get("KEYWORD_FAIL").trim(), tcRowCount,
					Integer.parseInt(settings.get("Col_Result").trim()), settings.get("Path_TestData").trim(),
					settings.get("Sheet_TestCases").trim());

			testStepList.clear();
			testStepResultList.clear();

			testStepList.add("Test steps are not defined for the test case=" + sTestCaseID + ".");
			testStepResultList.add(settings.get("KEYWORD_FAIL").trim());

			if (settings.get("Testlink_integration").trim().equalsIgnoreCase("Yes")
					&& settings.get("Testlink_Result_Update").trim().equalsIgnoreCase("Yes")) {
				TestLinkIntegration.reportResult(testPrj, testPlan, sTestCaseID, testBuild, (testStepList.toString()),
						TestLinkAPIResults.TEST_FAILED);
			}

			testCaseResult = false;
			Log.endTestCase(sTestCaseID);
			extent.endTest(test);

		} else {
			if (sTestDataDriven.equalsIgnoreCase("Yes")) {

				int startStep = iTestStep;

				String[][] dataObjectwithYes = ExcelUtils
						.getDataFromXlsxSheetWithYesMode(settings.get("Test_Data_Excel_Path").trim(), sTestCaseID);

				// Fetch test data from the TestData excel
				String[][] dataObject = ExcelUtils.getDataFromXlsxSheet(settings.get("Test_Data_Excel_Path").trim(),
						sTestCaseID);

				boolean multipleDataResultFlag = false;

				// Clear previous excel results from the list of testcases
				ExcelUtils.clearResults(settings.get("Test_Data_Excel_Path").trim(), sTestCaseID,
						Integer.parseInt(settings.get("Col_Data_Result").trim()), "");
				Log.info("Data rows with Yes option=" + dataObjectwithYes.length);

				if (dataObject.length > 1) {
					int yesDataCount = 0;
					for (int invCount = 0; invCount < dataObject.length; invCount++) {

						try {

							int tempResultInvCount = invCount;
							// Test Data Run Yes
							if (dataObject[invCount][0].equalsIgnoreCase("yes")) {
								yesDataCount++;
								Log.startTestCase(sTestCaseID + " Iteration data row=" + yesDataCount);
								test = extent.startTest(
										sTestCaseID + "-" + sTestDescription + " Iteration Data=" + yesDataCount);
								test.log(LogStatus.INFO, "Test Case Flow is ->" + sTestDescription);

								// Create new report sheet for the Test case
								// pam 29 may bug heance below 3 are commented
								// and
								// used at bottom
								// ExcelUtils.addNewSheet(settings.get("Report_Excel_Path").trim(),
								// (sTestCaseID + " Iteration=" +
								// yesDataCount));
								// ExcelUtils.setCellDataValue("Test Steps", 0,
								// Integer.parseInt(settings.get("Col_Report_Step").trim()),
								// settings.get("Report_Excel_Path").trim(),
								// (sTestCaseID + " Iteration=" +
								// yesDataCount));
								// ExcelUtils.setCellDataValue("Status", 0,
								// Integer.parseInt(settings.get("Col_Report_Status").trim()),
								// settings.get("Report_Excel_Path").trim(),
								// (sTestCaseID + " Iteration=" +
								// yesDataCount));

								String currentTestStep = "";
								String sActionKeyword;
								String sObjectName;
								String sTestData;
								String sObjectType;

								testStepList.clear();
								testStepResultList.clear();
								boolean warningFlag = false;

								// List of test steps for Data Driven test case
								// Remoal excel
								// String[][] testStepsList = ExcelUtils
								// .getDataFromXlsxSheet(settings.get("Path_TestData").trim(),
								// sTestScenarioID);

								// List of reusable steps
								String[][] testReusableStepsList = ExcelUtils.getDataFromXlsxSheet(
										settings.get("Path_TestData").trim(),
										settings.get("ReusableUtilitySheetName").trim());

								// pam clear result
								// Clear previous excel results from the list of
								// testcases
								// ExcelUtils.clearResults(settings.get("Path_TestData").trim(),
								// sTestScenarioID,
								// Integer.parseInt(settings.get("Col_TestStepResult").trim()),
								// "");
								//
								// Log.info("Cleared the previous execution
								// results from test case " + sTestScenarioID +
								// ".");

								// Clear previous excel results from the list of
								// testcases
								// ExcelUtils.clearResults(settings.get("Path_TestData").trim(),
								// settings.get("ReusableUtilitySheetName").trim(),
								// Integer.parseInt(settings.get("Col_TestStepResult").trim()),
								// "");
								// Log.info("Cleared the previous execution
								// results
								// from reusable component sheet.");

								int tempInvCount = invCount;

								for (iTestStep = startStep; iTestStep < iTestLastStep; iTestStep++) {
									sActionKeyword = testStepsList[iTestStep][Integer
											.parseInt(settings.get("Col_ActionKeyword").trim())]; // ExcelUtils.getCellData(iTestStep,Integer.parseInt(settings.get("Col_ActionKeyword").trim()),sTestScenarioID.trim());
									sObjectName = testStepsList[iTestStep][Integer
											.parseInt(settings.get("Col_ObjectName").trim())]; // ExcelUtils.getCellData(iTestStep,Integer.parseInt(settings.get("Col_ObjectName").trim()),sTestScenarioID.trim());
									sTestData = testStepsList[iTestStep][Integer
											.parseInt(settings.get("Col_TestData").trim())]; // ExcelUtils.getCellData(iTestStep,Integer.parseInt(settings.get("Col_TestData").trim()),sTestScenarioID.trim());
									sObjectType = testStepsList[iTestStep][Integer
											.parseInt(settings.get("Col_ObjectType").trim())]; // ExcelUtils.getCellData(iTestStep,Integer.parseInt(settings.get("Col_ObjectType").trim()),sTestScenarioID.trim());
									currentTestStep = testStepsList[iTestStep][Integer
											.parseInt(settings.get("Col_StepsDescription").trim())]; // ExcelUtils.getCellData(iTestStep,Integer.parseInt(settings.get("Col_StepsDescription").trim()),sTestScenarioID.trim());

									// loop handle start
									int loopStepsCount = 0;
									int loopStartRow = iTestStep;
									int loopEndRow = iTestStep;
									int numOfDatarowslooped = 0;
									if (currentTestStep.equals("#StartLoop")) {
										numOfDatarowslooped = Integer.parseInt(sTestData);

										for (int count = loopStartRow + 1; count < iTestLastStep; count++) {
											currentTestStep = testStepsList[count][Integer
													.parseInt(settings.get("Col_StepsDescription").trim())];
											if (currentTestStep.equals("#EndLoop")) {
												loopEndRow = count;
												break;
											}
										}
										loopStepsCount = loopEndRow - loopStartRow;

									}
									// Log.info("Loop Start Row=" +
									// loopStartRow);
									// Log.info("Loop End Row=" + loopEndRow);
									// Log.info("Loop Step Count=" +
									// loopStepsCount);
									// Log.info("Data rows for looping" +
									// numOfDatarowslooped);

									currentTestStep = testStepsList[iTestStep][Integer
											.parseInt(settings.get("Col_StepsDescription").trim())]; // ExcelUtils.getCellData(iTestStep,Integer.parseInt(settings.get("Col_StepsDescription").trim()),sTestScenarioID.trim());

									// int tempInvCount = invCount;
									for (int loopDataCount = 0; loopDataCount <= numOfDatarowslooped; loopDataCount++) {

										invCount = tempInvCount + loopDataCount;

										if (loopDataCount > 0 && numOfDatarowslooped == loopDataCount) {
											break;
										}

										// Log.info("Data Execution=" +
										// loopDataCount + " inv
										// Count="+invCount);

										for (int loopExec = loopStartRow; loopExec <= loopEndRow; loopExec++) {
											iTestStep = loopExec;

											sActionKeyword = testStepsList[iTestStep][Integer
													.parseInt(settings.get("Col_ActionKeyword").trim())];
											sObjectName = testStepsList[iTestStep][Integer
													.parseInt(settings.get("Col_ObjectName").trim())];
											sTestData = testStepsList[iTestStep][Integer
													.parseInt(settings.get("Col_TestData").trim())];
											sObjectType = testStepsList[iTestStep][Integer
													.parseInt(settings.get("Col_ObjectType").trim())];
											currentTestStep = testStepsList[iTestStep][Integer
													.parseInt(settings.get("Col_StepsDescription").trim())];

											if (currentTestStep.trim().equals("#StartLoop")
													|| currentTestStep.trim().equals("#EndLoop")) {
												continue;
											}

											// loop handle end

											if (currentTestStep
													.contains(settings.get("ReusableUtilitySymbol").trim())) {
												int resuableTestStep = 0;
												int resuableTestLastStep = 0;

												// resuableTestStep =
												// ExcelUtils.getRowContains(settings.get("Path_TestData").trim(),currentTestStep.replaceAll(settings.get("ReusableUtilitySymbol").trim(),
												// ""),0,
												// settings.get("ReusableUtilitySheetName").trim());

												resuableTestStep = ExcelUtils.getRowContains(testReusableStepsList,
														currentTestStep.replaceAll(
																settings.get("ReusableUtilitySymbol").trim(), ""),
														0, settings.get("ReusableUtilitySheetName").trim());

												Log.info("Reusable Test Step First Row # :" + resuableTestStep);

												// resuableTestLastStep =
												// ExcelUtils.getTestStepsCount(settings.get("Path_TestData").trim(),settings.get("ReusableUtilitySheetName").trim(),currentTestStep.replaceAll(settings.get("ReusableUtilitySymbol").trim(),
												// ""),resuableTestStep);

												resuableTestLastStep = ExcelUtils.getTestStepsCount(
														testReusableStepsList,
														currentTestStep.replaceAll(
																settings.get("ReusableUtilitySymbol").trim(), ""),
														resuableTestStep);

												Log.info("Reusable Test Step Last Row # :" + resuableTestLastStep);

												// Reusable test steps form the
												// Reusable
												// Utility sheet

												// Remove Excel in each loop
												// String[][]
												// testReusableStepsList
												// =
												// ExcelUtils.getDataFromXlsxSheet(
												// settings.get("Path_TestData").trim(),
												// settings.get("ReusableUtilitySheetName").trim());

												for (int reuseTestStep = resuableTestStep; reuseTestStep < resuableTestLastStep; reuseTestStep++) {
													sActionKeyword = testReusableStepsList[reuseTestStep][Integer
															.parseInt(settings.get("Col_ActionKeyword").trim())]; // ExcelUtils.getCellData(reuseTestStep,Integer.parseInt(settings.get("Col_ActionKeyword").trim()),"ReusableUtilities");
													sObjectName = testReusableStepsList[reuseTestStep][Integer
															.parseInt(settings.get("Col_ObjectName").trim())]; // ExcelUtils.getCellData(reuseTestStep,Integer.parseInt(settings.get("Col_ObjectName").trim()),"ReusableUtilities");
													sTestData = testReusableStepsList[reuseTestStep][Integer
															.parseInt(settings.get("Col_TestData").trim())]; // ExcelUtils.getCellData(reuseTestStep,Integer.parseInt(settings.get("Col_TestData").trim()),"ReusableUtilities");
													currentTestStep = testReusableStepsList[reuseTestStep][Integer
															.parseInt(settings.get("Col_StepsDescription").trim())];

													// for(int
													// matchColumn=0;matchColumn<dataObject[0][0].length();matchColumn++)

													// loop handle start
													int reusalbeloopStepsCount = 0;
													int reusalbeloopStartRow = reuseTestStep;
													int reusalbeloopEndRow = reuseTestStep;
													int reusalbelnumOfDatarowslooped = 0;
													if (currentTestStep.equals("#StartLoop")) {
														reusalbelnumOfDatarowslooped = Integer.parseInt(sTestData);

														for (int count = reusalbeloopStartRow
																+ 1; count < resuableTestLastStep; count++) {
															currentTestStep = testReusableStepsList[count][Integer
																	.parseInt(settings.get("Col_StepsDescription")
																			.trim())];
															if (currentTestStep.equals("#EndLoop")) {
																reusalbeloopEndRow = count;
																break;
															}
														}
														reusalbeloopStepsCount = reusalbeloopEndRow
																- reusalbeloopStartRow;

													}
													// Log.info("Loop Start
													// Row=" +
													// reusalbeloopStartRow);
													// Log.info("Loop End Row="
													// +
													// reusalbeloopEndRow);
													// Log.info("Loop Step
													// Count=" +
													// reusalbeloopStepsCount);
													// Log.info("Data rows for
													// looping" +
													// reusalbelnumOfDatarowslooped);

													tempInvCount = invCount;
													for (int reusableloopDataCount = 0; reusableloopDataCount <= reusalbelnumOfDatarowslooped; reusableloopDataCount++) {
														invCount = tempInvCount + reusableloopDataCount;

														if (reusableloopDataCount > 0
																&& reusalbelnumOfDatarowslooped == reusableloopDataCount) {
															break;
														}

														for (int reusableloopExec = reusalbeloopStartRow; reusableloopExec <= reusalbeloopEndRow; reusableloopExec++) {

															reuseTestStep = reusableloopExec;

															currentTestStep = testReusableStepsList[reuseTestStep][Integer
																	.parseInt(settings.get("Col_StepsDescription")
																			.trim())];

															if (currentTestStep.trim().equals("#StartLoop")
																	|| currentTestStep.trim().equals("#EndLoop")) {
																continue;
															}
															sActionKeyword = testReusableStepsList[reuseTestStep][Integer
																	.parseInt(
																			settings.get("Col_ActionKeyword").trim())];
															sObjectName = testReusableStepsList[reuseTestStep][Integer
																	.parseInt(settings.get("Col_ObjectName").trim())];
															sTestData = testReusableStepsList[reuseTestStep][Integer
																	.parseInt(settings.get("Col_TestData").trim())];
															sObjectType = testReusableStepsList[reuseTestStep][Integer
																	.parseInt(settings.get("Col_ObjectType").trim())];

															int columnNum = ExcelUtils.getColumnCount(
																	settings.get("Test_Data_Excel_Path").trim(),
																	sTestCaseID);

															for (int matchColumn = 0; matchColumn < columnNum; matchColumn++) {
																if (sTestData
																		.equalsIgnoreCase(dataObject[0][matchColumn])) {
																	sTestData = dataObject[invCount][matchColumn];
																	break;
																}
															}

															sObjectType = testReusableStepsList[reuseTestStep][Integer
																	.parseInt(settings.get("Col_ObjectType").trim())]; // ExcelUtils.getCellData(reuseTestStep,Integer.parseInt(settings.get("Col_ObjectType").trim()),"ReusableUtilities");

															currentTestStep = testReusableStepsList[reuseTestStep][Integer
																	.parseInt(settings.get("Col_StepsDescription")
																			.trim())]; // ExcelUtils.getCellData(reuseTestStep,Integer.parseInt(settings.get("Col_StepsDescription").trim()),"ReusableUtilities");

															testStepsResult = false;
															testStepsWarningResult = false;

															execute_Actions(sTestCaseID, sActionKeyword, sObjectType,
																	sObjectName, sTestData);

															if (testStepsWarningResult == true
																	&& testStepsResult == false) {
																Log.info(currentTestStep + ". Action=" + sActionKeyword
																		+ ", Object Name=" + sObjectName + ", data= "
																		+ sTestData + "");
																testStepList.add("Validation failed for the data="
																		+ sTestData + ". Action=" + sActionKeyword
																		+ ", Object Name=" + sObjectName + ", data= "
																		+ sTestData + "");
																testStepResultList
																		.add(settings.get("KEYWORD_FAIL").trim());

																test.log(LogStatus.WARNING,
																		"Validation failed for the data=" + sTestData
																				+ ". Action=" + sActionKeyword
																				+ ", Object Name=" + sObjectName
																				+ ", data= " + sTestData + "");
																ActionKeywords.captureScreenshot(testPrj, sTestCaseID,
																		settings.get("KEYWORD_FAIL").trim());

																ExcelUtils.setCellDataValue(
																		settings.get("KEYWORD_FAIL").trim(),
																		reuseTestStep,
																		Integer.parseInt(settings
																				.get("Col_TestStepResult").trim()),
																		settings.get("Path_TestData").trim(),
																		settings.get("ReusableUtilitySheetName")
																				.trim());

																ExcelUtils.setCellDataValue(
																		settings.get("KEYWORD_FAIL").trim(), iTestStep,
																		Integer.parseInt(settings
																				.get("Col_TestStepResult").trim()),
																		settings.get("Path_TestData").trim(),
																		sTestScenarioID.trim());

																warningFlag = true;
																testStepsWarningResult = false;
																testCaseResult = true;
															} else if (testStepsResult == false) {
																Log.info(currentTestStep + ". Action=" + sActionKeyword
																		+ ", Object Name=" + sObjectName + ", data= "
																		+ sTestData + "");
																testStepList.add(currentTestStep + ". Action="
																		+ sActionKeyword + ", Object Name="
																		+ sObjectName + ", data= " + sTestData + "");
																testStepResultList
																		.add(settings.get("KEYWORD_FAIL").trim());

																test.log(LogStatus.FAIL,
																		currentTestStep + ". Action=" + sActionKeyword
																				+ ", Object Name=" + sObjectName
																				+ ", data= " + sTestData + "");

																ExcelUtils.setCellDataValue(
																		settings.get("KEYWORD_FAIL").trim(),
																		reuseTestStep,
																		Integer.parseInt(settings
																				.get("Col_TestStepResult").trim()),
																		settings.get("Path_TestData").trim(),
																		settings.get("ReusableUtilitySheetName")
																				.trim());

																ExcelUtils.setCellDataValue(
																		settings.get("KEYWORD_FAIL").trim(), iTestStep,
																		Integer.parseInt(settings
																				.get("Col_TestStepResult").trim()),
																		settings.get("Path_TestData").trim(),
																		sTestScenarioID.trim());

																testCaseResult = false;
																break;

															} else {
																Log.info(currentTestStep + ". Action=" + sActionKeyword
																		+ ", Object Name=" + sObjectName + ", data= "
																		+ sTestData + "");
																testStepList.add(currentTestStep + ". Action="
																		+ sActionKeyword + ", Object Name="
																		+ sObjectName + ", data= " + sTestData + "");
																testStepResultList
																		.add(settings.get("KEYWORD_PASS").trim());

																// test.log(LogStatus.PASS,currentTestStep
																// + ". Action="
																// +
																// sActionKeyword+
																// ", Object
																// Name="
																// +
																// sObjectName+
																// ",
																// data= " +
																// sTestData +
																// "");

																test.log(LogStatus.PASS,
																		currentTestStep + ". data= " + sTestData + "");

																ExcelUtils.setCellDataValue(
																		settings.get("KEYWORD_PASS").trim(),
																		reuseTestStep,
																		Integer.parseInt(settings
																				.get("Col_TestStepResult").trim()),
																		settings.get("Path_TestData").trim(),
																		settings.get("ReusableUtilitySheetName")
																				.trim());

																ExcelUtils.setCellDataValue(
																		settings.get("KEYWORD_PASS").trim(), iTestStep,
																		Integer.parseInt(settings
																				.get("Col_TestStepResult").trim()),
																		settings.get("Path_TestData").trim(),
																		sTestScenarioID.trim());
																testCaseResult = true;
															}
														}
														// tempInvCount=invCount;
													}
													if (testCaseResult == false) {
														break;
													}
												}

											} else {

												// Check the test data column
												// mapping
												// for (int matchColumn = 0;
												// matchColumn
												// < dataObject[0][0].length();
												// matchColumn++) {
												int columnNum = ExcelUtils.getColumnCount(
														settings.get("Test_Data_Excel_Path").trim(), sTestCaseID);

												for (int matchColumn = 0; matchColumn < columnNum; matchColumn++) {
													if (sTestData.equalsIgnoreCase(dataObject[0][matchColumn])) {
														sTestData = dataObject[invCount][matchColumn];
														break;
													}
												}

												sObjectType = testStepsList[iTestStep][Integer
														.parseInt(settings.get("Col_ObjectType").trim())]; // ExcelUtils.getCellData(iTestStep,Integer.parseInt(settings.get("Col_ObjectType").trim()),sTestScenarioID.trim());

												currentTestStep = testStepsList[iTestStep][Integer
														.parseInt(settings.get("Col_StepsDescription").trim())]; // ExcelUtils.getCellData(iTestStep,Integer.parseInt(settings.get("Col_StepsDescription").trim()),sTestScenarioID.trim());

												testStepsResult = false;
												testStepsWarningResult = false;

												execute_Actions(sTestCaseID, sActionKeyword, sObjectType, sObjectName,
														sTestData);

												if (testStepsWarningResult == true && testStepsResult == false) {
													Log.info(currentTestStep + ". Action=" + sActionKeyword
															+ ", Object Name=" + sObjectName + ", data= " + sTestData
															+ "");
													testStepList.add("Validation failed for the data=" + sTestData
															+ ". Action=" + sActionKeyword + ", Object Name="
															+ sObjectName + ", data= " + sTestData + "");
													testStepResultList.add(settings.get("KEYWORD_FAIL").trim());

													test.log(LogStatus.WARNING,
															"Validation failed for the data=" + sTestData + ". Action="
																	+ sActionKeyword + ", Object Name=" + sObjectName
																	+ ", data= " + sTestData + "");
													ActionKeywords.captureScreenshot(testPrj, sTestCaseID,
															settings.get("KEYWORD_FAIL").trim());

													ExcelUtils.setCellDataValue(settings.get("KEYWORD_FAIL").trim(),
															iTestStep,
															Integer.parseInt(settings.get("Col_TestStepResult").trim()),
															settings.get("Path_TestData").trim(),
															sTestScenarioID.trim());
													// ExcelUtils.setCellData(settings.get("KEYWORD_FAIL").trim(),
													// iTestStep,
													// Integer.parseInt(settings.get("Col_TestStepResult").trim()),
													// settings.get("Path_TestData").trim(),
													// "ReusableUtilities");
													warningFlag = true;
													testStepsWarningResult = false;
													testCaseResult = true;
												} else if (testStepsResult == false) {
													Log.info(currentTestStep + ". Action=" + sActionKeyword
															+ ", Object Name=" + sObjectName + ", data= " + sTestData
															+ "");
													testStepList.add(currentTestStep + ". Action=" + sActionKeyword
															+ ", Object Name=" + sObjectName + ", data= " + sTestData
															+ "");
													testStepResultList.add(settings.get("KEYWORD_FAIL").trim());

													test.log(LogStatus.FAIL,
															currentTestStep + ". Action=" + sActionKeyword
																	+ ", Object Name=" + sObjectName + ", data= "
																	+ sTestData + "");

													ExcelUtils.setCellDataValue(settings.get("KEYWORD_FAIL").trim(),
															iTestStep,
															Integer.parseInt(settings.get("Col_TestStepResult").trim()),
															settings.get("Path_TestData").trim(),
															sTestScenarioID.trim());

													testCaseResult = false;
													// Log.endTestCase(sTestCaseID);
													break;

												} else {
													Log.info(currentTestStep + ". Action=" + sActionKeyword
															+ ", Object Name=" + sObjectName + ", data= " + sTestData
															+ "");
													testStepList.add(currentTestStep + ". Action=" + sActionKeyword
															+ ", Object Name=" + sObjectName + ", data= " + sTestData
															+ "");
													testStepResultList.add(settings.get("KEYWORD_PASS").trim());

													// test.log(LogStatus.PASS,
													// currentTestStep + ".
													// Action="
													// + sActionKeyword
													// + ", Object Name=" +
													// sObjectName + ", data= "
													// +
													// sTestData + "");

													test.log(LogStatus.PASS,
															currentTestStep + ". data= " + sTestData + "");

													ExcelUtils.setCellDataValue(settings.get("KEYWORD_PASS").trim(),
															iTestStep,
															Integer.parseInt(settings.get("Col_TestStepResult").trim()),
															settings.get("Path_TestData").trim(),
															sTestScenarioID.trim());

													testCaseResult = true;
												}

											}
											if (testCaseResult == false) {
												break;
											}
										}
										// tempInvCount=invCount;

									}
									if (testCaseResult == false) {
										break;
									}
								}
								invCount = tempResultInvCount;
								if (testCaseResult == true && warningFlag == false) {
									ExcelUtils.setCellDataValue(settings.get("KEYWORD_PASS").trim(), (tcRowCount),
											Integer.parseInt(settings.get("Col_Result").trim()),
											settings.get("Path_TestData").trim(),
											settings.get("Sheet_TestCases").trim());

									ExcelUtils.setCellDataValue(settings.get("KEYWORD_PASS").trim(), (invCount),
											Integer.parseInt(settings.get("Col_Data_Result").trim()),
											settings.get("Test_Data_Excel_Path").trim(), sTestCaseID);

									// ActionKeywords.captureScreenshot(testPrj,
									// sTestCaseID,
									// settings.get("KEYWORD_PASS").trim());
									testStepList.add(
											"Test Case " + sTestCaseID + " is " + settings.get("KEYWORD_PASS").trim());
									testStepResultList.add(settings.get("KEYWORD_PASS").trim());

									test.log(LogStatus.PASS,
											"Test Case " + sTestCaseID + " is " + settings.get("KEYWORD_PASS").trim());
									Log.info("Test Case " + sTestCaseID + " is " + settings.get("KEYWORD_PASS").trim());

									if (settings.get("Testlink_integration").trim().equalsIgnoreCase("Yes")
											&& settings.get("Testlink_Result_Update").trim().equalsIgnoreCase("Yes")) {
										TestLinkIntegration.reportResult(testPrj, testPlan, sTestCaseID, testBuild,
												(testStepList.toString()), TestLinkAPIResults.TEST_PASSED);
									}

								} else {
									ExcelUtils.setCellDataValue(settings.get("KEYWORD_FAIL").trim(), (tcRowCount),
											Integer.parseInt(settings.get("Col_Result").trim()),
											settings.get("Path_TestData").trim(),
											settings.get("Sheet_TestCases").trim());

									ExcelUtils.setCellDataValue(settings.get("KEYWORD_FAIL").trim(), (invCount),
											Integer.parseInt(settings.get("Col_Data_Result").trim()),
											settings.get("Test_Data_Excel_Path").trim(), sTestCaseID);

									ActionKeywords.captureScreenshot(testPrj, sTestCaseID,
											settings.get("KEYWORD_FAIL").trim());
									test.log(LogStatus.FAIL,
											"Test Case " + sTestCaseID + " is " + settings.get("KEYWORD_FAIL").trim());
									Log.info("Test Case " + sTestCaseID + " is " + settings.get("KEYWORD_FAIL").trim());
									testStepList.add(
											"Test Case " + sTestCaseID + " is " + settings.get("KEYWORD_FAIL").trim());
									testStepResultList.add(settings.get("KEYWORD_FAIL").trim());

									// metis start
									if (settings.get("mentis_integration").trim().equalsIgnoreCase("yes")
											|| settings.get("mentis_integration").trim().equalsIgnoreCase("Y")) {

										// MentisIntegration.testCreateMentisIssue(sTestCaseID);

										Log.info("Bug Raised");

										testStepList.add("Bug Raised");
										testStepResultList.add(settings.get("KEYWORD_FAIL").trim());
										test.log(LogStatus.FAIL, "Bug Raised");

										ExcelUtils.setCellDataValue("Bug Raised", (tcRowCount),
												Integer.parseInt(settings.get("Col_Result").trim()),
												settings.get("Path_TestData").trim(),
												settings.get("Sheet_TestCases").trim());

										ExcelUtils.setCellDataValue(settings.get("KEYWORD_FAIL").trim(), (invCount),
												Integer.parseInt(settings.get("Col_Data_Result").trim()),
												settings.get("Test_Data_Excel_Path").trim(), sTestCaseID);

										if (settings.get("Testlink_integration").trim().equalsIgnoreCase("Yes")
												&& settings.get("Testlink_Result_Update").trim()
														.equalsIgnoreCase("Yes")) {
											TestLinkIntegration.reportResult(testPrj, testPlan, sTestCaseID, testBuild,
													(testStepList.toString()), TestLinkAPIResults.TEST_FAILED);
										}

									}

									// mentis end

									if (settings.get("buzilla_integration").trim().equalsIgnoreCase("yes")
											|| settings.get("buzilla_integration").trim().equalsIgnoreCase("Y")) {

										String bugid = BugzillaIntegration.createBug(settings.get("bugURL"),
												settings.get("bugUsername"), settings.get("bugPwd"),
												settings.get("product"), settings.get("component"),
												(sTestCaseID + " Execution Failed."), settings.get("priority"),
												settings.get("os"), (testStepList.toString()), settings.get("version"),
												settings.get("platform"), Constants.BUG_SCREENSHOTPATH);
										Log.info("Bug ID=" + bugid);
										String bugUrl = settings.get("bugShow") + bugid;
										Log.info("Bug URL=" + bugUrl);
										Log.info("Bug Raised with bug id=" + bugid + " and Bug URL=" + bugUrl);

										testStepList.add("Bug Raised with Bug ID=" + bugid + " and Bug URL=" + bugUrl);
										testStepResultList.add(settings.get("KEYWORD_FAIL").trim());
										test.log(LogStatus.FAIL,
												"Bug Raised with bug id=" + bugid + " and Bug URL=" + bugUrl);

										ExcelUtils.setCellDataValue(
												"Bug Raised with Bug ID=" + bugid + " and Bug URL=" + bugUrl,
												(tcRowCount), Integer.parseInt(settings.get("Col_Result").trim()),
												settings.get("Path_TestData").trim(),
												settings.get("Sheet_TestCases").trim());

										ExcelUtils.setCellDataValue(settings.get("KEYWORD_FAIL").trim(), (invCount),
												Integer.parseInt(settings.get("Col_Data_Result").trim()),
												settings.get("Test_Data_Excel_Path").trim(), sTestCaseID);

										if (settings.get("Testlink_integration").trim().equalsIgnoreCase("Yes")
												&& settings.get("Testlink_Result_Update").trim()
														.equalsIgnoreCase("Yes")) {
											TestLinkIntegration.reportResult(testPrj, testPlan, sTestCaseID, testBuild,
													(testStepList.toString()), TestLinkAPIResults.TEST_FAILED);
										}

									} else {
										if (settings.get("Testlink_integration").trim().equalsIgnoreCase("Yes")
												&& settings.get("Testlink_Result_Update").trim()
														.equalsIgnoreCase("Yes")) {
											TestLinkIntegration.reportResult(testPrj, testPlan, sTestCaseID, testBuild,
													(testStepList.toString()), TestLinkAPIResults.TEST_FAILED);
										}

									}
									multipleDataResultFlag = true;
								}
								extent.endTest(test);

								// Log.info("Test Case " + sTestCaseID + " Steps
								// are:-");
								String currentStep = "";
								String currentStepStatus = "";
								// String stepNum = "Step ";

								try {
									// pam 29 may bug in excel hence above
									// commented
									// and used here
									if (testStepResultList.contains("FAIL")) {
										ExcelUtils.addNewSheet(settings.get("Report_Excel_Path").trim(),
												(sTestCaseID + " Iteration=" + yesDataCount));
										ExcelUtils.setCellDataValue("Test Steps", 0,
												Integer.parseInt(settings.get("Col_Report_Step").trim()),
												settings.get("Report_Excel_Path").trim(),
												(sTestCaseID + " Iteration=" + yesDataCount));
										ExcelUtils.setCellDataValue("Status", 0,
												Integer.parseInt(settings.get("Col_Report_Status").trim()),
												settings.get("Report_Excel_Path").trim(),
												(sTestCaseID + " Iteration=" + yesDataCount));

										for (int rowCount = 0; rowCount < testStepList.size(); rowCount++) {
											// stepNum = "Step " + (rowCount +
											// 1) +
											// ":";

											currentStep = testStepList.get(rowCount);
											currentStepStatus = testStepResultList.get(rowCount);
											// Log.info(stepNum + currentStep);

											ExcelUtils.setCellDataValue(currentStep, (rowCount + 1),
													Integer.parseInt(settings.get("Col_Report_Step").trim()),
													settings.get("Report_Excel_Path").trim(),
													(sTestCaseID + " Iteration=" + yesDataCount).trim());
											ExcelUtils.setCellDataValue(currentStepStatus, (rowCount + 1),
													Integer.parseInt(settings.get("Col_Report_Status").trim()),
													settings.get("Report_Excel_Path").trim(),
													(sTestCaseID + " Iteration=" + yesDataCount).trim());
										}
									} else {
										ExcelUtils.addNewSheet(settings.get("Report_Excel_Path").trim(), "Sheet1");
									}
								} catch (Exception e) {
									Log.info("Error occured in Report excel. Error=" + e.getMessage());
								}
								// try
								// {
								//
								// // To raise bug in excel
								// if (testStepResultList.contains("FAIL")) {
								// int rowCount =
								// ExcelUtils.getRowCount(settings.get("Bug_Excel_Path").trim(),
								// "Bugs");
								// String bugId =
								// ExcelUtils.getCellData(settings.get("Bug_Excel_Path").trim(),
								// (rowCount - 1), 0, "Bugs");
								// bugId =
								// String.valueOf(Integer.parseInt(bugId) +
								// 1);
								//
								// ExcelUtils.setCellDataValue(String.valueOf(bugId),
								// rowCount, 0,
								// settings.get("Bug_Excel_Path").trim(),
								// "Bugs");
								// ExcelUtils.setCellDataValue(testStepList.toString(),
								// rowCount, 1,
								// settings.get("Bug_Excel_Path").trim(),
								// "Bugs");
								// ExcelUtils.setCellDataValue((sTestCaseID + "
								// Iteration data row=" + yesDataCount),
								// rowCount, 2,
								// settings.get("Bug_Excel_Path").trim(),
								// "Bugs");
								// Log.info("Excel BugId=" + bugId);
								// }
								// }
								// catch(Exception e)
								// {
								// Log.info("Defect not logged in excel due to
								// error="+e.getMessage());
								// }

								// To write the ouput in excel

								try {

									if (!outputInExcel.isEmpty()) {
										Map<String, String> map = new HashMap<String, String>();
										map = outputInExcel;
										Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
										while (entries.hasNext()) {
											Entry<String, String> entry = entries.next();
											System.out.println(
													"Key = " + entry.getKey() + ", Value = " + entry.getValue());

											String[][] togetUpdatedColumnCount = ExcelUtils.getDataFromXlsxSheet(
													settings.get("Test_Data_Excel_Path").trim(), sTestCaseID);

											int columnNum = ExcelUtils.getColumnCount(
													settings.get("Test_Data_Excel_Path").trim(), sTestCaseID);

											// boolean columnNameFlag = false;
											// Check the test data output column
											// mapping
											for (int matchColumn = 0; matchColumn < columnNum; matchColumn++) {
												if (entry.getKey().trim()
														.equalsIgnoreCase(togetUpdatedColumnCount[0][matchColumn])) {
													columnNum = matchColumn;
													// columnNameFlag = true;
													break;
												}
											}

											ExcelUtils.setCellDataValue(entry.getKey().trim(), 0, columnNum,
													settings.get("Test_Data_Excel_Path").trim(), sTestCaseID);
											ExcelUtils.setCellDataValue(entry.getValue().trim(), invCount, columnNum,
													settings.get("Test_Data_Excel_Path").trim(), sTestCaseID);

											outputInExcel.remove(entry.getKey(), entry.getValue());
										}
									}
								} catch (Exception e) {
									Log.info("Error occured while writting result in excel. Error=" + e.getMessage());
								}
								Log.endTestCase(sTestCaseID + " Iteration data row=" + yesDataCount);
							}

						} catch (Exception e) {

						}
					}

					// If any of the data is fail then overall TC status is fail
					if (multipleDataResultFlag) {
						ExcelUtils.setCellDataValue(settings.get("KEYWORD_FAIL").trim(), tcRowCount,
								Integer.parseInt(settings.get("Col_Result").trim()),
								settings.get("Path_TestData").trim(), settings.get("Sheet_TestCases").trim());
					} else {
						ExcelUtils.setCellDataValue(settings.get("KEYWORD_PASS").trim(), tcRowCount,
								Integer.parseInt(settings.get("Col_Result").trim()),
								settings.get("Path_TestData").trim(), settings.get("Sheet_TestCases").trim());
					}

				} else {
					multipleDataResultFlag = false;
					Log.startTestCase(sTestCaseID);
					test = extent.startTest(sTestCaseID);

					Log.info("Test Data not provided for the test case=" + sTestCaseID);
					test.log(LogStatus.FAIL, "Test Data not provided for the test case=" + sTestCaseID);

					ExcelUtils.setCellDataValue("Test Data not provided for the test case=" + sTestCaseID, 1,
							Integer.parseInt(settings.get("Col_Report_Step").trim()),
							settings.get("Report_Excel_Path").trim(), sTestCaseID.trim());
					ExcelUtils.setCellDataValue("Fail", 1, Integer.parseInt(settings.get("Col_Report_Status").trim()),
							settings.get("Report_Excel_Path").trim(), sTestCaseID.trim());

					ExcelUtils.setCellDataValue(settings.get("KEYWORD_FAIL").trim(), (tcRowCount),
							Integer.parseInt(settings.get("Col_Result").trim()), settings.get("Path_TestData").trim(),
							settings.get("Sheet_TestCases").trim());

					DriverScript.testStepList
							.add("Class DriverScript | Method executeSelectedTestCase | Exception desc : Test Data not provided for the test case="
									+ sTestCaseID);
					DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

					if (settings.get("Testlink_integration").trim().equalsIgnoreCase("Yes")
							&& settings.get("Testlink_Result_Update").trim().equalsIgnoreCase("Yes")) {
						TestLinkIntegration.reportResult(testPrj, testPlan, sTestCaseID, testBuild,
								(testStepList.toString()), TestLinkAPIResults.TEST_FAILED);
					}

					testCaseResult = false;
					Log.endTestCase(sTestCaseID);
					extent.endTest(test);
				}
			} else {

				// Without data driven approach
				Log.startTestCase(sTestCaseID);
				test = extent.startTest(sTestCaseID);

				// Create new report sheet for the Test case
				ExcelUtils.addNewSheet(settings.get("Report_Excel_Path").trim(), sTestCaseID);
				ExcelUtils.setCellDataValue("Test Steps", 0, Integer.parseInt(settings.get("Col_Report_Step").trim()),
						settings.get("Report_Excel_Path").trim(), sTestCaseID);
				ExcelUtils.setCellDataValue("Status", 0, Integer.parseInt(settings.get("Col_Report_Status").trim()),
						settings.get("Report_Excel_Path").trim(), sTestCaseID);

				String currentTestStep = "";
				String sActionKeyword;
				String sObjectName;
				String sTestData;
				String sObjectType;

				testStepList.clear();
				testStepResultList.clear();
				boolean warningFlag = false;

				// List of test steps for Data Driven test case
				testStepsList = ExcelUtils.getDataFromXlsxSheet(settings.get("Path_TestData").trim(), sTestScenarioID);

				for (; iTestStep < iTestLastStep; iTestStep++) {
					sActionKeyword = testStepsList[iTestStep][Integer
							.parseInt(settings.get("Col_ActionKeyword").trim())]; // ExcelUtils.getCellData(iTestStep,Integer.parseInt(settings.get("Col_ActionKeyword").trim()),sTestScenarioID.trim());
					sObjectName = testStepsList[iTestStep][Integer.parseInt(settings.get("Col_ObjectName").trim())]; // ExcelUtils.getCellData(iTestStep,Integer.parseInt(settings.get("Col_ObjectName").trim()),sTestScenarioID.trim());
					sTestData = testStepsList[iTestStep][Integer.parseInt(settings.get("Col_TestData").trim())]; // ExcelUtils.getCellData(iTestStep,Integer.parseInt(settings.get("Col_TestData").trim()),sTestScenarioID.trim());
					sObjectType = testStepsList[iTestStep][Integer.parseInt(settings.get("Col_ObjectType").trim())]; // ExcelUtils.getCellData(iTestStep,Integer.parseInt(settings.get("Col_ObjectType").trim()),sTestScenarioID.trim());
					currentTestStep = testStepsList[iTestStep][Integer
							.parseInt(settings.get("Col_StepsDescription").trim())]; // ExcelUtils.getCellData(iTestStep,Integer.parseInt(settings.get("Col_StepsDescription").trim()),sTestScenarioID.trim());

					if (currentTestStep.contains(settings.get("ReusableUtilitySymbol").trim())) {
						int resuableTestStep = 0;
						int resuableTestLastStep = 0;

						resuableTestStep = ExcelUtils.getRowContains(settings.get("Path_TestData").trim(),
								currentTestStep.replaceAll(settings.get("ReusableUtilitySymbol").trim(), ""), 0,
								settings.get("ReusableUtilitySheetName").trim());
						Log.info("Reusable Test Step First Row # :" + resuableTestStep);
						resuableTestLastStep = ExcelUtils.getTestStepsCount(settings.get("Path_TestData").trim(),
								settings.get("ReusableUtilitySheetName").trim(),
								currentTestStep.replaceAll(settings.get("ReusableUtilitySymbol").trim(), ""),
								resuableTestStep);
						Log.info("Reusable Test Step Last Row # :" + resuableTestLastStep);

						// Reusable test steps form the Reusable Utility sheet
						String[][] testReusableStepsList = ExcelUtils.getDataFromXlsxSheet(
								settings.get("Path_TestData").trim(), settings.get("ReusableUtilitySheetName").trim());

						for (int reuseTestStep = resuableTestStep; reuseTestStep < resuableTestLastStep; reuseTestStep++) {
							sActionKeyword = testReusableStepsList[reuseTestStep][Integer
									.parseInt(settings.get("Col_ActionKeyword").trim())]; // ExcelUtils.getCellData(reuseTestStep,Integer.parseInt(settings.get("Col_ActionKeyword").trim()),"ReusableUtilities");
							sObjectName = testReusableStepsList[reuseTestStep][Integer
									.parseInt(settings.get("Col_ObjectName").trim())]; // ExcelUtils.getCellData(reuseTestStep,Integer.parseInt(settings.get("Col_ObjectName").trim()),"ReusableUtilities");
							sTestData = testReusableStepsList[reuseTestStep][Integer
									.parseInt(settings.get("Col_TestData").trim())]; // ExcelUtils.getCellData(reuseTestStep,Integer.parseInt(settings.get("Col_TestData").trim()),"ReusableUtilities");
							sObjectType = testReusableStepsList[reuseTestStep][Integer
									.parseInt(settings.get("Col_ObjectType").trim())]; // ExcelUtils.getCellData(reuseTestStep,Integer.parseInt(settings.get("Col_ObjectType").trim()),"ReusableUtilities");
							currentTestStep = testReusableStepsList[reuseTestStep][Integer
									.parseInt(settings.get("Col_StepsDescription").trim())]; // ExcelUtils.getCellData(reuseTestStep,Integer.parseInt(settings.get("Col_StepsDescription").trim()),"ReusableUtilities");

							testStepsResult = false;
							testStepsWarningResult = false;

							execute_Actions(sTestCaseID, sActionKeyword, sObjectType, sObjectName, sTestData);

							if (testStepsWarningResult == true && testStepsResult == false) {
								Log.info(currentTestStep + ". Action=" + sActionKeyword + ", Object Name=" + sObjectName
										+ ", data= " + sTestData + "");
								testStepList.add(
										"Validation failed for the data=" + sTestData + ". Action=" + sActionKeyword
												+ ", Object Name=" + sObjectName + ", data= " + sTestData + "");
								testStepResultList.add(settings.get("KEYWORD_FAIL").trim());

								test.log(LogStatus.WARNING,
										"Validation failed for the data=" + sTestData + ". Action=" + sActionKeyword
												+ ", Object Name=" + sObjectName + ", data= " + sTestData + "");

								ActionKeywords.captureScreenshot(testPrj, sTestCaseID,
										settings.get("KEYWORD_FAIL").trim());

								ExcelUtils.setCellDataValue(settings.get("KEYWORD_FAIL").trim(), reuseTestStep,
										Integer.parseInt(settings.get("Col_TestStepResult").trim()),
										settings.get("Path_TestData").trim(),
										settings.get("ReusableUtilitySheetName").trim());

								ExcelUtils.setCellDataValue(settings.get("KEYWORD_FAIL").trim(), iTestStep,
										Integer.parseInt(settings.get("Col_TestStepResult").trim()),
										settings.get("Path_TestData").trim(), sTestScenarioID.trim());

								warningFlag = true;
								testStepsWarningResult = false;
								testCaseResult = true;
							} else if (testStepsResult == false) {
								Log.info(currentTestStep + ". Action=" + sActionKeyword + ", Object Name=" + sObjectName
										+ ", data= " + sTestData + "");
								testStepList.add(currentTestStep + ". Action=" + sActionKeyword + ", Object Name="
										+ sObjectName + ", data= " + sTestData + "");
								testStepResultList.add(settings.get("KEYWORD_FAIL").trim());

								test.log(LogStatus.FAIL, currentTestStep + ". Action=" + sActionKeyword
										+ ", Object Name=" + sObjectName + ", data= " + sTestData + "");

								ExcelUtils.setCellDataValue(settings.get("KEYWORD_FAIL").trim(), reuseTestStep,
										Integer.parseInt(settings.get("Col_TestStepResult").trim()),
										settings.get("Path_TestData").trim(),
										settings.get("ReusableUtilitySheetName").trim());

								ExcelUtils.setCellDataValue(settings.get("KEYWORD_FAIL").trim(), iTestStep,
										Integer.parseInt(settings.get("Col_TestStepResult").trim()),
										settings.get("Path_TestData").trim(), sTestScenarioID.trim());

								testCaseResult = false;
								break;

							} else {
								Log.info(currentTestStep + ". Action=" + sActionKeyword + ", Object Name=" + sObjectName
										+ ", data= " + sTestData + "");
								testStepList.add(currentTestStep + ". Action=" + sActionKeyword + ", Object Name="
										+ sObjectName + ", data= " + sTestData + "");
								testStepResultList.add(settings.get("KEYWORD_PASS").trim());

								// test.log(LogStatus.PASS, currentTestStep + ".
								// Action=" + sActionKeyword
								// + ", Object Name=" + sObjectName + ", data= "
								// + sTestData + "");

								test.log(LogStatus.PASS, currentTestStep + ".data= " + sTestData + "");

								ExcelUtils.setCellDataValue(settings.get("KEYWORD_PASS").trim(), reuseTestStep,
										Integer.parseInt(settings.get("Col_TestStepResult").trim()),
										settings.get("Path_TestData").trim(),
										settings.get("ReusableUtilitySheetName").trim());

								ExcelUtils.setCellDataValue(settings.get("KEYWORD_PASS").trim(), iTestStep,
										Integer.parseInt(settings.get("Col_TestStepResult").trim()),
										settings.get("Path_TestData").trim(), sTestScenarioID.trim());
								testCaseResult = true;
							}
						}
					} else {
						sObjectType = testStepsList[iTestStep][Integer.parseInt(settings.get("Col_ObjectType").trim())]; // ExcelUtils.getCellData(iTestStep,Integer.parseInt(settings.get("Col_ObjectType").trim()),sTestScenarioID.trim());
						currentTestStep = testStepsList[iTestStep][Integer
								.parseInt(settings.get("Col_StepsDescription").trim())]; // ExcelUtils.getCellData(iTestStep,Integer.parseInt(settings.get("Col_StepsDescription").trim()),sTestScenarioID.trim());
						testStepsResult = false;
						testStepsWarningResult = false;
						execute_Actions(sTestCaseID, sActionKeyword, sObjectType, sObjectName, sTestData);
						if (testStepsWarningResult == true && testStepsResult == false) {
							Log.info(currentTestStep + ". Action=" + sActionKeyword + ", Object Name=" + sObjectName
									+ ", data= " + sTestData + "");
							testStepList.add("Validation failed for the data=" + sTestData + ". Action="
									+ sActionKeyword + ", Object Name=" + sObjectName + ", data= " + sTestData + "");
							testStepResultList.add(settings.get("KEYWORD_FAIL").trim());

							test.log(LogStatus.WARNING, "Validation failed for the data=" + sTestData + ". Action="
									+ sActionKeyword + ", Object Name=" + sObjectName + ", data= " + sTestData + "");
							ActionKeywords.captureScreenshot(testPrj, sTestCaseID, settings.get("KEYWORD_FAIL").trim());

							ExcelUtils.setCellDataValue(settings.get("KEYWORD_FAIL").trim(), iTestStep,
									Integer.parseInt(settings.get("Col_TestStepResult").trim()),
									settings.get("Path_TestData").trim(), sTestScenarioID.trim());
							ActionKeywords.captureScreenshot(testPrj, sTestCaseID, settings.get("KEYWORD_FAIL").trim());

							warningFlag = true;
							testStepsWarningResult = false;
							testCaseResult = true;
						} else if (testStepsResult == false) {
							Log.info(currentTestStep + ". Action=" + sActionKeyword + ", Object Name=" + sObjectName
									+ ", data= " + sTestData + "");
							testStepList.add(currentTestStep + ". Action=" + sActionKeyword + ", Object Name="
									+ sObjectName + ", data= " + sTestData + "");
							testStepResultList.add(settings.get("KEYWORD_FAIL").trim());

							test.log(LogStatus.FAIL, currentTestStep + ". Action=" + sActionKeyword + ", Object Name="
									+ sObjectName + ", data= " + sTestData + "");

							ExcelUtils.setCellDataValue(settings.get("KEYWORD_FAIL").trim(), iTestStep,
									Integer.parseInt(settings.get("Col_TestStepResult").trim()),
									settings.get("Path_TestData").trim(), sTestScenarioID.trim());

							testCaseResult = false;
							break;

						} else {
							Log.info(currentTestStep + ". Action=" + sActionKeyword + ", Object Name=" + sObjectName
									+ ", data= " + sTestData + "");
							testStepList.add(currentTestStep + ". Action=" + sActionKeyword + ", Object Name="
									+ sObjectName + ", data= " + sTestData + "");
							testStepResultList.add(settings.get("KEYWORD_PASS").trim());

							// test.log(LogStatus.PASS, currentTestStep + ".
							// Action=" + sActionKeyword + ", Object Name="
							// + sObjectName + ", data= " + sTestData + "");

							test.log(LogStatus.PASS, currentTestStep + ".data= " + sTestData + "");

							ExcelUtils.setCellDataValue(settings.get("KEYWORD_PASS").trim(), iTestStep,
									Integer.parseInt(settings.get("Col_TestStepResult").trim()),
									settings.get("Path_TestData").trim(), sTestScenarioID.trim());

							testCaseResult = true;
						}

					}
					if (testCaseResult == false) {
						break;
					}
				}

				if (testCaseResult == true && warningFlag == false) {
					ExcelUtils.setCellDataValue(settings.get("KEYWORD_PASS").trim(), (tcRowCount),
							Integer.parseInt(settings.get("Col_Result").trim()), settings.get("Path_TestData").trim(),
							settings.get("Sheet_TestCases").trim());

					// ActionKeywords.captureScreenshot(testPrj, sTestCaseID,
					// settings.get("KEYWORD_PASS").trim());
					testStepList.add("Test Case " + sTestCaseID + " is " + settings.get("KEYWORD_PASS").trim());
					testStepResultList.add(settings.get("KEYWORD_PASS").trim());

					test.log(LogStatus.PASS, "Test Case " + sTestCaseID + " is " + settings.get("KEYWORD_PASS").trim());
					Log.info("Test Case " + sTestCaseID + " is " + settings.get("KEYWORD_PASS").trim());

					if (settings.get("Testlink_integration").trim().equalsIgnoreCase("Yes")
							&& settings.get("Testlink_Result_Update").trim().equalsIgnoreCase("Yes")) {
						TestLinkIntegration.reportResult(testPrj, testPlan, sTestCaseID, testBuild,
								(testStepList.toString()), TestLinkAPIResults.TEST_PASSED);
					}

				} else {
					ExcelUtils.setCellDataValue(settings.get("KEYWORD_FAIL").trim(), (tcRowCount),
							Integer.parseInt(settings.get("Col_Result").trim()), settings.get("Path_TestData").trim(),
							settings.get("Sheet_TestCases").trim());

					ActionKeywords.captureScreenshot(testPrj, sTestCaseID, settings.get("KEYWORD_FAIL").trim());
					test.log(LogStatus.FAIL, "Test Case " + sTestCaseID + " is " + settings.get("KEYWORD_FAIL").trim());
					Log.info("Test Case " + sTestCaseID + " is " + settings.get("KEYWORD_FAIL").trim());
					testStepList.add("Test Case " + sTestCaseID + " is " + settings.get("KEYWORD_FAIL").trim());
					testStepResultList.add(settings.get("KEYWORD_FAIL").trim());

					if (settings.get("buzilla_integration").trim().equalsIgnoreCase("yes")
							|| settings.get("buzilla_integration").trim().equalsIgnoreCase("Y")) {

						String bugid = BugzillaIntegration.createBug(settings.get("bugURL"),
								settings.get("bugUsername"), settings.get("bugPwd"), settings.get("product"),
								settings.get("component"), (sTestCaseID + " Execution Failed."),
								settings.get("priority"), settings.get("os"), (testStepList.toString()),
								settings.get("version"), settings.get("platform"), Constants.BUG_SCREENSHOTPATH);
						Log.info("Bug ID=" + bugid);
						String bugUrl = settings.get("bugShow") + bugid;
						Log.info("Bug URL=" + bugUrl);
						Log.info("Bug Raised with bug id=" + bugid + " and Bug URL=" + bugUrl);

						testStepList.add("Bug Raised with Bug ID=" + bugid + " and Bug URL=" + bugUrl);
						testStepResultList.add(settings.get("KEYWORD_FAIL").trim());
						test.log(LogStatus.FAIL, "Bug Raised with bug id=" + bugid + " and Bug URL=" + bugUrl);

						ExcelUtils.setCellDataValue("Bug Raised with Bug ID=" + bugid + " and Bug URL=" + bugUrl,
								(tcRowCount), Integer.parseInt(settings.get("Col_Result").trim()),
								settings.get("Path_TestData").trim(), settings.get("Sheet_TestCases").trim());

						if (settings.get("Testlink_integration").trim().equalsIgnoreCase("Yes")
								&& settings.get("Testlink_Result_Update").trim().equalsIgnoreCase("Yes")) {
							TestLinkIntegration.reportResult(testPrj, testPlan, sTestCaseID, testBuild,
									(testStepList.toString()), TestLinkAPIResults.TEST_FAILED);
						}
					} else {
						if (settings.get("Testlink_integration").trim().equalsIgnoreCase("Yes")
								&& settings.get("Testlink_Result_Update").trim().equalsIgnoreCase("Yes")) {
							TestLinkIntegration.reportResult(testPrj, testPlan, sTestCaseID, testBuild,
									(testStepList.toString()), TestLinkAPIResults.TEST_FAILED);
						}
					}
				}
				extent.endTest(test);

				try {
					// Log issue in excel
					// Log.info("Test Case " + sTestCaseID + " Steps are:-");
					String currentStep = "";
					String currentStepStatus = "";
					// String stepNum = "Step ";
					for (int rowCount = 0; rowCount < testStepList.size(); rowCount++) {
						// stepNum = "Step " + (rowCount + 1) + ":";

						currentStep = testStepList.get(rowCount);
						currentStepStatus = testStepResultList.get(rowCount);
						// Log.info(stepNum + currentStep);

						ExcelUtils.setCellDataValue(currentStep, (rowCount + 1),
								Integer.parseInt(settings.get("Col_Report_Step").trim()),
								settings.get("Report_Excel_Path").trim(), sTestCaseID);
						ExcelUtils.setCellDataValue(currentStepStatus, (rowCount + 1),
								Integer.parseInt(settings.get("Col_Report_Status").trim()),
								settings.get("Report_Excel_Path").trim(), sTestCaseID);
					}
				} catch (Exception e) {

				}
				// // To raise bug in excel
				// if (testStepResultList.contains("FAIL")) {
				// int rowCount =
				// ExcelUtils.getRowCount(settings.get("Bug_Excel_Path").trim(),
				// "Bugs");
				// String bugId =
				// ExcelUtils.getCellData(settings.get("Bug_Excel_Path").trim(),
				// (rowCount - 1), 0,
				// "Bugs");
				// bugId = String.valueOf(Integer.parseInt(bugId) + 1);
				//
				// ExcelUtils.setCellDataValue(String.valueOf(bugId), rowCount,
				// 0,
				// settings.get("Bug_Excel_Path").trim(), "Bugs");
				// ExcelUtils.setCellDataValue(testStepList.toString(),
				// rowCount, 1,
				// settings.get("Bug_Excel_Path").trim(), "Bugs");
				// ExcelUtils.setCellDataValue((sTestCaseID), rowCount, 2,
				// settings.get("Bug_Excel_Path").trim(),
				// "Bugs");
				// Log.info("Excel BugId=" + bugId);
				//
				// test.log(LogStatus.ERROR, "Excel BugId=" + bugId);
				// DriverScript.testStepList.add("Excel BugId=" + bugId);
				// DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());
				//
				// }
				Log.endTestCase(sTestCaseID);
			}
		}
	}

	/*
	 * pramod patil Purpose:- To execute the only excel based script execution
	 */
	private void execute_TestCase_Excel() {
		int iTotalTestCases = ExcelUtils.getRowCount(settings.get("Path_TestData").trim(),
				settings.get("Sheet_TestCases").trim());
		// Log.info("Total Test Cases Count =" + (iTotalTestCases - 2));

		// List of all testcases from Test Cases Sheet
		String[][] listAllTCs = ExcelUtils.getDataFromXlsxSheet(settings.get("Path_TestData").trim(),
				settings.get("Sheet_TestCases").trim());

		// Clear previous excel results from the list of test cases
		ExcelUtils.clearResults(settings.get("Path_TestData").trim(), settings.get("Sheet_TestCases").trim(),
				Integer.parseInt(settings.get("Col_Result").trim()), "");

		Log.info("Cleared the previous execution results from " + settings.get("Sheet_TestCases") + " excel sheet.");
		// Can we get all reusable steps here?? and avoid the each time reading
		// for excel--Pramod check

		String sTestCaseID;
		String sRunMode;
		String sTestScenarioID;
		String sTestDataDriven;
		String sTestDescription;
		for (int iTestcase = 1; iTestcase < iTotalTestCases; iTestcase++) {
			testCaseResult = false;
			sTestCaseID = listAllTCs[iTestcase][Integer.parseInt(settings.get("Col_TestCaseID").trim())];// ExcelUtils.getCellData(iTestcase,
																											// Integer.parseInt(settings.get("Col_TestCaseID").trim()),
																											// settings.get("Sheet_TestCases").trim());
			sRunMode = listAllTCs[iTestcase][Integer.parseInt(settings.get("Col_RunMode").trim())]; // ExcelUtils.getCellData(iTestcase,
																									// Integer.parseInt(settings.get("Col_RunMode").trim()),
																									// settings.get("Sheet_TestCases").trim());
			sTestScenarioID = listAllTCs[iTestcase][Integer.parseInt(settings.get("Col_TestScenarioID").trim())]; // ExcelUtils.getCellData(iTestcase,Integer.parseInt(settings.get("Col_TestScenarioID").trim()),settings.get("Sheet_TestCases").trim());
			sTestDataDriven = settings.get("Col_TestDataDriven").trim(); // ExcelUtils.getCellData(iTestcase,Integer.parseInt(settings.get("Col_TestDataDriven").trim()),settings.get("Sheet_TestCases").trim());
			sTestDescription = listAllTCs[iTestcase][Integer.parseInt(settings.get("Col_TestDescription").trim())];

			if (sRunMode.equalsIgnoreCase("Yes")) {
				try {
					executeSelectedTestCase(sTestCaseID, "", "", "", sTestScenarioID, sTestDataDriven, iTestcase,
							sTestDescription);
				} catch (Exception e) {
					Log.error(
							"Class ActionKeywords | Method execute_TestCase_Excel | Exception desc :" + e.getMessage());
					DriverScript.test.log(LogStatus.FAIL,
							"Class ActionKeywords | Method execute_TestCase_Excel | Exception desc :" + e.getMessage());
					DriverScript.testStepList.add(
							"Class ActionKeywords | Method execute_TestCase_Excel | Exception desc :" + e.getMessage());
					DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());
					DriverScript.testStepsResult = false;
				}
			}

		}
	}

	/*
	 * pramod patil Purpose:-To execute the keyword actions
	 */
	private static void execute_Actions(String sTestCaseID, String sActionKeyword, String sObjectType,
			String sObjectName, String sTestData) {
		try {
			boolean methodFound = false;
			for (int i = 0; i < method.length; i++) {
				// Log.info("#method[i].getName() --:"+ method[i].getName());
				if (method[i].getName().equals(sActionKeyword)) {

					if (sActionKeyword.equalsIgnoreCase("takeScreenshot")) {
						sObjectName = sTestCaseID;
						if (settings.get("SCREENSHOT_CAPTURE").trim().equalsIgnoreCase("no")) {
							methodFound = true;
							testStepsResult = true;
							break;
						}
					}
					method[i].invoke(actionKeywords, sObjectType, sObjectName, sTestData);
					methodFound = true;
					break;
				}
			}
			if (methodFound == false) {
				testStepsResult = false;
				Log.error("Class DriverScript | Method execute_Actions | Exception desc : Action Keyword="
						+ sActionKeyword + " not found.");

				test.log(LogStatus.ERROR,
						"Class DriverScript | Method execute_Actions | Exception desc : Action Keyword="
								+ sActionKeyword + " not found.");
				DriverScript.testStepList
						.add("Class DriverScript | Method execute_Actions | Exception desc : Action Keyword="
								+ sActionKeyword + " not found.");
				DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());

			}
		} catch (Exception e) {
			testStepsResult = false;
			Log.error("Class Utils | Method execute_Actions | Exception desc : " + e.getMessage());
			test.log(LogStatus.ERROR, "Class Utils | Method execute_Actions | Exception desc : " + e.getMessage());
			DriverScript.testStepList.add("Class Utils | Method execute_Actions | Exception desc : " + e.getMessage());
			DriverScript.testStepResultList.add(DriverScript.settings.get("KEYWORD_FAIL").trim());
		}
	}

	private static class JVMShutdownHookThread extends Thread {
		public void run() {
			System.out.println("JVM Shutdown Hook : Clean up resources before graceful shutdown");
			// ActionKeywords.closeCurrentBrowser("", "", "");
			// Close the Extent report instance
			extent.flush();

			// Copy Report
			ActionKeywords.copyReport();

			// To backup the logs and reports to Build folder
			ActionKeywords.backupLogs(Constants.BUILD_NAME);
			Log.info("Logs and Report Backup successfull. Location=" + Constants.BUILD_NAME);
			// To Create the Zip file of the Execution report (helps when we
			// sent
			// mail attachement)
			Zip.createZipReport(Constants.BUILD_PATH + ".zip", Constants.BUILD_PATH + "\\");
			Log.info("Converted Logs and Report to zip successfully. Location=" + Constants.BUILD_PATH + ".zip");
			// Log.info("Ending Automation Test Execution");

			// sent mail
			if (settings.get("SentMail").trim().equalsIgnoreCase("Yes")) {
				String subjectEmail = settings.get("subjectEmail").trim();
				String mailBody = settings.get("mailBody").trim();
				String reportAttancment = new File(Constants.BUILD_PATH + ".zip").getAbsolutePath();
				String toMail = settings.get("toMail").trim();
				SentMail.sentReport(subjectEmail, mailBody, reportAttancment, toMail);

			}
			Log.info("--------------- End Automation Test Execution -------------------");
		}
	}
}
