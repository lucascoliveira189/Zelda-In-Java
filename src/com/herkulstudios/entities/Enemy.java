package com.herkulstudios.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.herkulstudios.main.Game;
import com.herkulstudios.main.Sound;
import com.herkulstudios.world.AStar;
import com.herkulstudios.world.Camera;
import com.herkulstudios.world.Vector2i;

public class Enemy extends Entity {
	
	private double speed = 1;
	
	//private int maskx = 8, masky = 8, maskw = 10, maskh = 10;
	private int frames = 0, maxFrames = 20;
	private int index = 0, maxIndex = 1;
	private int life = 2;
	private int damagedFrames = 9, damageCurrent = 0;
	
	private boolean isDamaged = false;
	
	private BufferedImage[] sprites;
	

	

	public Enemy(int x, int y, int z, int width, int height, BufferedImage sprite) {
		super(x, y, z, width, height, null);
		sprites = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(112, 16, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(112 + 16, 16, 16, 16);
	}
	
	public void update() {
		maskX = 5;
		maskY = 5;
		maskWidth = 8;
		maskHeight = 8;
		depth = 0;

		/*          Primeiro Algoritmo do inimigo buscar ir ao encontro do Player - Extremamente simples, sendo apenas if e else e incrementando o x e y
		 * 
		 * #####################################################################################################################################################
		if(this.calculateDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY()) < 75) {
			//if( Game.rand.nextInt(100) < 50) FOR RANDOMIZE ENEMY's MOVE
			if(!this.isCollidingWithPlayer()) {
				
				if ((int)x < Game.player.getX() && World.isFree((int)(x + speed), this.getY()) && !isColliding((int)(x + speed), this.getY())) {
					x += speed;
				}
				else if ((int)x > Game.player.getX() && World.isFree((int)(x - speed), this.getY()) && !isColliding((int)(x - speed), this.getY())) {
					x -= speed;
				}
				
				if ((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y + speed)) && !isColliding(this.getX(), (int)(y + speed))) {
					y += speed;
				}
				else if ((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y - speed)) && !isColliding(this.getX(), (int)(y - speed))) {
					y -= speed;
				} 
			}
			else {
				

				
				if(Game.rand.nextInt(100) < 10) {
					Sound.hurtEffect.play();
					Game.player.isDamaged = true;
					Game.player.life-= Game.rand.nextInt(2);
				}


			}
		} 
		#######################################################################################################################################################################
		*/
		
		if(!this.isCollidingWithPlayer()) {
			
			if(path == null || path.size() == 0) {
				
				Vector2i start = new Vector2i((int)(x / 16) , (int)(y / 16));
				Vector2i end = new Vector2i((int)(Game.player.x / 16) , (int)(Game.player.y / 16));
				
				path = AStar.findPath(Game.world, start, end);
			}
			
		}
		else {
			
			if(Game.rand.nextInt(100) < 10) {
				Sound.hurtEffect.play();
				Game.player.isDamaged = true;
				Game.player.life-= Game.rand.nextInt(2);
			}
			
		}
		
		

		if(new Random().nextInt(100) < 90)
			followPath(path);
		
		if(new Random().nextInt(100) < 5) {
			
			Vector2i start = new Vector2i((int)(x / 16) , (int)(y / 16));
			Vector2i end = new Vector2i((int)(Game.player.x / 16) , (int)(Game.player.y / 16));
			
			path = AStar.findPath(Game.world, start, end);
			
		}
		
		frames++;
		
		if(frames == maxFrames) {
			
			frames = 0;
			index++;
			
			if (index > maxIndex) {
				
				index = 0;
			}
		}
		
		CollidingWithBullet();
		CheckAndIfTrueDEATH();
		CheckLifeForAnimation();
		
	}
	
	public void CheckLifeForAnimation() {
		
		if(isDamaged) {
			this.damageCurrent++;
			
			if(this.damageCurrent == this.damagedFrames) {
				this.damageCurrent = 0;
				this.isDamaged = false;
			}
		}
	}
	
	public void CheckAndIfTrueDEATH() {
		if(life <= 0) {
			Game.enemies.remove(this);
			Game.entities.remove(this);
			return;
		}
	}
	
	public void CollidingWithBullet() {
		
		for(int i = 0; i < Game.bullets.size(); i++) {
			
			Entity e = Game.bullets.get(i);
			
			if(e instanceof BulletShoot) {
				
				if(Entity.isColliding(this, e)) {
					isDamaged = true;
					life--;
					Game.bullets.remove(i);
					return;
				}
				
			}
		}
		
	
	}
	
	public boolean isCollidingWithPlayer() {
		
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskX, this.getY() + maskY, maskWidth, maskHeight);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);
		return enemyCurrent.intersects(player);
	}
	

	
	public void render(Graphics g) {
		
		if(!isDamaged)
			g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		else
			g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);
		g.setColor(Color.blue);
		g.fillRect(this.getX() - Camera.x + maskX, this.getY() + maskY - Camera.y, maskWidth, maskHeight);
	}
}
