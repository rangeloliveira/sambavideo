package projeto;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * Classe de testes das operações da Amazon S3.
 * @author Rangel
 */
public class TesteAmazonS3 {

    private AmazonS3Tools amazonS3Tools = null;

    private final String folder = "tests";
    private String fileName = "arquivo_teste.txt";

    public TesteAmazonS3() {
    }

    /**
     * Configuração inicial para execução dos testes.
     */
    @Before
    public void setUp() {
        amazonS3Tools = AmazonS3Tools.getAmazonS3();
        amazonS3Tools.FOLDER_NAME = folder;
        amazonS3Tools.FOLDER_NAME_OUPUT = folder;
    }
    
    private void createFileInS3() throws IOException
    {
        File file = new File(fileName);
        amazonS3Tools.createObject(new FileInputStream(file), file.length(), fileName);
    }

    /**
     * Realiza o teste de upload do arquivo para a Amazon S3
     */
    @Test
    public void testeUpload() {
        try {
            createFileInS3();
        } catch (IOException ex) {
            Logger.getLogger(TesteAmazonS3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Teste de download do arquivo da Amazon S3
     */
    @Test
    public void testeDownload() {

        S3Object object = null;
        try {
            createFileInS3();
            
            object = amazonS3Tools.getObject(AmazonS3Tools.BUCKET, folder + "/" + fileName);
            Assert.assertNotNull(object);
            
        } catch (AmazonS3Exception e) {
            Assert.fail(e.getErrorCode() + ":" + e.getMessage());
        } catch (IOException ex) {
            Assert.fail(ex.getMessage());
            Logger.getLogger(TesteAmazonS3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Teste de remoção do arquivo da S3
     */
    @Test
    public void testeDeleteObject() {

        S3Object object = null;
        try {
            File file = new File(fileName);
            amazonS3Tools.createObject(new FileInputStream(file), file.length(), fileName);
            
            amazonS3Tools.deleteObject(AmazonS3Tools.BUCKET, folder + "/" + fileName);
            object = amazonS3Tools.getObject(AmazonS3Tools.BUCKET, folder + "/" + fileName);
            System.out.println("Content-Type: " + object.getObjectMetadata().getContentType());
        } catch (AmazonS3Exception e) {
            Assert.assertEquals("NoSuchKey", e.getErrorCode());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TesteAmazonS3.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TesteAmazonS3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Teste para recuperar a URL pública de um determinado arquivo na S3
     */
    @Test
    public void testeGetURL() {
        try {
            createFileInS3();
        } catch (IOException ex) {
            Logger.getLogger(TesteAmazonS3.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        URL url = amazonS3Tools.getURL(AmazonS3Tools.BUCKET, folder + "/" + fileName);
        String urlExpected = "https://rangelsambavideo.s3.amazonaws.com/tests/arquivo_teste.txt";
        
        Assert.assertEquals(urlExpected, url.toExternalForm());
    }
}
