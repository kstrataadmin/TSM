package com.kstrata.apps.tsm.business.uibeans;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kstrata.apps.tsm.business.dao.entity.Employee;

public class Authenticator implements Filter {
	@SuppressWarnings("unused")
	private FilterConfig customedFilterConfig;

	public void doFilter(HttpServletRequest req, HttpServletResponse resp,FilterChain chain) throws IOException, ServletException {
		HttpSession session = req.getSession(true);
		Employee employee = (Employee)session.getAttribute("loggedUser");
		if (employee == null) {
			((HttpServletResponse)resp).sendRedirect("/TSM/login.xhtml");
		} else {
			chain.doFilter(req, resp);
		}
	}
	
	public void init(FilterConfig customedFilterConfig) throws ServletException {
		this.customedFilterConfig = customedFilterConfig;
	}

	public void destroy() {
		customedFilterConfig = null;
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest)arg0;
		HttpServletResponse httpServletResponse = (HttpServletResponse)arg1;
		doFilter(httpServletRequest, httpServletResponse, arg2);
		
	}
}