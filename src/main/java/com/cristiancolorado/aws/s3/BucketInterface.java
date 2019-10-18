package com.cristiancolorado.aws.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteBucketRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BucketInterface {
  private String clientRegion;

  public BucketInterface(String clientRegion) {
    this.clientRegion = clientRegion;
  }

  public boolean createBucket(String bucketName) {
    try {
      AmazonS3 s3Client = createClient();

      if(!s3Client.doesBucketExistV2(bucketName)) {
        s3Client.createBucket(new CreateBucketRequest(bucketName));
        String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucketName));
        System.out.println("Bucket " + bucketName + " is at " + bucketLocation);
      }

      return s3Client.doesBucketExistV2(bucketName);

    } catch(AmazonServiceException e) {
      e.printStackTrace();
    } catch(SdkClientException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean deleteBucket(String bucketName) {
    try {
      AmazonS3 s3Client = createClient();

      if(s3Client.doesBucketExistV2(bucketName)) {
        s3Client.listObjects(bucketName).getObjectSummaries().forEach(item -> {
          System.out.println(item.getKey());
          s3Client.deleteObject(bucketName,item.getKey());
        });
        s3Client.deleteBucket(new DeleteBucketRequest(bucketName));
      }

      return !s3Client.doesBucketExistV2(bucketName);

    } catch(AmazonServiceException e) {
      e.printStackTrace();
    } catch(SdkClientException e) {
      e.printStackTrace();
    }
    return false;
  }

  public void uploadObject(String bucketName, String key, File file) {
    try {
      AmazonS3 s3Client = createClient();

      PutObjectRequest putRequest = new PutObjectRequest(bucketName, key, file);
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentType("plain/text");
      metadata.addUserMetadata("x-amz-metadata-title", "My Title");
      putRequest.setMetadata(metadata);
      s3Client.putObject(putRequest);

    } catch( AmazonServiceException e) {
      e.printStackTrace();
    }
  }

  public void downloadObject(String bucketName, String key, String destinyFile) {
    try {
      AmazonS3 s3Client = createClient();

      GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
      ObjectMetadata metadata = s3Client.getObject(getObjectRequest, new File(destinyFile));

    } catch( AmazonServiceException e) {
      e.printStackTrace();
    }
  }

  public void uploadContent(String bucketName, String content, String fileName) {
    try {
      AmazonS3 s3Client = createClient();

      s3Client.putObject(bucketName, fileName, content);

    } catch( AmazonServiceException e) {
      e.printStackTrace();
    }
  }

  public String getContent(String bucketName, String fileName) {
    try {
      AmazonS3 s3Client = createClient();

      S3Object object = s3Client.getObject(bucketName, fileName);

      return parseInputStream(object.getObjectContent());

    } catch( AmazonServiceException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String parseInputStream(InputStream input) throws IOException {
    // Read the text input stream one line at a time and display each line.
    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    String line = "";
    StringBuilder builder = new StringBuilder();
    while ((line = reader.readLine()) != null) {
      builder.append(line);
    }
    return builder.toString();
  }

  private AmazonS3 createClient() {
    return AmazonS3ClientBuilder.standard()
            .withCredentials(new ProfileCredentialsProvider())
            .withRegion(this.clientRegion)
            .build();
  }

}
