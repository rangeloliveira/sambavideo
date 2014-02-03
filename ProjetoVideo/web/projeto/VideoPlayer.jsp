<%-- 
    Document   : VideoPlayer
    Created on : Feb 3, 2014, 9:09:05 AM
    Author     : Rangel
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Player para o vídeo convertido</title>
    </head>
    <body>
        <a href="<%= (String) request.getAttribute("urlOut")%>">
            Assistir ao video</a>
    </body>
</html>
