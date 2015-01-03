/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services.upload;

import DAL.LeagueDAO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.League;
import model.LeagueStructure;
import model.Match;
import model.Team;

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
        String season = request.getParameter("season");
        
        try {
            PrintWriter out = response.getWriter();
            
            /*
            *
            * OBS KOLLA OM USERNAME OCH LÖSENORD STÄMMER OCH OM USERNAME HAR EN ROLL SOM TILLÅTS GÖRA DETTA (League Administrator)!
            *
            */
            
            if (leaguesJson != null && !leaguesJson.isEmpty()) {
                try {
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<ArrayList<League>>(){}.getType();
                    ArrayList<League> leagues = gson.fromJson(leaguesJson, collectionType);
                    
                    for (League l : leagues) {
                        l.setSeason(String.valueOf(Integer.parseInt(season))); // Kollar så att det är ett giltigt år.
                        l.setMatches(MatchupGenerator.generateSeasonMatchups(l, LeagueStructure.ROUND_ROBIN));
                    }
                    
                    LeagueDAO dao = LeagueDAO.getInstance();
                    leagues = dao.uploadLeagueData(leagues);
                    
                    for (League l : leagues) {
                        ArrayList<Match> matches = dao.getMatchesForLeague(l);
                        Iterator<Match> it = matches.iterator();
                        Date today = new Date();
                        while (it.hasNext()) {
                            Match match = it.next();
                            if (match.getDate().before(today))
                                MatchupGenerator.generateRandomScores(match);
                            else
                                it.remove(); // Vi vill inte ha match-resultat för matcher som inte har passerat
                        }
                        
                        dao.uploadMatchResults(matches);
                    }
                    
                    for (League l : leagues) {
                        out.println("<h2>"+l.getName()+" ("+l.getId()+")</h2>");
                        out.println("<table>");
                        for (int i = 0; i < l.getMatches().size(); i++) {
                            DateFormat format = DateFormat.getInstance();
                            String date = format.format(l.getMatches().get(i).getDate());
                            out.println("<tr>");
                            Team home = l.getMatches().get(i).getHome();
                            Team away = l.getMatches().get(i).getAway();
                            out.println("<td>"+(i+1)+"</td><td>"+date+"</td><td>"+home.getName()+" ("+home.getId()+")</td><td>vs</td><td>"+away.getName()+" ("+away.getId()+")</td>");
                            out.println("</tr>");
                        }
                        out.println("</table>");
                    }
                } catch (NumberFormatException nfe) {
                    out.print("Ogiltigt år valt!");
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
