package org.unoxuno.communication;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

public class RMIUno extends UnicastRemoteObject 
implements IUno{
	
	private static final long serialVersionUID = 1L;
	String nickname;
	int myId;
	GameState state;
	boolean token = false;
	Registry localRegistry;
	
	/*private class Task extends TimerTask{

		@Override
		public void run() {
			boolean changed = false;
			boolean checknext = true;
			while (checknext){
				checknext = false;
				int nextId = state.getNextId(myId);
				if (nextId != myId){
					String nextname = state.getUsername(nextId);
					String nextdomain = state.getDomain(nextId);
					try
					{
						IUno tempServer = 
								(IUno) Naming.lookup("rmi://"+nextdomain+"/"+nextname);
						tempServer.ping(nickname);
					}
					catch(ConnectException e){
						System.out.println("Successivo non trovato, rimozione");
						state.removeUser(nextId);
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
					catch(MalformedURLException e)
					{
						e.printStackTrace( );
					}
				}
			}
			if (changed){
				try
			  {
				for (int i=0; i<state.getNumberOfUsers(); i++){
					if (i != myId){
						IUno tempServer = 
								(IUno) Naming.lookup("rmi://"+state.getDomain(i)+"/"+state.getUsername(i));
						tempServer.refreshState(state);
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
			  catch(MalformedURLException e)
			  {
			   e.printStackTrace( );
			  }
		}
		}
		
	}
	*/
	 public RMIUno(String name,int port)throws RemoteException{
		 this.nickname = name;
		 this.myId = 0;
		 localRegistry = LocateRegistry.createRegistry(port);
		 localRegistry.rebind(name, this);
		 state = new GameState(name, localRegistry);
		 System.out.println("Binding eseguito su "+name+" in porta "+port);
			 //Naming.rebind("//"+dom+"/"+name,this);
		 //Timer t = new Timer();
		 //t.scheduleAtFixedRate(new Task(), 2000, 2000);

	 }
	 
	 public static void main(String[] args) throws RemoteException {
		 RMIUno server = new RMIUno(args[0],Integer.parseInt(args[1]));
		 if (args.length == 5)
				  server.connectSend(args[0],Integer.parseInt(args[4]),args[2],args[3]);
		 		  server.token = true;
			  
	 }
	 
	 private void connectSend(String myname, int serverport, String servername, String serverdom){
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
		state.addUser(name,r);
		//System.out.print(playersnames);
		System.out.println("Utente entrato in stanza: "+name);
		try
		  {
			for (int i=0; i<state.getNumberOfUsers(); i++){
				if (i != myId){
					Registry tempRegistry = state.getRegistry(i);
					IUno tempServer = 
							(IUno) tempRegistry.lookup(state.getUsername(i));
					tempServer.refreshState(state);
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
	}

	@Override
	public void refreshState(GameState s) throws RemoteException {
		state = s;
		System.out.println("Aggiornato stato");
		myId = state.getUserId(nickname);
	}

	@Override
	public void ping(String name) throws RemoteException {
		System.out.println("Pingato da "+name);
		
	}

}
