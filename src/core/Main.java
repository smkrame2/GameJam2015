package core;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;

import processing.core.PApplet;
import processing.core.PImage;
import sound.SoundPlayer;
import core.level.Loader;
import core.level.blocks.Block;

public class Main extends PApplet {

	private static final long serialVersionUID = 1L;

	public static void main(String args[]) {
		PApplet.main(new String[] {"core.Main" });
	}
	
	public Main() {}
	
	private ArrayList<ArrayList<Block>> data = null;
	
	public Main(ArrayList<ArrayList<Block>> data, int bgType) {
		this.data = data;
		this.testBgColor = bgType;
	}
	
	ArrayList<GameWorld> worlds;
	
	GameWorld currWorld;
	SoundPlayer soundPlayer;
	
	float worldChangeDelay = 0;
	float worldChangeTimer = 60 * 10;
	
	int testBgColor;
	
	public long lastTime;
	
	private Random randGen;
	
	public static PImage spriteL, spriteR, bunnyL, bunnyR;

	public void setup() {
		spriteR = loadImage("res/img/upr.png");
		spriteL = loadImage("res/img/upl.png");
		bunnyR = loadImage("res/img/bunnyr.png");
		bunnyL = loadImage("res/img/bunnyl.png");
		
		randGen = new Random();
		
		float scale = .7f;
		size((int) (displayWidth * scale), (int) (displayHeight * scale), P2D);
		if (frame != null) {
			frame.setIconImage(new ImageIcon("res/img/ggj.png").getImage());
			frame.setTitle("GameJam!");
			frame.setResizable(true);
		}
		colorMode(RGB, 255);
		frameRate(60);
		smooth();
		this.rectMode(CENTER);
		
		soundPlayer = new SoundPlayer(this);
		
		//Test mode
		worlds = new ArrayList<GameWorld>();

		if (data != null) {
			worlds.add(Loader.load(data, soundPlayer, testBgColor));
		} else {
//			for (int i = 0; i < 100; i++) {
//				if (!new File(Loader.SAVES + "level" + i + ".lvl").exists()) continue;
//				System.out.println("Loading level " + i);
//				worlds.add(Loader.load("level" + i, soundPlayer));
//			}
			
			File levelDir = new File(Loader.SAVES);
			File[] levels = levelDir.listFiles();
			
			for (File level : levels) {
				System.out.println("Loading level " + level.getName());
				worlds.add(Loader.load(level.getName(), soundPlayer));
			}
		}
		
		currWorld = worlds.get(0);
		
		switch (currWorld.level.getBgType()) {
		case 0:
			soundPlayer.playField();
			break;
		case 1:
			soundPlayer.playSpace();
			break;
		}	
		
		/*
		for (int i = 0; i < 5; i++)
			worlds.get(0).entities.add(new TurtleEnemy(new Vec2(500 + i*100, 100), worlds.get(0).world));
		for (int i = 5; i < 20; i++)
			worlds.get(0).entities.add(new BunnyEnemy(new Vec2(500 + i*100, 100), worlds.get(0).world, (Math.random() < .5) ? worlds.get(0).player.playerBody : null));
		*/
		
		lastTime = this.millis();
	}


	public void draw() {
		float delta = getNewDelta();
		clear();
		//g.scale(.5f);
		background(0, 216, 216);
		
		if (worldChangeDelay <= 0) {
			worldChangeDelay = 0;
			currWorld.update(delta);
		} else {
			worldChangeDelay -= delta;
		}
		
		if (worldChangeTimer <= 0 && data == null) {
			int newWorld = randGen.nextInt(worlds.size());
			changeWorld(newWorld);
			worldChangeTimer = 60 * 10;
		} else {
			worldChangeTimer -= delta;
		}
		
		currWorld.draw(g);
		
		if(currWorld.player.dead) {
			int i = worlds.indexOf(currWorld);
			currWorld = Loader.load("level" + (i+1), soundPlayer);
			worlds.set(i, currWorld);
		}
	}
	
	public float getNewDelta() {
		long newTime = System.nanoTime();
		long diff = newTime - lastTime;
		lastTime = newTime;
		return (float) (diff/16000000.0);
	}
	
	
	public void changeWorld(int worldId) {
		currWorld.playerLeft = false;
		currWorld.playerRight = false;
		currWorld.playerJump = false;
		
		soundPlayer.play("static");
		
		currWorld = worlds.get(worldId);
		
		switch(currWorld.level.getBgType()) {
			case 0: 
				soundPlayer.playField();
			break;
			case 1: 
				soundPlayer.playSpace();
			break;
		}
		
		worldChangeDelay = 30;
	}
	
	
	public void keyReleased() {
		if (key == 'a') {
			currWorld.playerLeft = false;
		}
		if (key == 'd') {
			currWorld.playerRight = false;
		}
		if (key == 'w') {
			currWorld.playerJump = false;
		}
		if (key == 'j') {
			currWorld.playerShoot = false;
		}
		
		
		if (key == 'l') {
			changeWorld(0);
		}
		if (key == ';') {
			changeWorld(1);
		}
	}
	
	public void keyPressed() {
		if (key == 'a') {
			currWorld.playerLeft = true;
		}
		if (key == 'd') {
			currWorld.playerRight = true;
		}
		if (key == 'w') {
			currWorld.playerJump = true;
		}
		if (key == 'j') {
			currWorld.playerShoot = true;
		}
	}
}
