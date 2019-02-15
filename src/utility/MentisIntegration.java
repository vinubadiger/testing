package utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class MentisIntegration {
	public static void testCreateMentisIssue(String testCaseId) {

		/*
		 * if(epic=="Register") { response=null; }
		 */
		StringBuffer jsonString = new StringBuffer();

		URL url;
		
		String payLoad = "{\r\n  \"summary\": \"This is a Automation test issue Web Portal  failed.\",\r\n  \"description\": \""+testCaseId+"\",\r\n  \"category\": {\r\n    \"name\": \"General\"\r\n  },\r\n  \"project\": {\r\n    \"name\": \"MyProject\"\r\n  }\r\n}";
		
		System.out.println("payLoad="+payLoad);
		

		try {
			url = new URL("https://opussoftware.mantishub.io/api/rest/issues");
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", "ncEjmUeOt7nuznrnA4CeuvtKuufduVbQ");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			writer.write(payLoad);
			writer.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			jsonString.delete(0, jsonString.length());
			String line;
			while ((line = br.readLine()) != null) {
				jsonString.append(line);
				System.out.println("connectiong " + jsonString.append(line));
			}
			br.close();
			connection.disconnect();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		//System.out.println("jason val--" + jsonString.toString());
	}

}
