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

    /** the length of one side of the board */
    private int DIM;
    /** the grid of looking values */
    private int[][] lookingValues;
    /** the grid representing the board */
    private int[][] board;
    /** the current row */
    private int row;
    /** the current column */
    private int col;
    /** a list of sets with a set for each row in the board */
    private ArrayList<HashSet<Integer>> rows;
    /** a list of sets with a set for each column in the board */
    private ArrayList<HashSet<Integer>> columns;
    /** whether or not the board has been pre-processed */
    private Boolean preFilled = false;
    /** whether or not a number was placed */
    private boolean placed;
    /** whether or not the board is solvable based on the looking values */
    private boolean possible;
    /** the current size of the board */
    private int size;
    /** the max size of the board */
    private int fullSize;

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
        this.size = 0;
        this.possible = true;

        Scanner f = new Scanner(new File(filename));

        this.DIM = f.nextInt();

        this.fullSize = this.DIM*this.DIM;
        this.placed = true;
        this.lookingValues = new int[4][this.DIM];
        this.board = new int[this.DIM][this.DIM];
        this.rows = new ArrayList<>();
        this.columns = new ArrayList<>();

        for (int i = 0; i < this.DIM; i++) {
            this.rows.add(i, new HashSet<>());
            this.columns.add(i, new HashSet<>());
        }

        for (int i = 0; i < 4; i++) {
            boolean seen1 = false;
            boolean seenDIM = false;
            for (int j = 0; j < this.DIM; j++) {
                int tempNum = f.nextInt();
                if (tempNum == 1) {
                    if (!seen1) {
                        seen1 = true;
                    } else {
                        this.possible = false;
                        this.preFilled = true;
                    }
                } else if (tempNum == this.DIM) {
                    if (!seenDIM) {
                        seenDIM = true;
                    } else {
                        this.possible = false;
                        this.preFilled = true;
                    }
                }
                this.lookingValues[i][j] = tempNum;
            }
        }

        for (int i = 0; i < this.DIM; i++) {
            for (int j = 0; j < this.DIM; j++) {
                int token = f.nextInt();
                this.board[i][j] = token;
                if (token != 0) {
                    this.rows.get(i).add(token);
                    this.columns.get(j).add(token);
                    this.size++;
                }
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
    public SkyscraperConfig(SkyscraperConfig copy, int row, int col, int num) {
        this.size = copy.size;
        this.fullSize = copy.fullSize;
        this.possible = copy.possible;

        this.DIM = copy.DIM;
        this.row = row;
        this.col = col;
        this.lookingValues = new int[4][this.DIM];
        this.board = new int[copy.DIM][copy.DIM];
        this.rows = new ArrayList<>();
        this.columns = new ArrayList<>();
        this.placed = copy.placed;
        this.preFilled = copy.preFilled;

        for (int i = 0; i < this.DIM; i++) {
            this.rows.add(i,new HashSet<>(copy.rows.get(i)));
            this.columns.add(i,new HashSet<>(copy.columns.get(i)));
        }

        for (int r=0; r<this.DIM; r++) {
            System.arraycopy(copy.board[r], 0, this.board[r], 0, this.DIM);
        }
        for (int i = 0; i < 4; i++) {
            System.arraycopy(copy.lookingValues[i], 0, this.lookingValues[i], 0, this.DIM);
        }

        if (this.board[this.row][this.col] == EMPTY) {
            this.board[this.row][this.col] = num;
        }
    }

    @Override
    public boolean isGoal() {
        return this.size == this.fullSize;
    }

    /**
     * Pre-process the board and fill any squares that only have one possible
     * value based on the looking values.
     */
    public void preFill() {
        HashSet<ArrayList<Integer>> placed = new HashSet<>();
        this.preFilled = true;
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
                    /*
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
                    this.board[r][c] = this.DIM;
                    this.rows.get(r).add(this.DIM);
                    this.columns.get(c).add(this.DIM);
                    placed.add(new ArrayList<>(Arrays.asList(r,c)));
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
                    placed.add(new ArrayList<>(Arrays.asList(j-1,i)));
                }
            }
        }

        //bottom row
        for (int i = 0; i < this.DIM; i++) {
            if (this.lookingValues[2][i] == this.DIM) {
                int num = 1;
                for (int j = this.DIM; j >= 1; j--) {
                    this.board[j-1][i] = num;
                    this.rows.get(j-1).add(num);
                    this.columns.get(i).add(num);
                    num++;
                    placed.add(new ArrayList<>(Arrays.asList(j-1,i)));
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
                    placed.add(new ArrayList<>(Arrays.asList(i,j-1)));
                }
            }
        }

        //right row
        for (int i = 0; i < this.DIM; i++) {
            if (this.lookingValues[1][i] == this.DIM) {
                int num = 1;
                for (int j = this.DIM; j >= 1; j--) {
                    this.board[i][j-1] = num;
                    this.rows.get(i).add(num);
                    this.columns.get(j-1).add(num);
                    num++;
                    placed.add(new ArrayList<>(Arrays.asList(i,j-1)));
                }
            }
        }
        this.size += placed.size();
    }

    /**
     * getSuccessors
     *
     * @returns Collection of Configurations
     */
    @Override
    public Collection<Configuration> getSuccessors() {
        ArrayList<Configuration> successors = new ArrayList<>();
        if (!this.preFilled) {
            if (possible) {
                this.preFill();
            }
        }

        while (this.board[this.row][this.col] != EMPTY) {
            if (this.col < this.DIM - 1) {
                this.col++;
            } else if (this.col == this.DIM - 1 && this.row < this.DIM - 1) {
                this.col = 0;
                this.row += 1;
            } else {
                this.col = this.DIM - 1;
                this.row = this.DIM - 1;
                this.placed = false;
                break;
            }
        }

        for (int i = 1; i <= this.DIM; i++) {
            if (!this.rows.get(this.row).contains(i) && !this.columns.get(this.col).contains(i)) {
                SkyscraperConfig child = new SkyscraperConfig(this, this.row, this.col, i);
                successors.add(child);
            } else if (this.isGoal()) {
                successors.add(this);
            }
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
        if (!this.possible) {
            return false;
        }
        if (!this.placed) {
            return true;
        }
        int current = this.board[this.row][this.col];
        if (this.rows.get(this.row).contains(current) || this.columns.get(this.col).contains(current)) {
            return false;
        } else {
            int currentLookingValue;

            //fast left check (is left value too big?)
            currentLookingValue = this.lookingValues[3][this.row];
            if (this.DIM - currentLookingValue < this.board[this.row][0]-1) {
                return false;
            }

            //fast top check (is top value too big?)
            currentLookingValue = this.lookingValues[0][this.col];
            if (this.DIM - currentLookingValue < this.board[0][this.col]-1) {
                return false;
            }

            //slow checks
            int num;
            int seen;

            //check left looking values
            if (this.rows.get(this.row).size() == this.DIM-1) {
                num = 0;
                seen = 0;
                currentLookingValue = this.lookingValues[3][this.row];
                for (int i = 0; i < this.DIM; i++) {
                    if (this.board[this.row][i] > num) {
                        seen++;
                        num = this.board[this.row][i];
                    }
                }
                if (seen != currentLookingValue) {
                    return false;
                }
            }

            //check top looking values
            if (this.columns.get(this.col).size() == this.DIM-1) {
                num = 0;
                seen = 0;
                currentLookingValue = this.lookingValues[0][this.col];
                for (int i = 0; i < this.DIM; i++) {
                    if (this.board[i][this.col] > num) {
                        seen++;
                        num = this.board[i][this.col];
                    }
                }
                if (seen != currentLookingValue) {
                    return false;
                }
            }

            //check bottom looking values
            if (this.columns.get(this.col).size() == this.DIM-1) {
                num = 0;
                seen = 0;
                currentLookingValue = this.lookingValues[2][this.col];
                for (int i = this.DIM - 1; i >= 0; i--) {
                    if (this.board[i][this.col] > num) {
                        seen++;
                        num = this.board[i][this.col];
                    }
                }
                if (seen != currentLookingValue) {
                    return false;
                }
            }

            //check right looking values
            if (this.rows.get(this.row).size() == this.DIM-1) {
                num = 0;
                seen = 0;
                currentLookingValue = this.lookingValues[1][this.row];
                for (int i = this.DIM - 1; i >= 0; i--) {
                    if (this.board[this.row][i] > num) {
                        seen++;
                        num = this.board[this.row][i];
                    }
                }
                if (seen != currentLookingValue) {
                    return false;
                }
            }
        }

        this.rows.get(this.row).add(current);
        this.columns.get(this.col).add(current);
        this.size++;

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
