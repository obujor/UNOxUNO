/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unoxuno.game;

import java.rmi.RemoteException;
import java.rmi.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.StateBasedGame;
import org.unoxuno.communication.RMIUno;

/**
 *
 * @author tavy
 */
public class JoinMenu extends MainMenu {
      
    String title = "Join room";
    String insertName = "Insert nickname";
    int textLeft, titleHeight, txtLeftsmall;
    TextField nickname, room, roomIp, port, serverPort;
    public JoinMenu(int state) {
        super(state);
    }
    
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        super.init(gc, sbg);
        textLeft = centerX - (txtFont.getWidth(title)/2);
        titleHeight = txtFont.getHeight(title)+20;
        txtLeftsmall = centerX - (txtFontSmall.getWidth(insertName)/2);
        
        nickname = new TextField(gc, txtFontSmall, centerX-100, initTop+titleHeight+30, 150, 25);
        nickname.setText("Nickname2");
        port = new TextField(gc, txtFontSmall, centerX+50, initTop+titleHeight+30, 50, 25);
        port.setText("4600");
        roomIp = new TextField(gc, txtFontSmall, centerX-100, initTop+titleHeight+60, 150, 25);
        roomIp.setText("localhost");
        serverPort = new TextField(gc, txtFontSmall, centerX+50, initTop+titleHeight+60, 50, 25);
        serverPort.setText("4500");
        room = new TextField(gc, txtFontSmall, centerX-75, initTop+titleHeight+90, 150, 25);
        room.setText("Nickname");
        setInputAccepting(false);
    }
    
    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) {
        super.enter(gc, sbg);
        setInputAccepting(true);
    }
    
    @Override
    public void leave(GameContainer gc, StateBasedGame sbg) {
        super.leave(gc, sbg);
        setInputAccepting(false);
    }
    
    public void setInputAccepting(boolean state) {
        nickname.setAcceptingInput(state);
        roomIp.setAcceptingInput(state);
        port.setAcceptingInput(state);
        room.setAcceptingInput(state);
        serverPort.setAcceptingInput(state);
    }
    
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        super.render(gc, sbg, g);
        txtFont.drawString(textLeft, initTop+25, title, txtColor);
        nickname.render(gc, g);
        roomIp.render(gc, g);
        room.render(gc, g);
        port.render(gc, g);
        serverPort.render(gc, g);
    }
    
    @Override
    public void addButtons() {
        super.addButton("Back", new GoBack(), centerX-220, MainClass.height-50);
        super.addButton("Join", new JoinRoom(), centerX+20, MainClass.height-50);
    }
    
    public class JoinRoom implements ComponentListener {
        public void componentActivated(AbstractComponent ac) {
            try {
                MainClass.player = new RMIUno(nickname.getText(), Integer.parseInt(port.getText()));
                MainClass.player.setGameStartListener(new GameStart());
                MainClass.player.connectSend(nickname.getText(), Integer.parseInt(serverPort.getText()), room.getText(), roomIp.getText());
                sbg.enterState(MainClass.roomViewer);
            } catch (RemoteException ex) {
                Logger.getLogger(CreateRoomMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
