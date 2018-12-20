package fxtm;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import org.openqa.selenium.firefox.*;
import javax.imageio.ImageIO;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xml.internal.security.Init;

import org.apache.commons.io.FileUtils;


public class Util {
	/*
	 * 辅助函数，执行查询，返回结果集
	 */
	public static ResultSet getitems(String sql, Connection conn) {
		Statement stmt = null;
		ResultSet rs = null;		
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			 rs = (ResultSet) stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rs;
	}

	/*
	 * 辅助函数，更新在数据库的元素
	 */
	public static void update(String sql) {
		 Connection conn = DBConnect.getConnect();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			stmt.executeUpdate(sql);
			DBConnect.close(conn, stmt, rs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Copy the oriDB to tarDB
	 * @param oriDB
	 * @param tarDB
	 * @param conn
	 * @throws SQLException
	 */
	public static void copyTable(String oriDB,String tarDB,Connection conn) throws SQLException{
		createTable(tarDB, conn);
		String sql = "INSERT INTO "+tarDB+" SELECT * FROM "+oriDB;
		update(sql);
	}
	
	public static Vector<Elem> getElems(String imgName, String sql, Connection conn) throws IOException, SQLException {
		ResultSet rs = getitems(sql, conn);
		Vector<Elem> elements = new Vector<Elem>();
		int width=0,height=0;
		if(imgName==null){
			width=100;height=100;
		}else{
			BufferedImage tmpImg;
			tmpImg = ImageIO.read(new File(imgName));
			width = tmpImg.getWidth();
			height = tmpImg.getHeight();
		}
		while(rs.next()) { 
			elements.add(new Elem(rs, width , height));
		}
		return elements;
	}
	
	public static Elem[] getElemsArray(String imgName, String sql, Connection conn) throws IOException, SQLException {
		ResultSet rs = getitems(sql, conn);
		Vector<Elem> elements = new Vector<Elem>();
		int width=0,height=0;
		if(imgName==null){
			width=100;height=100;
		}else{
			BufferedImage tmpImg;
			tmpImg = ImageIO.read(new File(imgName));
			width = tmpImg.getWidth();
			height = tmpImg.getHeight();
		}
		while(rs.next()) { 
			elements.add(new Elem(rs, width , height));
		}
		Elem[] res = elements.toArray(new Elem[1]);
		ElemUtil.init(res);
		return res;
	}
	
	public static boolean isNumeric(String str){
		 for (int i = str.length();--i>=0;){  
		 if (!Character.isDigit(str.charAt(i)) && str.charAt(i)!=('.'))
			{
		       return false;
		    }
		}
		return true;
	}
	
	public static void executeSQL(String sql_path){
		File file = new File(sql_path);
		String sql = "";
		if (file.isFile() && file.exists()) { // 判断文件是否存在
			InputStreamReader read;
			try {
				read = new InputStreamReader(new FileInputStream(file));
				BufferedReader bufferedReader = new BufferedReader(read);
				String line = "";
				while((line = bufferedReader.readLine()) != null){
					sql+=line;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		update(sql);
	}
	public static void createTable(String dbName, Connection conn) {

		String checkDB = "DROP TABLE IF EXISTS `" + dbName + "`";
		String test = "CREATE TABLE `"
				+ dbName
				+ "` ("
				+ " `ID` int(10) NOT NULL,"
				+ " `parentID` int(10) DEFAULT NULL,"
				+ " `width` int(10) DEFAULT NULL,"
				+ " `height` int(10) DEFAULT NULL,"
				+ " `offsetLeft` int(10) DEFAULT NULL,"
				+ " `offsetTop` int(10) DEFAULT NULL,"
				+ "`v_side` double(10,3) DEFAULT '0.000',"
				+ "`h_side` double(10,3) DEFAULT '0.000',"
				+ "`l_side` double(10,3) DEFAULT '0.000',"
				+ "`t_side` double(10,3) DEFAULT '0.000',"
				+ "`shapeAprnc` double(10,3) DEFAULT '0.000',"
				+ " `isZero` int(1) DEFAULT '0',"
				+ " `tag` varchar(20) DEFAULT '',"
				+ " `color` varchar(25) DEFAULT '',"
				+ " `fontColor` varchar(25) DEFAULT '',"
				+ " `overflow` varchar(25) DEFAULT '',"
				+ " `font_size` varchar(25) DEFAULT '',"
				+ " `font_weight` int(1) DEFAULT '0',"
				+ " `word_count` int(1) DEFAULT '0',"
				+ " `word_len` int(1) DEFAULT '0',"
				+ " `search` int(1) DEFAULT '0',"
				+ " `footer` int(1) DEFAULT '0',"
				+ " `logo` int(1) DEFAULT '0',"
				+ " `image` int(1) DEFAULT '0',"
				+ " `navigation` int(1) DEFAULT '0',"
				+ " `bottom` int(1) DEFAULT '0',"
				+ " `top` int(1) DEFAULT '0',"
				+ " `fills_height` int(1) DEFAULT '0',"
				+ " `fills_width` int(1) DEFAULT '0',"
				+ " `selector` varchar(200) DEFAULT '',"
				+ " `lv` int(3) DEFAULT '0',"
				
				+ " `is_title` int(1) DEFAULT '0',"
				+ " `is_txt` int(1) DEFAULT '0',"
				+ " `is_input` int(1) DEFAULT '0',"
				+ " `is_href` int(1) DEFAULT '0',"
				
				+ " `sib_order` int(3) DEFAULT '0',"
				+ " `children_num` int(3) DEFAULT '0',"
				+ " `sib_num` int(3) DEFAULT '0',"
				

				+ " `textarea` int(10) DEFAULT '0',"
				

				// + " `display` varchar(25) DEFAULT '',"


				+ " `matchID` int(10) DEFAULT NULL,"
				+ " `clusterID` int(10) unsigned DEFAULT '0',"

				+ " `typeCls` int(2) DEFAULT NULL,"
				+ "`coverage` double(10,3) DEFAULT '0.000',"
				+ "`coverage_img` double(10,3) DEFAULT '0.000',"
				+ "`coverage_txt` double(10,3) DEFAULT '0.000',"
				+ " `matchCls` int(10) unsigned DEFAULT '0',"

				+ " PRIMARY KEY (`ID`)"

				+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		Util.update(checkDB);
		Util.update(test);
	}
	
	/**
	 * open the webpage (url) by browser, and get the snapshot of the webpage, the screen-size is set to be maximize
	 * @param url The directory of the webpage, usually in a server's folder
	 * @param folder The location of where the snapshot you wanna put in.
	 * @param imgName The name of the snapshot
	 * @param webDriverName The driver name in Selenium, in this project, we use the FirefoxDriver
	 * @param firefoxPath The location of the browser's path
	 */
	public static void snapping(String url, String folder, String imgName, String webDriverName, String firefoxPath){
		//System.setProperty(webDriverName, firefoxPath);
		System.out.println("snapping");
		System.setProperty("webdriver.firefox.bin", firefoxPath);
		//System.setProperty("webdriver.firefox.marionette","F:\\MyYunDisk\\edu.sysu.fxtm\\lib_2\\geckodriver.exe");
		//System.setProperty("webdriver.gecko.driver", "lib_2/geckodriver.exe");
		
		//ProfilesIni pi = new ProfilesIni();
		//FirefoxProfile profile = pi.getProfile("selenium");
		// Set options of Firefox
		//FirefoxOptions opt = new FirefoxOptions();
		//opt.setProfile(profile);
		
		WebDriver driver = new FirefoxDriver();
		driver.manage().window().maximize();
	
		//Dimension dimsn = new Dimension(1920, 1030);// that will get a snapshot whose width is 1464 (not for IPS)
		//driver.manage().window().setSize(dimsn);
		

		driver.get(url);
		try {
			Thread.sleep(5000); 
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		

		File screenShotFile = ((RemoteWebDriver) driver)
				.getScreenshotAs(OutputType.FILE);
		String pageImgPath = folder + imgName;
		
		try {
			FileUtils.copyFile(screenShotFile, new File(pageImgPath));

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		}
		driver.close();
	}
	/**
	 * Get the image of each visible element in the webpage
	 * @param dbName the name of webpage
	 * @param imgSrc the location of the webpage's snapshot
	 * @param folder the target where you wanna put the snapshots in
	 * @param conn
	 */
	public static void dividePage(String dbName, String imgSrc, String folder,Connection conn) {
		String url = folder + "/" + dbName + "/";
		File f = new File(url); 
		f.mkdirs();
		f=null;

		BufferedImage tSrc=null;
		File tFile = new File(imgSrc); 
		int width = 0,height=0;
		try {
			tSrc = javax.imageio.ImageIO.read(tFile);
			width = tSrc.getWidth(null); // 得到源图宽
			height = tSrc.getHeight(null); // 得到源图长

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String sql = "select * from " + dbName+" where iszero=0";
		ResultSet rs = Util.getitems(sql,conn);

		try {
			while (rs.next()) {
				int id = rs.getInt("ID");
				int w = rs.getInt("width");
				int h = rs.getInt("height");
				int x = rs.getInt("offsetLeft");
				int y = rs.getInt("offsetTop");

				String smallImg = folder + "/" + dbName + "/" + id + ".png";
				int bot = h + y;
				int right = w + x;

				if ((bot > 0 || y > 0) && (right > 0 || x > 0)) {
					BufferedImage tmp=null;
					if(right-x!=0&&bot-y!=0){
						if(x-width<0&&y-height<0&&right<=width&&bot<=height&&right>=0&&x>=0&&y>=0&&bot>=0){
							System.out.println(x + " " + y + " " + right + " " + bot);
							tmp=tSrc.getSubimage(x, y,Math.abs(right-x), Math.abs(bot-y));
							try {
								ImageIO.write(tmp, "PNG", new File(smallImg));
								System.out.println("dividing page..."+id);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						tmp=null;
					}

				}
			}

			tSrc=null;
			tFile=null;

			rs.close();
			rs = null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {

		FileInputStream input = new FileInputStream(sourceFile);
		BufferedInputStream inBuff = new BufferedInputStream(input);

		FileOutputStream output = new FileOutputStream(targetFile);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = inBuff.read(b)) != -1) {
			outBuff.write(b, 0, len);
		}
		outBuff.flush();

		inBuff.close();
		outBuff.close();
		output.close();
		input.close();
	}
	public static int[][] getMatrix(int width, int height){
		int [][] mtx = new int[width][];
		for(int i=0;i<width;i++) {
			mtx[i] = new int[height];
			for(int j=0;j<height;j++) {
				mtx[i][j] = 0;
			}
		}
		return mtx;
	}
	public static void copyFolder(String srcFolder, String destFolder)
	        throws IOException {	
	    long startTime = System.currentTimeMillis();
	    final Path srcPath = Paths.get(srcFolder);
	    // 这里多创建一级，就解决了没有外壳的问题
	    final Path destPath = Paths.get(destFolder, srcPath.toFile().getName());
	    // 检查源文件夹是否存在
	    if (Files.notExists(srcPath)) {
	        System.err.println("源文件夹不存在");
	        System.exit(1);
	    }
	    // 如果目标目录不存在，则创建
	    if (Files.notExists(destPath)) {
	        Files.createDirectories(destPath);
	    }
	
	    Files.walkFileTree(srcPath, new SimpleFileVisitor<Path>() {
	        public FileVisitResult visitFile(Path file,
	            BasicFileAttributes attrs) throws IOException {
	        Path dest = destPath.resolve(srcPath.relativize(file));
	        
	        if (Files.notExists(dest.getParent())) {
	            Files.createDirectories(dest.getParent());
	        }
	        if(!new File(dest.toString()).exists()){
	        	 Files.copy(file, dest);
	        }else {
	        	new File(dest.toString()).delete();
	        	Files.copy(file, dest);
	        }
	        return FileVisitResult.CONTINUE;
	        }
	    });
	    long endTime = System.currentTimeMillis();
	    System.out.println("复制成功!耗时：" + (endTime - startTime) + "ms");
	    }
	public static Bg[][] matrixClone(Bg[][] ori){
		Bg[][] res = new Bg[ori.length][];
		for(int i=0;i<ori.length;i++){
			res[i] = new Bg[ori[i].length];
			for(int j=0; j<ori[0].length;j++){
				res[i][j] = (Bg) ori[i][j].clone();
			}
		}
		return res;
	}
	public static int[][] matrixClone(int[][] ori){
		int[][] res = new int[ori.length][];
		for(int i=0;i<ori.length;i++){
			res[i] = ori[i].clone();
		}
		return res;
	}
}
