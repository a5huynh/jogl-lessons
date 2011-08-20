import java.awt.*;
import java.awt.event.*;

// JOGL Packages
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

public class Main {
    
    static Animator animator = null;
 
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Frame frame = new Frame("JOGL Fun");
        GLCanvas canvas = new GLCanvas();
        
        //canvas.addGLEventListener(new GlassBoxRenderer(animator));
        canvas.addGLEventListener( new CelShader(animator) );
        
        frame.add(canvas);
        frame.setSize(640, 480);
        animator = new Animator(canvas);
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                animator.stop();
                System.exit(0);
            }
        });
        
        // frame.show(); DEPRECATED
        frame.setVisible(true);
        animator.start();
        canvas.requestFocus();
    }
}
