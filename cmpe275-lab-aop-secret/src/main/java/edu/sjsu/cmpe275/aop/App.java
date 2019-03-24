package edu.sjsu.cmpe275.aop;

import java.util.UUID;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
    public static void main(String[] args) {
        /***
         * Following is a dummy implementation of App to demonstrate bean creation with Application context.
         * You may make changes to suit your need, but this file is NOT part of your submission.
         */

    	ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("context.xml");
        SecretService secretService = (SecretService) ctx.getBean("secretService");
        SecretStats stats = (SecretStats) ctx.getBean("secretStats");

        try {
        	UUID secret = secretService.createSecret("Alice", "My little secret");
        	UUID secret1 = secretService.createSecret("Alice", "Ay little secret");
        	System.out.println("Inside main: "+secret);
//        	secretService.shareSecret("Alice", secret, "Bob");
//        	secretService.shareSecret("Alice", secret1, "Bob");
//        	secretService.shareSecret("Alice", secret, "Marley");
//        	secretService.shareSecret("Alice", secret, "Olivia");
//        	secretService.shareSecret("Alice", secret1, "Olivia");
        	secretService.shareSecret("Alice1", secret1, "Alice");
//        	secretService.shareSecret("Bob", secret, "Marley");
        	secretService.readSecret("Bob", secret);
        	secretService.readSecret("Bob67", secret);
        	secretService.unshareSecret("Alice", secret, "Alice");
        	secretService.readSecret("Bob", secret1);
 
   
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Best known secret: " + stats.getBestKnownSecret());
        System.out.println("Worst secret keeper: " + stats.getWorstSecretKeeper());
        System.out.println("Most trusted user: " + stats.getMostTrustedUser());
        System.out.println(stats.getLengthOfLongestSecret());
      
        System.out.println(stats.getLengthOfLongestSecret());
        ctx.close();
    }
}
