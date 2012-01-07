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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

/**
 *
 * @author bjgil
 */
public class SlickDesktop
    implements org.newdawn.slick.InputListener
{
    private SlickDesktopImageGraphics graphics;
    private JDesktopPane desktop;
    private org.newdawn.slick.Image texture;
    private boolean initialized;
    private int width;
    private int height;
    
    private boolean showingJFrame = false;
    private final Frame awtWindow;
    private int desktopWidth;
    private int desktopHeight;

    private boolean synchronizingThreadsOnUpdate;

    private static int desktopsUsed = 0;

    private final LockRunnable paintLockRunnable = new LockRunnable();

    /**
     * @see #setShowingJFrame
     * @return true if frame is displayed
     */
    public boolean isShowingJFrame() {
        return showingJFrame;
    }

    /**
     * @param showingJFrame true to display the desktop in a JFrame instead on this quad.
     * @deprecated for debuggin only
     */
    public void setShowingJFrame( boolean showingJFrame ) {
        this.showingJFrame = showingJFrame;
        awtWindow.setVisible( showingJFrame );
        awtWindow.repaint();
    }

    public SlickDesktop( String name ) {
//        super( name );

//        inputHandler = new InputHandler();

        awtWindow = new Frame() {
            private static final long serialVersionUID = 1L;
            public boolean isShowing() {
                return true;
            }

            public boolean isVisible() {
                if ( awtWindow.isFocusableWindow()
                        && new Throwable().getStackTrace()[1].getMethodName().startsWith( "requestFocus" ) ) {
                    return false;
                }
                return initialized || super.isVisible();
            }

            public Graphics getGraphics() {
                if ( !showingJFrame ) {
                    return graphics == null ? super.getGraphics() : graphics.create();
                }
                
                return super.getGraphics();
            }

            public boolean isFocused() {
                return true;
            }
        };
        awtWindow.setFocusableWindowState( false );
        Container contentPane = awtWindow;
        awtWindow.setUndecorated( true );
        dontDrawBackground( contentPane );

        desktop = new JDesktopPane() {
            private static final long serialVersionUID = 1L;
            public void paint( Graphics g ) {
                if ( !isShowingJFrame() ) {
                    g.clearRect( 0, 0, getWidth(), getHeight() );
                }
                super.paint( g );
            }

            public boolean isOptimizedDrawingEnabled() {
                return false;
            }
        };

//        desktop.setDragMode( JDesktopPane.OUTLINE_DRAG_MODE );
        
        new ScrollPaneRepaintFixListener().addTo( desktop );


        final Color transparent = new Color( 0, 0, 0, 0 );
        desktop.setBackground( transparent );
        desktop.setFocusable( true );
        desktop.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                desktop.requestFocusInWindow();
            }
        } );

        // this internal frame is a workaround for key binding problems in JDK1.5
        // todo: this workaround does not seem to work on mac
        if ( System.getProperty( "os.name" ).toLowerCase().indexOf( "mac" ) < 0 ) {
            final JInternalFrame internalFrame = new JInternalFrame();
            internalFrame.setUI( new BasicInternalFrameUI( internalFrame ) {
                protected void installComponents() {
                }
            } );
            internalFrame.setOpaque( false );
            internalFrame.setBackground( null );
            internalFrame.getContentPane().setLayout( new BorderLayout() );
            internalFrame.getContentPane().add( desktop, BorderLayout.CENTER );
            internalFrame.setVisible( true );
            internalFrame.setBorder( null );
            contentPane.add( internalFrame );
        }
        else {
            // this would have suited for JDK1.4:
            contentPane.add( desktop, BorderLayout.CENTER );
        }

        awtWindow.pack();

        RepaintManager.currentManager( null ).setDoubleBufferingEnabled( false );
    }
    
    public SlickDesktop( String name, final int width, final int height ) {
        this( name, width, height, false );
    }
    
    public SlickDesktop( String name, final int width, final int height, boolean mipMapping ) {
        this( name );

        setup( width, height );
    }
    
    public void setup( int width, int height ) {
        if ( initialized ) {
            throw new IllegalStateException( "may be called only once" );
        }

        this.width = powerOf2SizeIfNeeded( width );
        this.height = powerOf2SizeIfNeeded( height );

        desktop.setPreferredSize( new Dimension( width, height ) );
        desktopWidth = width;
        desktopHeight = height;
        awtWindow.pack();

        graphics = SlickDesktopImageGraphics.createInstance( this.width, this.height/*, mipMapping ? 2 : 0*/ );
        if( graphics == null )
        {
            throw new RuntimeException( "SD2_ImageGraphics - could not instantiate graphics object" );   
        }

        if ( desktopsUsed == 0 ) {
            PopupFactory.setSharedInstance( new MyPopupFactory() );
        }
        desktopsUsed++;

        this.setFocusOwner( desktop );

        initialized = true;

        setSynchronizingThreadsOnUpdate( true );
        
        desktop.repaint();
    }

    /**
     * @return true if update and swing thread should be synchronized (avoids flickering, eats some performance)
     */
    public boolean isSynchronizingThreadsOnUpdate() {
        return synchronizingThreadsOnUpdate;
    }

    /**
     * Choose if update and swing thread should be synchronized (avoids flickering, eats some performance)
     *
     * @param synchronizingThreadsOnUpdate true to synchronize
     */
    public void setSynchronizingThreadsOnUpdate( boolean synchronizingThreadsOnUpdate ) {
        if ( this.synchronizingThreadsOnUpdate != synchronizingThreadsOnUpdate ) {
            this.synchronizingThreadsOnUpdate = synchronizingThreadsOnUpdate;
        }
    }

    private void enableAntiAlias( Graphics2D graphics ) {
        RenderingHints hints = graphics.getRenderingHints();
        if ( hints == null ) {
            hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        }
        else {
            hints.put( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        }
        graphics.setRenderingHints( hints );
    }

    private static void dontDrawBackground( Container container ) {
        if ( container != null ) {
            container.setBackground( null );
            if ( container instanceof JComponent ) {
                final JComponent component = ( (JComponent) container );
                component.setOpaque( false );
            }
            dontDrawBackground( container.getParent() );
        }
    }

    private static int powerOf2SizeIfNeeded( int size/*, boolean generateMipMaps*/ ) {
        int powerOf2Size = 1;
        while ( powerOf2Size < size ) {
            powerOf2Size <<= 1;
        }
        IntBuffer temp = BufferUtils.createIntBuffer(16);
        GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE, temp);
        int max = temp.get(0);
        if( powerOf2Size > max )
        {
            powerOf2Size = max;
        }
        return powerOf2Size;
    }
    
    private org.newdawn.slick.Image ret = null;
    
    public org.newdawn.slick.Image render( GameContainer gameContainer, org.newdawn.slick.Graphics g ) {
        if ( graphics.isDirty() ) {
            ret = null;
            final boolean synchronizingThreadsOnUpdate = this.synchronizingThreadsOnUpdate;
            if ( synchronizingThreadsOnUpdate ) {
                synchronized ( paintLockRunnable ) {
                    try {
                        paintLockRunnable.wait = true;
                        SwingUtilities.invokeLater( paintLockRunnable );
                        paintLockRunnable.wait( 100 );
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                if ( graphics != null ) {
                    ret = graphics.render( g, false );
                }
            } finally {

                if ( synchronizingThreadsOnUpdate ) {
                    synchronized ( paintLockRunnable ) {
                        paintLockRunnable.notifyAll();
                    }
                }
            }
        }
        return ret;
    }

    public JDesktopPane getJDesktop() {
        return desktop;
    }

    public Component getFocusOwner() {
        if ( !focusCleared ) {
            return this.awtWindow.getFocusOwner();
        }
        
        return null;
    }

    private class LockRunnable implements Runnable {
        private boolean wait = false;

        public void run() {
            synchronized ( paintLockRunnable ) {
                notifyAll();
                if ( wait ) {
                    try {
                        //wait for repaint to finish
                        wait = false;
                        paintLockRunnable.wait( 200 );
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Call this method of the desktop is no longer needed. Removes this from the scenegraph, later use is not
     * possible any more.
     */
    public void dispose() {
        if ( desktop != null ) {
            try {
                SwingUtilities.invokeAndWait( new Runnable() {
                    public void run() {
                        desktop.removeAll();
                        awtWindow.dispose();
                    }
                } );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            } catch ( InvocationTargetException e ) {
                e.printStackTrace();
            }
            desktop = null;
            desktopsUsed--;
            if ( desktopsUsed == 0 ) {
                PopupFactory.setSharedInstance( new PopupFactory() );
            }
        }
    }

    private Component lastComponent;
    private Component grabbedMouse;
    private int grabbedMouseButton;
    private int downX = 0;
    private int downY = 0;
    private long lastClickTime = 0;
    private int clickCount = 0;
    private static final int MAX_CLICKED_OFFSET = 4;

    public Component componentAt( int x, int y ) {
        Component component = componentAt( x, y, desktop, true );
        if ( component != desktop ) {
            return component;
        }
       
        return null;        
    }

    private Component componentAt( int x, int y, Component parent, boolean scanRootPanes ) {
        if ( scanRootPanes && parent instanceof JRootPane ) {
            JRootPane rootPane = (JRootPane) parent;
            parent = rootPane.getContentPane();
        }

        Component child = parent;
        if ( !parent.contains( x, y ) ) {
            child = null;
        }
        else {
            synchronized ( parent.getTreeLock() ) {
                if ( parent instanceof Container ) {
                    Container container = (Container) parent;
                    int ncomponents = container.getComponentCount();
                    for ( int i = 0; i < ncomponents; i++ ) {
                        Component comp = container.getComponent( i );
                        if ( comp != null
                                && comp.isVisible()
                                && comp.contains( x - comp.getX(), y - comp.getY() ) ) {
                            child = comp;
                            break;
                        }
                    }
                }
            }
        }

        if ( child != null ) {
            if ( parent instanceof JTabbedPane && child != parent ) {
                child = ( (JTabbedPane) parent ).getSelectedComponent();
            }
            x -= child.getX();
            y -= child.getY();
        }
        return child != parent && child != null ? componentAt( x, y, child, scanRootPanes ) : child;
    }

    private boolean focusCleared = false;

    public void setFocusOwner( Component comp ) {
        if ( comp == null || comp.isFocusable() ) {
            for ( Component p = comp; p != null; p = p.getParent() ) {
                if ( p instanceof JInternalFrame ) {
                    try {
                        ( (JInternalFrame) p ).setSelected( true );
                    } catch ( PropertyVetoException e ) {
                        e.printStackTrace();
                    }
                }
            }
            awtWindow.setFocusableWindowState( true );
            Component oldFocusOwner = getFocusOwner();
            if ( comp == desktop ) {
                comp = null;
            }
            if ( oldFocusOwner != comp ) {
                if ( oldFocusOwner != null ) {
                    dispatchEvent( oldFocusOwner, new FocusEvent( oldFocusOwner,
                            FocusEvent.FOCUS_LOST, false, comp ) );
                }
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
                if ( comp != null ) {
                    dispatchEvent( comp, new FocusEvent( comp,
                            FocusEvent.FOCUS_GAINED, false, oldFocusOwner ) );
                }
            }
            awtWindow.setFocusableWindowState( false );
        }
        focusCleared = comp == null;
    }

    private void dispatchEvent( final Component receiver, final AWTEvent event ) {
        if ( getModalComponent() == null || SwingUtilities.isDescendingFrom( receiver, getModalComponent() ) ) {
            if ( !SwingUtilities.isEventDispatchThread() ) {
                throw new IllegalStateException( "not in swing thread!" );
            }
            receiver.dispatchEvent( event );
        }
    }

    private Input input = null;
    
    public void setInput(Input input)
    {
        this.input = input;
        input.addListener( this );
    }

    public boolean isAcceptingInput()
    {
        return !showingJFrame;
    }

    public void inputEnded()
    {
    }

    public void keyPressed(int keyCode, char character)
    {
        createKeyEvent( keyCode, character, true );
    }

    public void keyReleased(int keyCode, char character)
    {
        createKeyEvent( keyCode, character, false );
    }

    private void createKeyEvent( final int keyCode, final char character, final boolean pressed )
    {
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    sendAWTKeyEvent( keyCode, pressed, character );
                }
            } );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
        }
    }
    
    private static Int anInt = new Int( 0 );

    private static class Int {
        public Int( int value ) {
            this.value = value;
        }

        public boolean equals( Object obj ) {
            return obj instanceof Int && ( (Int) obj ).value == value;

        }

        public int hashCode() {
            return value;
        }

        int value;
    }

    private static class Char {
        public Char( char value ) {
            this.value = value;
        }

        char value;
    }

    /**
     * From keyCode (Int) to character (Char)
     */
    private Map<Int,Char> characters = new HashMap<Int,Char>();

    private void sendAWTKeyEvent( int keyCode, boolean pressed, char character ) {
        keyCode = AWTKeyInput.toAWTCode( keyCode );
        if ( keyCode != 0 ) {
            Component focusOwner = getFocusOwner();
            if ( focusOwner == null ) {
                focusOwner = desktop;
            }
            if ( character == '\0' ) {
                character = KeyEvent.CHAR_UNDEFINED;
            }
            if ( focusOwner != null ) {
                if ( pressed ) {
                    KeyEvent event = new KeyEvent( focusOwner, KeyEvent.KEY_PRESSED,
                            System.currentTimeMillis(), getCurrentModifiers( -1 ),
                            keyCode, character );
                    dispatchEvent( focusOwner, event );
                    anInt.value = keyCode;
                    Char c = characters.get( anInt );
                    if ( c == null ) {
                        characters.put( new Int( keyCode ), new Char( character ) );
                    }
                    else {
                        c.value = character;
                    }
                    if ( character != KeyEvent.CHAR_UNDEFINED ) {
                        dispatchEvent( focusOwner, new KeyEvent( focusOwner, KeyEvent.KEY_TYPED,
                                System.currentTimeMillis(), getCurrentModifiers( -1 ),
                                0, character ) );
                    }
                }
                if ( !pressed ) {
                    anInt.value = keyCode;
                    Char c = characters.get( anInt );
                    if ( c != null ) {
                        character = c.value;
                        //TODO: repeat input
//                        if ( character != KeyEvent.CHAR_UNDEFINED ) {
//                            dispatchEvent( focusOwner, new KeyEvent( focusOwner, KeyEvent.KEY_TYPED,
//                                    System.currentTimeMillis(), getCurrentModifiers( -1 ),
//                                    0, character ) );
//                        }
                    }
                    dispatchEvent( focusOwner, new KeyEvent( focusOwner, KeyEvent.KEY_RELEASED,
                            System.currentTimeMillis(), getCurrentModifiers( -1 ),
                            keyCode, character ) );
                }
            }
        }
    }

    public void mouseWheelMoved(int i)
    {
    }

    public void mousePressed(int button, int x, int y)
    {
        createMouseEvent( button, x, y, true );
    }

    public void mouseReleased(int button, int x, int y)
    {
        createMouseEvent( button, x, y, false );
    }
    
    private void createMouseEvent( final int button, final int x, final int y, final boolean pressed )
    {
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    sendAWTMouseEvent( x, y, pressed, button + 1 );
                }
            } );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
        }
    }
    
    private boolean useConvertPoint = true;

    private Point convertPoint( Component parent, int x, int y, Component comp ) {
        if ( useConvertPoint ) {
            try {
                return SwingUtilities.convertPoint( parent, x, y, comp );
            } catch ( InternalError e ) {
                useConvertPoint = false;
            }
        }
        if ( comp != null ) {
            while ( comp != parent ) {
                x -= comp.getX();
                y -= comp.getY();
                if ( comp.getParent() == null ) {
                    break;
                }
                comp = comp.getParent();
            }
        }
        return new Point( x, y );
    }

    private int getButtonMask( int swingButton ) {
        int buttonMask = 0;
        if ( input.isMouseButtonDown( 0 ) || swingButton == MouseEvent.BUTTON1 ) {
            buttonMask |= InputEvent.BUTTON1_MASK;
            buttonMask |= InputEvent.BUTTON1_DOWN_MASK;
        }
        if ( input.isMouseButtonDown( 1 ) || swingButton == MouseEvent.BUTTON2 ) {
            buttonMask |= InputEvent.BUTTON2_MASK;
            buttonMask |= InputEvent.BUTTON2_DOWN_MASK;
        }
        if ( input.isMouseButtonDown( 2 ) || swingButton == MouseEvent.BUTTON3 ) {
            buttonMask |= InputEvent.BUTTON3_MASK;
            buttonMask |= InputEvent.BUTTON3_DOWN_MASK;
        }
        return buttonMask;
    }

    private int getCurrentModifiers( int swingBtton ) {
        int modifiers = 0;
        if ( isKeyDown( Input.KEY_LMENU ) ) {
            modifiers |= InputEvent.ALT_DOWN_MASK;
            modifiers |= InputEvent.ALT_MASK;
        }
        if ( isKeyDown( Input.KEY_RMENU ) ) {
            modifiers |= InputEvent.ALT_GRAPH_DOWN_MASK;
            modifiers |= InputEvent.ALT_GRAPH_MASK;
        }
        if ( isKeyDown( Input.KEY_LCONTROL ) || isKeyDown( Input.KEY_RCONTROL ) ) {
            modifiers |= InputEvent.CTRL_DOWN_MASK;
            modifiers |= InputEvent.CTRL_MASK;
        }
        if ( isKeyDown( Input.KEY_LSHIFT ) || isKeyDown( Input.KEY_RSHIFT ) ) {
            modifiers |= InputEvent.SHIFT_DOWN_MASK;
            modifiers |= InputEvent.SHIFT_MASK;
        }
        return modifiers | getButtonMask( swingBtton );
    }

    private boolean isKeyDown( int key ) {
        return input.isKeyDown( key );
    }

    private void sendEnteredEvent( Component comp, Component lastComponent, int buttonMask, Point pos ) {
        if ( comp != null && comp != lastComponent ) {
            sendEnteredEvent( comp.getParent(), lastComponent, buttonMask, pos );

            pos = convertPoint( lastComponent, pos.x, pos.y, comp );
            final MouseEvent event = new MouseEvent( comp,
                    MouseEvent.MOUSE_ENTERED,
                    System.currentTimeMillis(), buttonMask, pos.x, pos.y, 0, false, 0 );
            dispatchEvent( comp, event );
        }

    }

    private void sendExitedEvent( Component lastComponent, int buttonMask, Point pos ) {
        final MouseEvent event = new MouseEvent( lastComponent,
                MouseEvent.MOUSE_EXITED,
                System.currentTimeMillis(), buttonMask, pos.x, pos.y, 1, false, 0 );
        dispatchEvent( lastComponent, event );
    }

    private static final int DOUBLE_CLICK_TIME = 300;

    private void sendAWTMouseEvent( int x, int y, boolean pressed, int swingButton ) {
        Component comp = componentAt( x, y, desktop, false );

        final int eventType;
        if ( swingButton > MouseEvent.NOBUTTON ) {
            eventType = pressed ? MouseEvent.MOUSE_PRESSED : MouseEvent.MOUSE_RELEASED;
        }
        else {
            eventType = getButtonMask( MouseEvent.NOBUTTON ) == 0 ? MouseEvent.MOUSE_MOVED : MouseEvent.MOUSE_DRAGGED;
        }

        final long time = System.currentTimeMillis();
        if ( lastComponent != comp ) {
            //enter/leave events
            while ( lastComponent != null && ( comp == null || !SwingUtilities.isDescendingFrom( comp, lastComponent ) ) )
            {
                final Point pos = convertPoint( desktop, x, y, lastComponent );
                sendExitedEvent( lastComponent, getCurrentModifiers( swingButton ), pos );
                lastComponent = lastComponent.getParent();
            }
            final Point pos = convertPoint( desktop, x, y, lastComponent );
            if ( lastComponent == null ) {
                lastComponent = desktop;
            }
            sendEnteredEvent( comp, lastComponent, getCurrentModifiers( swingButton ), pos );
            lastComponent = comp;
            downX = Integer.MIN_VALUE;
            downY = Integer.MIN_VALUE;
            lastClickTime = 0;
        }

        if ( comp != null ) {
            boolean clicked = false;
            if ( swingButton > MouseEvent.NOBUTTON ) {
                if ( pressed ) {
                    grabbedMouse = comp;
                    grabbedMouseButton = swingButton;
                    downX = x;
                    downY = y;
                    setFocusOwner( componentAt( x, y, desktop, true ) );
                }
                else if ( grabbedMouseButton == swingButton && grabbedMouse != null ) {
                    comp = grabbedMouse;
                    grabbedMouse = null;
                    if ( Math.abs( downX - x ) <= MAX_CLICKED_OFFSET && Math.abs( downY - y ) < MAX_CLICKED_OFFSET ) {
                        if ( lastClickTime + DOUBLE_CLICK_TIME > time ) {
                            clickCount++;
                        }
                        else {
                            clickCount = 1;
                        }
                        clicked = true;
                        lastClickTime = time;
                    }
                    downX = Integer.MIN_VALUE;
                    downY = Integer.MIN_VALUE;
                }
            }
            else if ( grabbedMouse != null ) {
                comp = grabbedMouse;
            }

            final Point pos = convertPoint( desktop, x, y, comp );
            final MouseEvent event = new MouseEvent( comp,
                    eventType,
                    time, getCurrentModifiers( swingButton ), pos.x, pos.y, clickCount,
                    swingButton == MouseEvent.BUTTON2 && pressed, // todo: should this be platform dependent? (e.g. mac)
                    swingButton >= 0 ? swingButton : 0 );
            dispatchEvent( comp, event );
            if ( clicked ) {
                // CLICKED seems to need special glass pane handling o_O
                comp = componentAt( x, y, desktop, true );
                final Point clickedPos = convertPoint( desktop, x, y, comp );

                final MouseEvent clickedEvent = new MouseEvent( comp,
                        MouseEvent.MOUSE_CLICKED,
                        time, getCurrentModifiers( swingButton ), clickedPos.x, clickedPos.y, clickCount,
                        false, swingButton );
                dispatchEvent( comp, clickedEvent );
            }
        }
        else if ( pressed ) {
            // clicked no component at all
            setFocusOwner( null );
        }
    }
    
    public void mouseMoved(int oldx, int oldy, int newx, int newy)
    {
        final int awtX = newx;
        final int awtY = newy;
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    sendAWTMouseEvent( awtX, awtY, false, MouseEvent.NOBUTTON );
                }
            } );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
        }
    }

    public void controllerLeftPressed(int i)
    {
    }

    public void controllerLeftReleased(int i)
    {
    }

    public void controllerRightPressed(int i)
    {
    }

    public void controllerRightReleased(int i)
    {
    }

    public void controllerUpPressed(int i)
    {
    }

    public void controllerUpReleased(int i)
    {
    }

    public void controllerDownPressed(int i)
    {
    }

    public void controllerDownReleased(int i)
    {
    }

    public void controllerButtonPressed(int i, int i0)
    {
    }

    public void controllerButtonReleased(int i, int i0)
    {
    }

    /**
     * @return current modal component
     * @see #setModalComponent(java.awt.Component)
     */
    public Component getModalComponent() {
        return this.modalComponent;
    }

    /**
     * @see #setModalComponent(java.awt.Component)
     */
    private Component modalComponent;

    /**
     * Filter the swing event to allow events to the specified component and its children only.
     * Note: this does not prevent shortcuts and mnemonics to work for the other components!
     *
     * @param value component that can be exclusively accessed (including children)
     */
    public void setModalComponent( final Component value ) {
        this.modalComponent = value;
    }

    private static class LightWeightPopup extends Popup {
        private static final Integer INTEGER_MAX_VALUE = Integer.MAX_VALUE;

        public LightWeightPopup( JComponent desktop ) {
            this.desktop = desktop;
            new ScrollPaneRepaintFixListener().addTo( panel );
        }

        private final JComponent desktop;

        JPanel panel = new JPanel( new BorderLayout() );

        public void adjust( Component owner, Component contents, int x, int y ) {
            panel.setVisible( false );
            desktop.add( panel, INTEGER_MAX_VALUE );
            panel.removeAll();
            panel.add( contents, BorderLayout.CENTER );
            if ( contents instanceof JComponent ) {
                JComponent jComponent = (JComponent) contents;
                jComponent.setDoubleBuffered( false );
            }
            panel.setSize( panel.getPreferredSize() );
            y = Math.min( y, desktop.getHeight() - panel.getHeight() );
            x = Math.min( x, desktop.getWidth() - panel.getWidth() );
            panel.setLocation( x, y );
            contents.invalidate();
            panel.validate();
        }

        public void show() {
            panel.setVisible( true );
        }

        public void hide() {
            Rectangle bounds = panel.getBounds();
            desktop.remove( panel );
            desktop.repaint( bounds );
        }
    }

    private static class MyPopupFactory extends PopupFactory {
        private final PopupFactory defaultPopupFactory = new PopupFactory();

        public Popup getPopup( Component owner, Component contents, int x, int y ) throws IllegalArgumentException {
            while ( !( owner instanceof JDesktopPane ) ) {
                owner = owner.getParent();
                if ( owner == null ) {
                    System.out.println( "SlickDesktop Popup creation failed, default popup created - desktop not found in component hierarchy of " + owner );
                    return defaultPopupFactory.getPopup( owner, contents, x, y );
                }
            }
            SlickDesktop.LightWeightPopup popup = new SlickDesktop.LightWeightPopup( (JComponent) owner );
            popup.adjust( owner, contents, x, y );
            return popup;
        }
    }

    private static class ScrollPaneRepaintFixListener implements ContainerListener {
        public void componentAdded( ContainerEvent e ) {
            Component child = e.getChild();
            componentAdded( child );
        }

        private void componentAdded( Component child ) {
            if ( child instanceof Container ) {
                Container container = (Container) child;
                addTo( container );
                container.addContainerListener( this );
            }
            if ( child instanceof JScrollPane ) {
                final JScrollPane scrollPane = (JScrollPane) child;
                // note: the listener added here is only a fix for repaint problems with scrolling
                subscribeRepaintListener( scrollPane.getViewport() );
            }
        }

        private void addTo( Container container ) {
            container.addContainerListener( this );
            for ( int i = 0; i < container.getComponentCount(); i++ ) {
                componentAdded( container.getComponent( i ) );
            }
        }

        private void removeFrom( Container container ) {
            container.removeContainerListener( this );
            for ( int i = 0; i < container.getComponentCount(); i++ ) {
                componentRemoved( container.getComponent( i ) );
            }
        }

        private void subscribeRepaintListener( JViewport viewport ) {
            for ( int i = 0; i < viewport.getChangeListeners().length; i++ ) {
                ChangeListener listener = viewport.getChangeListeners()[i];
                if ( listener instanceof ScrollPaneRepaintChangeListener ) {
                    // listener already subscribed
                    return;
                }
            }
            viewport.addChangeListener( new ScrollPaneRepaintChangeListener( viewport ) );
        }

        public void componentRemoved( ContainerEvent e ) {
            Component child = e.getChild();
            componentRemoved( child );
        }

        private void componentRemoved( Component child ) {
            if ( child instanceof Container ) {
                Container container = (Container) child;
                removeFrom( container );
            }
        }

        private static class ScrollPaneRepaintChangeListener implements ChangeListener {
            private final Component component;

            public ScrollPaneRepaintChangeListener( Component component ) {
                this.component = component;
            }

            public void stateChanged( ChangeEvent e ) {
                component.repaint();
            }
        }
    }

}
