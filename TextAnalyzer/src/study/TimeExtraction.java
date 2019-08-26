package study;

import java.util.regex.Matcher;

import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TimeExtraction {
	private JSONObject result;
	
	// 记录匹配出来的原始格式
	private String date ; // 日期
	private String clock ; // 具体时间
	private String week ; // 周几----------用来判断日期
	private String Day ; // 今明后天----------用来判断日期
	private String day ; // 一天内的时间段---------用来判断具体时间
	private String initial ; // 原始时间
	

	private LinkedList<String> times = new LinkedList<String>();
	private String[] allmonths= {"1","2","3","4","5","6","7","8","9","10","11","12"};
	private String[] allweeks= {"1","2","3","4","5","6","7"};
	private String[] alldays= {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18",
			"19","20","21","22","23","24","25","26","27","28","29","30","31"};

	// 日期匹配的正则表达式
	private String[] splits = { "到", "至", "-", "--", "—", "——" };// 分隔符汇总

	private String base_split = "(到|至|-{1,2}|~|—{1,2})";
	private String base_year = "\\d{2,4}";
	private String base_month = "(\\d{1,2}|一|二|三|四|五|六|七|八|九|十|十一|十二)";
	private String base_day = "(\\d{1,2}|(十|二十){0,1}[一二三四五六七八九]|十|二十|三十|三十一)";

	private String base_time_hour = "(\\d{1,2}|一|二|两|三|四|五|六|七|八|九|十|十一|十二)";
	private String base_time_minute = "((\\d{1,2}|半|(十|二十|三十|四十|五十)五{0,1}|零{0,1}五){0,1}分{0,1})";

	private String[] reg_date = {
			"("+base_year + "\\.){0,1}" + base_month + "\\." + base_day + "[\\u4e00-\\u9fa5]{0,1}"+base_split
			+"("+base_year + "\\.){0,1}" + "("+base_month + "\\.){0,1}" + base_day + "[\\u4e00-\\u9fa5]{0,1}",
			
			"(" + base_year + "年){0,1}" + base_month + "月" + base_day + "[日号]{0,1}" + base_split + "("
					+ base_year + "年){0,1}" + "(" + base_month + "月){0,1}" + base_day + "[日号]{0,1}",	
			 base_day + "[日号]{0,1}" + base_split + base_day + "[日号]",					
					
			base_year + "年" + base_month + "月" + base_day + "[日号]{0,1}", 
			base_month + "月" + base_day + "[日号]{0,1}",
			base_year + "年" + "("+base_month + "月){0,1}",
			
			base_year + "\\." + base_month + "\\." + base_day + "[\\u4e00-\\u9fa5]{0,1}",
			base_year + "\\." + base_month + "[\\u4e00-\\u9fa5]{0,1}", 
			base_month + "\\." + base_day + "[\\u4e00-\\u9fa5]{0,1}",
			
			"(" + base_year + "年){0,1}" + base_month + "月" + "(初|末|底|(上|中|下)旬){0,1}",
			"每年("+base_month + "月){0,1}"+"("+base_day + "[日号]{0,1}){0,1}",
			"(每|下)[\\u4e00-\\u9fa5]{0,1}月(的|得|地){0,1}"+"("+base_day + "[日号]){0,1}",
			
			
			base_day + "[日号]", 
			};

	private String[] reg_week = { "(下{0,3}|每{0,1})(周|星期|礼拜)[一二三四五六日]{0,1}" };

	private String[] reg_Day = { "(今|明)天", "(后|每)天", "(今|明)(早|晚)" };
	private String[] reg_day = { "(上|中|下)午", "(早|晚)上{0,1}" };

	private String[] reg_clock = { "(\\d{1,2}[:：]\\d{1,2})" + base_split + "(\\d{1,2}[:：]\\d{1,2})",
			"(\\d{1,2})" + base_split + "(\\d{1,2}[:：]\\d{1,2})",
			base_time_hour + "[点时]{0,1}" + base_time_minute + base_split + base_time_hour + "[点时]" + base_time_minute,
			"(\\d{1,2}[:：]\\d{1,2})", base_time_hour + "[点时]" + base_time_minute, };

	
	
	public TimeExtraction() {
		result=new JSONObject();
		result.put("status", "");
		result.put("start", "");
		result.put("end", "");
		result.put("repeat_month", "");
		result.put("repeat_week", "");
		result.put("repeat_day", "");
		result.put("count", "");
		result.put("summary", "");
		
		date = ""; 
		clock = ""; 
		week = ""; 
		Day = ""; 
		day = ""; 
		initial = "";
	}
	
	
	/**
	 * 得到最终分析结果
	 */
	public JSONObject getExtractionResult(String str,String currentTime) {
		JSONObject jsonObject=new JSONObject();
		String initStr=getInitResult(str);
		jsonObject=getStdResult(initStr, currentTime);
		return jsonObject;
	}
	
	
	/**
	 * 获取每一句中的原始时间信息
	 * 
	 * @param str
	 */
	public String getInitResult(String str) {
		//this.init();
		Matcher ma;
		int index_clock = 0;
		int index_day = 0;

		for (int i = 0; i < reg_date.length; i++) {
			ma = Pattern.compile(reg_date[i]).matcher(str);
			if (ma.find()) {
				this.date = ma.group(0);
				// 如果匹配到12.7形式的
				if (this.date.contains(".")) {
					Pattern p = Pattern.compile("[\u4e00-\u9fa5]");// 中文字符
					Matcher m = p.matcher(this.date);
					if (m.find() && m.group(0).contains("元")) {// 并且包含中文字符，判断中文字符是不是“元”
						this.date = "";
					}
				}
				times.add(this.date);
				break;
			}
		}

		for (int i = 0; i < reg_clock.length; i++) {
			ma = Pattern.compile(reg_clock[i]).matcher(str);
			if (ma.find()) {
				index_clock = ma.start(0);
				this.clock = ma.group(0);
				times.add(this.clock);
				break;
			}
		}

		for (int i = 0; i < reg_week.length; i++) {
			ma = Pattern.compile(reg_week[i]).matcher(str);
			if (ma.find()) {
				this.week = ma.group(0);
				times.add(this.week);
				break;
			}
		}

		for (int i = 0; i < reg_Day.length; i++) {
			ma = Pattern.compile(reg_Day[i]).matcher(str);
			if (ma.find()) {
				this.Day = ma.group(0);
				times.add(this.Day);
				break;
			}
		}

		for (int i = 0; i < reg_day.length; i++) {
			ma = Pattern.compile(reg_day[i]).matcher(str);
			if (ma.find()) {
				index_day = ma.start(0);
				this.day = ma.group(0);
				times.add(this.day);
				break;
			}
		}

		if ((index_clock != 0) && (index_day != 0) && (index_clock < index_day)) {
			this.day = "";
		}

		// 去除时间，得到摘要
		str = str.replaceAll(this.date, "");
		str = str.replaceAll(this.week, "");
		str = str.replaceAll(this.Day, "");
		str = str.replaceAll(this.day, "");
		str = str.replaceAll(this.clock, "");
		this.result.put("summary", str);

		this.initial = this.date + this.week + this.Day + this.day + this.clock;
		return this.initial;

	}

	/**
	 * 时间格式规范化
	 * 
	 * @param initial
	 * @return
	 */
	public JSONObject getStdResult(String initial, String currentTime) {
		String date_start = ""; // 开始日期
		String date_end = ""; // 结束日期
		String time_start = ""; // 开始时间
		String time_end = ""; // 结束时间
		
		if(currentTime=="" || currentTime==null || currentTime.equals("")) {
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
			currentTime=df2.format(new Date());// new Date()为获取当前系统时间
		}
		
		String currentDate = currentTime.split(" ")[0];
	
		if (initial != "") {
			/**
			 * 日期的转换
			 */
			if (this.date != "") {
				date_start = dateToStdDate(currentDate);
			}

			// date为空时---根据星期几和周几week判断日期(几种情况：周三、下周三、下下周三、每周三)
			if (this.date.equals("") && this.week != "") {
				date_start = weekToStdDate(currentDate);
			}

			// date和week为空时---根据今明后天Day判断日期
			if (this.date.equals("") && this.week.equals("") && this.Day != "") {
				date_start=dayToStdDate(currentDate);

			}

			// date、week、Day都为空时，但时间不为空时---默认日期是今天
			if (this.date.equals("") && this.week.equals("") && this.Day.equals("")) {
				if (this.clock != "" || this.day != "")
					date_start = currentDate;
			}

			/**
			 * 时间的转换
			 */
			// clock不为空,使用24小时格式,且将中文时间转换成数字,如果是时间段,分割为开始时间和结束时间
			if (this.clock != "") {
				boolean isDur = false;
				for (int i = 0; i < splits.length; i++) {
					if (this.clock.contains(splits[i])) {
						isDur = true;
						break;
					}
				}
				if (isDur) {
					String[] temp2 = this.clock.split(base_split);
					String start = temp2[0];
					String end = temp2[1];
					time_start = timeToStdTime(start);
					time_end = timeToStdTime(end);
					if (date_end == "" && time_end != "")
						date_end = date_start;
				} else
					time_start = timeToStdTime(this.clock);
			}

			// clock为空时设置默认时间---根据一天内的时间段day判断具体时间
			if (this.clock == "" && this.day != "") {
				time_start = setDefaultTime();
			}

			// clock和day都为空时设置默认时间为0点
			if (this.clock.equals("") && this.day.equals("")) {
				time_start = "00:00";
			}
			
			/**
			 * 存储结果
			 */
			this.result.put("status", "true");
			if (date_start != "" && time_start != "")
				this.result.put("start", date_start + " " + time_start);
			if(date_start==""&&time_start!="")
				this.result.put("start", time_start);
			if (date_end != "" && time_end != "")
				this.result.put("end", date_end + " " + time_end);

			if(this.result.get("count").equals(""))
				this.result.put("count", 1 + "");
			return this.result;

		} else {
			this.result.put("status", "false");
			return this.result;
		}

	}

	

	/**
	 * 将日期格式组合成标准日期格式
	 */
	public String dateToStdDate(String currentDate) {
		String currentYear = currentDate.split("-")[0]; // 当前年份
		String currentMonth = currentDate.split("-")[1]; // 当前月份
		
		String year = "";
		String month = "";
		String daytemp = "";
		int flag=0;//代表是否是重复时间
		
		// 如果识别出来了时间段，分隔符前面的开始时间
		boolean isDur = false;
		for (int i = 0; i < splits.length; i++) {
			if (this.date.contains(splits[i])) {
				isDur = true;
				break;
			}
		}
		if (isDur) {
			this.date = this.date.split(base_split)[0];
		}

		//重复时间（月）: 日期字段设为空，每月九号、每月、每个月9号     默认重复12次
		if(this.date.contains("每")) {
			flag=1;
			String day;
			
			String[] tempdate=this.date.split("月(的|地|得)|日|号");
			System.out.println(tempdate[1]);
			if(tempdate.length<2) 
				daytemp=""+1;
			else
				daytemp=tempdate[1];
				
			boolean isNumber = daytemp.matches("[0-9]+");
			if (!isNumber)
				day= ""+chineseNumberToInt(daytemp) ;		
			else
				day= daytemp;
	
			
			String[] days= {day};
			
			this.result.put("repeat_month", allmonths);
			this.result.put("repeat_week", allweeks);
			this.result.put("repeat_day", days);		
			this.result.put("count", "12");
			
		}
		
		// 2019.12.7 || 12.7 || 2019.12
		else if (this.date.contains(".")) {

			String[] tempdate = this.date.split("\\.|[\\u4e00-\\u9fa5]");
			if (tempdate.length == 2) {
				if (tempdate[0].length()==4) { // 2019.12
					year = tempdate[0];
					month = tempdate[1];
					daytemp = "" + 1;
				} else {  //12.7
					year = currentYear;
					month = tempdate[0];
					daytemp = tempdate[1];
				}
			} else {
				year = tempdate[0];
				month = tempdate[1];
				daytemp = tempdate[2];
			}

		} 
		//年月日时间
		else {
			// 对于同时包含年月日的完整日期：18年8月22日、19年8月22、19年8月
			if (this.date.contains("年")) { // 年月日
				String[] tempdate = this.date.split("年|月|日|号");
				if (tempdate.length == 1) { // 只有年份
					year = tempdate[0];
					month = "" + 1;
					daytemp = "" + 1;
				} else {
					year = tempdate[0];
					month = tempdate[1];
					if (tempdate.length < 3)
						daytemp = "" + 1;
					else
						daytemp = tempdate[2];
				}
			} else if (this.date.contains("月")) { // 不包含年份，但包含月份的 8月22，8月
				String[] tempdate = this.date.split("月(的|地|得)|日|号");
				year = currentYear;
				
				if(tempdate[0].contains("下")) {//下个月的情况
					int monthtemp=Integer.parseInt(currentMonth)+1;
					if(monthtemp>12) {
						monthtemp=monthtemp%12;
						year = "" + (Integer.parseInt(year)+1);
					}
					month = ""+monthtemp;
				}
				else
					month = tempdate[0];
				
				if (tempdate.length < 2)
					daytemp = "" + 1;
				else
					daytemp = tempdate[1];
			} else { // 不包含年份和月份
						// 只包含日，22日、22 ，
				String[] tempdate = this.date.split("日|号");
				year = currentYear;
				month = currentMonth;
				daytemp = tempdate[0];
			}
		}

		boolean isNumber = true;
		isNumber = month.matches("[0-9]+");
		if (!isNumber) // 是中文格式
			month = "" + chineseNumberToInt(month);

		isNumber = daytemp.matches("[0-9]+");
		if (!isNumber) {
			if (daytemp.contains("旬")) {
				daytemp = daytemp.replaceAll("旬", "");
				switch (daytemp) {
				case "上":
					daytemp = "" + 1;
				case "中":
					daytemp = "" + 11;
				case "下":
					daytemp = "" + 21;
				}
			} 
			else if(daytemp.contains("初"))
				daytemp = "" + 1;
			else if(daytemp.contains("末")||daytemp.contains("底")) {
				if(month.equals("4")||month.equals("6")||month.equals("9")||month.equals("11"))
					daytemp = "" + 30;
				else if(month.equals("2")) {
					if(isLeapYear(Integer.parseInt(year)))
						daytemp = "" + 29;
					else
						daytemp = "" + 28;
				}
				else
					daytemp = "" + 31;			
			}
			else 				
				daytemp = "" + chineseNumberToInt(daytemp);
			
		}
		
		if(flag==1)
			return "";
		else
			return year + "-" + month + "-" + daytemp;
	}
	
	
	/**
	 * 将周几转变成日期
	 * @return
	 */
	public String weekToStdDate(String currentDate) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日历日期格式
		// 获取日历
		Calendar calendar = new GregorianCalendar();
		try {
			calendar.setTime(df.parse(currentDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int current_dayofweek = calendar.get(Calendar.DAY_OF_WEEK);// 获得今天是周几，1--周日，2--周一
		int dayofweek = weekToNumber();// 获取week所代表的数字
		
		String date_start;
		//重复日期
		if(this.week.contains("每")) {
			date_start="";
			
			String[] weeks= {""+(dayofweek-1)};
			this.result.put("repeat_month", allmonths);
			this.result.put("repeat_week", weeks);
			this.result.put("repeat_day", alldays);
			this.result.put("count", "52");
		}
		else {
			if (dayofweek > current_dayofweek) {
				
					if (this.week.contains("下")) {
						int len = 0;
						for (int i = 0; i < this.week.length(); i++) {
							if (this.week.charAt(i) == '下')
								len++;
						}
						calendar.add(Calendar.DATE, dayofweek - current_dayofweek + 7 * len);
						date_start = df.format(calendar.getTime());
					} else {
						calendar.add(Calendar.DATE, dayofweek - current_dayofweek);
						date_start = df.format(calendar.getTime());
					}
				
			} 
			else {
				calendar.add(Calendar.DATE, dayofweek + 7 - current_dayofweek);
				date_start = df.format(calendar.getTime());
			}
		}
		return date_start;
	}

	

	/**
	 * 今天，明天...转换成标准日期
	 * @param currentDate
	 * @return
	 */
	public String dayToStdDate(String currentDate) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日历日期格式
		// 获取日历
		Calendar calendar = new GregorianCalendar();
		try {
			calendar.setTime(df.parse(currentDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String date_start="";
		
		if (this.Day.contains("每")) {
			date_start = "";
		
			this.result.put("repeat_month", allmonths);
			this.result.put("repeat_week", allweeks);
			this.result.put("repeat_day", alldays);
			this.result.put("count", "365");
		}
		else if (this.Day.contains("今"))
			date_start = currentDate;
		else if (this.Day.contains("明")) {
			calendar.add(Calendar.DATE, 1);
			date_start = df.format(calendar.getTime());
		}
		else if (this.Day.contains("后")) {
			calendar.add(Calendar.DATE, 2);
			date_start = df.format(calendar.getTime());
		}
		
		if (this.Day.contains("晚")) {
			this.day += "晚";
		}
		
		return date_start;
	}
	
	
	/**
	 * 将各种时间格式转换成标准时间格式，例如8点，8点半,八点
	 */
	public String timeToStdTime(String clock) {
		String[] temp1 = clock.split("点|时|:|：|分");
		String hour;
		String minute;
		boolean isNumber = true;
		if (temp1.length > 1) {
			hour = temp1[0];
			minute = temp1[1];
		} else {
			hour = temp1[0];
			minute = "00";
		}

		// hour是否是中文
		isNumber = hour.matches("[0-9]+");
		int hourtemp = 0;
		if (!isNumber) // 如果是中文
			hourtemp = chineseNumberToInt(hour);
		else
			hourtemp = Integer.parseInt(hour);

		// 转换为24小时格式
		if ((this.day.contains("下午") || this.day.contains("晚")) && hourtemp < 12)
			hourtemp += 12;

		hour = "" + hourtemp;

		// minute是否是中文
		isNumber = minute.matches("[0-9]+");
		int minutetemp = 0;
		if (!isNumber) {
			minutetemp = chineseNumberToInt(minute);
			minute = "" + minutetemp;
		}

		return hour + ":" + minute;
	}
	
	/**
	 * 将周几转换为与日历中相对应的数字，周日--1，周一--2，周二---3
	 */
	public int weekToNumber() {
		int dayofweek = 0;
		//int index = this.week.length() - 1;
		//char temp = this.week.charAt(index);// 取最后一个字符
		
		String[] weektemp=this.week.split("周|星期|礼拜");
		String temp = null;
		if(weektemp.length<2) 
			temp = "一";
		else 
			temp=weektemp[1];
		
		switch (temp) {
		case "日":
			dayofweek = 8;
			break;
		case "一":
			dayofweek = 2;
			break;
		case "二":
			dayofweek = 3;
			break;
		case "三":
			dayofweek = 4;
			break;
		case "四":
			dayofweek = 5;
			break;
		case "五":
			dayofweek = 6;
			break;
		case "六":
			dayofweek = 7;
			break;
		}
		return dayofweek;
	}
	
	/**
	 * 为“上午、下午、早上、晚上”等模糊时间设置默认时间
	 */
	public String setDefaultTime() {
		String clock = "";
		switch (this.day) {
		case "上午":
			clock = "9:00";
			break;
		case "中午":
			clock = "12:00";
			break;
		case "下午":
			clock = "13:00";
			break;
		case "早上":
			clock = "6:00";
			break;
		case "晚上":
			clock = "20:00";
			break;
		case "晚":
			clock = "20:00";
			break;
		case "凌晨":
			clock = "00:00";
			break;
		}
		return clock;
	}


	/**
	 * 将中文数字转换成阿拉伯数字
	 */
	public int chineseNumberToInt(String chineseNumber) {
		int result = 0;
		int temp = 1;// 存放一个单位的数字如：十万
		int count = 0;// 判断是否有chArr
		char[] cnArr = new char[] { '一', '二', '三', '四', '五', '六', '七', '八', '九' };
		char[] chArr = new char[] { '十', '百', '千', '万', '亿' };

		if (chineseNumber.contains("半")) {
			return 30;
		} else if (chineseNumber.contains("两")) {
			return 2;
		} else {
			for (int i = 0; i < chineseNumber.length(); i++) {
				boolean b = true;// 判断是否是chArr
				char c = chineseNumber.charAt(i);
				for (int j = 0; j < cnArr.length; j++) {// 非单位，即数字
					if (c == cnArr[j]) {
						if (0 != count) {// 添加下一个单位之前，先把上一个单位值添加到结果中
							result += temp;
							temp = 1;
							count = 0;
						}
						// 下标+1，就是对应的值
						temp = j + 1;
						b = false;
						break;
					}
				}
				if (b) {// 单位{'十','百','千','万','亿'}
					for (int j = 0; j < chArr.length; j++) {
						if (c == chArr[j]) {
							switch (j) {
							case 0:
								temp *= 10;
								break;
							case 1:
								temp *= 100;
								break;
							case 2:
								temp *= 1000;
								break;
							case 3:
								temp *= 10000;
								break;
							case 4:
								temp *= 100000000;
								break;
							default:
								break;
							}
							count++;
						}
					}
				}
				if (i == chineseNumber.length() - 1) {// 遍历到最后一个字符
					result += temp;
				}
			}
		}
		return result;
	}

	/**
	 * 判断是否是闰年
	 */
	public boolean isLeapYear(int year) {
		if(year%4==0 && year%100!=0)
		   return true;
		else if(year%400==0)
		   return true;
		else
		   return false;
	}
	
	
	/**
	 * 判断是否是时间
	 * 
	 * @param str
	 * @return
	 */
	public boolean isTime(String str) {
		//getInitTime(str);
		if (this.initial.equals(""))
			return false;
		else
			return true;
	}

	/**
	 * 获取时间信息列表
	 * 
	 * @return
	 */
	public LinkedList<String> getTimes() {
		return times;
	}


	public JSONObject getResult() {
		return result;
	}

	public String getDate() {
		return date;
	}

	public String getClock() {
		return clock;
	}

	public String getWeek() {
		return week;
	}

	public String getDAY() {
		return Day;
	}

	public String getDay() {
		return day;
	}

	public String getInitial() {
		return initial;
	}

}
