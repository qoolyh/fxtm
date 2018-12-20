package fxtm;

import java.util.Vector;

import bsh.This;

public class Edge implements Cloneable{
	private int src;
	private int ref;
	private double minCost;
	private double maxCost;
	private int cNum_src;
	private int sNum_src;
	private int cNum_ref;
	private int sNum_ref;
	public Edge(int src,int ref,double min, double max) {
		this.src = src;
		this.ref = ref;
		this.minCost = min;
		this.maxCost = max;
	}
	
	public Edge(int src,int ref,double min, double max, Elem sElem, Elem rElem) {
		this.src = src;
		this.ref = ref;
		this.minCost = min;
		this.maxCost = max;
		this.cNum_src = sElem.getChildren().size();
		this.cNum_ref = rElem.getChildren().size();
		this.sNum_src = sElem.getSiblings().size();
		this.sNum_ref = rElem.getSiblings().size();
	}
	public Edge() {
	}
	
	public Edge clone(){
		Edge clone = null; 
        try{ 
            clone = (Edge) super.clone(); 
        }catch(CloneNotSupportedException e){ 
            throw new RuntimeException(e); // won't happen 
        }   
    return clone; 
	}
	public int src_upper_sibD;
	public int ref_upper_sibD;
	public int src_upper_sibI;
	public int ref_upper_sibI;
	public int src_upper_acs;
	public int ref_upper_acs;
	public int src_lower_sibD;
	public int ref_lower_sibD;
	public int src_lower_sibI;
	public int ref_lower_sibI;
	public int src_lower_acs;
	public int ref_lower_acs;
	
	public void setBounds(int s_up_sibD, int s_up_sibI, int s_up_acs, int r_up_sibD, int r_up_sibI, int r_up_acs,
			int s_lower_sibD, int s_lower_sibI, int s_lower_acs, int r_lower_sibD, int r_lower_sibI, int r_lower_acs) {
		this.src_upper_sibD = s_up_sibD;
		this.src_upper_sibI = s_up_sibI;
		this.src_upper_acs = s_up_acs;
		this.src_lower_sibD = s_lower_sibD;
		this.src_lower_sibI = s_lower_sibI;
		this.src_lower_acs = s_lower_acs;
		
		this.ref_upper_sibD = r_up_sibD;
		this.ref_upper_sibI = r_up_sibI;
		this.ref_upper_acs = r_up_acs;
		this.ref_lower_sibD = r_lower_sibD;
		this.ref_lower_sibI = r_lower_sibI;
		this.ref_lower_acs = r_lower_acs;
		
	}
	public void setCost(double vis, Vector<Double> weights) {
		double  weight_acs = weights.get(weights.size()-3),
				weight_sib = weights.get(weights.size()-2),
				weight_ban = weights.get(weights.size()-1);
		
//		double upper_acs = (double)weight_acs*(this.src_upper_acs/cNum_src+this.ref_upper_acs/cNum_ref),
//			   lower_acs = (double)weight_acs*(this.src_lower_acs/cNum_src+this.ref_lower_acs/cNum_ref);
//		double upper_sib = weight_sib/2 * ((double)this.src_upper_sibD/(this.src_lower_sibI*sNum_src)
//				+(double)this.ref_upper_sibD/(this.ref_lower_sibI*sNum_ref)),
//			   lower_sib = weight_sib*((double)this.src_lower_sibD/((this.src_upper_sibI*(this.src_lower_sibD+1)*sNum_src))
//					   +(double)this.ref_lower_sibD/((this.ref_upper_sibI*(this.ref_lower_sibD+1)*sNum_ref)));
		
		double upper_acs = (double)weight_acs*(this.src_upper_acs+this.ref_upper_acs),
				   lower_acs = (double)weight_acs*(this.src_lower_acs+this.ref_lower_acs);
			double upper_sib = weight_sib/2 * ((double)this.src_upper_sibD/(this.src_lower_sibI)
					+(double)this.ref_upper_sibD/(this.ref_lower_sibI)),
				   lower_sib = weight_sib*((double)this.src_lower_sibD/((this.src_upper_sibI*(this.src_lower_sibD+1)))
						   +(double)this.ref_lower_sibD/((this.ref_upper_sibI*(this.ref_lower_sibD+1))));
			
		double upper = upper_acs+upper_sib+vis;
		double lower = lower_acs+lower_sib+vis;
//		if(Double.isInfinite(lower)){
//			System.out.println("inf found... l_a="+lower_acs+" l_s="+lower_sib);
//			System.out.println("src_u_i="+this.src_upper_sibI+" src_l_d="+(this.src_lower_sibD+1));
//			System.out.println("ref_u_i="+this.ref_upper_sibI+" ref_l_d="+(this.ref_lower_sibD+1));
//		}else if(Double.isNaN(lower)){
//			System.out.println("NaN found... l_a="+lower_acs+" l_s="+lower_sib);
//			System.out.println("src_u_i="+this.src_upper_sibI+" src_l_d="+(this.src_lower_sibD+1));
//			System.out.println("ref_u_i="+this.ref_upper_sibI+" ref_l_d="+(this.ref_lower_sibD+1));
//			
//		}
		this.minCost = lower;
		this.maxCost = upper;
	}
	public int getSrc() {
		return src;
	}
	public int getcNum_src() {
		return cNum_src;
	}
	public void setcNum_src(int cNum_src) {
		this.cNum_src = cNum_src;
	}
	public int getsNum_src() {
		return sNum_src;
	}
	public void setsNum_src(int sNum_src) {
		this.sNum_src = sNum_src;
	}
	public int getcNum_ref() {
		return cNum_ref;
	}
	public void setcNum_ref(int cNum_ref) {
		this.cNum_ref = cNum_ref;
	}
	public int getsNum_ref() {
		return sNum_ref;
	}
	public void setsNum_ref(int sNum_ref) {
		this.sNum_ref = sNum_ref;
	}
	public void setSrc(int src) {
		this.src = src;
	}
	public int getRef() {
		return ref;
	}
	public double getMinCost() {
		return minCost;
	}
	public void setMinCost(double minCost) {
		this.minCost = minCost;
	}
	public double getMaxCost() {
		return maxCost;
	}
	public void setMaxCost(double maxCost) {
		this.maxCost = maxCost;
	}
	public void setRef(int ref) {
		this.ref = ref;
	}
	
	
}
