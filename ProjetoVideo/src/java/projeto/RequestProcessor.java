/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projeto;

import de.bitzeche.video.transcoding.zencoder.response.ZencoderErrorResponseException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Rangel
 */
@WebServlet(name = "RequestProcessor", urlPatterns = {"/RequestProcessor"})
public class RequestProcessor extends HttpServlet {

    // Configuração de upload
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 200; // 200MB
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 230; // 230MB

    // Variáveis para integração com a Amazon S3 e ZEncoder
    private AmazonS3Tools amazonS3Tools;
    private ZEncoderTools zEncoderTools;
    private ServletFileUpload servletFileUpload;

    /**
     * 
     */    
    public RequestProcessor() {
        configure();
    }

    /**
     * 
     */
    private void configure() {
        // Configura o upload
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Configura o limite de memória para armazenamento de arquivos em disco
        factory.setSizeThreshold(MEMORY_THRESHOLD);

        // configura o local temporário de armazenamento de arquivos
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        servletFileUpload = new ServletFileUpload(factory);

        // configura o tamanho máximo do arquivo para upload
        servletFileUpload.setFileSizeMax(MAX_FILE_SIZE);

        // configura o tamanho máximo da request (incluindo arquivo e dados do formulário)
        servletFileUpload.setSizeMax(MAX_REQUEST_SIZE);

        amazonS3Tools = AmazonS3Tools.getAmazonS3();
        zEncoderTools = ZEncoderTools.getEncoder();
    }

    
    /**
     * 
     * @param request
     * @param response 
     */
    private boolean checkRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // verifica se a request contém um arquivo para upload
        if (!ServletFileUpload.isMultipartContent(request)) {
            // if not, we stop here
            PrintWriter writer = response.getWriter();
            writer.println("Erro: O form deve estar como enctype=multipart/form-data.");
            writer.flush();
            return false;
        }
        return true;
    }
    /**
     * 
     * @param request
     * @return 
     */
    private FileItem getFileItem(HttpServletRequest request) {
        try {
            // Recupera o conteúdo da request para extração dos dados do arquivo
            @SuppressWarnings("unchecked")
            List<FileItem> formItems = servletFileUpload.parseRequest(request);

            if (formItems != null && formItems.size() > 0) {
                for (FileItem item : formItems) {
                    if (!item.isFormField()) {
                        return item;
                    }
                }
            }
        } catch (FileUploadException ex) {
            request.setAttribute("message",
                    "Houve um erro no upload do arquivo: " + ex.getMessage());
        }
        return null;
    }

    /**
     * 
     * @param request
     * @return
     * @throws IOException 
     */
    private FileItem uploadAmazonS3(HttpServletRequest request) {
        FileItem item = getFileItem(request);
        String fileName = new File(item.getName()).getName();
        try {
            // Cria um bucket do inputStream de upload na Amazon S3
            amazonS3Tools.create(item.getInputStream(), item.getSize(), fileName);
        } catch (IOException ex) {
            Logger.getLogger(RequestProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return item;
    }

    /**
     * 
     * @param item 
     */
    private void encoderWithZEncoder(FileItem item) {
        String fileName = new File(item.getName()).getName();
        try {
            zEncoderTools.createJob(fileName, fileName);
        } catch (ZencoderErrorResponseException ex) {
            Logger.getLogger(RequestProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     * 
     * É responsável pelo fluxo principal da applicação.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        if(!checkRequest(request, response))
        {
            return;
        }

        // Fase de upload para a Amazon S3
        FileItem item = uploadAmazonS3(request);
        
        // Fase de Encoding com o Zencoder
        encoderWithZEncoder(item);        
        
        // URL na Amazon S3 do arquivo convertido
        String urlOut = zEncoderTools.getUrlOut();

        request.setAttribute("message", "Upload realizado com sucesso!");
        request.setAttribute("urlOut", urlOut);

        // Redireciona para o player de vídeo
        getServletContext().getRequestDispatcher("/VideoPlayer.jsp").forward(
                request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        processRequest(request, response);
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
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
