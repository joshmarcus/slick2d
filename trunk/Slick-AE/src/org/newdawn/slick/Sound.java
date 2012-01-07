package org.newdawn.slick;

import java.net.URL;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files.FileType;

/**
 * A single sound effect loaded from either OGG or XM/MOD file. Sounds are allocated to 
 * channels dynamically - if not channel is available the sound will not play. 
 *
 * @author kevin
 */
public class Sound {
	/** The underlying sound */
	private com.badlogic.gdx.audio.Sound sound;
	
	/**
	 * Create a new Sound 
	 * 
	 * @param url The location of the OGG or MOD/XM to load
	 * @throws SlickException Indicates a failure to load the sound effect
	 */
	public Sound(URL url) throws SlickException {
		throw new RuntimeException("URL sound loading not supported in Slick-AE");
	}
	
	/**
	 * Create a new Sound 
	 * 
	 * @param ref The location of the OGG or MOD/XM to load
	 * @throws SlickException Indicates a failure to load the sound effect
	 */
	public Sound(String ref) throws SlickException {
        sound = Gdx.audio.newSound(Gdx.files.getFileHandle(ref, FileType.Internal));
	}
	
	/**
	 * Play this sound effect at default volume and pitch
	 */
	public void play() {
		play(1.0f, 1.0f);
	}
	
	/**
	 * Play this sound effect at a given volume and pitch
	 * 
	 * @param pitch The pitch to play the sound effect at
	 * @param volume The volumen to play the sound effect at
	 */
	public void play(float pitch, float volume) {
		sound.play(volume);
	}

	/**
	 * Play a sound effect from a particular location
	 * 
	 * @param x The x position of the source of the effect
 	 * @param y The y position of the source of the effect
	 * @param z The z position of the source of the effect
	 */
	public void playAt(float x, float y, float z) {
		playAt(1.0f, 1.0f, x,y,z);
	}
	
	/**
	 * Play a sound effect from a particular location
	 * 
	 * @param pitch The pitch to play the sound effect at
	 * @param volume The volumen to play the sound effect at
	 * @param x The x position of the source of the effect
 	 * @param y The y position of the source of the effect
	 * @param z The z position of the source of the effect
	 */
	public void playAt(float pitch, float volume, float x, float y, float z) {
		sound.play(volume);
	}
	
	/**
	 * Loop this sound effect at default volume and pitch
	 */
	public void loop() {
		loop(1.0f, 1.0f);
	}
	
	/**
	 * Loop this sound effect at a given volume and pitch
	 * 
	 * @param pitch The pitch to play the sound effect at
	 * @param volume The volumen to play the sound effect at
	 */
	public void loop(float pitch, float volume) {
		sound.play(volume);
	}
	
	/**
	 * Check if the sound is currently playing
	 * 
	 * @return True if the sound is playing
	 */
	public boolean playing() {
		return false;
	}
	
	/**
	 * Stop the sound being played
	 */
	public void stop() {
	}
}
