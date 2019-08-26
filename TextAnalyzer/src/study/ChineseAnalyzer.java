package study;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.sun.jna.Native;

public class ChineseAnalyzer {
	private CLibrary instance;
	private String timeText;

	/**
	 * 
	 */
	public ChineseAnalyzer() {
	
		try {
			System.out.println(System.getProperty("user.dir") + "\\source\\NLPIR");
			//instance = (CLibrary) Native.loadLibrary(ChineseAnalyzer.class.getResource("/").getPath()+"source/NLPIR", CLibrary.class);
			//instance = (CLibrary) Native.loadLibrary("d:\\source\\NLPIR", CLibrary.class);
			//instance = (CLibrary) Native.loadLibrary(System.getProperty("user.dir") + "\\source\\NLPIR", CLibrary.class);	
			instance = (CLibrary) Native.loadLibrary("source/NLPIR", CLibrary.class);	
		}catch (Exception e) {
			System.out.println("错误信息："+new File("").getAbsolutePath()+"\\\\source\\\\NLPIR");
			e.printStackTrace();
		}

		int init_flag = instance.NLPIR_Init("", 1, "0");
		String resultString = null;
		if (0 == init_flag) {
			resultString = instance.NLPIR_GetLastErrorMsg();
			System.err.println("初始化失败！\n" + resultString);

		}
		try {
			//instance.NLPIR_ImportUserDict(System.getProperty("user.dir") + "\\source\\userdic.txt");
			instance.NLPIR_ImportUserDict(System.getProperty("user.dir") + "source/userdic.txt");
		} catch (Exception e) {
			System.out.println("错误信息：");
			e.printStackTrace();
		}

	}

	/**
	 * add list of location in to dictionary the list can be from mysql or file and
	 * so on
	 * 
	 * @param locations
	 *            the locations is the user often used
	 */
	public void addLocationToDict(List<String> locations) {
		int length = locations.size();
		for (int i = 0; i < length; i++)
			instance.NLPIR_AddUserWord(locations.get(i) + " ns");
	}

	/**
	 * add a location to dictionary
	 * 
	 * @param location
	 */
	public void addOneLocationToDict(String location) {

		instance.NLPIR_AddUserWord(location + " ns");
	}

	/**
	 * add time in to dictionary
	 * 
	 * @param text
	 *            time text
	 */
	public void addTimetoText(String text) {

		instance.NLPIR_AddUserWord(text + " p");
	}

	/**
	 * get location
	 * 
	 * @param sInput
	 *            the primitive notification text
	 * @return the location in the text
	 */
	public LinkedList<String> get_location(String sInput) {

		String resultString = null;
		resultString = instance.NLPIR_ParagraphProcess(sInput, 1);
	//	System.out.println("分词结果为：\n " + resultString);
		String[] strings = resultString.split(" ");
		LinkedList<String> locations = new LinkedList();
		String location = "";
		int t = 0;
		String last = "p";
		for (int i = 0; i < strings.length; i++) {
			String[] strings2 = strings[i].split("/");
			if (strings2.length >= 2) {
				// System.out.println(t+" "+strings2[1]+" "+location);
				if (isBefore(last) && isAfter(strings2[1]))
					continue;
				if (isAfter(strings2[1]) && t == 1) {
					t = 0;
					locations.add(location);
				//	System.out.println("添加成功");
					location = "";
				}
				if (t == 1 && isNotInLocation(strings2[1])) {
					t = 0;
					location = "";
				}
				if (t == 1) {
					location = location + strings2[0];
				}
				if (isBefore(strings2[1])) {
					t = 1;
					location = "";
				}
				last = strings2[1];
			}
		}
		locations.add(location);
		return locations;
	}

	private boolean isBefore(String word) {
		String[] before = { "t", "p", "vf" };
		for (int i = 0; i < before.length; i++) {
			if (before[i].equals(word))
				return true;
		}
		return false;
	}

	private boolean isAfter(String word) {
		String[] after = { "v", "vi", "w", "wd", "f", "wj","wm","wp","vn","udeng" };
		for (int i = 0; i < after.length; i++) {
			if (after[i].equals(word))
				return true;
		}
		return false;
	}

	private boolean isNotInLocation(String word) {
		String[] not = { "rr", "r", "vn", "ude1", "ule", "y", "vyou" };
		for (int i = 0; i < not.length; i++) {
			if (not[i].equals(word))
				return true;
		}
		return false;
	}

	public LinkedList<String> clearLocation(LinkedList<String> locations) {
		LinkedList<String> newlocation = new LinkedList();
		for (int i = 0; i < locations.size(); i++) {
			// System.out.println(" old "+locations.get(i));
			String sInput = locations.get(i);
			if (isNotAppear(sInput)||isNotLocation(sInput))
				continue;
	
			if (sInput.length() == 1 || sInput.length() == 0)
				continue;
			if (sInput.toCharArray()[0] == '：' || sInput.toCharArray()[0] == '）' || sInput.toCharArray()[0] == ':')
				sInput = sInput.substring(1, sInput.length());

			char[] s = sInput.toCharArray();
			int t = 0;
			
			for (int j = 0; j < s.length; j++) {
				if (!isNumber(s[j]) && !isSymbol(s[j])&&!isDirection(s[j])) {
					t = 1;
					break;
				}
			}
			if (t == 0)
				continue;
			
			if (sInput.length() == 1 || sInput.length() == 0)
				continue;

			
			newlocation.add(sInput);
			// System.out.println(" new "+sInput);

		}

		return newlocation;
	}

	private boolean isNumber(char c) {
		char[] number = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
		for (int i = 0; i < number.length; i++) {
			if (number[i] == c)
				return true;
		}
		return false;
	}

	private boolean isSymbol(char c) {
		char[] number = { '@', '#', '$', '%', '^', '&', '*', '(', ')', '!', '（', '）', '?', '？',':','：' };
		for (int i = 0; i < number.length; i++) {
			if (number[i] == c)
				return true;
		}
		return false;
	}
	private boolean isDirection(char c) {
		char[] number = { '前', '后', '左', '右', '东', '西', '南', '北' };
		for (int i = 0; i < number.length; i++) {
			if (number[i] == c)
				return true;
		}
		return false;
	}
	private boolean isNotAppear(String location) {
		String[] notLocation = {  "班级","班会","实际","一个月","怎","一定","月份","办法","学期","情况","届时","情况","附件","身上","专业","之前","进行",
				"不再","按时","全团进一步","善好青年","通知","人选","逐级","后两周","四六级","位数",
				"原因","还是","导员","学生证","网站改","旬报","左右","同学","已经","队将","文件","与","老师",
				"针","成员","或","体质"};
		for (int i = 0; i < notLocation.length; i++) {
			if (location.indexOf(notLocation[i])!=-1)
				return true;
		}
		return false;
	}
	private boolean isNotLocation(String location) {
		String[] notLocation = {  "学生","一场双","2毛","好！","分别","父母","学年","时间",""};
		for (int i = 0; i < notLocation.length; i++) {
			if (location.equals(notLocation[i]))
				return true;
		}
		return false;
	}

}
