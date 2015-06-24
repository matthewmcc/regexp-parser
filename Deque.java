import java.util.List;
import java.util.ArrayList;

public class Deque {
    
    private List<State> que;
    
    public Deque() {
        que = new ArrayList<State>();
    }
    
    // Returns the first element in the que without changing que
    
    public State peek() {
        if(que.size() > 0) return que.get(0);
        else return null;
    }
    
    // Remove element from the front of the que and returns it
    
    public State pop() {
        if(que.size() > 0) return que.remove(0);
        else return null;
    }
    
    // Adds element to front of the que
    
    public void pushFront(State c) {
        que.add(0, c);
    }
    
    // Appends an element to the end of the que
    
    public void pushEnd(State c) {
        que.add(c);
    }
    
    // Rotates the que to the left r times
    
    public void rotateLeft(int r) {
        if(que.size() > 1) {
            for(int i = 0; i < r; i++) {
                State temp = que.remove(0);
                que.add(temp);
            }
        }
    }
    
    // Rotates the que to the left r times
    
    public void rotateRight(int r) {
        if(que.size() > 1) {
            for(int i = 0; i < r; i++) {
                State temp = que.remove(que.size() - 1);
                que.add(0, temp);
            }
        }
    }
    
    public int size() {
        return que.size();
    }
}