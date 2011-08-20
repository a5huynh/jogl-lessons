package utils;

import java.awt.*;
import java.awt.image.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import javax.imageio.*;

// JOGL Packages
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

public class PNGLoader extends Loader {

    public static Texture readPNGImage(String resourceName) {
        ByteBuffer dest = null;
        BufferedImage img = null;
        
        try {
            URL url = getResource(resourceName);
            if( url == null ) {
                throw new RuntimeException("Error reading resource " + resourceName);
            }

            img = ImageIO.read(url);
            java.awt.geom.AffineTransform tx = java.awt.geom.AffineTransform.getScaleInstance(1, -1);
            tx.translate(0, -img.getHeight(null));
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            img = op.filter(img, null);
        } catch( IOException e) { throw new RuntimeException(e); }
         
        switch(img.getType()) {

        case BufferedImage.TYPE_3BYTE_BGR:
        case BufferedImage.TYPE_CUSTOM: {               
            byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
            dest = ByteBuffer.allocate(data.length);
            dest.order(ByteOrder.nativeOrder());
            dest.put(data, 0, data.length);
            dest.position(0);
            break;
        }    
        case BufferedImage.TYPE_INT_RGB: {
            int[] data = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
            dest = ByteBuffer.allocateDirect(data.length * BufferUtil.SIZEOF_INT);
            dest.order(ByteOrder.nativeOrder());
            dest.asIntBuffer().put(data, 0, data.length);
            dest.position(0);
            break;
        }    
        default:
            throw new RuntimeException("Unsupported Image type " + img.getType());
        }
        
        return new Texture(dest, img.getWidth(), img.getHeight());
    }   
}
  
  
