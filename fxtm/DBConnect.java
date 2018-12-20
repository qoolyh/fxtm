package fxtm;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * <p>
 * 这个类主要是提供给其它需要连接到数据库的类来调用
 * </p>
 * 
 * @author 08se06
 * @since 2010-11-07
 * @version 1.0
 */
public class DBConnect {

	private static String url;
	private static String userName;
	private static String password;

	/*
	 * 通过从属性文件DBConnect.properties获取数据来初始化URL,userName，password 等连接数据库所需要的信息
	 */
	static {
		try {
			Properties dbCon = new Properties();
			String path = DBConnect.class.getResource("")
					+ "DBConnect.properties";
			String filePath = path.substring(6, path.length());
			String pPath = filePath.replace("%20", " ");
			FileInputStream fis = new FileInputStream(pPath);
			dbCon.load(fis);

			url = dbCon.getProperty("URL");
			userName = dbCon.getProperty("USERNAME");
			password = dbCon.getProperty("PASSWORD");

			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/*
	 * 获取数据库的连接
	 */
	public static Connection getConnect() {
		Connection conn = null;
		try {
			conn = (Connection) DriverManager.getConnection(url, userName,
					password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static int executeUpdate(Connection conn, String sql,
			Object... params) {
		int rst = -1;
		try {
			conn.setAutoCommit(false);
			PreparedStatement pstmt = (PreparedStatement) conn
					.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				pstmt.setObject(i + 1, params[i]);
			}
			rst = pstmt.executeUpdate();
			conn.commit();
		} catch (SQLException e1) {
			e1.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return rst;
	}

	/**
	 * <p>
	 * 这个函数主要用于执行查询的SQL
	 * </p>
	 * 
	 * @param conn
	 *            一个数据库连接对象
	 * @param sql
	 *            一条即将被执行的SQL语句
	 * @param params
	 *            通过在SQL语句中使用占位符?，并在这里输入参数来实现动态SQL语句
	 */
	public static ResultSet executeQuery(java.sql.Connection conn, String sql,
			Object... params) {
		ResultSet rs = null;
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				pstmt.setObject(i + 1, params[i]);
			}
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * 这个类用于执行数据库连接之后的处理，如关闭连接，关闭结果集...
	 * 
	 * @param conn
	 * @param stmt
	 * @param rs
	 */
	public static void close(Connection conn, Statement stmt, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
