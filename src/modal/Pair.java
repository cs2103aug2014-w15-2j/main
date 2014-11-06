package modal;

public class Pair<Object1, Object2> {
	//@author A0119379R
	public Object1 head;
	public Object2 tail;
	
	public Pair(Object1 thisHead, Object2 thisTail) {
		this.head = thisHead;
		this.tail = thisTail;
	}
	
	public Pair() {
		
	}
	
	public void setHead(Object1 h){
		this.head = h;
	}
	
	public Object1 getHead(){
		return this.head;
	}
	
	public void setTail(Object2 t){
		this.tail = t;
	}
	
	public Object2 getTail(){
		return this.tail;
	}
}
