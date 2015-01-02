/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services.upload;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.League;
import model.LeagueStructure;
import model.Match;

/**
 *
 * @author Jeff
 */
public class UploadDataServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UploadDataServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UploadDataServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        String leaguesJson = request.getParameter("leagues");
        String username = request.getParameter("username");
        String pass = request.getParameter("pass");
        
        try {
            PrintWriter out = response.getWriter();
            
            if (leaguesJson != null && !leaguesJson.isEmpty()) {
                try {
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<ArrayList<League>>(){}.getType();
                    ArrayList<League> leagues = gson.fromJson(leaguesJson, collectionType);
                    
                    for (League l : leagues) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.WEEK_OF_YEAR, 43);
                        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                        cal.set(Calendar.HOUR_OF_DAY, 19);
                        cal.set(Calendar.MINUTE, 00);
                        l.setStartDate(cal.getTime());
                        l.setMatches(MatchupGenerator.generateSeasonMatchups(l, LeagueStructure.ROUND_ROBIN));
                        
                        out.println("<h2>"+l.getName()+"</h2>");
                        out.println("<table>");
                        for (int i = 0; i < l.getMatches().size(); i++) {
                            DateFormat format = DateFormat.getInstance();
                            String date = format.format(l.getMatches().get(i).getDate());
                            out.println("<tr>");
                            out.println("<td>"+(i+1)+"</td><td>"+date+"</td><td>"+l.getMatches().get(i).getHome().getName()+"</td><td>vs</td><td>"+l.getMatches().get(i).getAway().getName()+"</td>");
                            out.println("</tr>");
                        }
                        out.println("</table>");
                    }
                } catch (Exception e) {
                    //out.print("-1");
                    out.println(e.getMessage()+":  <br />");
                    for (StackTraceElement ste : e.getStackTrace()) {
                        out.println(ste.getLineNumber()+": "+ste.getFileName()+" "+ste.getMethodName()+"<br />");
                    }
                }
            } else {
                out.print("-1");
            }
        } catch(IOException ioe) {
            
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
