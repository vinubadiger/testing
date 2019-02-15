package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import executionEngine.DriverScript;

public class ExcelUtils {
	// private static XSSFSheet ExcelWSheet;
	// private static XSSFWorkbook ExcelWBook;
	// private static Cell cell;
	// private static XSSFRow Row;

	// public static void setExcelFile(String Path) throws Exception {
	// try {
	// FileInputStream ExcelFile = new FileInputStream(Path);
	// ExcelWBook = new XSSFWorkbook(ExcelFile);
	// } catch (Exception e) {
	// Log.error("Class Utils | Method setExcelFile | Exception desc : " +
	// e.getMessage());
	// // DriverScript.testCaseResult = false;
	// throw new Exception(e);
	// }
	// }

	/**
	 * 
	 * @Purpose: To get the values for each cell
	 * @param cell
	 */

	// public static String getCellData(int RowNum, int ColNum, String
	// SheetName) {
	// try {
	// ExcelWSheet = ExcelWBook.getSheet(SheetName);
	//
	// Row = ExcelWSheet.getRow(RowNum);
	//
	// if (Row == null) {
	// ExcelWSheet.createRow(RowNum);
	// Row = ExcelWSheet.getRow(RowNum);
	// }
	//
	// cell = ExcelWSheet.getRow(RowNum).getCell(ColNum,
	// Row.RETURN_BLANK_AS_NULL);
	//
	// String CellData = null; // = Cell.getStringCellValue();
	//
	// if (cell != null) {
	// switch (cell.getCellType()) {
	// case Cell.CELL_TYPE_STRING:
	// CellData = cell.toString();
	// break;
	// case Cell.CELL_TYPE_NUMERIC:
	// if (DateUtil.isCellDateFormatted(cell)) {
	// SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	// CellData = dateFormat.format(cell.getDateCellValue());
	// } else {
	// Double value = cell.getNumericCellValue();
	// Long longValue = value.longValue();
	// CellData = new String(longValue.toString());
	// }
	// break;
	// case Cell.CELL_TYPE_BOOLEAN:
	// CellData = new String(new
	// Boolean(cell.getBooleanCellValue()).toString());
	// break;
	// case Cell.CELL_TYPE_BLANK:
	// CellData = "";
	// break;
	// }
	// } else {
	// CellData = "";
	// }
	// return CellData;
	//
	// } catch (Exception e) {
	// Log.error("Class Utils | Method getCellData | Exception desc : " +
	// e.getMessage());
	// DriverScript.testCaseResult = false;
	// return "";
	// }
	// }

	// public static int getRowCount(String SheetName) {
	// int iNumber = 0;
	// try {
	// ExcelWSheet = ExcelWBook.getSheet(SheetName);
	// iNumber = ExcelWSheet.getLastRowNum() + 1;
	// } catch (Exception e) {
	// Log.error("Class Utils | Method getRowCount | Exception desc : " +
	// e.getMessage());
	// DriverScript.testCaseResult = false;
	// }
	// return iNumber;
	// }

	// public static int getRowContains(String sTestCaseName, int colNum, String
	// SheetName) {
	// int iRowNum = 0;
	// try {
	// // ExcelWSheet = ExcelWBook.getSheet(SheetName);
	// int rowCount = ExcelUtils.getRowCount(SheetName);
	// for (; iRowNum < rowCount; iRowNum++) {
	// if (ExcelUtils.getCellData(iRowNum, colNum, SheetName) != null) {
	// if (ExcelUtils.getCellData(iRowNum, colNum,
	// SheetName).equalsIgnoreCase(sTestCaseName)) {
	// break;
	// }
	// } else {
	// break;
	// }
	// }
	// } catch (Exception e) {
	// Log.error("Class Utils | Method getRowContains | Exception desc : " +
	// e.getMessage());
	// DriverScript.testCaseResult = false;
	// }
	// return iRowNum;
	// }

	// public static int getTestStepsCount(String SheetName, String sTestCaseID,
	// int iTestCaseStart) {
	// try {
	// for (int i = iTestCaseStart; i <= ExcelUtils.getRowCount(SheetName); i++)
	// {
	// // changed to 0
	// if (!sTestCaseID.equals(ExcelUtils.getCellData(i, 0, SheetName))) {
	// int number = i;
	// return number;
	// }
	// }
	// ExcelWSheet = ExcelWBook.getSheet(SheetName);
	// int number = ExcelWSheet.getLastRowNum() + 1;
	// return number;
	// } catch (Exception e) {
	// Log.error("Class Utils | Method getRowContains | Exception desc : " +
	// e.getMessage());
	// DriverScript.testCaseResult = false;
	// return 0;
	// }
	// }

	// @SuppressWarnings("static-access")
	// public static void setCellData(String Result, int RowNum, int ColNum,
	// String filePath, String SheetName) {
	// try {
	// ExcelWSheet = ExcelWBook.getSheet(SheetName);
	// Row = ExcelWSheet.getRow(RowNum);
	//
	// if (Row == null) {
	// ExcelWSheet.createRow(RowNum);
	// Row = ExcelWSheet.getRow(RowNum);
	// }
	// cell = Row.getCell(ColNum, Row.RETURN_BLANK_AS_NULL);
	// if (cell == null) {
	// cell = Row.createCell(ColNum);
	// cell.setCellValue(Result);
	// } else {
	// cell.setCellValue(Result);
	// }
	// FileOutputStream fileOut = new FileOutputStream(filePath);
	// ExcelWBook.write(fileOut);
	// // fileOut.flush();
	// fileOut.close();
	// ExcelWBook = new XSSFWorkbook(new FileInputStream(filePath));
	// } catch (Exception e) {
	// Log.error("Class Utils | Method setCellData | Exception desc : " +
	// e.getMessage());
	// DriverScript.testCaseResult = false;
	// }
	// }

	public static HashMap<String, String> readProperty(String filename) {
		HashMap<String, String> config = new HashMap<>();
		try {
			File file = new File(filename);

			if (file.exists()) {
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
			} else {
				System.out.println("Class Utils | Method readProperty | Exception desc : File not found at location="
						+ file.getAbsolutePath());
				Log.error("Class Utils | Method readProperty | Exception desc : File not found at location="
						+ file.getAbsolutePath());
			}
		} catch (Exception e) {
			Log.error("Class Utils | Method readProperty | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}
		return config;
	}

	public static void createExcelFile(String reportExcel) {
		XSSFWorkbook workbook = null;
		try {
			FileOutputStream reportOutFile = new FileOutputStream(new File(reportExcel));
			workbook = new XSSFWorkbook();
			workbook.write(reportOutFile);
			workbook.close();
		} catch (Exception e) {
			try {
				workbook.close();
			} catch (Exception e1) {
				Log.error("Class Utils | Method createExcelFile | Exception desc : " + e.getMessage());
				DriverScript.testCaseResult = false;
			}
			Log.error("Class Utils | Method createExcelFile | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}
	}

	public static void addNewSheet(String reportExcel, String sheetName) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet reportSheet = null;
		try {
			File file = new File(reportExcel);

			if (!file.exists()) {
				reportSheet = workbook.createSheet(sheetName);
			} else {
				XSSFWorkbook currentworkbook = (XSSFWorkbook) WorkbookFactory.create(file);
				workbook = currentworkbook;
				reportSheet = (XSSFSheet) workbook.createSheet(sheetName);
			}

			FileOutputStream reportOutFile = new FileOutputStream(reportExcel, true);
			workbook.write(reportOutFile);
			workbook.close();
			reportOutFile.close();
		} catch (Exception e) {
			try {
				workbook.close();

			} catch (Exception e1) {
				Log.error("Class Utils | Method addNewSheet | Exception desc : " + e.getMessage());
				DriverScript.testCaseResult = false;
			}
			Log.error("Class Utils | Method addNewSheet | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}
	}

	public static void setCellDataValue(String Result, int RowNum, int ColNum, String reportExcel, String sheetName) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet reportSheet = null;
		FileOutputStream reportOutFile = null;
		try {
			File file = new File(reportExcel);

			if (!file.exists()) {
				reportSheet = workbook.createSheet(sheetName);
			} else {
				XSSFWorkbook currentworkbook = new XSSFWorkbook(file);
				workbook = currentworkbook;
			}

			int sheetCount = workbook.getNumberOfSheets();
			String currentSheetName = null;
			for (int loopCount = 0; loopCount < sheetCount; loopCount++) {
				currentSheetName = workbook.getSheetName(loopCount);
				if (currentSheetName.equalsIgnoreCase(sheetName)) {
					XSSFSheet currentreportSheet = workbook.getSheet(currentSheetName);

					XSSFRow row = currentreportSheet.getRow(RowNum);

					if (row == null) {
						currentreportSheet.createRow(RowNum);
						row = currentreportSheet.getRow(RowNum);
					}

					Cell currentCell = row.getCell(ColNum, XSSFRow.RETURN_BLANK_AS_NULL);
					if (currentCell == null) {
						currentCell = row.createCell(ColNum);
						currentCell.setCellValue(Result);
					} else {
						currentCell.setCellValue(Result);
					}

					reportOutFile = new FileOutputStream(reportExcel, true);
					workbook.write(reportOutFile);
				}
			}

			workbook.close();
			reportOutFile.close();
		} catch (Exception e) {
			try {
				workbook.close();

			} catch (Exception e1) {
				Log.error("Class Utils | Method setCellDataValue | Exception desc : " + e.getMessage());
				DriverScript.testCaseResult = false;
			}
			Log.error("Class Utils | Method setCellDataValue | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}
	}

	public static int getRowCount(String xlsxFilePath, String SheetName) {
		int iNumber = 0;
		try {
			FileInputStream file;
			file = new FileInputStream(new File(xlsxFilePath));
			Workbook workbook = new XSSFWorkbook(file);
			Sheet sheet = workbook.getSheet(SheetName);
			iNumber = sheet.getLastRowNum() + 1;
			workbook.close();
			file.close();
		} catch (Exception e) {
			Log.error("Class Utils | Method getRowCount | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}
		return iNumber;
	}

	public static int getColumnCount(String xlsxFilePath, String SheetName) {
		int iNumber = 0;
		try {
			FileInputStream file;
			file = new FileInputStream(new File(xlsxFilePath));
			Workbook workbook = new XSSFWorkbook(file);
			Sheet sheet = workbook.getSheet(SheetName);
			iNumber = sheet.getRow(0).getLastCellNum();
			workbook.close();
			file.close();
		} catch (Exception e) {
			Log.error("Class Utils | Method getColumnCount | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}
		return iNumber;
	}

	// Read from excel
	public static int getRowContains(String xlsxFilePath, String sTestCaseName, int colNum, String SheetName) {
		int iRowNum = 0;
		try {
			// ExcelWSheet = ExcelWBook.getSheet(SheetName);
			int rowCount = getRowCount(xlsxFilePath, SheetName);
			for (; iRowNum < rowCount; iRowNum++) {
				if (getCellData(xlsxFilePath, iRowNum, colNum, SheetName) != null) {
					if (getCellData(xlsxFilePath, iRowNum, colNum, SheetName).equalsIgnoreCase(sTestCaseName)) {
						break;
					}
				} else {
					break;
				}
			}
		} catch (Exception e) {
			Log.error("Class Utils | Method getRowContains | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}
		return iRowNum;
	}

	public static int getRowContains(String[][] testReusableStepsList, String sTestCaseName, int colNum, String trim) {

		int iRowNum = 0;
		try {

			int rowCount = testReusableStepsList.length;

			for (; iRowNum < rowCount; iRowNum++) {
				if (testReusableStepsList[iRowNum][colNum] != null) {
					if (testReusableStepsList[iRowNum][colNum].equalsIgnoreCase(sTestCaseName)) {
						break;
					}
				} else {
					break;
				}
			}
		} catch (Exception e) {
			Log.error("Class Utils | Method getRowContains | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}
		return iRowNum;

	}

	public static int getTestStepsCount(String xlsxFilePath, String SheetName, String sTestCaseID, int iTestCaseStart) {
		try {
			for (int i = iTestCaseStart; i < ExcelUtils.getRowCount(xlsxFilePath, SheetName); i++) {
				// changed to 0
				if (!sTestCaseID.equals(ExcelUtils.getCellData(xlsxFilePath, i, 0, SheetName))) {
					int number = i;
					return number;
				}
			}

			FileInputStream file;
			file = new FileInputStream(new File(xlsxFilePath));
			Workbook workbook = new XSSFWorkbook(file);
			Sheet sheet = workbook.getSheet(SheetName);
			int number = sheet.getLastRowNum() + 1;
			workbook.close();
			file.close();
			return number;
		} catch (Exception e) {
			Log.error("Class Utils | Method getRowContains | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
			return 0;
		}
	}

	public static int getTestStepsCount(String[][] testReusableStepsList, String sTestCaseID, int iTestCaseStart) {
		try {
			int number = iTestCaseStart;
			for (int i = iTestCaseStart; i < testReusableStepsList.length; i++) {
				// changed to 0
				if (!sTestCaseID.equals(testReusableStepsList[i][0])) {
					number = i;
					return number;
				}
			}

			number = testReusableStepsList.length;

			return number;
		} catch (Exception e) {
			Log.error("Class Utils | Method getRowContains | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
			return 0;
		}
	}

	public static String getCellData(String xlsxFilePath, int RowNum, int ColNum, String SheetName) {
		try {

			FileInputStream file;

			file = new FileInputStream(new File(xlsxFilePath));
			Workbook workbook = new XSSFWorkbook(file);
			Sheet sheet = workbook.getSheet(SheetName);

			Cell cell = sheet.getRow(RowNum).getCell(ColNum, XSSFRow.RETURN_BLANK_AS_NULL);

			String CellData = null; // = Cell.getStringCellValue();

			if (cell != null) {
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					CellData = cell.toString();
					break;
				case Cell.CELL_TYPE_NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
						CellData = dateFormat.format(cell.getDateCellValue());
					} else {
						Double value = cell.getNumericCellValue();
						Long longValue = value.longValue();
						CellData = new String(longValue.toString());
					}
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					CellData = new String(new Boolean(cell.getBooleanCellValue()).toString());
					break;
				case Cell.CELL_TYPE_BLANK:
					CellData = "";
					break;
				}
			} else {
				CellData = "";
			}

			workbook.close();
			file.close();
			return CellData;

		} catch (Exception e) {
			Log.error("Class Utils | Method getCellData | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
			return "";
		}
	}

	public static String[][] getDataFromXlsxSheet(String xlsxFilePath, String sheetName) {
		Log.info("Reading xlsx file: " + xlsxFilePath + "");
		String[][] tabArray = null;
		int ci = 0, cj = 0, noOfRow = 0, noOfCols = 0;
		FileInputStream file;
		try {
			if (fileExistAtLocation(xlsxFilePath)) {
				file = new FileInputStream(new File(xlsxFilePath));
				Workbook workbook = new XSSFWorkbook(file);
				if (sheetPresentInExcel(xlsxFilePath, sheetName)) {
					Sheet sheet = workbook.getSheet(sheetName);
					noOfRow = sheet.getLastRowNum();
					// DriverScript.invocationRows=noOfRow;
					noOfCols = sheet.getRow(0).getLastCellNum();
					DriverScript.invocationColumns = noOfCols;
					tabArray = new String[noOfRow + 1][noOfCols];
					for (ci = 0; ci <= noOfRow; ci++) {
						for (cj = 0; cj < noOfCols; cj++) {
							Cell cell = sheet.getRow(ci).getCell(cj, XSSFRow.RETURN_BLANK_AS_NULL);
							tabArray[ci][cj] = getCellValueAsString(cell);
						}
					}
				} else {
					Log.error("Class Utils | Method getDataFromXlsxSheet | Exception desc : Sheet =" + sheetName
							+ " not found in excel=" + xlsxFilePath);
					DriverScript.testCaseResult = false;
				}
				workbook.close();
				file.close();
			} else {
				Log.error(
						"Class Utils | Method getDataFromXlsxSheet | Exception desc : Excel file not found at location="
								+ xlsxFilePath);
				DriverScript.testCaseResult = false;
			}
		} catch (Exception e) {
			Log.error("Class Utils | Method getDataFromXlsxSheet | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;

		}
		// DriverScript.invocationCount=tabArray.length;
		return ((tabArray));
	}

	public static String[][] getDataFromXlsxSheetWithYesMode(String xlsxFilePath, String sheetName) {
		String[][] returnArray = checkOption(getDataFromXlsxSheet(xlsxFilePath, sheetName));
		return returnArray;
	}

	public static String getCellValueAsString(Cell cell) {
		String strCellValue = null;
		if (cell != null) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				strCellValue = cell.toString();
				break;
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
					strCellValue = dateFormat.format(cell.getDateCellValue());
				} else {
					Double value = cell.getNumericCellValue();
					Long longValue = value.longValue();
					strCellValue = new String(longValue.toString());
				}
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				strCellValue = new String(new Boolean(cell.getBooleanCellValue()).toString());
				break;
			// pramod
			case Cell.CELL_TYPE_FORMULA:
				System.out.println("Formula=" + cell.getCellFormula());
				getCellValueAsString(cell);

				switch (cell.getCachedFormulaResultType()) {
				case Cell.CELL_TYPE_STRING:
					strCellValue = cell.toString();
					break;
				case Cell.CELL_TYPE_NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
						strCellValue = dateFormat.format(cell.getDateCellValue());
					} else {
						Double value = cell.getNumericCellValue();
						Long longValue = value.longValue();
						strCellValue = new String(longValue.toString());
					}
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					strCellValue = new String(new Boolean(cell.getBooleanCellValue()).toString());
					break;
				case Cell.CELL_TYPE_BLANK:
					strCellValue = "";
					break;
				}

				break;

			// prmaod
			case Cell.CELL_TYPE_BLANK:
				strCellValue = "";
				break;
			}
		} else {
			strCellValue = "";
		}
		return strCellValue;
	}

	public static void clearResults(String excelPath, String sheetName, int resultColumn, String data) {
		int rowNum = ExcelUtils.getRowCount(excelPath, sheetName);
		for (int iRowNum = 1; iRowNum < rowNum; iRowNum++) {
			ExcelUtils.setCellDataValue(data, iRowNum, resultColumn, excelPath, sheetName);
		}
	}

	public static void createPropertyFile(String fileName) {
		FileOutputStream reportOutFile = null;
		try {

			File file = new File(fileName);
			if (!file.exists()) {
				reportOutFile = new FileOutputStream(new File(fileName));
				reportOutFile.close();
			}
		} catch (Exception e) {
			try {
				reportOutFile.close();
			} catch (Exception e1) {
				Log.error("Class Utils | Method createPropertyFile | Exception desc : " + e.getMessage());
				DriverScript.testCaseResult = false;
			}
			Log.error("Class Utils | Method createPropertyFile | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}
	}

	public static boolean fileExistAtLocation(String filename) {
		File file = new File(filename);
		if (file.exists())
			return true;
		else
			return false;
	}

	public static boolean sheetPresentInExcel(String xlsxFilePath, String sheetName) {
		boolean sheetFound = false;
		try {
			FileInputStream file;
			file = new FileInputStream(new File(xlsxFilePath));
			Workbook workbook = new XSSFWorkbook(file);
			int noOfSheets = workbook.getNumberOfSheets();
			for (int count = 0; count < noOfSheets; count++) {
				Sheet sheet = workbook.getSheetAt(count);
				if (sheet.getSheetName().equals(sheetName)) {
					sheetFound = true;
					break;
				}
			}
			workbook.close();
			file.close();
		} catch (Exception e) {
			Log.error("Class Utils | Method sheetPresentInExcel | Exception desc : " + e.getMessage());
			sheetFound = false;
		}
		return sheetFound;
	}

	public static String[][] checkOption(String xlsData[][]) {
		String[][] tabArray = new String[0][0];
		String data = "";
		int noOfRow = xlsData.length;
		if (noOfRow > 0) {
			int noOfCols = xlsData[0].length;
			int newRows = 0;
			// Calculate no of rows with y option only for second Column.
			for (int ci = 0; ci < noOfRow; ci++) {
				data = xlsData[ci][Integer.parseInt(DriverScript.settings.get("Col_Data_RunMode").trim())];
				if (data.equalsIgnoreCase("yes")) {
					newRows++;
				}
			}
			int rowNo = 0;
			if (newRows > 0) {
				tabArray = new String[newRows][noOfCols];
				for (int ci = 0; ci < noOfRow; ci++) {
					data = xlsData[ci][Integer.parseInt(DriverScript.settings.get("Col_Data_RunMode").trim())];
					if (data.equalsIgnoreCase("yes")) {
						for (int cj = 0; cj < noOfCols; cj++) {
							tabArray[rowNo][cj] = xlsData[ci][cj];
						}
						rowNo++;
					}
				}
			}
			return tabArray;
		} else {
			return tabArray;
		}
	}
}
