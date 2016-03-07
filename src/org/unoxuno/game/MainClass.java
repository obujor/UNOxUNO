package org.unoxuno.game;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class MainClass extends StateBasedGame
{

	static int width = 768;
	static int height = 432;
	
	public static final String gameName = "UNOxUNO";
	public static final int menu = 0;
	
	public MainClass(String gamename)
	{
		super(gamename);
		this.addState(new MainMenu(menu));
	}

	public void initStatesList(GameContainer gc) throws SlickException {
		this.getState(menu).init(gc, this);
		this.enterState(menu);
                
	}

	public static void main(String[] args) throws SlickException
	{
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new MainClass(gameName));
			appgc.setShowFPS(false);
			appgc.setDisplayMode(width, height, false);
			appgc.start();
		}
		catch (SlickException ex)
		{
			Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}