package edu.sjsu.cmpe275.aop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

public class SecretStatsImpl implements SecretStats {
	public static int length_longest=0;
	public static HashMap<UUID,String> owner=new HashMap<UUID,String>();
	public static Map<UUID, HashSet> shareList = new HashMap<UUID, HashSet>();
	HashMap<String,HashSet<String>> sender=new HashMap<String,HashSet<String>>();
	HashMap<String,HashSet<String>> receiver=new HashMap<String,HashSet<String>>();
	HashMap<String,Integer> worstsecret_count=new HashMap<String,Integer>();
	HashMap<UUID,String> message_content=new HashMap<UUID,String>();
	HashMap<UUID, HashSet> whole_shareList = new HashMap<UUID, HashSet>();
	

	public void resetStatsAndSystem() {
		length_longest=0;
		sender.clear();
		receiver.clear();
		worstsecret_count.clear();
		message_content.clear();
		whole_shareList.clear();
		owner.clear();
		shareList.clear();
	}

	public int getLengthOfLongestSecret() {
		return length_longest;
	}


	public String getMostTrustedUser() {
		System.out.println(receiver);
		int count=Integer.MIN_VALUE;
		String trustedUser = null;
		for(String s:receiver.keySet()) {
			if(count<receiver.get(s).size()) {
				count=receiver.get(s).size();
				trustedUser=s;
			}else if(count==receiver.get(s).size()) {
				int flag=trustedUser.compareTo(s);
				trustedUser=flag>0?s:trustedUser;
			}
		}
		return trustedUser;
	}

	public String getWorstSecretKeeper() {
		System.out.println(worstsecret_count);
		String worst_secret_keeper = null;
		int count=Integer.MAX_VALUE;
		
		for(String s:worstsecret_count.keySet()) {
			int tmp=worstsecret_count.get(s);
			if(count>tmp) {
				count=tmp;
				worst_secret_keeper=s;
			}else if(count==tmp) {
				int flag=worst_secret_keeper.compareTo(s);
				worst_secret_keeper=flag>0?s:worst_secret_keeper;
			}
		}
		return worst_secret_keeper;
	}

	public String getBestKnownSecret() {
		int count=Integer.MIN_VALUE;
		String best_secret=null;
		System.out.println(whole_shareList);
		for(UUID id:whole_shareList.keySet()) {
			int size=whole_shareList.get(id).size();
			if(count<size) {
				count=size;
				best_secret=message_content.get(id);
			}else if(count==size) {
				int flag=best_secret.compareTo(message_content.get(id));
				best_secret=flag>0?message_content.get(id):best_secret;
			}
		}
		return best_secret;
	}
	
	public HashMap<String, HashSet<String>> getSender() {
		return sender;
	}

	public void setSender(HashMap<String, HashSet<String>> sender) {
		this.sender = sender;
	}

	public HashMap<String, HashSet<String>> getReceiver() {
		return receiver;
	}

	public void setReceiver(HashMap<String, HashSet<String>> receiver) {
		this.receiver = receiver;
	}


	public HashMap<String, Integer> getWorstsecret_count() {
		return worstsecret_count;
	}

	public void setWorstsecret_count(HashMap<String, Integer> worstsecret_count) {
		this.worstsecret_count = worstsecret_count;
	}

	public HashMap<UUID, String> getMessage_content() {
		return message_content;
	}

	public void setMessage_content(HashMap<UUID, String> message_content) {
		this.message_content = message_content;
	}

	public HashMap<UUID, HashSet> getWhole_shareList() {
		return whole_shareList;
	}

	public void setWhole_shareList(HashMap<UUID, HashSet> whole_shareList) {
		this.whole_shareList = whole_shareList;
	}
	
	
    
}



