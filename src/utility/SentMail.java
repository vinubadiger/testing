package utility;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import executionEngine.DriverScript;

import javax.activation.*;

public class SentMail {

	public static void main(String[] args) {
		
	}
	
	public static void sentReport(String subjectEmail,String mailBody, String reportAttancment,String toMail)
	{

		// Recipient's email ID needs to be mentioned.
		String splitToMail[] = toMail.split(",");

		// Sender's email ID needs to be mentioned
		String from = DriverScript.settings.get("fromMail");

		// Assuming you are sending email from localhost
		// String host ="outlook.office365.com";
		String host = DriverScript.settings.get("fromHost");
		String port = DriverScript.settings.get("fromPort");
		String userName = DriverScript.settings.get("fromMail");
		String password = DriverScript.settings.get("fromPwd");

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.smtp.host", host);
		properties.setProperty("mail.smtp.port", port);
		properties.setProperty("mail.smtp.user", userName);
		properties.setProperty("mail.smtp.password", password);

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		});

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			
			 //String[] mailAddressTo= null;  //can put multiple receivers in the array
			 message.setFrom(new InternetAddress(from));
			 InternetAddress[] mailAddress_TO = new InternetAddress[splitToMail.length] ;
			 for(int i=0;i<splitToMail.length;i++){
				 mailAddress_TO[i] = new InternetAddress(splitToMail[i]);
			 }
			 message.addRecipients(Message.RecipientType.TO, mailAddress_TO);
			         
			// Set Subject: header field
			message.setSubject(subjectEmail,"UTF-8");

			
			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();

			// Fill the message
			messageBodyPart.setText(mailBody);
			
			// Create a multipar message
			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			// Part two is attachment
			messageBodyPart = new MimeBodyPart();
			//String filename = "D:/FrameworkHistory/V1/subloop/Resources/Settings.properties";
			String filename=reportAttancment;
			DataSource source = new FileDataSource(filename);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(filename);
			multipart.addBodyPart(messageBodyPart);

			// Send the complete message parts
			message.setContent(multipart);

			// Send message
			Transport.send(message);
			
			Log.info("Report mailed sent successfully.");
		} catch (Exception mex) {
			Log.info("Failed to email the report.Error="+mex.getMessage());		
		}
	
	}
}