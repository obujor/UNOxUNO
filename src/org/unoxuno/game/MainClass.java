package org.unoxuno.game;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class MainClass extends BasicGame
{

	Image gameBG = null;
	static int width = 768;
	static int height = 432;
	public MainClass(String gamename)
	{
		super(gamename);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		gameBG = new Image("res/images/main_background.jpg");
	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		g.drawImage(gameBG.getScaledCopy(MainClass.width, MainClass.height), 0, 0);
	}

	public static void main(String[] args)
	{
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new MainClass("UNOxUNO Game"));
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