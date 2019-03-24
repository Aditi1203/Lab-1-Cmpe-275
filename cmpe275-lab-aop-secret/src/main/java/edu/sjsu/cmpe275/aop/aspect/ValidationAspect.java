package edu.sjsu.cmpe275.aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;

@Aspect
@Order(1)
public class ValidationAspect {
    /***
     * Following is a dummy implementation of this aspect.
     * You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
     */

	@Before("execution(public * edu.sjsu.cmpe275.aop.SecretService.*(..))")
	public void dummyAdvice(JoinPoint joinPoint) {
		System.out.println("\n---------Inside validation before every method------------- ");
		System.out.printf("Doing validation prior to the executuion of the method %s\n", joinPoint.getSignature().getName());
		Object[] obj= joinPoint.getArgs();
		
		if(joinPoint.getSignature().getName()=="createSecret") {
			System.out.println("Inside create test to check message size");
			if(obj[0]==null||obj[1].toString().length()>100)
				throw new IllegalArgumentException();
		}
		else{
			for(Object o:obj){
			if(o==null)
				throw new IllegalArgumentException();
			 }
		}
		System.out.println("--------- validation before every method finished------------- ");
		System.out.println("\n");
	}

}
