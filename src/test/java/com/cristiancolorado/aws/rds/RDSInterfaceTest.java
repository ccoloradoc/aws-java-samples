package com.cristiancolorado.aws.rds;

import com.cristiancolorado.aws.utils.Utils;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class RDSInterfaceTest {

  @Test
  public void createDatabaseInstance() {
    String securityGroupId = Utils.createSecurityGroup("rds-sg-demo", "Sybex RDS Demo", "0.0.0.0/0");
    Assert.assertNotNull("Security group should not be null!", securityGroupId);

    RDSInterface db = new RDSInterface("sandman", "masteruser", "4815162342");

    String endpoint = db.createDatabaseInstance("db-sybex", securityGroupId);
    Assert.assertNotNull("Instance endpoint should not be null!", endpoint);
  }

  @Test
  public void describeInstances() {
    RDSInterface.describeInstances();
  }

  @Test
  public void createTable() {
    String securityGroupId = Utils.createSecurityGroup("rds-sg-demo", "Sybex RDS Demo", "0.0.0.0/0");
    Assert.assertNotNull("Security group should not be null!", securityGroupId);

    RDSInterface db = new RDSInterface("sandman", "masteruser", "4815162342");

    String endpoint = db.createDatabaseInstance("db-sybex", securityGroupId);
    Assert.assertNotNull("Instance endpoint should not be null!", endpoint);

    db.createTable(endpoint);
  }

  @Test
  public void queryTable() {
    String securityGroupId = Utils.createSecurityGroup("rds-sg-demo", "Sybex RDS Demo", "0.0.0.0/0");
    Assert.assertNotNull("Security group should not be null!", securityGroupId);

    RDSInterface db = new RDSInterface("sandman", "masteruser", "4815162342");

    String endpoint = db.createDatabaseInstance("db-sybex", securityGroupId);
    Assert.assertNotNull("Instance endpoint should not be null!", endpoint);

    db.populateUsersTable(endpoint, "Users");
    db.queryTable(endpoint, "Users");
  }

  @Test
  public void deleteDatabase() {
    RDSInterface db = new RDSInterface("sandman", "masteruser", "4815162342");
    db.deleteInstance("db-sybex");
  }
}