/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unoxuno.game;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.state.StateBasedGame;
import org.unoxuno.utilities.GameStrings;

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
    }
    
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        super.render(gc, sbg, g);
        textLeft = centerX - (txtFont.getWidth(title)/2);
        txtFont.drawString(textLeft, initTop+20, title, txtColor);
        txtFont.drawString(centerX-125, initTop+100, "Fullscreen", txtColor);
    }
    
    @Override
    public void addButtons() {
        setButtonsInputAccepted(false);
        buttons.clear();
        super.addButton("On/Off", new ToggleFullscreen(), centerX+50, initTop+100);
        super.addButton("Back", new GoToMainListener(), centerX-75, initTop+200);
    }
    
    public void setButtonsPosition() {
        setSizes();
        UnoButton btn = buttons.get(0);
        btn.setPos(centerX+50, initTop+100);
        btn = buttons.get(1);
        btn.setPos(centerX-75, initTop+200);
    }
    
    public class ToggleFullscreen implements ComponentListener {
        public void componentActivated(AbstractComponent ac) {
            Preferences userPrefs = Preferences.userRoot();
            if (userPrefs.getBoolean(GameStrings.fullsreenPref, false)) {
                userPrefs.putBoolean(GameStrings.fullsreenPref, false);
            } else {
                userPrefs.putBoolean(GameStrings.fullsreenPref, true);
            }
            MainClass.setAppDisplayMode();
            setButtonsPosition();
        }
    }
    
}
