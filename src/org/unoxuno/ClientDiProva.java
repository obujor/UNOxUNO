package org.unoxuno;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientDiProva {
	 public static void main(String[] args) {
		  int x = Integer.parseInt(args[0]);

		  try
		  {
		   ServerDiProva squareServer = 
		      (ServerDiProva) Naming.lookup("rmi://localhost/ServerDiProva");

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
