import java.util.*;

public class AdDfsMaze {
    static final char WALL = '#', EMPTY = ' ';
    final int H, W;              // Zellenbreite/-höhe
    final char[][] grid;         // (2H+1)x(2W+1)
    final boolean[][] visited;   // HxW
    final Deque<Dir> lastDirs = new ArrayDeque<>();
    final int k;                 // Fenstergröße Historie
    final double beta;           // Anti-Persistenz
    final double pBraid;         // 0..0.2 typ.
    final Random rng;

    enum Dir {U,D,L,R}
    static final char START = 'S', END = 'E';

    // mark cell centers as S/E
    public void markStartEnd(int sx, int sy, int ex, int ey) {
        grid[2*sy+1][2*sx+1] = START;
        grid[2*ey+1][2*ex+1] = END;
    }

    public AdDfsMaze(int W, int H, long seed, int k, double beta, double pBraid) {
        this.W=W; this.H=H; this.k=k; this.beta=beta; this.pBraid=pBraid;
        this.rng = new Random(seed);
        this.grid = new char[2*H+1][2*W+1];
        this.visited = new boolean[H][W];
        for (int y=0;y<grid.length;y++) Arrays.fill(grid[y], WALL);
    }

    public void generate(int startX, int startY) {
        carveCell(startX, startY);
        Deque<int[]> st = new ArrayDeque<>();
        st.push(new int[]{startX,startY});

        while (!st.isEmpty()) {
            int[] c = st.peek();
            int cx=c[0], cy=c[1];

            List<Dir> cand = candidates(cx, cy);
            if (cand.isEmpty()) { st.pop(); continue; }

            Dir dir = sampleByWeights(cand);
            int nx = cx + dx(dir), ny = cy + dy(dir);
            carveBetween(cx, cy, nx, ny);
            carveCell(nx, ny);
            st.push(new int[]{nx,ny});
            pushHistory(dir);

            // optional braiding
            if (rng.nextDouble() < pBraid) braidOnce(cx, cy);
        }
    }

    private void carveCell(int x, int y) {
        grid[2*y+1][2*x+1] = EMPTY;
        visited[y][x] = true;
    }

    private void carveBetween(int x, int y, int nx, int ny) {
        // Zwischenwand
        grid[ (2*y+1 + 2*ny+1)/2 ][ (2*x+1 + 2*nx+1)/2 ] = EMPTY;
        // Zielzelle wird in carveCell geleert
    }

    private List<Dir> candidates(int x, int y) {
        List<Dir> out = new ArrayList<>(4);
        for (Dir d : Dir.values()) {
            int nx = x + dx(d), ny = y + dy(d);
            if (inBounds(nx,ny) && !visited[ny][nx]) out.add(d);
        }
        return out;
    }

    private Dir sampleByWeights(List<Dir> cand) {
        int[] cnt = countsInHistory();
        double sum=0;
        double[] w = new double[cand.size()];
        for (int i=0;i<cand.size();i++) {
            int c = cnt[idx(cand.get(i))];
            w[i] = Math.exp(-beta * c); // Softmax
            sum += w[i];
        }
        double r = rng.nextDouble()*sum, acc=0;
        for (int i=0;i<w.length;i++) { acc+=w[i]; if (r<=acc) return cand.get(i); }
        return cand.get(cand.size()-1);
    }

    private void braidOnce(int x, int y) {
        // Öffne 1 Wand zu bereits besuchter Nachbarzelle, wenn dabei kein 2x2-Raum entsteht
        List<Dir> visN = new ArrayList<>();
        for (Dir d: Dir.values()) {
            int nx=x+dx(d), ny=y+dy(d);
            if (inBounds(nx,ny) && visited[ny][nx]) visN.add(d);
        }
        if (visN.isEmpty()) return;
        Collections.shuffle(visN, rng);
        for (Dir d: visN) {
            int nx=x+dx(d), ny=y+dy(d);
            if (!creates2x2Open(x,y,nx,ny)) {
                carveBetween(x,y,nx,ny);
                return;
            }
        }
    }

    private boolean creates2x2Open(int x0,int y0,int x1,int y1){
        // Simple Heuristik: prüfe 2x2 in Rasterkoords um die zwischenwand herum
        // (Implementierbar je nach Bedürfnis – kann auch weggelassen werden)
        return false;
    }

    // --- Utils ---
    private static int dx(Dir d){ return d==Dir.L?-1:d==Dir.R?1:0; }
    private static int dy(Dir d){ return d==Dir.U?-1:d==Dir.D?1:0; }
    private static int idx(Dir d){ return d.ordinal(); }
    private boolean inBounds(int x,int y){ return 0<=x && x<W && 0<=y && y<H; }

    private void pushHistory(Dir d){
        lastDirs.addLast(d);
        if (lastDirs.size() > k) lastDirs.removeFirst();
    }
    private int[] countsInHistory(){
        int[] c = new int[4];
        for (Dir d: lastDirs) c[idx(d)]++;
        return c;
    }

    public void print() {
        for (char[] row : grid) {
            for (char c : row) {
                if (c == WALL)      System.out.print("██");  // wall
                else if (c == START)System.out.print("S ");  // start
                else if (c == END)  System.out.print("E ");  // end
                else                System.out.print("  ");  // empty
            }
            System.out.println();
        }
    }

    // Öffnungen an Rand erzeugen (Start oben rechts, Ziel unten links):
    // oben (über Zelle x=0,y=0) und unten (unter Zelle x=W-1,y=H-1) öffnen
    public void openEntrancesTopBottom() {
        grid[0][1] = EMPTY;                 // obere Außenwand öffnen (über (0,0))
        grid[2*H][2*W-1] = EMPTY;           // untere Außenwand öffnen (unter (W-1,H-1))
    }
}
