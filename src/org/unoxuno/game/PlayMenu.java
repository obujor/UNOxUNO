package org.unoxuno.game;

public class PlayMenu extends MainMenu {

    public PlayMenu(int state) {
        super(state);
    }
    
    @Override
    public void addButtons() {
        super.addButton("Join room", new EnterState(MainClass.joinRoom));
        super.addButton("Create room", new EnterState(MainClass.createRoom));
        super.addButton("Back", new GoToMainListener());
    }
}
