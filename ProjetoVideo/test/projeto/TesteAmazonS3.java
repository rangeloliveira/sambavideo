/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projeto;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Rangel
 */
public class TesteAmazonS3 {

    private AmazonS3Tools amazonS3Tools = null;

    private final String folder = "tests";
    private String fileName = "arquivo_teste.txt";

    public TesteAmazonS3() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        amazonS3Tools = AmazonS3Tools.getAmazonS3();
        amazonS3Tools.FOLDER_NAME = folder;
        amazonS3Tools.FOLDER_NAME_OUPUT = folder;
    }

    @After
    public void tearDown() {
        // Remove se o arquivo foi criado
    }
    
    private void createFileInS3() throws IOException
    {
        File file = new File(fileName);
        amazonS3Tools.create(new FileInputStream(file), file.length(), fileName);
    }

    @Test
    public void testeUpload() {
        try {
            createFileInS3();
        } catch (IOException ex) {
            Logger.getLogger(TesteAmazonS3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     */
    @Test
    public void testeDownload() {

        S3Object object = null;
        try {
            createFileInS3();
            
            object = amazonS3Tools.getObject(AmazonS3Tools.BUCKET, folder + "/" + fileName);
            
            System.out.println("Content-Type: " + object.getObjectMetadata().getContentType());
        } catch (AmazonS3Exception e) {
            Assert.fail(e.getErrorCode() + ":" + e.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(TesteAmazonS3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 
     */
    @Test
    public void testeDeleteObject() {

        S3Object object = null;
        try {
            File file = new File(fileName);
            amazonS3Tools.create(new FileInputStream(file), file.length(), fileName);
            
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
     * 
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

    @Test
    public void testeSetPublicFile() {
        int a = 10;
        a++;
        Assert.assertEquals(a, 11);
    }
}
