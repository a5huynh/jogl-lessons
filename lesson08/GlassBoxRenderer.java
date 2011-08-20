import java.awt.event.*;

// JOGL Packages
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

import utils.*;

public class GlassBoxRenderer implements GLEventListener, KeyListener {
    
    private Animator animator = null;
    
    private boolean light;
    private boolean blend;
    
    private int filter = 0;
    private int[] textures = new int[3];
    
    private float xspeed = 0.01f;
    private float yspeed = 0.01f;
    private float z = -5.0f;
    private float xrot, yrot;
    
    private float[] lightAmbient = {0.5f, 0.5f, 0.5f, 1.0f};
    private float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
    private float[] lightPosition = {0.0f, 0.0f, 2.0f, 1.0f};    
    
    public GlassBoxRenderer(Animator anim) {
        animator = anim;
    }

    public void init(GLAutoDrawable glDrawable) {
        GL gl = glDrawable.getGL();
        GLU glu = new GLU();
        
        gl.glEnable(GL.GL_TEXTURE_2D);              // Enable Texture Mapping
        gl.glShadeModel(GL.GL_SMOOTH);              //Enables Smooth Color Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);    //This Will Clear The Background Color To Black
        gl.glClearDepth(1.0);                       //Enables Clearing Of The Depth Buffer
        gl.glEnable(GL.GL_DEPTH_TEST);                              //Enables Depth Testing
        gl.glDepthFunc(GL.GL_LEQUAL);                               //The Type Of Depth Test To Do
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST); // Really Nice Perspective Calculations
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightAmbient, 0);     // Setup The Ambient Light
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, lightDiffuse, 0);     // Setup The Diffuse Light
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, lightPosition, 0);   // Position The Light
        gl.glEnable(GL.GL_LIGHT1);                                      // Enable Light One

        gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);                           // Full Brightness.  50% Alpha (new )
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);	
        
        glDrawable.addKeyListener(this);
        
        Texture tex = TextureLoader.readImage("data/glass.png");
        
        //Create Nearest Filtered Texture
        gl.glGenTextures(3, textures, 0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);

        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);

        gl.glTexImage2D(GL.GL_TEXTURE_2D,
                0,
                3,
                tex.getWidth(),
                tex.getHeight(),
                0,
                GL.GL_RGB,
                GL.GL_UNSIGNED_BYTE,
                tex.getData());

        //Create Linear Filtered Texture
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[1]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);

        gl.glTexImage2D(GL.GL_TEXTURE_2D,
                0,
                3,
                tex.getWidth(),
                tex.getHeight(),
                0,
                GL.GL_RGB,
                GL.GL_UNSIGNED_BYTE,
                tex.getData());

        //Create MipMapped Texture (Only with GL4Java 2.1.2.1 and later!)
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[2]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_NEAREST);

        glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D,
                3,
                tex.getWidth(),
                tex.getHeight(),
                GL.GL_RGB,
                GL.GL_UNSIGNED_BYTE,
                tex.getData());        
    }

    public void display(GLAutoDrawable glDrawable) {
       GL gl = glDrawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);       //Clear The Screen And The Depth Buffer
        gl.glLoadIdentity();                                         //Reset The View
        gl.glTranslatef(0.0f, 0.0f, z);

        gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);

        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[filter]);
        
        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_BLEND);

        gl.glBegin(GL.GL_QUADS);
        // Front Face
        gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, 1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, 1.0f);
        // Back Face
        gl.glNormal3f(0.0f, 0.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        // Top Face
        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, 1.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, 1.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        // Bottom Face
        gl.glNormal3f(0.0f, -1.0f, 0.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, 1.0f);
        // Right face
        gl.glNormal3f(1.0f, 0.0f, 0.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, 1.0f);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, 1.0f);
        // Left Face
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, 1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();

        xrot += xspeed;
        yrot += yspeed;    
    }

    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
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

    public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        switch( e.getKeyCode() ) {

            case KeyEvent.VK_UP:
                this.xspeed -= 0.01f;
                break;

            case KeyEvent.VK_DOWN:    
                this.xspeed += 0.01f;
                break;

            case KeyEvent.VK_RIGHT:
                this.yspeed += 0.01f;
                break;

            case KeyEvent.VK_LEFT:
                this.yspeed -= 0.01f;
                break;

            case KeyEvent.VK_ESCAPE:
                animator.stop();
                System.exit(0);
                break;
        }        
    }

    public void keyReleased(KeyEvent e) {
    }

}
