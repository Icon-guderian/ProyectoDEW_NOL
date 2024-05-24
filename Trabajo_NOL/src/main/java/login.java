package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONObject;




public class Login implements Filter {

	File logFile;
	HashMap<String, User> usuarios = null;
    public Login() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(true);

        // Si no hay key, login
        if (session.getAttribute("key") == null) {
            String userTomcat = req.getRemoteUser();
            User userObj = usuarios.get(userTomcat);
            if (userObj == null) {
                // Si no se encuentra
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            String user = userObj.getDni();
            String pass = userObj.getPassword();

            // JSON creds obj
            JSONObject cred = new JSONObject();
            cred.put("dni", user);
            cred.put("password", pass);
            StringEntity entity = new StringEntity(cred.toString());

            // Post a Auth Server
			/** 
            BasicCookieStore cookieStore = new BasicCookieStore();
            try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build()) {
                HttpPost httpPost = new HttpPost("http://" + req.getServerName() + ":9090/CentroEducativo/login/");
                httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                httpPost.setEntity(entity);

                try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
                    HttpEntity responseEntity = httpResponse.getEntity();
                    String keyRes = EntityUtils.toString(responseEntity);
                    EntityUtils.consume(responseEntity);

                    if (httpResponse.getStatusLine().getStatusCode() == 200 && !keyRes.equals("NOT MODIFIED")) {
                        session.setAttribute("dni", user);
                        session.setAttribute("password", pass);
                        session.setAttribute("key", keyRes);
                        session.setAttribute("cookie", cookieStore.getCookies());
                    } else {
                        res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                }
            }
			*/

            // Dependiendo del rol redirigil
            redirectToRoleBasedPage(req, res);
        } else {
            // Si la sesión tiene key redirigir
            String requestURI = req.getRequestURI();
            if (requestURI.equals(req.getContextPath() + "/index.html") || requestURI.equals(req.getContextPath() + "/")) {
                redirectToRoleBasedPage(req, res);
            } else {
                // Continue with the filter chain
                chain.doFilter(request, response);
            }
        }
    }

    private void redirectToRoleBasedPage(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (req.isUserInRole("rolalu")) {
            res.sendRedirect(req.getContextPath() + "/Alumno.html");
        } else if (req.isUserInRole("rolpro")) {
            res.sendRedirect(req.getContextPath() + "/Profesor.html");
        }
    }

    @Override
    public void destroy() {
        // Clean up any resources if necessary
    }
	
	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

}