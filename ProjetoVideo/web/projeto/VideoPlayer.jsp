<%-- 
    Document   : VideoPlayer
    Created on : Feb 3, 2014, 9:09:05 AM
    Author     : Rangel

    Arquivo proposto para resolver o problema de visualização do vídeo. Entretanto
a visualização do mesmo na página inicial ficou melhor.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Player para o vídeo convertido</title>
    </head>
    <body>
        <video width="640" height="480" controls autoplay>
            <source src="<%= (String) request.getAttribute("urlOut")%>" type="video/mp4"-->    
            <object data="<%= (String) request.getAttribute("urlOut")%>" width="640" height="480">
                <embed src="<%= (String) request.getAttribute("urlOut")%>" width="640" height="480">
            </object> 
        </video>
    </body>
</html>
