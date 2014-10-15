package reference;

public class Pair {
	public Object head;
	public Object tail;
	
	public Pair(Object thisHead, Object thisTail) {
		this.head = thisHead;
		this.tail = thisTail;
	}
	
	public void setHead(Object h){
		this.head = h;
	}
	
	public Object getHead(){
		return this.head;
	}
	
	public void setTail(Object t){
		this.tail = t;
	}
	
	public Object getTail(){
		return this.tail;
	}
}
