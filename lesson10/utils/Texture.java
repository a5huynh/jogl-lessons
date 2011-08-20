package utils;

import java.nio.ByteBuffer;

public class Texture {
    private ByteBuffer data;
    private int height, width;
    
    public Texture(ByteBuffer data, int width, int height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }
    
    public ByteBuffer getData() {
        return data;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void destroy(){
        data   = null;
        width  = -1;
        height = -1;
    }    
}
