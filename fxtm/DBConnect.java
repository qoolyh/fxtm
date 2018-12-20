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
 * �������Ҫ���ṩ��������Ҫ���ӵ����ݿ����������
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
	 * ͨ���������ļ�DBConnect.properties��ȡ��������ʼ��URL,userName��password ���������ݿ�����Ҫ����Ϣ
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
	 * ��ȡ���ݿ������
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
	 * ���������Ҫ����ִ�в�ѯ��SQL
	 * </p>
	 * 
	 * @param conn
	 *            һ�����ݿ����Ӷ���
	 * @param sql
	 *            һ��������ִ�е�SQL���
	 * @param params
	 *            ͨ����SQL�����ʹ��ռλ��?�������������������ʵ�ֶ�̬SQL���
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
	 * ���������ִ�����ݿ�����֮��Ĵ�����ر����ӣ��رս����...
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
