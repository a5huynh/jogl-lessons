import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.nio.*;
import javax.imageio.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.Animator;

import utils.*;
public class Lesson07 {
    static Animator animator = null;
  
    static class Renderer implements GLEventListener, KeyListener {
        private boolean light;
        private boolean lp;
        private boolean fp;
    
        private float	xrot;
        private float	yrot;
        private float xspeed;
        private float yspeed;
        private float	z = -5.0f;
        private float[] lightAmbient = { 0.5f, 0.5f, 0.5f, 1.0f };
        private float[] lightDiffuse = { 1.0f, 1.0f, 1.0f, 1.0f };
        private float[] lightPosition = { 0.0f, 0.0f, 2.0f, 1.0f };
        int filter;
        private int[] texture = new int[3];
    
        /** Called by the drawable to initiate OpenGL rendering by the client.
         * After all GLEventListeners have been notified of a display event, the
         * drawable will swap its buffers if necessary.
         * @param gLDrawable The GLDrawable object.
         */
         public void display(GLAutoDrawable gLDrawable) {
		     final GL gl = gLDrawable.getGL();
             gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
             gl.glLoadIdentity();									// Reset The View
             gl.glTranslatef(0.0f, 0.0f, this.z);

             gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
             gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);

             gl.glBindTexture(GL.GL_TEXTURE_2D, texture[filter]);
             
             if (light)
                 gl.glEnable(GL.GL_LIGHTING);
             else
                 gl.glDisable(GL.GL_LIGHTING);

             gl.glBegin(GL.GL_QUADS);
                // Front Face
                gl.glNormal3f(0.0f, 0.0f, 1.0f);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);
                // Back Face
                gl.glNormal3f(0.0f, 0.0f, -1.0f);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);
                // Top Face
                gl.glNormal3f(0.0f, 1.0f, 0.0f);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);
                // Bottom Face
                gl.glNormal3f(0.0f, -1.0f, 0.0f);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);
                // Right face
                gl.glNormal3f(1.0f, 0.0f, 0.0f);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);
                // Left Face
                gl.glNormal3f(-1.0f, 0.0f, 0.0f);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);
            gl.glEnd();

            xrot += xspeed;
            yrot += yspeed;
        }

        /** Called when the display mode has been changed.  <B>!! CURRENTLY UNIMPLEMENTED IN JOGL !!</B>
         * @param gLDrawable The GLDrawable object.
         * @param modeChanged Indicates if the video mode has changed.
         * @param deviceChanged Indicates if the video device has changed.
         */
         public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) {}
    
        /** Called by the drawable immediately after the OpenGL context is
         * initialized for the first time. Can be used to perform one-time OpenGL
         * initialization such as setup of lights and display lists.
         * @param gLDrawable The GLDrawable object.
         */
         public void init(GLAutoDrawable gLDrawable) {
             final GL gl = gLDrawable.getGL();
             final GLU glu = new GLU();

             gl.glShadeModel(GL.GL_SMOOTH);              // Enable Smooth Shading
             gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);    // Black Background
             gl.glClearDepth(1.0f);                      // Depth Buffer Setup
             gl.glEnable(GL.GL_DEPTH_TEST);							// Enables Depth Testing
             gl.glDepthFunc(GL.GL_LEQUAL);								// The Type Of Depth Testing To Do
             gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);	// Really Nice Perspective Calculations
             gl.glEnable(GL.GL_TEXTURE_2D);
             gLDrawable.addKeyListener(this);
             
             Texture img = TextureLoader.readImage("data/crate.png");
      
             gl.glGenTextures(3, this.texture, 0);

             // Create Nearest Filtered Texture
             gl.glBindTexture(GL.GL_TEXTURE_2D, texture[0]);
  		        gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,GL.GL_NEAREST);
  		        gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER,GL.GL_NEAREST);
                gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, img.getWidth(), img.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, img.getData());      

            // Create Linear Filtered Texture
            gl.glBindTexture(GL.GL_TEXTURE_2D, texture[1]);
  		        gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,GL.GL_LINEAR);
  		        gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER,GL.GL_LINEAR);
                gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, img.getWidth(), img.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, img.getData());      

            // Create MipMapped Texture
            gl.glBindTexture(GL.GL_TEXTURE_2D, texture[2]);
  		        gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,GL.GL_LINEAR);
  		        gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER,GL.GL_LINEAR);
                glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, GL.GL_RGB8, img.getWidth(), img.getHeight(), GL.GL_RGB, GL.GL_UNSIGNED_BYTE, img.getData());  		        
      
            // Set up lighting
            gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, this.lightAmbient, 0);
            gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, this.lightDiffuse, 0);
            gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, this.lightPosition, 0);
            gl.glEnable(GL.GL_LIGHT1);
            gl.glEnable(GL.GL_LIGHTING);
            this.light = true;
        }

        /** Called by the drawable during the first repaint after the component has
         * been resized. The client can update the viewport and view volume of the
         * window appropriately, for example by a call to
         * GL.glViewport(int, int, int, int); note that for convenience the component
         * has already called GL.glViewport(int, int, int, int)(x, y, width, height)
         * when this method is called, so the client may not have to do anything in
         * this method.
         * @param gLDrawable The GLDrawable object.
         * @param x The X Coordinate of the viewport rectangle.
         * @param y The Y coordinate of the viewport rectanble.
         * @param width The new width of the window.
         * @param height The new height of the window.
         */
         public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
             final GL gl = gLDrawable.getGL();
		     final GLU glu = new GLU();

             if (height <= 0) // avoid a divide by zero error!
                 height = 1;
             
             final float h = (float)width / (float)height;
             gl.glViewport(0, 0, width, height);
             gl.glMatrixMode(GL.GL_PROJECTION);
             gl.glLoadIdentity();
             glu.gluPerspective(45.0f, h, 1.0, 20.0);
             gl.glMatrixMode(GL.GL_MODELVIEW);
             gl.glLoadIdentity();
         }
    
        /** Invoked when a key has been pressed.
         * See the class description for {@link KeyEvent} for a definition of
         * a key pressed event.
         * @param e The KeyEvent.
         */
         public void keyPressed(KeyEvent e) {

             switch (e.getKeyCode()) {
                 
                 case KeyEvent.VK_L:
                    this.light = !this.light; break;
                    
                case KeyEvent.VK_F:
                    this.filter += 1;
                    
                    if (filter > 2)
                        filter = 0;
                    break;

                case KeyEvent.VK_PAGE_UP:
                    this.z -= 0.02f; break;
                    
                case KeyEvent.VK_PAGE_DOWN:
                    this.z += 0.02f; break;

                case KeyEvent.VK_UP:
                    this.xspeed -= 0.01f; break;
                    
                case KeyEvent.VK_DOWN:
                    this.xspeed += 0.01f; break;
                
                case KeyEvent.VK_RIGHT:
                    this.yspeed += 0.01f; break;

                case KeyEvent.VK_LEFT:
                    this.yspeed -= 0.01f; break;
        
                case KeyEvent.VK_ESCAPE:
                    animator.stop();
                    System.exit(0);
                    break;
            }
        }
    
        /** Invoked when a key has been released.
         * See the class description for {@link KeyEvent} for a definition of
         * a key released event.
         * @param e The KeyEvent.
         */
         public void keyReleased(KeyEvent e) {}
    
        /** Invoked when a key has been typed.
         * See the class description for {@link KeyEvent} for a definition of
         * a key typed event.
         * @param e The KeyEvent.
         */
         public void keyTyped(KeyEvent e) {}
     }
     
     public static void main(String[] args) {
         
         Frame frame = new Frame("Lesson 7: Texture Filters, Lighting & Keyboard Control");
         GLCanvas canvas = new GLCanvas();
         canvas.addGLEventListener(new Renderer());
         frame.add(canvas);
         frame.setSize(640, 480);
         animator = new Animator(canvas);
         frame.addWindowListener(new WindowAdapter() {
             public void windowClosing(WindowEvent e) {
                 animator.stop();
                 System.exit(0);
             }
         });
         
         frame.setVisible(true);
         animator.start();
         canvas.requestFocus();
     }
}
