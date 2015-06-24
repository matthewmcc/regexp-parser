import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Searcher {
    
    Deque deq = new Deque();
    final State SCAN = new State(null, -1, -1);
    char[] c;
    Character[] ch;
    int[] next1;
    int[] next2;
    State[] s;
    
    public Searcher(File FSM, File patterns) throws FileNotFoundException {
        
        Scanner sc = new Scanner(FSM);
        String[] split0 = sc.nextLine().split(",");
        String[] split1 = sc.nextLine().split(",");
        String[] split2 = sc.nextLine().split(",");
        sc.close();
        
        sc = new Scanner(patterns);
        
        String example = "AABCD";
        c = example.toCharArray();
        
        ch = new Character[split0.length];
        next1 = new int[split1.length];
        next2 = new int[split2.length];
        s = new State[split0.length];
        
        for (int i = 0; i < next1.length; i++) {
            ch[i] = split0[i].charAt(0);
            next1[i] = Integer.parseInt(split1[i]);
            next2[i] = Integer.parseInt(split2[i]);
            s[i] = new State(ch[i], next1[i], next2[i]);
        }
        
        System.out.println("Test Pattern = " + example);
        System.out.println(SCAN.c);
        if (automaton()) System.out.println("Match");
        else System.out.println("Not Match");
    }
    
    private boolean automaton() {
        // set up the deque
        deq.pushFront(SCAN);
        deq.pushFront(s[0]);
        deq.pushEnd(s[s[0].next1]);
        deq.pushEnd(s[s[0].next2]);
        
        int inputIndex = 0;
        
        while(inputIndex < c.length) {
            
            State cur = deq.pop();
            System.out.println("State Char: " + cur.c + " Next1: " + cur.next1 + " Next2: " + cur.next2);
            
            if(cur.next1 == -1) {
                return false;
            }
            
            if (cur.c == null && cur.next1 == 0 && cur.next2 == 0) {
                return true;
            }
            
            if (cur.c == null) {
                deq.pushFront(s[cur.next1]);
                deq.pushFront(s[cur.next2]);
            }
            
            if (cur.c != null) {
                if (c[inputIndex] == cur.c){
                    inputIndex++;
                    deq.pushEnd(s[cur.next1]);
                    deq.pushEnd(s[cur.next2]);
                }
            }
        }
        return false;
    }
}