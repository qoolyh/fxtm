package fxtm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.sql.ResultSet;
import com.opera.core.systems.scope.protos.SelftestProtos.SelftestResult.Result;



public class Prepare {
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

	public static void getHTMLElements(String dbName) {
		addJsToWebFolder(dbName, server_url);
		String sql_location = firefox_download_folder + "/" + dbName + ".sql";
		//you should change this location to your webpage's location
		String webpage_url = "http://localhost/crt_webpages/"
				+ dbName + "/" + dbName + "_sql.html";
		getSQL(webDriverName, firefoxPath, webpage_url, sql_location);
	}

	/**
	 * 1. Copy the webpage from your disk to the server, 2. add a JS calling in HTML file to get elements in the webpage
	 * @param dbName The name of webpage and the database, I prefer use prefix 'tar' and a numeral suffix, e.g. tar10 
	 * @param server_url Declared in the Firefox.properties
	 */
	private static void addJsToWebFolder(String dbName, String server_url) {
		// copy the webpage from the project to server
		try {
			String webpage_folder = "E:/data_crt/";
			//Here I use a relative path, please download the webpages in 'webpages' folder
			Util.copyFolder(webpage_folder+ dbName, server_url);
			String dest = server_url + "/" + dbName;

			// copy 'jsUtil' folder to the corresponding folder
			Util.copyFolder("jsUtil", dest);

			// add <script> element in the HTML file
			String original_html = dest + "/" + dbName.substring(3) + ".html"; // dbName's
																				// format:
																				// 'tar'+number
																				// or
																				// 'ref'+number
			String target_html = dest + "/" + dbName + "_sql.html";
			File s = new File(original_html);
			String charsetS = getCharset(s);
			System.out.println(original_html + "  ----" + charsetS);
			OutputStream outS = new FileOutputStream(target_html);
			OutputStreamWriter newS = new OutputStreamWriter(outS, charsetS);
			OutputStreamWriter tmpS = newS;
			addJS(new File(original_html), tmpS, charsetS, "getElem.js");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void getSQL(String webDriverName, String firefoxPath,
			String webpage_url, String sql_location) {
		//Set system path (for geckoDriver and Firefox)
		System.setProperty("webdriver.gecko.driver", "lib_2/geckodriver.exe");
		System.setProperty(webDriverName, firefoxPath);
		// Declare which profile you will use
		ProfilesIni pi = new ProfilesIni();
		FirefoxProfile profile = pi.getProfile("selenium");
		// Set options of Firefox
		FirefoxOptions opt = new FirefoxOptions();
		opt.setProfile(profile);
		//Initialize the webDriver
		WebDriver driver = new FirefoxDriver(opt);
		driver.manage().window().maximize();
		/*
		 * if you want to shape the screenshot, use this. Dimension dimsn = new
		 * Dimension(1464, 1000); // that will get a snapshot whose width is
		 * 1464 (not for IPS) driver.manage().window().setSize(dimsn);
		 */
		// String tmp = new File(webpage_url).getAbsolutePath();
		driver.get(webpage_url);

		boolean exist = new File(sql_location).exists();
		while (!exist) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			exist = new File(sql_location).exists();
			System.out.println(exist);
		}
		driver.close();

	}

	/**
	 * generate a copy that include the script element in HTML file
	 * 
	 * @param file
	 *            the original HTML file
	 * @param newFile
	 *            a copy of the original that contains the script element
	 * @param charset
	 *            the charset of the original
	 * @param jsName
	 * @throws UnsupportedEncodingException
	 */
	private static void addJS(File file, OutputStreamWriter newFile,
			String charset, String jsName) throws UnsupportedEncodingException {

		if (file.isFile() && file.exists()) { // 判断文件是否存在
			InputStreamReader read;
			System.out.println(file.getAbsolutePath());
			try {
				read = new InputStreamReader(new FileInputStream(file), charset);
				// read = new InputStreamReader(new FileInputStream(file));

				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				Boolean flag = true;
				try {
					while ((lineTxt = bufferedReader.readLine()) != null) {

						if (lineTxt.contains("</body") && flag) {

							String script = "<script type=\"module\" src="
									+ '"' + "./jsUtil/" + jsName + '"'
									+ "></script>";
							newFile.write(script + "\n");
							newFile.write(lineTxt + "\n");
							flag = false;
							newFile.flush();
						} else {
							newFile.write(lineTxt + "\n");
							newFile.flush();
						}

						//

					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					read.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			System.out.println("找不到指定的文件");
		}
	}

	/**
	 * Check if the string declares the charset
	 * 
	 * @param s
	 * @return
	 */
	private static String regCharset(String s) {
		String charset = null;
		Pattern pattern = Pattern.compile("charset=(\\S+?)\"");
		Matcher matcher = pattern.matcher(s);
		if (matcher.find()) {
			charset = matcher.group(1);
		} else {
			charset = "UTF-8";
		}
		return charset;
	}

	/**
	 * Return the charset of the HTML file, only for HTML file cause it declares
	 * the charset property
	 * 
	 * @param file
	 * @return
	 */
	private static String getCharset(File file) {
		String charset = "";
		if (file.isFile() && file.exists()) { // 判断文件是否存在
			InputStreamReader read;
			try {

				read = new InputStreamReader(new FileInputStream(file));

				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				Boolean flag = true;
				try {
					while ((lineTxt = bufferedReader.readLine()) != null) {
						if (lineTxt.contains("<meta")
								&& lineTxt.contains("charset") && flag) {

							charset = regCharset(lineTxt);
							flag = false;
						}

					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					read.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			System.out.println("找不到指定的文件");
		}

		return charset;
	}

	public static void main(String[] args) throws IOException, SQLException {
//		int id[] = {10};
		Connection conn = DBConnect.getConnect();
		int id[] = {52};
		for(int i=0;i<id.length;i++) {
			String db = "tar"+id[i];
			Util.copyTable(db, db+"_ori", conn);		
			String tmpSql = "select * from "+db;
			Vector<Elem> v = Util.getElems(null, tmpSql, conn);
			String sql = "select * from "+db+"_manually";
			ResultSet rs = Util.getitems(sql, conn);
			while(rs.next()) {
				int eid = rs.getInt("ID");
				int cid = rs.getInt("clusterID");
				if(eid<v.size()) {
					System.out.println("update..."+db+" "+eid+"/"+v.size());
					String tmp = "update "+db+" set clusterID="+cid+" where ID="+eid;
					Util.update(tmp);
				}
			}
		}
	}
}
