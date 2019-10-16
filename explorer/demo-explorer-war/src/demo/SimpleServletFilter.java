package demo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import com.github.javafaker.Faker;
import com.ibm.cics.server.CicsConditionException;
import com.ibm.cics.server.TSQ;
import com.ibm.cics.server.TSQType;

@WebFilter("/*")
public class SimpleServletFilter implements Filter {

	private TSQ tsq;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		tsq = new TSQ();
		tsq.setName(SimpleServlet.TSQ_NAME);
		tsq.setType(TSQType.MAIN);
		purgeQueue(tsq);
		
		Faker faker = new Faker();
		 
		String streetName = faker.address().streetName();
		String number = faker.address().buildingNumber();
		String city = faker.address().city();
		String country = faker.address().country();
		 
		String address = String.format("%s<br />%s<br />%s<br />%s",
			number,
			streetName,
			city,
			country
		);
		
		try {
			tsq.writeItem(address.getBytes(StandardCharsets.UTF_8));
		} catch (CicsConditionException e) {
			throw new RuntimeException(e);
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		try {
			tsq.delete();
		} catch (CicsConditionException e) {
			//throw new RuntimeException(e);
		}
	}

	private void purgeQueue(TSQ tsq){
		try{
			tsq.delete();
		}catch(CicsConditionException cce){
			//purging the queue will fail if the queue doesn't exist
			//so ignore this exception
		}
	}

}
