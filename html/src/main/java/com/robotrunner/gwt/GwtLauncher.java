package com.robotrunner.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.preloader.Preloader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Panel;
import com.robotrunner.States.GameStateManager;

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
    @Override
    public GwtApplicationConfiguration getConfig () {
        // Resizable application, uses available space in browser with no padding:
        GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(true);
        cfg.padVertical = 150;
        cfg.padHorizontal = 100;
        return cfg;
        // If you want a fixed size application, comment out the above resizable section,
        // and uncomment below:
        //return new GwtApplicationConfiguration(640, 480);
    }

    @Override
    public ApplicationListener createApplicationListener () {
            return new GameStateManager();
        }

    @Override
    public Preloader.PreloaderCallback getPreloaderCallback() {
        return createPreloaderPanel(GWT.getHostPageBaseURL() + "black pixel.png");
    }

    @Override
    protected void adjustMeterPanel(Panel meterPanel, Style meterStyle) {
        meterPanel.setStyleName("gdx-meter");
        meterPanel.addStyleName("nostripes");
        meterStyle.setProperty("backgroundColor", "#ffffff");
        meterStyle.setProperty("backgroundImage", "none");
    }

}
