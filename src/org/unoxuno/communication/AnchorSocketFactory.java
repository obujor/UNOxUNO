/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unoxuno.communication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

/**
 *
 * @author tavy
 */
public class AnchorSocketFactory extends RMISocketFactory {
    
    public AnchorSocketFactory() {}

    public Socket createSocket( String host, int port )
            throws IOException
        {
            Socket socket = new Socket();
            socket.setSoTimeout( 2000 );
            socket.setSoLinger( false, 0 );
            socket.connect( new InetSocketAddress( host, port ), 2000 );
            return socket;
        }

        public ServerSocket createServerSocket( int port )
            throws IOException
        {
            return new ServerSocket( port );
        }
    
    
}
