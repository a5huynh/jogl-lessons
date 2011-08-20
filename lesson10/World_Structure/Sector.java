package World_Structure;

import java.util.*;

//holds a series of polygons, in this case triangles
public class Sector
{
    //holds 3 vertices, points
    class Triangle
    {    
        //holds the values for each Vertex in a triangle
        class Vertex
        {
            private float x;  // 3D coords
            private float y;
            private float z;
            private float u;  //texture coords
            private float v;

            /** A Vertex of a Triangle.
             * @param x X-Coordinate
             * @param y Y-Coordinate
             * @param z Z-Coordinate
             * @param u U-Coordinate, for the texture
             * @param v V-Coordinate, for the texture
             */                
            public Vertex(float x, float y, float z, float u, float v)
            { this.x = x; this.y = y; this.z = z; this.u = u; this.v = v; }

            /** Return the X coordinate
             * @return Return the X coordinate
             */                
            public float getX() { return x; }
            /** Return the Y coordinate
             * @return Return the Y coordinate
             */                
            public float getY() { return y; }
            /** Return the Z coordinate
             * @return Return the Z coordinate
             */                
            public float getZ() { return z; }
            /** Return the U coordinate, for textures
             * @return Return the U coordinate, for textures
             */                
            public float getU() { return u; }
            /** Return the V coordinate, for textures
             * @return Return the V coordinate, for textures
             */                
            public float getV() { return v; }
            /** Returns the Vertex information in an array of floats
             * @return Return an array of floats that are the coord info
             */                
            public float[] getVertexInfo() { float[] f = { x, y, z, u, v }; return f; }

            /** Sets the X coordinate to x
             * @param x Set the X coordinate to x
             */                
            public void setX(float x) { this.x = x; }
            /** Set the Y coordinate to y
             * @param y Set the Y coordinate to y
             */                
            public void setY(float y) { this.y = y; }
            /** Set the Z coordinate to z
             * @param z Set the Z coordinate to z
             */                
            public void setZ(float z) { this.z = z; }
            /** Set the U coordinate to u
             * @param u Set the U coordinate to u
             */                
            public void setU(float u) { this.u = u; }
            /** Set the V coordinate to V
             * @param v Set the V coordinate to V
             */                
            public void setV(float v) { this.v = v; }
            //this last one will crash unless you have EXACTLY 5 elements in the array
            /** Set the Vertex info using the data in the array that is sent as a parameter
             * @param f Set the Vertex information using the float array f
             */                
            public void setVertexInfo(float[] f) 
            { f[0] = x; f[1] = y; f[2] = z; f[3] = u; f[4] = v; }
        }

        private final int NUM_VERTICES = 3;
        private Vertex[] vertex = new Vertex[NUM_VERTICES];
        
        /** Triangle class that has Vertices and has methods for
         * manipulating those Vertices.
         * @param f A multidimensional array; the first [] is
         * for the number of the vertex for that
         * Triangle and the second [] is for the value
         * x, y, z, u, or v
         */            
        public Triangle(float[][] f)
        {
            vertex[0] = new Vertex(f[0][0],
                                    f[0][1],
                                    f[0][2],
                                    f[0][3],
                                    f[0][4]);
            vertex[1] = new Vertex(f[1][0],
                                    f[1][1],
                                    f[1][2],
                                    f[1][3],
                                    f[1][4]);
            vertex[2] = new Vertex(f[2][0],
                                    f[2][1],
                                    f[2][2],
                                    f[2][3],
                                    f[2][4]);
        }
        
        /** Get the information, coordinates for placement and texture,
         * about the vertex specified.
         * @param which Which vertex to get info from
         * @return An array of float's are sent back holding the vertex coords
         */            
        public float[] getVertexInfo(int which) { return vertex[which].getVertexInfo(); }
        /** Sets the specified vertex's information using the
         * array sent.
         * @param which Which vertex to set
         * @param f An array of floats holding coordinate information
         */            
        public void setVertexInfo(int which, float[] f) { vertex[which].setVertexInfo(f); }
        /** Gets the information of the Triangle.
         * @return Sends back a multidimensional array of floats
         * detailing the information of the Triangle
         */            
        public float[][] getTriangleInfo()
        {
            float[][] f = { getVertexInfo(0), getVertexInfo(1), getVertexInfo(2) };
            //f[0] = getVertexInfo(0);
            //f[1] = getVertexInfo(1);
            //f[2] = getVertexInfo(2);
            return f;
        }
    }
    
    private int numTriangles;
    Triangle[] tri = null;
    
    /** A class that holds polygons, in this case Triangles,
     * and methods that manipulate those polygons.
     * @param s Takes an array of values so that the
     * polygons can be created, the first line
     * must be of format NUMPOLLIES ##<br>
     * and the others must have a format of
     * ## ## ## ## ##<br>
     * 5 numbers
     */        
    public Sector(String[] s)
    {
        StringTokenizer sT = new StringTokenizer(s[0], " ");
        String t = sT.nextToken();
        t = sT.nextToken();
        numTriangles = (new Integer(t)).intValue();
        tri = new Triangle[numTriangles];
        createTris(s);
    }
    
    /** Get the number of triangles
     * @return Returns the number of triangles
     */        
    public int getNumTriangles()
    {
        return numTriangles;
    }
    
    /** Return the coordinates of the triangle and it's
     * texture info.
     * @param whichTri Which Triangle of the number of triangles do you want
     * @return Return a multidimensional array of floats
     */        
    public float[][] getTriangleInfo(int whichTri)
    {
        return tri[whichTri].getTriangleInfo();
    }
    
    /** StringTokenizer breaks the strings up and with for loops
     * the information is added to it's correct Triangle
     * @param s An array of strings; formating of strings is specific
     */        
    public void createTris(String[] s)
    {
        int j = 0;
        int k = 1;
        String str;
        float[][] vertices = new float[3][5];
        for(int i = 1; i < (numTriangles + 1); i++)
        {
            for (j = 0; j < 3; j++)
            {
                str = s[k];
                StringTokenizer sT = new StringTokenizer(str, " ");
                vertices[j][0] = (new Float(sT.nextToken())).floatValue();
                vertices[j][1] = (new Float(sT.nextToken())).floatValue();
                vertices[j][2] = (new Float(sT.nextToken())).floatValue();
                vertices[j][3] = (new Float(sT.nextToken())).floatValue();
                vertices[j][4] = (new Float(sT.nextToken())).floatValue();
                k++;
            }
            tri[(i-1)] = new Triangle(vertices);
        }
    }
}