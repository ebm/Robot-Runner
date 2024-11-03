package com.tbd.game;

public abstract class State {
    public GameStateManager gsm;
    public State(GameStateManager gsm) {
        this.gsm = gsm;
    }
    public abstract void render();
    public abstract void dispose();
}
