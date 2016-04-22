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
import org.unoxuno.utilities.GameStrings;

/**
 *
 * @author tavy
 */
public class GameBoard extends BasicGameState {
    
    Image gameBG, btnImage, btnOverImage, unoDeck, unoCard, rotateRight, rotateLeft;
    int state, centerX = MainClass.width/2, 
        centerY = MainClass.height/2, cardW = 73, cardH=109,
        maxWidth = MainClass.width-100, playersCardW = cardW/2, 
        playersCardH = cardH/2, myPosInUsers = 0, 
        mainCardW  = cardW+cardW/3, mainCardH = cardH+cardH/3;
    long systemMills = 0;
    GameContainer gc;
    StateBasedGame sbg;
    private final Map<String,Image> cardImages;
    private final int[][] playersPosX = new int[][] {{centerX},{20, centerX},{20, centerX,MainClass.width-80},
                                                    {20, centerX-150,centerX+150,MainClass.width-80},
                                                    {20, centerX-150, centerX, centerX+150,MainClass.width-80},
                                                    {20, 20, centerX-150, centerX, centerX+150,MainClass.width-80},
                                                    {20, 20, centerX-150, centerX, centerX+150,MainClass.width-80, MainClass.width-80},
                                                    {20, 20, centerX-200, centerX-80, centerX+80, centerX+200,MainClass.width-80, MainClass.width-80}};
    private final int[][] playersPosY = new int[][] {{20}, {centerY, 20},{centerY, 20, centerY},
                                                    {centerY, 20, 20, centerY},
                                                    {centerY, 20, 20, 20, centerY},
                                                    {centerY+50,centerY-50, 20, 20, 20, centerY},
                                                    {centerY+50,centerY-50, 20, 20, 20, centerY-50, centerY+50},
                                                    {centerY+50,centerY-50, 20, 20, 20, 20, centerY-50, centerY+50}};
    ArrayList<UnoButton> buttons = new ArrayList<UnoButton>();
    ArrayList<UnoCardButton> cardButtons = new ArrayList<UnoCardButton>();
    TrueTypeFont txtFontSmall, txtCardNr;
    Color playersTxtColor = new Color(0,0,0);
    Color activePlayerTxtColor = new Color(0,180,0);
    String playerPenality = "", userTurn = "", myNickname = "";
    private final Lock lock = new ReentrantLock();
    private final Lock lockButtons = new ReentrantLock();
    Card selectedColorCard, lastDiscardedCard;
    GameState gState;
    ArrayList<String> users;
    ArrayList<Integer> usersCardsNr;
    ArrayList<Card> playerCards;
    boolean gameSenseClockwise = true, gameFinished = false, isMyTurn = false,
            needSetUserCards = false;
    
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
        addButton("Pesca", new GetCard(), centerX-300, MainClass.height-140);
        addButton("Passa", new Pass(), centerX-100, MainClass.height-140);
        addButton("UNO!", new SayUno(), centerX+100, MainClass.height-140);
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
        if (gameFinished) {
            showTheWinner();
            return;
        }
        
        if (MainClass.player.isStateChanged()) {
            System.out.println("State changed "+Long.toString(System.currentTimeMillis()));
            updateStateVariables();
            setUserCards();
            if (isMyTurn) {
                MainClass.player.checkAllUsersState();
            }
        }
        
        if (needSetUserCards) {
            setUserCards();
            needSetUserCards = false;
        }
        
        drawMainCard(g);
        drawPlayers(g);
        drawButtons(g);
        drawPlayerCards(g);
        drawGameSense(g);
        drawPenalityMsg();
    }
    
    private void drawButtons(Graphics g) {
        if (isMyTurn) {
            for(UnoButton b : buttons) {
                b.render(gc, g);
            }
        }
    }
    
    private void drawPlayerCards(Graphics g) {
        lockButtons.lock();
        for(UnoCardButton b : cardButtons) {
            b.render(gc, g);
        }
        lockButtons.unlock();
    }
    
    private void showTheWinner() {
        String winner = gState.whoIsTheWinner();
        String text;
        if (winner.equals(myNickname)) {
            text = "You are the winner!!!";
            txtCardNr.drawString(centerX-txtCardNr.getWidth(text)/2, centerY, text, activePlayerTxtColor);
        } else {
            text = "The winner is "+winner+"! Game over!";
            txtCardNr.drawString(centerX-txtCardNr.getWidth(text)/2, centerY, text, Color.red);
        }
    }
    
    private void drawGameSense(Graphics g) {
        if (gameSenseClockwise) {
            g.drawImage(rotateRight, MainClass.width-60, 10);
        } else {
            g.drawImage(rotateLeft, MainClass.width-60, 10);
        }
    }
    
    private void drawPenalityMsg() {
        if (!playerPenality.isEmpty() && System.currentTimeMillis()-systemMills < 5000) {
            txtFontSmall.drawString(centerX-100, MainClass.height-90, playerPenality, Color.red);
        }
    }
    
    private void drawMainCard(Graphics g) throws SlickException {
        if (lastDiscardedCard == null) return;
        g.drawImage(getImage(lastDiscardedCard).getScaledCopy(mainCardW, mainCardH), centerX-mainCardW/2, centerY-mainCardH/2);
        g.drawImage(unoDeck, centerX-mainCardW/2-120, centerY-mainCardH/2-10);
    }
    
    private void setUserCards() {
        ArrayList<Card> myCards;
        boolean colorSelection = false;
        if (selectedColorCard != null) {
            myCards = new ArrayList<Card>();
            for ( String color : GameStrings.colors) {
                myCards.add(new Card(color, selectedColorCard.getEffect()));
            }
            colorSelection = true;
        } else 
            myCards = playerCards;
        
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
        
        lockButtons.lock();
        cardButtons.clear();
        lockButtons.unlock();
        
        try {
            int index = 0;
            for (Card c: myCards) {
                int counter = cardsCounter.get(c.getUri());
                if (counter != 0) {
                    addCardButton(getImage(c), new CardClick(c, colorSelection), initWidth+index*width, y, isMyTurn, counter);
                    cardsCounter.put(c.getUri(), 0);
                    index++;
                }
            }
        } catch (SlickException ex) {
            Logger.getLogger(GameBoard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void updateStateVariables() {
        System.out.println(">> Entrato in update");
        MainClass.player.lockCards.lock();
        MainClass.player.lockUsers.lock();
        
        isMyTurn = MainClass.player.isMyTurn();
        if (isMyTurn) {
            String penality = MainClass.player.checkPenality();
            if (!penality.isEmpty()) {
                setErrorMessage(penality);
                System.out.println(">> Penalita': "+playerPenality);
            }
        }
        isMyTurn = MainClass.player.isMyTurn();
        lastDiscardedCard = MainClass.player.getState().getLastDiscardedCard();
        gState = MainClass.player.getState();
        userTurn = gState.getUserActualTurn();
        users = gState.getUsernames();
        usersCardsNr = new ArrayList<Integer>();
        for(String user : users) {
            usersCardsNr.add(gState.getHand(user).size());
        }
        myNickname = MainClass.player.getNickname();
        myPosInUsers = users.indexOf(myNickname);
        gameSenseClockwise = gState.getSense();
        gameFinished = gState.isGameFinished();
        playerCards = (ArrayList<Card>)MainClass.player.getMyCards().clone();
        MainClass.player.lockUsers.unlock();
        MainClass.player.lockCards.unlock();
        System.out.println(">>UScito in update");
    }
    
    private void drawPlayers(Graphics g) throws SlickException {
        int playerPos = 0;
        int playerPosInArray = users.size()-2;
        for(int i=(myPosInUsers+1)%users.size(); i!=myPosInUsers; i=(i+1)%users.size()) {
            int cardsNr = usersCardsNr.get(i);
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
    
    private void setErrorMessage(String msg) {
        systemMills = System.currentTimeMillis();
        playerPenality = msg;
    }
    
    public class GetCard implements ComponentListener {
        public void componentActivated(AbstractComponent ac) {
            if (!isMyTurn()) return;
            if (MainClass.player.canDraw()) {
                Card c = MainClass.player.drawCard();
                System.out.println("Pescata carta "+c.getColor()+" "+c.getEffect());
            } else {
                setErrorMessage("Hai gia' pescato una carta!");
            }
        }
    }
    
    public class Pass implements ComponentListener {
        public void componentActivated(AbstractComponent ac) {
            if (!isMyTurn()) return;
            if (MainClass.player.canDraw()) {
                setErrorMessage("Devi pescare una carta prima!");
            } else {
                MainClass.player.passTurn();
            }
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
        boolean colorSelection;
        String noUNO = "Non hai detto UNO! Hai preso due carte!";
        public CardClick(Card c, boolean cs) {
            card = c;
            colorSelection = cs;
        }
        public void componentActivated(AbstractComponent ac) {
            if (!isMyTurn()) return;
            System.out.println("Clicked "+card.getColor()+" "+card.getEffect());
            if (colorSelection) {
                if (MainClass.player.discardJollyCard(selectedColorCard, card.getColor())) {
                    setErrorMessage(noUNO);
                }
                selectedColorCard = null;
                return;
            }
            if (MainClass.player.discardable(card) && card.isJollyCard()) {
                selectedColorCard = card;
                needSetUserCards = true;
                //setUserCards();
                return;
            }
            if (MainClass.player.discardable(card))
                if (MainClass.player.discardCard(card)) {
                    setErrorMessage(noUNO);
                }
        }
    }
    
    public class StateChanged implements ComponentListener {
        public void componentActivated(AbstractComponent ac) {}
        public void activate(){
            updateStateVariables();
            setUserCards();
        }
    }
    
}
