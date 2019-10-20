package com.cristiancolorado.aws.utils;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {

  @Test
  public void createSecurityGroup() {
    String securityGroupId = Utils.createSecurityGroup("rds-sg-demo", "Sybex RDS Demo", "0.0.0.0/0");
    Assert.assertNotNull("Security group should not be null!", securityGroupId);
  }

  @Test
  public void listSecurityGroup() {
    Utils.listSecurityGroup();
  }

  @Test
  public void deleteSecurityGroup() {
    Utils.deleteSecurityGroup("rds-sg-demo");
  }
}