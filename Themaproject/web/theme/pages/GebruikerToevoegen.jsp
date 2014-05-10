<%-- 
    Document   : GebruikerToevoegen
    Created on : 10-mei-2014, 13:41:39
    Author     : Simon Whiteley <simonwhiteley@hotmail.com>
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="../header.jsp" />
<!-- Main jumbotron for a primary marketing message or call to action -->
<div class="jumbotron">
    <div class="container">
        <h2>Gebruiker toevoegen</h2>
    </div>
</div>

<div class="container">
    <div class="row">
        <form action="UserAdd" method="post">
        <div class="input-group input-group-lg">
            <span class="input-group-addon">#</span>
            <input type="text" class="form-control" placeholder="Gebruikersnaam">
        </div>
        <div class="input-group input-group-lg">
            <span class="input-group-addon">#</span>
            <input type="text" class="form-control" placeholder="Wachtwoord">
        </div>
            
           <br/> <input type="submit"  class="btn btn-default" value="Voltooien" />
            
    </div>
    <jsp:include page="../footer.jsp" />