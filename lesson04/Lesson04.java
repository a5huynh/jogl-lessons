import java.awt.*;
import java.awt.event.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;

import com.sun.opengl.util.Animator;
public class Lesson04 {
    static Animator animator = null;

    static class Renderer implements GLEventListener, KeyListener {
        private float rquad = 0.0f;
        private float rtri = 0.0f;

        /** Called by the drawable to initiate OpenGL rendering by the client.
         * After all GLEventListeners have been notified of a display event, the 
         * drawable will swap its buffers if necessary.
         * @param gLDrawable The GLDrawable object.
         */    
         public void display(GLAutoDrawable gLDrawable) {
             final GL gl = gLDrawable.getGL();
             gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
             gl.glLoadIdentity();
             gl.glTranslatef(-1.5f, 0.0f, -6.0f);
             gl.glRotatef(rtri, 0.0f, 1.0f, 0.0f);
             
             gl.glBegin(GL.GL_TRIANGLES);		    // Drawing Using Triangles
                gl.glColor3f(1.0f, 0.0f, 0.0f);   // Set the current drawing color to red
                gl.glVertex3f( 0.0f, 1.0f, 0.0f);	// Top
                gl.glColor3f(0.0f, 1.0f, 0.0f);   // Set the current drawing color to green
                gl.glVertex3f(-1.0f,-1.0f, 0.0f);	// Bottom Left
                gl.glColor3f(0.0f, 0.0f, 1.0f);   // Set the current drawing color to blue
                gl.glVertex3f( 1.0f,-1.0f, 0.0f);	// Bottom Right
            gl.glEnd();				// Finished Drawing The Triangle

            gl.glLoadIdentity();
            gl.glTranslatef(1.5f, 0.0f, -6.0f);
            gl.glRotatef(rquad, 1.0f, 0.0f, 0.0f);
            
            gl.glBegin(GL.GL_QUADS);           	// Draw A Quad
                gl.glColor3f(0.5f, 0.5f, 1.0f);   // Set the current drawing color to light blue
                gl.glVertex3f(-1.0f, 1.0f, 0.0f);	// Top Left
                gl.glVertex3f( 1.0f, 1.0f, 0.0f);	// Top Right
                gl.glVertex3f( 1.0f,-1.0f, 0.0f);	// Bottom Right
                gl.glVertex3f(-1.0f,-1.0f, 0.0f);	// Bottom Left
                gl.glEnd();				// Done Drawing The Quad
                
            gl.glFlush();
            rtri += 0.2f;
            rquad += 0.15f;
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
            gl.glShadeModel(GL.GL_SMOOTH);              // Enable Smooth Shading
            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);    // Black Background
            gl.glClearDepth(1.0f);                      // Depth Buffer Setup
            gl.glEnable(GL.GL_DEPTH_TEST);							// Enables Depth Testing
            gl.glDepthFunc(GL.GL_LEQUAL);								// The Type Of Depth Testing To Do
            gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);	// Really Nice Perspective Calculations
            gLDrawable.addKeyListener(this);
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
             
             if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                 animator.stop();
                 System.exit(0);
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

    /** Program's main entry point
    * @param args command line arguments.
    */
    public static void main(String[] args) {
        
        Frame frame = new Frame("Lesson 4: Rotation");
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
