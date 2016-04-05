/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unoxuno.game;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.gui.MouseOverArea;

/**
 *
 * @author tavy
 */
public class UnoButton extends MouseOverArea {
    
    int w, h, y, x, textLeft, textTop;
    String text;
    TrueTypeFont btnFont;
    GUIContext gc;
    static long lastNotified = System.currentTimeMillis();
    Color txtColor;
    
    public UnoButton(GUIContext gc, Image im, int x, int y, String text, ComponentListener listener) {
        super(gc, im, x, y, listener);
        this.w = im.getWidth();
        this.h = im.getHeight();
        this.y = y;
        this.x = x;
        this.text = text;
        this.gc = gc;
        this.btnFont = new TrueTypeFont(MainMenu.trueTypeFont.deriveFont(30f), true);
	this.textLeft = this.x + (this.w/2 - (this.btnFont.getWidth(this.text)/2));
        this.textTop = this.y + (this.h/2 - (this.btnFont.getHeight(this.text)/2));
        this.txtColor = Color.white;
    }
    
    @Override
    public void mousePressed(int button, int mx, int my) {
        if (this.isMouseOver() && (System.currentTimeMillis()-UnoButton.lastNotified) > 100) {
            UnoButton.lastNotified = System.currentTimeMillis();
            this.notifyListeners();
            this.consumeEvent();
        }
    }
    
    @Override
    public void render(GUIContext gc, Graphics g) {
        super.render(gc, g);
        g.drawRect(this.x, this.y, this.w, this.h);
        this.btnFont.drawString(this.textLeft, this.textTop, this.text, this.txtColor);
    }
    
    public void setTextColor(Color c) {
        txtColor = c;
    }
    
}

