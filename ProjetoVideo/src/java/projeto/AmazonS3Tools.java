package projeto;

import java.io.InputStream;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import java.io.IOException;
import java.net.URL;

public class AmazonS3Tools {

    public static final String BUCKET = "rangelsambavideo";
    public final static String FOLDER_SUFFIX = "/";

    public static String FOLDER_NAME = "input";
    public static String FOLDER_NAME_OUPUT = "output";

    private final AmazonS3Client client;
    private static AmazonS3Tools amazonS3Tools;

    private AmazonS3Tools() {
        // Cria o cliente S3, de acordo com o arquivo de configuração
        client = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
    }

    /**
     * 
     * @return 
     */
    public static AmazonS3Tools getAmazonS3() {
        if (amazonS3Tools == null) {
            amazonS3Tools = new AmazonS3Tools();
        }
        return amazonS3Tools;
    }

    /**
     *
     * @return
     */
    public static String getBasePath() {
        return AmazonS3Tools.BUCKET + AmazonS3Tools.FOLDER_SUFFIX + AmazonS3Tools.FOLDER_NAME + AmazonS3Tools.FOLDER_SUFFIX;
    }
    
      /**
     *
     * @return
     */
    public static String getOutputPath() {
        return AmazonS3Tools.BUCKET + AmazonS3Tools.FOLDER_SUFFIX + AmazonS3Tools.FOLDER_NAME_OUPUT + AmazonS3Tools.FOLDER_SUFFIX;
    }

    /**
     * 
     * @param inputStream
     * @param lenght
     * @param fileName
     * @throws IOException 
     */
    public void create(InputStream inputStream, Long lenght, String fileName) throws IOException {

        // Configura o metadata de acordo com o tamanho do fluxo de entrada
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(lenght);

        // Criação objeto alvo de criação na S3
        PutObjectRequest putObjectRequest
                = new PutObjectRequest(BUCKET, FOLDER_NAME + FOLDER_SUFFIX + fileName,
                        inputStream, metadata);
                        //.withCannedAcl(CannedAccessControlList.PublicRead); já adicionei as políticas para o ZEncoder diretamente na S3

        // Envia request para a S3 criar o arquivo
        PutObjectResult result = client.putObject(putObjectRequest);
    }

    /**
     * 
     * @param bucket
     * @param key
     * @return 
     */
    public S3Object getObject(String bucket, String key) {
        return client.getObject(bucket, key);
    }

    /**
     *
     * @param bucket
     * @param key
     * @return
     */
    public URL getURL(String bucket, String key) {
        return client.getUrl(bucket, key);
    }

    /**
     * 
     * @param bucket
     * @param key 
     */
    public void deleteObject(String bucket, String key) {
        client.deleteObject(bucket, key);
    }

}
