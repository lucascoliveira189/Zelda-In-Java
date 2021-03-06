package com.herkulstudios.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.herkulstudios.graficos.Spritesheet;
import com.herkulstudios.main.Game;
import com.herkulstudios.world.Camera;
import com.herkulstudios.world.World;

public class Player extends Entity{
	
	public boolean jump = false, isJumping = false;
	public boolean jumpUp = false, jumpDown = false;
	public boolean left, right, down, up;
	public boolean isDamaged = false;
	public boolean shoot = false, mouseShoot = false;
	public double speed = 1.4;
	
	public int ammo;
	public int right_dir = 0, left_dir = 1;
	public int dir = right_dir; 
	public int mouseX, mouseY;
	public int jumpFrames = 50, jumpCurrent = 0;
	
	
	public double life = 100, maxLife= 100;
	
	private int frames = 0, maxFrames = 5;
	private int index = 0, maxIndex = 3;
	private int damageFrames = 0;

	private boolean hasGun = false;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	private BufferedImage playerDamage;
	

	


	public Player(int x, int y, int z, int width, int height, BufferedImage sprite) {
		super(x, y, z, width, height, sprite);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		playerDamage = Game.spritesheet.getSprite(0, 16, 16, 16);
		
		for(int i = 0; i < 4; i++) {
			
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 0, 16, 16);
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 16, 16, 16);
			
		}
		
		

	}
	
	public void update() {
		
		depth = 1;
		
		CheckDeath();
		move();
		checkCollisionWithLifePack();
		checkCollisionWithAmmo();
		checkCollisionWithGun();
		cameraOffset();
		shoot();
		
		if(jump) {
			if(!isJumping) {
				jump = false;
				isJumping = true;
				jumpUp = true;
			}
		}
		
		if(isJumping) {

			
			if(jumpUp) {
				jumpCurrent += 4;
			}
			else if(jumpDown) {
				jumpCurrent -= 2;
				
				if(jumpCurrent <= 0) {
					isJumping = false;
					jumpDown = false;
					jumpUp = false;
				}
				
			}

			
			this.setZ(jumpCurrent);
			
			if( jumpCurrent >= jumpFrames) {
				jumpUp = false;
				jumpDown = true;
			}
			
		}
		

	}
	
	public void render(Graphics g) {
		
		renderAnimation(g);
		
		
	}
	
	private void cameraOffset() {
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH / 2), 0, World.WIDTH * 16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT / 2), 0, World.HEIGHT * 16 - Game.HEIGHT);
	}
	
	private void CheckDeath() {
		
		if(Game.player.life <= 0) {
			life = 0;
			Game.gameState = "GAME_OVER";
		}
		
	}
	
	private void shoot() {
		
		if(shoot && hasGun && ammo > 0) {
			ammo--;
			shoot = false;
			
			int dx = 0;
			int px = 0;
			int py = 8;
			
			if(dir == right_dir) {
				dx = 1;
				px = 18;
			}
			else {
				dx = -1;
				px = -8;
			}
			
			BulletShoot bullet = new BulletShoot(this.getX() + px, this.getY() + py, 0, 3, 3, null, dx, 0);
			Game.bullets.add(bullet);
		}
		
		if(mouseShoot && hasGun && ammo > 0) {
			
			ammo--;
			mouseShoot = false;
				
			int px = 0;
			int py = 8;
			double angle = 0;
			
			if(dir == right_dir) {
				px = 18;
				angle = Math.atan2(mouseY - (this.getY() + py - Camera.y), mouseX - (this.getX() + px - Camera.x));
			}
			else {
				px = -8;
				angle = Math.atan2(mouseY - (this.getY() + py - Camera.y), mouseX - (this.getX() + px - Camera.x));
			}
			
			double dx = Math.cos(angle);
			double dy = Math.sin(angle);
						
			BulletShoot bullet = new BulletShoot(this.getX() + px, this.getY() + py, 0, 3, 3, null, dx, dy);
			Game.bullets.add(bullet);
		}
		
	}
	
	private void move() {
		
		moved = false;
		
		if(right && World.isFree((int)(x + speed), this.getY())) {
			moved = true;
			dir = right_dir;
			x += speed;
		}
		else if(left && World.isFree((int)(x - speed), this.getY())) {
			moved = true;
			dir = left_dir;
			x -= speed;
		}
		
		if(down && World.isFree(this.getX(), (int)(y + speed))) {
			moved = true;
			y += speed;
		}
		else if (up && World.isFree(this.getX(), (int)(y - speed))) {
			moved = true;
			y -= speed;
		}
		
		updateAnimation();
		

	}
	
	private void updateAnimation() {
		
		if(moved) {
			
			frames++;
			
			if(frames == maxFrames) {
				
				frames = 0;
				index++;
				
				if (index > maxIndex) {
					
					index = 0;
				}
			}
		}
		
		if(isDamaged) {
			this.damageFrames++;
			if(this.damageFrames == 3) {
				this.damageFrames = 0;
				isDamaged = false;
			}
		}
	}
	
	public void checkCollisionWithGun() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			
			if(e instanceof Weapon) {
				if(Entity.isColliding(this, e)) {
					
					hasGun = true;
					
					Game.entities.remove(i);
				}
			}
		}
	}
	
	public void checkCollisionWithAmmo() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			
			if(e instanceof Bullet) {
				if(Entity.isColliding(this, e)) {
					
					ammo+=10;
					
					Game.entities.remove(i);
				}
			}
		}
	}
	
	public void checkCollisionWithLifePack() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			
			if(e instanceof Lifepack) {
				if(Entity.isColliding(this, e)) {
					
					life +=8;
					
					if(life >= 100) {
						life = 100;
					}
					
					Game.entities.remove(i);
					return;
				}
			}
		}
	}
	
	private void renderAnimation(Graphics g) {
		if(!isDamaged) {
			if(dir == right_dir) {
				
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - this.getZ(), null);
				
				if(hasGun) {
					g.drawImage(Entity.GUN_RIGHT, this.getX() - Camera.x + 5, (this.getY() - Camera.y + 2) - this.getZ(), null);
				}
				
			}
			else if(dir == left_dir) {
				
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - this.getZ(), null);
				
				if(hasGun) {
					g.drawImage(Entity.GUN_LEFT, this.getX() - Camera.x - 5, (this.getY() - Camera.y + 2) - this.getZ(), null);
				}
			}
		}
		else {
			g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y - this.getZ(), null);
		}
		
		if(isJumping) {
			g.setColor(Color.black);
			g.fillOval(this.getX() - Camera.x + 8, this.getY() - Camera.y + 16, 8, 8);
		}

	}

}
