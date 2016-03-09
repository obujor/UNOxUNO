package org.unoxuno.communication;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

public class RMIUno extends UnicastRemoteObject 
implements IUno{
	
	private static final long serialVersionUID = 1L;
	String nickname;
	String domain;
	int myId;
	GameState state;
	boolean token = false;
	
	private class Task extends TimerTask{

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
	 public RMIUno(String name, String dom)throws RemoteException{
		 this.nickname = name;
		 this.domain = dom;
		 state = new GameState(name,dom);
		 this.myId = 0;
		 try{
			 Naming.rebind("//"+dom+"/"+name,this);
		 }
		 catch(MalformedURLException e)
		 {
			 e.printStackTrace();
		 }
		 Timer t = new Timer();
		 t.scheduleAtFixedRate(new Task(), 2000, 2000);

	 }
	 
	 public static void main(String[] args) throws RemoteException {
		 RMIUno server = new RMIUno(args[1],args[0]);
		 if (args.length == 4)
				  server.connectSend(args[0],args[1],args[2],args[3]);
		 		  server.token = true;
			  
	 }
	 
	 private void connectSend(String mydom,String myname, String serverdom, String servername){
		 try
		  {
		   IUno tempServer = 
		      (IUno) Naming.lookup("rmi://"+serverdom+"/"+servername);
		   tempServer.connectReply(mydom,myname);
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

	@Override
	public void connectReply(String dom,String name) throws RemoteException {
		state.addUser(name,dom);
		//System.out.print(playersnames);
		System.out.println("Utente entrato in stanza: "+name);
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

	@Override
	public void refreshState(GameState s) throws RemoteException {
		state = s;
		//System.out.print(playersnames);
		myId = state.getUserId(nickname);
	}

	@Override
	public void ping(String name) throws RemoteException {
		System.out.println("Pingato da "+name);
		
	}

}
