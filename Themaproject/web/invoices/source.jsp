<%-- 
    Document   : source
    Created on : Jun 23, 2014, 11:27:59 AM
    Author     : Jelle
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.oncloud6.atd.domain.Factuur" %>
<%@page import="com.oncloud6.atd.domain.FactuurItem" %>
<% Factuur factuur = (Factuur)request.getAttribute("factuur"); %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <link href="assets/invoices/Site.css" rel="stylesheet" type="text/css" />
    <link href="assets/invoices/Site.print.css" rel="stylesheet" type="text/css" />
    <link href="assets/invoices/bootstrap.min.css" rel="stylesheet" type="text/css" />
    <link href="assets/invoices/bootstrap.print.css" rel="stylesheet" type="text/css" />
    
</head>
<body>
    
    <div class="container-fluid">

<table class="invoiceHeader">
<tr>
<td>
    <h1>AutoTotaalDiensten</h1>
</td>
<td style="text-align:right">
 <h2>FAC${factuur.getFactuurNummer()}</h2> 
 ${factuur.getFactuurDatum()}
</td>
</tr>
</table>

<div class="from">
    <blockquote>
    <p>    <strong>
    Auto Totaal diensten B.V.
    </strong>
    </p>

    <p>
    Hatseflats 15<br/> 1234 AB  Utrecht<br/>the Netherlands
    </p>
    <p>
    Telefoon: +31 (0)30 1234567
    </p>

    <p>
    administratie@autototaaldiensten.nl
    </p>

    </blockquote>
</div>

 <table>
 <tr>
    <td width="65%">
            <h3>Klant :</h3>
            <blockquote>
            <p>
                <strong>
                    ${factuur.getKlantNaam()}
                </strong>
            </p>
             <p>
                ${factuur.getKlantAdres()}
            </p>	      
            </blockquote>
    </td>
    <td>
        
    </td>
 </tr>
 </table>

<table class='zebra-striped'>
    <thead>
        <th>Item</th>
        <th width='40'>Aantal</th>
        <th width='75'>Prijs</th>
        <th width='80'>BTW %</th>
        <th width='80'>SubTotaal</th>
    </thead>
    <%
    for(FactuurItem item : factuur.getDeFactuurItems()) {
        %>
        <tr id="invoiceDetail<%=item.getFactuurItemId() %>">
            <td><%=item.getFactuurItemNaam() %></td>
            <td><span style="float:right;"><%=item.getFactuurItemHoeveelheid() %></span></td>
            <td><span style="float:right;">&euro; <%=item.getFactuurItemPrijs() %></span></td>
            <td><span style="float:right;">21%</span></td>
            <td><span style="float:right;">&euro; <%=item.getFactuurItemSubtotaal() %></span></td>
        </tr>
        <% 
    } %>
</table>

<table>
    <tr>
        <td colspan="5" class="invoiceFooterTitle">Factuur korting</td>
        <td width="200" class="invoiceFooterValue">${factuur.getFactuurKorting()}%</td>
    </tr>
    <tr>
        <td colspan="5" class="invoiceFooterTitle">SubTotaal:</td>
        <td width="200" class="invoiceFooterValue">&euro; ${factuur.getSubTotaalBedrag()}</td>
    </tr>
    <tr>
        <td colspan="5" class="invoiceFooterTitle">BTW:</td>
        <td width="200" class="invoiceFooterValue">&euro; ${factuur.getBtwBedrag()}</td>
    </tr>
    <tr>
        <td colspan="5" class="invoiceFooterTitle">Totaal te betalen: </td>
        <td class="invoiceFooterValueTotal">&euro; ${factuur.getTotaalBedrag()}</td>
    </tr>
</table>

<div class="invoiceFooter">
    <p>
    Gelieve binnen 30 dagen na dagtekening te voldoen op onze bankrekening,<br />
    Betalings referentie: <asp:Label ID="lblInvoiceNumberBottom" runat="server" /><br />
    IBAN/SEPA Nummer: NL93RABO0178376287<br />
    BIC Nummer: RABONL2U<br />
    Ten name van: Auto Totaal Diensten B.V.<br />
    Plaats: Utrecht<br />
    Bank: Rabobank<br />
    KVK nummer: 26484053<br />
    BTW Nummer: NL368426852B01
    </p>
    

</div>

    </div>
</body>
</html>
