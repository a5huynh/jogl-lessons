import javax.media.opengl.*;
import javax.media.opengl.glu.*;

import javax.vecmath.*;

import com.sun.opengl.util.*;

import java.util.StringTokenizer;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;
import java.awt.*;
import java.io.*;
import java.nio.*;

public class CelShader implements GLEventListener, KeyListener {
    
    private Animator animator = null;
    
    boolean             outlineSmooth = true;                          //Flag To Anti-Alias The Lines
    boolean             outlineDraw = true;                     // Flag To Draw The Outline
    boolean             lightRotate = false;                    // Flag To See If We Rotate The Light
    
    boolean             keys[]      = new boolean[256];         // Array Used For The Keyboard Routine
    boolean             light = true;                                  // Lighting ON/OFF
                      
    // User Defined Variables
    float               outlineColor[] = { 0.0f, 0.0f, 0.0f },   // Color Of The Lines
                        outlineWidth   = 3f,                     // Width Of The Lines
                        xrot           = 0f,
                        yrot           = 0f;

    POLYGON             polyData[];                              // Polygon Data
    
    Vector3f            lightAngle = new Vector3f();

    int                 shaderTexture[] = new int[1];            // Storage For One Texture
    int                 screenWidth,
                        screenHeight,
                        canvasHeight,
                        canvasWidth,
                        xLocation,
                        yLocation,
                        polyNum         = 0;                     // Number Of Polygons
    
    public CelShader( Animator anim ) {
        animator = anim;
    }
    
    public void init(GLAutoDrawable drawable) {
        
        GL gl   = drawable.getGL();
        GLU glu = new GLU();
        
        drawable.addKeyListener(this);

        // Start Of User Initialization
        gl.glHint(gl.GL_PERSPECTIVE_CORRECTION_HINT, gl.GL_NICEST); // Realy Nice perspective calculations
        gl.glClearColor(0.7f, 0.7f, 0.7f, 0.7f);
        gl.glClearDepth(1.0f);                                      // Depth Buffer Setup

        gl.glEnable(gl.GL_DEPTH_TEST);                              // Enable Depth Testing
        gl.glDepthFunc(gl.GL_LESS);                                 // The Type Of Depth Test To Do

        gl.glShadeModel(gl.GL_SMOOTH);                              // Enables Smooth Color Shading ( NEW )
        gl.glDisable(gl.GL_LINE_SMOOTH);                            // Initially Disable Line Smoothing ( NEW )

        gl.glEnable(gl.GL_CULL_FACE);                               // Enable OpenGL Face Culling ( NEW )

        gl.glDisable(gl.GL_LIGHTING);                               // Disable OpenGL Lighting ( NEW )

        FloatBuffer shaderData = FloatBuffer.allocate( 96 * 3 );

        try {
    
            BufferedReader shader = new BufferedReader( new FileReader("data/shader.txt") );

            String line = null;
            while( ( line = shader.readLine() ) != null ) {
                float tmp = Float.parseFloat( line );
                shaderData.put( tmp );
                shaderData.put( tmp );
                shaderData.put( tmp );
            }            
    
        } catch( IOException e ) {
   
           System.out.println("Couldn't open shader.txt");
           System.exit(0);
   
        }
       
        // Reset shader data bufer position
        shaderData.position(0);

        gl.glGenTextures(1, shaderTexture, 0);                           // Get A Free Texture ID ( NEW )
        gl.glBindTexture(gl.GL_TEXTURE_1D, shaderTexture[0]);         // Bind This Texture. From Now On It Will Be 1D ( NEW )

        // For Crying Out Loud Don't Let OpenGL Use Bi/Trilinear Filtering! ( NEW )
        gl.glTexParameteri(gl.GL_TEXTURE_1D,gl.GL_TEXTURE_MAG_FILTER,gl.GL_NEAREST);
        gl.glTexParameteri(gl.GL_TEXTURE_1D,gl.GL_TEXTURE_MIN_FILTER,gl.GL_NEAREST);

        gl.glTexImage1D(gl.GL_TEXTURE_1D, 0 ,gl.GL_RGB, 32, 0, gl.GL_RGB, gl.GL_FLOAT, shaderData); // Upload ( NEW )

        lightAngle.x = 0.0f;                                          // Set The X Direction ( NEW )
        lightAngle.y = 0.0f;                                          // Set The Y Direction ( NEW )
        lightAngle.z = 1.0f;                                          // Set The Z Direction ( NEW )

        lightAngle.normalize();                                        // Normalize The Light Direction ( NEW )
        ReadMesh ();                                                  // Return The Value Of ReadMesh ( NEW )
    }

    public void display(GLAutoDrawable drawable){
        GL gl = drawable.getGL();
        GLU glu = new GLU();
        
        // Clear Color Buffer, Depth Buffer
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
       
        float TmpShade;                                             // Temporary Shader Value ( NEW )

        Matrix4f TmpMatrix = new Matrix4f();
        Vector3f TmpVector = new Vector3f();
        Vector3f TmpNormal = new Vector3f();

        gl.glLoadIdentity();                                        // Reset The Matrix

        // Check To See If We Want Anti-Aliased Lines
        if(outlineSmooth) {
            
            // Use The Good Calculations
            gl.glHint(gl.GL_LINE_SMOOTH_HINT,gl.GL_NICEST);
            // Enable Anti-Aliasing
            gl.glEnable(gl.GL_LINE_SMOOTH);
            
        // We Don't Want Smooth Lines 
        } else {
            // Disable Anti-Aliasing  
            gl.glDisable(gl.GL_LINE_SMOOTH);
        }

        gl.glTranslatef (0.0f, 0.0f, -2.0f);                        // Move 2 Units Away From The Screen ( NEW )
        gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);                 // Rotate The Model On It's Y-Axis ( NEW )
        gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);                 // Rotate The Model On It's Y-Axis ( NEW )        
        
        float[] matrixData = new float[16];
        gl.glGetFloatv(gl.GL_MODELVIEW_MATRIX, matrixData, 0);     // Get The Generated Matrix ( NEW )
        TmpMatrix.set( matrixData );

        // Cel-Shading Code //
        gl.glEnable(gl.GL_TEXTURE_1D);                              // Enable 1D Texturing ( NEW )
        gl.glBindTexture(gl.GL_TEXTURE_1D, shaderTexture[0]);       // Bind Our Texture ( NEW )
        
        //gl.glColor3f( 0.0f, 0.644f, 1.0f);
        gl.glColor3f( 1.0f, 1.0f, 1.0f );

        gl.glBegin(gl.GL_TRIANGLES);                                // Tell OpenGL That We're Drawing Triangles

        // Loop Through Each Polygon
        for(int i = 0; i < polyNum; i++) {
            
            // Loop Through Each Vertex
            for(int j = 0; j < 3; j++) {

                TmpNormal.x = polyData[i].Verts[j].Nor.x;               // Fill Up The TmpNormal Structure With
                TmpNormal.y = polyData[i].Verts[j].Nor.y;               // The Current Vertices' Normal Values ( NEW )
                TmpNormal.z = polyData[i].Verts[j].Nor.z;
                
                TmpVector = rotateVector( TmpMatrix, TmpNormal );         // Rotate This By The Matrix ( NEW )
                TmpVector.normalize();

                // Calculate The Shade Value
                TmpShade = TmpVector.dot( lightAngle );
                
                // Clamp The Value to 0 If Negative
                if(TmpShade < 0.0f)
                    TmpShade = 0.0f;

                gl.glTexCoord1f(TmpShade);                              // Set The Texture Co-ordinate As The Shade Value ( NEW )
                gl.glVertex3f(polyData[i].Verts[j].Pos.x,
                            polyData[i].Verts[j].Pos.y,
                            polyData[i].Verts[j].Pos.z);              // Send The Vertex Position ( NEW )
            }
        }

        gl.glEnd ();                                                // Tell OpenGL To Finish Drawing
        gl.glDisable(gl.GL_TEXTURE_1D);                             // Disable 1D Textures ( NEW )

        // Outline Code //
        if(outlineDraw) {                                            // Check To See If We Want To Draw The Outline ( NEW )
            gl.glEnable(gl.GL_BLEND);                                 // Enable Blending ( NEW )
            gl.glBlendFunc(gl.GL_SRC_ALPHA,gl.GL_ONE_MINUS_SRC_ALPHA);// Set The Blend Mode ( NEW )

            gl.glPolygonMode(gl.GL_BACK,gl.GL_LINE);                  // Draw Backfacing Polygons As Wireframes ( NEW )
            gl.glLineWidth(outlineWidth);                             // Set The Line Width ( NEW )
            gl.glCullFace(gl.GL_FRONT);                               // Don't Draw Any Front-Facing Polygons ( NEW )

            gl.glDepthFunc(gl.GL_LEQUAL);                             // Change The Depth Mode ( NEW )
            gl.glColor3fv(outlineColor,0);                              // Set The Outline Color ( NEW )

            gl.glBegin(gl.GL_TRIANGLES);                              // Tell OpenGL What We Want To Draw

            // Loop Through Each Polygon
            for(int i = 0; i < polyNum; i++) {
                // Loop Through Each Vertex ( NEW )
                for(int j = 0; j < 3; j++) {
                    
                    // Send The Vertex Position ( NEW )
                    gl.glVertex3f(polyData[i].Verts[j].Pos.x,
                                polyData[i].Verts[j].Pos.y,
                                polyData[i].Verts[j].Pos.z);
                }
            }

            gl.glEnd();                                               // Tell OpenGL We've Finished
            gl.glDepthFunc(gl.GL_LESS);                               // Reset The Depth-Testing Mode ( NEW )
            gl.glCullFace(gl.GL_BACK);                                // Reset The Face To Be Culled ( NEW )
            gl.glPolygonMode(gl.GL_BACK,gl.GL_FILL);                  // Reset Back-Facing Polygon Drawing Mode ( NEW )
            gl.glDisable(gl.GL_BLEND);                                // Disable Blending ( NEW )
        }
        
     }

     public void reshape(GLAutoDrawable drawable, int xstart,int ystart, int width, int height) {
         GL gl = drawable.getGL();
         GLU glu = new GLU();
         
         height = (height == 0) ? 1 : height;
         
         gl.glViewport(0,0,width,height);
         gl.glMatrixMode(gl.GL_PROJECTION);
         gl.glLoadIdentity();

         glu.gluPerspective(45,(float)width/height,1,1000);
         gl.glMatrixMode(gl.GL_MODELVIEW);
         gl.glLoadIdentity();
     }

     public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged){}    
     
     private Vector3f rotateVector( Matrix4f m, Vector3f v ) {
         
         Vector3f tmp = new Vector3f();
         
         // Rotate around the X axis
         tmp.x = ( m.m00 * v.x ) + ( m.m10 * v.y ) + ( m.m20 * v.z ); 
         
         // Rotate around the Y axis
         tmp.y = ( m.m01 * v.x ) + ( m.m11 * v.y ) + ( m.m21 * v.z );
         
         // Rotate around the Z axis
         tmp.z = ( m.m02 * v.x ) + ( m.m12 * v.y ) + ( m.m22 * v.z );
         
         return tmp;
     }
     
     private void ReadMesh() {
         
         URL fileSource = null;
         try {
             fileSource = new URL("file", "localhost","data/model.txt");
         } catch(java.net.MalformedURLException e) {
             System.out.println("Couldn't locate model, exiting");
             System.exit(0);
         }

         InputStream in = null;
         try {
             in = fileSource.openStream();
         } catch(IOException e) {
             System.out.println("Couldn't load model, exiting");
             System.exit(0);
         }

         polyNum  = byteToInt(readNextFourBytes(in));
         polyData = new POLYGON[polyNum];

         for(int i=0; i<polyData.length;i++) {
             polyData[i] = new POLYGON();
             for(int j =0; j<3; j++) {
                 polyData[i].Verts[j].Nor.x = byteToFloat(readNextFourBytes(in));
                 polyData[i].Verts[j].Nor.y = byteToFloat(readNextFourBytes(in));
                 polyData[i].Verts[j].Nor.z = byteToFloat(readNextFourBytes(in));

                 polyData[i].Verts[j].Pos.x = byteToFloat(readNextFourBytes(in));
                 polyData[i].Verts[j].Pos.y = byteToFloat(readNextFourBytes(in));
                 polyData[i].Verts[j].Pos.z = byteToFloat(readNextFourBytes(in));
             }
         }
     }

     private byte[] readNextFourBytes(InputStream in){
         
         byte[] bytes = new byte[4];

         try {
             for(int i = 0; i<4; i++)
                 bytes[i] = (byte)in.read();
         } catch(Exception e) {
             System.out.println("Error reading file, exiting");
             System.exit(0);
         }
         
         return bytes;
     }

     private int byteToInt(byte[] array) {
         int value = 0;
         for(int i = 0; i < 4; i++) {
             int b = array[i];
             b &= 0xff;
             value |= (b << (i*8));
         }
         
         return value;
     }

     private float byteToFloat(byte[] array){
         int value = 0;
         for(int i = 3; i >= 0; i--) {
             int b = array[i];
             b &= 0xff;
             value |= (b << (i * 8));
         }
        
         return Float.intBitsToFloat(value);
     }

     public void keyReleased(KeyEvent evt){
         keys[evt.getKeyCode()] = false;
     }

      public void keyPressed (KeyEvent evt){

          keys[evt.getKeyCode()] = true;

          if( keys[KeyEvent.VK_ESCAPE] ) {
              animator.stop();
              System.exit(0);
          }

          if(keys[KeyEvent.VK_1])                             // Is The Number 1 Being Pressed? ( NEW )
              outlineDraw = !outlineDraw;                       // Toggle Outline Drawing On/Off ( NEW )

          if(keys[KeyEvent.VK_2])                             // Is The Number 2 Being Pressed? ( NEW )
              outlineSmooth = !outlineSmooth;                   // Toggle Anti-Aliasing On/Off ( NEW )

          if(keys[KeyEvent.VK_UP])                            // Is The Up Arrow Being Pressed? ( NEW )
              outlineWidth++;                                   // Increase Line Width ( NEW )

          if(keys[KeyEvent.VK_DOWN])                          // Is The Down Arrow Being Pressed? ( NEW )
              outlineWidth--;                                   // Decrease Line Width ( NEW )
              
          if( keys[KeyEvent.VK_RIGHT] ) {
              yrot += 2.0f;
          }
          
          if( keys[KeyEvent.VK_LEFT] ) {
              yrot -= 2.0f;
          }
     }

     public void keyTyped(KeyEvent evt) {}     
}

// A Structure To Hold A Single Vertex ( NEW )
class VERTEX{
    
    // Vertex Normal
    Vector3f Nor = new Vector3f();
    
    // Vertex Position
    Vector3f Pos = new Vector3f();
    
}

// A Structure To Hold A Single Polygon ( NEW )
class POLYGON{                                             
    VERTEX Verts[] = new VERTEX[3];
    POLYGON() {
        for(int i =0; i<3; i++)
            Verts[i] = new VERTEX();
    }
}
