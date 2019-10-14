package demo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.cics.server.CicsConditionException;
import com.ibm.cics.server.ItemHolder;
import com.ibm.cics.server.TSQ;

/**
 * Servlet implementation class SimpleServlet
 */
@WebServlet("/SimpleServlet")
public class SimpleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    static final String TSQ_NAME = "DEMO";

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		TSQ tsq = new TSQ();
		tsq.setName(TSQ_NAME);
		
		ItemHolder itemHolder = new ItemHolder();
		
		try {
			tsq.readNextItem(itemHolder);
			String value = new String(itemHolder.getValue(), StandardCharsets.UTF_8);
			response.setContentType("text/html");
			response.getWriter().print(value);
			
		} catch (CicsConditionException e) {
			throw new RuntimeException(e);
		}
    }

}
