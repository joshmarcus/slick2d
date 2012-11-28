package org.newdawn.slick;

import java.net.URL;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files.FileType;

/**
 * A piece of music loaded and playable within the game. Only one piece of music can
 * play at any given time and a channel is reserved so music will always play. 
 *
 * @author kevin
 */
public class Music {
	/** The underling GDX audio stream */
	private com.badlogic.gdx.audio.Music music;
	
	/**
	 * Create and load a piece of music (either OGG or MOD/XM)
	 * 
	 * @param ref The location of the music
	 * @throws SlickException
	 */
	public Music(String ref) throws SlickException {
		this(ref, false);
	}

	/**
	 * Create and load a piece of music (either OGG or MOD/XM)
	 * 
	 * @param ref The location of the music
	 * @throws SlickException
	 */
	public Music(URL ref) throws SlickException {
		this(ref, false);
	}
	
	/**
	 * Create and load a piece of music (either OGG or MOD/XM)
	 * 
	 * @param url The location of the music
	 * @param streamingHint A hint to indicate whether streaming should be used if possible
	 * @throws SlickException
	 */
	public Music(URL url, boolean streamingHint) throws SlickException {
		throw new RuntimeException("URL sound loading not supported in Slick-AE");
	}
	
	/**
	 * Create and load a piece of music (either OGG or MOD/XM)
	 * 
	 * @param ref The location of the music
	 * @param streamingHint A hint to indicate whether streaming should be used if possible
	 * @throws SlickException
	 */
	public Music(String ref, boolean streamingHint) throws SlickException {
		Gdx.audio.newMusic(Gdx.files.getFileHandle(ref, FileType.Internal));
	}

	/**
	 * Add a listener to this music
	 * 
	 * @param listener The listener to add
	 */
	public void addListener(MusicListener listener) {
	}

	/**
	 * Remove a listener from this music
	 * 
	 * @param listener The listener to remove
	 */
	public void removeListener(MusicListener listener) {
	}

	/**
	 * Loop the music
	 */
	public void loop() {
		loop(1.0f, 1.0f); 
	}
	
	/**
	 * Play the music
	 */
	public void play() {
		play(1.0f, 1.0f); 
	}

	/**
	 * Play the music at a given pitch and volume
	 * 
	 * @param pitch The pitch to play the music at (1.0 = default)
	 * @param volume The volume to play the music at (1.0 = default)
	 */
	public void play(float pitch, float volume) {
		music.setLooping(false);
		music.play();
	}

	/**
	 * Loop the music at a given pitch and volume
	 * 
	 * @param pitch The pitch to play the music at (1.0 = default)
	 * @param volume The volume to play the music at (1.0 = default)
	 */
	public void loop(float pitch, float volume) {
		music.setLooping(true);
		music.play();
	}
	
	/**
	 * Pause the music playback
	 */
	public void pause() {
		music.pause();
	}
	
	/**
	 * Stop the music playing
	 */
	public void stop() {
		music.stop();
	}
	
	/**
	 * Resume the music playback
	 */
	public void resume() {
		music.play();
	}
	
	/**
	 * Check if the music is being played
	 * 
	 * @return True if the music is being played
	 */
	public boolean playing() {
		return music.isPlaying();
	}
	
	/**
	 * Set the volume of the music as a factor of the global volume setting
	 * 
	 * @param volume The volume to play music at. 0 - 1, 1 is Max
	 */
	public void setVolume(float volume) {
		music.setVolume(volume);
	}

	/**
	 * Get the individual volume of the music
	 * @return The volume of this music, still effected by global SoundStore volume. 0 - 1, 1 is Max
	 */
	public float getVolume() {
		return 1;
	}

	/**
	 * Fade this music to the volume specified
	 * 
	 * @param duration Fade time in milliseconds.
	 * @param endVolume The target volume
	 * @param stopAfterFade True if music should be stopped after fading in/out
	 */
	public void fade (int duration, float endVolume, boolean stopAfterFade) {
	}

	/**
	 * Seeks to a position in the music. For streaming music, seeking before the current position causes 
	 * the stream to be reloaded.
	 * 
	 * @param position Position in seconds.
	 * @return True if the seek was successful
	 */
	public boolean setPosition(float position) {
		return false;
	}

	/**
	 * The position into the sound thats being played
	 * 
	 * @return The current position in seconds.
	 */
	public float getPosition () {
		return 0;
	}
}
