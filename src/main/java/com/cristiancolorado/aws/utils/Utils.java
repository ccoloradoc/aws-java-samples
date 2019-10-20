package com.cristiancolorado.aws.utils;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;

public class Utils {
  public static String createSecurityGroup(String groupName, String description, String cidr) {
    final AmazonEC2 ec2Client = AmazonEC2ClientBuilder.defaultClient();

    DescribeSecurityGroupsRequest sgRequest = new DescribeSecurityGroupsRequest().withGroupNames(groupName);
    DescribeSecurityGroupsResult sgResult = ec2Client.describeSecurityGroups(sgRequest);
    if(sgResult.getSecurityGroups().size() == 0) {
      CreateSecurityGroupRequest request = new CreateSecurityGroupRequest()
              .withGroupName(groupName)
              .withDescription(description);

      CreateSecurityGroupResult result = ec2Client.createSecurityGroup(request);

      System.out.println(result);

      AuthorizeSecurityGroupIngressRequest authRequest = new AuthorizeSecurityGroupIngressRequest()
              .withCidrIp(cidr)
              .withFromPort(3306)
              .withGroupName(groupName)
              .withToPort(3306)
              .withIpProtocol("tcp");

      AuthorizeSecurityGroupIngressResult authResult = ec2Client.authorizeSecurityGroupIngress(authRequest);

      System.out.println(authResult);

      return result.getGroupId();
    } else {
      System.out.println("Security Group Already Exist!" + sgResult.getSecurityGroups());
      return sgResult.getSecurityGroups().get(0).getGroupId();
    }
  }

  public static void listSecurityGroup() {
    final AmazonEC2 ec2Client = AmazonEC2ClientBuilder.defaultClient();

    DescribeSecurityGroupsRequest listRequest = new DescribeSecurityGroupsRequest();

    DescribeSecurityGroupsResult listResult = ec2Client.describeSecurityGroups(listRequest);

    listResult.getSecurityGroups().forEach(securityGroup -> {
      System.out.println(securityGroup);
    });
  }

  public static void deleteSecurityGroup(String groupName)  {
    final AmazonEC2 ec2Client = AmazonEC2ClientBuilder.defaultClient();

    DeleteSecurityGroupRequest request = new DeleteSecurityGroupRequest()
            .withGroupName(groupName);
    ec2Client.deleteSecurityGroup(request);
  }
}
