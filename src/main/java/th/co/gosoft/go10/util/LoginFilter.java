package th.co.gosoft.go10.util;

import java.io.IOException;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import th.co.gosoft.go10.callback.handler.JAASCallbackHandler;

public class LoginFilter implements Filter {

    protected FilterConfig filterConfig;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String username = request.getParameter("j_username").toLowerCase();
        String password = request.getParameter("j_password");
        System.out.println("username : "+username+", password : "+password);
        
        LoginContext lc = null;
        try {
            lc = new LoginContext("GO10", new JAASCallbackHandler(username, password));
            lc.login();
            Subject subject = lc.getSubject();
            subject.getPrincipals();
        } catch (LoginException e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }

}
