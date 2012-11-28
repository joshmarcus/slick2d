/*
 * SD2_DesktopTest.java
 *
 * Created on 23 October 2007, 15:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package slickdesktop;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.painter.SpecularGradientPainter;
import org.jvnet.substance.theme.SubstanceBottleGreenTheme;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 * @author bjgil
 */
public class SlickDesktopTest
    extends BasicGame
{
    private SlickDesktop desktop = null;
    
    private static int width = 800;
    private static int height = 600;
    
    private Image img = null;
    private int renderInterval = (1000/30);
    private int tick = 1000;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
//        try
//        {
//            UIManager.setLookAndFeel(new SubstanceLookAndFeel());
//            SubstanceLookAndFeel.setCurrentGradientPainter( new SpecularGradientPainter() );
//            SubstanceLookAndFeel.setCurrentTheme( 
//                    new SubstanceBottleGreenTheme()
//            );
//            UIManager.setLookAndFeel(new SubstanceLookAndFeel());
//            
//            JFrame.setDefaultLookAndFeelDecorated( true );
//            JDialog.setDefaultLookAndFeelDecorated( true );
//        } 
//        catch( UnsupportedLookAndFeelException ex )
//        {
//            ex.printStackTrace();
//        }

        try 
        {
            SlickDesktopTest sd = new SlickDesktopTest();
            AppGameContainer container = new AppGameContainer( sd );
            container.setDisplayMode( width, height, false );
            container.start();
        } 
        catch( SlickException e ) 
        {
            e.printStackTrace();
        }
    }

    /** Creates a new instance of SD2_DesktopTest */
    public SlickDesktopTest()
    {
        super( "SlickDesktopTest" );
    }

    public void init(GameContainer gameContainer) 
        throws SlickException
    {
        desktop = new SlickDesktop( "Test", width, height );
        desktop.getJDesktop().setOpaque( true );
        desktop.getJDesktop().setBackground( new java.awt.Color(0.0f, 0.0f, 0.0f, 0.0f) );
        desktop.setInput( gameContainer.getInput() );
        
        createFrame();
        createFrame();
        createFrame();
        
        JButton b = new JButton( "test" );
        b.setBounds( 0, 0, 75, 20 );
        desktop.getJDesktop().add( b );
        
//        desktop.setShowingJFrame( true );

        gameContainer.getGraphics().setBackground( org.newdawn.slick.Color.orange );
    }

    private void createFrame()
    {
        TestIFrame iframe = new TestIFrame();
        iframe.setVisible(true); //necessary as of 1.3
        desktop.getJDesktop().add(iframe);
        try {
            iframe.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }

    public void update(GameContainer gameContainer, int i) 
        throws SlickException
    {
        tick += i;
    }

    public void render(GameContainer gameContainer, Graphics graphics) 
        throws SlickException
    {
        if( tick > renderInterval )
        {
            tick = 0;
            img = desktop.render( gameContainer, graphics );
        }
        if( img != null )
        {
            graphics.drawImage( img, 0, 0 );
        }
        
    }
    
}
