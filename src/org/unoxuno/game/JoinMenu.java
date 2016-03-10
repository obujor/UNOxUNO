/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unoxuno.game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author tavy
 */
public class JoinMenu extends MainMenu {
      
    String title = "Join room";
    String insertName = "Insert nickname";
    int textLeft, titleHeight, txtLeftsmall;
    TextField nickname, room, roomIp;
    public JoinMenu(int state) {
        super(state);
    }
    
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        super.init(gc, sbg);
        textLeft = centerX - (txtFont.getWidth(title)/2);
        titleHeight = txtFont.getHeight(title);
        txtLeftsmall = centerX - (txtFontSmall.getWidth(insertName)/2);
        
        nickname = new TextField(gc, txtFontSmall, centerX-100, initTop+titleHeight+30, 200, 25);
        nickname.setText("Nickname");
        roomIp = new TextField(gc, txtFontSmall, centerX-100, initTop+titleHeight+70, 200, 25);
        roomIp.setText("Room IP");
        room = new TextField(gc, txtFontSmall, centerX-100, initTop+titleHeight+110, 200, 25);
        room.setText("Room name");
    }
    
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        super.render(gc, sbg, g);
        txtFont.drawString(textLeft, initTop+25, title, txtColor);
        nickname.render(gc, g);
        roomIp.render(gc, g);
        room.render(gc, g);
    }
    
    @Override
    public void addButtons() {
        super.addButton("Start!", new EnterState(MainClass.play),3);
        super.addButton("Back", new EnterState(MainClass.play),4);
    }  
}
