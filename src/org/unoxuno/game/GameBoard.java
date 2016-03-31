/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unoxuno.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.unoxuno.communication.Card;

/**
 *
 * @author tavy
 */
public class GameBoard extends BasicGameState {
    
    Image gameBG, btnImage, btnOverImage, unoDeck;
    int state, centerX = MainClass.width/2, 
        centerY = MainClass.height/2, cardW = 73, cardH=109,
        maxWidth = MainClass.width-100;
    GameContainer gc;
    StateBasedGame sbg;
    private final Map<String,Image> cardImages;
    
    ArrayList<UnoButton> buttons = new ArrayList<UnoButton>();
    
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
        initImages();
        addButtons();
    }
    
    private void addButtons() {
        addButton("Add card", new AddCard(), centerX-100, centerY-100);
        addButton("Remove card", new DiscardCard(), centerX+100, centerY-100);
    }
    
    public void initImages() throws SlickException {
        gameBG = new Image("res/images/game_background.jpg").getScaledCopy(MainClass.width, MainClass.height);
        btnImage = new Image("res/images/btnBg.png");
        btnOverImage = new Image("res/images/btnOver.png");
        unoDeck = new Image("res/images/uno_deck.png").getScaledCopy(106, 154);
    }

    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        g.drawImage(gameBG, 0, 0);
        drawMainCard(g);
        drawMyCards(g);
        for(UnoButton b : buttons) {
            b.render(gc, g);
        }
    }
    
    private void drawMainCard(Graphics g) throws SlickException {
        Card c = MainClass.player.getState().getLastDiscardedCard();
        int w = cardW+cardW/3;
        int h = cardH+cardH/3;
        g.drawImage(new Image(c.getUri()).getScaledCopy(w, h), centerX-w/2, centerY-h/2);
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
            g.drawImage(getImage(c), initWidth+i*width, MainClass.height-cardH);
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
