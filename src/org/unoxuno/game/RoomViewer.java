/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unoxuno.game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author tavy
 */
public class RoomViewer extends MainMenu {
    String title = "Game room";
    int textLeft, titleHeight;
    public RoomViewer(int s) {
        super(s);
    }
    
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        super.init(gc, sbg);
        titleHeight = txtFont.getHeight(title);
        textLeft = centerX - (txtFont.getWidth(title)/2);
        
    }
    
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        super.render(gc, sbg, g);
        txtFont.drawString(textLeft, initTop+25, title, txtColor);
    }
    
        @Override
    public void addButtons() {
        super.addButton("Start", new Start(),3);
        super.addButton("Back", new EnterState(MainClass.play),4);
    } 
    
    
    public class Start implements ComponentListener {
        public void componentActivated(AbstractComponent ac) {
            //sbg.enterState(MainClass.menu);
        }
    }
    
}
