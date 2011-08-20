package utils;

import java.net.*;

public class Loader {
    public final static URL getResource(final String filename) {
        URL url = ClassLoader.getSystemResource(filename);

        if( url == null ) {
            try {
                url = new URL("file", "localhost", filename);
            } catch(Exception urlException){}
        }

        return url;
    }
}
