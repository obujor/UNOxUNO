import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IUno extends Remote {
	void connectReply(String name) throws RemoteException;
	void refreshUserList(ArrayList<String> users) throws RemoteException;
}


