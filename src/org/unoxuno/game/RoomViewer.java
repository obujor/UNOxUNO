/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unoxuno.game;

import java.util.ArrayList;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
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
        Image readyTick;
	ArrayList<String> players = new ArrayList<String>();

	public RoomViewer(int s) {
		super(s);
	}

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		super.init(gc, sbg);
		titleHeight = txtFont.getHeight(title);
		textLeft = centerX - (txtFont.getWidth(title)/2);
                readyTick = new Image("res/images/ok_tick.png").getScaledCopy(20, 20);
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		super.render(gc, sbg, g);
		txtFont.drawString(textLeft, initTop+25, title, txtColor);
		Color prevColor = g.getColor();
		g.setColor(txtColor);
		players = MainClass.player.getState().getUsernames();
		for(int i=0; i<players.size(); i++) {
			g.drawRect(centerX-100, initTop+55+i*20, 30, 20);
			g.drawRect(centerX-100, initTop+55+i*20, 200, 20);
			txtFontSmall.drawString(centerX-90, initTop+55+i*20, Integer.toString(i+1), txtColor);
			txtFontSmall.drawString(centerX-60, initTop+55+i*20, players.get(i), txtColor);
                        if (MainClass.player.getState().getUserReady(players.get(i)))
                            g.drawImage(readyTick, centerX+80, initTop+55+i*20);
		}
		g.setColor(prevColor);
	}

	@Override
	public void addButtons() {
		super.addButton("Back", new GoBack(), centerX-220, MainClass.height-50);
		super.addButton("Start", new Start(), centerX+20, MainClass.height-50);
	} 


	public class Start implements ComponentListener {
		public void componentActivated(AbstractComponent ac) {
			MainClass.player.setReady(true);
		}
	}

}
