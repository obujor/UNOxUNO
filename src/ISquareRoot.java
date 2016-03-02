import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ISquareRoot extends Remote {
	double calculateSquareRoot(double aNumber) throws RemoteException;
}


