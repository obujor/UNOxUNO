package org.unoxuno.communication;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameState implements Serializable{

	private static final long serialVersionUID = 1L;

	private ArrayList<String> users;
	private ArrayList<Card> deck;
	private ArrayList<Card> discarded;
	private boolean clockwiseSense;
	private Map<String,ArrayList<Card>> hand;
	private Map<String,Boolean> user_ready;
	private int user_id_turn;

	public GameState(String name){

		users = new ArrayList<String>();
		users.add(name);
		deck = new ArrayList<Card>();
		discarded = new ArrayList<Card>();
		clockwiseSense = true;
		hand = new HashMap<String,ArrayList<Card>>();
		user_ready = new HashMap<String,Boolean>();
		user_id_turn = 0;

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
	
	private void initGame(){
		for (String username: users){
			for (int i=0; i<7; i++){
				getCard(username);
			}
		}
		user_id_turn = (int) (Math.random() * users.size());
	}
	
	/**
	 * Controlla se il turno attuale è quello che corrisponde all'utente che ha
	 * id uguale a quello passato come parametro.
	 * @param id Id dell'utente di cui si vuole controllare il turno.
	 * @return True se il turno è quello dell'utente, false altrimenti.
	 */
	public boolean isMyTurn(int id){
		return (id == user_id_turn);
	}

	/**
	 * Restituisce il nome dell'utente in base all'id passato come parametro.
	 * @param id Id dell'utente da cercare.
	 * @return Nome dell'utente relativo all'id.
	 */
	public String getUsername(int id){
		return users.get(id);
	}

	/**
	 * Restituisce tutti i nomi degli utenti in gioco.
	 * @return Lista di nomi degli utenti in gioco.
	 */
	public ArrayList<String> getUsernames(){
		return users;
	}

	/**
	 * Restituisce il numero di utenti attualmente in gioco.
	 * @return Numero di utenti in gioco.
	 */
	public int getNumberOfUsers(){
		return users.size();
	}
	/**
	 * Aggiunge un utente al gioco. Usare questo metodo solo PRIMA che il gioco
	 * sia iniziato.
	 * @param name Nome dell'utente da aggiungere.
	 * @return Id dell'utente aggiunto.
	 */
	public int addUser(String name){
		users.add(name);
		hand.put(name, new ArrayList<Card>());
		user_ready.put(name, false);
		return users.size();
	}

	/**
	 * Rimuove un utente dal gioco. Le carte della sua mano vengono automaticamente scartate.
	 * @param id Id dell'utente da rimuovere
	 */
	public void removeUser(int id){
		String name = users.remove(id);
		for (Card c : hand.get(name))
			this.discard(c,name);
		hand.remove(name);
		user_ready.remove(name);
	}

	/**
	 * Restituisce l'id dell'utente partendo dal suo nome
	 * @param name Nome dell'utente
	 * @return Id dell'utente corrispondente al nome passato come parametro
	 */
	public int getUserId(String name){
		for (int i=0; i<users.size(); i++){
			String u = users.get(i);
			if (u.equals(name))
				return i;
		}
		return 0;
	}

	/**
	 * Restituisce l'id dell'utente successivo
	 * @param id Id dell'utente che fa la richiesta
	 * @return Id dell'utente successivo a quello che fa la richiesta
	 */
	public int getNextId(int id){
		return (id + 1) % users.size();
	}

	/**
	 * Restituisce tutte le carte del mazzo
	 * @return Lista delle carte che compongono il mazzo
	 */
	public ArrayList<Card> getDeck(){
		return deck;
	}

	/**
	 * Restituisce il senso di gioco
	 * @return Ritorna true se il senso è orario, false altrimenti
	 */
	public boolean getSense(){
		return clockwiseSense;
	}

	/**
	 * Inverte il senso attuale di gioco (orario/antiorario)
	 */
	public void reverseSense(){
		clockwiseSense = !clockwiseSense;
	}

	/**
	 * L'utente pesca una carta dal mazzo e la aggiunge a quelle della sua mano.
	 * @param username Nome dell'utente che pesca la carta.
	 * @return Carta pescata.
	 */
	public Card getCard(String username){
		Card c;
		if (deck.size() == 0){
			this.refillDeck();
		}
		int cardId = (int) (Math.random() * deck.size());
		c = deck.remove(cardId);
		ArrayList<Card> user_hand = hand.get(username);
		user_hand.add(c);
		hand.put(username,user_hand);
		return c;
	}

	/**
	 * Restituisce il numero di carte nel mazzo
	 * @return Numero (intero) di carte nel mazzo
	 */
	public int getDeckSize(){
		return deck.size();
	}

	/**
	 * L'utente scarta la carta passata come parametro. La carta viene spostata
	 * dalla mano dell'utente alle carte scartate.
	 * @param c Carta da scartare
	 * @param username Nome dell'utente che scarta la carta
	 */
	public void discard(Card c, String username){
		ArrayList<Card> user_hand = hand.get(username);
		user_hand.remove(c);
		hand.put(username,user_hand);
		discarded.add(c);
	}
	
	/**
	 * Restituisce la mano dell'utente
	 * @param username Nome dell'utente di cui si vuole sapere la mano
	 * @return Lista di carte che rappresentano la mano attuale dell'utente
	 */
	public ArrayList<Card> getHand(String username){
		return hand.get(username);
	}

	/**
	 * Sposta tutte le carte scartate nel mazzo (e le rimescola)
	 */
	public void refillDeck(){
		deck.addAll(discarded);
		discarded.clear();
	}

	/**
	 * Imposta lo stato dell'utente nella stanza di attesa
	 * @param username Nome dell'utente
	 * @param ready Nuovo stato
	 * @return Restituisce true se tutti gli utenti sono pronti per iniziare il gioco
	 */
	public boolean setUserReady(String username, boolean ready){
		user_ready.put(username, ready);
		boolean allReady = true;
		for (String u : users){
			if (!user_ready.get(u)){
				allReady = false;
				break;
			}
		}
		if (allReady)
			this.initGame();
		return allReady;
	}

	/**
	 * Restituisce lo stato dell'utente
	 * @param username Nome dell'utente di cui si vuole sapere lo stato
	 * @return Se l'utente è pronto restituisce true, altrimenti false
	 */
	public boolean getUserReady(String username){
		return user_ready.get(username);
	}
}
