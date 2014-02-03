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

    private static ZEncoderTools encoder;

    private final IZencoderClient client;

    private XPath xPath;

    private ZEncoderTools() {
        client = new ZencoderClient("df3956fa9bb98d381065fa454f7832ec", ZencoderAPIVersion.API_V2);
        xPath = XPathFactory.newInstance().newXPath();
    }

    public static ZEncoderTools getEncoder() {
        if (encoder == null) {
            encoder = new ZEncoderTools();
        }
        return encoder;
    }

    public void createJob(String inputFileName, String outputFileName) throws ZencoderErrorResponseException {

        ZencoderJob job = new ZencoderJob("s3://" + AmazonS3Tools.BUCKET + AmazonS3Tools.FOLDER_SUFFIX + AmazonS3Tools.FOLDER_NAME + AmazonS3Tools.FOLDER_SUFFIX + inputFileName);

        //job.setTest(true);
        ZencoderOutput output = new ZencoderOutput("test", "s3://" + AmazonS3Tools.BUCKET + AmazonS3Tools.FOLDER_SUFFIX + AmazonS3Tools.FOLDER_NAME_OUPUT + AmazonS3Tools.FOLDER_SUFFIX + outputFileName);
        output.setAudioBitrate(64);
        output.setPublic(true);
        job.addOutput(output);

        client.createJob(job);

        ZencoderNotificationJobState state = client.getJobState(job);

        float porcentFinished = 0;
        while (!state.equals(ZencoderNotificationJobState.FINISHED)) {
            try {
                if (state.equals(ZencoderNotificationJobState.PROCESSING)) {
                    porcentFinished = getTransformationProgress("" + job.getJobId());
                    System.out.println("Andamento: " + porcentFinished + "%");
                }
            } catch (XPathExpressionException ex) {
                Logger.getLogger(ZEncoderTools.class.getName()).log(Level.SEVERE, null, ex);
            }
            state = client.getJobState(job);
        }

        String urlView = null;
        try {
            urlView = getRemoteTargetContentReference("" + job.getJobId());
        } catch (XPathExpressionException ex) {
            Logger.getLogger(ZEncoderTools.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Finalizado!! -->" + urlView + ": porcent = " + porcentFinished+"%");
    }

    protected float getTransformationProgress(String jobId) throws XPathExpressionException {
        Document response = client.getJobProgress(new Integer(jobId));
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

    protected String getRemoteTargetContentReference(String jobId) throws XPathExpressionException {
        Document response = client.getJobDetails(new Integer(jobId));
        // TODO: support for multiple outputs
        String outputUrl = (String) xPath.evaluate("/api-response/job/output-media-files/output-media-file/url",
                response, XPathConstants.STRING);
        return outputUrl;
    }

    public static void main(String[] args) {
        ZEncoderTools encoder = ZEncoderTools.getEncoder();
        try {
            encoder.createJob("sample.dv", "saida.m4v");
        } catch (ZencoderErrorResponseException ex) {
            Logger.getLogger(ZEncoderTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
