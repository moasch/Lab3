import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Sample game for illustration. Intentionally stupid; more interesting
 * games to be provided by students.
 * <p>
 * Initially 20 gold coins are randomly placed in the matrix. The red gold
 * collector aims to collect these coins which disappear after collection. Each
 * coin is randomly moved to a new position every n moves, where n is the number
 * of remaining coins. The game is won when all coins are collected and lost when
 * collector leaves game board.
 */
public class GoldModel implements GameModel {
    public enum Directions {
        EAST(1, 0),
        WEST(-1, 0),
        NORTH(0, -1),
        SOUTH(0, 1),
        NONE(0, 0);

        private final int xDelta;
        private final int yDelta;

        Directions(final int xDelta, final int yDelta) {
            this.xDelta = xDelta;
            this.yDelta = yDelta;
        }

        public int getXDelta() {
            return this.xDelta;
        }

        public int getYDelta() {
            return this.yDelta;
        }
    }

    private static final int COIN_START_AMOUNT = 20;

	/*
     * The following GameTile objects are used only
	 * to describe how to paint the specified item.
	 * 
	 * This means that they should only be used in
	 * conjunction with the get/setGameboardState()
	 * methods.
	 */

    /**
     * Graphical representation of a coin.
     */
    private static final GameTile COIN_TILE = new RoundTile(new Color(255, 215,
            0),
            new Color(255, 255, 0), 2.0);

    /**
     * Graphical representation of the collector
     */
    private static final GameTile COLLECTOR_TILE = new RoundTile(Color.BLACK,
            Color.RED, 2.0);

    /**
     * Graphical representation of a blank tile.
     */
    private static final GameTile BLANK_TILE = new BlankTile();

    /**
     * A list containing the positions of all coins.
     */
    private final List<Position> coins = new ArrayList<Position>();

    /**
     * A GameUtils objects which is used for calling of method?
     */
    private final GameUtils gameUtils = new GameUtils();
    /*
	 * The declaration and object creation above uses the new language feature
	 * 'generic types'. It can be declared in the old way like this:
	 * private java.util.List coins = new ArrayList();
	 * This will however result in a warning at compilation
	 * "Position" in this case is the type of the objects that are going
	 * to be used in the List
	 */

    /**
     * A Matrix containing the state of the gameboard.
     */
    private final GameTile[][] gameboardState;

    /**
     * The position of the collector.
     */
    private Position collectorPos;

    /**
     * The direction of the collector.
     */
    private Directions direction = Directions.NORTH;

    /**
     * The number of coins found.
     */
    private int score;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Create a new model for the gold game.
     */
    public GoldModel() {

        Dimension size = Constants.getGameSize();
        this.gameboardState =
                new GameTile[size.width][size.height];

        // Blank out the whole gameboard
        for (int i = 0; i < size.width; i++) {
            for (int j = 0; j < size.height; j++) {
                gameUtils.setGameboardState(i, j, BLANK_TILE, gameboardState);
            }
        }

        // Insert the collector in the middle of the gameboard.
        this.collectorPos = new Position(size.width / 2, size.height / 2);
        gameUtils.setGameboardState(this.collectorPos, COLLECTOR_TILE, gameboardState);

        // Insert coins into the gameboard.
        for (int i = 0; i < COIN_START_AMOUNT; i++) {
            addCoin();
        }
    }

    public void addObserver(PropertyChangeListener listener){
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removeObserver(PropertyChangeListener listener){
        this.pcs.removePropertyChangeListener(listener);
    }

    /**
     * Insert another coin into the gameboard.
     */
    private void addCoin() {
        Position newCoinPos;
        Dimension size = Constants.getGameSize();
        // Loop until a blank position is found and ...
        do {
            newCoinPos = new Position((int) (Math.random() * size.width),
                    (int) (Math.random() * size.height));
        } while (!isPositionEmpty(newCoinPos));

        // ... add a new coin to the empty tile.
        gameUtils.setGameboardState(newCoinPos, COIN_TILE, gameboardState);
        this.coins.add(newCoinPos);
    }

    /**
     * Return whether the specified position is empty.
     *
     * @param pos The position to test.
     * @return true if position is empty.
     */
    private boolean isPositionEmpty(final Position pos) {
        return (getGameboardState(pos) == BLANK_TILE);
    }

    /**
     * Update the direction of the collector
     * according to the user's keypress.
     */
    private void updateDirection(final int key) {
        switch (key) {
            case KeyEvent.VK_LEFT:
                this.direction = Directions.WEST;
                break;
            case KeyEvent.VK_UP:
                this.direction = Directions.NORTH;
                break;
            case KeyEvent.VK_RIGHT:
                this.direction = Directions.EAST;
                break;
            case KeyEvent.VK_DOWN:
                this.direction = Directions.SOUTH;
                break;
            default:
                // Don't change direction if another key is pressed
                break;
        }
    }

    /**
     * Get next position of the collector.
     */
    private Position getNextCollectorPos() {
        return new Position(
                this.collectorPos.getX() + this.direction.getXDelta(),
                this.collectorPos.getY() + this.direction.getYDelta());
    }

    /**
     * This method is called repeatedly so that the
     * game can update its state.
     *
     * @param lastKey The most recent keystroke.
     */
    @Override
    public void gameUpdate(final int lastKey) throws GameOverException {
        updateDirection(lastKey);

        // Erase the previous position.
        gameUtils.setGameboardState(this.collectorPos, BLANK_TILE, gameboardState);
        // Change collector position.
        this.collectorPos = getNextCollectorPos();

        if (isOutOfBounds(this.collectorPos)) {
            throw new GameOverException(this.score);
        }
        // Draw collector at new position.
        gameUtils.setGameboardState(this.collectorPos, COLLECTOR_TILE, gameboardState);

        // Remove the coin at the new collector position (if any)
        if (this.coins.remove(this.collectorPos)) {
            this.score++;
        }

        // Check if all coins are found
        if (this.coins.isEmpty()) {
            throw new GameOverException(this.score + 5);
        }

        // Remove one of the coins
        Position oldCoinPos = this.coins.get(0);
        this.coins.remove(0);
        gameUtils.setGameboardState(oldCoinPos, BLANK_TILE, gameboardState);

        // Add a new coin (simulating moving one coin)
        addCoin();

        this.pcs.firePropertyChange("gameUpdate", 0, 1);

    }

    /**
     * @param pos The position to test.
     * @return <code>false</code> if the position is outside the playing field, <code>true</code> otherwise.
     */
    private boolean isOutOfBounds(Position pos) {
        return pos.getX() < 0 || pos.getX() >= Constants.getGameSize().width
                || pos.getY() < 0 || pos.getY() >= Constants.getGameSize().height;
    }

    @Override
    public GameTile getGameboardState(Position pos) {
        return getGameboardState(pos.getX(), pos.getY());
    }

    @Override
    public GameTile getGameboardState(int x, int y) {
        return gameboardState[x][y];
    }

}
