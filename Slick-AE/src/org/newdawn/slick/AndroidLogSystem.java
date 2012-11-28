package org.newdawn.slick;

import org.newdawn.slick.util.LogSystem;

import android.util.Log;

public class AndroidLogSystem implements LogSystem {

	@Override
	public void debug(String message) {
		Log.d("SLICK", message);
	}

	@Override
	public void error(String message, Throwable e) {
		Log.e("SLICK", message, e);
	}

	@Override
	public void error(Throwable e) {
		Log.e("SLICK", "", e);
	}

	@Override
	public void error(String message) {
		Log.e("SLICK", message);
	}

	@Override
	public void info(String message) {
		Log.i("SLICK", message);
	}

	@Override
	public void warn(String message) {
		Log.w("SLICK", message);
	}

	@Override
	public void warn(String message, Throwable e) {
		Log.w("SLICK", message, e);
	}

}
