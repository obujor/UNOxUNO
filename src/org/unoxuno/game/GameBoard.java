/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unoxuno.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
import org.unoxuno.communication.Card;
import org.unoxuno.communication.GameState;
import static org.unoxuno.game.MainMenu.trueTypeFont;

/**
 *
 * @author tavy
 */
public class GameBoard extends BasicGameState {
    
    Image gameBG, btnImage, btnOverImage, unoDeck, unoCard, rotateRight, rotateLeft;
    int state, centerX = MainClass.width/2, 
        centerY = MainClass.height/2, cardW = 73, cardH=109,
        maxWidth = MainClass.width-100, playersCardW = cardW/2, 
        playersCardH = cardH/2;
    long systemMills = 0;
    GameContainer gc;
    StateBasedGame sbg;
    private final Map<String,Image> cardImages;
    private final int[][] playersPosX = new int[][] {{centerX},{20, centerX},{20, centerX,MainClass.width-50}};
    private final int[][] playersPosY = new int[][] {{20}, {centerY, 20},{centerY, 20, centerY}};
    ArrayList<UnoButton> buttons = new ArrayList<UnoButton>();
    ArrayList<UnoCardButton> cardButtons = new ArrayList<UnoCardButton>();
    TrueTypeFont txtFontSmall, txtCardNr;
    Color playersTxtColor = new Color(0,0,0);
    Color activePlayerTxtColor = new Color(0,180,0);
    String playerPenality = "";
    private final Lock lock = new ReentrantLock();
    private final Lock lockButtons = new ReentrantLock();
    
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
        txtCardNr  = new TrueTypeFont(trueTypeFont.deriveFont(30f), true);
        initImages();
        addButtons();
    }
    
    private void addButtons() {
        addButton("Pesca", new GetCard(), centerX-300, MainClass.height-120);
        addButton("Passa", new Pass(), centerX-100, MainClass.height-120);
        addButton("UNO!", new SayUno(), centerX+100, MainClass.height-120);
    }
    
    public void initImages() throws SlickException {
        gameBG = new Image("res/images/game_background.jpg").getScaledCopy(MainClass.width, MainClass.height);
        btnImage = new Image("res/images/btnBg.png");
        btnOverImage = new Image("res/images/btnOver.png");
        unoDeck = new Image("res/images/uno_deck.png").getScaledCopy(106, 154);
        unoCard = new Image("res/images/uno.png").getScaledCopy(playersCardW, playersCardH);
        rotateRight = new Image("res/images/rotate-right.png").getScaledCopy(50, 50);
        rotateLeft = new Image("res/images/rotate-left.png").getScaledCopy(50, 50);
    }

    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        g.drawImage(gameBG, 0, 0);
        GameState gState = MainClass.player.getState();

        if (gState.isGameFinished()) {
            String winner = gState.whoIsTheWinner();
            String text;
            if (winner.equals(MainClass.player.getNickname())) {
                text = "You are the winner!!!";
                txtCardNr.drawString(centerX-txtCardNr.getWidth(text)/2, centerY, text, activePlayerTxtColor);
            } else {
                text = "The winner is "+winner+"! Game over!";
                txtCardNr.drawString(centerX-txtCardNr.getWidth(text)/2, centerY, text, Color.red);
            }
        } else {
            drawMainCard(g);
            drawPlayers(g);
            if (gState.getSense()) {
                g.drawImage(rotateRight, MainClass.width-60, 10);
            } else {
                g.drawImage(rotateLeft, MainClass.width-60, 10);
            }
            if (isMyTurn()) {
                String penality = MainClass.player.checkPenality();
                if (!penality.isEmpty()) {
                    systemMills = System.currentTimeMillis();
                    playerPenality = penality;
                    System.out.println(">> Penalita': "+playerPenality);
                }
                for(UnoButton b : buttons) {
                    b.render(gc, g);
                }
            }
            
            lockButtons.lock();
            for(UnoCardButton b : cardButtons) {
                b.render(gc, g);
            }
            lockButtons.unlock();

            if (!playerPenality.isEmpty() && System.currentTimeMillis()-systemMills < 5000) {
                txtFontSmall.drawString(centerX-100, MainClass.height-110, playerPenality, Color.red);
            }
        }
    }
    
    private void drawMainCard(Graphics g) throws SlickException {
        Card c = MainClass.player.getState().getLastDiscardedCard();
        int w = cardW+cardW/3;
        int h = cardH+cardH/3;
        g.drawImage(getImage(c).getScaledCopy(w, h), centerX-w/2, centerY-h/2);
        g.drawImage(unoDeck, centerX-w/2-120, centerY-h/2-10);
    }
    
    private void setUserCards() {
        ArrayList<Card> myCards = MainClass.player.getMyCards();
        if (myCards.isEmpty()) return;

        Map<String,Integer> cardsCounter = new HashMap<String,Integer>();
        for(int i=0; i<myCards.size(); i++) {
            String uri = myCards.get(i).getUri();
            int counter = cardsCounter.containsKey(uri) ? cardsCounter.get(uri) : 0;
            cardsCounter.put(uri, counter+1);
        }
        
        int margin = maxWidth/cardsCounter.size()-cardW;
        margin = margin > 10 ? 10 : margin;
        int width = margin+cardW;
        int totalWidth = cardsCounter.size()*width-margin;
        int initWidth = centerX-(totalWidth/2);
        int y = MainClass.height-cardH/2;
        boolean myTurn = isMyTurn();
        
        lockButtons.lock();
        cardButtons.clear();
        lockButtons.unlock();
        
        try {
            int index = 0;
            for (Card c: myCards) {
                int counter = cardsCounter.get(c.getUri());
                if (counter != 0) {
                    addCardButton(getImage(c), new CardClick(c), initWidth+index*width, y, myTurn, counter);
                    cardsCounter.put(c.getUri(), 0);
                    index++;
                }
            }
        } catch (SlickException ex) {
            Logger.getLogger(GameBoard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void drawPlayers(Graphics g) throws SlickException {
        GameState state = MainClass.player.getState();
        ArrayList<String> users = state.getUsernames();
        int myPos = users.indexOf(MainClass.player.getNickname());
        String userTurn = state.getUserActualTurn();
        int playerPos = 0;
        int playerPosInArray = users.size()-2;
        for(int i=(myPos+1)%users.size(); i!=myPos; i=(i+1)%users.size()) {
            int cardsNr = state.getHand(users.get(i)).size();
            int txtMidle = this.txtFontSmall.getWidth(users.get(i))/2;
            int cardX = playersPosX[playerPosInArray][playerPos];
            int textX = cardX-txtMidle+playersCardW/2;
            textX = textX >=0 ? textX : 0;
            txtCardNr.drawString(cardX+playersCardW, playersPosY[playerPosInArray][playerPos]+10, Integer.toString(cardsNr), playersTxtColor);
            Color c = (userTurn.equals(users.get(i))) ? activePlayerTxtColor : playersTxtColor;
            txtFontSmall.drawString(textX, playersPosY[playerPosInArray][playerPos]-20, users.get(i), c);
            g.drawImage(unoCard, cardX, playersPosY[playerPosInArray][playerPos]);
            playerPos++;
        }
    }
    
    private Image getImage(Card c) throws SlickException {
        Image img;
        lock.lock();
        try {
            String uri = c.getUri();
            if (cardImages.containsKey(uri))
                return cardImages.get(uri);
            img = new Image(uri).getScaledCopy(cardW, cardH);
            cardImages.put(uri, img);
        } finally {
            lock.unlock();
        }
        return img;
    }
    
    public void update(GameContainer gc, StateBasedGame sbg, int i) throws SlickException {
    }
    
    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
        super.enter(gc, sbg);
        System.out.println("Gioco iniziato!");
        setButtonsInputAccepted(true);
        StateChanged lst = new StateChanged();
        MainClass.player.setStateChangeListener(lst);
        lst.activate();
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
    
    protected UnoCardButton addCardButton(Image im, ComponentListener listener, int x, int y, boolean accept, int counter) {
        UnoCardButton button = new UnoCardButton(this.gc, im, x, y, listener);
        button.setAcceptingInput(accept);
        if (counter > 1)
            button.setText(Integer.toString(counter));
        lockButtons.lock();
        cardButtons.add(button);
        lockButtons.unlock();
        return button;
    }
    
    private boolean isMyTurn() {
        return MainClass.player.isMyTurn();
    }
    
    public class GetCard implements ComponentListener {
        public void componentActivated(AbstractComponent ac) {
            if (!isMyTurn()) return;
            Card c = MainClass.player.drawCard();
            System.out.println("Pescata carta "+c.getColor()+" "+c.getEffect());
        }
    }
    
    public class Pass implements ComponentListener {
        public void componentActivated(AbstractComponent ac) {
            if (!isMyTurn()) return;
            System.out.println("Pass turn "+Long.toString(System.currentTimeMillis()));
            MainClass.player.passTurn();
        }
    }
    
    public class SayUno implements ComponentListener {
        public void componentActivated(AbstractComponent ac) {
            if (!isMyTurn()) return;
            System.out.println("UNO!!!");
            MainClass.player.sayUNO();
        }
    }
    
    public class CardClick implements ComponentListener {
        Card card;
        public CardClick(Card c) {
            card = c;
        }
        public void componentActivated(AbstractComponent ac) {
            if (!isMyTurn()) return;
            System.out.println("Clicked "+card.getColor()+" "+card.getEffect());
            if (MainClass.player.discardable(card))
                if (MainClass.player.discardCard(card)) {
                    systemMills = System.currentTimeMillis();
                    playerPenality = "Non hai detto UNO! Hai preso due carte!";
                }
        }
    }
    
    public class StateChanged implements ComponentListener {
        public void componentActivated(AbstractComponent ac) {}
        public void activate(){
            setUserCards();
        }
    }
    
}
