package study;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

import com.sun.jna.Native;

import net.sf.json.JSONObject;

public class TestAnalyzer {

	public JSONObject OneTest(String text, String currentTime, String CurrentLocation) {
		// System.out.println(System.getProperty("user.dir") + "\\source\\NLPIR");

		ChineseAnalyzer chineseAnalyzer = new ChineseAnalyzer();
		// String text =
		// "各位同学：学校将于3月19如（星期二）晚6:00在学研A0101举办本学期的校际交流项目宣讲会，学生如有相关问题也可以在宣讲会上进行咨询！";
		// text = " 今天中午一点在明远一区二楼教师休息室开会，这次会议是老师主持的，大家都尽量早到10分钟左右。收到请回复！@全体成员";
		TimeExtraction timeExtraction = new TimeExtraction();
		String initial = timeExtraction.getInitResult(text);
		LinkedList<String> times = timeExtraction.getTimes();
		JSONObject result = timeExtraction.getStdResult(initial, currentTime);

		for (int i = 0; i < times.size(); i++) {

			chineseAnalyzer.addTimetoText(times.get(i));
		}

		LinkedList<String> locations = chineseAnalyzer.get_location(text);
		LinkedList<String> loc = chineseAnalyzer.clearLocation(locations);

		LinkedList<String> newLocation = new LinkedList();
		for (int i = 0; i < loc.size(); i++) {
			newLocation.add(CurrentLocation + loc.get(i));
			System.out.println(loc.get(i));
		}
		result.put("destination", newLocation);
		System.out.println(result);
		return result;

	}

	public void TxtTest() {
		ChineseAnalyzer chineseAnalyzer = new ChineseAnalyzer();
		File file = new File("d:/file.txt");
		InputStreamReader reader;
		try {
			reader = new InputStreamReader(new FileInputStream(file), "GBK");

			BufferedReader bReader = new BufferedReader(reader);
			String line = bReader.readLine();
			while (line != null) {
				// System.out.println(line);
				/////////////////
				TimeExtraction timeExtraction = new TimeExtraction();
				String initial = timeExtraction.getInitResult(line);
				LinkedList<String> times = timeExtraction.getTimes();
				for (int i = 0; i < times.size(); i++) {
					// System.out.println("添加 " +times.get(i));
					chineseAnalyzer.addTimetoText(times.get(i));
				}
				// chineseAnalyzer.addOneLocationToDict(time);
				LinkedList<String> locations = chineseAnalyzer.get_location(line);
				LinkedList<String> loc = chineseAnalyzer.clearLocation(locations);
				for (int i = 0; i < loc.size(); i++)
					System.out.println(loc.get(i));

				System.out.println();

				/////////////////////////////
				line = bReader.readLine();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

}
