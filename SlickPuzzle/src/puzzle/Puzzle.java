package puzzle;

import java.util.ArrayList;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;

/**
 * KitiPong - a demo game for Slick
 *
 * @author kevin
 */
public class Puzzle extends BasicGame {
	private static final int BOARD_TOP = -816;
	private static final int BOARD_LEFT = 12;
	
	/** The scalar applied to the rotation of the hint kitty */
	public static final int SPIN_SCALAR = 2;
	/** The maximum number of piece types addedto the board */
	public static final int MAX_PIECE = 6;
	
	/** The index in the array of the tile data */
	public static final int TILE = 0;
	/** The index in the array of the marker indicating a tile should be removed */
	public static final int REMOVE_MARKER = 1;
	/** The index in the array of the marker indicating a tile should fall down */
	public static final int FALL_MARKER = 2;
	/** The index in the array of the marker indicating a tile should rotate to indicate the hint */
	public static final int MATCH_HINT = 3;
	
	/** Indicates we're waiting for mouse press moves */
	public static final int PLAY = 0;
	/** Indicates we're running a swap over animation */
	public static final int SWAP = 1;
	/** Indicates we're running a remove animation */
	public static final int REMOVE = 2;
	/** Indicates we're running a fall animation */
	public static final int FALL = 3;
	/** Indicates we're running a swap back animation */
	public static final int SWAP_BACK = 4;
	/** Indicates we're running a hint animation */
	public static final int HINT = 5;
	
	/** The image displayed in the background */
	private Image background;
	/** The bamboo at the bottom of the play area */
	private Image bottom;
	/** The bamboo at the edges of the play area */
	private Image edge;
	
	/** The images of the different kats */
	private Image[] kitis = new Image[9];
	/** The logo displayed on the start screen and faded in the background */
	private Image logo;
	/** The label for the matches yPos */
	private Image matchesLabel;
	/** The label for the score display */
	private Image scoreLabel;
	/** The label for the time display */
	private Image timeLabel;
	/** A kitty to sit at the bottom at look cute */
	private Image kitty;
	/** The font used for rendering the start up text */
	private Font font;
	/** The font used for rendering the numbers in the game display */
	private Font numbers;
	
	/** True if we at starting page - showing and text and waiting for a click */
	private boolean starting = true;
	/** The ang used in a cosine operation to make the text bounce */
	private float ang;

	/** The current time played in milliseconds */
	private int time;
	/** The current score */
	private int score;
	/** The number of matches achieved */
	private int matches;
	
	/** The tiles on the game board - and associated data hence the [10] */
	private int[][][] tiles = new int[8][20][10];
	/** The current selected x position */
	private int selectedx;
	/** The current selected y position */
	private int selectedy;
	/** True if a first token has been selected */
	private boolean selected; 
	/** The x position of the tapped (second piece) */
	private int tappedx;
	/** The y position of the tapped (second piece) */
	private int tappedy;
	/** True if a second piece has been tapped */
	private boolean tapped;
	
	/** The container holding kitipong */
	private GameContainer container;
	/** The sound effect for meowing cats */
	private Sound meow;
	/** The sound effect for cliking on place where a move cannot be made */
	private Sound nope;
	/** The sound effect for starting a game */
	private Sound gong;
	
	/** The current state the game is - see above for game states */
	private int state;
	
	/** The amount on the x axis we're swapping - used for the swap over animation */
	private float swapx;
	/** The amount on the y axis we're swapping - used for the swap over animation */
	private float swapy;
	
	/** The scalar used to shrink the blocks when they're removed */
	private float removeStep;
	/** The size of the step to fall when a tile falls */
	private int fallOffset;
	
	/** The time at which the player started the game */
	private long start;
	/** True if we should show the current FPS */
	private boolean showFPS = false;
	
	/** The area that can be clicked for a hint */
	private Rectangle hintRect;
	/** The area that can be clicked to quit */
	private Rectangle quitRect;
	
	/** The timer that waits while the game over message is shown */
	private int gameOverCounter;
	/** The number of possible moves left */
	private int movesLeft;
	
	/**
	 * Create a new example puzzle game instance
	 */
	public Puzzle() {
		super("Kitipong");
	}

	/**
	 * Pad a string with zeros to the length specified 
	 * 
	 * @param value The value to turn into a string
	 * @param len The final length of the required string
	 * @return The padded string
	 */
	private String pad(int value, int len) {
		String v = ""+value;
		
		while (v.length() < len) {
			v = "0"+v;
		}
		
		return v;
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		this.container = container;
		
		container.setShowFPS(false);
		container.setVSync(true);
		container.setVerbose(false);
		
		container.setIcon("res/icon.png");
		container.setMouseCursor("res/cursor.png", 5, 2);
		
		Music music = new Music("res/orientp.xm");
		music.loop(1.0f,0.5f);
		meow = new Sound("res/meeow.wav");
		nope = new Sound("res/thud.ogg");
		gong = new Sound("res/gong.wav");
		
		font = new AngelCodeFont("res/puzzle.fnt","res/puzzle_00.tga");
		numbers = new AngelCodeFont("res/numbers.fnt","res/numbers_00.tga");
		
		background = new Image("res/background.png");
		bottom = new Image("res/bottom.png");
		edge = new Image("res/edges.png");
		logo = new Image("res/logo.png");
		matchesLabel = new Image("res/matches.png");
		scoreLabel = new Image("res/score.png");
		timeLabel = new Image("res/time.png");
		kitty = new Image("res/kitty.png");
		for (int i=1;i<10;i++) {
			kitis[i-1] = new Image("res/"+i+".png");
		}
		kitis[2] = kitis[6];
		kitis[4] = kitis[8];
		
		hintRect = new Rectangle(469,375,100,30);
		quitRect = new Rectangle(468,415,100,30);
		
		clearBoard();
	}

	/**
	 * Get a list of the valid pieces for a particular location, i.e. pieces that won't
	 * cause a match by placing them.
	 * 
	 * @param x The x coordinate to search for
	 * @param y The y coordinate to search for
	 * @return The list of pieces that can be safely placed
	 */
	private ArrayList getValidPieces(int x,int y) {
		ArrayList list = new ArrayList();
		for (int i=0;i<MAX_PIECE;i++) {
			list.add(new Integer(i));
		}
		
		if (x > 0) {
			list.remove(new Integer(tiles[x-1][y][TILE]));
		}
		if (x < 7) {
			list.remove(new Integer(tiles[x+1][y][TILE]));
		}
		if (y > 0) {
			list.remove(new Integer(tiles[x][y-1][TILE]));
		}
		if (y < 9) {
			list.remove(new Integer(tiles[x][y+1][TILE]));
		}
		
		return list;
	}
	
	/**
	 * Start a game of kitipong!
	 */
	private void start() {
		gameOverCounter = 0;
		gong.play(1.0f,0.6f);
		
		start = container.getTime();
		starting = false;
		matches = 0;
		score = 0;
		time = 0;

		clearBoard();
		fillSpaces();
		
		state = PLAY;
	}
	
	/**
	 * Remove all the tiles from the board
	 */
	private void clearBoard() {
		for (int x=0;x<8;x++) {
			for (int y=0;y<20;y++) {
				tiles[x][y][TILE] = -1;
			}
		}
	}
	
	/**
	 * Draw a number in the game display, centres and shadows it
	 * 
	 * @param g The graphics context to draw onto
	 * @param str The string to be drawn
	 * @param y The y location at which the text should be rendered
	 */
	private void drawNumberString(Graphics g, String str, int y) {
		g.setColor(Color.black);
		g.drawString(str, (int) (456+((180-(numbers.getWidth(str)))/2)),y+1);
		g.setColor(Color.white);
		g.drawString(str, (int) (455+((180-(numbers.getWidth(str)))/2)),y);
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) throws SlickException {
		background.draw(0,0,640,480);
		g.scale(0.8f, 1);
		bottom.draw(2,450);
		edge.draw(9,0,edge.getWidth(),450);
		g.resetTransform();
		
		logo.draw(80,190,new Color(1,1,1,0.3f));
		matchesLabel.draw(460,10);
		scoreLabel.draw(480,130);
		timeLabel.draw(490,250);
		kitty.draw(535,340);

		g.setFont(font);
		g.setColor(Color.black);
		g.drawString("HINT",470,376);
		g.drawString("QUIT",469,416);
		g.setColor(Color.white);
		g.drawString("HINT",469,375);
		g.drawString("QUIT",468,415);
		
		String str;
		g.setFont(numbers);
		
		str = pad(matches, 4);
		drawNumberString(g, str, 50);
		str = pad(score, 7);
		drawNumberString(g, str, 170);
		str = pad(time / 60, 2) + ":" + pad(time % 60, 2);
		drawNumberString(g, str, 290);
		
		// draw all the kittys!
		for (int x=0;x<8;x++) {
			for (int y=0;y<20;y++) {
				int tile = tiles[x][y][TILE];

				float yofs = 0;
				float xofs = 0;
				
				if (tile != -1) {
					if (selected) {
						if ((selectedx == x) && (selectedy == y)) {
							yofs = (float) (Math.cos(ang)* 4);
							g.setColor(new Color(1,1,0,0.5f));
							g.fillOval(BOARD_LEFT+(x*54),(int) (BOARD_TOP+(y*64)),54,44);
							g.setColor(Color.black);
							g.setAntiAlias(true);
							g.drawOval(BOARD_LEFT+(x*54),(int) (BOARD_TOP+(y*64)),54,44);
							g.setAntiAlias(false);
						}
					}
					if ((state == SWAP) || (state == SWAP_BACK)) {
						int xbase = swapx < 0 ? -54 : 54;
						int ybase = swapy < 0 ? -64 : 64;
						
						if ((selectedx == x) && (selectedy == y)) {
							if (swapx != 0) {
								xofs = xbase - swapx;
							}
							if (swapy != 0) {
								yofs = ybase - swapy;
							}
						}
						if ((tappedx == x) && (tappedy == y)) {
							if (swapx != 0) {
								xofs = -(xbase - swapx);
							}
							if (swapy != 0) {
								yofs = -(ybase - swapy);
							}
						}
					}
					float size = 1;
					if (state == REMOVE) {
						if (tiles[x][y][REMOVE_MARKER] == 1) {
							size = removeStep;
							xofs = (54 - (54 * size)) / 2;
							yofs = (64 - (64 * size)) / 2;
						}
					}
					if (state == FALL) {
						if (tiles[x][y][FALL_MARKER] == 1) {
							yofs = -fallOffset;
						}
					}
					if ((state == HINT) && (tiles[x][y][MATCH_HINT] > 0)) {
						int xp = 11+(x*54);
						int yp = -831+(y*64);
						
						g.rotate(xp+27,yp+32,tiles[x][y][MATCH_HINT]/SPIN_SCALAR);
						kitis[tile].draw(xp,yp);
						g.resetTransform();
					} else {
						kitis[tile].draw((int) (11+(x*54)+xofs),(int) (-831+(y*64)+yofs),(int) (54 * size),(int) (64 * size));
					}
				}
			}
		}
		
		if (starting) {
			g.setColor(Color.white);
			g.setFont(font);
			g.setColor(new Color(0,0,0,0.7f));
			g.fillRect(50,50,540,380);
			g.setColor(new Color(0,0,0));
			g.drawRect(50,50,540,380);
			g.drawRect(48,48,544,384);
			g.setColor(Color.white);
			g.drawRect(49,49,542,382);
			
			logo.draw(180,60);
			
			if (gameOverCounter > 0) {
				g.drawString("Board Complete - No more matches left!",90,200);
				g.drawString("Well Done!",280,300);
			} else {
				g.drawString("Kitipong is a simple bejewled style game to",70,130);
				g.drawString("demonstrate the Slick library.",150,160);
	
				g.drawString("Select two adjacent Kitty Kats to swap them",70,200);
				g.drawString("over. If three Kats of the same colour are next",70,230);
				g.drawString("to each other they will vanish scoring you",70,260);
				g.drawString("points!",290,290);
	
				g.drawString("S - Toggle Sound/Music",200,335);
				
				g.drawString("Click to Start",255,(int) (385+(Math.cos(ang)*8)));
			}
		}
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#update(org.newdawn.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) throws SlickException {
		time = (int) ((container.getTime() - start) / 1000);
		if (starting) {
			time = 0;
		}
		ang += delta * 0.01f;
		
		int originalState = state;
		
		if (state == HINT) {
			boolean found = false;
			
			for (int x=0;x<8;x++) {
				for (int y=0;y<20;y++) {
					if (tiles[x][y][MATCH_HINT] > 0) {
						tiles[x][y][MATCH_HINT] -= delta;
						found = true;
					}
				}
			}
			
			if (!found) {
				state = PLAY;
			}
		}
		if (state == REMOVE) {
			removeStep -= delta * 0.005f;
			if (removeStep < 0) {
				removeStep = 0;
				finishRemove();
				int falls = findFalls();
				if (falls == 0) {
					fillSpaces();
					int matches = findMatches();
					if (matches == 0) {
						state = PLAY;
					} else {
						state = REMOVE;
						removeStep = 1;
					}
				} else {
					fallOffset = 64;
					state = FALL;
				}
			}
		}
		if (state == FALL) {
			fallOffset -= delta * 0.5f;
			if (fallOffset <= 0) {
				fallOffset = 0;
				int falls = findFalls();
				if (falls == 0) {
					fillSpaces();
					int matches = findMatches();
					if (matches == 0) {
						state = PLAY;
					} else {
						state = REMOVE;
						removeStep = 1;
					}
				} else {
					fallOffset = 64;
					state = FALL;
				}
			}
		}
		if ((state == SWAP) || (state == SWAP_BACK)) {
			float swapSpeed = 0.17f;
			
			if (swapx > 0) {
				swapx -= delta * swapSpeed;
				swapx = Math.max(0,swapx);
			}
			if (swapy > 0) {
				swapy -= delta * swapSpeed;
				swapy = Math.max(0,swapy);
			}
			if (swapx < 0) {
				swapx += delta * swapSpeed;
				swapx = Math.min(0,swapx);
			}
			if (swapy < 0) {
				swapy += delta * swapSpeed;
				swapy = Math.min(0,swapy);
			}
			
			if ((swapx == 0) && (swapy == 0)) {
				int temp = tiles[tappedx][tappedy][TILE];
				tiles[tappedx][tappedy][TILE] = tiles[selectedx][selectedy][TILE];
				tiles[selectedx][selectedy][TILE] = temp;
				
				if (state == SWAP_BACK) {
					state = PLAY;
				} else {
					int matches = findMatches();
					if (matches == 0) {
						swap();
						state = SWAP_BACK;
					} else {
						removeStep = 1;
						state = REMOVE;
					}
				}
			}
		}
		
		if (gameOverCounter > 0) {
			gameOverCounter -= delta;
			if (gameOverCounter <= 0) {
				clearBoard();
			}
		}
		
		if ((!starting) && (state == PLAY)) {
			movesLeft = findMoves();
			
			if (movesLeft == 0) {
				starting = true;
				gameOverCounter = 2000;
			}
		}
		
		if (showFPS) {
			Thread.yield();
		}
	}


	/**
	 * Finish off remove, i.e. actually remove the tiles and unset the remove marker
	 * so the animation stops and the game state can go back to PLAY
	 */
	private void finishRemove() {
		for (int x=0;x<8;x++) {
			for (int y=0;y<20;y++) {
				if (tiles[x][y][REMOVE_MARKER] == 1) {
					tiles[x][y][REMOVE_MARKER] = 0;
					tiles[x][y][TILE] = -1;
				}
			}
		}
	}
	
	/**
	 * Fill any spaces in the game board with tiles that won't automatically 
	 * form a match
	 */
	private void fillSpaces() {
		for (int x=0;x<8;x++) {
			for (int y=0;y<20;y++) {
				ArrayList pieces = getValidPieces(x,y);
				
				if (tiles[x][y][TILE] == -1) {
					tiles[x][y][TILE] = ((Integer) pieces.get((int) (Math.random() * pieces.size()))).intValue();
				}
			}
		}
	}
	
	/**
	 * Find the tiles that should fall down and mark them as such
	 * 
	 * @return The number of tiles that should fall
	 */
	private int findFalls() {
		int foundFalls = 0;
		
		for (int y=19;y>0;y--) {
			for (int x=0;x<8;x++) {
				tiles[x][y][FALL_MARKER] = 0;
				
				if ((tiles[x][y][TILE] == -1) && (tiles[x][y-1][TILE] != -1)) {
					tiles[x][y][TILE] = tiles[x][y-1][TILE];
					tiles[x][y-1][TILE] = -1;
					tiles[x][y][FALL_MARKER] = 1;
					foundFalls++;
					fallOffset = 64;
				}
			}
		}
		
		return foundFalls;
	}
	
	/**
	 * Find the number of moves available on the game board that will succesfully
	 * cause a match.
	 * 
	 * @return The number of moves available
	 */
	private int findMoves() {
		int total = 0;
		
		for (int y=13;y<20;y++) {
			for (int x=0;x<8;x++) {
				if (x < 7) {
					swap(x,y,x+1,y);
					int matches = findMatches(false);
					swap(x,y,x+1,y);
					
					if (matches != 0) {
						total++;
					}
				}
				if (y < 19) {
					swap(x,y,x,y+1);
					int matches = findMatches(false);
					swap(x,y,x,y+1);
					
					if (matches != 0) {
						total++;
					}
				}
			}
		}
		
		return total;
	}
	
	/**
	 * Cause a hint to be display. We'll find a list of moves then pick a random one
	 * to show.
	 */
	private void hint() {
		if (state == PLAY) {
			int total = findMoves();
			
			if (total == 0) {
				// no moves left!
			} else {
				int move = (int) (Math.random() * total);
				
				for (int y=13;y<20;y++) {
					for (int x=0;x<8;x++) {
						if (x < 7) {
							swap(x,y,x+1,y);
							int matches = findMatches(false);
							swap(x,y,x+1,y);
							
							if (matches != 0) {
								move--;
								if (move <= 1) {
									tiles[x][y][MATCH_HINT] = 360 * SPIN_SCALAR;
									tiles[x+1][y][MATCH_HINT] = 360* SPIN_SCALAR;
									state = HINT;
									return;
								}
							}
						}
						if (y < 19) {
							swap(x,y,x,y+1);
							int matches = findMatches(false);
							swap(x,y,x,y+1);
							
							if (matches != 0) {
								move--;
								if (move <= 1) {
									tiles[x][y][MATCH_HINT] = 360 * SPIN_SCALAR;
									tiles[x][y+1][MATCH_HINT] = 360 * SPIN_SCALAR;
									state = HINT;
									return;
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Swap two tiles over in the board
	 * 
	 * @param x1 The x coordinate of the first tile to swap
	 * @param y1 The y coordinate of the first tile to swap
	 * @param x2 The x coordinate of the second tile to swap
	 * @param y2 The y coordinate of the second tile to swap
	 */
	private void swap(int x1, int y1, int x2, int y2) {
		int t1 = tiles[x1][y1][TILE];
		tiles[x1][y1][TILE] = tiles[x2][y2][TILE];
		tiles[x2][y2][TILE] = t1;
	}

	/**
	 * Find the number of matches on the board
	 * 
	 * @return The number of matches on the board
	 */
	private int findMatches() {
		return findMatches(true);
	}
	
	/**
	 * Find the number of matches on the board
	 * 
	 * @param updateGameState True if we should update game state based on the matches, false
	 * if we're just checking for a status update.
	 * @return The number of matches on the board
	 */
	private int findMatches(boolean updateGameState) {
		int foundMatches = 0;
		
		for (int y=19;y>12;y--) {
			for (int x=0;x<8;x++) {
				int ours = tiles[x][y][TILE];
				if (ours != -1) {
					if ((x > 0) && (x < 7)) {
						if ((tiles[x-1][y][TILE] == ours) && (tiles[x+1][y][TILE] == ours)) {
							int bonus = 100;
							boolean newMatch = true;
							
							if (tiles[x-1][y][REMOVE_MARKER] == 1) {
								newMatch = false;
								bonus *= 2;
							}
							if (tiles[x+1][y][REMOVE_MARKER] == 1) {
								newMatch = false;
								bonus *= 2;
							}


							foundMatches++;

							if (updateGameState) {
								tiles[x-1][y][REMOVE_MARKER] = 1;
								tiles[x][y][REMOVE_MARKER] = 1;
								tiles[x+1][y][REMOVE_MARKER] = 1;
								
								score += bonus;
								if (newMatch) {
									matches++;
								}
							}
						}
					}
					
					if ((y > 13) && (y < 19)) {
						if ((tiles[x][y-1][TILE] == ours) && (tiles[x][y+1][TILE] == ours)) {
							int bonus = 100;
							boolean newMatch = true;
							
							if (tiles[x][y-1][REMOVE_MARKER] == 1) {
								newMatch = false;
								bonus *= 2;
							}
							if (tiles[x][y+1][REMOVE_MARKER] == 1) {
								newMatch = false;
								bonus *= 2;
							}
	
							foundMatches++;
							
							if (updateGameState) {
								tiles[x][y-1][REMOVE_MARKER] = 1;
								tiles[x][y][REMOVE_MARKER] = 1;
								tiles[x][y+1][REMOVE_MARKER] = 1;
								
								score += bonus;
								if (newMatch) {
									matches++;
								}
							}
						}
					}
				}
			}
		}
		
		return foundMatches;
	}
	
	/**
	 * Swap the two selected tiles (selected and tapped)
	 */
	private void swap() {
		int xd = tappedx - selectedx;
		int yd = tappedy - selectedy;
		
		swapx = xd * 54;
		swapy = yd * 64;
		
		selected = false;
		
		state = SWAP;
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_S) {
			container.setMusicOn(!container.isMusicOn());
			container.setSoundOn(!container.isSoundOn());
		}
		if (key == Input.KEY_ESCAPE) {
			if (starting) {
				container.exit();
			} else {
				starting = true;
			}
		}
		if (key == Input.KEY_F) {
			showFPS = !showFPS;
			container.setShowFPS(showFPS);
			container.setVSync(!showFPS);
		}
	}
	
	/**
	 * @see org.newdawn.slick.BasicGame#mousePressed(int, int, int)
	 */
	public void mousePressed(int button, int x, int y) {
		if (starting) {
			start();
		} else {
			if (quitRect.contains(x,y)) {
				starting = true;
				return;
			}
			if (hintRect.contains(x,y)) {
				hint();
				return;
			}
			if (state == PLAY) {
				if (button != 0) {
					selected = false;
					return;
				}
				
				x -= 11;
				y -= -831;
				
				x /= 54;
				y /= 64;
		
					if ((x >= 0) && (x < 8) && (y >= 2) && (y < 20)) {
					if (!selected) {
						selected = true;
						selectedx = x;
						selectedy = y;
						meow.play((float) (0.9f + (Math.random()*0.3f)), (float) (0.4f + (Math.random()*0.3f)));
					} else {
						int xd = Math.abs(x - selectedx);
						int yd = Math.abs(y - selectedy);
						
						if ((xd == 0) && (yd == 0)) {
							selected = false;
							meow.play((float) (0.9f + (Math.random()*0.3f)), (float) (0.4f + (Math.random()*0.3f)));
						} else if (xd+yd == 1) {
							tappedx = x;
							tappedy = y;
							selected = false;
							meow.play((float) (0.9f + (Math.random()*0.3f)), (float) (0.4f + (Math.random()*0.3f)));
						
							swap();
						} else {
							nope.play();
						}
					}
				} else {
					selected = false;
				}
			}
		}
	}
	
	/**
	 * Entry point into our game. Simple bootstrap for the container and fire
	 * the game off.
	 * 
	 * @param argv The arguments passed into the game
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new Puzzle());
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
