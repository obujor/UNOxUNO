/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unoxuno.communication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

/**
 *
 * @author tavy
 */
public class AnchorSocketFactory extends RMISocketFactory {
    
    private InetAddress ipInterface = null;
    
    public AnchorSocketFactory() {}
    public AnchorSocketFactory(InetAddress ipInterface) {
        this.ipInterface = ipInterface;
    }

    @Override
    public Socket createSocket(String string, int port) throws IOException {
        return (new Socket(ipInterface, port));
    }

    @Override
    public ServerSocket createServerSocket(int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port, 50, ipInterface);
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return (serverSocket);
    }
    
    @Override
    public boolean equals(Object that) {
        return (that != null && that.getClass() == this.getClass());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + (this.ipInterface != null ? this.ipInterface.hashCode() : 0);
        return hash;
    }
    
}
