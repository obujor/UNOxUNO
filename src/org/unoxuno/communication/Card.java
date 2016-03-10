package org.unoxuno.communication;

import java.io.Serializable;

public class Card implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String color;
	private String effect;
	
	
	public Card(String color, String effect){
		this.color = color;
		this.effect = effect;
	}
	
	public String getColor(){
		return this.color;
	}
	
	public String getEffect(){
		return this.effect;
	}
	
	public String getUri(){
		return (this.color+"_"+this.effect+".png");
	}

}
