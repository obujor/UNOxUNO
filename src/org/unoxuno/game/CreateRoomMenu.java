/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unoxuno.game;

import java.rmi.RemoteException;
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
public class CreateRoomMenu extends MainMenu {
    String title = "Create room";
    int textLeft;
    TextField nickname, port;
    public CreateRoomMenu(int state) {
        super(state);
    }
    
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        super.init(gc, sbg);
    }
    
    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) {
        super.enter(gc, sbg);
        int titleHeight = txtFont.getHeight(title);
        int x1 = centerX-100;
        int y1 = initTop+titleHeight+30;
        int y2 = initTop+titleHeight+70;
        if (nickname != null) {
            nickname.setLocation(x1, y1);
            port.setLocation(x1, y2);
        } else {
            nickname = new TextField(gc, txtFontSmall, x1, y1, 200, 25);
            nickname.setText("Nickname");
            port = new TextField(gc, txtFontSmall, x1, y2, 200, 25);
            port.setText("4500");
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
        port.setAcceptingInput(state);
    }
    
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        super.render(gc, sbg, g);
        textLeft = centerX - (txtFont.getWidth(title)/2);
        txtFont.drawString(textLeft, initTop+25, title, txtColor);
        nickname.render(gc, g);
        port.render(gc, g);
    }
    
    @Override
    public void addButtons() {
        setButtonsInputAccepted(false);
        buttons.clear();
        super.addButton("Create", new CreateRoom(),3);
        super.addButton("Back", new EnterState(MainClass.play),4);
    } 
    
    
    public class CreateRoom implements ComponentListener {
        public void componentActivated(AbstractComponent ac) {
            createRoom(nickname.getText(), Integer.parseInt(port.getText()));
            sbg.enterState(MainClass.roomViewer);
        }
    }
}
