import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import javax.imageio.*;

// JOGL Packages
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

import utils.*;

/**
 *
 * @author Andrew Huynh
 */
public class StarsRenderer implements GLEventListener, KeyListener {
        
    // Reference to Animator
    private Animator animator = null;
    
    // Number of stars to draw
    private final int numStars = 50;
        
    // Twinkle stars?
    private boolean twinkle;

    // Describe what a star is
    class stars {
        int r, g, b;
        float dist, angle;
    }

    stars star[] = new stars[numStars];

    private float zoom = -15.0f;    // Distance away from stars
    private float tilt = 90.f;      // Tilt the view
    private float spin;             // Spin the stars

    private int[] texture = new int[1];

    public StarsRenderer(Animator anim) {
        animator = anim;
    }

    public void display(GLAutoDrawable gLDrawable) {

        // Init
        final GL gl = gLDrawable.getGL();
        gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture[0]);

        // Draw the stars
        for(int loop = numStars - 1; loop >= 0; loop--) {
            gl.glLoadIdentity();
            gl.glTranslatef(0.0f, 0.0f, zoom);
            gl.glRotatef(tilt, 1.0f, 0.0f, 0.0f);
            gl.glRotatef(star[loop].angle, 0.0f, 1.0f, 0.0f);
            gl.glTranslatef(star[loop].dist, 0.0f, 0.0f);
            gl.glRotatef(-star[loop].angle, 0.0f, 1.0f, 0.0f);
            gl.glRotatef(-tilt, 1.0f, 0.0f, 0.0f);

            if(twinkle){
                gl.glColor4ub((byte)star[(numStars-loop)-1].r,(byte)star[(numStars-loop)-1].g,(byte)star[(numStars-loop)-1].b,(byte)255);
                gl.glBegin(gl.GL_QUADS);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f,-1.0f, 0.0f);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f,-1.0f, 0.0f);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f, 1.0f, 0.0f);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f, 1.0f, 0.0f);
                gl.glEnd();
            }

            gl.glRotatef(spin,0.0f,0.0f,1.0f);
            gl.glColor4ub((byte)star[loop].r,(byte)star[loop].g,(byte)star[loop].b,(byte)255);
            gl.glBegin(gl.GL_QUADS);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f,-1.0f, 0.0f);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f,-1.0f, 0.0f);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f, 1.0f, 0.0f);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f, 1.0f, 0.0f);
            gl.glEnd();

            spin+=0.01f;
            star[loop].angle+=(float)loop/numStars;
            star[loop].dist-=0.01f;

            if(star[loop].dist<0.0f){
                star[loop].dist += 5.0f;
                star[loop].r = (int)(Math.random()*1000)%256;
                star[loop].g = (int)(Math.random()*1000)%256;
                star[loop].b = (int)(Math.random()*1000)%256;
            }
        }                       
    }

    public void displayChanged(GLAutoDrawable glDrawable, boolean modeChanged,
                            boolean deviceChanged) {}

    public void init(GLAutoDrawable gLDrawable) {

        final GL gl = gLDrawable.getGL();

        // Enable Smooth Shading
        gl.glShadeModel(GL.GL_SMOOTH);

        // Black background
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

        // Depth Buffer setup
        gl.glClearDepth( 1.0f );

        // Enables Depth testing
        gl.glEnable( GL.GL_DEPTH_TEST );

        // The type of depth testin to do
        gl.glDepthFunc( GL.GL_LEQUAL );

        // Nice perspective calculations
        gl.glHint( GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

        // Enable 2d Textures
        gl.glEnable( GL.GL_TEXTURE_2D);

        // Prepare the stars
        for(int loop = 0; loop < 50; loop++) {
            star[loop] = new stars();
            star[loop].angle = 0.0f;
            star[loop].dist = ((float)loop/50)*5.0f;
            star[loop].r = (int)(Math.random()*1000)%256;
            star[loop].g = (int)(Math.random()*1000)%256;
            star[loop].b = (int)(Math.random()*1000)%256;
        }

        // Respond to key events
        gLDrawable.addKeyListener(this);

        // Generate star texture
        Texture img = TextureLoader.readImage("data/star.bmp");

        gl.glGenTextures(1, texture, 0);

        // Create nearest filtered texture
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture[0]);
            gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,GL.GL_NEAREST);
            gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER,GL.GL_NEAREST);
            gl.glTexImage2D(gl.GL_TEXTURE_2D, 0, GL.GL_RGB, img.getWidth(), img.getHeight(), 0, gl.GL_RGB, gl.GL_UNSIGNED_BYTE, img.getData());
            
        // Set and enable blending
        gl.glBlendFunc( GL.GL_SRC_ALPHA, GL.GL_ONE);
        gl.glEnable( GL.GL_BLEND );
    }

    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, 
                        int height) {
        final GL gl = gLDrawable.getGL();
        final GLU glu = new GLU();

        if(height <= 0)
                height = 1;

        final float h = (float)width / (float)height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(45.0f, h, 1.0, 20.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void keyPressed(KeyEvent s) {
        switch( s.getKeyCode() ) {

            case KeyEvent.VK_UP:
                this.tilt -= 0.5f;
                break;

            case KeyEvent.VK_DOWN:    
                this.tilt += 0.5f;
                break;

            case KeyEvent.VK_RIGHT:
                this.zoom += 0.2f;
                break;

            case KeyEvent.VK_LEFT:
                this.zoom -= 0.2f;
                break;

            case KeyEvent.VK_ESCAPE:
                animator.stop();
                System.exit(0);
                break;
        }
    }

    public void keyReleased(KeyEvent e){}

    public void keyTyped(KeyEvent e){}

}
