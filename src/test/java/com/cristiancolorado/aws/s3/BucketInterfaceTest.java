package com.cristiancolorado.aws.s3;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class BucketInterfaceTest {
  private static BucketInterface bucketInterface = new BucketInterface("us-east-1");
  private static String bucketName = "common-bucket-cristian-colorado";

  @BeforeClass
  public static void createCommonBucket() {
    Assert.assertTrue("Common bucket could not be created!", bucketInterface.createBucket(bucketName));
  }

  @AfterClass
  public static void deleteCommonBucket() {
    Assert.assertTrue("Common bucket could not be deleted!", bucketInterface.deleteBucket(bucketName));
  }

  @Test
  public void uploadContent() {
    String fileName = UUID.randomUUID().toString() + ".txt";
    String fileContent= "TestString";
    bucketInterface.uploadContent(bucketName, fileContent, fileName);
    String content = bucketInterface.getContent(bucketName, fileName);
    Assert.assertEquals("Content stored is not equals!", fileContent, content);
  }

  @Test
  public void uploadObject() {
    String key = UUID.randomUUID().toString();
    String destiny = "./backup/" + key + "/pom.s3.java";
    bucketInterface.uploadObject(bucketName, key, new File("./pom.xml"));
    bucketInterface.downloadObject(bucketName, key, destiny);
    Assert.assertTrue(Files.exists(Paths.get(System.getProperty("user.dir"), destiny)));
  }

}