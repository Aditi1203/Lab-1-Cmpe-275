package edu.sjsu.cmpe275.aop.aspect;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import edu.sjsu.cmpe275.aop.NotAuthorizedException;
import edu.sjsu.cmpe275.aop.SecretServiceImpl;
import edu.sjsu.cmpe275.aop.SecretStatsImpl;

@Aspect
@Order(3)
public class AccessControlAspect {

	@AfterReturning(
			pointcut="execution(public * edu.sjsu.cmpe275.aop.SecretService.createSecret(..))",
			returning="result")
	public void storeAdvice(JoinPoint joinPoint,UUID result) {
		System.out.println("-----------after returning(create secret)------------------------");
		System.out.printf("Access control prior to the execution of the method %s\n", joinPoint.getSignature().getName());
		System.out.println("JoinPoint is:"+joinPoint);
		Object obj[]=joinPoint.getArgs();
		String userid=(String)obj[0];
		SecretStatsImpl.owner.put(result, userid);
		HashSet<String> hmp=new HashSet<String>();
		hmp.add(userid);
		SecretStatsImpl.shareList.put(result, hmp);
		System.out.println("Content of map owner"+ SecretStatsImpl.owner);
		System.out.println("Content of map user"+ SecretStatsImpl.shareList);
		System.out.println("-------------end of after returning advise for create secret------------------\n");
	}

	@Before("execution(public * edu.sjsu.cmpe275.aop.SecretService.readSecret(..))")
	public void dummyAdvice(JoinPoint joinPoint) {
		System.out.println("-------------Before read secret--------------------------");
		System.out.printf(joinPoint.getSignature().getName());
		Object[] o=joinPoint.getArgs();
		String userid=(String)o[0];
		UUID id=(UUID)o[1];
		if(!SecretStatsImpl.owner.containsKey(o[1])) {
			throw new NotAuthorizedException();
		}
		else if(SecretStatsImpl.owner.get(id).equals(userid)) {}
		else if(SecretStatsImpl.shareList.containsKey(id)){
			HashSet<String> tmp=SecretStatsImpl.shareList.get(id);
			if(tmp.contains(userid)) {
				System.out.println("Found key");
			}else
				throw new NotAuthorizedException();
		}
	}
	
	
	
	@Before("execution(public * edu.sjsu.cmpe275.aop.SecretService.shareSecret(..))")
	public void shareAdvice(JoinPoint joinPoint) {
		System.out.println("-------------Before share secret--------------------------");
		Object obj[]=joinPoint.getArgs();
		String ownerid=(String)obj[0];
		UUID id=(UUID)obj[1];
		String shareid=(String)obj[2];
		if(!SecretStatsImpl.owner.containsKey(id)) {
			System.out.println("Stage 1");
			throw new NotAuthorizedException();
		}
		else if(SecretStatsImpl.owner.get(id).equals(ownerid)) {
			System.out.println("Stage 2");
			HashSet<String> tmp=SecretStatsImpl.shareList.get(id);
			tmp.add(shareid);
			SecretStatsImpl.shareList.put(id,tmp);	
			System.out.println(SecretStatsImpl.shareList);
		}
		
		else if(SecretStatsImpl.shareList.containsKey(id)){
			System.out.println("Stage 3");
			HashSet<String> tmp=SecretStatsImpl.shareList.get(id);
			if(tmp.contains(ownerid)) {
			tmp.add(shareid);
			SecretStatsImpl.shareList.put(id,tmp);	
			System.out.println(SecretStatsImpl.shareList);
			}
			else {
				throw new NotAuthorizedException();
			}
		}
		System.out.println("-------------Finished share secret--------------------------");
	}
	
	@Before("execution(public * edu.sjsu.cmpe275.aop.SecretService.unshareSecret(..))")
	public void unshareAdvice(JoinPoint joinPoint) {
		System.out.println("-------------------Before unshare secret--------------------------");
		Object obj[]=joinPoint.getArgs();
		String ownerid=(String)obj[0];
		UUID id=(UUID)obj[1];
		String unshareid=(String)obj[2];
		if(!SecretStatsImpl.owner.containsKey(id)) {
			throw new NotAuthorizedException();
		}
		else if(SecretStatsImpl.owner.get(id).equals(ownerid) && SecretStatsImpl.owner.get(id).equals(unshareid)) {
			System.out.println("Owner will have silent access");
		}
		else if(SecretStatsImpl.owner.get(id).equals(ownerid)) {
			HashSet<String> tmp=SecretStatsImpl.shareList.get(id);
			if(tmp.contains(unshareid)) {
				tmp.remove(unshareid);
			}else {
				System.out.println("Secret is not shared with: "+unshareid);
			}
			System.out.println(tmp);
			SecretStatsImpl.shareList.put(id,tmp);	
			System.out.println("Set after unshare: "+SecretStatsImpl.shareList);
		}
		else {
			throw new NotAuthorizedException();
		}
		System.out.println("-------------Finished unshare secret--------------------------");
	}

}
