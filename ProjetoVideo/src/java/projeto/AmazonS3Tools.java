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

    public void create(FileItem fileItem, String fileName) throws IOException {
		// TODO validate foldername 

        // Create metadata for your folder & set content-length to 0
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileItem.getSize());

        // Create empty content
        InputStream conteudo = fileItem.getInputStream();

        // Create a PutObjectRequest passing the foldername suffixed by /
        PutObjectRequest putObjectRequest
                = new PutObjectRequest(BUCKET, FOLDER_NAME + FOLDER_SUFFIX + fileName,
                        conteudo, metadata);

        // Send request to S3 to create folder
        PutObjectResult result = client.putObject(putObjectRequest);

    }
    
}
