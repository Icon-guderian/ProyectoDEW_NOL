import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

@WebServlet("/dataServlet")
public class DataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String BASE_URL = "http://localhost:9090/CentroEducativo";
    private HttpClient client;

    @Override
    public void init() throws ServletException {
        // Inicializa el HttpClient al iniciar el servlet
        client = HttpClient.newHttpClient();
    }

    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Comprobamos la sesión del usuario
        // Obtiene el parámetro 'action' para determinar qué operación realizar
        String action = request.getParameter("action");
        
        // Selecciona la operación en función del parámetro 'action'


        switch(action){
            case equals("login"): //Va a haber que sacar el login de aquí
            //Y meterlo en otro .java junto con handleLogin() handleLogin lo mismo
                handleLogin(request, response);
                break;
            case equals("detallesAsignatura"):
                getAsignatura(request, response);
                break;
            case equals("listaAsignaturas"):
                getListaAsignaturas(request, response);
                break;
        }
    }

    /**
     * Maneja la operación de login.
     * @param request  La solicitud HTTP recibida.
     * @param response La respuesta HTTP a enviar.
     * @throws IOException Si ocurre un error de E/S.
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //Comprobamos si el usuario tiene una sesión
        HttpSession session = request.getSession(true);
        if(session.getAttribute("key") == null) {
            //Cogemos los datos del usuario y creamos el JSON
            String usuario = req.getRemoteUser();
            String user = usuarios.get(usuario).getDni();
            String pass = usuarios.get(usuario).getPassword();;
            JSONObject loginCredsJSON = new JSONObject();
            loginCredsJSON.put("dni", user);
            loginCredsJSON.put("password", pass);
            StringEntity loginCredsString = new StringEntity(loginCredsJSON.toString());
            
            BasicCookieStore cookieStore = new BasicCookieStore();
            CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
                HttpPost httpPost = new HttpPost(BASE_URL + "/login");
                httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                httpPost.setEntity(loginCredsString);
                CloseableHttpResponse responseFromCentro = httpclient.execute(httpPost);
                String keyRes = "-1";
                    HttpEntity resCentro = responseFromCentro.getEntity();
                    try {
                        keyRes = EntityUtils.toString(resCentro);
                        
                    }catch (ParseException e) {
                    }
                    
                    EntityUtils.consume(resCentro);
                    responseFromCentro.close();
                if(responseFromCentro.getCode() == 200 && !keyRes.equals("-1")){
                        session.setAttribute("dni", user);
			            session.setAttribute("key", keyRes);
                        response.sendRedirect(req.getContextPath() + "/alumnoPrincipal.html");
                } else {
                    // Si la respuesta es un error, envía un código de error HTTP
                    response.sendError(httpResponse.statusCode(), "Login failed");
                }
    
            }




    }


    private void getAsignatura(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //Parámetros necesarios
        HttpSession sesion = request.getSession("false");
        String key = sesion.getAttribute("key");

        String acronimo = request.getParameter("acronimo");
        


        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/asignaturas/" + acronimo + "?key=" + key))
            .header("Accept", "application/json")
            .GET()
            .build();

        try{
             // Envía la solicitud HTTP y obtiene la respuesta
             HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
             if (httpResponse.statusCode() == 200) {
                 // Si la respuesta es exitosa, establece el tipo de contenido y envía la respuesta JSON
                 response.setContentType("application/json");
                 response.getWriter().write(httpResponse.body());
                 response.setCharacterEncoding("UTF-8");
                 
                 //Mandamos la respuesta en formato json al cliente
                 PrintWriter out = response.getWriter();
                 out.write(httpResponse.body());
                 out.flush();

             } else {
                 // Si la respuesta es un error, envía un código de error HTTP
                 response.sendError(httpResponse.statusCode(), "Error inesperado");
             }
         } catch (InterruptedException e) {
             // Maneja la interrupción de la solicitud
             response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Proceso interrumpido");
        }

    private void getListaAsignaturas(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //Cogemos la clave
        HttpSession sesion = request.getSession("false");
        String key = sesion.getAttribute("key");
        
        //Creamos la petición
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/asignaturas?key=" + key))
            .header("Accept", "application/json")
            .GET()
            .build();

        try{
             // Envía la solicitud HTTP y obtiene la respuesta
             HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
             if (httpResponse.statusCode() == 200) {
                 // Si la respuesta es exitosa, establece el tipo de contenido y envía la respuesta JSON
                 response.setContentType("application/json");
                 response.getWriter().write(httpResponse.body());
                 response.setCharacterEncoding("UTF-8");
                 
                 //Mandamos la respuesta en formato json al cliente
                 PrintWriter out = response.getWriter();
                 out.write(httpResponse.body());
                 out.flush();

             } else {
                 // Si la respuesta es un error, envía un código de error HTTP
                 response.sendError(httpResponse.statusCode(), "Error inesperado");
             }
         } catch (InterruptedException e) {
             // Maneja la interrupción de la solicitud
             response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Proceso interrumpido");
        }          
    }
    /**
     * Maneja la operación de obtención de calificaciones.
     * @param request  La solicitud HTTP recibida.
     * @param response La respuesta HTTP a enviar.
     * @throws IOException Si ocurre un error de E/S.
     */
    private void handleGetGrades(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Obtiene los parámetros de la solicitud
        String sessionKey = request.getParameter("sessionKey");
        String dni = request.getParameter("dni");
        
        // Construye la solicitud HTTP para obtener las calificaciones
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/alumnos/" + dni + "/notas?key=" + sessionKey))
            .header("Accept", "application/json")
            .GET()
            .build();

        try {
            // Envía la solicitud HTTP y obtiene la respuesta
            HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() == 200) {
                // Si la respuesta es exitosa, establece el tipo de contenido y envía la respuesta JSON
                response.setContentType("application/json");
                response.getWriter().write(httpResponse.body());
            } else {
                // Si la respuesta es un error, envía un código de error HTTP
                response.sendError(httpResponse.statusCode(), "Error inesperado");
            }
        } catch (InterruptedException e) {
            // Maneja la interrupción de la solicitud
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Proceso interrumpido");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Llama al método doGet para manejar las solicitudes POST de la misma manera que las GET
        doGet(request, response);
    }
}