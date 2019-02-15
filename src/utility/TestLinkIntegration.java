package utility;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.xml.DOMConfigurator;

import config.ActionKeywords;
import executionEngine.DriverScript;
import testlink.api.java.client.TestLinkAPIClient;

public class TestLinkIntegration {

	// public static String DEVKEY = "cef8c614d9679d78431f489622e776e1";
	// public static String URL =
	// "http://localhost/testlink/lib/api/xmlrpc/v1/xmlrpc.php";

	static String testPrj = "";
	static String testPlan = "";
	// public static HashMap<String, String> settings = new HashMap<>();

	public static void reportResult(String TestProject, String TestPlan, String Testcase, String Build, String Notes,
			String Result) {
		try {
			TestLinkAPIClient api = new TestLinkAPIClient(DriverScript.settings.get("TestLinkDEVKEY"),
					DriverScript.settings.get("TestLinkURL"));
			api.reportTestCaseResult(TestProject, TestPlan, Testcase, Build, Notes, Result);
			ActionKeywords.captureScreenshot(TestProject, Testcase, Result);
		} catch (Exception e) {
			Log.error("Class Utils | Method reportResult | Exception desc : " + e.getMessage());
			//DriverScript.testCaseResult = false;
		}
	}

	/**
	 * @Purpose To Get the Suited Ids
	 * @param apiClient
	 * @param testProject
	 * @param testPlan
	 * @return
	 */
	@SuppressWarnings("null")
	public static List<String> getProjectSuiteIds(TestLinkAPIClient apiClient, String testProject, String testPlan) {
		List<String> suiteId = new ArrayList<>();

		try {
			int sizeOfSuiteList = apiClient.getTestSuitesForTestPlan(testProject, testPlan).size();
			if (sizeOfSuiteList > 0) {
				for (int count = 0; count < sizeOfSuiteList; count++) {
					suiteId.add(apiClient.getTestSuitesForTestPlan(testProject, testPlan).getData(count).get("id")
							.toString());
				}
			} else {
				Log.warn("No Test Suite Available for the project=" + testProject + " and plan =" + testPlan);
				DriverScript.testCaseResult = false;
			}

		} catch (Exception e) {
			Log.error("Class Utils | Method getProjectSuiteIds | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}
		return suiteId;
	}

	/**
	 * @Purpose To get Project Names
	 * @param apiClient
	 * @param testProject
	 * @param testPlan
	 * @return
	 */
	@SuppressWarnings("null")
	public static List<String> getProjectSuiteNames(TestLinkAPIClient apiClient, String testProject, String testPlan) {
		List<String> suiteList = new ArrayList<>();
		try {
			int sizeOfSuiteList = apiClient.getTestSuitesForTestPlan(testProject.trim(), testPlan.trim()).size();
			if (sizeOfSuiteList > 0) {
				for (int count = 0; count < sizeOfSuiteList; count++) {
					suiteList.add(apiClient.getTestSuitesForTestPlan(testProject, testPlan).getData(count).get("name")
							.toString());
				}
			} else {
				Log.warn("No Test Suite Available for the project=" + testProject + " and plan =" + testPlan);
				DriverScript.testCaseResult = false;
			}

		} catch (Exception e) {
			Log.error("Class Utils | Method getProjectSuiteNames | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}
		return suiteList;
	}

	/**
	 * @Purpose To get Project Names
	 * @param apiClient
	 * @param testProject
	 * @param testPlan
	 * @return
	 */
	@SuppressWarnings("null")
	public static List<String> getProjectNames(TestLinkAPIClient apiClient) {
		List<String> projectNameList = new ArrayList<>();
		try {
			int sizeOfProjectList = apiClient.getProjects().size();
			if (sizeOfProjectList > 0) {
				for (int count = 0; count < sizeOfProjectList; count++) {
					projectNameList.add(apiClient.getProjects().getData(count).get("name").toString());
				}
			} else {
				Log.error("Test project not found.");
				DriverScript.testCaseResult = false;
			}

		} catch (Exception e) {
			Log.error("Class Utils | Method getProjectNames | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}
		return projectNameList;
	}

	/**
	 * @Purpose To get Project Ids
	 * @param apiClient
	 * @param testProject
	 * @param testPlan
	 * @return
	 */
	@SuppressWarnings("null")
	public static List<String> getProjectIds(TestLinkAPIClient apiClient) {
		List<String> projectIdList = new ArrayList<>();
		try {
			int sizeOfProjectList = apiClient.getProjects().size();
			if (sizeOfProjectList > 0) {
				for (int count = 0; count < sizeOfProjectList; count++) {
					projectIdList.add(apiClient.getProjects().getData(count).get("id").toString());
				}
			} else {
				Log.warn("No Test project found.");
				DriverScript.testCaseResult = false;
			}

		} catch (Exception e) {
			Log.error("Class Utils | Method getProjectIds | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}
		return projectIdList;
	}

	public static List<String> getTestCaseNames(TestLinkAPIClient apiClient, String testProjectId, String testSuiteid) {
		List<String> testCaseNameList = new ArrayList<>();
		try {
			int sizeOfTestCaseNameList = apiClient
					.getCasesForTestSuite(Integer.parseInt(testProjectId), Integer.parseInt(testSuiteid)).size();
			if (sizeOfTestCaseNameList > 0) {
				for (int count = 0; count < sizeOfTestCaseNameList; count++) {
					testCaseNameList.add(apiClient
							.getCasesForTestSuite(Integer.parseInt(testProjectId), Integer.parseInt(testSuiteid))
							.getData(count).get("name").toString());
				}
			} else {
				Log.warn(
						"No Test Cases Available for the project id=" + testProjectId + " and Suite id=" + testSuiteid);
			}

		} catch (Exception e) {
			Log.error("Class Utils | Method getTestCaseNames | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}

		return testCaseNameList;
	}

	public static List<String> getTestCaseIds(TestLinkAPIClient apiClient, String testProjectId, String testSuiteid) {
		List<String> testCaseIdList = new ArrayList<>();
		try {
			int sizeOfTestCaseIdList = apiClient
					.getCasesForTestSuite(Integer.parseInt(testProjectId), Integer.parseInt(testSuiteid)).size();
			if (sizeOfTestCaseIdList > 0) {
				for (int count = 0; count < sizeOfTestCaseIdList; count++) {
					testCaseIdList.add(apiClient
							.getCasesForTestSuite(Integer.parseInt(testProjectId), Integer.parseInt(testSuiteid))
							.getData(count).get("id").toString());
				}
			} else {
				Log.warn(
						"No Test Cases Available for the project id=" + testProjectId + " and Suite id=" + testSuiteid);
				DriverScript.testCaseResult = false;
			}

		} catch (Exception e) {
			Log.error("Class Utils | Method getTestCaseIds | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}

		return testCaseIdList;
	}

	public static List<String> getTestCaseExternalIds(TestLinkAPIClient apiClient, String testProjectId,
			String testSuiteid) {
		List<String> testCaseExternalIdList = new ArrayList<>();
		try {
			int sizeOfTestCaseExternalIdList = apiClient
					.getCasesForTestSuite(Integer.parseInt(testProjectId), Integer.parseInt(testSuiteid)).size();
			if (sizeOfTestCaseExternalIdList > 0) {
				for (int count = 0; count < sizeOfTestCaseExternalIdList; count++) {
					testCaseExternalIdList
							.add(apiClient
									.getCasesForTestSuite(Integer.parseInt(testProjectId),
											Integer.parseInt(testSuiteid))
									.getData(count).get("external_id").toString());
				}
			} else {
				Log.warn(
						"No Test Cases Available for the project id=" + testProjectId + " and Suite id=" + testSuiteid);
				DriverScript.testCaseResult = false;
			}

		} catch (Exception e) {
			Log.error("Class Utils | Method getTestCaseExternalIds | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}

		return testCaseExternalIdList;
	}

	/**
	 * @Purpose To get the Project Plan name
	 * @param apiClient
	 * @param projectName
	 * @return
	 */
	public static List<String> getPlanNames(TestLinkAPIClient apiClient, String projectName) {
		List<String> testPlanNameList = new ArrayList<>();
		try {
			int sizeOfTestPlanNameList = apiClient.getProjectTestPlans(projectName).size();
			if (sizeOfTestPlanNameList > 0) {
				for (int count = 0; count < sizeOfTestPlanNameList; count++) {
					testPlanNameList
							.add(apiClient.getProjectTestPlans(projectName).getData(count).get("name").toString());
				}
			} else {
				Log.warn("No Test Plan Available for the project =" + projectName + ".");
				DriverScript.testCaseResult = false;
			}

		} catch (Exception e) {
			Log.error("Class Utils | Method getPlanNames | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}

		return testPlanNameList;
	}

	/**
	 * @Purpose To get the Project Plan Id
	 * @param apiClient
	 * @param projectName
	 * @return
	 */
	public static List<String> getPlanIds(TestLinkAPIClient apiClient, String projectName) {
		List<String> testPlanIdList = new ArrayList<>();
		try {
			int sizeOfTestPlanIdList = apiClient.getProjectTestPlans(projectName).size();
			if (sizeOfTestPlanIdList > 0) {
				for (int count = 0; count < sizeOfTestPlanIdList; count++) {
					testPlanIdList.add(apiClient.getProjectTestPlans(projectName).getData(count).get("id").toString());
				}
			} else {
				Log.warn("No Test Plan Available for the project =" + projectName + ".");
				DriverScript.testCaseResult = false;
			}

		} catch (Exception e) {
			Log.error("Class Utils | Method getPlanIds | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}

		return testPlanIdList;
	}

	/**
	 * @Purpose To get the Project build Id for the specified Plan
	 * @param apiClient
	 * @param projectName
	 * @return
	 */
	public static List<String> getBuildIdsForTestPlan(TestLinkAPIClient apiClient, String projectName,
			String planName) {
		List<String> testBuildIdList = new ArrayList<>();
		try {
			int sizeOfTestBildIdList = apiClient.getBuildsForTestPlan(projectName, planName).size();
			if (sizeOfTestBildIdList > 0) {
				for (int count = 0; count < sizeOfTestBildIdList; count++) {
					testBuildIdList.add(
							apiClient.getBuildsForTestPlan(projectName, planName).getData(count).get("id").toString());
				}
			} else {
				Log.warn("No Test Build available for the Plan=" + planName + ".");
				DriverScript.testCaseResult = false;
			}

		} catch (Exception e) {
			Log.error("Class Utils | Method getBuildIdsForTestPlan | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}

		return testBuildIdList;
	}

	/**
	 * @Purpose To get the Project build Id for the specified Plan
	 * @param apiClient
	 * @param projectName
	 * @return
	 */
	public static List<String> getBuildNamesForTestPlan(TestLinkAPIClient apiClient, String projectName,
			String planName) {
		List<String> testBuildIdList = new ArrayList<>();
		try {
			int sizeOfTestBildIdList = apiClient.getBuildsForTestPlan(projectName, planName).size();
			if (sizeOfTestBildIdList > 0) {
				for (int count = 0; count < sizeOfTestBildIdList; count++) {
					testBuildIdList.add(apiClient.getBuildsForTestPlan(projectName, planName).getData(count).get("name")
							.toString());
				}
			} else {
				Log.warn("No Test Build available for the Plan=" + planName + ".");
				DriverScript.testCaseResult = false;
			}

		} catch (Exception e) {
			Log.error("Class Utils | Method getBuildNamesForTestPlan | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}

		return testBuildIdList;
	}
//
//	public static void getTestCasesFromTestLink() {
//
//		DOMConfigurator.configure(DriverScript.settings.get("Path_Log4j"));
//
//		TestLinkAPIClient api = new TestLinkAPIClient(DriverScript.settings.get("TestLinkDEVKEY"),
//				DriverScript.settings.get("TestLinkURL"));
//		try {
//			ExcelUtils.setExcelFile(DriverScript.settings.get("Path_TestData").toString().trim());
//
//			int iTotalTestRunner = ExcelUtils.getRowCount(DriverScript.settings.get("Sheet_TestRunner"));
//			int iTestCaseCount = 1;
//			for (int iTestRunner = 1; iTestRunner < iTotalTestRunner; iTestRunner++) {
//				testPrj = ExcelUtils.getCellData(iTestRunner, 0, DriverScript.settings.get("Sheet_TestRunner"));
//				testPlan = ExcelUtils.getCellData(iTestRunner, 1, DriverScript.settings.get("Sheet_TestRunner"));
//				Log.info("Project Name in Data Excel=" + testPrj);
//				Log.info("Project Plan in Data Excel=" + testPlan);
//
//				String testProjectId = null;
//
//				// To get Project name and iD
//				List<String> projectNames = getProjectNames(api);
//				List<String> projectIds = getProjectIds(api);
//				Boolean projectFlag = false;
//
//				for (int i = 0; i < projectNames.size(); i++) {
//					if (projectNames.get(i).trim().equals(testPrj.trim())) {
//						testProjectId = projectIds.get(i);
//						Log.info("Project name=" + testPrj);
//						Log.info("Project Id=" + testProjectId);
//						projectFlag = true;
//
//						// To get Project Plan and id
//						List<String> planNames = getPlanNames(api, testPrj);
//						List<String> planIds = getPlanIds(api, testPrj);
//
//						Boolean planFlag = false;
//						for (int j = 0; j < planNames.size(); j++) {
//
//							if (planNames.get(j).trim().equals(testPlan.trim())) {
//
//								Log.info("Plan Name=" + testPlan);
//								Log.info("Plan Id=" + planIds.get(j));
//								planFlag = true;
//
//								// To get Test buid
//								List<String> buildIds = getBuildIdsForTestPlan(api, testPrj, testPlan);
//								List<String> buildnames = getBuildNamesForTestPlan(api, testPrj, testPlan);
//
//								for (int bld = 0; bld < buildIds.size(); bld++) {
//									Log.info("Build Id=" + buildIds.get(bld));
//									Log.info("Build Name=" + buildnames.get(bld));
//								}
//
//								// To get suite name and id
//								List<String> suiteIds = getProjectSuiteIds(api, testPrj, testPlan);
//								List<String> suiteNames = getProjectSuiteNames(api, testPrj, testPlan);
//
//								for (int c = 0; c < suiteNames.size(); c++) {
//									Log.info("Suite Name=" + suiteNames.get(c));
//									Log.info("Suite Id=" + suiteIds.get(c));
//
//									// To get Testcase Name and id
//									List<String> testcaseNames = getTestCaseNames(api, testProjectId, suiteIds.get(c));
//									List<String> testcaseIds = getTestCaseIds(api, testProjectId, suiteIds.get(c));
//									List<String> testcaseExternalIds = getTestCaseExternalIds(api, testProjectId,
//											suiteIds.get(c));
//
//									for (int d = 0; d < testcaseNames.size(); d++) {
//										Log.info("Test Case Name=" + testcaseNames.get(d));
//										Log.info("Test Case Id=" + testcaseIds.get(d));
//										Log.info("Test Case External Ids=" + testcaseExternalIds.get(d));
//
//										ExcelUtils.setCellData(testcaseExternalIds.get(d).trim(), iTestCaseCount, 0,
//												DriverScript.settings.get("Path_TestData").trim(), "Test Cases");
//										ExcelUtils.setCellData(testcaseNames.get(d).trim(), iTestCaseCount, 1,
//												DriverScript.settings.get("Path_TestData").trim(), "Test Cases");
//
//										iTestCaseCount++;
//
//									}
//								}
//
//							}
//						}
//
//						if (!planFlag) {
//							Log.warn("Test Plan not found.");
//							DriverScript.testCaseResult = false;
//						}
//					}
//				}
//				if (!projectFlag) {
//					Log.warn("Test Project not found.");
//					DriverScript.testCaseResult = false;
//				}
//			}
//
//		} catch (Exception e) {
//			Log.error("Class Utils | Method getTestCasesFromTestLink | Exception desc : " + e.getMessage());
//			DriverScript.testCaseResult = false;
//		}
//	}
//
}
