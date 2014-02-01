package projeto;

import java.io.InputStream;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import org.apache.commons.fileupload.FileItem;

public class S3Folder {
	private static final String AWS_KEY = "your-aws-key";
	private static final String AWS_SECRET = "your-aws-secret";
	private static final String BUCKET = "s3-bucket-location";
	private final static String FOLDER_SUFFIX = "/";
        private final static String FOLDER_NAME = "input";
	final static AmazonS3Client client;

	static {
		// Create S3 Client object using AWS KEY & SECRET
		client = new AmazonS3Client(
				new BasicAWSCredentials(AWS_KEY, AWS_SECRET));
	}

	public void create(FileItem fileItem) throws IOException {
		// TODO validate foldername 

		// Create metadata for your folder & set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(fileItem.getSize());

		// Create empty content
		InputStream conteudo = fileItem.getInputStream();

		// Create a PutObjectRequest passing the foldername suffixed by /
		PutObjectRequest putObjectRequest =
				new PutObjectRequest(BUCKET, FOLDER_NAME + FOLDER_SUFFIX,
						conteudo, metadata);

		// Send request to S3 to create folder
		client.putObject(putObjectRequest);
	}
}