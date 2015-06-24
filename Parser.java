import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    
    
    private char[] specials; // list of non literals
    private int j; // index of the string we are currently parsing
    private char[] p; // String[] containing the given regexp
    private String reg;
    private List<Character> ch;	// character at given state
    private List<Integer> next1;
    private List<Integer> next2;
    private int state = 0;
    
    
    public Parser(String regexp) {
        // create variables and call parse
        reg = regexp;
        specials = new char[] {'.','\\','*','?','|','[',']','(',')'}; // set of all special operators inside our grammar
        j = 0;
        ch = new ArrayList<Character>();
        next1 = new ArrayList<Integer>();
        next2 = new ArrayList<Integer>();
        p = regexp.toCharArray();
    }
    
    public void parse() throws IOException {
        int initial = expression();
        if (!outOfBounds()) error("Did not consume entire input"); // check if index is matching a correct regex
        else valid(initial);
    }
    
    private void setState(int s, Character c, int n1, int n2){
        ch.add(s, c);
        next1.add(s, n1);
        next2.add(s, n2);
    }
    
    private void overwriteState(int s, Character c, int n1, int n2){
        
        if (c != null) ch.set(s, c);
        next1.set(s, n1);
        next2.set(s, n2);
    }
    
    private int expression() {
        setState(state++, null, state, state);
        // state++ will increase the next state to state + 1 within function
        // 1, null, 2, 2
        int r = term();
        
        if (!outOfBounds() && p[j] == ('|')) {
            
            j++;
            setState(state, null, 0, 0);
            int pipeState =	state++;
            int t = expression();
            
            overwriteState(pipeState, null, t, r);
            // Last recursion of pipe will point the 0th index with the first pipes index
            overwriteState(0, null, pipeState, pipeState);
            
            // If its a range before the end of the state sets the range dummy state to point to the end of the pipe enclosure
            int index = pipeState - 4;
            if (index >= 0 && ch.get(index) == null && next1.get(index) == (index + 1) && next2.get(index) == index + 2) {
                overwriteState(pipeState - 1, ch.get(pipeState - 1), state, state);
            }
            
            if (ch.get(pipeState - 1) != null) overwriteState(pipeState - 1, ch.get(pipeState - 1), state, state);
            // if last state before pipe is a * or ? state update its next pointer to end while leaving its top branch to recur
            else overwriteState(pipeState - 1, ch.get(pipeState - 1), next1.get(pipeState - 1), state);
            
            r = pipeState;
            
        }
        
        if (!outOfBounds() && (isLiteral(p[j]) || p[j] != ')')) expression();
        return r;
    }
    
    private int term() {
        
        int r = state;
        
        // If we don't check for escape symbols first the regex will return a false result
        if (p[j] == '\\') {
            j++;
            if (!outOfBounds()) {
                setState(state++, p[j], state, state);
                j++;
                if (outOfBounds()) return r;
            }
            else error("escape character last character");
        }
        
        // Pipe infix
        if (!outOfBounds() && p[j] == '|') {
            return r;
        }
        
        // Check valid term finals
        // Increment if valid so expression can read the next character
        else {
            r = factor();
            
            // If we see * or ?, we need to update the dummy state to point to the branching state to avoid writing
            // the character if we don't need to.
            if (!outOfBounds() && ((p[j] == '*') || (p[j] == '?'))) {
                if (p[j - 1] != ')' && p[j - 1] != ']') {
                    
                    for (int i = 0; i < ch.size(); i++) {
                        if (next1.get(i) == state - 1) next1.set(i, state);
                        if (next2.get(i) == state - 1) next2.set(i, state);
                    }
                    
                    //overwriteState(r - 1, 't', state, state);
                    if (p[j] == '*') overwriteState(r, p[j - 1], state, state);
                    else overwriteState(r, p[j - 1], state + 1, state + 1);
                    setState(state++, null, r, state);
                }
                else {
                    overwriteState(r - 1, null, state, state);
                    setState(state++, null, r, state);
                    if (p[j] == '?') overwriteState(state - 2, null, state, state);
                }
                r = state - 1;
                j++;
            }
        }
        
        if (!outOfBounds() && (isLiteral(p[j]) || p[j] != ')')) term();
        
        return r;
    }
    
    private int factor() {
        
        int r = state;
        
        if (!outOfBounds() && isLiteral(p[j]) || p[j] == '.') {
            setState(state++, p[j], state, state);
            j++; // check literal or wild card to eliminate base cases
        }
        else if (p[j] == '(') {
            j++;
            if (p[j] == ')') error("Empty Parentheses");
            r = expression();
            if (!outOfBounds() && p[j] == ')') j++;
            else error("Unclosed parentheses inside factor expression");
        }
        else if (p[j] == '['){ // check range with valid structure
            
            StringBuilder sb = new StringBuilder(); // contents of the range exclude the '[' and ']'
            
            j++; // consume the '['
            
            if (p[j] == ']') {
                sb.append(p[j]);
                j++;
            }
            
            if (outOfBounds()) error("Unfinished range");
            
            while (p[j] != ']'){
                sb.append(p[j]);
                j++;
                if (outOfBounds()) error("Unfinished range");
            }
            
            j++; // consume the ']'
            
            int endState = (sb.length() * 2) - 1 + state;
            
            if (sb.length() == 1) {
                setState(state++, sb.charAt(0), state, state);
            }
            else if (sb.length() == 2) {
                setFinalBranch(sb.charAt(0), sb.charAt(1), endState);
            }
            else {
                
                for (int i = 0; i < sb.length() - 2; i++) {
                    setState(state++, null, state, state + 1);
                    setState(state++, sb.charAt(i), endState, endState);
                }
                
                setFinalBranch(sb.charAt(sb.length() - 2), sb.charAt(sb.length() - 1), endState);
            }
        }
        return r;
    }
    
    /**
     * Sets the final branching state for a range
     * @param c1 the second to last character
     * @param c2 the last character
     * @param end the new state after the range
     */
    private void setFinalBranch(Character c1, Character c2, int end) {
        setState(state++, null, state, state + 1);
        setState(state++, c1, end, end);
        setState(state++, c2, end, end);
        setState(state++, null, state, state);
    }
    
    /**
     * Call if regex discovered invalid
     */
    private void error(String ex) {
        System.out.println("ERROR: " + ex + ": " + reg);
        System.exit(-1);
    }
    
    /**
     * call if regex is discovered valid
     * @throws IOException
     */
    private void valid(int Start) throws IOException {
        setState(state, ' ', 0, 0);
        System.out.println('\n' + "SUCCESS: VALID REGEX " + reg);
        printFSM();
        outputFSM();
    }
    
    /**
     * Check if the char is a literal or a special symbol
     * @param c the given char
     * @return true or false depending on special status
     */
    private boolean isLiteral(char c){
        for(char h : specials) {
            if (c == h) return false;
        }
        return true;
    }
    
    /**
     * Checks if regex is out of bounds
     * @return
     */
    private boolean outOfBounds() {
        if (j >= p.length) return true;
        return false;
    }
    
    public void printFSM() {
        
        StringBuilder sb0 = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();
        
        for (int i = 0; i < ch.size(); i++) {
            sb0.append(i + " ");
            if( ch.get(i) == null )
                sb1.append("  ");
            else sb1.append(ch.get(i) + " ");
            sb2.append(next1.get(i) + " ");
            sb3.append(next2.get(i) + " ");
        }
        
        System.out.println(sb0.toString());
        System.out.println(sb1.toString());
        System.out.println(sb2.toString());
        System.out.println(sb3.toString());
    }
    
    public void outputFSM() throws IOException{
        String abs = System.getProperty("user.dir");
        File output = new File(abs + File.separator + "output.fsm");
        FileWriter fr = new FileWriter(output);
        BufferedWriter wr = new BufferedWriter(fr);
        
        
        for(int i = 0; i < ch.size(); i++) {
            if (ch.get(i) != null) wr.write(ch.get(i));
            else wr.write(' ');
            if (i < ch.size() - 1) wr.write(',');
        }		
        wr.newLine();
        
        for(int i = 0; i < next1.size(); i++) {
            wr.write(next1.get(i).toString());
            if (i < next1.size() - 1) wr.write(',');
        }		
        wr.newLine();
        
        for(int i = 0; i < next2.size(); i++) {
            wr.write(next2.get(i).toString());
            if (i < next2.size() - 1) wr.write(',');
        }
        wr.close();
    }
}