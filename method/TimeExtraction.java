package regex_test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Test1 {

	static String[] messages;
	
	public static void main(String[] args) {
		//String str="1. 明早九点要参加学院组织的观看两会开幕式活动，请办理学生证的同学明天下午2:30-4：30再来办公室办理。请本学期重修语言学导论的同学，于明天下午2:30-4:30至办公室添加上课时段，大家可以自行查阅之前发的学院课表里2017级的课程，将把大家加入到2017级的班级上课，每个班5-6人，目前只能采取先到先得的政策，希望大家理解。";
		//String str="请通知本班第三十二次学生代表大会正式代表，扫码进微信群并于12月5日（本周四）17：30西配楼317参加分团会";
		//String str="通知Ç:各位同学大家晚上好！收团费工作开始了。1.团员每月2毛，本学期收两个季度的团费，每人1.2元。2.请每位同学将钱转给寝室长，寝室长收齐后于❗️今晚十点前❗️转到我的支付宝。转钱时备注一下宿舍号和交团费的人数¹。谢谢大家配合，辛苦大家了！@全体成员";	
		//String str="【评优表彰大会】本周五12.7号晚上5点50分，二教扇形报告厅开评优表彰大会大会，16级的没有签到表，去了的在那里的空白表写自己名字，有一分量化";		
		//String str="@全体成员 【转发至班群】2月27日下午为学生接种水痘疫苗第二针，凡上学期末已报名并交费两针的同学请于2月27日下午一点半到四点半按时到校医院接种水痘疫苗";	
		//String str="请本学期选修 《插花艺术(视频课)》但尚未提交作业的同学，最晚于2019年1月15日前补交作业。特此通知。";  
		//String str="Javaweb答疑周一上午34节二教408";   
		//String str="@全体成员 金融专业的同学今天上午8:10-11:30，下午13:40-16:30到2教312领取教材，没时间的同学找别人代取，每人3本书。";   
		//String str="@全体成员 6月20号（下周三）晚上考保险学、高级财务会计、项目管理、渠道管理，同学们好好复习，端午假期按时回校，不能参加考试的同学务必考前与我联系！";   
		//String str="今天（3月1日）是审批免修考试的最后一天，请同学们抓紧时间完成审核流程（系统申请-教师审核-教务处审核）。截止到3月1日17点。";   
		//String str="关于2018-2019学年第二学期通识选修课及专业选修课选课的通知已发群里，要求学生按时选课。选课时间1月24日8点——1月31日晚24点！";   
		//String str="选课时间：第二周星期二 至 星期五（可能根据实际情况延长）";    //第二周  
		//String str="中午验收，11点半，524，能排10个人。下午3点半验收余下同学。";
		//String str="PCB还没验收的明天10点--12点老师监考，可这个时间来，老师抽空回出来给你们验收人多的话12点以后去，在办公室321验收@全体成员";
		String str="每周二晚上8点15-9点半开班会";
		
		split_message(str);
		for(int i=0;i<messages.length;i++) {
			System.out.println(messages[i]);
			getTime(messages[i]);
			System.out.println("------------------------------------");
		}	
	}
	
	
	/**
	 * 按照标点分离
	 * 
	 * @param str
	 */
	public static void split_message(String str) {
		messages=str.split(",|，|。");
	}
	
	/**
	 * 提取每一句的时间信息
	 * 
	 * @param str
	 */
	public static void getTime(String str) {
		
		String[] reg_month = { "\\d{2,4}[年\\.]\\d{1,2}[月\\.]\\d{1,2}[日{0,1}号{0,1}]",
								"\\d{1,2}[月\\.]\\d{1,2}[日{0,1}号{0,1}]"};
		String[] reg_week = { "(下{0,1}|每{0,1})(周|星期)[一二三四五六日]"};
		String[] day2 = { "(上|中|下)午","晚上", "凌晨", "(明|今)(早|晚)"};
		String[] day1 = { "今天", "明天", "后天"};
		String[] reg_clock = { "\\d{1,2}[:：]\\d{1,2}(到|至|-{1,2}|~)\\d{1,2}[:：]\\d{1,2}",
								"\\d{1,2}[:：]\\d{1,2}", 
								"\\d{1,2}点(\\d{1,2}|半){0,1}(到|至|-{1,2}|~)\\d{1,2}点(\\d{1,2}|半){0,1}",
								"\\d{1,2}点(\\d{1,2}|半){0,1}" 																			
							};
								

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
			if (ma.find()) {
				month = ma.group(0);
				break;
			}
				
		}
		
		//
		for (int i = 0; i < reg_week.length; i++) {
			ma = Pattern.compile(reg_week[i]).matcher(str);
			if (ma.find()) {
				week = ma.group(0);
				break;
			}		
		}

		//
		for (int i = 0; i < day2.length; i++) {
			ma = Pattern.compile(day2[i]).matcher(str);
			if (ma.find()) {
				day = ma.group(0);
				break;
			}			
		}
		for (int i = 0; i < day1.length; i++) {
			ma = Pattern.compile(day1[i]).matcher(str);
			if (ma.find()) {
				Day = ma.group(0);
				break;
			}
				
		}
		for (int i = 0; i < reg_clock.length; i++) {
			ma = Pattern.compile(reg_clock[i]).matcher(str);
			if (ma.find()) {
				clock = ma.group(0);
				break;
			}
				
		}
		System.out.println(month+week+Day+day+clock);
	}
}
