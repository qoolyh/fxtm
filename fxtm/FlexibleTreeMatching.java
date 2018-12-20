package fxtm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.sun.javafx.scene.paint.GradientUtils.Parser;

import sun.net.www.content.text.plain;

public class FlexibleTreeMatching {
	/**
	 * 
	 * @param e1 the source element
	 * @param e2 the reference element
	 * @param src
	 * @param ref
	 * @param bg binary graph
	 * @param upperOrLower a boolean value to choose which result you wanna get (the upper cost or lower cost)
	 * @param weight_acs
	 * @return
	 */
	public static double getAncestorViolation(Elem e1, Elem e2, Elem[] src, Elem[] ref, int[][] bg,
			boolean upperOrLower, double weight_acs, Edge e) {
		double v = 0;
		int num_may_cause_ACV_e1 = GraphUtil.countViolateNodes(e1, e2, src, ref, bg, true, true, true);
		int num_maynot_avoid_ACV_e1 = GraphUtil.countViolateNodes(e1, e2, src, ref, bg, true, true, false);

		int num_may_cause_ACV_e2 = GraphUtil.countViolateNodes(e1, e2, src, ref, bg, true, false, true);
		int num_maynot_avoid_ACV_e2 = GraphUtil.countViolateNodes(e1, e2, src, ref, bg, true, false, false);
		
		e.ref_lower_acs = num_maynot_avoid_ACV_e2;
		e.ref_upper_acs = num_may_cause_ACV_e2;
		e.src_lower_acs = num_maynot_avoid_ACV_e1;
		e.src_upper_acs = num_may_cause_ACV_e1;
		if (upperOrLower) {
			v = num_may_cause_ACV_e1 + num_may_cause_ACV_e2;
		} else {
			v = num_maynot_avoid_ACV_e1 + num_maynot_avoid_ACV_e2;
		}
		return (double) v * weight_acs;
	}

	/**
	 * 
	 * @param e1
	 *            the src element
	 * @param e2
	 *            the ref element
	 * @param src
	 * @param ref
	 * @param bg
	 * @param upperOrLower
	 *            a boolean value to choose which result you wanna get (the upper
	 *            cost or lower cost)
	 * @param w_sib
	 * @return
	 */
	public static double getSiblingViolation(Elem e1, Elem e2, Elem[] src, Elem[] ref, int[][] bg, boolean upperOrLower,
			double weight_sib, Edge e) {
		double v = 0;

		int num_may_cause_SIBV_e1 = GraphUtil.countViolateNodes(e1, e2, src, ref, bg, false, true, true);
		int num_maynot_avoid_SIBV_e1 = GraphUtil.countViolateNodes(e1, e2, src, ref, bg, false, true, false);

		int num_may_cause_SIBV_e2 = GraphUtil.countViolateNodes(e1, e2, src, ref, bg, false, false, true);
		int num_maynot_avoid_SIBV_e2 = GraphUtil.countViolateNodes(e1, e2, src, ref, bg, false, false, false);

		int num_may_reduce_SIBV_e1 = GraphUtil.countValidNodes(e1, e2, src, ref, bg, false, true, true)+1;
		int num_must_reduce_SIBV_e1 = GraphUtil.countValidNodes(e1, e2, src, ref, bg, false, true, false);

		int num_may_reduce_SIBV_e2 = GraphUtil.countValidNodes(e1, e2, src, ref, bg, false, false, true)+1;
		int num_must_reduce_SIBV_e2 = GraphUtil.countValidNodes(e1, e2, src, ref, bg, false, false, false);
		
		e.ref_lower_sibD = num_maynot_avoid_SIBV_e2;
		e.ref_lower_sibI = num_must_reduce_SIBV_e2;
		e.ref_upper_sibD = num_may_cause_SIBV_e2;
		e.ref_upper_sibI = num_may_reduce_SIBV_e2;
		
		e.src_lower_sibD = num_maynot_avoid_SIBV_e1;
		e.src_lower_sibI = num_must_reduce_SIBV_e1;
		e.src_upper_sibD = num_may_cause_SIBV_e1;
		e.src_upper_sibI = num_may_reduce_SIBV_e1;
		
		if (upperOrLower) {
			v = (double) weight_sib / 2 * (num_may_cause_SIBV_e1 / (1 + num_must_reduce_SIBV_e1)
					+ num_may_cause_SIBV_e2 / (1 + num_must_reduce_SIBV_e2));
		} else {
			v = (double) weight_sib * (num_maynot_avoid_SIBV_e1 / (num_may_reduce_SIBV_e1 * (num_maynot_avoid_SIBV_e1 + 1))
					+ num_maynot_avoid_SIBV_e2 / (num_may_reduce_SIBV_e2 * (num_maynot_avoid_SIBV_e2 + 1)));
		}
		return v;
	}
	
	/**
	 * A function to calculate the visual difference (i.e. visual cost) between two elements
	 * @param e1  the src element
	 * @param e2  the ref element
	 * @return
	 */
	public static double getVisualCost(Elem e1, Elem e2, List<Double> weight_vis) {
		double v_cost = 0;
		v_cost = e1.getId()==e2.getId()?0:1;
		Vector<Double> vis_distance = Compare.elemComp(e1, e2); //here you should try different kinds of vis_distance (e.g. delta(a,b), sigmoid(a,b), etc.)
		return VecUtil.dotProduct(weight_vis, vis_distance);
	}

	private static boolean oracle(double threshold) {
		boolean seletected = false;
		double value = Math.random();
		if (value <= threshold) {
			seletected = true;
		}
		return seletected;
	}
	
	/**
	 * A comparator for sorting the bounds
	 * @author Administrator
	 *
	 */
	private final class CompareEgs implements Comparator<Edge> {

		public int compare(Edge x, Edge y) {
			int res = 0;
			if (x.getMinCost() > y.getMinCost()) {

				res = 1;

			} else if (x.getMinCost() < y.getMinCost()) {
				res = -1;
			} else {
				if (x.getMaxCost() > y.getMaxCost()) {
					res = 1;
				} else if (x.getMaxCost() == y.getMaxCost()) {
					res = 0;
				} else {
					res = -1;
				}
			}
			return res;

		}
	}
	
	/**
	 * Sort the array by lower bound and upper bound. e.g. (1,2),(1,3),(2,2)
	 * @param egs the array to be sorted
	 * @return
	 */
	public static Vector<Edge> sort(Vector<Edge> egs) {
		FlexibleTreeMatching ftm = new FlexibleTreeMatching();
		
		Edge[] res = new Edge[egs.size()];
		Vector<Edge> tmp = new Vector<Edge>();
		ArrayList<Edge> list = new ArrayList<>();
		for (int i = 0; i < egs.size(); i++) {
			list.add(egs.get(i));
		}

		list.sort(ftm.new CompareEgs());
		for(int i=0;i<list.size();i++) {
			tmp.add(list.get(i));
		}
		return tmp;
	}
	
	/**
	 * Initializing the binary graph, the root element in src must match to the ref's root, some special elements(i.e. text, image, input, button) cannot match to usual elements.
	 * @param src
	 * @param ref
	 * @param bg
	 */
	public static void init(Elem[] src, Elem[] ref, int[][] bg) {
		for(int i=0;i<src.length;i++) {
			for(int j=0;j<ref.length;j++) {
				if(src[i].getIsImg()!=ref[j].getIsImg() || src[i].getIsTxt()!=ref[j].getIsTxt() ||
						src[i].getIsInput()!=ref[j].getIsInput()) {
					bg[i][j]=-1;
				}
			}
		}
	}
	
	/**
	 * Return candidate edges in current binary graph.
	 * @param src set of source elements
	 * @param ref set of reference elements
	 * @param bg binary graph between src and ref elements
	 * @param weight_vis the weight of visual distance
	 * @param weight_acs the weight of ancestor violation
	 * @param weight_sib the weight of sibling violation 
	 * @return the set of edges in the binary graph bg
	 */ 
	public static Vector<Edge> getEdges(Elem[] src, Elem[] ref, int[][] bg, boolean[][] decided, Vector<Double> weights){
		
		double  weight_acs = weights.get(weights.size()-3),
				weight_sib = weights.get(weights.size()-2),
				weight_ban = weights.get(weights.size()-1);
		List<Double> weight_vis = weights.subList(0, weights.size()-3);
		Vector<Edge> edges = new Vector<Edge>();
		for(int i=0;i<bg.length;i++) {
			for(int j=0;j<bg[i].length;j++) {
//				System.err.println("i="+i+" j="+j+" decided.len="+decided.length);
					if(bg[i][j]==1 && !decided[i][j]) {
						if(i==src.length||j==ref.length){
							Edge e = new Edge();
							e.setSrc(i);
							e.setRef(j);
							e.setMaxCost(weight_ban); // This cost could be refined
							e.setMinCost(weight_ban);
							edges.add(e);
						}
						else {
							Edge e = new Edge();
						double upper_sib_cost = getSiblingViolation(src[i], ref[j], src, ref, bg, true, weight_sib,e);
						double lower_sib_cost = getSiblingViolation(src[i], ref[j], src, ref, bg, false, weight_sib,e);
						
						double vis_cost = getVisualCost(src[i], ref[j], weight_vis);
						
						double upper_acs_cost = getAncestorViolation(src[i], ref[j], src, ref, bg, true, weight_acs,e);
						double lower_acs_cost = getAncestorViolation(src[i], ref[j], src, ref, bg, false, weight_acs,e);
						
						//System.out.println("edge cost: " + upper_sib_cost + " " + lower_sib_cost);
						//System.out.println("edge cost: " + upper_acs_cost + " " + lower_acs_cost);
						
						
						e.setSrc(i);
						e.setRef(j);
						e.setMaxCost(vis_cost+upper_acs_cost+upper_sib_cost);
						e.setMinCost(vis_cost+lower_acs_cost+lower_sib_cost);
						edges.add(e);
						}
					}
			}
		}
		return edges;
	}
	
	public static Vector<Edge> getEdges(Elem[] src, Elem[] ref, int[][] bg,  Vector<Double> weights){
		
		double  weight_acs = weights.get(weights.size()-3),
				weight_sib = weights.get(weights.size()-2),
				weight_ban = weights.get(weights.size()-1);
		List<Double> weight_vis = weights.subList(0, weights.size()-3);
		Vector<Edge> edges = new Vector<Edge>();
		for(int i=0;i<bg.length;i++) {
			
			for(int j=0;j<bg[i].length;j++) {
				if(bg[i][j]==1) {
					if(i==src.length||j==ref.length){
						Edge e = new Edge();
						e.setSrc(i);
						e.setRef(j);
						e.setMaxCost(weight_ban); // This cost could be refined
						e.setMinCost(weight_ban);
						edges.add(e);
					}
					else {
						Edge e = new Edge();
					double upper_sib_cost = getSiblingViolation(src[i], ref[j], src, ref, bg, true, weight_sib,e);
					double lower_sib_cost = getSiblingViolation(src[i], ref[j], src, ref, bg, false, weight_sib,e);
					
					double vis_cost = getVisualCost(src[i], ref[j], weight_vis);
					
					double upper_acs_cost = getAncestorViolation(src[i], ref[j], src, ref, bg, true, weight_acs,e);
					double lower_acs_cost = getAncestorViolation(src[i], ref[j], src, ref, bg, false, weight_acs,e);
					
					
					e.setSrc(i);
					e.setRef(j);
					e.setMaxCost(vis_cost+upper_acs_cost+upper_sib_cost);
					e.setMinCost(vis_cost+lower_acs_cost+lower_sib_cost);
					
					edges.add(e);
					}
				}
			}
		}
		return edges;
	}
	/**
	 * Remove the other edges which are connected to node k or t, if edge(k,t) is obtained
	 * @param decidedEdge the edge which is decided to be kept
	 * @param edges the set of edges
	 * @param bg the binary graph
	 */
	private static void updateBG(Edge decidedEdge, Vector<Edge> edges, int[][] bg) {
		int src = decidedEdge.getSrc();
		int ref = decidedEdge.getRef();
		int deleteNum = 0;
		int size = edges.size();
//		System.out.println("before...");
//		print(bg);
//		System.out.println("edge decided...."+decidedEdge.getSrc()+", "+decidedEdge.getRef());
		
			Edge curr = decidedEdge;
			int srcIdx = curr.getSrc(),
					refIdx = curr.getRef();
			if(srcIdx== bg.length-1){// that means ref is banned
				for(int j=0;j<bg.length;j++){
					bg[j][refIdx] = j==srcIdx?1:0;
				}
			}else if(refIdx == bg[0].length-1){ // that means src is banned
				for(int j=0;j<bg[srcIdx].length;j++){
					bg[srcIdx][j] = j==refIdx?1:0;
				}
			}else{
				for(int j=0;j<bg.length;j++){
					bg[j][refIdx] = j==srcIdx?1:0;
				}
				for(int j=0;j<bg[srcIdx].length;j++){
					bg[srcIdx][j] = j==refIdx?1:0;
				}
			
//			
//			for(int j=0;j<bg[srcIdx].length;j++){
//				if(srcIdx == bg.length-1){ 
//					bg[srcIdx][j] = j==refIdx?1:0;
//				}else{
//					
//				}
//			}
//			boolean violate = false;
//			if(decidedEdge.getSrc()==bg.length-1) { // that means ref is banned
//				violate = curr.getSrc()==src && curr.getRef()!=ref;	
//			}else if(decidedEdge.getRef()==bg[0].length-1) { // that means src is banned
//				violate = curr.getSrc()!=src && curr.getRef()==ref;
//			}else{
//				violate = (curr.getSrc()==src && curr.getRef()!=ref)|| (curr.getSrc()!=src && curr.getRef()==ref);
//			}
//			if(violate) {
//				edges.remove(i-deleteNum);
//				++deleteNum;
//				bg[curr.getSrc()][curr.getRef()]=0;
//			}
		}
//		System.out.println("after....");
//		print(bg);
	}
	/**
	 * Remove the other edges which connected to node k,t in a decided set of edges (k,t)
	 * @param decidedEdge_idx idicates the index of edges[0,decided_idx] which are decided in edge set 
	 * @param edges the set of edges
	 * @param bg the binary graph
	 */
	private static void updateBG(int decidedEdge_idx, Vector<Edge> edges, int[][] bg) {
		Vector<Integer> src_nodes = new Vector<Integer>();
		Vector<Integer> ref_nodes = new Vector<Integer>();
		GraphUtil.update_bg_by_value(bg, 1); // now this is a full-connected binary graph
		for(int i=0;i<=decidedEdge_idx;i++) {
			Edge tmp = edges.get(i);
			bg[tmp.getSrc()][tmp.getRef()]=1;
			updateBG(tmp, edges, bg);
		}
		
		
//		for(int i=0;i<=decidedEdge_idx;i++) {
//			Edge tmp = edges.get(i);
//			bg[tmp.getSrc()][tmp.getRef()]=1; // connect the decided nodes
//			if(!src_nodes.contains(tmp.getSrc())) {
//				src_nodes.add(tmp.getSrc()); // buddy you forgot the banning nodes
//			}
//			if(!ref_nodes.contains(tmp.getRef())) {
//				ref_nodes.add(tmp.getRef());
//			}
//		}
//		
//		for(int i=0;i<bg.length;i++) {
//			for(int j=0;j<bg[i].length;j++) {
//				if(bg[i][j]!=-1) {
//				bg[i][j] = ((src_nodes.contains(i)||ref_nodes.contains(j))&&bg[i][j]!=1) ? 0:1;
//				}
//			}
//		}
	}
	
	/**
	 * a boolean function to indicate the binary graph is a one-to-one mapping
	 * @param bg
	 * @return
	 */
	private static boolean noMoreEdges(int [][] bg) {
		boolean noMoreEgs = true;
		for(int i=0;i<bg.length-2;i++) {
			int flag = 1;
			for(int j=0;j<bg[i].length-2;j++) {
				if(bg[i][j]==1) {
					--flag;
					if(flag<0) {
						noMoreEgs=false;
						break;
					}
				}
			}
			if(noMoreEgs==false) {
				break;
			}
		}
		return noMoreEgs;
	}
	/**
	 * The flexible tree matching 
	 * complex: O((src.length*ref.length)^2)
	 * @param src the set of source elements
	 * @param ref the set of reference elements
	 */
	public static int[] fxm_one_iteration(Elem[] src, Elem[] ref, int[][] bg, Vector<Double> weights, HashMap<String, Vector<Edge>> cache) {
		
		double threshold = 0.7;
		 long startTime = System.currentTimeMillis();
		//Initializing e.g. root-root, input-input, nav-nav
		//init(src, ref, bg);
		boolean [][] decided = new boolean[bg.length][];
		for(int i=0;i<bg.length;i++) {
			if(i!=bg.length-1) {
			decided[i] = new boolean[bg[0].length];
			int matchIdx = -1, matchNum =0;
			for(int j=0; j<bg[0].length;j++) {
				decided[i][j] = false;
				if(bg[i][j] == 1) {
					++matchNum;
					matchIdx = j;
				}
			}
			if(matchNum == 1) {
				decided[i][matchIdx] = true;
			}
			}else {
				decided[i] = new boolean[bg[0].length];
				for(int j=0; j<bg[0].length;j++) {
					decided[i][j] = false;
					if(bg[i][j] == 1) {
						decided[i][j] = true;
					}
				}
			}
		}
		int edgeNum = bg.length*bg[0].length;
		//A loop for randomly selecting an edge until the bg is a one-to-one mapping
		while(!noMoreEdges(bg)) {
			Vector<Edge> tmp = null;
			String currentDecidedEdges = getCurrentEdges(decided);
			
			if(cache.get(currentDecidedEdges)!=null) {
				tmp = cache.get(currentDecidedEdges);
			}else {
				Vector<Edge> edges = getEdges(src, ref, bg, decided, weights);
				//Sorting the edges according to costs

				tmp = sort(edges);
				cache.put(currentDecidedEdges, tmp);

			}

					//Randomly selecting an edge to be the matching
			for(int i=0;i<tmp.size();i++) {
			//	System.out.println("i="+i+" edg.src="+tmp.get(i).getSrc()+" edg.ref="+tmp.get(i).getRef()+" cost="+tmp.get(i).getMinCost());
				if(oracle(threshold)) {
					System.out.println("before...");
					print(tmp);
					updateBG(tmp.get(i), tmp, bg);
					decided[tmp.get(i).getSrc()][tmp.get(i).getRef()] = true;
					System.out.println("after...");
					print(getEdges(src, ref, bg, decided, weights));
					break;
				}
				if(i==tmp.size()-1) {
					updateBG(tmp.get(i), tmp, bg);
					decided[tmp.get(i).getSrc()][tmp.get(i).getRef()] = true;
				}
			}
		}
		int [] match_id = new int[src.length];
		for(int i=0;i<src.length;i++) {
			match_id[i] = -1;
			for(int j=0;j<ref.length;j++) {
				if(bg[i][j]==1) {
					match_id[i] = j;
				}
			}
			match_id[i] = match_id[i]==-1?ref.length:match_id[i];
		}
		
		long endTime = System.currentTimeMillis();
		long minutes = endTime/60;
		long sec = endTime%60;
	    System.out.println("one iteration time cost£º" + minutes +" min, "+sec+ " seconds");
		return match_id;
		
		
	
	}
	
	/**
	 * Introduce some constraints to limit the mapping
	 * @param src
	 * @param ref
	 * @param bg
	 */
	private static void constrains(Elem[] src, Elem[] ref, int[][] bg) {
		for(int i=0;i<src.length;i++) {
			for(int j=0;j<ref.length;j++) {
				if(src[i].getIsImg()!=ref[j].getIsImg() || src[i].getIsTxt()!=ref[j].getIsTxt() ||
						src[i].getIsInput()!=ref[j].getIsInput()) {
					bg[i][j]=-1;
				}
			}
		}
		
	}
	/**
	 * Flexible tree matching algorithm
	 * @param src
	 * @param ref
	 * @param weight_vis
	 * @param weight_acs
	 * @param weight_sib
	 */
	public static int[][] fxm(Elem[] src, Elem[] ref, Vector<Double> weights, HashMap<String, Vector<Edge>> cache) {
		int N = 100; // total iteration number
//		int N = 1;
		
		double beta = Math.min(src.length, ref.length);
		int iter = 1; // A counter
		// Initialization
		int [][] bg = GraphUtil.getBinaryGraph(src, ref, false);
		//constrains(src, ref, bg);
		// get the first mapping
		int[] match_iter_prev = fxm_one_iteration(src, ref, bg, weights,cache);
		double cost_iter_prev = getCost(src, ref, bg, weights);
		
		//record the minimum cost and corresponding mapping
		double global_min_cost = cost_iter_prev;
		
		// a Boltzmann-like objective function
		cost_iter_prev = Math.exp(-1*cost_iter_prev*beta);
		int[] global_best_match = match_iter_prev;
		
		// a loop to randomly generate N mappings, and find a approximate best mapping
		while(iter<=N) {
			Vector<Edge> edges = getEdges(src, ref, bg, weights);
			Vector<Edge> tmp = sort(edges);
			
			// randomly keep some edges in the next generation
			int kept = (int) (Math.random()*(tmp.size()-1));

			updateBG(kept, tmp, bg); // that's the bug
			// generate a new mapping
			int[] match_iter_next = fxm_one_iteration(src, ref, bg, weights,cache);
			double cost_iter_next = getCost(src, ref, bg, weights);
			boolean global_changed = global_min_cost>cost_iter_next;
			global_best_match = global_changed?match_iter_next:global_best_match;
			global_min_cost = global_changed?cost_iter_next:global_min_cost;
			cost_iter_next = Math.exp((-1)*cost_iter_next*beta);
			
			//System.out.println( "cost_iter_prev: " + cost_iter_prev ); 
			// this value indicates the probability of replacing the previous by the current mapping 
			double replace_prob = cost_iter_next/cost_iter_prev;
			if(oracle(replace_prob)) {
				match_iter_prev = match_iter_next;
				cost_iter_prev = cost_iter_next;
			}
			++iter;
			
		}
		
		// after the loop, we get N mappings, and choose the mapping who has minimum cost to be the approximate best mapping
		System.out.println("min cost="+global_min_cost);
		GraphUtil.update_bg_by_value(bg, 0);
		for(int i=0;i<global_best_match.length;i++) {
			bg[i][global_best_match[i]]=1; 
			src[i].setMid(global_best_match[i]);
		}
		return bg;	
	}
	
	private static double getBanningCost(int [][] bg, double weight_ban) {
		double ban_cost = 0;
		int w = bg.length;
		int h = bg[0].length;
		int ban_num = 0;
		for(int i=0;i<w;i++) {
			boolean matched = false;
			for(int j=0;j<h;j++) {
				if(bg[i][j]==1) {
					matched = true;
					break;
				}
			}
			if(!matched) {
				++ban_num;
			}
		}
		
		for(int i=0;i<h;i++) {
			boolean matched = false;
			for(int j=0;j<w;j++) {
				if(bg[j][i]==1) {
					matched = true;
					break;
				}
			}
			if(!matched) {
				++ban_num;
			}
		}
		return ban_num*weight_ban;
	}
	
	public static double get_violation_paras(Elem e1, Elem e2, Elem[] src, Elem[] ref, int[][] bg,  boolean acs_or_sib) {
		double paras = 0;
		if(acs_or_sib) {
			int v1 = GraphUtil.count_nodes(e1, e2, src, ref, bg, acs_or_sib, false, true);
			int v2 = GraphUtil.count_nodes(e1, e2, src, ref, bg, acs_or_sib, false, false);
			paras = v1+v2;
		}else {
			int i1 = GraphUtil.count_nodes(e1, e2, src, ref, bg, acs_or_sib, true, true);
			int i2 = GraphUtil.count_nodes(e1, e2, src, ref, bg, acs_or_sib, true, false);
			
			int d1 = GraphUtil.count_nodes(e1, e2, src, ref, bg, acs_or_sib, false, true);
			int d2 = GraphUtil.count_nodes(e1, e2, src, ref, bg, acs_or_sib, false, false);
			
			int f1 = GraphUtil.count_f(e1, e2, src, ref, bg, true);
			int f2 = GraphUtil.count_f(e1, e2, src, ref, bg, false);
			
			//System.out.println("e1="+e1.getId()+"e2="+e2.getId()+"f1="+f1+" f2="+f2);
			paras = (double)d1/(i1*f1)+(double)d2/(i2*f2);
		}
		return paras;
	}
	private static Vector<Double> get_visual_paras(Elem[] src, Elem[] ref){
		Vector<Double> visual_paras = new Vector<>();
		
		for(int i=0;i<src.length;i++){
			Elem s = src[i];
			int mid = s.getMid();
			Elem r = ElemUtil.getElemById(ref, mid);
			Vector<String> s_property = Compare.getProperties(s);
			Vector<String> r_property = Compare.getProperties(r);
			Vector<Double> visual_dist = VecUtil.compare_vec(s_property, r_property);
			if(visual_paras.size()==0){
				visual_paras = visual_dist;
			}else{
				visual_paras = VecUtil.add(visual_paras, visual_dist);
			}
		}
		
		return visual_paras;
	}
	
	/**
	 * Return the parameters of a mapping (denoted as binary_graph) between src and ref
	 * @param src
	 * @param ref
	 * @param binary_graph
	 * @return
	 */
	public static Vector<Double> get_mapping_paras(Elem[] src, Elem[] ref, int [][] binary_graph){
		Vector<Double> paras = new Vector<>();
		double acs = 0;
		double sib = 0;
		for(int i=0;i<src.length;i++) {
			Elem s = src[i];
			int ridx = ElemUtil.getElemIdxById(ref, s.getMid());
			Elem r = ref[ridx];
			acs += get_violation_paras(s, r, src, ref, binary_graph, true);
			sib += get_violation_paras(s, r, src, ref, binary_graph, false);
		}
		paras = get_visual_paras(src, ref);
		paras.add(acs);
		paras.add(sib);
		return paras;
	}
	
	/**
	 * Calculate the cost of current mapping in bg
	 * @param src elements in source web page
	 * @param ref elements in reference web page
	 * @param bg the binary graph
	 * @param weights weight vector
	 * @return
	 */
	public static double getCost(Elem[] src, Elem[] ref, int[][] bg, Vector<Double> weights) {
		double cost = 0;
		Vector<Edge> edges = getEdges(src, ref, bg, weights);
		for(int i=0;i<edges.size();i++) {
			cost+=edges.get(i).getMaxCost();
			
			//System.out.println("cost+" + edges.get(i).getMaxCost());
		}
		cost+=getBanningCost(bg, weights.get(weights.size()-1));
		return cost;
	}
	
	private static int countEdge(int[][] bg){
		int egs = 0;
		for(int i=0;i<bg.length-1;i++) {
			for(int j=0;j<bg[i].length-1;j++) {
				if(bg[i][j]==1) {
					++egs;					
				}
			}
		}
		return egs;
	}
	
	private static String getCurrentEdges(boolean [][] edges) {
		String msg = "";
		for(int i=0; i<edges.length;i++) {
			boolean matched = false;
			for(int j=0; j<edges[i].length;j++) {
				if(edges[i][j]) {
					matched = true;
					msg+=j;
					break;
				}
			}
			if(!matched) {
				msg+="-1";
			}
		}
		return msg;	
	}
	
	private static void print(int[][] bg){
		for(int i=0;i<bg.length;i++){
			String msg = i+":  ";
			for(int j=0;j<bg[i].length;j++){
				msg+= bg[i][j]+" ";
			}
			System.out.println(msg);
		}
	}
	private static void print(Vector<Edge> v){
		String msg = "";
		for(int i=0;i<v.size();i++){
			msg+=v.get(i).getSrc()+" "+v.get(i).getRef()+" max:"+v.get(i).getMaxCost()+" min:"+v.get(i).getMinCost()+"\n";
		}
		System.out.println(msg);
	}
}
