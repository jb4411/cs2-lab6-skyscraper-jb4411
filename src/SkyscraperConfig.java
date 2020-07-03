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
    private ArrayList<HashSet<Integer>> rows;
    private ArrayList<HashSet<Integer>> columns;
    private Boolean preFilled = false;

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

        this.lookingValues = new int[4][this.DIM];
        this.board = new int[this.DIM][this.DIM];
        this.rows = new ArrayList<>();
        this.columns = new ArrayList<>();
        for (int i = 0; i < this.DIM; i++) {
            this.rows.add(i, new HashSet<>());
            this.columns.add(i, new HashSet<>());
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < this.DIM; j++) {
                this.lookingValues[i][j] = f.nextInt();
            }
        }

        for (int i = 0; i < this.DIM; i++) {
            for (int j = 0; j < this.DIM; j++) {
                int num = f.nextInt();
                this.board[i][j] = num;
                this.rows.get(i).add(num);
                this.columns.get(j).add(num);
            }
        }

        this.row = 0;
        this.col = -1;
        // close the input file
        f.close();
    }

    /**
     * Copy constructor
     *
     * @param copy SkyscraperConfig instance
     */
    public SkyscraperConfig(SkyscraperConfig copy, int row, int col, int num) {
        this.DIM = copy.DIM;
        this.row = row;
        this.col = col;
        this.lookingValues = new int[4][this.DIM];
        this.board = new int[copy.DIM][copy.DIM];
        this.rows = new ArrayList<>();
        this.columns = new ArrayList<>();

        for (int i = 0; i < this.DIM; i++) {
            this.rows.add(i,new HashSet<>(copy.rows.get(i)));
            this.columns.add(i,new HashSet<>(copy.columns.get(i)));
        }

        for (int r=0; r<this.DIM; r++) {
            System.arraycopy(copy.board[r], 0, this.board[r], 0, this.DIM);
            System.arraycopy(copy.lookingValues[r], 0, this.lookingValues[r], 0, 4);
        }
        this.board[this.row][this.col] = num;
    }

    @Override
    public boolean isGoal() {
        return this.col == this.DIM - 1 && this.row == this.DIM - 1;
    }

    public void preFill() {
        preFilled = true;
        //prefill
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < this.DIM; j++) {
                if (this.lookingValues[i][j] == 1) {
                    int r;
                    int c;
                    switch (i) {
                        case 0 -> {
                            r = 0;
                            c = j;
                        }
                        case 1 -> {
                            r = j;
                            c = this.DIM - 1;
                        }
                        case 2 -> {
                            r = this.DIM - 1;
                            c = j;
                        } default -> {
                            r = j;
                            c = 0;
                        }
                    }
                    /**
                     if (i == 0) {
                        r = 0;
                        c = j;
                    } else if (i == 1) {
                        r = j;
                        c = this.DIM-1;
                    } else if (i == 2) {
                        r = this.DIM-1;
                        c = j;
                    } else {
                        r = j;
                        c = 0;
                    }*/
                    this.board[r][c] = 4;
                    this.rows.get(r).add(4);
                    this.columns.get(c).add(4);
                }
            }
        }

        //top row
        for (int i = 0; i < this.DIM; i++) {
            if (this.lookingValues[0][i] == this.DIM) {
                for (int j = 1; j <= this.DIM; j++) {
                    this.board[j-1][i] = j;
                    this.rows.get(j-1).add(j);
                    this.columns.get(i).add(j);
                }
            }
        }

        //bottom row
        for (int i = 0; i < this.DIM; i++) {
            if (this.lookingValues[2][i] == this.DIM) {
                int num = 1;
                for (int j = 4; j >= 1; j--) {
                    this.board[j-1][i] = num;
                    this.rows.get(j-1).add(num);
                    this.columns.get(i).add(num);
                    num++;
                }
            }
        }

        //left row
        for (int i = 0; i < this.DIM; i++) {
            if (this.lookingValues[3][i] == this.DIM) {
                for (int j = 1; j <= this.DIM; j++) {
                    this.board[i][j-1] = j;
                    this.rows.get(i).add(j);
                    this.columns.get(j-1).add(j);
                }
            }
        }

        //right row
        for (int i = 0; i < this.DIM; i++) {
            if (this.lookingValues[1][i] == this.DIM) {
                int num = 1;
                for (int j = 4; j >= 1; j--) {
                    this.board[i][j-1] = num;
                    this.rows.get(i).add(num);
                    this.columns.get(j-1).add(num);
                    num++;
                }
            }
        }
    }

    public void quickFill() {
        for (int i = 0; i < this.DIM; i++) {
            if (this.rows.get(i).size() == this.DIM - 1) {
                for (int j = 0; j < this.DIM; j++) {
                    if (this.board[i][j] == EMPTY) {
                        for (int k = 1; k <= this.DIM; k++) {
                            if (!this.rows.get(i).contains(k)) {
                                this.board[i][j] = k;
                                this.rows.get(i).add(k);
                                break;
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < this.DIM; i++) {
            if (this.columns.get(i).size() == this.DIM - 1) {
                for (int j = 0; j < this.DIM; j++) {
                    if (this.board[j][i] == EMPTY) {
                        for (int k = 1; k <= this.DIM; k++) {
                            if (!this.columns.get(i).contains(k)) {
                                this.board[j][i] = k;
                                this.columns.get(i).add(k);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * getSuccessors
     *
     * @returns Collection of Configurations
     */
    @Override
    public Collection<Configuration> getSuccessors() {
        ArrayList<Configuration> successors = new ArrayList<>();
        if (!preFilled) {
            this.preFill();
        }
        //this.quickFill();

        if (this.col == this.DIM - 1) {
            this.col = 0;
            this.row += 1;
        } else {
            this.col++;
        }

        while (this.board[this.row][this.col] != EMPTY) {
            if (this.col == this.DIM - 1) {
                this.col = 0;
                this.row += 1;
            } else {
                this.col++;
            }
        }
        for (int i = 1; i <= this.DIM; i++) {
            SkyscraperConfig child = new SkyscraperConfig(this, this.row, this.col, i);
            successors.add(child);
        }

        return successors;
    }

    /**
     * isValid() - checks if current config is valid
     *
     * @returns true if config is valid, false otherwise
     */
    @Override
    public boolean isValid() {
        int current = this.board[this.row][this.col];
        if (this.rows.get(this.row).contains(current) || this.columns.get(this.col).contains(current)) {
            return false;
        } else {

        }


        this.rows.get(this.row).add(current);
        this.columns.get(this.col).add(current);

        return true;
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
                if (this.board[i][j] == EMPTY) {
                    output.append(EMPTY_CELL).append(" ");
                } else {
                    output.append(this.board[i][j]).append(" ");
                }
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
