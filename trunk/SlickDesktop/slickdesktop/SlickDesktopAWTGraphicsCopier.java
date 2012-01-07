/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package slickdesktop;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.nio.ByteBuffer;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.SlickException;

/**
 *
 * @author bjgil
 */
public class SlickDesktopAWTGraphicsCopier
    extends SlickDesktopImageGraphics
{
    private Graphics2D delegate = null;
    
    private java.awt.image.BufferedImage awtImage = null;
    private org.newdawn.slick.Image renderRet = null;

    private ByteBuffer dataBuffer = null;
    private final byte[] data;

    private final Rectangle dirty;
    private final Point translation = new Point();

    private Rectangle imageBounds;

    private Rectangle clip = new Rectangle();
    private Rectangle tmp_dirty = new Rectangle();

    private final Color TRANSPARENT = new Color( 0, 0, 0, 0 );

    private float scaleX = 1;
    private float scaleY = 1;
    
    private boolean subImageSupported = true;
    
    private SlickDesktopAWTGraphicsCopier( BufferedImage awtImage, byte[] data, Graphics2D delegate,
                                org.newdawn.slick.Image image, Rectangle dirty,
                                int translationX, int translationY,
                                float scaleX, float scaleY, boolean subImageSupported ) 
    {
        super( image );
        this.awtImage = awtImage;
        this.data = data;
        this.delegate = delegate;
        this.dirty = dirty;
        translation.x = translationX;
        translation.y = translationY;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.subImageSupported = subImageSupported;
    }

    protected SlickDesktopAWTGraphicsCopier( int width, int height ) 
        throws SlickException
    {
        this( width, height, 1 );
    }
    
    private SlickDesktopAWTGraphicsCopier( int width, int height, float scale ) 
        throws SlickException
    {
        super( new org.newdawn.slick.Image( width, height ) );
        
        awtImage = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR );
        dataBuffer = BufferUtils.createByteBuffer(4 * width * height );
        
        renderRet = new org.newdawn.slick.Image( width, height );
        
        data = (byte[]) awtImage.getRaster().getDataElements( 0, 0,
                awtImage.getWidth(), awtImage.getHeight(), null );

        delegate = (Graphics2D) awtImage.getGraphics();
//        delegate.scale( 0.5, 0.5 );
        
        dirty = new Rectangle( 0, 0, width, height );

        scale( scale, scale );
        setBackground( TRANSPARENT );
    }

    private void makeDirty( int x, int y, int width, int height ) {
        if ( width < 0 ) {
            x = x + width;
            width = -width;
        }
        if ( height < 0 ) {
            y = y + height;
            height = -height;
        }
        tmp_dirty.setBounds( x, y, width, height );
        makeDirty( tmp_dirty );
    }

    private void makeDirty( Rectangle rectangle ) {
        synchronized ( dirty ) {

            getClipBounds( clip );
            
            Rectangle2D.intersect( clip, rectangle, rectangle );
            if ( !rectangle.isEmpty() ) {
                rectangle.x *= scaleX;
                rectangle.y *= scaleY;
                rectangle.width *= scaleX;
                rectangle.height *= scaleY;
                rectangle.translate( translation.x, translation.y );
                Rectangle2D.intersect( rectangle, getImageBounds(), rectangle );
                if ( !rectangle.isEmpty() ) {
                    if ( !dirty.isEmpty() ) {
                        dirty.add( rectangle );
                    }
                    else {
                        dirty.setBounds( rectangle );
                    }
                }
            }
        }
    }

    private void makeDirty() {
        makeDirty( 0, 0, getImage().getWidth(), getImage().getHeight() );
    }

    public java.awt.Graphics create()
    {
        return new SlickDesktopAWTGraphicsCopier( awtImage, data, (Graphics2D)delegate.create(), image, dirty, translation.x, translation.y, scaleX, scaleY, subImageSupported );
    }

    public void dispose()
    {
        delegate.dispose();
    }
    
    public void draw(Shape s)
    {
        synchronized ( dirty ) {
            makeDirty( s.getBounds() );
            delegate.draw( s );
        }
    }

    public boolean drawImage( java.awt.Image img, AffineTransform xform, ImageObserver obs)
    {
        synchronized ( dirty ) {
            makeDirty();
            return delegate.drawImage( img, xform, obs );
        }
    }

    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y)
    {
        synchronized ( dirty ) {
            makeDirty( x, y, img.getWidth( null ), img.getHeight( null ) );
            delegate.drawImage( img, op, x, y );
        }
    }

    public void drawRenderedImage(RenderedImage img, AffineTransform xform)
    {
        synchronized ( dirty ) {
            makeDirty();
            delegate.drawRenderedImage( img, xform );
        }
    }

    public void drawRenderableImage(RenderableImage img, AffineTransform xform)
    {
        synchronized ( dirty ) {
            makeDirty();
            delegate.drawRenderableImage( img, xform );
        }
    }

    public void drawString(String str, int x, int y)
    {
        synchronized ( dirty ) {
            makeDirty();
            delegate.drawString( str, x, y );
        }
    }

    public void drawString(String str, float x, float y)
    {
        synchronized ( dirty ) {
            makeDirty();
            delegate.drawString( str, x, y );
        }
    }

    public void drawString(AttributedCharacterIterator iterator, int x, int y)
    {
        synchronized ( dirty ) {
            makeDirty();
            delegate.drawString( iterator, x, y );
        }
    }

    public void drawString(AttributedCharacterIterator iterator, float x, float y)
    {
        synchronized ( dirty ) {
            makeDirty();
            delegate.drawString( iterator, x, y );
        }
    }

    public void drawGlyphVector(GlyphVector g, float x, float y)
    {
        synchronized ( dirty ) {
            makeDirty();
            delegate.drawGlyphVector( g, x, y );
        }
    }

    public void fill(Shape s)
    {
        synchronized ( dirty ) {
            makeDirty( s.getBounds() );
            delegate.fill( s );
        }
    }

    public boolean hit(Rectangle rect, Shape s, boolean onStroke)
    {
        return delegate.hit( rect, s, onStroke );
    }

    public GraphicsConfiguration getDeviceConfiguration()
    {
        return delegate.getDeviceConfiguration();
    }

    public void setComposite(Composite comp)
    {
        delegate.setComposite( comp );
    }

    public void setPaint(Paint paint)
    {
        delegate.setPaint( paint );
    }

    public void setStroke(Stroke s)
    {
        delegate.setStroke( s );
    }

    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue)
    {
        delegate.setRenderingHint( hintKey, hintValue );
    }

    public Object getRenderingHint(RenderingHints.Key hintKey)
    {
        return delegate.getRenderingHint( hintKey );
    }

    public void setRenderingHints(Map<?, ?> hints)
    {
        delegate.setRenderingHints( hints );
    }

    public void addRenderingHints(Map<?, ?> hints)
    {
        delegate.addRenderingHints( hints );
    }

    public RenderingHints getRenderingHints()
    {
        return delegate.getRenderingHints();
    }

    public void translate(int x, int y)
    {
        delegate.translate( x, y );
    }

    public void translate(double tx, double ty)
    {
        translation.x += tx * scaleX;
        translation.y += ty * scaleY;
        delegate.translate( tx, ty );
    }

    public void rotate(double theta)
    {
        delegate.rotate( theta );
    }

    public void rotate(double theta, double x, double y)
    {
        delegate.rotate( theta, x, y );
    }

    public void scale(double sx, double sy)
    {
        scaleX *= sx;
        scaleY *= sy;
        delegate.scale( sx, sy );
    }

    public void shear(double shx, double shy)
    {
        delegate.shear( shx, shy );
    }

    public void transform(AffineTransform Tx)
    {
        delegate.transform( Tx );
    }

    public void setTransform(AffineTransform Tx)
    {
        delegate.setTransform( Tx );
    }

    public AffineTransform getTransform()
    {
        return delegate.getTransform();
    }

    public Paint getPaint()
    {
        return delegate.getPaint();
    }

    public Composite getComposite()
    {
        return delegate.getComposite();
    }

    public void setBackground(Color color)
    {
        delegate.setBackground( color );
    }

    public Color getBackground()
    {
        return delegate.getBackground();
    }

    public Stroke getStroke()
    {
        return delegate.getStroke();
    }

    public void clip(Shape s)
    {
        delegate.clip( s );
    }

    public FontRenderContext getFontRenderContext()
    {
        return delegate.getFontRenderContext();
    }

    public Color getColor()
    {
        return delegate.getColor();
    }

    public void setColor(Color c)
    {
        delegate.setColor( c );
    }

    public void setPaintMode()
    {
        delegate.setPaintMode();
    }

    public void setXORMode(Color c1)
    {
        delegate.setXORMode( c1 );
    }

    public Font getFont()
    {
        return delegate.getFont();
    }

    public void setFont(Font font)
    {
        delegate.setFont( font );
    }

    public FontMetrics getFontMetrics(Font f)
    {
        return delegate.getFontMetrics( f );
    }

    public Rectangle getClipBounds()
    {
        return delegate.getClipBounds();
    }

    public void clipRect(int x, int y, int width, int height)
    {
        delegate.clipRect( x, y, width, height );
    }

    public void setClip(int x, int y, int width, int height)
    {
        delegate.setClip( x, y, width, height );
    }

    public Shape getClip()
    {
        return delegate.getClip();
    }

    public void setClip(Shape clip)
    {
        delegate.setClip( clip );
    }

    public void copyArea(int x, int y, int width, int height, int dx, int dy)
    {
        synchronized ( dirty ) {
            makeDirty( x + dx, y + dy, width, height );
            delegate.copyArea( x, y, width, height, dx, dy );
        }
    }

    public void drawLine(int x1, int y1, int x2, int y2)
    {
        synchronized ( dirty ) {
            makeDirty( x1, y1, x2 - x1, y2 - y1 );
            delegate.drawLine( x1, y1, x2, y2 );
        }
    }

    public void fillRect(int x, int y, int width, int height)
    {
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            delegate.fillRect( x, y, width, height );
        }
    }

    public void clearRect(int x, int y, int width, int height)
    {
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            //works in JDK1.5:
//            delegate.clearRect( x, y, width, height );

            //fix for bug in JDK1.4:
            Color color = delegate.getColor();
            delegate.setColor( TRANSPARENT );
            Composite composite = delegate.getComposite();
            delegate.setComposite( AlphaComposite.Clear );
            delegate.fillRect( x, y, width, height );
            delegate.setComposite( composite );
            delegate.setColor( color );
        }
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
    {
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            delegate.drawRoundRect( x, y, width, height, arcWidth, arcHeight );
        }
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
    {
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            delegate.fillRoundRect( x, y, width, height, arcWidth, arcHeight );
        }
    }

    public void drawOval(int x, int y, int width, int height)
    {
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            delegate.drawOval( x, y, width, height );
        }
    }

    public void fillOval(int x, int y, int width, int height)
    {
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            delegate.fillOval( x, y, width, height );
        }
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle)
    {
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            delegate.drawArc( x, y, width, height, startAngle, arcAngle );
        }
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle)
    {
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            delegate.fillArc( x, y, width, height, startAngle, arcAngle );
        }
    }

    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints)
    {
        synchronized ( dirty ) {
            makeDirty();
            delegate.drawPolyline( xPoints, yPoints, nPoints );
        }
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints)
    {
        synchronized ( dirty ) {
            makeDirty();
            delegate.drawPolygon( xPoints, yPoints, nPoints );
        }
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints)
    {
        synchronized ( dirty ) {
            makeDirty();
            delegate.fillPolygon( xPoints, yPoints, nPoints );
        }
    }

    public boolean drawImage(java.awt.Image img, int x, int y, ImageObserver observer)
    {
        synchronized ( dirty ) {
            makeDirty( x, y, img.getWidth( observer ), img.getHeight( observer ) );
            return delegate.drawImage( img, x, y, observer );
        }
    }

    public boolean drawImage(java.awt.Image img, int x, int y, int width, int height, ImageObserver observer)
    {
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            return delegate.drawImage( img, x, y, width, height, observer );
        }
    }

    public boolean drawImage(java.awt.Image img, int x, int y, Color bgcolor, ImageObserver observer)
    {
        synchronized ( dirty ) {
            makeDirty( x, y, img.getWidth( observer ), img.getHeight( observer ) );
            return delegate.drawImage( img, x, y, bgcolor, observer );
        }
    }

    public boolean drawImage(java.awt.Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer)
    {
        synchronized ( dirty ) {
            makeDirty( x, y, width, height );
            return delegate.drawImage( img, x, y, width, height, bgcolor, observer );
        }
    }

    public boolean drawImage(java.awt.Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer)
    {
        synchronized ( dirty ) {
            makeDirty( dx1, dy1, dx2 - dx1, dy2 - dy1 );
            return delegate.drawImage( img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer );
        }
    }

    public boolean drawImage(java.awt.Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer)
    {
        synchronized ( dirty ) {
            makeDirty( dx1, dy1, dx2 - dx1, dy2 - dy1 );
            return delegate.drawImage( img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer );
        }
    }

    public boolean isDirty()
    {
        return !dirty.isEmpty();
    }

    private Rectangle getImageBounds() 
    {
        if ( imageBounds == null ) 
        {
            imageBounds = new Rectangle( 0, 0, getImage().getWidth(), getImage().getHeight() );
        }
        return imageBounds;
    }

    private int error = GL11.GL_NO_ERROR;
    public void renderToGraphics( org.newdawn.slick.Graphics g )
    {
        renderRet.getTexture().bind();
        GL11.glPixelStorei( GL11.GL_UNPACK_ALIGNMENT, 1 );
        synchronized ( dirty ) {
            awtImage.getRaster().getDataElements( dirty.x, dirty.y, dirty.width, dirty.height, data );
            ByteBuffer scratch = dataBuffer;
            scratch.clear();
            scratch.put( data );
            scratch.flip();
            if( !subImageSupported )
            {
                GL11.glTexImage2D( GL11.GL_TEXTURE_2D, 0,
                        GL11.GL_RGBA8, renderRet.getWidth(),
                        renderRet.getHeight(), 0, GL11.GL_RGBA,
                        GL11.GL_UNSIGNED_BYTE, scratch );
            }
            else
            {
                //debug: check if we already have an error from previous operations
                GL11.glTexSubImage2D( GL11.GL_TEXTURE_2D, 0,
                        dirty.x, dirty.y, dirty.width,
                        dirty.height, GL11.GL_RGBA,
                        GL11.GL_UNSIGNED_BYTE, scratch );
                error = GL11.glGetError();
                if( error != GL11.GL_NO_ERROR )
                {
                    subImageSupported = false;
                }
            }
        }
        renderRet.getTexture().getLastBind().bind();
    }

    public org.newdawn.slick.Image render( org.newdawn.slick.Graphics g, boolean clean)
    {
        boolean updateChildren = false;
        synchronized ( dirty ) {
            if ( !dirty.isEmpty() ) {
                dirty.grow( 2, 2 ); // to prevent antialiasing problems
            }
            Rectangle2D.intersect( dirty, getImageBounds(), dirty );

            if ( !this.dirty.isEmpty() ) {
                renderToGraphics( g );
            }
        }

        if ( clean ) {
            this.dirty.width = 0;
        }
        
        return renderRet;
    }
}
