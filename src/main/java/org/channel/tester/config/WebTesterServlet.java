package org.channel.tester.config;


import org.channel.tester.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author channel<zclsoft@163.com>
 * @since 2018/05/14 17:40.
 */
public class WebTesterServlet extends HttpServlet {
    protected String           username            = "tester";
    protected String           password            = "kadatester";
    public static final String SESSION_USER_KEY    = "tester-user";
    public static final String PARAM_NAME_USERNAME = "username";
    public static final String PARAM_NAME_PASSWORD = "password";
    protected String resourcePath;
    public WebTesterServlet() {
        this.resourcePath = "support/http/resources";;
    }

    public WebTesterServlet(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public void init() throws ServletException {
        initAuthEnv();
    }

    private void initAuthEnv() {
        String paramUserName = getInitParameter(PARAM_NAME_USERNAME);
        if (null!=paramUserName) {
            this.username = paramUserName;
        }
        String paramPassword = getInitParameter(PARAM_NAME_PASSWORD);
        if (null!=paramPassword) {
            this.password = paramPassword;
        }
    }
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String requestURI = request.getRequestURI();

        response.setCharacterEncoding("utf-8");

        if (contextPath == null) { // root context
            contextPath = "";
        }
        String uri = contextPath + servletPath;
        String path = requestURI.substring(contextPath.length() + servletPath.length());
        if ("/submitLogin".equals(path)) {
            String usernameParam = request.getParameter(PARAM_NAME_USERNAME);
            String passwordParam = request.getParameter(PARAM_NAME_PASSWORD);
            if (username.equals(usernameParam) && password.equals(passwordParam)) {
                request.getSession().setAttribute(SESSION_USER_KEY, username);
                response.getWriter().print("success");
            } else {
                response.getWriter().print("error");
            }
            return;
        }
          if(!ContainsUser(request)
                  && !("/login.html".equals(path) //
                  || path.startsWith("/css")//
                  || path.startsWith("/js") //
                  || path.startsWith("/img"))
                  ){
              response.sendRedirect("/tester/login.html");
              return;
          }
        returnResourceFile(path, uri, response);
    }

    protected void returnResourceFile(String fileName, String uri, HttpServletResponse response)
            throws ServletException,
            IOException {

        String filePath = getFilePath(fileName);
        if (fileName.endsWith(".jpg")) {
            byte[] bytes = Utils.readByteArrayFromResource(filePath);
            if (bytes != null) {
                response.getOutputStream().write(bytes);
            }

            return;
        }

        String text = Utils.readFromResource(filePath);
        if (text == null) {
            response.sendRedirect(uri + "/index.html");
            return;
        }
        if (fileName.endsWith(".css")) {
            response.setContentType("text/css;charset=utf-8");
        } else if (fileName.endsWith(".js")) {
            response.setContentType("text/javascript;charset=utf-8");
        }
        response.getWriter().write(text);
    }

    protected String getFilePath(String fileName) {
        return resourcePath + fileName;
    }
    public boolean ContainsUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return session != null && session.getAttribute(SESSION_USER_KEY) != null;
    }
}
