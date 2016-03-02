import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMISquareRootServer extends UnicastRemoteObject 
implements ISquareRoot{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RMISquareRootServer()throws RemoteException
	 {
	  
	 }
	 
	 public double calculateSquareRoot(double aNumber)
	 {
	  return Math.sqrt( aNumber);
	 }
	 
	 public static void main(String[] args)
	 {
	  try 
	  {
	   ISquareRoot server = new RMISquareRootServer();
	   Naming.rebind("//localhost/RMISquareRoot",server);
	  }
	  catch (RemoteException e){e.printStackTrace( );}
	  catch (MalformedURLException e) {e.printStackTrace( );}
	 }
}
