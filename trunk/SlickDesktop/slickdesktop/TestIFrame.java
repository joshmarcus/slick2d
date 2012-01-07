/*
 * TestIFrame.java
 *
 * Created on 20 October 2007, 23:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package slickdesktop;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

/**
 *
 * @author bjgil
 */
public class TestIFrame extends JInternalFrame {
    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;

    public TestIFrame() {
        super("Document #" + (++openFrameCount), 
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable

        //...Create the GUI and put it in the window...

        //...Then set the window size or call pack...
        setSize(300,300);

        //Set the window's location.
        setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
        
        JPanel p = new JPanel( new BorderLayout() );
        p.setBackground( java.awt.Color.blue );
        JPanel tmp = new JPanel();
        JButton b = new JButton( "test" );
        b.addActionListener( new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                System.out.println( "test button on internal frame " + getTitle() );
            }
        });
        tmp.add( b );
        p.add( tmp, BorderLayout.SOUTH );
        
        tmp = new JPanel();
        JComboBox cb = new JComboBox( new Object[]{ "item 1", "item 2", "item 3" } );
        tmp.add( cb );
        p.add( tmp, BorderLayout.NORTH );
        
        
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab( "Test", p );
        
        tabs.addTab( "Test 2", new JScrollPane( new JTextArea() ) );
        
        
        this.add( tabs );
    }
}