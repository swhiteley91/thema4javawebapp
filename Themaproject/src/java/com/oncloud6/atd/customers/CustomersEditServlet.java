/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oncloud6.atd.customers;

import com.oncloud6.atd.domain.Factuur;
import com.oncloud6.atd.domain.Klant;
import com.oncloud6.atd.hibernate.HibernateConnector;
import com.oncloud6.atd.mysql.MySQLConnection;
import com.oncloud6.atd.rights.RightsControl;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.reflect.Array.set;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import static sun.misc.MessageUtils.where;
import com.oncloud6.atd.customers.DropdownValues;

/**
 *
 * @author Simon Whiteley <simonwhiteley@hotmail.com>
 */
@WebServlet(name = "CustomersEditServlet", urlPatterns = {"/customersedit"})
public class CustomersEditServlet extends HttpServlet {

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
        
        HttpSession session = request.getSession(true);
        RequestDispatcher rd = null;
        rd = request.getRequestDispatcher("customers/edit.jsp");
       
        String right = RightsControl.GetRightGroup("customers_edit", session);
        
        // Connecten met hibernate
        SessionFactory factory = new HibernateConnector().getSessionFactory();
        Session hibernateSession = factory.openSession();
        Transaction tx = null;
        
        if(session.getAttribute("userID") == null) {
            rd = request.getRequestDispatcher("error/403error.jsp");
            rd.forward(request, response);
            return;
        }
        
        List klantList = (List<Klant>)hibernateSession.createQuery(""
				    + "FROM Klant AS klant "
				    + "WHERE klant.deGebruiker.id = :id")
				    .setParameter("id", Integer.parseInt(session.getAttribute("userID").toString()))
				    .list();
        Iterator ite = klantList.iterator();
        int id = 0;
        if(ite.hasNext()) {
            Klant klantTijdelijk = (Klant) ite.next();
        
            id = klantTijdelijk.getId();
        }
            if(!RightsControl.checkGroup("customers_edit", "own", session, id)) {
                rd = request.getRequestDispatcher("error/403error.jsp");
                rd.forward(request, response);
                return;
            }
        
        if(right.equals("other")) {
             if (request.getParameter("id") != null && !request.getParameter("id").equals("")) {
                id = Integer.parseInt(request.getParameter("id").toString());
            }
            List<Klant> klantenList = null;
        ArrayList<DropdownValues> values = null;
        klantenList = hibernateSession.createQuery("FROM Klant").list();
            values = new ArrayList<DropdownValues>();
             DropdownValues value;

             value = new DropdownValues();
             value.key = "";
             value.value = "";
             value.selected = false;
             values.add(value);
             // Klantlist doorlopen en voor elke klant in de list het klantId als key zetten en de naam als value, en deze values toevoegen aan de dropdownvalues
             for (Klant klant : klantenList) {
             value = new DropdownValues();
             value.key = Integer.toString(klant.getId());
             value.value = klant.getKlantNaam();
             value.selected = false;
             values.add(value);
             }
        request.setAttribute("klantlist", values);
        }
        
        // Controleren of er wel een customer is om te editen

            
             try {
            tx = hibernateSession.beginTransaction();
            
            // "Nieuwe" klant aanmaken.
            Klant klant = new Klant();
            // Klant de gegevens geven van de klant in de database met de parameter CustomerId
            klant = (Klant)hibernateSession.get(Klant.class, id);
            if(klant == null) {
                rd = request.getRequestDispatcher("error/404error.jsp");
                rd.forward(request, response);
                return;
            }
            String klantNaam = klant.getKlantNaam();
            String klantAdres = klant.getKlantAdres();
            Double klantKorting = klant.getKorting();
            Date klantGeboortedatum = klant.getGeboorteDatum();
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String geboorteDatum = df.format(klantGeboortedatum);
            String klantPostcode = klant.getPostcode();
            String klantWoonplaats = klant.getWoonplaats();
            String klantWachtwoord = klant.getGebruiker().getPassword();
            
            // Gegevens klant als attribuut zetten zodat ze in de tekstvelden worden geplaatst
            request.setAttribute("klant_naam", klantNaam);
            request.setAttribute("klant_adres", klantAdres);
            request.setAttribute("klant_korting", klantKorting);
            request.setAttribute("klant_geboortedatum", geboorteDatum);
             request.setAttribute("klant_postcode", klantPostcode);
            request.setAttribute("klant_woonplaats", klantWoonplaats);
            request.setAttribute("klant_wachtwoord", klantWachtwoord);
            request.setAttribute("right",right);
            
            hibernateSession.update(klant);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            hibernateSession.close();
        }
             
        rd.forward(request, response);

           

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
          HttpSession session = request.getSession(true);
        RequestDispatcher rd = null;
           rd = request.getRequestDispatcher("customers/edit.jsp");
        
        if(session.getAttribute("userID") == null) {
            rd = request.getRequestDispatcher("error/403error.jsp");
            rd.forward(request, response);
            return;
        }
      
        // Connecten met hibernate
          SessionFactory factory = new HibernateConnector().getSessionFactory();
        Session hibernateSession = factory.openSession();
        Transaction tx = null;
           try {
            tx = hibernateSession.beginTransaction();
            
            String right = RightsControl.GetRightGroup("customers_edit", session);
   
        List klantList = (List<Klant>)hibernateSession.createQuery(""
				    + "FROM Klant AS klant "
				    + "WHERE klant.deGebruiker.id = :id")
				    .setParameter("id", Integer.parseInt(session.getAttribute("userID").toString()))
				    .list();
        Iterator ite = klantList.iterator();
        int id = 0;
        if(ite.hasNext()) {
            Klant klantTijdelijk = (Klant) ite.next();
        
            id = klantTijdelijk.getId();
        }
            if(!RightsControl.checkGroup("customers_edit", "own", session, id)) {
                rd = request.getRequestDispatcher("error/403error.jsp");
                rd.forward(request, response);
                return;
            }
        if(right.equals("other")) {
            if(request.getParameter("customerselect") != null && !request.getParameter("customerselect").equals("")) {
                response.sendRedirect("customersedit?id="+request.getParameter("customerselect"));
                return;
            } 
            
            if (request.getParameter("id") != null) {
                id = Integer.parseInt(request.getParameter("id").toString());
            }
            
                        List<Klant> klantenList = null;
        ArrayList<DropdownValues> values = null;
        klantenList = hibernateSession.createQuery("FROM Klant").list();
            values = new ArrayList<DropdownValues>();
             DropdownValues value;

             value = new DropdownValues();
             value.key = "";
             value.value = "";
             value.selected = false;
             values.add(value);
             // Klantlist doorlopen en voor elke klant in de list het klantId als key zetten en de naam als value, en deze values toevoegen aan de dropdownvalues
             for (Klant klant : klantenList) {
             value = new DropdownValues();
             value.key = Integer.toString(klant.getId());
             value.value = klant.getKlantNaam();
             value.selected = false;
             values.add(value);
             }
        request.setAttribute("klantlist", values);
        }
            
            // "Nieuwe" klant aanmaken.
            Klant klant = new Klant();
            // Klant de gegevens geven van de klant in de database met de parameter CustomerId
            klant = (Klant)hibernateSession.get(Klant.class, id);
            if(klant == null) {
                rd = request.getRequestDispatcher("error/404error.jsp");
                rd.forward(request, response);
                return;
            }
            
            
            request.setAttribute("right", right);
            
            
            String customerName = request.getParameter("customername");
            String customerAddress = request.getParameter("customeraddress");
            String customerDiscount = "";
            if(right.equals("other")) {
                customerDiscount = request.getParameter("discount");
            }
            String customerDateofBirth = request.getParameter("dateofbirth");
            String customerPostcode = request.getParameter("customerpostcode");
            String customerPlace = request.getParameter("customerplace");
            String customerPassword = request.getParameter("password");
            
            request.setAttribute("klant_naam", customerName);
            request.setAttribute("klant_adres", customerAddress);
            if(right.equals("other")) {
                request.setAttribute("klant_korting", customerDiscount);
            }
            request.setAttribute("klant_geboortedatum", customerDateofBirth);
             request.setAttribute("klant_postcode", customerPostcode);
            request.setAttribute("klant_woonplaats", customerPlace);
            request.setAttribute("klant_wachtwoord", customerPassword);
            
            // post variabelen uitzetten
            
            
            if(customerName== null || customerName.equals("")) {
               request.setAttribute("message", "U heeft geen naam ingevuld!");
               rd.forward(request, response); 
               return;
            }
            if(customerPostcode== null || customerPostcode.equals("")) {
               request.setAttribute("message", "U heeft geen postcode ingevuld!");
               rd.forward(request, response); 
               return;
            }
            if(customerAddress== null || customerAddress.equals("")) {
               request.setAttribute("message", "U heeft geen adres ingevuld!");
               rd.forward(request, response); 
               return;
            }
            if(customerPlace== null || customerPlace.equals("")) {
               request.setAttribute("message", "U heeft geen plaatsnaam ingevuld!");
               rd.forward(request, response); 
               return;
            }
            if(right.equals("other")) {
                if(customerDiscount== null || customerDiscount.equals("")) {
                   request.setAttribute("message", "U heeft geen korting ingevuld!");
                   rd.forward(request, response); 
                   return;
                }
            }
            if(customerDateofBirth== null || customerDateofBirth.equals("")) {
               request.setAttribute("message", "U heeft geen adres ingevuld!");
               rd.forward(request, response); 
               return;
            }
            if(customerPassword== null || customerPassword.equals("")) {
               request.setAttribute("message", "U heeft geen wachtwoord ingevuld!");
               rd.forward(request, response); 
               return;
            }
            // "Nieuwe" waarden aan de klant geven
            klant.setKlantNaam(customerName);
            klant.setKlantAdres(customerAddress);
             if(right.equals("other")) {
                   klant.setKorting(Double.parseDouble(customerDiscount));
            }
         
          Date dateofBirth = new SimpleDateFormat("dd-MM-yyyy").parse(customerDateofBirth);
                 klant.setWoonplaats(customerPlace);
            klant.setGeboorteDatum(dateofBirth);
            klant.getGebruiker().setPassword(customerPassword);
            
            if(right.equals("other")) {
                klant.setKorting(Double.parseDouble(customerDiscount));
            }
            // Klant opslaan
            hibernateSession.update(klant);
             request.setAttribute("message", "Gegevens zijn aangepast!");
            rd.forward(request, response); 
            
            
               tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } catch (ParseException ex) {
            Logger.getLogger(CustomersEditServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            hibernateSession.close();
        }

      

    }

}
