package org.unoxuno.communication;

public class Card {
	
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

}
