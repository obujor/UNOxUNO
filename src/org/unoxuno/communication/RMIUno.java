package org.unoxuno.communication;

import java.rmi.ConnectException;
import java.rmi.ConnectIOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.unoxuno.game.GameBoard.StateChanged;
import org.unoxuno.game.MainMenu.GameStart;
import org.unoxuno.utilities.GameNumbers;

public class RMIUno extends UnicastRemoteObject 
implements IUno{

	private static final long serialVersionUID = 1L;
	String nickname, service_name = "unoxuno";
	int myId;
	GameState state;
	boolean token = false;
	Registry localRegistry;
	RegistryContainer players_registries;
	GameStart gameStartListener;
	StateChanged stateChangeListener;
	boolean saidUNO;
	boolean already_draw;
	public final Lock lockCards = new ReentrantLock();
	public final Lock lockUsers = new ReentrantLock();
        public final Lock lockLeader = new ReentrantLock();
	boolean stateChanged = true;

	private class PingTask extends TimerTask{

		@Override
		public void run() {
			if (state.isMyTurn(nickname))
				return;
			boolean checknext = false;
			String leader_name = state.getUserActualTurn();
			int actual_id = state.getUserId(leader_name);
			try
			{
				IUno tempServer = 
						(IUno) players_registries.getRegistry(leader_name).lookup(service_name);
				tempServer.ping();
				System.out.println("Server pingato e presente!");
			}
			catch(ConnectIOException | ConnectException e)
			{
				checknext = true;
				System.out.println("Server non presente, elezione");
			}
			catch(NotBoundException e)
			{
				//e.printStackTrace( );
			}
			catch(RemoteException e)
			{
				
				//e.printStackTrace( );
			}

			while (checknext){
				actual_id = state.nextPlayerId(actual_id);	
				String actual_username = state.getUsername(actual_id);
				try
				{
					if (actual_username.equals(nickname)){
						youAreTheLeader();
						return;
					}
					IUno tempServer = 
							(IUno) players_registries.getRegistry(actual_username).lookup(service_name);
					tempServer.youAreTheLeader();
					checknext = false;
				}
				catch(ConnectIOException | ConnectException e){
					checknext = true;
				}
				catch(NotBoundException e)
				{
					//e.printStackTrace( );
				}
				catch(RemoteException e)
				{
					//e.printStackTrace( );
				}
			}
		}

	}

	public RMIUno(String name,int port)throws RemoteException{
		this.nickname = name;
		this.myId = 0;
		this.saidUNO = false;
		this.already_draw = false;
		if (localRegistry == null) {
			localRegistry = LocateRegistry.createRegistry(port);
		}

		localRegistry.rebind(service_name, this);
		state = new GameState(name);
		players_registries = new RegistryContainer(name,localRegistry);
		System.out.println("Binding eseguito su "+name+" in porta "+port);
		Timer t = new Timer();
		t.scheduleAtFixedRate(new PingTask(), 3000, 3000);

	}

	public static void main(String[] args) throws RemoteException {
		RMIUno server = new RMIUno(args[0],Integer.parseInt(args[1]));
		if (args.length == 5)
			server.connectSend(args[0],Integer.parseInt(args[4]),args[3]);
		server.token = true;

	}

	public void connectSend(String myname, int serverport, String serverdom){
		try
		{
			System.out.println("Tentativo di connessione da parte di "+myname+" con porta "+serverport+" all'indirizzo "+serverdom);
			Registry tempRegistry = LocateRegistry.getRegistry(serverdom, serverport);
			IUno tempServer = (IUno) tempRegistry.lookup(service_name);
			tempServer.connectReply(myname,localRegistry);
		}
		catch(NotBoundException e)
		{
			//e.printStackTrace( );
		}
		catch(RemoteException e)
		{
			//e.printStackTrace( );
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
		lockUsers.lock();
		myId = state.getUserId(nickname);
		lockUsers.unlock();
		startGame();
		notifyStateChanged();
	}

	@Override
	public int ping() throws RemoteException {
		//System.out.println("Pingato da "+name);
		return this.state.getClock();
	}

	private void refreshAllStates(){
		this.state.incrementClock();

		Map<String,Registry> reg = players_registries.getAllRegistries();
		for (String regname : state.getUsernames()){
			if (!regname.equals(nickname)){
				try
				{
					IUno tempServer = 
							(IUno) reg.get(regname).lookup(service_name);
					tempServer.refreshState(state,players_registries);
				}
				catch(ConnectIOException | ConnectException e)
				{
					//e.printStackTrace( );
				}
				catch(NotBoundException e)
				{
					//e.printStackTrace( );
				}
				catch(RemoteException e)
				{
					//e.printStackTrace( );
				}
			}
		}

		notifyStateChanged();
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
		lockCards.lock();
		Card last_discarded = state.getLastDiscardedCard();
		lockCards.unlock();
		return c.compatibleWith(last_discarded);
	}

	public void setGameStartListener(GameStart lst) {
		gameStartListener = lst;
	}

	public void setStateChangeListener(StateChanged lst) {
		stateChangeListener = lst;
	}

	public Card drawCard(){
		lockCards.lock();
		Card c = state.getCard(nickname);
		lockUsers.lock();
		refreshAllStates();
		lockUsers.unlock();
		already_draw = true;
		lockCards.unlock();
		return c;
	}

	public boolean discardCard(Card c){
		lockCards.lock();
		lockUsers.lock();
		boolean onlyOne = state.discard(c, nickname);

		boolean penality = false;
		if (onlyOne && !this.saidUNO){
			penality = true;
			state.getCard(nickname);
			state.getCard(nickname);
		}
		this.saidUNO = false;
		this.already_draw = false;
		refreshAllStates();
		lockUsers.unlock();
		lockCards.unlock();

		System.out.println("exit discard");
		return penality;
	}

	public void passTurn() {
		lockUsers.lock();
		state.passTurn();

		this.saidUNO = false;
		this.already_draw = false;
		refreshAllStates();
		lockUsers.unlock();
	}

	public void sayUNO(){
		this.saidUNO = true;
	}

	public String getNickname() {
		return this.nickname;
	}

	public boolean isMyTurn(){
		return state.isMyTurn(nickname);
	}

	public String checkPenality(){
		String penality = state.checkPenality(nickname);
		if (!penality.isEmpty())
			refreshAllStates();
		return penality;
	}

	public boolean discardJollyCard(Card c, String color){
		c.changeColor(color);
		return this.discardCard(c);
	}

	public boolean canDraw(){
		return !this.already_draw;
	}

	public boolean isStateChanged() {
		boolean changed = stateChanged;
		if (stateChanged)
			stateChanged = false;
		return changed;
	}

	private void notifyStateChanged() {
		stateChanged = true;
	}

	@Override
	public boolean youAreTheLeader() throws RemoteException {
            if(state.isMyTurn(nickname) || !lockLeader.tryLock()) return false;
            System.out.println("You are the leader");
		Map<String,Registry> reg = players_registries.getAllRegistries();
		ArrayList<String> users_crashed = new ArrayList<String>();
		for (String regname : state.getUsernames()){
			if (!regname.equals(nickname)){
				try{
					IUno tempServer = 
							(IUno) reg.get(regname).lookup(service_name);
					GameState temp_state = tempServer.requestState();
					int temp_state_id = temp_state.getClock();
					if (temp_state_id > this.state.getClock()){
						this.state = temp_state;
					}
				}
				catch(NotBoundException e)
				{
					//e.printStackTrace( );
				}
				catch(ConnectIOException | ConnectException e)
				{
					users_crashed.add(regname);
				}
				catch(RemoteException e)
				{
					//e.printStackTrace();
				}
			}
		}
		for (String user_to_remove : users_crashed){
			if (this.state.getUsernames().contains(user_to_remove))
				this.state.removeUser(this.state.getUserId(user_to_remove));
		}
		this.myId = this.state.getUserId(nickname);
		this.state.setAsMyTurn(myId);
                lockLeader.unlock();
		refreshAllStates();
		return true;
	}

	@Override
	public GameState requestState() throws RemoteException {
		// TODO Auto-generated method stub
		return this.state;
	}

	public void checkAllUsersState(){
		Map<String,Registry> reg = players_registries.getAllRegistries();
		ArrayList<String> users_crashed = new ArrayList<String>();
		for (String regname : state.getUsernames()){
			if (!regname.equals(nickname)){
				try{
					IUno tempServer = 
							(IUno) reg.get(regname).lookup(service_name);
					tempServer.ping();
				}
				catch(NotBoundException e)
				{
					//e.printStackTrace( );
				}
				catch(ConnectIOException | ConnectException e)
				{
					users_crashed.add(regname);
				}
				catch(RemoteException e)
				{
					//e.printStackTrace( );
				}
			}
		}
		for (String user_to_remove : users_crashed){
			if (this.state.getUsernames().contains(user_to_remove))
				this.state.removeUser(this.state.getUserId(user_to_remove));
		}
		this.myId = this.state.getUserId(nickname);
		this.state.setAsMyTurn(myId);
	}

}
