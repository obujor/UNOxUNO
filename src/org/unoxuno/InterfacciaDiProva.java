package org.unoxuno;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfacciaDiProva extends Remote {
	double calculateSquareRoot(double aNumber) throws RemoteException;
}


