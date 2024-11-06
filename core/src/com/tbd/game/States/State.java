package com.tbd.game.States;

public abstract class State {
    public GameStateManager gsm;
    public State(GameStateManager gsm) {
        this.gsm = gsm;
    }
    public abstract void render();
    public abstract void dispose();
}
