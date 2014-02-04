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
        <style type="text/css">
            fieldset {
                font-family: sans-serif;
                border: 3px solid #1F497D;
                background: #ddd;
                width: 640px;
                height: 50px;
            }

            fieldset legend {
                background: #1F497D;
                color: #fff;
                padding: 5px 10px ;
                font-size: 20px;
                border-radius: 5px;
                box-shadow: 0 0 0 5px #ddd;
                margin-left: 20px;
            }
        </style>
        <title>ProjetoVideo</title>
    </head>
    <body>

        <form action="RequestProcessor" method="post" enctype="multipart/form-data">

            <fieldset style="border-radius: 5px; padding: 5px; min-height:100px;">
                <legend>Conversão de vídeo: ZEncoder/AmazonS3</legend>
                Escolha o arquivo de vídeo: <input type="file" name="mediafile" size="50">
                <br>
                <input type="submit" value="Converter">
            </fieldset>
        </form>

        <br>
        <div ></div>
        <video width="640" height="480" controls autoplay>
            <source src="<%= (String) request.getAttribute("urlOut")%>" type="video/mp4">    
            <object data="<%= (String) request.getAttribute("urlOut")%>" width="640" height="480">
                <embed src="<%= (String) request.getAttribute("urlOut")%>" width="640" height="480">
            </object> 
        </video>
    </body>
</html>
