/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unoxuno.game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author tavy
 */
public class GameBoard extends BasicGameState {
    
    Image gameBG, btnImage, btnOverImage;
    int state, centerX, centerY, initTop;
    GameContainer gc;
    StateBasedGame sbg;
    
    public GameBoard(int s) {    
        state = s;
        centerX = MainClass.width/2;
        centerY = MainClass.height/2;
        initTop = MainClass.height/3;
    }

    @Override
    public int getID() {
        return state;
    }

    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        this.gc = gc;
        this.sbg = sbg;
        initImages();
    }
    
    public void initImages() throws SlickException {
        gameBG = new Image("res/images/main_background.jpg");
        btnImage = new Image("res/images/btnBg.png");
        btnOverImage = new Image("res/images/btnOver.png");
    }

    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        g.drawImage(gameBG.getScaledCopy(MainClass.width, MainClass.height), 0, 0);
    }

    public void update(GameContainer gc, StateBasedGame sbg, int i) throws SlickException {
    }
    
    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
        super.enter(gc, sbg);
        System.out.println("Gioco iniziato!");
    }
    
}
