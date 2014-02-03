package projeto;

import java.io.InputStream;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import java.io.IOException;
import org.apache.commons.fileupload.FileItem;

public class AmazonS3Tools {

    public static final String BUCKET = "rangelsambavideo";
    public final static String FOLDER_SUFFIX = "/";
    public final static String FOLDER_NAME = "input";
    public final static String FOLDER_NAME_OUPUT = "output";

    
    private final AmazonS3Client client;
    private static AmazonS3Tools amazonS3Tools;

    private AmazonS3Tools() {        
       // Cria o cliente S3, de acordo com o arquivo de configuração
        client = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
    }
    
    public static AmazonS3Tools getAmazonS3()
    {
        if(amazonS3Tools==null)
        {
            amazonS3Tools = new AmazonS3Tools();
        }
        return amazonS3Tools;
    }
    
    /**
     * 
     * @return 
     */
    public static String getBasePath()
    {
        return AmazonS3Tools.BUCKET + AmazonS3Tools.FOLDER_SUFFIX + AmazonS3Tools.FOLDER_NAME + AmazonS3Tools.FOLDER_SUFFIX;
    }

    public void create(FileItem fileItem, String fileName) throws IOException {

        // Configura o metadata de acordo com o tamanho do fluxo de entrada
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileItem.getSize());

        // Obtém o fluxo de dados do arquivo de upload
        InputStream conteudo = fileItem.getInputStream();

        // Criação objeto alvo de criação na S3
        PutObjectRequest putObjectRequest
                = new PutObjectRequest(BUCKET, FOLDER_NAME + FOLDER_SUFFIX + fileName,
                        conteudo, metadata);

        // Envia request para a S3 criar o arquivo
        PutObjectResult result = client.putObject(putObjectRequest);

    }
    
}
