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
 * Classe que organiza as operações necessárias do ZEncoder
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
     * @return recupera a URL de saída do vídeo
     */
    public String getUrlOut() {
        return urlOut;
    }

    /**
     * Construtor privado para forçar apenas uma instância do objeto.
     */
    private ZEncoderTools() {
        client = new ZencoderClient("df3956fa9bb98d381065fa454f7832ec", ZencoderAPIVersion.API_V2);
        xPath = XPathFactory.newInstance().newXPath();
    }

    /**
     * 
     * @return cria ou retorna uma instância desta classe
     */
    public static ZEncoderTools getEncoder() {
        if (encoder == null) {
            encoder = new ZEncoderTools();
        }
        return encoder;
    }

    /**
     * Configura o arquivo de saída de vídeo.
     * 
     * @param outputFileName nome do arquivo convertido de saída a ser gravado novamente na S3.
     * @return 
     */
    private ZencoderOutput createOuput(String outputFileName)
    {
        ZencoderOutput output = new ZencoderOutput("test", "s3://" + AmazonS3Tools.getOutputPath() + outputFileName);
        output.setAudioBitrate(64);
        output.setPublic(true);
        return output;
    }
    /**
     * O método cria um Job no ZEncoder para que o vídeo de entrada possa ser convertido.
     * Depois ele monitora o status da codificação e termina quando o Job no ZEncoder é finalizado.
     * O ZEncoder grava o arquivo de saída diretamente na S3.
     * 
     * @param inputFileName arquivo de entrada do ZEncoder
     * @param outputFileName arquivo de saída do ZEncoder
     * @throws ZencoderErrorResponseException 
     */
    public void createJob(String inputFileName, String outputFileName) throws ZencoderErrorResponseException {

        ZencoderJob job = new ZencoderJob("s3://" + AmazonS3Tools.getBasePath() + inputFileName);
        ZencoderOutput output = createOuput(outputFileName);
        job.addOutput(output);

        // Cria um Job no ZEncoder
        client.createJob(job);

        // Recupera o status remoto do Job
        ZencoderNotificationJobState state = client.getJobState(job);

        // Aguarda até que o JOB tenha finalizado sua execução:
        // 1. Encoding no ZEncoder
        // 2. Gravação do arquivo de saída na S3
        float porcentFinished = 0;
        while (!state.equals(ZencoderNotificationJobState.FINISHED)) {
            try {
                if (state.equals(ZencoderNotificationJobState.PROCESSING)) {
                    porcentFinished = getProgress(job.getJobId());
                    System.out.println("Andamento: " + porcentFinished + "%");
                }
            } catch (XPathExpressionException ex) {
                Logger.getLogger(ZEncoderTools.class.getName()).log(Level.SEVERE, null, ex);
            }
            state = client.getJobState(job);
        }
        
        // Recupera a URL na S3 do arquivo gerado pelo ZEncoder.
        try {
            urlOut = getRemoteURL(job.getJobId());
        } catch (XPathExpressionException ex) {
            Logger.getLogger(ZEncoderTools.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Finalizado!! -->" + urlOut + ": porcent = " + porcentFinished+"%");
    }

    /**
     * Recupera remotamente o status de um determinado Job
     * @param jobId id do Job
     * @return percentual de andamento do Job
     * 
     * @throws XPathExpressionException 
     */
    private float getProgress(Integer jobId) throws XPathExpressionException {
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
     * Recupera a URL do arquivo de saída a partir do ZEncoder.
     * 
     * @param jobId id do Job
     * @return URL do arquivo de saída.
     * @throws XPathExpressionException 
     */
    private String getRemoteURL(Integer jobId) throws XPathExpressionException {
        Document response = client.getJobDetails(jobId);

        String outputUrl = (String) xPath.evaluate("/api-response/job/output-media-files/output-media-file/url",
                response, XPathConstants.STRING);
        return outputUrl;
    }
}
