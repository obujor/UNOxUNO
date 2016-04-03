/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unoxuno.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import org.unoxuno.communication.Card;
import static org.unoxuno.game.MainMenu.trueTypeFont;

/**
 *
 * @author tavy
 */
public class GameBoard extends BasicGameState {
    
    Image gameBG, btnImage, btnOverImage, unoDeck, unoCard;
    int state, centerX = MainClass.width/2, 
        centerY = MainClass.height/2, cardW = 73, cardH=109,
        maxWidth = MainClass.width-100, playersCardW = cardW/2, 
        playersCardH = cardH/2;
    GameContainer gc;
    StateBasedGame sbg;
    private final Map<String,Image> cardImages;
    private final int[][] playersPosX = new int[][] {{centerX},{10, centerX},{10, centerX,MainClass.width-50}};
    private final int[][] playersPosY = new int[][] {{20}, {centerY, 20},{centerY, 20, centerY}};
    ArrayList<UnoButton> buttons = new ArrayList<UnoButton>();
    TrueTypeFont txtFontSmall, txtCardNr;
    Color playersTxtColor = new Color(0,0,0);
    
    public GameBoard(int s) {    
        state = s;
        
        cardImages = new HashMap<String,Image>();
    }

    @Override
    public int getID() {
        return state;
    }

    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        this.gc = gc;
        this.sbg = sbg;
        
        txtFontSmall = new TrueTypeFont(trueTypeFont.deriveFont(20f), true);
        txtCardNr  = new TrueTypeFont(trueTypeFont.deriveFont(40f), true);
        initImages();
        addButtons();
    }
    
    private void addButtons() {
        addButton("Add", new AddCard(), centerX-200, MainClass.height-120);
        addButton("Remove", new DiscardCard(), centerX, MainClass.height-120);
    }
    
    public void initImages() throws SlickException {
        gameBG = new Image("res/images/game_background.jpg").getScaledCopy(MainClass.width, MainClass.height);
        btnImage = new Image("res/images/btnBg.png");
        btnOverImage = new Image("res/images/btnOver.png");
        unoDeck = new Image("res/images/uno_deck.png").getScaledCopy(106, 154);
        unoCard = new Image("res/images/uno.png").getScaledCopy(playersCardW, playersCardH);
    }

    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        g.drawImage(gameBG, 0, 0);
        drawMainCard(g);
        drawMyCards(g);
        drawPlayers(g);
        for(UnoButton b : buttons) {
            b.render(gc, g);
        }
    }
    
    private void drawMainCard(Graphics g) throws SlickException {
        Card c = MainClass.player.getState().getLastDiscardedCard();
        int w = cardW+cardW/3;
        int h = cardH+cardH/3;
        g.drawImage(getImage(c).getScaledCopy(w, h), centerX-w/2, centerY-h/2);
        g.drawImage(unoDeck, centerX-w/2-120, centerY-h/2-10);
    }
    
    private void drawMyCards(Graphics g) throws SlickException {
        ArrayList<Card> myCards = MainClass.player.getMyCards();
        if (myCards.isEmpty()) return;
        int margin = maxWidth/myCards.size()-cardW;
        margin = margin > 10 ? 10 : margin;
        int width = margin+cardW;
        int totalWidth = myCards.size()*width-margin;
        int initWidth = centerX-(totalWidth/2);
        for(int i=0; i<myCards.size(); i++) {
            Card c = myCards.get(i);
            g.drawImage(getImage(c), initWidth+i*width, MainClass.height-cardH/2);
        }
    }
    
    private void drawPlayers(Graphics g) throws SlickException {
        ArrayList<String> users = MainClass.player.getState().getUsernames();
        users.remove(MainClass.player.getNickname());
        
        for(int i=0; i<users.size(); i++) {
            int cardsNr = MainClass.player.getState().getHand(users.get(i)).size();
            txtCardNr.drawString(playersPosX[users.size()-1][i]+playersCardW, playersPosY[users.size()-1][i], Integer.toString(cardsNr), playersTxtColor);
            txtFontSmall.drawString(playersPosX[users.size()-1][i], playersPosY[users.size()-1][i]-20, users.get(i), playersTxtColor);
            g.drawImage(unoCard, playersPosX[users.size()-1][i], playersPosY[users.size()-1][i]);
        }
    }
    
    private Image getImage(Card c) throws SlickException {
        String uri = c.getUri();
        if (cardImages.containsKey(uri))
            return cardImages.get(uri);
        Image img = new Image(uri).getScaledCopy(cardW, cardH);
        cardImages.put(uri, img);
        return img;
    }
    
    public void update(GameContainer gc, StateBasedGame sbg, int i) throws SlickException {
    }
    
    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
        super.enter(gc, sbg);
        System.out.println("Gioco iniziato!");
        setButtonsInputAccepted(true);
    }
    
    @Override
    public void leave(GameContainer gc, StateBasedGame sbg) {
        setButtonsInputAccepted(false);
    }
    
    public void setButtonsInputAccepted(boolean accept) {
        for(UnoButton b : buttons) {
            b.setAcceptingInput(accept);
        } 
    }
    
    protected UnoButton addButton(String text, ComponentListener listener, int x, int y) {           
        UnoButton button = new UnoButton(this.gc, btnImage, x, y, text, listener);
        button.setMouseOverImage(btnOverImage);
        button.setAcceptingInput(false);
        buttons.add(button);
        return button;
    }
    
    public class AddCard implements ComponentListener {
        public void componentActivated(AbstractComponent ac) {
            Card c = MainClass.player.drawCard();
            System.out.println("aggiunta carta "+c.getColor()+" "+c.getEffect());
            System.out.println(MainClass.player.getMyCards().size());
        }
    }
    
    public class DiscardCard implements ComponentListener {
        public void componentActivated(AbstractComponent ac) {
            MainClass.player.discardCard(MainClass.player.getMyCards().get(0));
            System.out.println("Rimossa la prima carta");
        }
    }
    
}
