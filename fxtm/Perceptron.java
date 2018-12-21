package fxtm;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import org.openqa.selenium.firefox.FirefoxDriver;

import training.FlxmUtil;

import com.google.common.cache.CacheStats;



public class Perceptron {
	/*
	 * returns the parameters of weights (i.e. values of features)
	 * e.g f(x) = w_1*x_1 +w_2*x_2, paras = <x_1,x_2>
	 * @param src
	 * @param ref
	 * @param bg
	 * @param length
	 * @return
	 */
	public static Vector<Double> get_paras_vectors(Elem[] src, Elem[] ref, int[][] bg, int length){
		Vector<Double> paras = VecUtil.getVec(length);
		for(int i=0;i<bg.length;i++) {
			
			for(int j=0;j<bg[0].length;j++) {
				Vector<Double> tmp = new Vector<>();
				if(bg[i][j]==1) {
					if(i!=src.length && j!=ref.length) {
					double acs = FlexibleTreeMatching.get_violation_paras(src[i], ref[j], src, ref, bg, true);
					double sib = FlexibleTreeMatching.get_violation_paras(src[i], ref[j], src, ref, bg, false);

					Vector<Double> vis = Compare.elemComp(src[i], ref[j]);
					
					tmp.addAll(vis);
					tmp.add(acs);
					tmp.add(sib);
					tmp.add(0.0);	
					}
					else {
						tmp = VecUtil.getVec(length-1);
						if(i==src.length && j!=ref.length) {
							tmp.add(1.0);
						}else if(i!=src.length && j==ref.length) {
							tmp.add(1.0);
						}else {
							tmp.add(0.0);
						}
					}
					
				}else {
					tmp = VecUtil.getVec(length);
				}
				
				paras = VecUtil.add(tmp, paras);
				
			}
			
		}
		System.out.println( "paras: " + paras);
		return paras;
	}
	
	private static int[][] generate_individuals(Elem[] src, Elem[] ref, Vector<Double> theta) {
		int[][] bg = FXM_v2.fxm(src, ref, theta);
		return bg;
	}

	public static int[][] get_manual_match(Elem[] src, Elem[] ref){
		int[][] bg = new int[src.length+1][];
		for(int i=0;i<src.length;i++) {
			bg[i] = new int[ref.length+1];
			for(int j=0;j<ref.length;j++) {
				bg[i][j] = src[i].getMid()==ref[j].getId()?1:0;
			}
			if(src[i].getMid()==-1) {
				bg[i][ref.length]=1;
			}
		}
		return bg;
	}

	/**
	 * using perceptron to get the weights of parameters
	 * @param src elements in source page
	 * @param ref elements in reference page
	 * @param bg the manually mapping which is represented as adjacency matrix
	 * @return
	 */
	public static Vector<Double> getTheta(Vector<Double> theta1, Elem[] src, Elem[] ref, int[][] bg, int max_iter,HashMap<String, Vector<Edge>> cache){

		int para_length = Compare.getProperties(src[0]).size()+3; // c_v + c_a + c_s + c_ban
		// We firstly initialize the value of parameters, which is denoted by theta
		Vector<Double> theta = theta1;
		

		/*for(int i=0;i<para_length;i++){
			theta.add(0.0);
		}*/
		System.out.println("th...");
		//Set the maximum number of loops
		int max_iter_num = max_iter, counter = 0,flag=2;
//		Vector<Double> mini = VecUtil.getVec(para_length);
//		Vector<Double> prev = VecUtil.getVec(para_length);
		
		while(counter!=max_iter_num) {
			System.out.println(counter+"th...");
			int[][] method_bg = generate_individuals(src,ref,theta);
			int[][] manual_bg = bg;
			
			
			Vector<Double> para_method = FlxmUtil.calViolations(src, ref, method_bg);
			Vector<Double> para_manual = FlxmUtil.calViolations(src, ref, manual_bg);
//			Vector<Double> para_manual = get_paras_vectors(src, ref, manual_bg, para_length);
			
			Vector<Double> distance = VecUtil.subtract(para_method, para_manual);

		String dist = "";
		for(int i=0;i<distance.size();i++) {
			dist+=distance.get(i)+" ";
		}
		String dist1 = "";
		for(int i=0;i<para_method.size();i++) {
			dist1+=para_method.get(i)+" ";
		}
		String dist2 = "";
		for(int i=0;i<para_manual.size();i++) {
			dist2+=para_manual.get(i)+" ";
		}
			double alpha = 1/Math.sqrt(counter+1);
			Vector<Double> alpha_vec = new Vector<>();
			alpha_vec.add(alpha);
			System.out.println(counter+"th...");
			System.out.println("dis: "+dist+"\n diff="+bgDiff(method_bg, manual_bg));
			System.out.println(dist1);
			System.out.println(dist2);
			Vector<Double> newTheta = VecUtil.add(theta, VecUtil.multiply(alpha_vec, distance)); // w_i+1 = w_i + alpha*(F_method - F_manual)
//			mini = VecUtil.distance(prev, para_manual)>VecUtil.distance(para_method, para_manual)?newTheta:mini;
//			prev = VecUtil.distance(prev, para_manual)>VecUtil.distance(para_method, para_manual)?para_method:prev;
//			System.out.println(counter+"th..."+"newtheta="+newTheta.get(0)+" "+newTheta.get(1)+" "+newTheta.get(2)+" "+newTheta.get(3));
//			System.out.println(counter+"th..."+"mini="+mini.get(0)+" "+mini.get(1)+" "+mini.get(2)+" "+mini.get(3));
			if (oscillate(theta, newTheta)) {
				--flag;
				if(flag==0) {
					break;
				}
				
			}else {
				theta = newTheta;
				++counter;
			}
			
			System.out.print("{");
			for (int i=0; i<theta.size(); i++)
				System.out.print(theta.get(i)+", ");
			
			System.out.print("}\n");
		}
		
		return theta;
	}
	
	private static int bgDiff(int[][] bg1, int[][] bg2) {
		int diff = 0;
		for(int i=0;i<bg1.length;i++) {
			for(int j=0;j<bg1[0].length;j++) {
				if(bg1[i][j]!=bg2[i][j]) {
					++diff;
				}
			}
		}
		return diff;
	}
	private static int count(int[][] bg) {
		int diff = 0;
		for(int i=0;i<bg.length;i++) {
			for(int j=0;j<bg[0].length;j++) {
				if(bg[i][j]==1) {
					++diff;
				}
			}
		}
		return diff;
	}
	private static boolean oscillate(Vector<Double> v1,Vector<Double> v2){
		boolean isOscillate = true;
		for(int i=0; i<v1.size(); i++){
			if(Math.abs(v1.get(i)-v2.get(i))>0.005){
				isOscillate = false;
				break;
			}
		}
		return isOscillate;
	}
	
	public static double sigmoid(double x){
	
		
		return (double)1/(1-Math.exp(0-x));
	}
	/*public static void gd(Elem[] src, Elem[] ref, int[][] manual_bg){
		//iteration 1st
		int len =20; //
		Vector<Double> weights = VecUtil.getVec(len);
		VecUtil.eval(weights, 0);
		double b = 0;
		int max_iter_num = 1000, counter = 0,flag=2;
		Vector<Double> mini = weights;
		double alpha = 0.1;
		while(counter<max_iter_num){
			double manual = FlexibleTreeMatching.getCost(src, ref, manual_bg, weights);
			int[][] method_bg = generate_individuals(src,ref,weights);
			Vector<Double> paras = get_paras_vectors(src, ref, method_bg,len);
			//Activation Function(ACF): sigmoid(weights * paras + b)
			//Loss Function: 1/2 * (ACF-manual)^2
			//gradient: (ACF-manual)*partial(ACF)/partial(weights)
			
			//and so as other weights
			double gradient_w0 = sigmoid(VecUtil.dotProduct(weights, paras)+b-manual)
					*sigmoid(VecUtil.dotProduct(weights, paras)+b)
					*(1-sigmoid(VecUtil.dotProduct(weights, paras)+b))*paras.get(0);
			
			Vector<Double> gradient = new Vector<>();
			for(int i=0;i<len;i++){
				double cur_grad = sigmoid(VecUtil.dotProduct(weights, paras)+b-manual)
						*sigmoid(VecUtil.dotProduct(weights, paras)+b)
						*(1-sigmoid(VecUtil.dotProduct(weights, paras)+b))*paras.get(i);
				gradient.add(0-cur_grad*alpha);
			}
			weights = VecUtil.add(weights, gradient);
		}
		
	}*/
	
	public static Vector<Double> LineSearch(Vector<Double> gradient, int length, Vector<Double> weights, Vector<Double> paras_method, Vector<Double> paras_manual) {
		double alpha = 1,
			   c = 0.001;
		
		double f1, f2;
		int iter = 1;
		
		Vector<Double> p = new Vector<Double>();
		for (int i=0; i<gradient.size(); i++) {
			p.add( gradient.get(i) > 0 ? -1.0: 1.0 );
		}

		Vector<Double> new_weight = new Vector<Double>();
		while(true) {
						
			new_weight.clear();
			
			for(int i=0; i<gradient.size(); i++)
				new_weight.add( alpha*p.get(i) + weights.get(i) );
			
			double v1, v2, v3;
			v1 = 0;
			v2 = 0;
			v3 = 0;
			
//			Vector<Double> y1 = VecUtil.sigmoid(weights, paras_method);
//			Vector<Double> predict1 = VecUtil.sigmoid(weights, paras_manual);
//			
//			Vector<Double> y2 = VecUtil.sigmoid(new_weight, paras_method);
//			Vector<Double> predict2 = VecUtil.sigmoid(new_weight, paras_manual);
			
			
			Vector<Double> w_2 = VecUtil.getVec(weights.size());
			VecUtil.eval(w_2, 1);
			w_2.set(0, 1.0/20);
			w_2.set(1, 1.0/20);
			w_2.set(2, 1.0/20);
			w_2.set(3, 1.0/20);
			w_2.set(4, 1.0/20);
			w_2.set(9, 1.0/20);
			w_2.set(11, 1.0/40);
			w_2.set(12, 1.0/40);
			w_2.set(14, 1.0/20);
			w_2.set(15, 1.0/20);
			w_2.set(16, 1.0/10);
			w_2.set(17, 1.0/20);
			w_2.set(18, 1.0/20);
			Vector<Double> predict = VecUtil.sigmoid(w_2, paras_manual);
			Vector<Double> y = VecUtil.sigmoid(w_2, paras_method);
			
			Vector<Double> y1 = VecUtil.multiply(weights, y);
			Vector<Double> y2 = VecUtil.multiply(new_weight, y);
			
			Vector<Double> predict1 = VecUtil.multiply(weights, predict);
			Vector<Double> predict2 = VecUtil.multiply(new_weight, predict);
			
			

			for (int i=0; i<length; i++) {
				
				v1 += 0.5 * Math.pow(y2.get(i)-predict2.get(i), 2);  // f(x+ap)
				v2 += 0.5 * Math.pow(y1.get(i)-predict1.get(i), 2);  // f(x)
				v3 += c * alpha * p.get(i) * gradient.get(i);
			}

//			System.out.println("index: " + iter + " " + v1 + " " + v2 + " " + v3);
			
			f1 = v1;
			f2 = v2 + v3;
			
			
			if (f1 <= f2) break;
			else alpha = alpha*0.9;
			++iter;
		} 
		
	System.out.println("alpha: " + alpha);
		
		
		return new_weight;
	}
	
	/*public static double gradient_alpha(double alpha, Vector<Double> gradient, int length, Vector<Double> weights, Vector<Double> paras_method, Vector<Double> paras_manual) {
		double res=0;
		for (int i=0; i<length; i++) {
			res += (weights.get(i)+alpha*gradient.get(i))*(paras_method.get(i)-paras_manual.get(i))*gradient.get(i);
		}
		
		return -1*res;
	}*/
		
	public static Vector<Double> gd(Vector<Double> theta1, Elem[] src, Elem[] ref, int[][] manual_bg, int max_iter){
		//iteration 1st
		int len = Compare.getProperties(src[0]).size()+3; //
		/*Vector<Double> weights = VecUtil.getVec(len);
		VecUtil.eval(weights,1);*/
		Vector<Double> weights = theta1;
		
		double b = 0;
		double alpha = 0.01;
		Vector<Double> prev_grad = new Vector<>();
		for(int i=0;i<len;i++){
			prev_grad.add(0.0);
		}
		int max_iter_num = max_iter, counter = 0, flag=2;
		//Vector<Double> mini = weights;

		
		while(counter!=max_iter_num){

			int[][] method_bg = generate_individuals(src,ref,weights);
			
			Vector<Double> paras_method = FlxmUtil.calViolations(src, ref, method_bg);
			Vector<Double> paras_manual = FlxmUtil.calViolations(src, ref, manual_bg);

			
			Vector<Double> gradient = new Vector<>();
			for(int i =0;i<paras_manual.size();i++) {
				//grad = -(y-f)*f
				double grad = weights.get(i)*(paras_manual.get(i)-paras_method.get(i))*paras_method.get(i);
				double step = alpha*(0-grad);
				gradient.add(step);
			}
			weights = VecUtil.add(weights, gradient);
			if (counter%10 == 1) alpha *= 0.7;
			counter++;
			
			System.out.println("diff="+bgDiff(method_bg, manual_bg));
			print(paras_method,"para_method:\n");
			print(paras_manual,"para_man:\n");
			print(weights, "weights");
		}
		
		return weights;
	}
	
	
	public static Vector<Double> gd2(Vector<Double> theta, Elem[] src, Elem[] ref, int[][] manual_bg, int max_iter){
		//iteration 1st
		int len = Compare.getProperties(src[0]).size()+3; 
		/*Vector<Double> weights = VecUtil.getVec(len);
		VecUtil.eval(weights,1);*/
		
		Vector<Double> weights = theta;
		
		double alpha = 1;
//		Vector<Double> beta = VecUtil.getVec(len);
//		VecUtil.eval(beta, 0);		
//		Vector<Double> weights = VecUtil.exp(beta);
		
		int max_iter_num = max_iter, counter = 0, flag=2;
		
		Vector<Double> pmethod2 = new Vector<Double>();
		Vector<Double> pmanual2 = new Vector<Double>();
		int diff2 = 10000;
		
		while(counter!=max_iter_num){
			System.out.println(counter+"th.....  ");
			int[][] method_bg = generate_individuals(src,ref,weights);
	
			
			Vector<Double> paras_method = FlxmUtil.calViolations(src, ref, method_bg); //todo  normalize
			Vector<Double> paras_manual = FlxmUtil.calViolations(src, ref, manual_bg); //todo

//			paras_method = FlxmUtil.normalize(paras_method);
//			paras_manual = FlxmUtil.normalize(paras_manual);
			
			/*if ( bgDiff(method_bg, manual_bg) > diff2) {
				paras_method = pmethod2;
				paras_manual = pmanual2;
				counter++;
				continue;
			}*/
			
			pmethod2 = paras_method;
			pmanual2 = paras_manual;
			diff2 = bgDiff(method_bg, manual_bg);
			
			
			System.out.println("diff="+bgDiff(method_bg, manual_bg));
			print(paras_method,"para_method:\n");
			print(paras_manual,"para_me:\n");

			
			double lamda = 1;
			double sum = 0;;
			for (int i=0; i<len; i++) {
				sum += 2 * lamda * (paras_method.get(i) - paras_manual.get(i));
			}
			
			//12.7
			/*Vector<Double> gradient = new Vector<>();
			double y = VecUtil.dotProduct(weights, paras_manual),
					predict = VecUtil.dotProduct(weights, paras_method);
			double diff =Math.pow(predict-y,2)+lamda*VecUtil.norm(weights,2);
			System.out.println("diff::======"+diff*diff);
			
			Vector<Double> dissV= new Vector<>();
			dissV.add(diff);
			gradient = VecUtil.multiply(dissV, paras_method);
			
			
			Vector<Double> regualize = VecUtil.multiply(lamda, weights);
			gradient = VecUtil.add(gradient, regualize);*/
			
			Vector<Double> gradient = new Vector<>();
			
			Vector<Double> y = VecUtil.sigmoid(weights, paras_manual);
			Vector<Double> predict = VecUtil.sigmoid(weights, paras_method);
			
			print(y,"y1111");
			print(predict, "predict");
			
			for (int i=0; i<len; i++) {
				//gradient.add( (y-predict)*(-1 * paras_method.get(i))*(predict)*(1-predict) );
				gradient.add( y.get(i)*(1-y.get(i))*paras_manual.get(i) - predict.get(i)*(1-predict.get(i))*paras_method.get(i));
			}
			
			//gradient = VecUtil.
			
//			for(int i=0;i<len;i++){
//				//gradient.add(0-cur_grad*alpha*paras_method.get(i));
//				gradient.add(0-diss*paras_method.get(i));
//			}
			
			//alpha = LineSearch(gradient, len, weights, predict, y);
			System.out.println("alpha = " + alpha);
			
			
			Vector<Double> new_gradient = new Vector<>();

			
			for(int i=0;i<len;i++){
				new_gradient.add(0.0-gradient.get(i)*alpha);
			}
			
			weights = VecUtil.add(weights, new_gradient);
			
			
			if (counter%10 == 0 && counter>0) alpha *= 0.9;
			
			counter++;
			
			print(new_gradient, "newGrad:\n");
			print(weights,"weight:\n");
			print(gradient,"grad:\n");
			
			
		}
		
		return weights;
	}

	public static Vector<Double> gd3(Vector<Double> theta, Elem[] src, Elem[] ref, int[][] manual_bg, int max_iter){
		//iteration 1st
		int len = Compare.getProperties(src[0]).size()+3; 
		/*Vector<Double> weights = VecUtil.getVec(len);
		VecUtil.eval(weights,1);*/
		
		Vector<Double> weights = theta;
		
		double alpha = 0.5;

		int max_iter_num = max_iter, counter = 0, flag=2;
		
		int minDiff = 1000;
		Vector<Double> minW = new Vector<Double>();
		
		while(counter!=max_iter_num){
			
			/*for (int i=0; i<len; i++) {
				weights.set(i,  Math.exp( weights.get(i)) );
				
			}*/
			
			//weights = FlxmUtil.normalize(weights);
			
			
			System.out.println(counter+"th.....  ");
			int[][] method_bg = generate_individuals(src,ref,weights);
	
			
			Vector<Double> paras_method = FlxmUtil.calViolations(src, ref, method_bg); //todo  normalize
			Vector<Double> paras_manual = FlxmUtil.calViolations(src, ref, manual_bg); //todo
			
//			for(int i =0;i<manual_bg.length;i++ ) {
//				String msg = "";
//				for(int j=0;j<manual_bg[i].length;j++) {
//					msg+=method_bg[i][j]+" ";
//				}
//				System.out.println(msg);
//			}
			
			
			
//			paras_method = FlxmUtil.normalize(paras_method);
//			paras_manual = FlxmUtil.normalize(paras_manual);
			
			/*if ( bgDiff(method_bg, manual_bg) > diff2) {
				paras_method = pmethod2;
				paras_manual = pmanual2;
				counter++;
				continue;
			}*/
//			
//			pmethod2 = paras_method;
//			pmanual2 = paras_manual;
//			diff2 = bgDiff(method_bg, manual_bg);
//			
			
			
			
			if (minDiff >= bgDiff(method_bg, manual_bg)) {
				minDiff = bgDiff(method_bg, manual_bg);
				minW = weights;
			}
			
			
			Vector<Double> gradient = new Vector<>();
			
			Vector<Double> w_2 = VecUtil.getVec(weights.size());
			VecUtil.eval(w_2, 1);
			Vector<Double> y = VecUtil.sigmoid(weights, paras_manual);
			Vector<Double> predict = VecUtil.sigmoid(weights, paras_method);
			
			System.out.println("diff="+bgDiff(method_bg, manual_bg));
			print(predict,"para_method:\n");
			print(y,"para_manual:\n");
			
			//print(y,"y1111:\n");
			//print(predict, "predict:\n");
			
			for (int i=0; i<len; i++) {
				//gradient.add( (y-predict)*(-1 * paras_method.get(i))*(predict)*(1-predict) );
				//gradient.add( y.get(i)*(1-y.get(i))*paras_manual.get(i)*weights.get(i) - predict.get(i)*(1-predict.get(i))*paras_method.get(i)*weights.get(i));
			
				//gradient.add( y.get(i)*(1-y.get(i))*paras_manual.get(i) - predict.get(i)*(1-predict.get(i))*paras_method.get(i));
				gradient.add( (y.get(i)-predict.get(i))*( y.get(i)*(1-y.get(i))*paras_manual.get(i) - predict.get(i)*(1-predict.get(i))*paras_method.get(i) ) );
			}

//			print(gradient, "gradient:\n");
			
			
			
			Vector<Double> new_weight = LineSearch(gradient, len, weights, paras_method, paras_manual);;
			
			weights = new_weight;
			
			//if (counter%5 == 0 && counter>0) alpha *= 0.9;
			
			counter++;
			
			
			
			//print(new_gradient, "newGrad:\n");
			//print(weights,"weight:\n");
			print(gradient,"grad:\n");
			
//			System.out.println("minDiff: " + minDiff);
//			print(minW, "min_weight:\n");
			
			
		}
		
		
		
		return weights;
	}
	
	public static Vector<Double> gd_sigmoidWithoutWeight(Vector<Double> theta, Elem[] src, Elem[] ref, int[][] manual_bg, int max_iter){
		//iteration 1st
		int len = Compare.getProperties(src[0]).size()+3; 
		/*Vector<Double> weights = VecUtil.getVec(len);
		VecUtil.eval(weights,1);*/
		
		Vector<Double> weights = theta;
		
		double alpha = 1;

		int max_iter_num = max_iter, counter = 0;
		
		int minDiff = 1000;
		Vector<Double> minW = new Vector<Double>();
		
		while(counter!=max_iter_num){
			
			System.out.println(counter+"th.....  ");
			int[][] method_bg = generate_individuals(src,ref,weights);
	
			
			Vector<Double> paras_method = FlxmUtil.calViolations(src, ref, method_bg); //todo  normalize
			Vector<Double> paras_manual = FlxmUtil.calViolations(src, ref, manual_bg); //todo
			
			
			Vector<Double> gradient = new Vector<>();
			
			Vector<Double> w_2 = VecUtil.getVec(weights.size());
			VecUtil.eval(w_2, 1);
			w_2.set(0, 1.0/20);
			w_2.set(1, 1.0/20);
			w_2.set(2, 1.0/20);
			w_2.set(3, 1.0/20);
			w_2.set(4, 1.0/20);
			w_2.set(9, 1.0/20);
			w_2.set(11, 1.0/40);
			w_2.set(12, 1.0/40);
			w_2.set(14, 1.0/20);
			w_2.set(15, 1.0/20);
			w_2.set(16, 1.0/10);
			w_2.set(17, 1.0/20);
			w_2.set(18, 1.0/20);
			Vector<Double> y = VecUtil.sigmoid(w_2, paras_manual);
			Vector<Double> predict = VecUtil.sigmoid(w_2, paras_method);
			
//			System.out.println("diff="+bgDiff(method_bg, manual_bg));
			print(predict,"pre:\n");
			print(y,"y:\n");
			
			
			for(int i=0;i<paras_manual.size();i++){
				double grad = (y.get(i)*weights.get(i)-predict.get(i)*weights.get(i))*paras_manual.get(i);
				gradient.add(alpha*(0-grad));
			}
			
			//print(y,"y1111:\n");
			//print(predict, "predict:\n");
			
//			for (int i=0; i<len; i++) {
//				gradient.add( (y.get(i)-predict.get(i))*( y.get(i)*(1-y.get(i))*paras_manual.get(i) - predict.get(i)*(1-predict.get(i))*paras_method.get(i) ) );
//			}

//			print(gradient, "gradient:\n");
			
			
			
			Vector<Double> new_weight = LineSearch(gradient, len, weights, paras_method, paras_manual);;
			
			weights = new_weight;

			
			
			
			//print(new_gradient, "newGrad:\n");
			System.out.println("diff="+bgDiff(method_bg, manual_bg));
			print(gradient, "grad:\n");
			print(paras_method,"para_method:\n");
			print(paras_manual,"para_man:\n");
			print(weights, "weights");
			
			
		}
		
		
		
		return weights;
	}
	
//	public static void main(String[] args) throws IOException, SQLException {
//		String webDriverName = "webdriver.firefox.bin"; 
//		String firefoxPath = "D:/coding/Mozilla Firefox/firefox.exe"; // this requires you install a firefox
//		String src = "src";
//		String ref = "ref";
//		String manual = "src_ref";
//		Connection conn = DBConnect.getConnect();
//		
//		String src_url = "D:\\test_src\\src.html"; //must use '\\' instead of '/'
//		String ref_url = "D:\\test_ref\\ref.html";
//		
//		//The following will get two snapshots and sql files
//		Util.snapping(src_url, "img/", "src.png", webDriverName, firefoxPath);
//		Util.snapping(ref_url, "img/", "ref.png", webDriverName, firefoxPath);
//		
//		Vector<Element> src_elems = Util.getElems(src, "img/src.png", conn);
//		Vector<Element>  ref_elems = Util.getElems(ref, "img/src.png", conn);
//		Vector<Element>  man_elems = Util.getElems(manual, "img/src.png", conn);
//		
//		
//	}
	/*
	public static void main(String[] args) throws IOException, SQLException {
		
		/*String srcpage = "tar6";
		String refpage = "ref6";
		String srcImg = "H:\\XAMPP\\xampp\\htdocs\\fxtm_website\\img\\" + srcpage + ".png";
		String refImg = "H:\\XAMPP\\xampp\\htdocs\\fxtm_website\\img\\" + refpage + ".png";
		String manual = srcpage+"_"+refpage+"_me";
		
		Connection conn = DBConnect.getConnect();
		Elem[] src = (Elem[]) Util.getElems(srcImg, "select * from "+srcpage+"_"+refpage+" where isZero=0", conn).toArray(new Elem[1]);
		Elem[] ref = (Elem[]) Util.getElems(refImg, "select * from "+refpage+" where isZero=0", conn).toArray(new Elem[1]);
		
		Elem[] man = (Elem[]) Util.getElems( srcImg, "select * from "+manual+" where isZero=0", conn).toArray(new Elem[1]);
		
		int bg[][] = GraphUtil.getBinaryGraph(man, ref, true);
		
		System.out.println("show bg:");
		for (int i = 0; i < man.length+1; i++) {
			for (int j = 0; j < ref.length+1; j++) {
				System.out.print(bg[i][j]);
			}
			System.out.print('\n');
		}
		
		System.out.println("bg finished...");
		Vector<Double> theta = getTheta(src, ref, bg);
		//Vector<Double> theta = gd(src, ref, bg);
		String info = "";
		for(int i=0;i<theta.size();i++){
			info+=theta.get(i)+",";
		}
		System.out.println(info); }
		*/
	
	public static void main(String[] args) throws IOException, SQLException {		
		
		
		String[] _srcpage = {"tar5"};
		String[] _refpage = {"ref5"};
		
		Elem[][] src1 = new Elem [_srcpage.length][];
		Elem[][] ref1 = new Elem [_refpage.length][];
		Elem[][] man1 = new Elem [_srcpage.length][];
		int[][][] bg1 = new int  [_srcpage.length][][];
		
		Connection conn = DBConnect.getConnect();
		
		for (int i=0; i<_srcpage.length; i++) {
			String srcImg = "img/" + _srcpage[i] + ".png";
			String refImg = "img/" + _refpage[i] + ".png";
			String manual = _srcpage[i]+"_"+ _refpage[i]+"_me";
			
//			src1[i] = (Elem[]) Util.getElems(srcImg, "select * from "+_srcpage[i]+"_"+_refpage[i]+" where isZero=0", conn).toArray(new Elem[1]);
//			ref1[i] = (Elem[]) Util.getElems(refImg, "select * from "+_refpage[i]+" where isZero=0", conn).toArray(new Elem[1]);
//			man1[i] = (Elem[]) Util.getElems(srcImg, "select * from "+manual+" where isZero=0", conn).toArray(new Elem[1]);
			
			
			src1[i] = Util.getElemsArray(srcImg, "select * from "+_srcpage[i]+"_"+_refpage[i]+" where isZero=0", conn);
			ref1[i] = Util.getElemsArray(refImg, "select * from "+_refpage[i]+" where isZero=0", conn);
			man1[i] = Util.getElemsArray(srcImg, "select * from "+manual+" where isZero=0", conn);
			
			bg1[i] = GraphUtil.getBinaryGraph(man1[i], ref1[i], true);
			System.out.println("bg finished...");
			int len = Compare.getProperties(src1[0][0]).size()+3;
			Vector<Double> theta = VecUtil.getVec(len);
			VecUtil.eval(theta, 1);
			theta.set( theta.size()-1 , 3.0);
			int max_iter = 10;
			System.out.println(bg1[i].length+" "+bg1[i][0].length);
			for (int m=0; m<max_iter; m++) {
				for (int j=0; j<_srcpage.length; j++) {
//					theta = getTheta(theta, src1[j], ref1[j], bg1[j], 1000,null);
					theta = gd_sigmoidWithoutWeight(theta, src1[j], ref1[j], bg1[i], 3000);
					String info = "";
					for(int k=0;k<theta.size();k++){
						info+=theta.get(k)+",";
					}
					System.out.println("grad:" + m + ":" + info);
				}
			}
		}
		
		
		
//		String srcpage = "tar5";
//		String refpage = "ref5";
//		String srcImg = "img/" + srcpage + ".png";
//		String refImg = "img/" + refpage + ".png";
//		String manual = srcpage+"_"+refpage+"_me";
//		Connection conn = DBConnect.getConnect();
//		
//		Elem[] src = Util.getElemsArray(srcImg, "select * from "+srcpage+"_"+refpage+" where isZero=0", conn);
//		Elem[] ref = Util.getElemsArray(refImg, "select * from "+refpage+" where isZero=0", conn);	
//		Elem[] man = Util.getElemsArray( srcImg, "select * from "+manual+" where isZero=0", conn);
//		
////		test();
//		evaluation(src, man, ref);
//		
		 
	}
	
	public static void evaluation_v2(Elem[] predict, Elem[] groundTruth, Elem[] reference){
		int tp=0,
			tn=0,
			fp=0,
			fn=0;
		int[][] preAM = new int[predict.length][];
		int[][] gndAM = new int[groundTruth.length][];
		for(int i=0;i<preAM.length;i++){
			preAM[i] = new int[reference.length];
			gndAM[i] = new int[reference.length];
		}
		for(int i=0;i<predict.length;i++){
			if(predict[i].getMid()==0||groundTruth[i].getMid()==0){
				tp = predict[i].getMid()==groundTruth[i].getMid()?tp+1:tp;
				fp = (predict[i].getMid()==0||groundTruth[i].getMid()!=0)?fp+1:fp;
				fn = (predict[i].getMid()!=0||groundTruth[i].getMid()==0)?fn+1:fn;
			}else{
				for(int j=0;j<reference.length;j++){
					preAM[i][j] = predict[i].getMid()==reference[j].getId()?1:0;
					gndAM[i][j] = groundTruth[i].getMid()==reference[j].getId()?1:0;
					int v = preAM[i][j]*10+gndAM[i][j];
					switch (v) {
					case 11:
						++tp;
						break;
					case 00:
						++tn;
						break;
					case 01:
						++fn;
						break;
					case 10:
						++fp;
						break;

					default:
						break;
					}
				}
			}
			
		}
		double precision = (double) tp/(tp+fp),
				recall = (double) tp/(tp+fn);
		double f1 = 2*precision*recall/(precision+recall);
		System.out.println(tp+" "+tn+" "+fp+" "+fn);
		String msg = "precision="+precision+" recall="+recall+" f1="+f1;
		System.out.println(msg);
		
	}
	
	public static void evaluation(Elem[] predict, Elem[] groundTruth, Elem[] reference){
		int tp=0,
			tn=0,
			fp=0,
			fn=0;
		int[][] preAM = new int[predict.length][];
		int[][] gndAM = new int[groundTruth.length][];
		for(int i=0;i<preAM.length;i++){
			preAM[i] = new int[reference.length];
			gndAM[i] = new int[reference.length];
		}
		for(int i=0;i<predict.length;i++){
			if(predict[i].getMcls()==0||groundTruth[i].getMcls()==0){
				tp = predict[i].getMcls()==groundTruth[i].getMcls()?tp+1:tp;
				fp = (predict[i].getMcls()==0||groundTruth[i].getMcls()!=0)?fp+1:fp;
				fn = (predict[i].getMcls()!=0||groundTruth[i].getMcls()==0)?fn+1:fn;
			}else{
				for(int j=0;j<reference.length;j++){
					preAM[i][j] = predict[i].getMcls()==reference[j].getCid()?1:0;
					gndAM[i][j] = groundTruth[i].getMcls()==reference[j].getCid()?1:0;
					int v = preAM[i][j]*10+gndAM[i][j];
					switch (v) {
					case 11:
						++tp;
						break;
					case 00:
						++tn;
						break;
					case 01:
						++fn;
						break;
					case 10:
						++fp;
						break;

					default:
						break;
					}
				}
			}
			
		}
		double precision = (double) tp/(tp+fp),
				recall = (double) tp/(tp+fn);
		double f1 = 2*precision*recall/(precision+recall);
		System.out.println(tp+" "+tn+" "+fp+" "+fn);
		String msg = "precision="+precision+" recall="+recall+" f1="+f1;
		System.out.println(msg);
		
	}
	
	private static void print(Vector<Double> v, String... info) {
		String msg = (String) (info.length>0?info[0]:"");
		for(double i : v) {
			msg+=i+", ";
		}
		System.out.println(msg);
	}
	public static void test() throws IOException, SQLException {
		//double[] theta = {2.187647813030699,2.8371880630724107,3.475013134115377,2.6123098661783932,1.3857506630504501,2.739346609821426,1.1844761555871206,1.0,1.0,2.0800060098866475,1.0,2.2472276019023387,1.4062279456390472,1.0,1.6728063540763742,3.3150379346289927,1.7481579239362202,1.3113684578713092,1.4177383345160923,2.0737321698620703,2.6153590214177616,1.0,1.0,1.0,1.0,1.0,4.886719653035512,1.0,1.0,1.0,2.1322977049346834,2.380375789051701,23.596282926062333};
		Vector<Double> weights = new Vector<>();
//		double[] theta = {2.0565172770604447, -2.4916939663541164, -6.9893194712642925, 0.8590058647421877, -0.5790484997876924, -7.990612170979992, -1.2352197667012121, 1.0, 1.0, 14.563390587933155, 1.0, 2.2566114926721346, 15.46201692829746, 1.0, 9.54753626572242, -6.287951612318866, 2.0985707422990787, 9.953570335060437, 13.283048509656808, 1.4222656735732657, 1.1370619989191928, 1.0, 1.0, 1.0, 1.0, 1.0, -4.345207966262257, 1.0, 6.225933497475607, 6.225933497475607, 62.717638365466094, 18.37246150858046, 21.64844534895708};
		
//		double[] theta = {1.0000000000000013,1.9000000000000148,1.0000000000000013,2.000000000000001,1.0,1.0,1.0,1.0,1.0,2.0,1.0,2.0,1.9000000000000001,1.0,1.0,1.0,1.0,1.9000000000000121,1.900000000000014,2.0,1.9000000000000148,1.0,1.0,1.0,1.0,1.0,1.9000000000000135,1.0,1.0,1.0,1.0,2.9000000000000132,10.900000000000013};
//		double[] theta = {4.000845053248746, -4.0810424768944475E-4, -4.0810424768944475E-4, -4.0810424768944475E-4, -4.0810424768944475E-4, 4.000845053248746, 4.000845053248746, 4.000845053248746, 4.000845053248746, 4.000845053248746, 4.000845053248746, 4.000845053248746, 4.000845053248746, 4.000845053248746, -4.0810424768944475E-4, 4.000845053248746, 4.000845053248746, 4.000845053248746, 4.000845053248746, 4.000845053248746, -4.0810424768944475E-4, 4.000845053248746, 4.000845053248746, 4.000845053248746, 4.000845053248746, 4.000845053248746, 4.000845053248746, 4.000845053248746, 4.000845053248746, 4.000845053248746, 4.000845053248746, 4.000845053248746, 6.000845053248746}; 

//		double [] theta= {4.501323086381922, 6.400150379495874, -2.169113782552403, 8.07041748726603, -9.241025723039911, -15.855522818602152, -2.9658413633337113, 1.0, 1.0, 13.684717211196691, 1.0, -6.146945540060568, 8.742982952359428, 1.0, -15.249634936346201, -19.0020649723925, -4.9763019500868495, 10.190186839196945, 10.610262299012135, -3.3364258385905465, 4.772523848084024, 1.0, 1.0, 1.0, 1.0, 1.0, -5.464795810437314, 1.0, 6.458838777082993, 6.458838777082993, 11.776871820045557, -0.9244273537661711, 5.434025250303517};
		double [] theta = {10.01762321484031, 10.614293711186827, 23.172173360156524, 12.955694873526209, -95.83065445389502, -1.9999999999999996, -17.707106781186546, 1.0, 1.0, -15.982535796031625, 1.0, 1.900485404549924, 8.579320725889266, 1.0, -73.72682165426797, -99.1025605604488, 52.30275031981155, -5.455705953211161, 35.18362588599148, -20.39913985139627, -0.7378527712570266, 3.5689141007523464, 1.0, 1.0, 1.0, 1.0, -6.447213595499958, 1.0, 1.0, 1.0, 63.196869960985254, 18.861711752576134, 12.812556457844572};
		for(int i=0;i<theta.length;i++) {
			weights.add(theta[i]);
		}
		String srcpage = "tar5", srcImg = "img/tar5.png";
		String refpage = "ref5", refImg = "img/ref5.png";
//		String manual = srcpage+"_"+refpage+"_me";
		Connection conn = DBConnect.getConnect();
		Elem[] src =  Util.getElemsArray(srcImg, "select * from "+srcpage+" where isZero=0", conn);
		Elem[] ref =  Util.getElemsArray(refImg, "select * from "+refpage+" where isZero=0", conn);
		HashMap<String, HashMap<String, Vector<Edge>>> caches = new HashMap<>();
		HashMap<String, Vector<Edge>> cache = null;
		if( caches.get(srcpage+"_"+refpage)!= null){
			cache = caches.get(srcpage+"_"+refpage);
		}
		else {
			cache = new HashMap<>();
		}
		int[][] method_bg = generate_individuals(src,ref,weights);
		int [] match = new int[src.length];
		for(int i=0;i<src.length;i++) {
			for(int j=0;j<method_bg[0].length;j++) {
				if(method_bg[i][j]==1) {
					//match[i] = j==ref.length?0:ref[j].getId();
					match[i] = j==ref.length?0:ref[j].getId();
				}
			}
		}
		String pre = srcpage+"_"+refpage;
		String update = "update "+pre+" set matchID = 0, matchCls=0";
		Util.update(update);
		/*for(int i=0;i<ref.length;i++) {
			String tmp = "update "+refpage +" set clusterID="+ref[i].getId()+" where ID="+ref[i].getId();
			Util.update(tmp);
		}*/
		
		for(int i=0;i<match.length;i++) {
			//String tmp = "update "+pre +" set clusterID="+src[i].getId()+" where ID="+src[i].getId();
			//Util.update(tmp);
			if(match[i]>0){
				String sql = "update "+pre+" set matchCls = "+ref[ElemUtil.getElemIdxById(ref, match[i])].getCid()+" where clusterID="+src[i].getCid();
			Util.update(sql);
			}
			
		}
//		evaluation_v2(predict, groundTruth, reference)
	}
	
	
}

