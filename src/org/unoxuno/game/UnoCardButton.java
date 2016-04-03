/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unoxuno.game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.gui.MouseOverArea;

/**
 *
 * @author tavy
 */
public class UnoCardButton extends MouseOverArea {
    int y, x;
    static long lastNotified = System.currentTimeMillis();
    public UnoCardButton(GUIContext gc, Image image, int x, int y, ComponentListener listener) {
        super(gc, image, x, y, listener);
        this.y = y;
        this.x = x;
    }
    
    @Override
    public void mousePressed(int button, int mx, int my) {
        if (this.isMouseOver() && (System.currentTimeMillis()-UnoCardButton.lastNotified) > 100) {
            UnoCardButton.lastNotified = System.currentTimeMillis();
            this.notifyListeners();
            this.consumeEvent();
        }
    }
    
    @Override
    public void render(GUIContext gc, Graphics g) {
        super.render(gc, g);
        if (this.isMouseOver() && this.isAcceptingInput()) {
            this.setLocation(x, y-30);
        } else {
            this.setLocation(x, y);
        }
    }   
}
