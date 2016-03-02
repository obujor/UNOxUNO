package org.unoxuno;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerDiProva extends UnicastRemoteObject 
implements InterfacciaDiProva{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServerDiProva()throws RemoteException
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
	   InterfacciaDiProva server = new ServerDiProva();
	   Naming.rebind("//localhost/ServerDiProva",server);
	  }
	  catch (RemoteException e){e.printStackTrace( );}
	  catch (MalformedURLException e) {e.printStackTrace( );}
	 }
}
