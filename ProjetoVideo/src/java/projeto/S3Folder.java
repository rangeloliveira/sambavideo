package projeto;

import java.io.InputStream;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import java.io.IOException;
import org.apache.commons.fileupload.FileItem;

public class S3Folder {

    private static final String AWS_KEY = "AKIAJ5NS3OEEFMT7JJPA";
    private static final String AWS_SECRET = "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";
    public static final String BUCKET = "rangelsambavideo";
    public final static String FOLDER_SUFFIX = "/";
    public final static String FOLDER_NAME = "input";
    public final static String FOLDER_NAME_OUPUT = "output";

    
    private final AmazonS3Client client;

    public S3Folder() {
        // Create S3 Client object using AWS KEY & SECRET
        client = new AmazonS3Client(
                new BasicAWSCredentials(AWS_KEY, AWS_SECRET));
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
