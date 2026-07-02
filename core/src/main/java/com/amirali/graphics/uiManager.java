package com.amirali.graphics;

import com.amirali.graphics.views.screens.AbstractScreen;
import com.badlogic.gdx.Screen;

public class uiManager {
    private static Main main;

    public static void init(Main main){
        uiManager.main = main;
    }

    public static void setScreen(Screen screen){
        main.setScreen(screen);
    }

    public static AbstractScreen getScreen(){
        if(main.getScreen() instanceof AbstractScreen abstractScreen){
            return abstractScreen;
        } else {
            return null;
        }
    }
}
