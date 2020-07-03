import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Represents a single configuration in the skyscraper puzzle.
 *
 * @author RIT CS
 * @author YOUR NAME HERE
 */
public class SkyscraperConfig implements Configuration {
    /** empty cell value */
    public final static int EMPTY = 0;

    /** empty cell value display */
    public final static char EMPTY_CELL = '.';

    private int DIM;
    private int[][] lookingValues;
    private int[][] board;
    private int row;
    private int col;

    /**
     * Constructor
     *
     * @param filename the filename
     *  <p>
     *  Read the board file.  It is organized as follows:
     *  DIM     # square DIMension of board (1-9)
     *  lookNS   # DIM values (1-DIM) left to right
     *  lookEW   # DIM values (1-DIM) top to bottom
     *  lookSN   # DIM values (1-DIM) left to right
     *  lookWE   # DIM values (1-DIM) top to bottom
     *  row 1 values    # 0 for empty, (1-DIM) otherwise
     *  row 2 values    # 0 for empty, (1-DIM) otherwise
     *  ...
     *
     *  @throws FileNotFoundException if file not found
     */
    SkyscraperConfig(String filename) throws FileNotFoundException {
        Scanner f = new Scanner(new File(filename));

        this.DIM = f.nextInt();

        this.lookingValues = new int[this.DIM][this.DIM];
        this.board = new int[this.DIM][this.DIM];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < this.DIM; j++) {
                this.lookingValues[i][j] = f.nextInt();
            }
        }

        for (int i = 0; i < this.DIM; i++) {
            for (int j = 0; j < this.DIM; j++) {
                this.board[i][j] = f.nextInt();
            }
        }

        this.row = 0;
        this.col = 0;
        // close the input file
        f.close();
    }

    /**
     * Copy constructor
     *
     * @param copy SkyscraperConfig instance
     */
    public SkyscraperConfig(SkyscraperConfig copy) {
        this.DIM = copy.DIM;
        this.row = copy.row;
        this.col = copy.col;
        this.board = new int[copy.DIM][copy.DIM];

        for (int r=0; r<this.DIM; r++) {
            System.arraycopy(copy.board[r], 0, this.board[r], 0, this.DIM);
        }
    }

    @Override
    public boolean isGoal() {

        // TODO

        return false; // remove after implementing
    }

    /**
     * getSuccessors
     *
     * @returns Collection of Configurations
     */
    @Override
    public Collection<Configuration> getSuccessors() {

        // TODO

        return new ArrayList<>();   // remove after implementing

    }

    /**
     * isValid() - checks if current config is valid
     *
     * @returns true if config is valid, false otherwise
     */
    @Override
    public boolean isValid() {

        // TODO

        return false;  // remove after implementing
    }

    /**
     * toString() method
     *
     * @return String representing configuration board & grid w/ look values.
     * The format of the output for the problem solving initial config is:
     *
     *   1 2 4 2
     *   --------
     * 1|. . . .|3
     * 2|. . . .|3
     * 3|. . . .|1
     * 3|. . . .|2
     *   --------
     *   4 2 1 2
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("\n   ");
        for (int val : this.lookingValues[0]) {
            output.append(val).append(" ");
        }
        output.append("\n   ").append("-".repeat(Math.max(0, 2 * this.DIM-1))).append("\n");

        int lookValue = 0;
        for (int i = 0; i < this.DIM; i++) {
            output.append(this.lookingValues[3][lookValue]).append("/ ");
            for (int j = 0; j < this.DIM; j++) {
                output.append(this.board[i][j]).append(" ");
            }
            output.append("/").append(this.lookingValues[1][lookValue]).append("\n");
            lookValue++;
        }
        output.append("   ").append("-".repeat(Math.max(0, 2 * this.DIM-1))).append("\n   ");
        for (int val : this.lookingValues[2]) {
            output.append(val).append(" ");
        }
        output.append("\n");

        return new String(output);
    }
}
