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

/**
 * Classe que organiza as operações necessárias para manipular objetos da AmazonS3.
 * @author Rangel
 */
public class AmazonS3Tools {

    public static final String BUCKET = "rangelsambavideo";
    public final static String FOLDER_SUFFIX = "/";

    public static String FOLDER_NAME = "input";
    public static String FOLDER_NAME_OUPUT = "output";

    private final AmazonS3Client client;
    private static AmazonS3Tools amazonS3Tools;

    /**
     * Construtor privado para forçar apenas uma instância do objeto.
     */
    private AmazonS3Tools() {
        // Cria o cliente S3, de acordo com o arquivo de configuração
        client = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
    }

    /**
     * 
     * @return cria ou retorna uma instância desta classe
     */
    public static AmazonS3Tools getAmazonS3() {
        if (amazonS3Tools == null) {
            amazonS3Tools = new AmazonS3Tools();
        }
        return amazonS3Tools;
    }

    /**
     *
     * @return recupera o caminho base para os arquivos de entrada na S3
     */
    public static String getBasePath() {
        return AmazonS3Tools.BUCKET + AmazonS3Tools.FOLDER_SUFFIX + AmazonS3Tools.FOLDER_NAME + AmazonS3Tools.FOLDER_SUFFIX;
    }
    
      /**
     *
     * @return recupera o caminho base para os arquivos de saída da S3
     */
    public static String getOutputPath() {
        return AmazonS3Tools.BUCKET + AmazonS3Tools.FOLDER_SUFFIX + AmazonS3Tools.FOLDER_NAME_OUPUT + AmazonS3Tools.FOLDER_SUFFIX;
    }

    /**
     * É responsável por criar um arquivo na S3, de acordo com um arquivo obtido por upload.
     * 
     * @param inputStream stream obtido no upload do arquivo
     * @param lenght tamanho do stream
     * @param fileName nome do arquivo selecionado.
     * @throws IOException 
     */
    public void createObject(InputStream inputStream, Long lenght, String fileName) throws IOException {

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
     * Recupera o objeto da S3.
     * 
     * @param bucket nome do bucket
     * @param key chave de identificação do arquivo no bucket
     * @return S3Object
     */
    public S3Object getObject(String bucket, String key) {
        return client.getObject(bucket, key);
    }

    /**
     * Recupera a URL pública de um determinado arquivo na S3.
     * 
     * @param bucket nome do bucket
     * @param key chave de identificação do arquivo no bucket
     * @return URL
     */
    public URL getURL(String bucket, String key) {
        return client.getUrl(bucket, key);
    }

    /**
     * Remove um objeto da S3.
     * 
     * @param bucket nome do bucket.
     * @param key chave de identificação do arquivo no bucket
     */
    public void deleteObject(String bucket, String key) {
        client.deleteObject(bucket, key);
    }

}
