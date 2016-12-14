
public class GameUtils {

    /**
     * Set the tile on a specified position in the gameboard.
     *
     * @param pos  The position in the gameboard matrix.
     * @param tile The type of tile to paint in specified position
     */
    protected void setGameboardState(final Position pos, final GameTile tile, final GameTile[][] gameboard) {
        setGameboardState(pos.getX(), pos.getY(), tile, gameboard);
    }

    /**
     * Set the tile on a specified position in the gameboard.
     *
     * @param x    Coordinate in the gameboard matrix.
     * @param y    Coordinate in the gameboard matrix.
     * @param tile The type of tile to paint in specified position
     */
    protected void setGameboardState(final int x, final int y,
                                     final GameTile tile, final GameTile[][] gameboard) {
        gameboard[x][y] = tile;
    }

}
