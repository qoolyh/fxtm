package fxtm;

public class Bg implements Cloneable{
	public Edge edge;
	public boolean decided;
	public boolean connected;
	public int srcCNum;
	public int srcSibNum;
	public int refCNum;
	public int refSibNum;
	public Bg clone(){
		Bg clone = null; 
	        try{ 
	            clone = (Bg) super.clone(); 
	            clone.edge = edge.clone();
	        }catch(CloneNotSupportedException e){ 
	            throw new RuntimeException(e); // won't happen 
	        }
	         
	    return clone; 
	}
}
