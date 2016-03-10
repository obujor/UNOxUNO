package org.unoxuno.communication;

import java.io.Serializable;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class RegistryContainer implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String,Registry> usersdata;
	
	public RegistryContainer(String name,Registry r){
		usersdata = new HashMap<String,Registry>();
		usersdata.put(name, r);
	}
	
	public void addRegistry(String name, Registry r){
		usersdata.put(name, r);
	}
	
	public void removeRegistry(String name){
		usersdata.remove(name);
	}
	
	public Registry getRegistry(String name){
		return usersdata.get(name);
	}
	
	public Map<String,Registry> getAllRegistries(){
		return usersdata;
	}
}
