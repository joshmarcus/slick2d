package org.newdawn.noodles.tankexamples.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.newdawn.noodles.message.Message;
import org.newdawn.noodles.tankexamples.Tank;

/**
 * A message updating the state of a given tank
 * 
 * @author kevin
 */
public class TankUpdateMessage implements Message {
	/** The ID of the message type */
	public static final int ID = 1;
	
	/** The ID of the tank to update */
	private int channelID;
	/** The x position of the tank */
	private float x;
	/** The y position of the tank */
	private float y;
	/** The angle of the tank */
	private float ang;
	/** The angle of the tank's turret */
	private float turretAng;
	
	/**
	 * Create a new empty message - used on reception
	 */
	public TankUpdateMessage() {
		
	}
	
	/**
	 * Create a new message
	 * 
	 * @param channelID The ID of the tank
	 * @param tank The tank's state to be sent
	 */
	public TankUpdateMessage(int channelID, Tank tank) {
		this.channelID = channelID;
		x = tank.getX();
		y = tank.getY();
		ang = tank.getAngle();
		turretAng = tank.getTurretAngle();
	}

	/**
	 * Get the ID of the tank
	 * 
	 * @return The ID of the tanke
	 */
	public int getChannelID() {
		return channelID;
	}
	
	/**
	 * Get the x coordinate of the tank's position
	 * 
	 * @return The x coordinate of the tank's positon
	 */
	public float getX() {
		return x;
	}

	/**
	 * Get the y coordinate of the tank's position
	 * 
	 * @return The y coordinate of the tank's positon
	 */
	public float getY() {
		return y;
	}
	
	/**
	 * Get the angle of the tank
	 * 
	 * @return The angle of the tank
	 */
	public float getAngle() {
		return ang;
	}

	/**
	 * Get the angle of the tank's turret
	 * 
	 * @return The angle of the tank's turret
	 */
	public float getTurretAngle() {
		return turretAng;
	}
	
	/**
	 * @see org.newdawn.noodles.message.Message#decode(java.io.DataInputStream)
	 */
	public void decode(DataInputStream din) throws IOException {
		channelID = din.readInt();
		x = din.readFloat();
		y = din.readFloat();
		ang = din.readFloat();
		turretAng = din.readFloat();
	}

	/**
	 * @see org.newdawn.noodles.message.Message#encode(java.io.DataOutputStream)
	 */
	public void encode(DataOutputStream dout) throws IOException {
		dout.writeInt(channelID);
		dout.writeFloat(x);
		dout.writeFloat(y);
		dout.writeFloat(ang);
		dout.writeFloat(turretAng);
	}

	/**
	 * @see org.newdawn.noodles.message.Message#getID()
	 */
	public short getID() {
		return ID;
	}

}
