//package com.fallenascendants;
//
//import com.badlogic.gdx.ApplicationAdapter;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.GL20;
//
//public class TestNewGame extends ApplicationAdapter {
//
//    private int counter = 0;
//
//    @Override
//    public void create() {
//
//        System.out.println("Vendor  : " + Gdx.gl.glGetString(GL20.GL_VENDOR));
//        System.out.println("Renderer: " + Gdx.gl.glGetString(GL20.GL_RENDERER));
//        System.out.println("Version : " + Gdx.gl.glGetString(GL20.GL_VERSION));
//    }
//    }
//
//    @Override
//    public void render() {
//
//        if(counter++ % 300 == 0){
//            System.out.println("RENDERING...");
//        }
//
//        Gdx.gl.glClearColor(1,0,0,1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//    }
//}
