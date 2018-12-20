package fxtm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class FileUtil {
	/**
	 * 
	 * @param file
	 * @param newFile
	 * @param charset
	 * @param jsName
	 * @param jqueryName
	 * @throws UnsupportedEncodingException
	 */
	public static void addJS(File file, OutputStreamWriter newFile,
			String charset, String js_path, String jqueryName, String... useModole)
	/*
	 * file:ԭhtml�ļ� ��File���͵ģ�������ʽ��preparation�еĵ��� newFile:�µ�html�ļ�
	 * charset:��ҳ��ı��뷽ʽ��UTF-8,Unicode�ȣ� jsName:��Ҫ��ӵ�js�ļ�����
	 * jqueryname:��Ҫ��ӵ�jQuery�����ƣ���ֹԭ��ҳ��û��ʹ��jQuery��
	 */
	throws UnsupportedEncodingException {

		if (file.isFile() && file.exists()) { // �ж��ļ��Ƿ����
			InputStreamReader read;
			try {
				read = new InputStreamReader(new FileInputStream(file), charset);
				// read = new InputStreamReader(new FileInputStream(file));

				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				Boolean flag = true;
				try {
					while ((lineTxt = bufferedReader.readLine()) != null) {
						if (lineTxt.contains("</body") && flag) {
							if(useModole != null) {
	
								String script = "<script type=\"module\" src=" + '"'
										+ js_path + '"' + "></script>";
								newFile.write(script + "\n");

								newFile.write(lineTxt + "\n");
								flag = false;
								newFile.flush();
							}else {
								String script1 = "<script src=" + '"' + "./"
										+ jqueryName + '"' + "></script>";

								String script2 = "<script src=" + '"'+ js_path + '"' + "></script>";
								newFile.write(script1 + "\n");
								newFile.write(script2 + "\n");

								newFile.write(lineTxt + "\n");
								flag = false;
								newFile.flush();
							}
							
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
			System.out.println("�Ҳ���ָ�����ļ�");
		}
	}
}
