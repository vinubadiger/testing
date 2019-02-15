package utility;
//
//import java.io.ByteArrayOutputStream;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.util.Date;
//
//import javax.tools.JavaCompiler;
//import javax.tools.ToolProvider;
//
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.openqa.selenium.By;
//import org.openqa.selenium.Keys;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.firefox.FirefoxDriver;
//import org.openqa.selenium.ie.InternetExplorerDriver;
//import org.testng.annotations.Test;
//
//import java.io.*;
//import java.util.*;
//import java.lang.reflect.*;
//
//
//import executionEngine.DriverScript;
//
//
public class XpathExpression {
//
//	Date today = new Date();
//	  String todayMillis = Long.toString(today.getTime());
//	  String todayClass = "z_" + todayMillis;
//	  String todaySource = todayClass + ".java";
//	  
//	public static void main(String[] args) {
//
//		XpathExpression mtc = new XpathExpression();
//	    mtc.createIt();
////	    if (mtc.compileIt()) {
////	       System.out.println("Running " + mtc.todayClass + ":\n\n");
////	       mtc.runIt();
////	       }
////	    else
////	       System.out.println(mtc.todaySource + " is bad.");
//	    }
//	
//	 public void createIt() {
//		    try {
//		      FileWriter aWriter = new FileWriter(todaySource, true);
//		      
//		      aWriter.write("import org.testng.annotations.Test;");
//		      aWriter.write("public class "+ todayClass + "{");
//		      aWriter.write("@Test()");
//		      aWriter.write(" public void doit() {");
//		      aWriter.write(" System.out.println(\""+todayMillis+"\");");
//		      aWriter.write(" }}\n");
//		      aWriter.flush();      
//		      aWriter.close();
//		      }
//		    catch(Exception e){
//		      e.printStackTrace();
//		      }
//		    }
//
////		  public boolean compileIt() {
////		    String [] source = { new String(todaySource)};
////		    ByteArrayOutputStream baos= new ByteArrayOutputStream();
////		    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
////		    //compiler.run(baos, source[0], "",source);
////		    
////		    //new sun.tools.javac.Main(baos,source[0]).compile(source);
////		    // if using JDK >= 1.3 then use
////		    //   public static int com.sun.tools.javac.Main.compile(source);    
////		    return (baos.toString().indexOf("error")==-1);
////		    }
////
////		  public void runIt() {
////		    try {
////		      Class params[] = {};
////		      Object paramsObj[] = {};
////		      Class thisClass = Class.forName(todayClass);
////		      Object iClass = thisClass.newInstance();
////		      Method thisMethod = thisClass.getDeclaredMethod("doit", params);
////		      thisMethod.invoke(iClass, paramsObj);
////		      }
////		    catch (Exception e) {
////		      e.printStackTrace();
////		      }
////		    }
////
////    
}