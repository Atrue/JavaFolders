package game.engine;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;

//responsible for painting the map for the client and tracking nodes

public class TileMap extends ImageView{
    private final static String TILESET_64 = "game/engine/res/tileset/tet64.png";
    private final static String TILESET_128 = "game/engine/res/tileset/tile-128.png";

    private final int TILE_GRASS = 0;
    private final int TILE_HORISONTAL = 1;
    private final int TILE_VERTICAL = 2;
    private final int TILE_BOT_RIGHT = 3;
    private final int TILE_BOT_LEFT = 4;
    private final int TILE_RIGHT_TOP = 5;
    private final int TILE_LEFT_TOP = 6;
    
    
    
    private int[][] map;
    private final int RESOLUTION_WIDTH; //Screen Resolution passed from main
    private final int RESOLUTION_HEIGHT;
    private final int TILE_LENGTH_X;    //Length of tiles
    private final int TILE_LENGTH_Y;
    private final int OFFSET_X;         //Offsets used for printing last tile row
    private final int OFFSET_Y;
    private final boolean OFFSET_X_FLAG;//Used for painting the edge of the tilemap to avoid ArrayOutOfBoundsException
    private final boolean OFFSET_Y_FLAG;
	private int[][] path;

    public TileMap(int mapWidth , int mapHeight, int[][] map){
        RESOLUTION_WIDTH  = mapWidth;
        RESOLUTION_HEIGHT  = mapHeight;

        TILE_LENGTH_X = (int) Math.ceil(mapWidth / 32d);
        TILE_LENGTH_Y = (int) Math.ceil(mapHeight / 32d);
        System.out.println(TILE_LENGTH_Y +" d " + TILE_LENGTH_X);
        OFFSET_X = TILE_LENGTH_X * 32 - RESOLUTION_WIDTH;
        OFFSET_Y = TILE_LENGTH_Y * 32 - RESOLUTION_HEIGHT;

        if(OFFSET_X == 0){
            OFFSET_X_FLAG = false;
        }
        else{
            OFFSET_X_FLAG = true;
        }


        if(OFFSET_Y == 0){
            OFFSET_Y_FLAG = false;
        }
        else{
            OFFSET_Y_FLAG = true;
        }

        this.map = map;
        repaint();
    }

    //Method paints the map using the given map array and tileset
    public void repaint(){

        //loads tileset
        Image tileset = loadTileSet();

        //Pixel reader
        PixelReader tilereader = tileset.getPixelReader();

        //buffer for aRGB 64x64 tiles
        byte[] buffer = new byte[32 * 32 * 4];
        WritablePixelFormat<ByteBuffer> picFormat = WritablePixelFormat.getByteBgraInstance();

        //Pixel writer
        WritableImage paintedMap = new WritableImage(RESOLUTION_WIDTH , RESOLUTION_HEIGHT);
        PixelWriter tileWriter = paintedMap.getPixelWriter();

        //reads map node than paints the tile
        for(int x = 0; x < TILE_LENGTH_X; x++){
            for(int y = 0; y < TILE_LENGTH_Y; y++ ){
                //populate each rectangle with tile from PixelReader
                switch(map[x][y]){
                    case TILE_GRASS: //paint grass(OPEN NODE)
                        tilereader.getPixels(0 , 0 , 32 , 32 , picFormat , buffer , 0 , 128);
                        break;
                    case TILE_HORISONTAL: //paint horizontal path
                        tilereader.getPixels(96 , 32 , 32 , 32 , picFormat , buffer , 0 , 128);
                        break;
                    case TILE_VERTICAL: //paint vertical path
                        tilereader.getPixels(96 , 0 , 32 , 32 , picFormat , buffer , 0 , 128);
                        break;
                    case TILE_BOT_RIGHT: //paint corner EAST TO NORTH
                    	tilereader.getPixels(32 , 0 , 32 , 32 , picFormat , buffer , 0 , 128);
                        break;
                    case TILE_BOT_LEFT: //paint corner SOUTH TO EAST
                    	tilereader.getPixels(64 , 0 , 32 , 32 , picFormat , buffer , 0 , 128);
                    	break;
                    case TILE_LEFT_TOP: //paint corner NORTH TO EAST
                    	tilereader.getPixels(64 , 32 , 32 , 32 , picFormat , buffer , 0 , 128);
                    	break;
                    case TILE_RIGHT_TOP: //paint corner EAST TO SOUTH
                    	tilereader.getPixels(32 , 32 , 32 , 32 , picFormat , buffer , 0 , 128);
                    	break;
                    case 7: //paint grass and tower
                        tilereader.getPixels(384 , 512 , 32 , 32 , picFormat , buffer , 0 , 128);
                        break;
                }
                if(y == TILE_LENGTH_Y - 1 & OFFSET_Y_FLAG){
                    tileWriter.setPixels(x * 32 , y * 32, 32 , OFFSET_Y , picFormat , buffer , 0 , 128);
                }
                else{
                    tileWriter.setPixels(x * 32 , y * 32, 32 , 32 , picFormat , buffer , 0 , 128);
                }
            }
        };
        this.setImage(paintedMap);
    }
 

    //loads the tileset for paintMap
    private Image loadTileSet(){
        //to do: add higher resolution 128x128 tiles for higher resolution
        //oh yeah, I still need to add higher resoltion support

        //tilesize 64x64
        return new Image(TILESET_64);
    }

    //sets the map node for the given coordinates to input value than repaints adjustment
    public void setMapNode(int xCord , int yCord , int updatedValue){
        map[xCord][yCord] = updatedValue;
        this.repaint();
    }

    //checks to see if the node is open
    public boolean nodeOpen(int xCord , int yCord){
        if(map[xCord][yCord] != 0){
            return false;
        }
        return true;
    }

    //returns a path for monster animations
    //monsters travel on straight path between corners
    public ArrayList<Coordinate> getPath(){
        ArrayList<Coordinate> pathXY = new ArrayList<Coordinate>();
        for(int i=0;i<path.length;i++){
        	pathXY.add(new Coordinate(path[i][0], path[i][1]));
        }
        /*
        
        boolean scanSwitch = false;
        int previousY = 0;
        int previousX = 0;

        //searches first column for first tile which is to be spawn location
        for(int y = 0; !scanSwitch; y++){
            if(map[0][y] > 0){
                pathXY.add(new Coordinate(0 , y));
                scanSwitch = true;
                previousY = y;
            }//end if - found first tile
        }//end for - found first tile

        //searches for corners by switching the search axis after each new corner is added
        findpath:
        for(int x = 0; scanSwitch; x++){
            //adds the final path coordinate before exiting loop
            if(x == TILE_LENGTH_X){
                pathXY.add(new Coordinate(x - 1 , previousY));
                break findpath;
            }//end if - no more corners
            if (map[x][previousY] > 2 & map[x][previousY] <7 & x != previousX){
                pathXY.add(new Coordinate(x , previousY));
                scanSwitch = false;
                previousX = x;
            }// end if - found corner
            for(int y = 0; !scanSwitch; y++){
                if (map[x][y] > 2 & map[x][y] <7 & y != previousY){
                    pathXY.add(new Coordinate(x , y));
                    scanSwitch = true;
                    previousY = y;
                }// end if - found corner
            }//end for - column scan
        }//end for - row scan
        
        
        */
        
        return pathXY;
    }
}
