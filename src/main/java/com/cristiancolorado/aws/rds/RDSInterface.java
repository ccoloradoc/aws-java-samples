package com.cristiancolorado.aws.rds;

import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DBInstanceNotFoundException;
import com.amazonaws.services.rds.model.DeleteDBInstanceRequest;
import com.amazonaws.services.rds.model.DeleteDBSecurityGroupRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.amazonaws.services.rds.model.Tag;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class RDSInterface {
  private String database;
  private String user;
  private String password;


  public RDSInterface(String database, String user, String password) {
    this.database = database;
    this.user = user;
    this.password = password;
  }

  public String createDatabaseInstance(String instanceName, String securityGroupId) {
    final AmazonRDS rdsClient = AmazonRDSClientBuilder.defaultClient();

    try {
      DescribeDBInstancesRequest instancesRequest = new DescribeDBInstancesRequest()
              .withDBInstanceIdentifier(instanceName);
      DescribeDBInstancesResult instancesResult = rdsClient.describeDBInstances(instancesRequest);

      System.out.println("Instance already exist!");
      return instancesResult.getDBInstances().get(0).getEndpoint().getAddress();

    } catch(DBInstanceNotFoundException e) {
      Tag email = new Tag().withKey("POC-Email").withValue("admin_email");
      Tag propose = new Tag().withKey("Purpose").withValue("Sybex AWS Study Guide");

      CreateDBInstanceRequest instanceRequest = new CreateDBInstanceRequest()
              .withDBInstanceIdentifier(instanceName)
              .withDBName(this.database)
              .withDBInstanceClass("db.t2.micro")
              .withEngine("mariadb")
              .withMasterUsername(this.user)
              .withMasterUserPassword(this.password)
              .withVpcSecurityGroupIds(securityGroupId)
              .withAllocatedStorage(20)
              .withTags(email, propose);

      DBInstance instance = rdsClient.createDBInstance(instanceRequest);

      return instance.getEndpoint() != null ? instance.getEndpoint().getAddress() : "";
    }

  }

  public static void describeInstances() {
    final AmazonRDS rdsClient = AmazonRDSClientBuilder.defaultClient();
    rdsClient.describeDBInstances().getDBInstances().forEach(instance -> {
      System.out.println(instance.getDBInstanceIdentifier());
      System.out.println(instance.getEngine());
      System.out.println(instance.getDBInstanceStatus());
      System.out.println(instance.getEndpoint());
    });
  }

  public void createTable(String endpoint) {
    Connection conn = null;
    Statement stmt = null;

    try {
      Class.forName("org.mariadb.jdbc.Driver");

      //STEP 3: Open a connection
      System.out.println("Connecting to a selected database...");
      conn = DriverManager.getConnection(
              "jdbc:mariadb://" + endpoint + "/" + this.database, this.user, this.password);
      System.out.println("Connected database successfully...");

      //STEP 4: Execute a query
      System.out.println("Creating table in given database...");
      stmt = conn.createStatement();

      StringBuilder builder = new StringBuilder();
      builder.append("CREATE TABLE Users ( ");
      builder.append("user_id INT NOT NULL AUTO_INCREMENT, ");
      builder.append("user_fname VARCHAR(100) NOT NULL, ");
      builder.append("user_lname VARCHAR(100) NOT NULL, ");
      builder.append("user_email VARCHAR(100) NOT NULL, ");
      builder.append("PRIMARY KEY (user_id))");

      stmt.executeUpdate(builder.toString());
      System.out.println("Created table in given database...");


    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }


  }

  public void populateUsersTable(String endpoint, String table) {
    Connection conn = null;
    Statement stmt = null;

    try {
      Class.forName("org.mariadb.jdbc.Driver");

      //STEP 3: Open a connection
      System.out.println("Connecting to a selected database...");
      conn = DriverManager.getConnection(
              "jdbc:mariadb://" + endpoint + "/" + this.database, this.user, this.password);
      System.out.println("Connected database successfully...");

      //STEP 4: Execute a query
      System.out.println("Creating table in given database...");
      stmt = conn.createStatement();

      String uid = UUID.randomUUID().toString();
      StringBuilder builder = new StringBuilder();
      builder.append("INSERT INTO Users ( user_fname, user_lname, user_email) VALUES  ");
      builder.append(String.format("('%s', '%s', '%s@gmail.com')", uid, uid, uid));

      stmt.executeUpdate(builder.toString());
      System.out.println("Created table in given database...");


    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public void queryTable(String endpoint, String table) {
    Connection conn = null;
    Statement stmt = null;

    try {
      Class.forName("org.mariadb.jdbc.Driver");

      //STEP 3: Open a connection
      System.out.println("Connecting to a selected database...");
      conn = DriverManager.getConnection(
              String.format("jdbc:mariadb://%s/%s", endpoint, this.database), this.user, this.password);
      System.out.println("Connected database successfully...");

      //STEP 4: Execute a query
      System.out.println("Creating table in given database...");
      stmt = conn.createStatement();

      StringBuilder builder = new StringBuilder();
      builder.append("SELECT * FROM  Users");

      stmt.execute(builder.toString());
      ResultSet rs = stmt.getResultSet();
      while(rs.next()) {
        System.out.println(String.format(">> %s) - %s %s [%s]",
                rs.getString("user_id"),
                rs.getString("user_fname"),
                rs.getString("user_lname"),
                rs.getString("user_email")));
      }
      System.out.println("Created table in given database...");


    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public void deleteInstance(String instanceName) {
    final AmazonRDS rdsClient = AmazonRDSClientBuilder.defaultClient();

    DeleteDBInstanceRequest deleteRequest = new DeleteDBInstanceRequest(instanceName)
            .withSkipFinalSnapshot(true);
    DBInstance instance = rdsClient.deleteDBInstance(deleteRequest);
  }

}
