package com.me.SpecOps.entities;

import java.util.ArrayList;
import java.util.Stack;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.me.SpecOps.misc.Node;

public class Enemy extends Player {
	private Vector2 velocity = new Vector2(); //movement velocity
	private static float speed = 4.0f; //how fast move; 22.628f equates to ~512px(512.02637)
	private float direction = 0.0f;
	private state curState = state.IDLE;
	private static boolean turn = false;

	public Enemy(Array<Sprite> animMove, TiledMapTileLayer collisionLayer,
			ArrayList<Bullet> bullets, String id) {
		super(animMove, collisionLayer, bullets, id,null);
		setHealth(10);
	}
	
	public float findPath() {
		Node root = new Node(getX(),getY(),findDirection());
		Stack<Node> s = new Stack<Node>();
		ArrayList<Node> nodes = new ArrayList<Node>();
		s.push(root);
		
		//System.out.println(root.getDir());
		while(s.peek().getX() != getOpponentX() || s.peek().getY() != getOpponentY()) {
			
			for(int i = 0; i < nodes.size(); i++) {
				if (nodes.get(i).getX() == s.peek().getX() && nodes.get(i).getY() == s.peek().getY()) {
					s.peek().setChecked(true);
					break;
				}
			}
			
			//System.out.println(s.peek().getX()/16.0f + " " + s.peek().getY()/16.0f);
			
			Node up = new Node(s.peek().getX(),s.peek().getY()+16.0f,90.0f);
			Node left = new Node(s.peek().getX()-16.0f,s.peek().getY(),180.0f);
			Node right = new Node(s.peek().getX()+16.0f,s.peek().getY(),0.0f);
			Node down = new Node(s.peek().getX(),s.peek().getY()-16.0f,270.0f);
			
			if(!s.peek().getChecked() && !blockedCell(s.peek().getX(),s.peek().getY()) && (s.peek().getX() >= 48.0f && s.peek().getX() <= 112.0f) && (s.peek().getY() >= 64.0f && s.peek().getY() <= 176.0f)) {
				s.peek().setChecked(true);
				nodes.add(s.peek());
				
				float playerDir = findDirection();
				
				if(s.peek().getDir() == 90.0f) {
					playerDir = findDirectionX(s.peek().getDir());
					
					if(playerDir == 180.0f) {
						s.push(right);
						s.push(left);
					}
					else if(playerDir == 0.0f) {
						s.push(left);
						s.push(right);
					}
					
					s.push(up);
					
				}
				else if(s.peek().getDir() == 180.0f) {
					playerDir = findDirectionY(s.peek().getDir());
					
					if(playerDir == 90.0f) {
						s.push(down);
						s.push(up);
					}
					else if(playerDir == 270.0f) {
						s.push(up);
						s.push(down);
					}
					
					s.push(left);
				}
				else if(s.peek().getDir() == 0.0f) {
					playerDir = findDirectionY(s.peek().getDir());
					
					if(playerDir == 90.0f) {
						s.push(down);
						s.push(up);
					}
					else if(playerDir == 270.0f) {
						s.push(up);
						s.push(down);
					}
					
					s.push(right);
				}
				else if(s.peek().getDir() == 270.0f) {
					playerDir = findDirectionX(s.peek().getDir());
					
					if(playerDir == 180.0f) {
						s.push(right);
						s.push(left);
					}
					else if(playerDir == 0.0f) {
						s.push(left);
						s.push(right);
					}
					
					s.push(down);
				}
				else {
					s.push(up);
					s.push(left);
					s.push(right);
					s.push(down);
				}
			}
			else if(nodes.contains(s.pop())) {
				nodes.remove(nodes.size()-1);
				
				if(s.size() == 0) {
					s.push(up);
					s.push(left);
					s.push(right);
					s.push(down);
				}
			}

			if(s.size() == 0)
				break;
			
		}

		float retDir = 0;
		
		if(nodes.size() < 2) {
			if(!blockedCell(getX(),getY()+16.0f))
				retDir = 90.0f;
			else if(!blockedCell(getX(),getY()-16.0f))
				retDir = 270.0f;
			else if(!blockedCell(getX()+16.0f,getY()))
				retDir = 0.0f;
			else if(!blockedCell(getX()-16.0f,getY()))
				retDir = 180.0f;
		}
		
		//System.out.println(nodes.get(1).getX() + " " + nodes.get(1).getY() + " " + nodes.get(1).getDir());
		return nodes.size() >= 2 ? nodes.get(1).getDir() : retDir;
	}
	
	public void update(float delta) {
		if((((int) (getX()+16.0f) == (int) (getOpponentX()) && getY() == getOpponentY()) 
				|| ((int) (getX()-16.0f) == (int) (getOpponentX()) && getY() == getOpponentY()) 
				|| ((int) (getY()+16.0f) == (int) (getOpponentY()) && getX() == getOpponentX()) 
				|| ((int) (getY()-16.0f) == (int) (getOpponentY()) && getX() == getOpponentX())) && ((getDirection()+180.0f) % 360.0f == getOpponentDirection())) 
			shoot();
		else if(getMoves() > 0) {
			float newDir = 0;
			
			if((((int) (getX()+16.0f) == (int) (getOpponentX()) && getY() == getOpponentY()) 
					|| ((int) (getX()-16.0f) == (int) (getOpponentX()) && getY() == getOpponentY()) 
					|| ((int) (getY()+16.0f) == (int) (getOpponentY()) && getX() == getOpponentX()) 
					|| ((int) (getY()-16.0f) == (int) (getOpponentY()) && getX() == getOpponentX())) && ((getDirection()+180.0f) % 360.0f != getOpponentDirection()))
				newDir = findDirection();
			else 
				newDir = findPath();
			
			setCurState(state.WALKING);
			
			if(newDir == 180.0f) {
				setDirection(180.0f);
				velocity.x = -speed;
				
				if (!collidesLeft()) 
					setX((int) (getX() - velocity.x * -speed));

			}
			else if(newDir == 0.0f) {
				setDirection(0.0f);
				velocity.x = speed;

				if(!collidesRight()) 
					setX((int) (getX() + velocity.x * speed));
			}
			else if(newDir == 270.0f) {
				setDirection(270.0f);
				velocity.y = -speed;
				
				if(!collidesBottom()) 
					setY((int) (getY() - velocity.y * -speed));
			}
			else if(newDir == 90.0f) {
				setDirection(90.0f);
				velocity.y = speed;
				
				if(!collidesTop()) 
					setY((int) (getY() + velocity.y * speed));
			}

			setMoves(getMoves()-1);

			velocity.x = 0;
			velocity.y = 0;
			setState(state.IDLE);
		}
	}
	
	public float findDirection() {
		float x = getX() - getOpponentX(), y = getY() - getOpponentY(), dir = getDirection();
		
		if (Math.abs(x) > Math.abs(y)) {
			if (x > 0) {
				dir = 180.0f;
			}
			else if (x < 0) {
				dir = 0.0f;
			}
		}
		else if(Math.abs(y) > Math.abs(x)) {
			if(y < 0) {
				dir = 90.0f;
			}
			else if(y > 0) {
				dir = 270.0f;
			}
		}
		return dir;
	}
	
	public float findDirectionX(float curNodeDir) {
		float x = getX() - getOpponentX(), dir = curNodeDir;

		if (x > 0) {
			dir = 180.0f;
		}
		else if (x < 0) {
			dir = 0.0f;
		}
		
		return dir;
	}
	
	public float findDirectionY(float curNodeDir) {
		float y = getY() - getOpponentY(), dir = curNodeDir;

		if(y < 0) {
			dir = 90.0f;
		}
		else if(y > 0) {
			dir = 270.0f;
		}
		
		return dir;
	}
	
	public boolean getTurn() {
		return turn;
	}
	
	public void setTurn(boolean turn) {
		Enemy.turn = turn;
	}
	
	public boolean keyDown(int keycode) {
		return false;
	}
	
	public boolean keyUp(int keycode) {
		return false;
	}
	
	public boolean touchDown(int keycode) {
		return false;
	}
	
	public boolean touchUp(int keycode) {
		return false;
	}

	public state getCurState() {
		return curState;
	}

	public void setCurState(state curState) {
		this.curState = curState;
	}

	public float getDirection() {
		return direction;
	}

	public void setDirection(float direction) {
		this.direction = direction;
	}

}
