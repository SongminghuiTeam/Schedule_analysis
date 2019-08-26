package it.bjfu;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import study.TestAnalyzer;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/test")
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		System.out.println(request.getContextPath() + "\\source\\NLPIR");
		String text = request.getParameter("text");
		String currenttime = request.getParameter("currenttime");
		String currentlocation = request.getParameter("currentlocation");
		System.out.println(text + "\n" + currenttime + "\n" + currentlocation);
		if(text==null)
			text="今天中午一点在明远一区二楼教师休息室开会，这次会议是老师主持的，大家都尽量早到10分钟左右。收到请回复！@全体成员";
		if(currenttime==null)
			currenttime="2016-12-01 12:00:00";
		TestAnalyzer testAnalyzer=new TestAnalyzer();
		JSONObject jsonObject = testAnalyzer.OneTest(text,currenttime , currentlocation);
		//response.setContentType("text/html");
		//response.setCharacterEncoding("utf-8");

		//response.getWriter().print(jsonObject);
		//PrintWriter out = response.getWriter();

		//out.flush();
		//out.close();

		response.sendRedirect(request.getContextPath() + "/home.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
