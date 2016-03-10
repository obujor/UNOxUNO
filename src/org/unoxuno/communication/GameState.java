package org.unoxuno.communication;

import java.io.Serializable;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameState implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<String> users;
	private ArrayList<Card> deck;
	private ArrayList<Card> discarded;
	private boolean clockwiseSense;
	private Map<String,Registry> usersdata;

	public GameState(String name, Registry r){
		users = new ArrayList<String>();
		usersdata = new HashMap<String,Registry>();
		users.add(name);
		usersdata.put(name, r);
		deck = new ArrayList<Card>();
		discarded = new ArrayList<Card>();
		clockwiseSense = true;
	}

	public String getUsername(int id){
		return users.get(id);
	}

	public ArrayList<String> getUsernames(){
		return users;
	}

	public int getNumberOfUsers(){
		return users.size();
	}
	public int addUser(String name, Registry r){
		users.add(name);
		usersdata.put(name, r);
		return users.size();
	}

	public void removeUser(int id){
		String name = users.remove(id);
		usersdata.remove(name);
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

	public Registry getRegistry(String name){
		return usersdata.get(name);
	}

	public Map<String,Registry> getAllRegistries(){
		return usersdata;
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
