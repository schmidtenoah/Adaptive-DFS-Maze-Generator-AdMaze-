import java.util.*;

public class Main {
    public static void main(String[] args){
        int w = 11;
        int h = 11;
        var m = new AdDfsMaze(w,h,21L,50,0.8,0.08);
        long t1 = System.nanoTime();
        m.generate(w-1,0);  // Start: oben rechts (x=W-1,y=0)
        m.openEntrancesTopBottom();
        m.markStartEnd(0, 0, w - 1, h - 1);  // S=top-right, E=bottom-left
        long t2 = System.nanoTime();
        m.print();
        System.out.println("AdDfsMaze generation time: " + (t2-t1)/1e9);
    }
}