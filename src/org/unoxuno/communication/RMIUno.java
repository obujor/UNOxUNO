package org.unoxuno.communication;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.unoxuno.game.GameBoard.StateChanged;
import org.unoxuno.game.MainMenu.GameStart;
import org.unoxuno.utilities.GameNumbers;

public class RMIUno extends UnicastRemoteObject 
implements IUno{

	private static final long serialVersionUID = 1L;
	String nickname;
	int myId;
	GameState state;
	boolean token = false;
	Registry localRegistry;
	RegistryContainer players_registries;
	GameStart gameStartListener;
        StateChanged stateChangeListener;

	private class PingTask extends TimerTask{

		@Override
		public void run() {
			boolean changed = false;
			boolean checknext = true;
			while (checknext){
				checknext = false;
				int nextId = state.getNextId(myId);
				if (nextId != myId){
					String nextname = state.getUsername(nextId);
					try
					{
						IUno tempServer = 
								(IUno) players_registries.getRegistry(nextname).lookup(nextname);
						tempServer.ping(nickname);
					}
					catch(ConnectException e){
						System.out.println("Successivo non trovato, rimozione");
						state.removeUser(nextId);
						players_registries.removeRegistry(nextname);
						checknext = true;
						changed = true;
						if (nextId < myId){
							myId--;
						}
					}
					catch(NotBoundException e)
					{
						e.printStackTrace( );
					}
					catch(RemoteException e)
					{
						e.printStackTrace( );
					}
				}
			}
			if (changed)
				refreshAllStates();

		}

	}

	public RMIUno(String name,int port)throws RemoteException{
		this.nickname = name;
		this.myId = 0;
		localRegistry = LocateRegistry.createRegistry(port);
		localRegistry.rebind(name, this);
		state = new GameState(name);
		players_registries = new RegistryContainer(name,localRegistry);
		System.out.println("Binding eseguito su "+name+" in porta "+port);
		Timer t = new Timer();
		t.scheduleAtFixedRate(new PingTask(), 5000, 5000);

	}

	public static void main(String[] args) throws RemoteException {
		RMIUno server = new RMIUno(args[0],Integer.parseInt(args[1]));
		if (args.length == 5)
			server.connectSend(args[0],Integer.parseInt(args[4]),args[2],args[3]);
		server.token = true;

	}

	public void connectSend(String myname, int serverport, String servername, String serverdom){
		try
		{
			System.out.println("Tentativo di connessione da parte di "+myname+" su "+servername+" con porta "+serverport+" all'indirizzo "+serverdom);
			Registry tempRegistry = LocateRegistry.getRegistry(serverdom, serverport);
			IUno tempServer = (IUno) tempRegistry.lookup(servername);
			tempServer.connectReply(myname,localRegistry);
		}
		catch(NotBoundException e)
		{
			e.printStackTrace( );
		}
		catch(RemoteException e)
		{
			e.printStackTrace( );
		}
	}

	@Override
	public void connectReply(String name, Registry r) throws RemoteException {
		System.out.println("Richiesta ricevuta da "+name);
		state.addUser(name);
		players_registries.addRegistry(name, r);
		System.out.println("Utente entrato in stanza: "+name);
		refreshAllStates();
	}

	@Override
	public void refreshState(GameState s, RegistryContainer r) throws RemoteException {
		state = s;
		players_registries = r;
		//System.out.println("Aggiornato stato");
		myId = state.getUserId(nickname);
		
		startGame();
                if (stateChangeListener != null)
                    stateChangeListener.activate();
	}

	@Override
	public void ping(String name) throws RemoteException {
		//System.out.println("Pingato da "+name);
	}

	private void refreshAllStates(){
		try
		{
			Map<String,Registry> reg = players_registries.getAllRegistries();
			for (String regname : state.getUsernames()){
				if (!regname.equals(nickname)){
					IUno tempServer = 
							(IUno) reg.get(regname).lookup(regname);
					tempServer.refreshState(state,players_registries);
				}
			}
		}
		catch(NotBoundException e)
		{
			e.printStackTrace( );
		}
		catch(RemoteException e)
		{
			e.printStackTrace( );
		}
                if (stateChangeListener != null)
                    stateChangeListener.activate();
                    
	}

	public GameState getState() {
		return state;
	}

	public void setReady(boolean ready){
		state.setUserReady(nickname, ready);

		if (arePlayersReady())
			startGame();

		refreshAllStates();
		System.out.println("Utente prontissimo!");
	}

	private boolean arePlayersReady() {
		boolean allReady = true;
		if (state.getNumberOfUsers()< GameNumbers.minimimum_users)
			allReady = false;
		else for (String u : state.getUsernames()){
			if (!state.getUserReady(u)){
				allReady = false;
				break;
			}
		}
		if (allReady) 
			state.initGame();

		return allReady;
	}

	public ArrayList<Card> getMyCards(){
		return state.getHand(nickname);
	}

	public void startGame(){
		if (state.isGameStarted()){
			gameStartListener.activate();
		}
		else
			System.out.println("Errore: il gioco non è iniziato per tutti!");
	}

	public boolean discardable(Card c){
		Card last_discarded = state.getLastDiscardedCard();
		return c.compatibleWith(last_discarded);
	}

	public void setGameStartListener(GameStart lst) {
		gameStartListener = lst;
	}
        
        public void setStateChangeListener(StateChanged lst) {
		stateChangeListener = lst;
	}

	public Card drawCard(){
		Card c = state.getCard(nickname);
		refreshAllStates();
		return c;
	}

	public void discardCard(Card c){
		state.discard(c, nickname);
                state.passTurn();
		refreshAllStates();
	}
        
        public void passTurn() {
            state.passTurn();
            refreshAllStates();
        }

	public String getNickname() {
		return this.nickname;
	}
	
	public boolean isMyTurn(){
		return state.isMyTurn(nickname);
	}
}
