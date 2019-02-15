package utility;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.j2bugzilla.base.Attachment;
import com.j2bugzilla.base.AttachmentFactory;
import com.j2bugzilla.base.Bug;
import com.j2bugzilla.base.BugFactory;
import com.j2bugzilla.base.BugzillaConnector;
import com.j2bugzilla.rpc.AddAttachment;
import com.j2bugzilla.rpc.LogIn;
import com.j2bugzilla.rpc.ReportBug;

import executionEngine.DriverScript;

public class BugzillaIntegration {

	public static String createBug(String bugURL, String username, String pwd, String product, String component,
			String summary, String priority, String os, String desc, String version, String platform,String attachmentPath) {
		Log.info("Creating Bug in Bugzilla:-");
		String bugId = "";
		try {
			BugzillaConnector conn = new BugzillaConnector();
			BugFactory factory = new BugFactory();
			conn.connectTo(bugURL);
			LogIn logIn = new LogIn(username, pwd);
			conn.executeMethod(logIn);
			Log.info("Connected to Bugzilla.");

			Bug bug = factory.newBug().setOperatingSystem(os).setComponent(component).setProduct(product)
					.setSummary(summary).setVersion(version).setDescription(desc).setPlatform(platform)
					.setPriority(priority).createBug();

			ReportBug report = new ReportBug(bug);
			conn.executeMethod(report);

			bugId = Integer.toString(report.getID());

			Log.info("Bug Created in Bugzilla. Bug Id=" + bugId);

			//Attachment
			 byte[] data = getImagebyteArray(attachmentPath);
		
			 AttachmentFactory attachmentFactory = new AttachmentFactory();
			 Attachment attachment = attachmentFactory.newAttachment()
					 .setData(data)
					 .setMime("image/jpeg")
					 .setSummary(bugId)
					 .setBugID(Integer.parseInt(bugId))
					 .setName(bugId)
					 .createAttachment();
			
			 AddAttachment add = new AddAttachment(attachment,Integer.parseInt(bugId));
			 conn.executeMethod(add);

		} catch (Exception e) {
			Log.error("Class BugzillaIntegration | Method createBug | Exception desc : " + e.getMessage());
			DriverScript.testCaseResult = false;
		}
		return bugId;
	}

	private static byte[] getImagebyteArray(String filePath)
	 {
	 //File fi = new File("myfile.jpg");
	 byte[] fileContent = null;
	 try {
	
	 ByteArrayOutputStream baos=new ByteArrayOutputStream(1000);
	 BufferedImage img=ImageIO.read(new File(filePath));
	 ImageIO.write(img, "jpg", baos);
	 baos.flush();
	
	 fileContent=baos.toByteArray();
	
	// String base64String=Base64.encode(baos.toByteArray());
	 baos.close();
	
	// byte[] bytearray = Base64.decode(base64String);
	
	// BufferedImage imag=ImageIO.read(new ByteArrayInputStream(bytearray));
	// ImageIO.write(imag, "jpg", new File(dirName,"snap.jpg"));
	 } catch (Exception e) {
	 Log.error("Class BugzillaIntegration | Method getImagebyteArray | Exception desc : " + e.getMessage());
	 }
	 return fileContent;
	 }
}
