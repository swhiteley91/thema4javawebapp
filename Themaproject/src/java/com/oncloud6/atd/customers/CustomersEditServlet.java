/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oncloud6.atd.customers;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import static sun.misc.MessageUtils.where;

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
        rd = request.getRequestDispatcher("customers/update.jsp");
        // Rechtencheck, controleren of de persoon op de pagina wel de rechten heeft om op de pagina te zijn.
      //  if(!RightsControl.checkBoolean("customers_edit", "true", session)) {
      //      rd = request.getRequestDispatcher("error/403error.jsp");
    //        rd.forward(request, response);
      //      return;
     //   }
   
        // Connecten met hibernate
         SessionFactory factory = new HibernateConnector().getSessionFactory();
        Session hibernateSession = factory.openSession();
        Transaction tx = null;
        
        // Controleren of er wel een customer is om te editen

        if (request.getParameter("cid") != null) {
            
             try {
            tx = hibernateSession.beginTransaction();
            
            // "Nieuwe" klant aanmaken.
            Klant klant = new Klant();
            // Klant de gegevens geven van de klant in de database met de parameter CustomerId
            hibernateSession.load(klant, Integer.parseInt(request.getParameter("cid")));
            String klantNaam = klant.getKlantNaam();
            String klantAdres = klant.getKlantAdres();
            Double klantKorting = klant.getKorting();
            Date klantGeboortedatum = klant.getGeboorteDatum();
            String klantPostcode = klant.getPostcode();
            String klantWoonplaats = klant.getWoonplaats();
            // Gegevens klant als attribuut zetten zodat ze in de tekstvelden worden geplaatst
            request.setAttribute("klant_naam", klantNaam);
            request.setAttribute("klant_adres", klantAdres);
            request.setAttribute("klant_korting", klantKorting);
            request.setAttribute("klant_geboortedatum", klantGeboortedatum);
             request.setAttribute("klant_postcode", klantPostcode);
            request.setAttribute("klant_woonplaats", klantWoonplaats);
            
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
           rd = request.getRequestDispatcher("customers/update.jsp");
        // Controleren of de persoon op de pagina de rechten heeft om iets te doen.
      //  if(!RightsControl.checkBoolean("customers_edit", "true", session)) {
      //      rd = request.getRequestDispatcher("error/403error.jsp");
      //      rd.forward(request, response);
      //      return;
     //   }
        // Connecten met hibernate
          SessionFactory factory = new HibernateConnector().getSessionFactory();
        Session hibernateSession = factory.openSession();
        Transaction tx = null;
           try {
            tx = hibernateSession.beginTransaction();
            
            Klant klant = new Klant();
            // "Nieuwe" klant aanmaken met de gegevens van de klant met parameter CustomerId
            hibernateSession.load(klant, Integer.parseInt(request.getParameter("cid")));
            
            // post variabelen uitzetten
            String customerName = request.getParameter("customername");
            String customerAddress = request.getParameter("customeraddress");
            String customerDiscount = request.getParameter("discount");
            Date customerDateofBirth = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("dateofbirth"));
            String customerPostcode = request.getParameter("customerpostcode");
            String customerPlace = request.getParameter("customerplace");
            
               if(customerName== null || customerName.equals("")) {
               request.setAttribute("message", "U heeft geen naam ingevuld!");
               rd.forward(request, response); 
            }
            else if(customerPostcode== null || customerPostcode.equals("")) {
               request.setAttribute("message", "U heeft geen postcode ingevuld!");
               rd.forward(request, response); 
            }
             else if(customerAddress== null || customerAddress.equals("")) {
               request.setAttribute("message", "U heeft geen adres ingevuld!");
               rd.forward(request, response); 
            }
             else if(customerPlace== null || customerPlace.equals("")) {
               request.setAttribute("message", "U heeft geen plaatsnaam ingevuld!");
               rd.forward(request, response); 
            }
             else if(customerDiscount== null || customerDiscount.equals("")) {
               request.setAttribute("message", "U heeft geen username ingevuld!");
               rd.forward(request, response); 
            }
           
             else if(customerDateofBirth== null || customerDateofBirth.equals("")) {
               request.setAttribute("message", "U heeft geen adres ingevuld!");
               rd.forward(request, response); 
            }
             else{
            // "Nieuwe" waarden aan de klant geven
            klant.setKlantNaam(customerName);
            klant.setKlantAdres(customerAddress);
            klant.setKorting(Double.parseDouble(customerDiscount));
            klant.setGeboorteDatum(customerDateofBirth);
                 klant.setWoonplaats(customerPlace);
            klant.setGeboorteDatum(customerDateofBirth);
            // Klant opslaan
            hibernateSession.update(klant);
           
            
            
            tx.commit();
             }
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
