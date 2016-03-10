package org.unoxuno.communication;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<String> users;
	private ArrayList<Card> deck;
	private ArrayList<Card> discarded;
	private boolean clockwiseSense;
	

	public GameState(String name){
		
		
		users = new ArrayList<String>();
		users.add(name);
		deck = new ArrayList<Card>();
		discarded = new ArrayList<Card>();
		clockwiseSense = true;
		
		//Init Deck
		try {
			BufferedReader br = new BufferedReader(new FileReader("res/cards.txt"));
			String line;
			while ((line = br.readLine())!= null){
				String color = line.split(",")[0];
				String effect = line.split(",")[1];
				Card crd = new Card(color,effect);
				deck.add(crd);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
	public int addUser(String name){
		users.add(name);
		return users.size();
	}

	public void removeUser(int id){
		users.remove(id);
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
		Card c;
		try{
			c = deck.remove(cardId);
		}
		catch (IndexOutOfBoundsException e){
			c = new Card("FINITE","FINITE");
		}
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
