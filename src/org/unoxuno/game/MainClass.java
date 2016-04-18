package org.unoxuno.game;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.unoxuno.communication.RMIUno;

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
        public static final int roomViewer = 5;
        public static final int gameBoard = 6;
        public static String playerNr = "0";
        
        public static int prevStateID = 0;
        
        public static RMIUno player;
        public static boolean isDebug = false;
	
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
            this.addState(new RoomViewer(roomViewer));
            this.addState(new GameBoard(gameBoard));
            this.enterState(menu, new EmptyTransition(), new EmptyTransition());
	}
        
        @Override
        public void enterState(int state) {
            this.prevStateID = this.getCurrentStateID();
            this.enterState(state, new FadeOutTransition(), new FadeInTransition());
        }

	public static void main(String[] args) throws SlickException
	{
            playerNr = (args.length>0) ? args[0] : playerNr;
            int playerPos = Integer.parseInt(playerNr);
            String titleSuf = (playerPos == 0 ) ? "" : " ("+playerNr+")";
            
            if (args.length > 1 && args[1].equals("debug")) {
                isDebug = true;
            }
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new MainClass(gameName.concat(titleSuf)));
			appgc.setShowFPS(false);
			appgc.setDisplayMode(width, height, false);
                        if (isDebug) {
                            int margin = 20;
                            int[][] windowPos = new int[][] {{margin,margin},
                                                             {margin+width, margin},
                                                             {margin+width*2, margin},
                                                             {margin,margin+height},
                                                             {margin+width,margin+height},
                                                             {margin+width*2,margin+height},
                                                             {margin,margin+height*2},
                                                             {margin+width,margin+height*2},
                                                             {margin+width*2,margin+height*2}};
                            Display.setLocation(windowPos[playerPos][0], windowPos[playerPos][1]);
                        }
			appgc.start();
		}
		catch (SlickException ex)
		{
			Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}