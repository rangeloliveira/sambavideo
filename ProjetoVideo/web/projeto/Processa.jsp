<%-- 
    Document   : Processa
    Created on : Feb 1, 2014, 12:10:29 AM
    Author     : Rangel
--%>

<%@page import="java.io.DataOutputStream"%>
<%@page import="java.io.DataInputStream"%>
<%@page import="java.io.File"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.apache.tomcat.util.http.fileupload.FileItem"%>
<%@page import="org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload"%>
<%@page import="org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Arquivo enviado como parâmetro</h1>
        <p><%= request.getParameter("mediafile")%></p>
        
        <%
   String contentType = request.getContentType();
      if ((contentType != null) && (contentType.indexOf("multipart/form-data") >= 0)) {
            DataInputStream in = new DataInputStream(request.getInputStream());
            int formDataLength = request.getContentLength();
            
            DataOutputStream out2 = new DataOutputStream(request.getInputStream());
            
            
            
  

%>
                
    </body>
</html>
