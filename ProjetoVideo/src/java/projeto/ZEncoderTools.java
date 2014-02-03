/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projeto;

import de.bitzeche.video.transcoding.zencoder.IZencoderClient;
import de.bitzeche.video.transcoding.zencoder.ZencoderClient;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderAPIVersion;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderNotificationJobState;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderVideoCodec;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderJob;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderOutput;
import de.bitzeche.video.transcoding.zencoder.response.ZencoderErrorResponseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;

/**
 *
 * @author Rangel
 */
public class ZEncoderTools {

    // Variáveis para o controle da codificação
    private static ZEncoderTools encoder;
    private final IZencoderClient client;
    private final XPath xPath;
    
    // URL de saída do vídeo
    private String urlOut = null;

    /**
     * 
     * @return 
     */
    public String getUrlOut() {
        return urlOut;
    }

    /**
     * 
     */
    private ZEncoderTools() {
        client = new ZencoderClient("df3956fa9bb98d381065fa454f7832ec", ZencoderAPIVersion.API_V2);
        xPath = XPathFactory.newInstance().newXPath();
    }

    /**
     * 
     * @return 
     */
    public static ZEncoderTools getEncoder() {
        if (encoder == null) {
            encoder = new ZEncoderTools();
        }
        return encoder;
    }

    /**
     * 
     * @param outputFileName
     * @return 
     */
    private ZencoderOutput createOuput(String outputFileName)
    {
        ZencoderOutput output = new ZencoderOutput("test", "s3://" + AmazonS3Tools.getOutputPath() + outputFileName);
        output.setVideoCodec(ZencoderVideoCodec.mpeg4);
        output.setAudioBitrate(64);
        output.setPublic(true);
        return output;
    }
    /**
     * 
     * @param inputFileName
     * @param outputFileName
     * @throws ZencoderErrorResponseException 
     */
    public void createJob(String inputFileName, String outputFileName) throws ZencoderErrorResponseException {

        ZencoderJob job = new ZencoderJob("s3://" + AmazonS3Tools.getBasePath() + inputFileName);
        ZencoderOutput output = createOuput(outputFileName);
        job.addOutput(output);

        client.createJob(job);

        ZencoderNotificationJobState state = client.getJobState(job);

        // Aguarda até que o JOB tenha finalizado sua execução:
        // 1. Encoding no ZEncoder
        // 2. Gravação do arquivo de saída na S3
        float porcentFinished = 0;
        while (!state.equals(ZencoderNotificationJobState.FINISHED)) {
            try {
                if (state.equals(ZencoderNotificationJobState.PROCESSING)) {
                    porcentFinished = getTransformationProgress(job.getJobId());
                    System.out.println("Andamento: " + porcentFinished + "%");
                }
            } catch (XPathExpressionException ex) {
                Logger.getLogger(ZEncoderTools.class.getName()).log(Level.SEVERE, null, ex);
            }
            state = client.getJobState(job);
        }
        
        try {
            urlOut = getRemoteTargetContentReference(job.getJobId());
        } catch (XPathExpressionException ex) {
            Logger.getLogger(ZEncoderTools.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Finalizado!! -->" + urlOut + ": porcent = " + porcentFinished+"%");
    }

    /**
     * 
     * @param jobId
     * @return
     * @throws XPathExpressionException 
     */
    private float getTransformationProgress(Integer jobId) throws XPathExpressionException {
        Document response = client.getJobProgress(jobId);
        String progress = (String) xPath.evaluate("/api-response/progress",
                response, XPathConstants.STRING);
        if (progress == null || progress.isEmpty()) {
            String stateString = (String) xPath.evaluate("/api-response/state",
                    response, XPathConstants.STRING);
            if (ZencoderNotificationJobState.getJobState(stateString) == ZencoderNotificationJobState.FINISHED) {
                progress = "100";
            } else {
                progress = "0";
            }
        }
        return new Float(progress);
    }

    /**
     * 
     * @param jobId
     * @return
     * @throws XPathExpressionException 
     */
    private String getRemoteTargetContentReference(Integer jobId) throws XPathExpressionException {
        Document response = client.getJobDetails(jobId);

        String outputUrl = (String) xPath.evaluate("/api-response/job/output-media-files/output-media-file/url",
                response, XPathConstants.STRING);
        return outputUrl;
    }
}
