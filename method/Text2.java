package text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Text2 {

	public static void main(String[] args) {
		String str="@全体成员 金融专业的同学今天上午8:10-11:30，下午13:40-16:30到2教312领取教材，没时间的同学找别人代取，每人3本书";
		getTime(str);
		
	}
	public static void getTime(String str) {
		
		String[] reg_month = { "\\d{1,2}月\\d{1,2}日","\\d{2,4}年\\d{1,2}月\\d{1,2}日","\\d{1,2}月\\d{1,2}号","\\d{2,4}年\\d{1,2}月\\d{1,2}号"};
		String[] reg_week = { "周一", "周二", "周三", "周四", "周五", "周六", "周日" };
		String[] day2 = { "上午", "中午", "下午", "晚上", "凌晨", "晚" };
		String[] day1 = { "今天", "明天", "后天"};
		String[] reg_clock = { "\\d{1,2}:\\d{1,2}", "\\d{1,2}点",  "\\d{1,2}点\\d{1,2}","\\d{1,2}半" };

		String month = "";
		String week = "";
		String Day = "";
		String day = "";
		String clock = "";
		//
		Matcher ma;
		//String str = "【学雷锋专题活动】大家新学期快乐！~学雷锋推进会将于3月5号周一（明天）晚7:00图五举行，推进会后将由全国道德模范代表作报告，会议全程约1.5小时，有二课堂素质认证，刷卡签到。请有意愿参加的同学名单于今日17:30前，私信发送给我，格式如下：姓名，班级，学号。暂时按照原班级为单位上报。";
		for (int i = 0; i < reg_month.length; i++) {
		ma = Pattern.compile(reg_month[i]).matcher(str);
		if (ma.find())
			month = ma.group(0);
		}
		
		//
		for (int i = 0; i < reg_week.length; i++) {
			ma = Pattern.compile(reg_week[i]).matcher(str);
			if (ma.find())
				week = ma.group(0);
		}

		//
		for (int i = 0; i < day2.length; i++) {
			ma = Pattern.compile(day2[i]).matcher(str);
			if (ma.find())
				day = ma.group(0);
		}
		for (int i = 0; i < day1.length; i++) {
			ma = Pattern.compile(day1[i]).matcher(str);
			if (ma.find())
				Day = ma.group(0);
		}
		for (int i = 0; i < reg_clock.length; i++) {
			ma = Pattern.compile(reg_clock[i]).matcher(str);
			if (ma.find())
				clock = ma.group(0);
		}
		System.out.println(month+week+Day+day+clock);
	}
}
