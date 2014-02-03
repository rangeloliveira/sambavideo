<%-- 
    Document   : index
    Created on : 31/01/2014, 23:32:16
    Author     : Rangel
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>ProjetoVideo</title>
    </head>
    <body>
        <h1>Conversão de vídeo: ZEncoder/AmazonS3</h1>
        <form action="FileUpload" method="post" enctype="multipart/form-data">
            Escolha o arquivo de vídeo: <input type="file" name="mediafile" size="40"> <br>
            <input type="submit" value="Converter">
        </form>    

        
    </body>
</html>
