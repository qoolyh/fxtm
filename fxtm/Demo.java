package fxtm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;


public class Demo {
	private static String firefox_download_folder;
	private static String webDriverName;
	private static String firefoxPath;
	private static String server_url;
	
	static {
		Properties prop = new Properties();
		String path = Prepare.class.getResource("") + "Firefox.properties";
		String filePath = path.substring(6, path.length());
		String pPath = filePath.replace("%20", " ");
		FileInputStream fis;
		try {
			fis = new FileInputStream(pPath);
			prop.load(fis);
			firefox_download_folder = prop
					.getProperty("firefox_download_folder");
			webDriverName = prop.getProperty("webDriverName");
			firefoxPath = prop.getProperty("firefoxPath");
			server_url = prop.getProperty("server_url");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void createTableAndRunSQL(String dbName,String sql_path) throws IOException{
		Connection conn = DBConnect.getConnect(); // create a connection

		// name should be the same as the dbname in getelem.js file
		Util.createTable(dbName, conn); // create a table in database

		String sql = "";
		UnicodeReader read;
		read = new UnicodeReader(new FileInputStream(sql_path), Charset
				.defaultCharset().name());
		BufferedReader bufferedReader = new BufferedReader(read);
		String lineTxt = null;
		Boolean flag = true;
		while ((lineTxt = bufferedReader.readLine()) != null) {
				sql += lineTxt;
		}
		Util.update(sql); // execute sql
	}
	

	public static void test_dividePage(String src, String xampp_folder) throws IOException {
		//If you wanna use dividePage, like this:

		Connection conn = DBConnect.getConnect();
		
		Util.dividePage(src, "img_training/" + src + ".png", xampp_folder, conn);
		Util.copyFile(new File("img_training/"+src+".png"), new File(xampp_folder +'/'+ src + ".png"));
		String ref = "ref"+src.substring(src.indexOf("tar")+3);
		Util.copyFile(new File("img_training/"+src+".png"), new File(xampp_folder +'/'+ ref + ".png"));
	}

	
	    
	
	public static void main(String[] args) throws IOException, SQLException {	
		
		int id[] = {9};
		for(int i=0;i<id.length;i++) {
		String dbName = "ref"+id[i]; // define the database name, note that the
		String sql_path = "H://sql/"+dbName+".sql"; // that's the sql you get from getelem.js
		createTableAndRunSQL(dbName, sql_path); //use this for creating a table in database and running the sql file you download
		
		String src_url = "http://localhost/crt_webpages/"+dbName+"/"+ dbName /*dbName.substring(3)*/ +".html";
		// The following will get a snapshot
		Util.snapping(src_url, "img_training/", dbName+".png", webDriverName, firefoxPath);
		test_dividePage(dbName, server_url+"/img");// get a set of snapshots of all visible elements.
		}
	
	}

}
