import java.util.*;

class SudokuSolver {
    public static Map<Integer, Set<Integer>> depend = new HashMap<>();
    static
    {
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 10; j++) {
                int a = ((i - 1) / 3) * 3;
                int b = ((j - 1) / 3) * 3;
                Set<Integer> lt = new HashSet<>();
                for (int x = 1; x < 10; x++) {
                    lt.add(x*16+j);
                    lt.add(i*16+x);
                }
                for (int x = a + 1; x < a + 4; x++) {
                    for (int y = b + 1; y < b + 4; y++) {
                        lt.add(x*16+y);
                    }
                }
                lt.remove(i*16+j);
                depend.put(i*16+j, lt);
            }
        }
    }
    private int[][] arr;
    private Map<Integer, Set<Integer>> zero;

    public SudokuSolver(int[][] game, boolean notbuilt, Map<Integer, Set<Integer>> z) {
        this.arr = game;
        this.zero = z;

        if (notbuilt) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    update(i, j);
                }
            }
            deleteOnes();
            if (!this.zero.isEmpty()) {
                solve();
            }
        }
    }

    private void update(int i, int j) {
        if (this.arr[i][j] == 0) {

            Set<Integer> lt = new HashSet<>(Arrays.asList(1,2,3,4,5,6,7,8,9));
            for (int x = 0; x < 9; x++) {
                int e = this.arr[i][x];
                if(e>0)lt.remove(e);
                e = this.arr[x][j];
                if(e>0)lt.remove(e);
            }
            int a = (i / 3) * 3;
            int b = (j / 3) * 3;
            for (int x = a; x < a + 3; x++) {
                for (int y = b; y < b + 3; y++) {
                    int e = this.arr[x][y];
                    if(e>0)lt.remove(e);
                }
            }
            this.zero.put(((i+1)<<4)+j+1, lt);
        }
    }

    private boolean deleteOnes() {
        boolean atleat = false;
        Set<Integer> szks = new HashSet<>(this.zero.keySet());
        for (Integer tempKey : szks) {
            Set<Integer> temp = this.zero.get(tempKey);
            if (temp.size() == 1) {
                int i = tempKey >> 4;
                int j = tempKey & 15;
                int k = temp.iterator().next();
                this.arr[i - 1][j - 1] = k;
                this.zero.remove(tempKey);
                atleat = true;
                for (Integer key : new HashSet<>(this.zero.keySet())) {
                    if (depend.get(tempKey).contains(key)) {
                        this.zero.get(key).remove(k);
                        if (this.zero.get(key).isEmpty()) {

//                            System.out.println(this.zero);
                            return true;
                        }
                    }
                }
            }
        }
        if (atleat) {
            return deleteOnes();
        }
        return false;
    }

    private boolean solve() {
        Integer tst = this.zero.keySet().iterator().next();
        Set<Integer> psb = new HashSet<>(this.zero.get(tst));
        for (int ni : psb) {
            Map<Integer, Set<Integer>> z = new HashMap<>();
            for (Integer x : this.zero.keySet()) {
                z.put(x, new HashSet<>(this.zero.get(x)));
            }
            int i = tst >> 4;
            int j = tst & 15;
            int[][] ac = new int[9][9];
            for (int x = 0; x < 9; x++) {
                ac[x] = Arrays.copyOf(this.arr[x], 9);
            }
//            System.out.println(""+i+" "+j+"tst"+tst);
            ac[i - 1][j - 1] = ni;
            z.remove(tst);
            boolean flag = false;
            for (Integer key : new HashSet<>(z.keySet())) {
                if (depend.get(tst).contains(key)) {
                    z.get(key).remove(ni);
                    if (z.get(key).isEmpty()) {
                        flag = true;
//                        System.out.println("hiii");
                        break;
                    }
                }
            }
            if (flag) {
                continue;
            }
            SudokuSolver s = new SudokuSolver(ac, false, z);
            if (s.deleteOnes()) {
                continue;
            }
            if (s.zero.isEmpty() || s.solve()) {
                this.arr = s.arr;
                return true;
            }
        }
        return false;
    }


    public int[][] answer() {
        return this.arr;
    }
}

class Main {
    public static void solveSudoku(char[][] board) {
        int[][] game = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != '.') {
                    game[i][j] = Character.getNumericValue(board[i][j]);
                }
            }
        }
//        System.out.println(SudokuSolver.depend.toString());
        SudokuSolver solver = new SudokuSolver(game, true, new HashMap<>());
        int[][] ans = solver.answer();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(ans[i][j]);
                board[i][j] = (char) (ans[i][j] + '0');
            }
            System.out.println();
        }
    }
    public static void main(String[] args)
    {
        char[][] b =(new char[][]{{'.','.','.','.','7','.','.','.','.'},{'.','.','.','.','.','.','.','.','.'},{'.','.','8','.','.','.','.','6','.'},{'8','.','.','.','6','.','.','.','3'},{'.','.','.','8','.','3','.','.','1'},{'7','.','.','.','2','.','.','.','6'},{'.','6','.','.','.','.','2','8','.'},{'.','.','.','4','1','9','.','.','5'},{'.','.','.','.','8','.','.','7','9'}});
        solveSudoku(b);
//        System.out.println(b);
    }
}