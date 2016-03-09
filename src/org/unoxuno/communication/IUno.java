package org.unoxuno.communication;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IUno extends Remote {
	void connectReply(String dom,String name) throws RemoteException;
	void refreshState(GameState s) throws RemoteException;
	void ping(String name) throws RemoteException;
}


