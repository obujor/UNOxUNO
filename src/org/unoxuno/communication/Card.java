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
		return ("res/images/"+this.color+"_"+this.effect+".png");
	}
	
	public boolean compatibleWith(Card otherCard){
		boolean cmp = false;
		if (this.getColor().equals(otherCard.getColor()) 
			|| this.getEffect().equals(otherCard.getEffect()))
			cmp = true;
		if (this.isJollyCard() || otherCard.isJollyCard())
			cmp = true;
		return cmp;
	}
	
	public boolean isJollyCard(){
		return this.getColor().equals("jolly");
	}
	
	public void changeColor(String newcolor){
		this.color = newcolor;
	}

}
