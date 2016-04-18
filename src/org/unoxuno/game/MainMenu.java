package org.unoxuno.game;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.unoxuno.communication.RMIUno;
import static org.unoxuno.game.MainClass.player;

public class MainMenu extends BasicGameState {
	
	Image gameBG, btnImage, btnOverImage;
        ArrayList<UnoButton> buttons;
        GameContainer gc;
        StateBasedGame sbg;
        int state, centerX, centerY, initTop;
        TrueTypeFont txtFont, txtFontSmall;
        Color txtColor = new Color(0,0,0);
        public static java.awt.Font trueTypeFont;
        boolean debugCreated = false;
        
	public MainMenu(int s) {
            buttons = new ArrayList<UnoButton>();
            state = s;
            centerX = MainClass.width/2;
            centerY = MainClass.height/2;
            initTop = MainClass.height/3;
	}

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
            this.gc = gc;
            this.sbg = sbg;
            try {
                    trueTypeFont = java.awt.Font.createFont(Font.TRUETYPE_FONT, new File("res/font/Ubuntu.ttf"));
            } catch (FontFormatException e) {
                    throw new SlickException(e.getMessage());
            } catch (IOException ex) {
                Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
            }
            txtFont = new TrueTypeFont(trueTypeFont.deriveFont(30f), true);
            txtFontSmall = new TrueTypeFont(trueTypeFont.deriveFont(18f), true);
            initImages();
            addButtons();
	}
        
                
        public void initImages() throws SlickException {
            gameBG = new Image("res/images/main_background.jpg");
            btnImage = new Image("res/images/btnBg.png");
            btnOverImage = new Image("res/images/btnOver.png");
        }
        
        public void addButtons() {
            addButton("Play", new EnterState(MainClass.play));
            addButton("Settings", new EnterState(MainClass.settings));
            addButton("Exit", new ExitListener());
        }
        
        protected UnoButton addButton(String text, ComponentListener listener) {
            return addButton(text, listener, buttons.size()+1);
        }
        
        protected UnoButton addButton(String text, ComponentListener listener, int order) {
            int buttonWidth = 200;
            int buttonHeight = 40;
            int marginV = 20;
            int left = centerX-buttonWidth/2;
            
            return addButton(text, listener, left, initTop+buttonHeight*order+marginV*order);
        }
        
        protected UnoButton addButton(String text, ComponentListener listener, int x, int y) {           
            UnoButton button = new UnoButton(this.gc, btnImage, x, y, text, listener);
            button.setMouseOverImage(btnOverImage);
            button.setAcceptingInput(false);
            buttons.add(button);
            return button;
        }
        
        @Override
        public void enter(GameContainer gc, StateBasedGame sbg) {
            if (MainClass.isDebug && state == 0 && !debugCreated) {
                String room = "Nickname";
                int port = 4500;
                String roomIp = "localhost";
                if (MainClass.playerNr.equals("0")) {
                    createRoom(room, port);
                } else {
                    joinRoom(room.concat(MainClass.playerNr), port+Integer.parseInt(MainClass.playerNr), roomIp, port, room);
                    MainClass.player.setReady(true);
                }
                debugCreated = true;
                sbg.enterState(MainClass.roomViewer);
            } else {
                setButtonsInputAccepted(true);
            }
        }
        
        public void setButtonsInputAccepted(boolean accept) {
            for(UnoButton b : buttons) {
                b.setAcceptingInput(accept);
            } 
        }
        
        @Override
        public void leave(GameContainer gc, StateBasedGame sbg) {
            setButtonsInputAccepted(false);
        }

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.drawImage(gameBG.getScaledCopy(MainClass.width, MainClass.height), 0, 0);
                for(UnoButton b : buttons) {
                    b.render(gc, g);
                }           
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2) throws SlickException {		
	}

	@Override
	public int getID() {
            return state;
	}
        
        private class ExitListener implements ComponentListener {
            public void componentActivated(AbstractComponent ac) {
                System.exit(0);
            }
        }
        
        public class EnterState implements ComponentListener {
            int state;
            public EnterState(int s) {
                state = s;
            }
            public void componentActivated(AbstractComponent ac) {
                sbg.enterState(state);
            }
        }
        
        public class GoBack implements ComponentListener {
            public void componentActivated(AbstractComponent ac) {
                sbg.enterState(MainClass.prevStateID);
            }
        }
        
        public class GoToMainListener implements ComponentListener {
            public void componentActivated(AbstractComponent ac) {
                sbg.enterState(MainClass.menu);
            }
        }
        
        public class GameStart implements ComponentListener {
            boolean activated = false;
            public void componentActivated(AbstractComponent ac) {}
            public void activate() {
                if (!activated)
                    sbg.enterState(MainClass.gameBoard);
                activated = true;    
            }
        }
                
        public void createRoom(String nick, int port) {
            createPlayer(nick, port);
        }
        
        public void joinRoom(String nick, int port, String roomIp, int roomPort, String room) {
            createPlayer(nick, port);
            MainClass.player.connectSend(nick, roomPort, room, roomIp);
        }
        
        private void createPlayer(String nick, int port) {
            try {
                MainClass.player = new RMIUno(nick, port);
            } catch (RemoteException ex) {
                Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
            }
            MainClass.player.setGameStartListener(new GameStart());
        }
}
