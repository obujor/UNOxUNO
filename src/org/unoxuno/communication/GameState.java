package org.unoxuno.communication;

import java.io.Serializable;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class GameState implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<String> users;
	//private ArrayList<String> domains;
	//private ArrayList<Integer> ports;
	private ArrayList<Registry>  registries;
	private ArrayList<Card> deck;
	private ArrayList<Card> discarded;
	private boolean clockwiseSense;
	
	public GameState(String name, Registry r){
		users = new ArrayList<String>();
		registries = new ArrayList<Registry>();
		//domains = new ArrayList<String>();
		//ports = new ArrayList<Integer>();
		users.add(name);
		registries.add(r);
		//domains.add(dom);
		//ports.add(port);
		deck = new ArrayList<Card>();
		discarded = new ArrayList<Card>();
		clockwiseSense = true;
	}
	
	public String getUsername(int id){
		return users.get(id);
	}
		
	public int getNumberOfUsers(){
		return users.size();
	}
	public int addUser(String name, Registry r){
		users.add(name);
		registries.add(r);
		return users.size();
	}
	
	public void removeUser(int id){
		users.remove(id);
		registries.remove(id);
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
	
	/*public String getDomain(int id){
		return domains.get(id);
	}
	
	public int getPort(int id){
		return ports.get(id);
	}*/
	
	public Registry getRegistry(int id){
		return registries.get(id);
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
