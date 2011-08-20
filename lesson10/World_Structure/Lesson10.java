package World_Structure;

/*
 * Lesson10.java
 *
 * Created on December 20, 2003, 9:05 PM
 *
 * Ported to JOGL 1.1 by Andrew Huynh, May 13, 2009
 */

import OptionFrame.Options; //the only class in the package but it was easier to do this in porting the OpenGL tuts
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.StringTokenizer;
import java.net.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import utils.*;

/** Used the JOGL2DBasics.java file from the aforementioned site for basic understanding.
 * Also, referenced the port of the NeHe's lessons by Kevin Duling (jattier@hotmail.com)
 * to gain my understanding of JoGL. I used his PNG loading methods and put the concepts
 * into a package. Also, I have used Abdul Bezrati's (abezrati@hotmail.com) *.bmp loader
 * and placed that in the same package. I created a jar for them and placed them in my
 * JRE_HOME/lib/ext directory. I have also created an OptionFrame which allows the user
 * to select different options and then load them into the main application. This was
 * put in it's own package and placed in the above directory. These two packages have
 * been included as folders in the jar, but i have also included the .jar files.
 * Thank you to the sources for the help and i hope that my contributions help anyone who
 * wishes for them to. Also, with the above package LoadImageTypes i have developed the
 * classes that are a part of that package so they may not be exactly as they were
 * but the main content is the same. It was easier to do this then to create it myself.
 * @author Nicholas Campbell - campbelln@hartwick.edu
 * @since 20 DECEMBER 2003
 * @version 1.00
 */
public class Lesson10 extends Frame implements GLEventListener, KeyListener
{
    /** the number of textures, really the number of filters but it produces 3
     *  different looking textures, thus the name
     */
    private final int NUM_TEXTURES = 3;
    
    /** the number of keys that we want to pay attention to */
    private final int NUM_KEYS = 250;
    
    /** the float value of PI/180 */
    private final float PI_OVER_180 = (float)(Math.PI/180.0);
    
    /** fullscreen or not, true means yes */
    private boolean fullscreen = true;
    /** is blending on or off */
    private boolean blending;
    /** is the key 'B' pressed or not, referenced in display() for blending*/
    private boolean bp;
    /** is the key 'F' pressed or not, referenced in display() for filtering*/
    private boolean fp;
    
    /** the array of textures for our objects */
    private int[] textures = new int[NUM_TEXTURES];
    /** the array of keys to store whether certain actions should be taken based on
     * their values
     */
    private boolean[] keys = new boolean[NUM_KEYS];
    /** the value of filtering determines the filter value */
    private int filtering = 0;
    /** the x position */
    private float xpos;
    /** the rotation value on the y axis */
    private float yrot;
    /** the z position */
    private float zpos;
    private float heading;
    /** walkbias for head bobbing effect */
    private float walkbias = 0.0f;
    /** the angle used in calculating walkbias */
    private float walkbiasangle = 0.0f;
    /** the value used for looking up or down pgup or pgdown */
    private float lookupdown = 0.0f;

    /** a GLCanvas object */
    private GLCanvas glCanvas;
    /** an Animator object */
    private Animator animator;
    /** a sector which holds a series of triangles*/
    private Sector sector;
    
    /** Creates a new instance of Lesson10
     * @param dim The Dimension of the Frame by which to view the canvas.
     * @param fscreen A boolean value to set fullscreen or not
     */
    public Lesson10(Animator animator) {
        super("Creating a World...");

        filtering = 0;
        blending = false;

        this.animator = animator;
    }
    
    /** Called in the beginning of the application to take grab the focus on the
     * monitor of all other apps.
     * @return glCanvas
     */
    public GLCanvas getGLCanvas() {
        return glCanvas;
    }
    
    /*
     * METHODS DEFINED BY GLEventListener
     */
    
    /** Called by drawable to initiate drawing
     * @param gLDrawable The GLDrawable Object
     */
    public void display(GLAutoDrawable gLDrawable) {
        
        GL gl = gLDrawable.getGL();
        
        // Clear Color Buffer, Depth Buffer
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
        gl.glLoadIdentity();
        
        float xTrans = -xpos;
        float yTrans = -walkbias - 0.43f;
        float zTrans = -zpos;
        float sceneroty = 360.0f - yrot;
        
        gl.glRotatef(lookupdown, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(sceneroty, 0.0f, 1.0f, 0.0f);
	
        gl.glTranslatef(xTrans, yTrans, zTrans);
        gl.glBindTexture(gl.GL_TEXTURE_2D, textures[filtering]);
        
        float[][] f = null;
        for (int i = 0; i < sector.getNumTriangles(); i++) {
            
            f = sector.getTriangleInfo(i);
            gl.glBegin(GL.GL_TRIANGLES);
                gl.glNormal3f( 0.0f, 0.0f, 1.0f);
                //first vertex
                gl.glTexCoord2f(f[0][3], f[0][4]); gl.glVertex3f(f[0][0], f[0][1], f[0][2]);
                //second vertex
                gl.glTexCoord2f(f[1][3], f[1][4]); gl.glVertex3f(f[1][0], f[1][1], f[1][2]);
                //third vertex
                gl.glTexCoord2f(f[2][3], f[2][4]); gl.glVertex3f(f[2][0], f[2][1], f[2][2]);
            gl.glEnd();
        }
        
        if (keys['F'] && !fp) {
            fp = true;
            filtering++;
            
            if (filtering > 2) {
                filtering = 0;
            }
        } else if (!(keys['F'])) {
            fp = false;
        }
        
        if (keys['B'] && !bp) {
            bp = true;
            blending = !blending;
            if (blending) { gl.glDisable(GL.GL_BLEND); gl.glEnable(GL.GL_DEPTH_TEST); }
            else { gl.glEnable(GL.GL_BLEND); gl.glDisable(GL.GL_DEPTH_TEST); }
        } else if (!(keys['B'])) {
            bp = false;
        }
        
        if (keys[KeyEvent.VK_RIGHT]) {
            heading -= 0.5f;
            yrot = heading;
        } else if (keys[KeyEvent.VK_LEFT]) {
            heading += 0.5f;
            yrot = heading;
        }
        
        if (keys[KeyEvent.VK_UP]) {
            xpos -= (float)Math.sin(heading*PI_OVER_180) * 0.005f;   // Move On The X-Plane Based On Player Direction
            zpos -= (float)Math.cos(heading*PI_OVER_180) * 0.005f;   // Move On The Z-Plane Based On Player Direction
            
            if (walkbiasangle >= 359.0f) { walkbiasangle = 0.0f; }
            else { walkbiasangle += 0.5f; }
            
            walkbias = (float)Math.sin(walkbiasangle * PI_OVER_180)/20.0f;  // Causes The Player To Bounce
            
        } else if (keys[KeyEvent.VK_DOWN]) {
            xpos += (float)Math.sin(heading*PI_OVER_180) * 0.005f;    // Move On The X-Plane Based On Player Direction
            zpos += (float)Math.cos(heading*PI_OVER_180) * 0.005f;    // Move On The Z-Plane Based On Player Direction
            
            if (walkbiasangle <= 1.0f) { walkbiasangle = 359.0f; }
            else { walkbiasangle-= 0.5f; }
            
            walkbias = (float)Math.sin(walkbiasangle * PI_OVER_180)/20.0f;   // Causes The Player To Bounce
        }
        
        if (keys[KeyEvent.VK_PAGE_UP]) {
            lookupdown += 2.0f;
        } else if (keys[KeyEvent.VK_PAGE_DOWN]) {
            lookupdown -= 2.0f;
        }
    }
    
    /** Called by drawable to show that a mode or device has changed <br>
     * <B>!! CURRENTLY NON-Functional IN JoGL !!</B>
     * @param gLDrawable The GLDrawable object.
     * @param modeChanged Indicates if the video mode has changed.
     * @param deviceChanged Indicates if the video device has changed.
     */
    public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) {}
    
    /**  Called by the drawable immediately after the OpenGL context is 
     * initialized for the first time. Can be used to perform one-time OpenGL 
     * initialization such as setup of lights and display lists.
     * @param gLDrawable The GLDrawable object.
     */
    public void init(GLAutoDrawable gLDrawable) {
        
        GLU glu = new GLU();
        GL gl = gLDrawable.getGL();
        gLDrawable.setGL( new DebugGL(gLDrawable.getGL()));
        
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);    // Black Background
        gl.glClearDepth(1.0f);                      // Depth Buffer Setup
        gl.glEnable(GL.GL_DEPTH_TEST);              // Enables Depth Testing
        gl.glDepthFunc(GL.GL_LEQUAL);               // The Type Of Depth Testing To Do
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);	// Really Nice Perspective Calculations
        gl.glEnable(GL.GL_TEXTURE_2D);
        gLDrawable.addKeyListener(this);            // Listening for key events
        
        String imgLoc = "data/concrete.png";
        String worldDataLoc = "data/World.txt";
	    
	    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);		// Blending Function For Translucency Based On Source Alpha Value ( NEW )
        gl.glDisable(GL.GL_BLEND);
        
        setupWorld(worldDataLoc);
        loadGLTextures(gl, glu, imgLoc);
    }
    
    /** This method loads textures into the texture array
     * @param gl A GL object to reference when setting values for it
     * @param glu A GLU object to reference when setting values for it
     * @param imgLoc The string location of the image/texture to load.
     */    
    public void loadGLTextures(GL gl, GLU glu, String imgLoc) {
        
        Texture tex = TextureLoader.readImage(imgLoc);
        
        gl.glGenTextures(3, textures, 0);
        
        // Create Nearest Filtered Texture
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        
        gl.glTexImage2D(GL.GL_TEXTURE_2D,
                0,
                3,
                tex.getWidth(),
                tex.getHeight(),
                0,
                GL.GL_RGB,
                GL.GL_UNSIGNED_BYTE,
                tex.getData());

        
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[1]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        
        gl.glTexImage2D(GL.GL_TEXTURE_2D,
                0,
                3,
                tex.getWidth(),
                tex.getHeight(),
                0,
                GL.GL_RGB,
                GL.GL_UNSIGNED_BYTE,
                tex.getData());
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[2]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        
        glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D,
                3,
                tex.getWidth(),
                tex.getHeight(),
                GL.GL_RGB,
                GL.GL_UNSIGNED_BYTE,
                tex.getData());
    }
    
    /** Reads the lines of the textfile sent as a parameter and loads them
     * into the sector
     * @param worldDataLoc The location of the textfile with the data
     */
    public void setupWorld(String worldDataLoc) {
        File file = new File(worldDataLoc);
        BufferedReader bR = null;
        try { bR = new BufferedReader(new FileReader(file)); }
        catch (FileNotFoundException fNFE) { System.out.println(fNFE); }
        try
        {
            while (!(bR.ready()))
            {
                try { Thread.sleep(200); } catch (InterruptedException ie) {}
            }
        }
        catch (IOException iOE) {}
        
        int i = 0;
        String[] s = new String[150];
        try
        {
            while (!(s[i]=(!((s[i] = bR.readLine()).trim().equals("EOF") || (s[i].trim().length() == 0) || (s[i].trim().startsWith("//"))))?s[i]:s[i--]).equals("EOF"))
            { i++; if (i == s.length) { break; } }
        }
        catch (IOException iOE) {}
        sector = new Sector(s);
        try { bR.close(); } catch (IOException ioe) {}
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
     * @param y The Y coordinate of the viewport rectangle.
     * @param width The new width of the window.
     * @param height The new height of the window.
     */
    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
        
        GLU glu = new GLU();
        GL gl = gLDrawable.getGL();

        if (height <= 0) // avoid a divide by zero error!
            height = 1;
            
        float h = (float)width / (float)height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 1, 1000);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    /** Forced by KeyListener; listens for keypresses and
     * sets a value in an array if they are not of
     * KeyEvent.VK_ESCAPE or KeyEvent.VK_F1
     * @param ke The KeyEvent passed from the KeyListener
     */    
    public void keyPressed(KeyEvent ke) {
        
       switch(ke.getKeyCode()) {
            case KeyEvent.VK_ESCAPE: {
                 System.out.println("User closed application.");
                 animator.stop();
                 System.exit(0);
                 break;
            }
            
            case KeyEvent.VK_F1: {
                setVisible(false);
                if (fullscreen)
                    setSize(800,600);
                else
                    setSize(Toolkit.getDefaultToolkit().getScreenSize().getSize());
                fullscreen = !fullscreen;
                //reshape();
                setVisible(true);
            }
            
            default :
               if(ke.getKeyCode()<250) // only interested in first 250 key codes, are there more?
                  keys[ke.getKeyCode()]=true;	
               break;
         }
    }
    
    /** Unsets the value in the array for the key pressed.
     * @param ke The KeyEvent passed from the KeyListener
     */    
    public void keyReleased(KeyEvent ke) {
        if (ke.getKeyCode() < 250) { keys[ke.getKeyCode()] = false; }
    }
    
    /** ...has no purpose in this class :)
     * @param ke The KeyEvent passed from the KeyListener
     */    
    public void keyTyped(KeyEvent ke) {}
    
}