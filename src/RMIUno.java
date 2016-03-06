
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RMIUno extends UnicastRemoteObject 
implements IUno{
	
	private static final long serialVersionUID = 1L;
	String nickname;
	ArrayList<String> playersnames = null;
	
	 public RMIUno(String name)throws RemoteException{
		 this.playersnames = new ArrayList<String>();
		 this.nickname = name;
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
		System.out.print(playersnames);
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
		System.out.print(playersnames);
		
	}

}
