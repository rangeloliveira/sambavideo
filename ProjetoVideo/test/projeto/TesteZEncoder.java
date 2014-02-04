package projeto;

import de.bitzeche.video.transcoding.zencoder.response.ZencoderErrorResponseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Classe de testes das operações da API do ZEnconder.
 * @author Rangel
 */
public class TesteZEncoder {
    
    private final String fileName = "sample.dv";
    private final String folder = "tests";
    
    private AmazonS3Tools amazonS3Tools;
    private ZEncoderTools zEncoderTools;
    
    public TesteZEncoder() {
    }
    
    /**
     * Configuração para execução dos testes.
     */
    @Before
    public void setUp() {
        zEncoderTools = ZEncoderTools.getEncoder();
        amazonS3Tools = AmazonS3Tools.getAmazonS3();        
        amazonS3Tools.FOLDER_NAME_OUPUT = folder;
    }
    
    /**
     * Testa a codificação de um video de exemplo disponível na S3
     * O vídeo é lido da S3 e gravado diretamente na S3.
     */
    @Test
    public void testeEncoding() 
    {
        try {      
            zEncoderTools.createJob(fileName, fileName);
        } catch (ZencoderErrorResponseException ex) {            
            Logger.getLogger(TesteZEncoder.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail(ex.getMessage());
        }
    }
}
