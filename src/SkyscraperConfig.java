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
    private Boolean run = true;

    private HashMap<ArrayList<Integer>,HashSet<Integer>> hashBoard;

    private ArrayList<Integer> possibleValues;

    private int oldRow;
    private int oldCol;

    private ArrayList<ArrayList<Integer>> placeable;
    private boolean placed;

    private boolean possible;

    private int size;
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
        this.placeable = new ArrayList<>();
        this.oldRow = 0;
        this.oldCol = 0;

        Scanner f = new Scanner(new File(filename));

        this.DIM = f.nextInt();

        this.fullSize = this.DIM*this.DIM;

        this.possibleValues = new ArrayList<>();
        for (int i = 1; i <= this.DIM; i++) {
            this.possibleValues.add(i);
        }

        //this.hashBoard = new HashMap<>();
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

        /*for (int i = 0; i < this.DIM; i++) {
            for (int j = 0; j < this.DIM; j++) {
                int num = f.nextInt();
                this.board[i][j] = num;
                this.rows.get(i).add(num);
                this.columns.get(j).add(num);
                if (num == 0) {
                    this.hashBoard.put(new ArrayList<>(Arrays.asList(i,j)), new HashSet<>(possibleValues));
                } else {
                    this.hashBoard.put(new ArrayList<>(Arrays.asList(i,j)), new HashSet<>(num));
                }
            }
        }*/

        //this.updateHashBoard();

        this.row = 0;
        this.col = 0;

        // close the input file
        f.close();
    }

    public void updateHashBoard() {
        for (ArrayList<Integer> key : this.hashBoard.keySet()) {
            if (this.hashBoard.get(key).size() == 1) {
                int cRow = key.get(0);
                int cCol = key.get(1);
                int val = 0;
                for (int element : this.hashBoard.get(key)) {
                    val = element;
                }

                for (int i = 1; i <= this.DIM; i++) {
                    ArrayList<Integer> cKey = new ArrayList<>(Arrays.asList(i, this.col));
                    ArrayList<Integer> rKey = new ArrayList<>(Arrays.asList(this.row, i));
                    if (this.hashBoard.get(cKey).size() > 1) {
                        this.hashBoard.get(cKey).remove(val);
                        if (this.hashBoard.get(cKey).size() == 1) {
                            this.placeable.add(new ArrayList<>(cKey));
                        }
                    } else if (this.hashBoard.get(rKey).size() > 1) {
                        this.hashBoard.get(rKey).remove(val);
                        if (this.hashBoard.get(rKey).size() == 1) {
                            this.placeable.add(new ArrayList<>(rKey));
                        }
                    }
                }

                /*for (ArrayList<Integer> cKey : this.hashBoard.keySet()) {
                    if (this.hashBoard.get(cKey).size() > 1) {
                        if (cKey.get(0) == cRow || cKey.get(1) == cCol) {
                            this.hashBoard.get(cKey).remove(val);
                            if (this.hashBoard.get(cKey).size() == 1) {
                                this.placeable.add(new ArrayList<>(cKey));
                            }
                        }
                    }
                }*/
            }
        }
    }

    /**
    public void updateHashBoard() {
        for (ArrayList<Integer> key : this.hashBoard.keySet()) {
            if (this.hashBoard.get(key).size() == 1) {
                int cRow = key.get(0);
                int cCol = key.get(1);
                int val = 0;
                for (int element : this.hashBoard.get(key)) {
                    val = element;
                }

                for (ArrayList<Integer> cKey : this.hashBoard.keySet()) {
                    if (this.hashBoard.get(cKey).size() > 1) {
                        if (cKey.get(0) == cRow || cKey.get(1) == cCol) {
                            this.hashBoard.get(cKey).remove(val);
                        }
                    }
                }
            }
        }
    }
     */

    /**
     * Copy constructor
     *
     * @param copy SkyscraperConfig instance
     */
    public SkyscraperConfig(SkyscraperConfig copy, int row, int col, int num) {
        this.size = copy.size;
        this.fullSize = copy.fullSize;
        this.possible = copy.possible;
        this.oldCol = copy.oldCol;
        this.oldRow = copy.oldRow;

        this.DIM = copy.DIM;
        this.row = row;
        this.col = col;
        this.lookingValues = new int[4][this.DIM];
        this.board = new int[copy.DIM][copy.DIM];
        this.rows = new ArrayList<>();
        this.columns = new ArrayList<>();
        this.placed = copy.placed;
        this.preFilled = copy.preFilled;
        //this.hashBoard = new HashMap<>();
        //this.hashBoard.putAll(copy.hashBoard);
        //this.placeable = new ArrayList<>();

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
        //this.placeable.addAll(copy.placeable);

        if (this.board[this.row][this.col] == EMPTY) {
            this.board[this.row][this.col] = num;
        }
        //this.hashBoard.put(new ArrayList<>(Arrays.asList(this.row,this.col)), new HashSet<>(num));
        //this.updateHashBoard();
    }

    @Override
    public boolean isGoal() {
        /*boolean result = this.col == this.DIM - 1 && this.row == this.DIM - 1;
        if (!result) {
            return false;
        }
        for (int i = 0; i < this.DIM; i++) {
            if (this.rows.get(i).size() != this.DIM ||this.columns.get(i).size() != this.DIM) {
                return false;
            }
        }
        return true;*/
        return this.size == this.fullSize;
    }

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

    public HashMap<Integer, Integer> quickFill() {
        HashMap<Integer,Integer> points = new HashMap<>();
        for (int i = 0; i < this.DIM; i++) {
            if (this.rows.get(i).size() == this.DIM - 1) {
                for (int j = 0; j < this.DIM; j++) {
                    if (this.board[i][j] == EMPTY) {
                        for (int k = 1; k <= this.DIM; k++) {
                            if (!this.rows.get(i).contains(k)) {
                                /*int currentRow = this.row;
                                int currentCol = this.col;
                                this.row = i;
                                this.col = j;
                                if (this.isValid()) {
                                    points.put(i,j);
                                    this.board[i][j] = k;
                                    this.rows.get(i).add(k);
                                    break;
                                } else {
                                    this.row = currentRow;
                                    this.col = currentCol;
                                    break;
                                }*/
                                points.put(i,j);
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
                                points.put(i, j);
                                this.board[j][i] = k;
                                this.columns.get(i).add(k);
                                break;
                                /*int currentRow = this.row;
                                int currentCol = this.col;
                                this.row = i;
                                this.col = j;
                                if (this.isValid()) {
                                    points.put(i, j);
                                    this.board[j][i] = k;
                                    this.columns.get(i).add(k);
                                    break;
                                } else {
                                    this.row = currentRow;
                                    this.col = currentCol;
                                    break;
                                }*/
                            }
                        }
                    }
                }
            }
        }
        return points;
    }

    public void quickFill2() {
        for (int i = 0; i < this.DIM; i++) {
            if (this.rows.get(i).size() == this.DIM) {
                for (int j = 0; j < this.DIM; j++) {
                    if (this.board[i][j] == EMPTY) {
                        for (int k = 1; k <= this.DIM; k++) {
                            if (!this.rows.get(i).contains(k)) {
                                this.board[i][j] = k;
                                this.oldRow = this.row;
                                this.oldCol = this.col;
                                this.row = i;
                                this.col = j;
                                if (!this.isValid()) {
                                    this.board[i][j] = 0;
                                }
                                this.row = this.oldRow;
                                this.col = this.oldCol;
                                break;
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < this.DIM; i++) {
            if (this.columns.get(i).size() == this.DIM) {
                for (int j = 0; j < this.DIM; j++) {
                    if (this.board[j][i] == EMPTY) {
                        for (int k = 1; k <= this.DIM; k++) {
                            if (!this.columns.get(i).contains(k)) {
                                this.board[j][i] = k;
                                this.oldRow = this.row;
                                this.oldCol = this.col;
                                this.row = i;
                                this.col = j;
                                if (!this.isValid()) {
                                    this.board[j][i] = 0;
                                }
                                this.row = this.oldRow;
                                this.col = this.oldCol;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void quickFill3() {
        if (this.rows.get(this.row).size() == this.DIM) {
            for (int j = 0; j < this.DIM; j++) {
                if (this.board[this.row][j] == EMPTY) {
                    for (int k = 1; k <= this.DIM; k++) {
                        if (!this.rows.get(this.row).contains(k)) {
                            this.board[this.row][j] = k;
                            this.oldRow = this.row;
                            this.oldCol = this.col;
                            this.col = j;
                            if (!this.isValid()) {
                                this.board[this.row][j] = 0;
                            }
                            this.row = this.oldRow;
                            this.col = this.oldCol;
                            break;
                        }
                    }
                }
            }
        }

        if (this.columns.get(this.col).size() == this.DIM) {
            for (int j = 0; j < this.DIM; j++) {
                if (this.board[j][this.col] == EMPTY) {
                    for (int k = 1; k <= this.DIM; k++) {
                        if (!this.columns.get(this.col).contains(k)) {
                            this.board[j][this.col] = k;
                            this.oldRow = this.row;
                            this.oldCol = this.col;
                            this.row = j;
                            if (!this.isValid()) {
                                this.board[j][this.col] = 0;
                            }
                            this.row = this.oldRow;
                            this.col = this.oldCol;
                            break;
                        }
                    }
                }
            }
        }
    }

    public void quickFill4() {
        System.out.println("qucik filled");
        for (int i = 0; i < this.DIM; i++) {
            if (this.rows.get(i).size() == this.DIM) {
                for (int j = 0; j < this.DIM; j++) {
                    if (this.board[i][j] == EMPTY) {
                        for (int k = 1; k <= this.DIM; k++) {
                            if (!this.rows.get(i).contains(k)) {
                                this.board[i][j] = k;
                                this.rows.get(i).add(k);
                                this.columns.get(j).add(k);
                                break;
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < this.DIM; i++) {
            if (this.columns.get(i).size() == this.DIM) {
                for (int j = 0; j < this.DIM; j++) {
                    if (this.board[j][i] == EMPTY) {
                        for (int k = 1; k <= this.DIM; k++) {
                            if (!this.columns.get(i).contains(k)) {
                                this.board[j][i] = k;
                                this.rows.get(i).add(k);
                                this.columns.get(j).add(k);
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
        if (!this.preFilled) {
            if (possible) {
                this.preFill();
            }
            //this.quickFill4();
        }
        //this.quickFill();
        //this.quickFill3();


        /*boolean valid = true;
        if (run) {
            valid = this.quickFill2();
        }

        if (!valid) {
            run = false;
        } else {
            run = true;
        }*/
        /*int currentRow = -1;
        int currentCol = -1;
        if (run) {
            currentRow = this.row;
            currentCol = this.col;

            HashMap<Integer, Integer> points = this.quickFill();
            for (int key : points.keySet()) {
                this.row = key;
                this.col = points.get(key);
                if (!this.isValid()) {
                    run = false;
                    break;
                }
            }
        } else {
            run = true;
            this.row = currentRow;
            this.col = currentCol;
        }*/

        /*if (this.col == this.DIM - 1) {
            this.col = 0;
            this.row += 1;
        } else {
            this.col++;
        }*/

        /*if (this.placeable.size() > 0) {
            ArrayList<Integer> cords = this.placeable.remove(0);
            this.oldRow = this.row;
            this.oldCol = this.col;
            this.row = cords.get(0);
            this.col = cords.get(1);
        } else {*/
            //this.row = this.oldRow;
            //this.col = this.oldCol;
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
        //}


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

            /*
            //fast checks
            int currentLookingValue = this.lookingValues[0][this.col];
            HashSet<Integer> temp = new HashSet<>();
            int biggest = 0;


            //left
            for (int nextNum : this.rows.get(this.row)) {
                if (nextNum > biggest) {
                    biggest = nextNum;
                    temp.add(nextNum);
                }
            }
            if (current > biggest) {temp.add(current);}

            if (temp.size() > currentLookingValue) {
                if (biggest == this.DIM) {
                    return false;
                }

                if ((this.DIM - biggest) < temp.size() - currentLookingValue) {
                    return false;
                }
            }

            //top
            for (int nextNum : this.columns.get(this.col)) {
                if (nextNum > current) {
                    if (nextNum > biggest) {
                        biggest = nextNum;
                    }
                    temp.add(nextNum);
                }
            }
            temp.add(current);

            if (temp.size() > currentLookingValue) {
                if (biggest == this.DIM) {
                    return false;
                }

                if ((this.DIM - biggest) < temp.size() - currentLookingValue) {
                    return false;
                }
            }
            //left
            currentLookingValue = this.lookingValues[3][this.row];
            temp = new HashSet<>();
            biggest = 0;

            for (int nextNum : this.leftSeen.get(this.row)) {
                if (nextNum > current) {
                    if (nextNum > biggest) {
                        biggest = nextNum;
                    }
                    temp.add(nextNum);
                }
            }
            temp.add(current);

            if (temp.size() > currentLookingValue) {
                if ((this.DIM - biggest) < temp.size() - currentLookingValue) {
                    return false;
                }
            }*/

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
        //this.hashBoard.remove(current);

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
