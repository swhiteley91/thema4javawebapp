/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oncloud6.atd.maintainances;

import com.oncloud6.atd.mysql.MySQLConnection;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.out;
import static java.lang.reflect.Array.set;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import static sun.misc.MessageUtils.out;
import static sun.misc.MessageUtils.where;

/**
 *
 * @author Laura van den Heuvel
 */
@WebServlet(name = "MaintenancesIndexServlet", urlPatterns = {"/maintenances"})
public class MaintenancesIndexServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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
        RequestDispatcher rd = null;
        HttpSession session = request.getSession(true);

        

            // get dbconnection
            MySQLConnection DBConnection = new MySQLConnection();
            // try it out
            try {
                Connection connect = DBConnection.getConnection();

                // prepare query
                PreparedStatement preparedStatement = connect.prepareStatement("SELECT onderhoud_id, onderhoud_bedrijf_id, onderhoud_auto_id, onderhoud_datum, onderhoud_beschrijving, onderhoud_status, onderhoud_manuur FROM atd.onderhoud");
        
                // voer de query uit en get result
                ResultSet result = preparedStatement.executeQuery();

             
               
                ArrayList<MaintainanceList> list = new ArrayList<MaintainanceList>();
                
                while(result.next()){
                    
                    // Waarden ophalen uit de database en plaatsen in de list
                  MaintainanceList maintainances = new MaintainanceList();
                maintainances.onderhoudId = result.getInt("onderhoud_id");
                maintainances.bedrijfsId = result.getInt("onderhoud_bedrijf_id");
                maintainances.autoId = result.getInt("onderhoud_auto_id");
                maintainances.datum = result.getDate("onderhoud_datum");
                maintainances.beschrijving = result.getString("onderhoud_beschrijving");
                maintainances.status = result.getString("onderhoud_status");
                maintainances.manuur = result.getInt("onderhoud_manuur");
                      
             
                // toevoegen aan de arraylist
                list.add(maintainances);

                
           
                }
              // request variabelen setten
                request.setAttribute("list", list);
                // afsluiten 
                preparedStatement.close();
                connect.close();

                rd = request.getRequestDispatcher("maintainances/index.jsp");
                rd.forward(request, response);

            } catch (Exception ex) {
                Logger.getLogger(MaintenancesIndexServlet.class.getName()).log(Level.SEVERE, null, ex);
            }

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
        MySQLConnection DBConnection = new MySQLConnection();
        try {

            RequestDispatcher rd = null;
            HttpSession session = request.getSession(true);

            doGet(request, response);

        } catch (Exception ex) {
            Logger.getLogger(MaintenancesIndexServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
