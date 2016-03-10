/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unoxuno.game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author tavy
 */
public class SettingMenu extends MainMenu {
    
    String title = "Settings";
    int textLeft;
    public SettingMenu(int state) {
        super(state);
    }
    
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        super.init(gc, sbg);
        textLeft = centerX - (txtFont.getWidth(title)/2);
    }
    
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        super.render(gc, sbg, g);
        txtFont.drawString(textLeft, initTop+20, title, txtColor);
    }
    
    @Override
    public void addButtons() {
        super.addButton("Back", new GoToMainListener(),3);
    }
    
}
