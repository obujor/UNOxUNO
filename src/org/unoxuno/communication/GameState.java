package org.unoxuno.communication;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.unoxuno.utilities.GameNumbers;

public class GameState implements Serializable{

	private static final long serialVersionUID = 1L;

	private ArrayList<String> users;
	private ArrayList<Card> deck;
	private ArrayList<Card> discarded;
	private boolean clockwise_sense;
	private Map<String,ArrayList<Card>> hand;
	private Map<String,Boolean> user_ready;
	private Map<String,String> user_penalities;
	private int user_id_turn;
	private boolean game_started;
	private boolean game_finished;
	private String winner;
	private final Lock lock = new ReentrantLock();
	private int clock;

	public GameState(String name){

		users = new ArrayList<String>();
		users.add(name);
		deck = new ArrayList<Card>();
		discarded = new ArrayList<Card>();
		clockwise_sense = true;
		hand = new HashMap<String,ArrayList<Card>>();
		user_ready = new HashMap<String,Boolean>();
		user_penalities = new HashMap<String,String>();
		user_id_turn = 0;
		game_started = false;
		game_finished = false;
		winner = "";
		clock = 0;

		hand.put(name, new ArrayList<Card>());
		user_ready.put(name, false);

		//Init Deck
		try {
			BufferedReader br = new BufferedReader(new FileReader("res/cardsreal.txt"));
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

	public void initGame(){
		int cardId = (int) (Math.random() * deck.size());
		Card c = deck.remove(cardId);
		discarded.add(c);
		for (String username: users){
			for (int i=0; i< GameNumbers.init_cards; i++){
				getCard(username);
			}
		}
		user_id_turn = (int) (Math.random() * users.size());
		game_started = true;
	}

	/**
	 * Controlla se il gioco è iniziato.
	 * @return True se il gioco è iniziato, false se si è ancora nella sala di attesa.
	 */
	public boolean isGameStarted(){
		return game_started;
	}

	/**
	 * Controlla se il gioco è finito.
	 * @return True se il gioco è finito, false altrimenti.
	 */
	public boolean isGameFinished(){
		return game_finished;
	}

	/**
	 * Restituisce il nome del giocatore vincitore
	 * @return Username del giocatore che ha vinto il gioco
	 */
	public String whoIsTheWinner(){
		return winner;
	}

	/**
	 * Controlla e utilizza le penalità del giocatore passato come parametro
	 * @param name Username del giocatore
	 * @return La stringa vuota se non ci sono penalità, altrimenti la descrizione della penalità applicata.
	 */
	public String checkPenality(String name){
		String result = "";
		String penality = user_penalities.remove(name);
		if (penality != null){
			if (penality.equals("plus2")){
				this.getCard(name);
				this.getCard(name);
				this.passTurn();
				result = "Aggiunte due carte";
			}
			else if (penality.equals("plus4")){
				this.getCard(name);
				this.getCard(name);
				this.getCard(name);
				this.getCard(name);
				this.passTurn();
				result = "Aggiunte quattro carte";
			}
			else if (penality.equals("jump")){
				this.passTurn();
				result = "Turno saltato";
			}
		}
		return result;
	}

	/**
	 * Controlla se il turno attuale è quello che corrisponde all'utente che ha
	 * nick uguale a quello passato come parametro.
	 * @param id Nome dell'utente di cui si vuole controllare il turno.
	 * @return True se il turno è quello dell'utente, false altrimenti.
	 */
	public boolean isMyTurn(String name){
		return (name.equals(getUserActualTurn()));

	}

	public String getUserActualTurn(){
		return users.get(user_id_turn);
	}

	/**
	 * Restituisce l'id del giocatore che giocherà il prossimo turno
	 * @return Id del prossimo giocatore in gioco
	 */
	public int nextTurnPlayerId(){
		return nextPlayerId(user_id_turn);

	}
	
	public int nextPlayerId(int id){
		int next_id;
		if (clockwise_sense)
			next_id = (id + 1) % users.size();

		else
			if (id == 0)
				next_id = (users.size()-1);
			else
				next_id = (user_id_turn - 1);
		return next_id;
	}

	/**
	 * Passa il turno al giocatore successivo
	 */
	public void passTurn(){
		user_id_turn = nextTurnPlayerId();
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
	@SuppressWarnings("unchecked")
	public ArrayList<String> getUsernames(){
		return (ArrayList<String>) users.clone();
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
		lock.lock();
		users.add(name);
		hand.put(name, new ArrayList<Card>());
		user_ready.put(name, false);
		lock.unlock();
		return users.size();

	}

	/**
	 * Rimuove un utente dal gioco. Le carte della sua mano vengono automaticamente scartate.
	 * @param id Id dell'utente da rimuovere
	 */
	public void removeUser(int id){
		lock.lock();
		String name = users.remove(id);
		for (Card c : hand.get(name))
			discarded.add(c);
		hand.remove(name);
		user_ready.remove(name);
		user_penalities.remove(name);
		if(id == user_id_turn){
			passTurn();
		}
		if (users.size()==1){
			game_finished = true;
			winner = users.get(id);
		}
		lock.unlock();
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
		return clockwise_sense;
	}

	/**
	 * Inverte il senso attuale di gioco (orario/antiorario)
	 */
	public void reverseSense(){
		clockwise_sense = !clockwise_sense;
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
	 * @return Restituisce true se all'utente rimane solo una carta
	 */
	public boolean discard(Card c, String username){
		ArrayList<Card> user_hand = hand.get(username);
		user_hand.remove(c);
		hand.put(username,user_hand);
		discarded.add(c);
		String nextuser = this.getUsername(this.nextTurnPlayerId());
		if (c.getEffect().equals("piu2"))
			user_penalities.put(nextuser, "plus2");
		else if (c.getEffect().equals("piu4"))
			user_penalities.put(nextuser, "plus4");
		else if (c.getEffect().equals("salta"))
			user_penalities.put(nextuser, "jump");
		else if (c.getEffect().equals("cambio"))
			this.reverseSense();

		if (user_hand.isEmpty()){
			game_finished = true;
			winner = username;
		}
		else
			this.passTurn();
		return (user_hand.size() == 1);
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
		Card lastCard = discarded.remove(discarded.size()-1);
		for (Card c: discarded){
			if (c.getEffect().equals("piu4") || c.getEffect().equals("colore"))
				c.changeColor("jolly");
			deck.add(c);
		}
		discarded.clear();
		discarded.add(lastCard);
	}

	/**
	 * Imposta lo stato dell'utente nella stanza di attesa
	 * @param username Nome dell'utente
	 * @param ready Nuovo stato
	 */
	public void setUserReady(String username, boolean ready){
		user_ready.put(username, ready);
	}

	/**
	 * Restituisce lo stato dell'utente
	 * @param username Nome dell'utente di cui si vuole sapere lo stato
	 * @return Se l'utente è pronto restituisce true, altrimenti false
	 */
	public boolean getUserReady(String username){
		return user_ready.get(username);
	}

	/**
	 * Restituisce l'ultima carta scartata, ovvero quella su cui ci si deve
	 * basare per la prossima mossa.
	 * @return L'ultima carta scartata.
	 */
	public Card getLastDiscardedCard(){
		return discarded.get(discarded.size()-1);
	}
	
	public void incrementClock(){
		clock++;
	}
	
	public int getClock(){
		return clock;
	}
	
	public void setAsMyTurn(int id){
		this.user_id_turn = id;
	}
}
