package org.newdawn.warpstone;

import org.newdawn.slick.SlickException;

public class ActorTypeStore {
	
	public static void init() throws SlickException {
		ActorType.PEASANT = new ActorType(0,"res/peasant.png", 50, new int[] {0,2,1,0,4,3}, new int[] {6,5,9,7,8,7,9,5}, new int[] {10,11,12});
		ActorType.SWORDSMAN = new ActorType(1,"res/sword.png", 70, new int[] {0,2,1,0,4,3}, new int[] {5,6,7,8}, new int[] {9,10,11});	
		ActorType.ARCHER = new ActorType(2,"res/archer.png", 50, 50, new int[] {0,2,1,0,4,3}, new int[] {5,6,7,8}, new int[] {9,10,11});		
		ActorType.MAGE = new ActorType(3,"res/mage.png", 50, 73, new int[] {0,2,1,0,4,3}, new int[] {5,6,7,8}, new int[] {9,10,11});		
		ActorType.SKELETON = new ActorType(4,"res/skel.png", 55, new int[] {0,8,11,0,2,5}, new int[] {5,6,7,8,9}, new int[] {10,11,12}); 	
	}
}
