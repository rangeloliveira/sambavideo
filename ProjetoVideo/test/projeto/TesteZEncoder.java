/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package projeto;

import de.bitzeche.video.transcoding.zencoder.response.ZencoderErrorResponseException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Rangel
 */
public class TesteZEncoder {
    
    private String fileName = "sample.dv";
    private final String folder = "tests";
    
    private AmazonS3Tools amazonS3Tools;
    private ZEncoderTools zEncoderTools;
    
    public TesteZEncoder() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        zEncoderTools = ZEncoderTools.getEncoder();
        amazonS3Tools = AmazonS3Tools.getAmazonS3();
        
        amazonS3Tools.FOLDER_NAME_OUPUT = folder;
    }
    
    @After
    public void tearDown() {
    }


    
    @Test
    public void testeEncoding() 
    {
        try {      
            zEncoderTools.createJob(fileName, fileName);
        } catch (ZencoderErrorResponseException ex) {
            Logger.getLogger(TesteZEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
