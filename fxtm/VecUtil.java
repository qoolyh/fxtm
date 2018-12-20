package fxtm;

import java.util.List;
import java.util.Vector;



public class VecUtil {
	public static Vector<Double> subtract(Vector<Double> v1, Vector<Double> v2){
		Vector<Double> vector = new Vector<Double>();
		for(int i=0;i<v1.size();i++){
			vector.add(v1.get(i)-v2.get(i));
		}
		return vector;
	}
	public static Vector<Double> exp(Vector<Double> v){
		Vector<Double> res = new Vector<>();
		for(double d : v) {
			res.add(Math.exp(d));
		}
		return res;
	}
	public static Vector<Double> add(Vector<Double> v1, Vector<Double> v2){
		Vector<Double> res = new Vector<Double>();
		for(int i=0;i<v1.size();i++){
			res.add(v1.get(i)+v2.get(i));
		}
		return res;
	}
	public static Vector<Double> multiply(Vector<Double> v1, Vector<Double> v2){
		Vector<Double> res = new Vector<Double>();
		
			for(int i=0;i<v2.size();i++){
				if(v1.size()==1){
				res.add(v2.get(i)*v1.get(0));
				}
				else{
					res.add(v2.get(i)*v1.get(i));
				}
			}
		return res;
	}
	public static double norm(Vector<Double> v, int p) {
		double res = 0;
		for(double i : v) {
			res+=Math.pow(i, p);
		}
		return res;
	}
	public static Vector<Double> sigmoid(Vector<Double> w1, Vector<Double> v1){
		Vector<Double> res = new Vector<Double>();
		for(int i=0;i<v1.size();i++){
			
			if ( w1.get(i)*v1.get(i) > 99 ) {
				res.add(1.0);
			}
			else if ( w1.get(i)*v1.get(i) < -99 ) {
				res.add(0.0);
			} 
			else {
				res.add( 1.0/(1 + Math.exp(-1*w1.get(i)*v1.get(i))) );
			}
		}
		return res;
	}
	public static Vector<Double> multiply(double v1, Vector<Double> v2){
		Vector<Double> res = new Vector<Double>();
		
			for(int i=0;i<v2.size();i++){
				res.add(v2.get(i)*v1);
			}
		return res;
	}
	public static double dotProduct(List<Double> v1, Vector<Double> v2){
		double res = 0;
		try {
			if(v1.size()!=v2.size()){
				throw new Exception("dot product failed cause the two vectors don't have same size");
			}else{
				for(int i=0;i<v1.size();i++){
					res+=v1.get(i)*v2.get(i);
				}
			}
		} catch (Exception e) {
			
			System.err.println(e);
			System.err.println("v1.size="+v1.size()+" v2.size="+v2.size());
			return 0;
		}	
	return res;
	}
	
	public static Vector<Double> avg(Vector<Double> v1, Vector<Double> v2){
		Vector<Double> vector = new Vector<Double>();
		for(int i=0;i<v1.size();i++){
			vector.add((double)(v1.get(i)+v2.get(i))/2);
		}
		return vector;
	}
	public static void eval(Vector<Double> vec, double value) {
		for(int i=0;i<vec.size();i++) {
			vec.set(i, value);
		}
	}
	public static Vector<Double> getVec(int len){
		Vector<Double> vector = new Vector<>();
		for(int i=0;i<len;i++){
			vector.add(0.0);
		}
		return vector;
	}
	public static void main(String[] args) {
		Vector<Double> v1 = new Vector<>();
		Vector<Double> v2 = new Vector<>();
		v1.add(1.0);
		v2.add(1.0);
		v1 = add(v1, v2);
		System.out.println(v1.get(0));
	}
	public static double distance(Vector<Double> v1, Vector<Double> v2) {
		double dis = 0.0;
		for(int i=0;i<v1.size();i++) {
			dis+=Math.abs(v1.get(i)-v2.get(i));
		}
		return dis;
	}
	public static Vector<Double> compare_vec(Vector<String> v1, Vector<String> v2){
		Vector<Double> res = new Vector<>();
		try {
			if(v1.size()!=v2.size()){
				res = null;
				throw new Exception("two vectors don't have same size");				
			}else{
				for(int i=0;i<v1.size();i++){
				
					
					double v = 0;
						if(Util.isNumeric(v1.get(i))) {
							v= (Compare.dist(Double.parseDouble(v1.get(i)), Double.parseDouble(v2.get(i))));
						}else {
							v= v1.get(i).equals(v2.get(i))?0.0:1.0;
						}
					
					if(res.size()<=i){
						res.add(v);
					}else{
						double tmp = res.get(i).doubleValue();
						res.set(i, tmp+v);
					}
				}
				
			}
		} catch (Exception e) {
			System.err.println(e);
			// TODO: handle exception
		}
		return res;
		
	}
	public static Elem[] toArray(Vector<Elem> vec){
		Elem [] a = new Elem[vec.size()];
		for(int i=0; i<vec.size();i++){
			a[i] = vec.get(i);
		}
		return a;
	}
	public static void addToVec(Vector<Integer> newer, Vector<Integer> target){
		for(int i=0;i<newer.size();i++){
			if(!target.contains(newer.get(i))){
				target.add(newer.get(i));
			}
		}
	}
	
}
