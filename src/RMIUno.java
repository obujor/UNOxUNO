
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class RMIUno extends UnicastRemoteObject 
implements IUno{
	
	private static final long serialVersionUID = 1L;
	String nickname;
	int myId;
	ArrayList<String> playersnames = null;
	
	private class Task extends TimerTask{

		@Override
		public void run() {
			boolean changed = false;
			boolean checknext = true;
			while (checknext){
				checknext = false;
				int nextId = (myId +1)% playersnames.size();
				if (nextId != myId){
					String nextname = playersnames.get(nextId);
					try
					{
						IUno tempServer = 
								(IUno) Naming.lookup("rmi://localhost/"+nextname);
						tempServer.ping(playersnames.get(myId));
					}
					catch(ConnectException e){
						System.out.println("Successivo non trovato, rimozione");
						playersnames.remove(nextId);
						checknext = true;
						changed = true;
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
				for (int i=0; i<playersnames.size(); i++){
					if (i != myId){
						IUno tempServer = 
								(IUno) Naming.lookup("rmi://localhost/"+playersnames.get(i));
						tempServer.refreshUserList(playersnames);
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
	 public RMIUno(String name)throws RemoteException{
		 this.playersnames = new ArrayList<String>();
		 this.nickname = name;
		 this.playersnames.add(name);
		 this.myId = 0;
		Timer t = new Timer();
		t.scheduleAtFixedRate(new Task(), 2000, 2000);

	 }
	 
	 public static void main(String[] args) {
		 RMIUno server = createServer(args[0]);
		 if (args.length == 2)
				  server.connectSend(args[0],args[1]);
			  
	 }
		  
	 private static RMIUno createServer(String name){
			RMIUno server = null;
		 	try 
			  {
			   server = new RMIUno(name);
			   Naming.rebind("//localhost/"+name,server);
			  }
			  catch (RemoteException e){e.printStackTrace( );}
			  catch (MalformedURLException e) {e.printStackTrace( );}
			return server;
	 }
	 
	 private void connectSend(String myname, String servername){
		 try
		  {
		   IUno tempServer = 
		      (IUno) Naming.lookup("rmi://localhost/"+servername);
		   tempServer.connectReply(myname);
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
	public void connectReply(String name) throws RemoteException {
		this.playersnames.add(name);
		//System.out.print(playersnames);
		System.out.println("Utente entrato in stanza: "+name);
		try
		  {
			for (String n: playersnames){
		   IUno tempServer = 
		      (IUno) Naming.lookup("rmi://localhost/"+n);
		   tempServer.refreshUserList(playersnames);
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
	public void refreshUserList(ArrayList<String> users) throws RemoteException {
		this.playersnames = users;
		//System.out.print(playersnames);
		for (int i=0; i<users.size(); i++){
			String u = users.get(i);
			if (u.equals(this.nickname))
				myId = i;
		}
		
	}

	@Override
	public void ping(String name) throws RemoteException {
		System.out.println("Pingato da "+name);
		
	}

}
