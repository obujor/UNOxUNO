package org.unoxuno.game;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class MainClass extends StateBasedGame
{

	static int width = 768;
	static int height = 432;
	
	public static final String gameName = "UNOxUNO";
	public static final int menu = 0;
        public static final int play = 1;
        public static final int settings = 2;
        public static final int joinRoom = 3;
        public static final int createRoom = 4;
	
	public MainClass(String gamename)
	{
		super(gamename);
	}

	public void initStatesList(GameContainer gc) throws SlickException {
            this.addState(new MainMenu(menu));
            this.addState(new PlayMenu(play));
            this.addState(new SettingMenu(settings));
            this.addState(new JoinMenu(joinRoom));
            this.addState(new CreateRoomMenu(createRoom));
            this.enterState(menu, new EmptyTransition(), new EmptyTransition()); 
	}
        
        @Override
        public void enterState(int state) {
            this.enterState(state, new FadeOutTransition(), new FadeInTransition());
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