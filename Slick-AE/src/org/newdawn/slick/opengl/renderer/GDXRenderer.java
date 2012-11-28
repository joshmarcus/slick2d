package org.newdawn.slick.opengl.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.utils.BufferUtils;

/**
 * A renderer that caches all operations into an array, creates an opengl vertex array when
 * required and spits the data down to the card in batch mode
 * 
 * Based on the LIBGDX renderer
 * 
 * @author kevin
 */
public class GDXRenderer implements SGL {
	/**
	 * Allocate a simple float buffer
	 * 
	 * @param numFloats The number of floats to allocate
	 * @return The buffer created
	 */
    private static FloatBuffer allocateBuffer (int numFloats) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(numFloats * 4);
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asFloatBuffer();
    }

	/** Indicates there is no current geometry buffer */
	public static final int NONE = -1;
	/** The maximum number of vertices draw in one batch */
	public static final int MAX_VERTS = 5000;
	
	/** The type of the geometry array currently being built - i.e. GL_QUADS */
	private int currentType = NONE;
	/** The last colour applied */
	private float[] color = new float[] {1f,1f,1f,1f};
	/** The last texture applied */
	private float[] tex = new float[] {0f,0f};
	/** The index of the next vertex to be created */
	private int vertIndex;
	
	/** The vertex data cached */
	private float[] verts = new float[MAX_VERTS*3];
	/** The vertex colour data cached */
	private float[] cols = new float[MAX_VERTS*4];
	/** The vertex texture coordinate data cached */
	private float[] texs = new float[MAX_VERTS*3];
	
	/** The buffer used to pass the vertex data to the card */
	private FloatBuffer vertices = allocateBuffer(MAX_VERTS * 3);
	/** The buffer used to pass the vertex color data to the card */
	private FloatBuffer colors = allocateBuffer(MAX_VERTS * 4);
	/** The buffer used to pass the vertex texture coordinate data to the card */
	private FloatBuffer textures = allocateBuffer(MAX_VERTS * 2);
	
	/** The GL context from GDX */
	private GL10 gl;
	
	/** The width of the context */
	private int width;
	/** The height of the context */
	private int height;
	
	/** The offset into a quad used to index the triangles */
	private int quadOffset;
	
	/*
	 * (non-Javadoc)
	 * @see org.newdawn.slick.opengl.renderer.SGL#initDisplay(int, int)
	 */
	public void initDisplay(int width, int height) {
		this.width = width;
		this.height = height;
		
        gl = Gdx.app.getGraphics().getGL10();
		
		String extensions = gl.glGetString(GL10.GL_EXTENSIONS);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glShadeModel(GL10.GL_SMOOTH);        
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_LIGHTING);                    
        
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);                   
        
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		gl.glViewport(0,0,width,height);
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		startBuffer();
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#enterOrtho(int, int)
	 */
	public void enterOrtho(int xsize, int ysize) {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0, xsize, ysize, 0, 1, -1);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		gl.glTranslatef((width-xsize)/2,
						  (height-ysize)/2,0);
	}
	
	/**
	 * Start a new buffer for a vertex array 
	 */
	private void startBuffer() {
		vertIndex = 0;
	}
	
	/**
	 * Flush the currently cached data down to the card 
	 */
	private void flushBuffer() {	
		if (vertIndex == 0) {
			return;
		}
		if (currentType == NONE) {
			return;
		}
		
		BufferUtils.copy(verts, vertices, vertIndex*3, 0);
		BufferUtils.copy(cols, colors, vertIndex*4, 0);
		BufferUtils.copy(texs, textures, vertIndex*2, 0);
		
		gl.glVertexPointer(3,GL10.GL_FLOAT,0,vertices);     
		gl.glColorPointer(4,GL10.GL_FLOAT,0,colors);     
		gl.glTexCoordPointer(2,GL10.GL_FLOAT,0,textures);     
		
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vertIndex);
		currentType = NONE;
		quadOffset = 0;
	}
	
	/**
	 * Apply the current buffer and restart it
	 */
	private void applyBuffer() {
		if (vertIndex != 0) {
			flushBuffer();
			startBuffer();
		}
		
		gl.glColor4f(color[0], color[1], color[2], color[3]);
	}
	
	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#flush()
	 */
	public void flush() {
		applyBuffer();
	}
	
	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glBegin(int)
	 */
	public void glBegin(int geomType) {
		if (currentType != geomType) {
			applyBuffer();
			currentType = geomType;
		}
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glColor4f(float, float, float, float)
	 */
	public void glColor4f(float r, float g, float b, float a) {
		color[0] = r;
		color[1] = g;
		color[2] = b;
		color[3] = a;
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glEnd()
	 */
	public void glEnd() {
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glTexCoord2f(float, float)
	 */
	public void glTexCoord2f(float u, float v) {
		tex[0] = u;
		tex[1] = v;
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glVertex2f(float, float)
	 */
	public void glVertex2f(float x, float y) {
		glVertex3f(x,y,0);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glVertex3f(float, float, float)
	 */
	public void glVertex3f(float x, float y, float z) {
		if (currentType != SGL.GL_QUADS) {
			glVertex3f(vertIndex, x, y, z);
			vertIndex++;
		} else {
			if (quadOffset == 0) {
				glVertex3f(vertIndex, x, y, z);
				glVertex3f(vertIndex+5, x, y, z);
				vertIndex++;
			}
			if (quadOffset == 1) {
				glVertex3f(vertIndex, x, y, z);
				vertIndex++;
			}
			if (quadOffset == 2) {
				glVertex3f(vertIndex, x, y, z);
				glVertex3f(vertIndex+2, x, y, z);
				vertIndex++;
			}
			if (quadOffset == 3) {
				glVertex3f(vertIndex, x, y, z);
				vertIndex += 3;
			}
			
			quadOffset++;
			if (quadOffset > 3) {
				quadOffset = 0;
			}
		}
		
		if (vertIndex > MAX_VERTS - 50) {
			if (isSplittable(vertIndex, currentType)) {
				int type = currentType;
				applyBuffer();
				currentType = type;
			}
		}
	}
	
	public void glVertex3f(int vertIndex, float x, float y, float z) {
		verts[(vertIndex*3)+0] = x;
		verts[(vertIndex*3)+1] = y;
		verts[(vertIndex*3)+2] = z;
		cols[(vertIndex*4)+0] = color[0];
		cols[(vertIndex*4)+1] = color[1];
		cols[(vertIndex*4)+2] = color[2];
		cols[(vertIndex*4)+3] = color[3];
		texs[(vertIndex*2)+0] = tex[0];
		texs[(vertIndex*2)+1] = tex[1];
	}

	/**
	 * Check if the geometry being created can be split at the current index
	 * 
	 * @param count The current index
	 * @param type The type of geometry being built
	 * @return True if the geometry can be split at the current index
	 */
	private boolean isSplittable(int count, int type) {
		switch (type) {
		case SGL.GL_TRIANGLES:
			return count % 3 == 0;
		case SGL.GL_LINES:
			return count % 2 == 0;
		case SGL.GL_QUADS:
			return count % 4 == 0;
		}
		
		return false;
	}
	
	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glBindTexture(int, int)
	 */
	public void glBindTexture(int target, int id) {
		applyBuffer();
		gl.glBindTexture(target, id);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glBlendFunc(int, int)
	 */
	public void glBlendFunc(int src, int dest) {
		applyBuffer();
		gl.glBlendFunc(src, dest);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glCallList(int)
	 */
	public void glCallList(int id) {
		applyBuffer();
		throw new RuntimeException("Unsupported: glCallList");
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glClear(int)
	 */
	public void glClear(int value) {
		applyBuffer();
		gl.glClear(value);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glClipPlane(int, java.nio.DoubleBuffer)
	 */
	public void glClipPlane(int plane, DoubleBuffer buffer) {
		applyBuffer();
		throw new RuntimeException("Unsupported: glClipPlane");
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glColorMask(boolean, boolean, boolean, boolean)
	 */
	public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		applyBuffer();
		gl.glColorMask(red, green, blue, alpha);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glDisable(int)
	 */
	public void glDisable(int item) {
		applyBuffer();
		gl.glDisable(item);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glEnable(int)
	 */
	public void glEnable(int item) {
		applyBuffer();
		gl.glEnable(item);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glLineWidth(float)
	 */
	public void glLineWidth(float width) {
		applyBuffer();
		gl.glLineWidth(width);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glPointSize(float)
	 */
	public void glPointSize(float size) {
		applyBuffer();
		gl.glPointSize(size);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glPopMatrix()
	 */
	public void glPopMatrix() {
		applyBuffer();
		gl.glPopMatrix();
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glPushMatrix()
	 */
	public void glPushMatrix() {
		applyBuffer();
		gl.glPushMatrix();
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glRotatef(float, float, float, float)
	 */
	public void glRotatef(float angle, float x, float y, float z) {
		applyBuffer();
		gl.glRotatef(angle, x, y, z);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glScalef(float, float, float)
	 */
	public void glScalef(float x, float y, float z) {
		applyBuffer();
		gl.glScalef(x, y, z);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glScissor(int, int, int, int)
	 */
	public void glScissor(int x, int y, int width, int height) {
		applyBuffer();
		gl.glScissor(x, y, width, height);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glTexEnvi(int, int, int)
	 */
	public void glTexEnvi(int target, int mode, int value) {
		applyBuffer();
		gl.glTexEnvf(target, mode, value);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glTranslatef(float, float, float)
	 */
	public void glTranslatef(float x, float y, float z) {
		applyBuffer();
		gl.glTranslatef(x, y, z);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glEndList()
	 */
	public void glEndList() {
		throw new RuntimeException("Unsupported: glEndList");
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.ImmediateModeOGLRenderer#glNewList(int, int)
	 */
	public void glNewList(int id, int option) {
		throw new RuntimeException("Unsupported: glNewList");
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#getCurrentColor()
	 */
	public float[] getCurrentColor() {
		return color;
	}
	
	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glLoadMatrix(java.nio.FloatBuffer)
	 */
	public void glLoadMatrix(FloatBuffer buffer) {
		flushBuffer();
		gl.glLoadMatrixf(buffer);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glClearColor(float, float, float, float)
	 */
	public void glClearColor(float red, float green, float blue, float alpha) {
		gl.glClearColor(red, green, blue, alpha);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glCopyTexImage2D(int, int, int, int, int, int, int, int)
	 */
	public void glCopyTexImage2D(int target, int level, int internalFormat, int x, int y, int width, int height, int border) {
		gl.glCopyTexImage2D(target, level, internalFormat, x, y, width, height, border);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glDeleteTextures(java.nio.IntBuffer)
	 */
	public void glDeleteTextures(IntBuffer buffer) {
		gl.glDeleteTextures(buffer.remaining(), buffer);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glGenLists(int)
	 */
	public int glGenLists(int count) {
		fail("Unsupported: glGenLists");
		return 0;
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glGetFloat(int, java.nio.FloatBuffer)
	 */
	public void glGetFloat(int id, FloatBuffer ret) {
		fail("Unsupported: glGetFloat");
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glGetInteger(int, java.nio.IntBuffer)
	 */
	public void glGetInteger(int id, IntBuffer ret) {
		gl.glGetIntegerv(id, ret);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glGetTexImage(int, int, int, int, java.nio.ByteBuffer)
	 */
	public void glGetTexImage(int target, int level, int format, int type, ByteBuffer pixels) {
		fail("Unsupported: glGetTexImage");
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glLoadIdentity()
	 */
	public void glLoadIdentity() {
		gl.glLoadIdentity();
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glReadPixels(int, int, int, int, int, int, java.nio.ByteBuffer)
	 */
	public void glReadPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) {
		gl.glReadPixels(x, y, width, height, format, type, pixels);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glTexParameteri(int, int, int)
	 */
	public void glTexParameteri(int target, int param, int value) {
		gl.glTexParameterf(target, param, value);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glDeleteLists(int, int)
	 */
	public void glDeleteLists(int list, int count) {
		fail("Unsupported: glDeleteLists");
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glClearDepth(float)
	 */
	public void glClearDepth(float value) {
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glDepthFunc(int)
	 */
	public void glDepthFunc(int func) {
		gl.glDepthFunc(func);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glDepthMask(boolean)
	 */
	public void glDepthMask(boolean mask) {
		gl.glDepthMask(mask);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#setGlobalAlphaScale(float)
	 */
	public void setGlobalAlphaScale(float alphaScale) {
	}

	/**
	 * Log a failure message
	 * 
	 * @param message The message to log
	 */
	private void fail(String message) {
		Log.error("Fail: "+message);
	}

	/*
	 * (non-Javadoc)
	 * @see org.newdawn.slick.opengl.renderer.SGL#glGenTextures(java.nio.IntBuffer)
	 */
	@Override
	public void glGenTextures(IntBuffer ids) {
		gl.glGenTextures(ids.remaining(), ids);
	}

	/*
	 * (non-Javadoc)
	 * @see org.newdawn.slick.opengl.renderer.SGL#glGetError()
	 */
	@Override
	public void glGetError() {
		gl.glGetError();
	}

	/*
	 * (non-Javadoc)
	 * @see org.newdawn.slick.opengl.renderer.SGL#glTexImage2D(int, int, int, int, int, int, int, int, java.nio.ByteBuffer)
	 */
	@Override
	public void glTexImage2D(int target, int i, int dstPixelFormat,
			int width, int height, int j, int srcPixelFormat,
			int glUnsignedByte, ByteBuffer textureBuffer) {
		gl.glTexImage2D(target, i, dstPixelFormat, width, height, j, srcPixelFormat,
				glUnsignedByte, textureBuffer);
	}

	/*
	 * (non-Javadoc)
	 * @see org.newdawn.slick.opengl.renderer.SGL#canSecondaryColor()
	 */
	@Override
	public boolean canSecondaryColor() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.newdawn.slick.opengl.renderer.SGL#canTextureMirrorClamp()
	 */
	@Override
	public boolean canTextureMirrorClamp() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.newdawn.slick.opengl.renderer.SGL#glSecondaryColor3ubEXT(byte, byte, byte)
	 */
	@Override
	public void glSecondaryColor3ubEXT(byte b, byte c, byte d) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.newdawn.slick.opengl.renderer.SGL#glTexSubImage2D(int, int, int, int, int, int, int, int, java.nio.ByteBuffer)
	 */
	@Override
	public void glTexSubImage2D(int glTexture2d, int i, int pageX, int pageY,
			int width, int height, int glBgra, int glUnsignedByte,
			ByteBuffer scratchByteBuffer) {
		gl.glTexSubImage2D(glTexture2d,i,pageX,pageY,width,height,glBgra,glUnsignedByte,scratchByteBuffer);
	}
}
