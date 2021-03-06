/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oncloud6.atd.maintenances;

import com.oncloud6.atd.domain.Auto;
import com.oncloud6.atd.domain.Klant;
import com.oncloud6.atd.domain.Onderhoud;
import com.oncloud6.atd.hibernate.HibernateConnector;
import com.oncloud6.atd.rights.RightsControl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

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
        List onderhoudList = null;
        List<Klant> klantList = null;
        ArrayList<DropdownValues> values = null;
        // Connecten met hibernate
        SessionFactory factory = new HibernateConnector().getSessionFactory();
        Session hibernateSession = factory.openSession();
        Transaction tx = null;

        // Controleren of het Customer id veld is ingevuld
        boolean idSet = false;
        int customerId = 0;
        try {
            tx = hibernateSession.beginTransaction();
            
            if(session.getAttribute("userID") == null) {
                rd = request.getRequestDispatcher("error/403error.jsp");
                rd.forward(request, response);
                return;
            }

            List klantListTemp = (List<Klant>)hibernateSession.createQuery(""
                                        + "FROM Klant AS klant "
                                        + "WHERE klant.deGebruiker.id = :id")
                                        .setParameter("id", Integer.parseInt(session.getAttribute("userID").toString()))
                                        .list();
            Iterator iteTemp = klantListTemp.iterator();
            if(iteTemp.hasNext()) {
                Klant klantTijdelijk = (Klant) iteTemp.next();

                customerId = klantTijdelijk.getId();
            }
            
            String right = RightsControl.GetRightGroup("maintenances_index", session);
            
            request.setAttribute("right", right);
            
            if(right.equals("false")) {
                rd = request.getRequestDispatcher("error/403error.jsp");
                rd.forward(request, response);
                return;
            }
            
            if(right.equals("other")) {
                if (request.getParameter("cid") == null || request.getParameter("cid").equals("")) {
                    idSet = false;
                } else {
                    customerId = Integer.parseInt(request.getParameter("cid"));
                    idSet = true;
                }
            }else{
                idSet = true;
            }
            
            // Als er geen klant is geselecteerd krijgt onderhoudsList alle auto's in de tabel onderhoud mee
            if (!idSet) {
                onderhoudList = (List<Onderhoud>) hibernateSession.createQuery("FROM Onderhoud").list();

                Iterator ite = onderhoudList.iterator();
                while (ite.hasNext()) {

                    Object object = (Object) ite.next();
                    Onderhoud onderhoud = (Onderhoud) object;
                                       
                }

            } else {
                // Als er wel een klant geselecteerd is, krijgt de onderhoudsList alle auto's in de tabel van die klant mee
                onderhoudList = (List) hibernateSession.createQuery(""
                        + "FROM Onderhoud AS onderhoud "
                        + "INNER JOIN onderhoud.deAuto AS auto "
                        + "WHERE auto.klant.id = :klantid")
                        .setParameter("klantid", customerId)
                        .list();

                Iterator ite = onderhoudList.iterator();
                List<Onderhoud> newOnderhoudList = new ArrayList<Onderhoud>();
                while (ite.hasNext()) {

                    //deze lijst bevat zowel onderhoud en autos omdat je ze joint
                    Object[] objects = (Object[]) ite.next();
                    Onderhoud onderhoud = (Onderhoud) objects[0];
                    Auto auto = (Auto) objects[1];
                    
                    newOnderhoudList.add(onderhoud);
                }
                onderhoudList = newOnderhoudList;
            }

                 klantList = hibernateSession.createQuery("FROM Klant").list();
            //Gegevens van alle klanten in de klantList zetten
             values = new ArrayList<DropdownValues>();
             DropdownValues value;

             value = new DropdownValues();
             value.key = "";
             value.value = "";
             value.selected = false;
             values.add(value);
             // Klantlist doorlopen en voor elke klant in de list het klantId als key zetten en de naam als value, en deze values toevoegen aan de dropdownvalues
             for (Klant klant : klantList) {
             value = new DropdownValues();
             value.key = Integer.toString(klant.getId());
             value.value = klant.getKlantNaam();
             value.selected = false;
             values.add(value);
             }
             
            tx.commit();

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            hibernateSession.close();
        }
        // Attribuutgegevens doorgeven
         request.setAttribute("list", onderhoudList);
         request.setAttribute("klantlist", values);
         rd = request.getRequestDispatcher("maintenances/home.jsp");
         rd.forward(request, response);
         
    }
}
