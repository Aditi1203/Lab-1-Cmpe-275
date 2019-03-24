package edu.sjsu.cmpe275.aop.aspect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import edu.sjsu.cmpe275.aop.NotAuthorizedException;
import edu.sjsu.cmpe275.aop.SecretStatsImpl;

@Aspect
@Order(8)
public class StatsAspect {
	

	@Autowired SecretStatsImpl stats;
	
	
	@AfterReturning(
			pointcut="execution(public * edu.sjsu.cmpe275.aop.SecretService.createSecret(..))",
			returning="result")
	public void storeAdvice(JoinPoint joinPoint,UUID result) {
		System.out.println("\n-----------Stats(create secret)------------------------");
		System.out.printf("Access control prior to the execution of the method %s\n", joinPoint.getSignature().getName());
		
		Object obj[]=joinPoint.getArgs();
		String userid=(String)obj[0];
		String content=(String)obj[1];
		if(SecretStatsImpl.length_longest<content.length()) {
			SecretStatsImpl.length_longest=content.length();
		}
		HashMap<UUID,String> message_content=stats.getMessage_content();
		message_content.put(result, content);
	    stats.setMessage_content(message_content);
		System.out.println("-------------End of after returning stats advise for create secret------------------\n");
	}
	
	
	
	@After("execution(public * edu.sjsu.cmpe275.aop.SecretService.shareSecret(..))")
	public void dummyAfterAdvice(JoinPoint joinPoint) {
		System.out.println("\n------------------Welcome to trusted user stats------------------------------------");
		System.out.printf("After the executuion of the method %s\n", joinPoint.getSignature().getName());
		
		Object obj[]=joinPoint.getArgs();
		String ownerid=(String)obj[0];
		UUID id=(UUID)obj[1];
		String shareid=(String)obj[2];
		String joinvalue = id.toString()+":mapsto:"+ownerid;
		String sender_joinvalue = id.toString()+":mapsto:"+shareid;
		boolean flag_receiver=false,flag_sender=false;
		
		HashMap<String,HashSet<String>> receiver=stats.getReceiver();
		HashMap<String,HashSet<String>> sender=stats.getSender();
		
		if(!receiver.containsKey(shareid)) {
			HashSet<String> tmp=new HashSet<String>();
			tmp.add(joinvalue);
			receiver.put(shareid,tmp);
			flag_receiver=true;
			System.out.println("Final hashset is"+receiver);
		}else {
			HashSet<String> tmp=receiver.get(shareid);
			System.out.println("check alert:"+tmp);
			System.out.println("check alert:"+receiver.get(shareid));
			System.out.println(tmp.contains(joinvalue));
			if(!tmp.contains(joinvalue)) {
				tmp.add(joinvalue);
				receiver.put(shareid,tmp);
				flag_receiver=true;
			}
		}
		
		stats.setReceiver(receiver);
		
		if(!sender.containsKey(ownerid)) {
			HashSet<String> tmp=new HashSet<String>();
			tmp.add(sender_joinvalue);
			sender.put(ownerid,tmp);
			flag_sender=true;
		}else {
			HashSet<String> tmp=sender.get(ownerid);
			if(!tmp.contains(sender_joinvalue)) {
				tmp.add(sender_joinvalue);
				sender.put(ownerid,tmp);
				flag_sender=true;
			}
		}
		
		if(flag_receiver==true) {
			HashMap<String,Integer> worstsecret_count=stats.getWorstsecret_count();
			if(worstsecret_count.containsKey(shareid)) {
				worstsecret_count.put(shareid, worstsecret_count.get(shareid)+1);
			}else
				worstsecret_count.put(shareid,1);
			stats.setWorstsecret_count(worstsecret_count);
		}
		
		if(flag_sender==true) {
			HashMap<String,Integer> worstsecret_count=stats.getWorstsecret_count();
			if(worstsecret_count.containsKey(ownerid)) {
				System.out.println(worstsecret_count.get(ownerid));
				worstsecret_count.put(ownerid, worstsecret_count.get(ownerid)-1);
			}else
				worstsecret_count.put(ownerid,-1);
			stats.setWorstsecret_count(worstsecret_count);
		}
		
		System.out.println("Final sender hashset is"+sender);
		stats.setSender(sender);
		
		System.out.println("------------------Welcome to end of trusted user stats------------------------------------");
		System.out.println("---------------------------------");
		//stats.resetStats();
	}
	
	
	@Before("execution(public * edu.sjsu.cmpe275.aop.SecretService.readSecret(..))")
	public void dummyAdvice(JoinPoint joinPoint) {
		System.out.println("\n\n-------------Before read secret statistics--------------------------");
		System.out.printf(joinPoint.getSignature().getName());
		Object[] o=joinPoint.getArgs();
		String userid=(String)o[0];
		UUID id=(UUID)o[1];
		HashMap<UUID, HashSet> whole_shareList=stats.getWhole_shareList();
		if(SecretStatsImpl.owner.get(id).equals(userid)) {
			return;
		}
		else{
			if(whole_shareList.containsKey(id)) {
				HashSet<String> tmp=whole_shareList.get(id);
				tmp.add(userid);
				whole_shareList.put(id, tmp);
				
			}else {
				HashSet<String> tmp=new HashSet<String>();
				tmp.add(userid);
				whole_shareList.put(id, tmp);
			}
		}
		System.out.println("hashmap"+whole_shareList);
		stats.setWhole_shareList(whole_shareList);
	}
	
	@Before("execution(public void edu.sjsu.cmpe275.aop.SecretService.*(..))")
	public void dummyBeforeAdvice(JoinPoint joinPoint) {
		System.out.printf("Doing stats before the executuion of the metohd %s\n", joinPoint.getSignature().getName());
	}
	
}
