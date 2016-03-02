
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RMISquareRootClient {
	 public static void main(String[] args) {
		  int x = Integer.parseInt(args[0]);

		  try
		  {
		   ISquareRoot squareServer = 
		      (ISquareRoot) Naming.lookup("rmi://localhost/RMISquareRoot");

		   double result = squareServer.calculateSquareRoot(x) ; 
		   System.out.println(result);
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
