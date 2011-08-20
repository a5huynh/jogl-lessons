package utils;

import java.awt.*;
import java.awt.image.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import javax.imageio.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

/**
 *
 * @author Omicron
 */
public class TextureLoader {
    
    public TextureLoader() {}
    
    public static Texture readImage(String resourceName) {
        
        if(resourceName.endsWith(".bmp")) {
            
            return BMPLoader.readBMPImage(resourceName, false);
            
        } else if(resourceName.endsWith(".png")) {
            
            return PNGLoader.readPNGImage(resourceName);
            
        }
        
        return null;
    }
}
