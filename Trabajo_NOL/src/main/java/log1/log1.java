package log1;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import java.io.BufferedWriter;
import java.io.FileWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class log1
 */
public class log1 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public log1() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = request.getParameter("user");
        String ip = request.getRemoteAddr();
        String method = request.getMethod();
        LocalDateTime date = LocalDateTime.now();

        String message = date + " " + user + " " + ip + " acceso " + method;

        PrintWriter out = response.getWriter();
        out.println(message);
        
        String ruta = "/home/user/tomcat/webapps/Trabajo_NOL/result.txt";
        
        try {
        	FileWriter fw = new FileWriter(ruta);
        	BufferedWriter bw = new BufferedWriter(fw);
        	bw.write(message);
        	bw.close();
        	fw.close();
        }catch(IOException e) {
        	out.print("Error: file not found");
        }
        out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
