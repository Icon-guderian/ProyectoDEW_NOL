import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

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
    private static final String BASE_URL = "http://localhost:9090/CentroEducativo/alumnos/";
	private HashMap<String, String> asignaturasAlumno = new HashMap<String, String>();
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
        
        HttpSession session = request.getSession(false);
        
        
        
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
                 response.
                 
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