package org.unoxuno.communication;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public interface IUno extends Remote {
	void connectReply(String name, Registry r) throws RemoteException;
	void refreshState(GameState s, RegistryContainer r) throws RemoteException;
	void ping(String name) throws RemoteException;
}


