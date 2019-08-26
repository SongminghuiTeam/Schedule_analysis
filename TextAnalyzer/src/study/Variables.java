package study;

import net.sf.json.JSONObject;

public class Variables {
	static JSONObject result;
	

	// 记录匹配出来的原始格式
	static String date ; // 日期
	static String clock ; // 具体时间
	static String week ; // 周几----------用来判断日期
	static String Day ; // 今明后天----------用来判断日期
	static String day ; // 一天内的时间段---------用来判断具体时间
	static String initial ; // 原始时间
	
	
	public static void init() {
		result=new JSONObject();
		result.put("status", "");
		result.put("start", "");
		result.put("end", "");
		result.put("repeat_month", "");
		result.put("repeat_week", "");
		result.put("repeat_day", "");
		result.put("repeat_count", "");
		result.put("summary", "");
		result.put("destination", "");
		
		date = ""; 
		clock = ""; 
		week = ""; 
		Day = ""; 
		day = ""; 
		initial = ""; 
		
	}
	
}
