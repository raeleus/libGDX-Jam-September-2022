package com.ray3k.template;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Compatible with Ogmo Community Edition 3.2.2. Create an instance of OgmoReader, add an OgmoListener, then call
 * OgmoReader#readFile().
 */
public class OgmoReader {
    private Array<OgmoListener> ogmoListeners;
    private JsonReader jsonReader;
    
    public OgmoReader() {
        ogmoListeners = new Array<>();
        jsonReader = new JsonReader();
    }
    
    public void readFile(FileHandle file) {
        JsonValue root = jsonReader.parse(file);
        String version = root.getString("ogmoVersion");
        int levelWidth = root.getInt("width");
        int levelHeight = root.getInt("height");
        int levelOffsetX = root.getInt("offsetX");
        int levelOffsetY = root.getInt("offsetY");
        ObjectMap<String, OgmoValue> valuesMap = new ObjectMap<>();
        if (root.has("values")) for (JsonValue value : root.get("values")) {
            OgmoValue ogmoValue = new OgmoValue();
            ogmoValue.name = value.name;
            ogmoValue.value = value.asString();
            valuesMap.put(ogmoValue.name, ogmoValue);
        }
        
        for (OgmoListener ogmoListener : ogmoListeners) {
            ogmoListener.level(version, levelWidth, levelHeight, levelOffsetX, levelOffsetY, valuesMap);
        }
        
        if (root.has("layers")) {
            for (JsonValue child : root.get("layers").iterator()) {
                for (OgmoListener ogmoListener : ogmoListeners) {
                    ogmoListener.layer(child.getString("name"),
                            child.getInt("gridCellWidth"), child.getInt("gridCellheight"),
                            child.getInt("offsetX"), child.getInt("offsetY"));
                }
                
                if (child.has("entities")) {
                    for (JsonValue entity : child.get("entities").iterator()) {
                        for (OgmoListener ogmoListener : ogmoListeners) {
                            Array<EntityNode> nodes = new Array<>();
                            if (entity.has("nodes")) for (JsonValue coordinate : entity.get("nodes")) {
                                EntityNode node = new EntityNode();
                                node.x = coordinate.getInt("x");
                                node.y = levelHeight - coordinate.getInt("y");
                                nodes.add(node);
                            }
                            valuesMap = new ObjectMap<>();
                            if (entity.has("values")) for (JsonValue value : entity.get("values")) {
                                OgmoValue ogmoValue = new OgmoValue();
                                ogmoValue.name = value.name;
                                ogmoValue.value = value.asString();
                                valuesMap.put(value.name, ogmoValue);
                            }
                            ogmoListener.entity(entity.getString("name"),
                                    entity.getInt("id"), entity.getInt("x"), levelHeight - entity.getInt("y"),
                                    entity.getInt("width", 0), entity.getInt("height", 0),
                                    entity.getBoolean("flippedX", false), entity.getBoolean("flippedY", false),
                                    entity.getInt("originX", 0), entity.getInt("originY", 0),
                                    (360 - entity.getInt("rotation", 0)) % 360, nodes, valuesMap);
                        }
                    }
                } else if (child.has("grid")) {
                    int cellWidth = child.getInt("gridCellWidth");
                    int cellHeight = child.getInt("gridCellHeight");
                    int columns = child.getInt("gridCellsX");
                    
                    int column = 0;
                    int row = 0;
                    for (JsonValue grid : child.get("grid").iterator()) {
                        for (OgmoListener ogmoListener : ogmoListeners) {
                            ogmoListener.grid(column, row, column * cellWidth, levelHeight - (row + 1) * cellHeight, cellWidth, cellHeight, grid.asInt());
                        }
                        
                        column++;
                        if (column >= columns) {
                            column = 0;
                            row++;
                        }
                    }
                } else if (child.has("grid2D")) {
                    int cellWidth = child.getInt("gridCellWidth");
                    int cellHeight = child.getInt("gridCellHeight");
                    
                    int row = 0;
                    for (JsonValue gridY : child.get("grid2D").iterator()) {
                        int column = 0;
                        for (JsonValue grid : gridY.iterator()) {
                            for (OgmoListener ogmoListener : ogmoListeners) {
                                ogmoListener.grid(column, row, column * cellWidth, levelHeight - (row + 1) * cellHeight, cellWidth, cellHeight, grid.asInt());
                            }
                            
                            column++;
                        }
                        row++;
                    }
                } else if (child.has("decals")) {
                    String folder = child.getString("folder");
                    for (JsonValue decal : child.get("decals").iterator()) {
                        for (OgmoListener ogmoListener : ogmoListeners) {
                            valuesMap = new ObjectMap<>();
                            if (decal.has("values")) for (JsonValue value : decal.get("values")) {
                                OgmoValue ogmoValue = new OgmoValue();
                                ogmoValue.name = value.name;
                                ogmoValue.value = value.asString();
                                valuesMap.put(value.name, ogmoValue);
                            }
                            
                            ogmoListener.decal(decal.getInt("x"), levelHeight - decal.getInt("y"), decal.getFloat("originX", .5f), decal.getFloat("originY", .5f),
                                    decal.getFloat("scaleX", 1f), decal.getFloat("scaleY", 1f),
                                    (360 - decal.getInt("rotation", 0)) % 360, decal.getString("texture"), folder, valuesMap);
                        }
                    }
                } else if (child.has("data")) {
                    String tileSet = child.getString("tileSet");
                    int cellWidth = child.getInt("gridCellWidth");
                    int cellHeight = child.getInt("gridCellHeight");
                    int columns = child.getInt("gridCellsX");
                    
                    int column = 0;
                    int row = 0;
                    for (JsonValue data : child.get("data").iterator()) {
                        for (OgmoListener ogmoListener : ogmoListeners) {
                            ogmoListener.tile(tileSet, column, row, column * cellWidth, levelHeight - row * cellHeight, data.asInt());
                        }
                        
                        column++;
                        if (column >= columns) {
                            column = 0;
                            row++;
                        }
                    }
                } else if (child.has("data2D")) {
                    String tileSet = child.getString("tileSet");
                    int cellWidth = child.getInt("gridCellWidth");
                    int cellHeight = child.getInt("gridCellHeight");
                    
                    int row = 0;
                    for (JsonValue dataY : child.get("data2D").iterator()) {
                        int column = 0;
                        for (JsonValue data : dataY.iterator()) {
                            for (OgmoListener ogmoListener : ogmoListeners) {
                                ogmoListener.tile(tileSet, column, row, column * cellWidth, levelHeight - row * cellHeight, data.asInt());
                            }
                            
                            column++;
                        }
                        row++;
                    }
                } else if (child.has("dataCoords")) {
                    String tileSet = child.getString("tileSet");
                    int cellWidth = child.getInt("gridCellWidth");
                    int cellHeight = child.getInt("gridCellHeight");
                    int columns = child.getInt("gridCellsX");
                    
                    int column = 0;
                    int row = 0;
                    for (JsonValue data : child.get("dataCoords").iterator()) {
                        for (OgmoListener ogmoListener : ogmoListeners) {
                            ogmoListener.tile(tileSet, column, row, column * cellWidth, levelHeight - row * cellHeight, data.getInt(0), data.getInt(1));
                        }
                        
                        column++;
                        if (column >= columns) {
                            column = 0;
                            row++;
                        }
                    }
                } else if (child.has("dataCoords2D")) {
                    String tileSet = child.getString("tileSet");
                    int cellWidth = child.getInt("gridCellWidth");
                    int cellHeight = child.getInt("gridCellHeight");
                    
                    int row = 0;
                    for (JsonValue dataY : child.get("dataCoords2D").iterator()) {
                        int column = 0;
                        for (JsonValue data : dataY.iterator()) {
                            for (OgmoListener ogmoListener : ogmoListeners) {
                                ogmoListener.tile(tileSet, column, row, column * cellWidth, levelHeight - row * cellHeight, data.getInt(0), data.getInt(1));
                            }
                            
                            column++;
                        }
                        row++;
                    }
                }
                
                for (OgmoListener ogmoListener : ogmoListeners) {
                    ogmoListener.layerComplete();
                }
            }
        }
        
        for (OgmoListener ogmoListener : ogmoListeners) {
            ogmoListener.levelComplete();
        }
        
    }
    
    public OgmoReader addListener(OgmoListener ogmoListener) {
        ogmoListeners.add(ogmoListener);
        return this;
    }
    
    public void clearListeners() {
        ogmoListeners.clear();
    }
    
    public Array<OgmoListener> getOgmoListeners() {
        return ogmoListeners;
    }
    
    /**
     * This listener is called when the OgmoReader reads a file and finds a layer. The associated methods are called
     * depending on the type of layer detected.
     */
    public interface OgmoListener {
        /**
         * Called first when OgmoReader#readFile() is called.
         * @param ogmoVersion The version of Ogmo Editor used to create the level.
         * @param width The width of the level.
         * @param height The height of the level.
         * @param offsetX Currently unused by the editor.
         * @param offsetY Currently unused by the editor.
         * @param valuesMap An ObjectMap of the user specified values for the level by value name.
         */
        void level(String ogmoVersion, int width, int height, int offsetX, int offsetY, ObjectMap<String, OgmoValue> valuesMap);
        
        /**
         * Called when a layer has been found while iterating through the level JSON.
         * @param name The name of the layer.
         * @param gridCellWidth The width of the cell in pixels.
         * @param gridCellHeight The height of the cell in pixels.
         * @param offsetX Currently unused by the editor.
         * @param offsetY Currently unused by the editor.
         */
        void layer(String name, int gridCellWidth, int gridCellHeight, int offsetX, int offsetY);
        
        /**
         * Called for every entity found in an entity layer.
         * @param name The name of the entity type.
         * @param id A unique identifier for the entity, sequentially numbered as they were placed on the layer.
         * @param x The x position of the entity.
         * @param y The y position of the entity, adjusted to y-up coordinates.
         * @param width The width of the entity. May be 0 if scaling was not enabled for the entity.
         * @param height The height of the entity.
         * @param flippedX If the entity is flipped horizontally.
         * @param flippedY If the entity is flipped vertically.
         * @param originX The horizontal origin offset of the entity
         * @param originY The vertical origin offset of the entity, adjusted to y-up coordinates.
         * @param rotation The rotation of the entity in degrees wound CCW.
         * @param nodes The nodes defined for the entity. May be a 0 length Array if there are no nodes.
         * @param valuesMap An ObjectMap of the user specified values for the entity by value name.
         */
        void entity(String name, int id, int x, int y, int width, int height, boolean flippedX, boolean flippedY, int originX, int originY, int rotation, Array<EntityNode> nodes, ObjectMap<String, OgmoValue> valuesMap);
        
        /**
         * Called for every grid point found in a grid layer.
         * @param col The column number of the grid.
         * @param row The row number of the grid.
         * @param x The x coordinate of the grid.
         * @param y The y coordinate of the grid, adjusted to y-up coordinates.
         * @param id The unique ID of the grid numbered from 0.
         */
        void grid(int col, int row, int x, int y, int width, int height, int id);
        
        /**
         * Called for every decal image placed in a decal layer.
         * @param x The x coordinate of the decal.
         * @param y The y coordinate of the decal, adjusted to y-up coordinates.
         * @param originX The origin as percentage of the total width;
         * @param originY The origin as percentage of the total height;
         * @param scaleX The horizontal scale multiplier. 1.0f is the original scale of the image.
         * @param scaleY The vertical scale multiplier. 1.0f is the original scale of the image.
         * @param rotation The rotation of the decal in degrees wound CCW.
         * @param texture The name of the texture linked by the decal.
         * @param folder The name of the folder that the decal layer is linked to.
         */
        void decal(int x, int y, float originX, float originY, float scaleX, float scaleY, int rotation, String texture, String folder, ObjectMap<String, OgmoValue> valuesMap);
        
        /**
         * Called for every tile placed in a tile layer when the layer uses id's.
         * @param tileSet The name of the tile set that this layer is associated with.
         * @param col The column number of the tile.
         * @param row The row number of the tile.
         * @param x The x coordinate of the tile.
         * @param y The y coordinate of the tile, adjusted to y-up coordinates.
         * @param id The ID number of the tile in the tileset as numbered from the top left.
         */
        void tile(String tileSet, int col, int row, int x, int y, int id);
        
        /**
         * Called for every tile placed in a tile layer when the layer uses XY coordinates.
         * @param tileSet The name of the tile set that this layer is associated with.
         * @param col The column number of the tile.
         * @param row The row number of the tile.
         * @param x The x coodinate of the tile.
         * @param y The y coodinate of the tile, adjusted to y-up coordinates.
         * @param tileX The column number of the tile in the tileset.
         * @param tileY The row number of the tile in the tileset as numbered from top down.
         */
        void tile(String tileSet, int col, int row, int x, int y, int tileX, int tileY);
        
        /**
         * Called after the layer is completely read.
         */
        void layerComplete();
        
        /**
         * Called after the level is completely read.
         */
        void levelComplete();
    }
    
    public static class OgmoAdapter implements OgmoListener {
        /**
         * Called first when OgmoReader#readFile() is called.
         *
         * @param ogmoVersion The version of Ogmo Editor used to create the level.
         * @param width       The width of the level.
         * @param height      The height of the level.
         * @param offsetX     Currently unused by the editor.
         * @param offsetY     Currently unused by the editor.
         * @param valuesMap   An ObjectMap of the user specified values for the level by value name.
         */
        @Override
        public void level(String ogmoVersion, int width, int height, int offsetX, int offsetY, ObjectMap<String, OgmoValue> valuesMap) {
        
        }
        
        /**
         * Called when a layer has been found while iterating through the level JSON.
         *
         * @param name           The name of the layer.
         * @param gridCellWidth  The width of the cell in pixels.
         * @param gridCellHeight The height of the cell in pixels.
         * @param offsetX        Currently unused by the editor.
         * @param offsetY        Currently unused by the editor.
         */
        @Override
        public void layer(String name, int gridCellWidth, int gridCellHeight, int offsetX, int offsetY) {
        
        }
        
        /**
         * Called for every entity found in an entity layer.
         *
         * @param name      The name of the entity type.
         * @param id        A unique identifier for the entity, sequentially numbered as they were placed on the layer.
         * @param x         The x position of the entity.
         * @param y         The y position of the entity, adjusted to y-up coordinates.
         * @param width     The width of the entity. May be 0 if scaling was not enabled for the entity.
         * @param height    The height of the entity.
         * @param flippedX  If the entity is flipped horizontally.
         * @param flippedY  If the entity is flipped vertically.
         * @param originX   The horizontal origin offset of the entity
         * @param originY   The vertical origin offset of the entity, adjusted to y-up coordinates.
         * @param rotation  The rotation of the entity in degrees wound CCW.
         * @param nodes     The nodes defined for the entity. May be a 0 length Array if there are no nodes.
         * @param valuesMap An ObjectMap of the user specified values for the entity by value name.
         */
        @Override
        public void entity(String name, int id, int x, int y, int width, int height, boolean flippedX, boolean flippedY, int originX, int originY, int rotation, Array<EntityNode> nodes, ObjectMap<String, OgmoValue> valuesMap) {
        
        }
        
        /**
         * Called for every grid point found in a grid layer.
         *
         * @param col The column number of the grid.
         * @param row The row number of the grid.
         * @param x   The x coordinate of the grid.
         * @param y   The y coordinate of the grid, adjusted to y-up coordinates.
         * @param id  The unique ID of the grid numbered from 0.
         */
        @Override
        public void grid(int col, int row, int x, int y, int width, int height, int id) {
        
        }
        
        /**
         * Called for every decal image placed in a decal layer.
         *
         * @param x The x coordinate of the decal.
         * @param y The y coordinate of the decal, adjusted to y-up coordinates.
         * @param originX The origin as percentage of the total width;
         * @param originY The origin as percentage of the total height;
         * @param scaleX   The horizontal scale multiplier. 1.0f is the original scale of the image.
         * @param scaleY   The vertical scale multiplier. 1.0f is the original scale of the image.
         * @param rotation The rotation of the decal in degrees wound CCW.
         * @param texture  The name of the texture linked by the decal.
         * @param folder   The name of the folder that the decal layer is linked to.
         */
        @Override
        public void decal(int x, int y, float originX, float originY, float scaleX, float scaleY, int rotation, String texture, String folder, ObjectMap<String, OgmoValue> valuesMap) {
        
        }
        
        /**
         * Called for every tile placed in a tile layer when the layer uses id's.
         *
         * @param tileSet The name of the tile set that this layer is associated with.
         * @param col     The column number of the tile.
         * @param row     The row number of the tile.
         * @param x       The x coordinate of the tile.
         * @param y       The y coordinate of the tile, adjusted to y-up coordinates.
         * @param id      The ID number of the tile in the tileset as numbered from the top left.
         */
        @Override
        public void tile(String tileSet, int col, int row, int x, int y, int id) {
        
        }
        
        /**
         * Called for every tile placed in a tile layer when the layer uses XY coordinates.
         *
         * @param tileSet The name of the tile set that this layer is associated with.
         * @param col     The column number of the tile.
         * @param row     The row number of the tile.
         * @param x       The x coodinate of the tile.
         * @param y       The y coodinate of the tile, adjusted to y-up coordinates.
         * @param tileX   The column number of the tile in the tileset.
         * @param tileY   The row number of the tile in the tileset as numbered from top down.
         */
        @Override
        public void tile(String tileSet, int col, int row, int x, int y, int tileX, int tileY) {
        
        }
        
        /**
         * Called after the layer is completely read.
         */
        @Override
        public void layerComplete() {
        
        }
        
        /**
         * Called after the level is completely read.
         */
        @Override
        public void levelComplete() {
        
        }
    }
    
    public class EntityNode {
        public int x;
        public int y;
    }
    
    public class OgmoValue {
        public String name;
        private String value;
        
        public int asInt() {
            return Integer.parseInt(value);
        }
        
        public float asFloat() {
            return Float.parseFloat(value);
        }
        
        public boolean asBoolean() {
            return Boolean.parseBoolean(value);
        }
        
        public String asString() {
            return value;
        }
        
        public Color asColor() {
            return Color.valueOf(value);
        }
    }
}