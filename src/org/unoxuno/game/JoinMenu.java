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
    TextField nickname, roomIp, port, serverPort;
    public JoinMenu(int state) {
        super(state);
    }
    
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        super.init(gc, sbg);
    }
    
    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) {
        super.enter(gc, sbg);
        textLeft = centerX - (txtFont.getWidth(title)/2);
        titleHeight = txtFont.getHeight(title)+20;
        txtLeftsmall = centerX - (txtFontSmall.getWidth(insertName)/2);
        int x1 = centerX-100;
        int x2 = centerX+50;
        int y1 = initTop+titleHeight+30;
        int y2 = initTop+titleHeight+60;
        if (nickname != null) {
            nickname.setLocation(x1, y1);
            port.setLocation(x2, y1);
            roomIp.setLocation(x1, y2);
            serverPort.setLocation(x2, y2);
        } else {
            nickname = new TextField(gc, txtFontSmall, x1, y1, 150, 25);
            nickname.setText("Nickname2".concat(MainClass.playerNr));
            port = new TextField(gc, txtFontSmall, x2, y1, 50, 25);
            port.setText("4600".concat(MainClass.playerNr));
            roomIp = new TextField(gc, txtFontSmall, x1, y2, 150, 25);
            roomIp.setText("localhost");
            serverPort = new TextField(gc, txtFontSmall, x2, y2, 50, 25);
            serverPort.setText("4500");
        }
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
        serverPort.setAcceptingInput(state);
    }
    
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        super.render(gc, sbg, g);
        txtFont.drawString(textLeft, initTop+25, title, txtColor);
        nickname.render(gc, g);
        roomIp.render(gc, g);
        port.render(gc, g);
        serverPort.render(gc, g);
    }
    
    @Override
    public void addButtons() {
        setButtonsInputAccepted(false);
        buttons.clear();
        super.addButton("Join", new JoinRoom(), 3);
        super.addButton("Back", new EnterState(MainClass.play), 4);
    }
    
    public class JoinRoom implements ComponentListener {
        public void componentActivated(AbstractComponent ac) {
            joinRoom(nickname.getText(), Integer.parseInt(port.getText()), roomIp.getText(), Integer.parseInt(serverPort.getText()));
            sbg.enterState(MainClass.roomViewer);
        }
    }
}
