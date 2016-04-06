/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unoxuno.game;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.ShapeFill;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.gui.MouseOverArea;

/**
 *
 * @author tavy
 */
public class UnoCardButton extends MouseOverArea {
    int y, x;
    String text;
    TrueTypeFont btnFont;
    static long lastNotified = System.currentTimeMillis();
    public UnoCardButton(GUIContext gc, Image image, int x, int y, ComponentListener listener) {
        super(gc, image, x, y, listener);
        this.y = y;
        this.x = x;
    }
    
    public void setText(String txt) {
        text = txt;
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
        this.btnFont = this.btnFont != null ? this.btnFont : new TrueTypeFont(MainMenu.trueTypeFont.deriveFont(20f), true);
        int updatedY = y;
        if (this.isMouseOver() && this.isAcceptingInput()) {
            updatedY = y-30;
            this.setLocation(x, updatedY);
        } else {
            this.setLocation(x, y);
        }
        if (text != null) {
            Circle c = new Circle(x+40,updatedY+11, 10);
            g.fill(c);
            this.btnFont.drawString(x+35, updatedY, text, Color.black);
        }
    }   
}
