package org.unoxuno.communication;

import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<String> users;
	private ArrayList<String> domains;
	private ArrayList<Card> deck;
	private ArrayList<Card> discarded;
	private boolean clockwiseSense;
	
	public GameState(String name, String domain){
		users = new ArrayList<String>();
		domains = new ArrayList<String>();
		users.add(name);
		domains.add(domain);
		deck = new ArrayList<Card>();
		discarded = new ArrayList<Card>();
		clockwiseSense = true;
	}
	
	public String getUsername(int id){
		return users.get(id);
	}
	
	public String getDomain(int id){
		return domains.get(id);
	}
	
	public int getNumberOfUsers(){
		return users.size();
	}
	public int addUser(String name, String domain){
		users.add(name);
		domains.add(domain);
		return users.size();
	}
	
	public void removeUser(int id){
		users.remove(id);
		domains.remove(id);
	}
	
	public int getUserId(String name){
		for (int i=0; i<users.size(); i++){
			String u = users.get(i);
			if (u.equals(name))
				return i;
		}
		return 0;
	}
	
	public int getNextId(int id){
			return (id + 1) % users.size();
	}
	
	public ArrayList<Card> getDeck(){
		return deck;
	}
	
	public boolean getSense(){
		return clockwiseSense;
	}
	
	public void reverseSense(){
		clockwiseSense = !clockwiseSense;
	}
	
	public Card getCard(){
		int cardId = (int) (Math.random() * deck.size());
		Card c = deck.get(cardId);
		deck.remove(cardId);
		return c;
	}
	
	public int getDeckSize(){
		return deck.size();
	}
	
	public void discard(Card c){
		discarded.add(c);
	}
	
	public void refillDeck(){
		deck.addAll(discarded);
		discarded.clear();
	}
	

}
