package fxtm;

import java.util.Vector;

class A{
	Edge e;
	int b;
}
public class Test {
	private static int f(int... a){
		if(a.length==0){
			return 0;
		}else{
			return 1;
		}
	}
	public static void gd(int[] xv, int[] yv) {
		double k = 10000;
		int x = xv[(int)Math.random()*(xv.length-1)];
		int y = yv[(int)Math.random()*(yv.length-1)];
		double loss = 1*x*(y-k*x*x)-1*k;
		double alpha = 0.1;
		while(Math.pow(y-k*x*x, 2)>=0.0001) {
			x = xv[(int)Math.random()*(xv.length-1)];
			y = yv[(int)Math.random()*(yv.length-1)];
			k = k+loss*alpha;
			loss = 2*x*(y-k*x*x);
			System.out.println("k="+k);
		}
		System.out.println(k);
	}
	public static void main(String[] args) {
		int[] x = {1,2,3,4,5,0};
		int[] y = {1,4,9,16,25,0};
		
		gd(x, y);
	}
}
