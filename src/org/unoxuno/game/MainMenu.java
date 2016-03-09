package org.unoxuno.game;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class MainMenu extends BasicGameState {
	
	Image gameBG = null;
        UnoButton playBtn = null;
        UnoButton settingsBtn = null;
        UnoButton exitBtn = null;
        int buttonWidth = 150;
        int buttonHeight = 40;
        public static java.awt.Font trueTypeFont;
        
	public MainMenu(int state) {
            
	}

	@Override
	public void init(GameContainer gc, StateBasedGame arg1) throws SlickException {
		gameBG = new Image("res/images/main_background.jpg");
                try {
			trueTypeFont = java.awt.Font.createFont(Font.TRUETYPE_FONT, new File("res/font/Ubuntu.ttf"));
		} catch (FontFormatException e) {
			throw new SlickException(e.getMessage());
		} catch (IOException ex) {
                    Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                int left = MainClass.width/2-buttonWidth/2;
                int topInit = MainClass.height/3;
                int marginV = 20;
                
                Image btnImage = new Image("res/images/btnBg.png");
                Image btnOverImage = new Image("res/images/btnOver.png");
                playBtn = new UnoButton(gc, btnImage, left, topInit+buttonHeight+marginV, "Play", new PlayListener());
                playBtn.setMouseOverImage(btnOverImage);
                playBtn.inputStarted();
                settingsBtn = new UnoButton(gc, btnImage, left, topInit+buttonHeight*2+marginV*2, "Settings", new SettingsListener());
                settingsBtn.setMouseOverImage(btnOverImage);
                settingsBtn.inputStarted();
                exitBtn = new UnoButton(gc, btnImage, left, topInit+buttonHeight*3+marginV*3, "Exit", new ExitListener());
                exitBtn.setMouseOverImage(btnOverImage);
                exitBtn.inputStarted();
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		// TODO Auto-generated method stub
		g.drawImage(gameBG.getScaledCopy(MainClass.width, MainClass.height), 0, 0);
                playBtn.render(gc, g);
                settingsBtn.render(gc, g);
                exitBtn.render(gc, g);
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2) throws SlickException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}
        
        private class PlayListener implements ComponentListener {
            public void componentActivated(AbstractComponent ac) {
                System.out.println("Play");
                
            }
        }
        
        private class SettingsListener implements ComponentListener {
            public void componentActivated(AbstractComponent ac) {
                System.out.println("Settings");
            }
        }
        
        private class ExitListener implements ComponentListener {
            public void componentActivated(AbstractComponent ac) {
                System.exit(0);
            }
        }

}
