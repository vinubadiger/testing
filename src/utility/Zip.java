package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import executionEngine.DriverScript;

public class Zip {

	public static void main(String[] args) {
		createZipReport("D:\\Pam\\LatestWorkspace\\Opus_Keyward_Framework\\27Dec2016\\Build_27Dec201617_12_51.zip","D:\\Pam\\LatestWorkspace\\Opus_Keyward_Framework\\27Dec2016\\Build_27Dec201617_12_51\\");
	}

	public static void createZipReport(String zipName,String zipFolderPath)
	{
		try {
			FileOutputStream fos = new FileOutputStream(zipName);
			ZipOutputStream zos = new ZipOutputStream(fos);
			addDirToZipArchive(zos,new File(zipFolderPath),null);
			zos.flush();
			fos.flush();
			zos.close();
			fos.close();
		} catch (Exception e) {
			DriverScript.testStepsResult = false;
			Log.error("Class Utils | Method createZipReport | Exception desc : " + e.getMessage());
		}
	}
	
	public static void addDirToZipArchive(ZipOutputStream zos, File fileToZip, String parrentDirectoryName)
			throws Exception {
		if (fileToZip == null || !fileToZip.exists()) {
			return;
		}

		String zipEntryName = fileToZip.getName();
		if (parrentDirectoryName != null && !parrentDirectoryName.isEmpty()) {
			zipEntryName = parrentDirectoryName + "/" + fileToZip.getName();
		}

		if (fileToZip.isDirectory()) {
			//Log.info("+" + zipEntryName);
			for (File file : fileToZip.listFiles()) {
				addDirToZipArchive(zos, file, zipEntryName);
			}
		} else {
			//Log.info("   " + zipEntryName);
			byte[] buffer = new byte[1024];
			FileInputStream fis = new FileInputStream(fileToZip);
			zos.putNextEntry(new ZipEntry(zipEntryName));
			int length;
			while ((length = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}
			zos.closeEntry();
			fis.close();
		}
	}
	
	

	public static void copyFolder(File source, File destination)
	{
	    if (source.isDirectory())
	    {
	        if (!destination.exists())
	        {
	            destination.mkdirs();
	        }

	        String files[] = source.list();

	        for (String file : files)
	        {
	            File srcFile = new File(source, file);
	            File destFile = new File(destination, file);

	            copyFolder(srcFile, destFile);
	        }
	    }
	    else
	    {
	    	FileInputStream in = null;
	        FileOutputStream out = null;

	        try
	        {
	            in = new FileInputStream(source);
	            out = new FileOutputStream(destination);

	            byte[] buffer = new byte[1024];

	            int length;
	            while ((length = in.read(buffer)) > 0)
	            {
	                out.write(buffer, 0, length);
	            }
	        }
	        catch (Exception e)
	        {
	            try
	            {
	                in.close();
	            }
	            catch (IOException e1)
	            {
	                e1.printStackTrace();
	            }

	            try
	            {
	                out.close();
	            }
	            catch (IOException e1)
	            {
	                e1.printStackTrace();
	            }
	        }
	    }
	}


}
