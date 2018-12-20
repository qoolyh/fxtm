package fxtm;

import java.awt.print.Printable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import training.FlxmUtil;

public class FXM_v3 {
	private final static double gamma = 0.7;
	private final static int iter_num = 100;


	/**
	 * A comparator for sorting the bounds
	 * 
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
	 * 
	 * @param egs the array to be sorted
	 * @return
	 */
	public static Vector<Edge> sort(Vector<Edge> egs) {
		FXM_v3 ftm = new FXM_v3();

		Edge[] res = new Edge[egs.size()];
		Vector<Edge> tmp = new Vector<Edge>();
		ArrayList<Edge> list = new ArrayList<>();
		for (int i = 0; i < egs.size(); i++) {
			list.add(egs.get(i));
		}

		list.sort(ftm.new CompareEgs());
		for (int i = 0; i < list.size(); i++) {
			tmp.add(list.get(i));
		}
		return tmp;
	}

	public static Vector<Edge> getEdges(Bg[][] bg) {
		Vector<Edge> edges = new Vector<>();
		for (int i = 0; i < bg.length; i++) {
			for (int j = 0; j < bg[i].length; j++) {
				if (i == bg.length - 1 && j == bg[i].length-1) {
					return sort(edges);
				} else {
					if (!bg[i][j].decided) {
						edges.add(bg[i][j].edge);
					}
				}
			}
		}
		Vector<Edge> res = sort(edges);
		return res;
	}

	private static void restore(Bg[][] bg, List<Edge> decidedEdges) {
		for (Edge e : decidedEdges) {
			int src = e.getSrc();
			int ref = e.getRef();
			for (int i = 0; i < bg.length; i++) {
				bg[i][ref].decided = ref != bg[0].length - 1;
			}
			for (int j = 0; j < bg[0].length; j++) {
				bg[src][j].decided = src != bg.length - 1;
			}
			bg[src][ref].decided = true;
			bg[src][ref].connected = true;
		}
	}

	private static void updateBg(Bg[][] bg, Edge decided_edge, Elem[] src, Elem[] ref, Vector<Double> weights,
			double[][] visDis) {	
		int s = decided_edge.getSrc();
		int r = decided_edge.getRef();
		Bg[][] before = Util.matrixClone(bg);
		
		if (s < src.length && r < ref.length) {
			// 0. the bg[i][j] is set to be connnected, other edges connect to i or j will
			// be removed
			for (int i = 0; i < bg.length; i++) {
				bg[i][r].connected = (i == s);
				bg[i][r].decided = true;
			}
			for (int j = 0; j < bg[s].length; j++) {
				bg[s][j].connected = (j == r);
				bg[s][j].decided = true;
			}
			// update the influenced nodes' cost bounds
			// 1. update the parent-parent cost
			int pi = src[s].getPidx();
			int pj = ref[r].getPidx();
			if (pi >= 0 && pj >= 0) {
				if (!bg[pi][pj].decided) {
					--bg[pi][pj].srcCNum;
					--bg[pi][pj].refCNum;
					--bg[pi][pj].edge.src_upper_acs;
					--bg[pi][pj].edge.ref_upper_acs;
//				bg[pi][pj].edge.src_lower_acs = tmp.src_lower_acs-1;
//				bg[pi][pj].edge.ref_lower_acs = tmp.ref_lower_acs-1;
					bg[pi][pj].edge.setCost(visDis[pi][pj], weights);
				}
				// 2. update the parent-non_parent cost
				for (int i = 0; i < bg.length; i++) {
					if (i != pi && !bg[i][pj].decided) {
						--bg[i][pj].refCNum;
//					bg[i][pj].edge.src_upper_acs = toBeUpdate.src_upper_acs+1;
//					bg[i][pj].edge.ref_upper_acs = toBeUpdate.ref_upper_acs+1;
						++bg[i][pj].edge.src_lower_acs;
						++bg[i][pj].edge.ref_lower_acs;
						bg[i][pj].edge.setCost(visDis[i][pj], weights);
					}
				}
				for (int j = 0; j < bg[s].length; j++) {
					if (j != pj && !bg[pi][j].decided) {
						--bg[pi][j].srcCNum;
//					bg[pi][j].edge.src_upper_acs = toBeUpdate.src_upper_acs;
//					bg[pi][j].edge.ref_upper_acs = toBeUpdate.ref_upper_acs;
						++bg[pi][j].edge.src_lower_acs;
						++bg[pi][j].edge.ref_lower_acs;
						bg[pi][j].edge.setCost(visDis[pi][j], weights);
					}
				}
			}
			// 3. update the siblings-siblings costs
			Vector<Integer> sib_i = src[s].getSiblings();
			Vector<Integer> sib_j = ref[r].getSiblings();
			for (int i : sib_i) {
				for (int j : sib_j) {
					if (!bg[i][j].decided) {
						--bg[i][j].srcSibNum;
						--bg[i][j].refSibNum;
						++bg[i][j].edge.src_lower_sibI;
						++bg[i][j].edge.ref_lower_sibI;
						--bg[i][j].edge.src_upper_sibD;
						--bg[i][j].edge.ref_upper_sibD;
						bg[i][j].edge.setCost(visDis[i][j], weights);
					}
				}
			}
			// 4. update sibling-non_sibling costs
			for (int i : sib_i) {
				for (int j = 0; j < bg[i].length; j++) {
					if (!bg[i][j].decided && !sib_j.contains(j)) {
						--bg[i][j].srcSibNum;
						--bg[i][j].edge.src_upper_sibI;
						--bg[i][j].edge.ref_upper_sibI;
						++bg[i][j].edge.src_lower_sibD;
						++bg[i][j].edge.ref_lower_sibD;
						bg[i][j].edge.setCost(visDis[i][j], weights);
					}
				}
			}
			for (int i = 0; i < bg.length; i++) {
				for (int j : sib_j) {
					if (!bg[i][j].decided && !sib_i.contains(i)) {
						--bg[i][j].refSibNum;
						--bg[i][j].edge.src_upper_sibI;
						--bg[i][j].edge.ref_upper_sibI;
						++bg[i][j].edge.src_lower_sibD;
						++bg[i][j].edge.ref_lower_sibD;
						bg[i][j].edge.setCost(visDis[i][j], weights);
					}
				}
			}
		} else {
			if (s == src.length && r!=ref.length) { // that means r is banned
				for (int i = 0; i < bg.length; i++) {
					bg[i][r].connected = (i == s);
					bg[i][r].decided = true;
				}
				int pj = ref[r].getPidx();
				Vector<Integer> sib_j = ref[r].getSiblings();
				if(pj>=0){
					for (int i = 0; i < bg.length; i++) {
						if (!bg[i][pj].decided) {
							--bg[i][pj].refCNum;
							--bg[i][pj].edge.ref_upper_acs;
							bg[i][pj].edge.setCost(visDis[i][pj], weights);
						}
					}
				}
				for (int i = 0; i < bg.length; i++) {
					for (int j : sib_j) {
						if (!bg[i][j].decided) {
							--bg[i][j].refSibNum;
							--bg[i][j].edge.ref_upper_sibI;
							--bg[i][j].edge.ref_upper_sibD;
//							++bg[i][j].edge.ref_lower_sibD;
						}
					}
				}
			} else if(s!=src.length && r==ref.length){ // otherwise, s is banned
				for (int j = 0; j < bg[s].length; j++) {
					bg[s][j].connected = (j == r);
					bg[s][j].decided = true;
				}
				int pi = src[s].getPidx();
				Vector<Integer> sib_i = src[s].getSiblings();
				if(pi>=0){
				for (int j = 0; j < bg[s].length; j++) {
					if (!bg[pi][j].decided) {
						--bg[pi][j].srcCNum;
						--bg[pi][j].edge.src_upper_acs;
						bg[pi][j].edge.setCost(visDis[pi][j], weights);
					}
				}
				}
				for (int i : sib_i) {
					for (int j = 0; j < bg[s].length; j++) {
						if (!bg[i][j].decided) {
							--bg[i][j].srcSibNum;
							--bg[i][j].edge.src_upper_sibI;
							--bg[i][j].edge.src_upper_sibD;
//							++bg[i][j].edge.src_lower_sibD;
						}
					}
				}
			}
		}
		if(!check(bg)){
			System.out.println("update...s="+s+" r="+r+" before...");
			print(toint(before));
		System.out.println("after...");
		print(toint(bg));
		}
	}
	private static boolean check(Bg[][] bg){
		boolean res = true;
		Vector<Integer> row = new Vector<>();
		Vector<Integer> col = new Vector<>();
		for(int i=0;i<bg.length;i++){
			for(int j=0;j<bg[i].length;j++){
				if(bg[i][j].connected){
					if(!row.contains(i) && !col.contains(j)){
						row.add(i);
						col.add(j);
					}else{
						if(i==bg.length-1){
							if(col.contains(j)){
								return false;
							}else{
								col.add(j);
							}
						}else if(j==bg[0].length-1){
							if(row.contains(i)){
								return false;
							}else{
								row.add(i);
							}
						}
					}
				}
			}
		}
		return res;
	}
	private static boolean noMoreEdges(Bg[][] bg) {
		boolean noEdges = true;
		for(int i=0;i<bg.length-1;i++){
//			boolean connect = false;
			for(int j=0;j<bg[0].length;j++){
				if(!bg[i][j].decided){
					if(i!=bg.length-1 || j!= bg[0].length-1){
						return false;
					}
				}
			}
//			if(!connect){
//				noEdges = false;
//				break;
//			}
		}
//		if (bg.length < bg[0].length) {
//			for (int i = 0; i < bg.length; i++) {
//				if (!bg[i][i].decided) {
//					noEdges = false;
//					break;
//				}
//			}
//		} else {
//			for (int i = 0; i < bg[0].length; i++) {
//				if (!bg[i][i].decided) {
//					noEdges = false;
//					break;
//				}
//			}
//		}
		return noEdges;
	}

	private static boolean oracle(double threshold) {
		boolean seletected = false;
		double value = Math.random();
		if (value <= threshold) {
			seletected = true;
		}
		return seletected;
	}

	private static Edge chooseEdge(Bg[][] bg) {
		Edge res = null;
		Vector<Edge> edges = getEdges(bg);
		for (int i=0;i<edges.size();i++) {
			if (oracle(gamma)) {
				res = edges.get(i);
				break;
			}else if(i==edges.size()-1){
				res = edges.get(i);
			}
		}
		
		return res;
	}

	/**
	 * do fxm on bg, return a set of decided edges(i.e. the mapping).
	 * 
	 * @param bg
	 * @param src
	 * @param ref
	 * @param weights
	 * @param visDis
	 * @return
	 */
	private static Vector<Edge> fxm_one_iter(Bg[][] bg, Elem[] src, Elem[] ref, Vector<Double> weights, double[][] visDis) {
		// initialize, get current edge cost
		// case 1. bg is a full bigraph
		// case 2. bg is a semi-bigraph, some edges are already decided
		initBg(bg, src, ref, weights, visDis);
		Vector<Edge> edges = new Vector<>();
		// now start to sort the edges and randomly choose one, update the bg, loop to
		// the previous, until no more edge found in bg.
		while (!noMoreEdges(bg)) {
			Edge edge = chooseEdge(bg);
			edges.add(edge);
			updateBg(bg, edge, src, ref, weights, visDis);
		}
		//this edge is not enough cause you've retained some edges in the previous iteration
		return edges;
	}

	public static int countGroups(Elem[] e, Vector<Integer> v) {
		int groups = 0;
		Vector<Integer> pids = new Vector<>();
		for (int i : v) {
			int tmp = e[i].getPid();
			if (!pids.contains(tmp)) {
				pids.add(tmp);
			}
		}
		groups = pids.size();
		return groups;
	}
	

	private static double getCost(Elem[] src, Elem[] ref, Bg[][] bg, double[][] vis, Vector<Double> weights) {
		double cost = 0;
		double weight_acs = weights.get(weights.size() - 3), weight_sib = weights.get(weights.size() - 2),
				weight_ban = weights.get(weights.size() - 1);
		for (int i = 0; i < bg.length; i++) {
			for (int j = 0; j < bg[0].length; j++) {
				if (bg[i][j].connected) {
					double ca = 0, cs = 0, cv = vis[i][j];
					if (i < src.length && j < ref.length) {
						Vector<Integer> src_children = src[i].getChildren();
						Vector<Integer> ref_children = ref[j].getChildren();
						Vector<Integer> src_sib = src[i].getSiblings();
						Vector<Integer> ref_sib = ref[j].getSiblings();
						if (src_children.size() == 0 || ref_children.size() == 0) {
							ca = (src_children.size() + ref_children.size()) * weight_acs;
						} else {
							int va_s = 0, va_r = 0;
							va_s = countNodes(bg, src_children, ref_children, true);
							va_r = countNodes(bg, ref_children, src_children, true, true);
							ca = (va_s + va_r) * weight_acs; //todo
						}
//						if (src_sib.size() == 0 || ref_sib.size() == 0) {
//							cs = (src_sib.size() + ref_sib.size()) * weight_sib;
//						} else {
							int vd_s = 0, vd_r = 0, vi_s = 0, vi_r = 0, f_s = 0, f_r = 0;
							vd_s = countNodes(bg, src_sib, ref_sib, true);
							vd_r = countNodes(bg, ref_sib, src_sib, true, true);
							vi_s = countNodes(bg, src_sib, ref_sib, false);
							vi_r = countNodes(bg, ref_sib, src_sib, false, true);
							f_s = countGroups(src, src_sib);
							f_r = countGroups(ref, ref_sib);
							vi_s = vi_s == 0 ? 1 : vi_s;
							vi_r = vi_r == 0 ? 1 : vi_r;
							f_s = f_s == 0 ? 1 : f_s;
							f_r = f_r == 0 ? 1 : f_r;
							cs = (vd_s / (vi_s * f_s) + vd_r / (vi_r * f_r)) * weight_sib; //todo
//						}
					}
//					else{
//						if(i==src.length){							
//							Vector<Integer> ref_children = ref[j].getChildren();
//							Vector<Integer> ref_sib = ref[j].getSiblings();
//						}else{
//							Vector<Integer> src_children = src[i].getChildren();
//							Vector<Integer> src_sib = src[i].getSiblings();
//						}
//					}
					cost += ca + cs + cv;
				}
			}
		}
		return cost;
	}

	public static int[][] fxm(Elem[] src, Elem[] ref, Vector<Double> weights) {
		int counter = 0;
		Bg[][] bg = new Bg[src.length + 1][];
		for (int i = 0; i < bg.length; i++) {
			bg[i] = new Bg[ref.length + 1];
			for (int j = 0; j < bg[i].length; j++) {
				bg[i][j] = null;
			}
		}
		double beta = (double)1/(src.length * ref.length);
		double[][] visDis = visualCompare(bg, src, ref, weights);
		initBg(bg, src, ref, weights, visDis);
//		Bg[][] bg_ori = bg.clone(); // that's the bug
		Bg[][] bg_ori = Util.matrixClone(bg);
		Vector<Edge> edges = fxm_one_iter(bg, src, ref, weights, visDis);
		double cost_iter_prev = getCost(src, ref, bg, visDis, weights);
		double global_min_cost = cost_iter_prev;
		Bg[][] finalBg= bg;
		while (counter < iter_num) {
			int kept = (int) (Math.random() * (edges.size() - 1));
			Bg[][] tmp = Util.matrixClone(bg_ori);
			restore(tmp, edges.subList(0, kept)); //pass
//			initBg(tmp, src, ref, weights, visDis);
			fxm_one_iter(tmp, src, ref, weights, visDis);			
			double cost_iter_next = getCost(src, ref, tmp, visDis, weights);
			boolean global_changed = global_min_cost >= cost_iter_next;
			global_min_cost = global_changed ? cost_iter_next : global_min_cost;
			double replace_prob = Math.exp((cost_iter_prev-cost_iter_next)*beta);
			if (oracle(replace_prob)) {
				edges = getEdges(tmp);
				cost_iter_prev = cost_iter_next;
				finalBg = tmp;
			}
//			edges = getEdges(finalBg);
			++counter;
		}
		int [][] res = toint(finalBg);
//		int[][] res = Util.getMatrix(src.length + 1, ref.length + 1);
//		for (Edge e : global_best_match) {
//			int s = e.getSrc(), r = e.getRef();
//			if (s != src.length) {
//				if(r!=ref.length) {
//					src[s].setMid(ref[r].getId());
//				}else {
//					src[s].setMid(-1);
//				}
//				
//			}
//			res[s][r] = 1;
//		}
//		print(finalBg);
		return res;
	}

	/**
	 * A function to calculate the visual difference (i.e. visual cost) between two
	 * elements
	 * 
	 * @param e1         the src element
	 * @param e2         the ref element
	 * @param weight_vis the weight of each dimension considered in the comparison
	 * @return
	 */
	private static double getVisualCost(Elem e1, Elem e2, List<Double> weight_vis) {
		double v_cost = 0;
		v_cost = e1.getId() == e2.getId() ? 0 : 1;
		Vector<Double> vis_distance = Compare.elemComp(e1, e2); // here you should try different kinds of vis_distance
																// (e.g. delta(a,b), sigmoid(a,b), etc.)
		return VecUtil.dotProduct(weight_vis, vis_distance);
	}

	/**
	 * return the visual distances of any two elements in src and ref.
	 * 
	 * @param src     the src element
	 * @param ref     the ref element
	 * @param weights the weight of each dimension considered in the matching
	 * @return
	 */
	private static double[][] visualCompare(Bg[][] bg, Elem[] src, Elem[] ref, Vector<Double> weights) {
			List<Double> weight_vis = weights.subList(0, weights.size() - 3);
			double ban_cost = weights.lastElement();
			double[][] visDis = new double[bg.length][];
			for (int i = 0; i < bg.length; i++) {
			visDis[i] = new double[bg[0].length];
			for (int j = 0; j < bg[i].length; j++) {
				if (i < src.length && j < ref.length) {
					visDis[i][j] = getVisualCost(src[i], ref[j], weight_vis);
				} else {
					visDis[i][j] = i == src.length && j == ref.length ? 0 : ban_cost; //todo, left!=right
				}
			}
		}
		return visDis;
	}

	/**
	 * calculate the nodes in ci which do or don't connect to the nodes in cj
	 * 
	 * @param bg
	 * @param ci
	 * @param cj
	 * @param calViolation a boolean value that tells the functions to count
	 *                     violated nodes or not
	 * @return
	 */
	public static int countNodes(Bg[][] bg, Vector<Integer> ci, Vector<Integer> cj, boolean calViolation,
			boolean... reversed) {
		int v_num = 0, c_num = 0;
		if (reversed.length == 0) {
			for (int i = 0; i < ci.size(); i++) {
					boolean connect = false;
					for (int j = 0; j < cj.size(); j++) {
						if (bg[ci.get(i)][cj.get(j)].connected) {
							connect = true;
							++c_num;
							break;
						}
					}
					if (!connect) {
						++v_num;
					}
				
			}
		} else {
			for (int i = 0; i < cj.size(); i++) {
				boolean connect = false;
				for (int j = 0; j< ci.size(); j++) {
					if (bg[cj.get(i)][ci.get(j)].connected) {
						connect = true;
						++c_num;
						break;
					}
				}
				if (!connect) {
					++v_num;
				}
			}

		}
		int res = calViolation ? v_num : c_num;
		return res;
	}

	/**
	 * return the decided nodes in v
	 * 
	 * @param v
	 * @param bg
	 * @return
	 */
	private static Vector<Integer> getCandidates(Vector<Integer> v, Bg[][] bg, boolean isSrc) {
		Vector<Integer> candidates = new Vector<>();
		for (int i : v) {
			if (isSrc ? bg[i][bg[i].length - 1].decided : bg[bg.length - 1][i].decided) {
				candidates.add(i);
			}
		}
		return candidates;
	}

	/**
	 * this function will initialize the bigraph bg in the first iteration of fxm
	 * 
	 * @param bg      the bigraph
	 * @param src
	 * @param ref
	 * @param weights
	 * @param visDis  the visual distance matrix of two element sets
	 */
	private static void initBg(Bg[][] bg, Elem[] src, Elem[] ref, Vector<Double> weights, double[][] visDis) {
		double weight_acs = weights.get(weights.size() - 3), weight_sib = weights.get(weights.size() - 2),
				weight_ban = weights.get(weights.size() - 1);
		for (int i = 0; i < bg.length; i++)
			for (int j = 0; j < bg[i].length; j++) {
				if (bg[i][j] == null) { // that indicates the bg is a full bigraph
					// initialize the upper and lower bounds of ancestry cost;
					if (i != src.length && j != ref.length) {
						int ua_ij = src[i].getChildren().size(),
//						la_ij = Math.max(src[i].getChildren().size()-ref[j].getChildren().size(), 0),
								la_ij = 0, ua_ji = ref[j].getChildren().size(),
//					    la_ji = Math.max(ref[j].getChildren().size()-src[i].getChildren().size(), 0);
								la_ji = 0;
						double upper_acs = (double) weight_acs * (ua_ij + ua_ji),
								lower_acs = (double) weight_acs * (la_ij + la_ji);

						// initialize the upper and lower bounds of sibling cost;
						// 1. the bounds of D(e)
						int ud_ij = src[i].getSiblings().size() + 1,
//						ld_ij = Math.max(src[i].getSiblings().size()-ref[j].getSiblings().size(), 0),
								ld_ij = 0, ud_ji = ref[j].getSiblings().size() + 1,
//						ld_ji = Math.max(ref[j].getSiblings().size()-src[i].getSiblings().size(), 0);
								ld_ji = 0;
						// 2. the bounds of I(e)
//					int ui_ij = Math.min(src[i].getSiblings().size(), ref[j].getSiblings().size()),
						int ui_ij = src[i].getSiblings().size() + 1, li_ij = 1,
//						ui_ji = Math.min(src[i].getSiblings().size(), ref[j].getSiblings().size()),
								ui_ji = ref[j].getSiblings().size() + 1, li_ji = 1;
						double upper_sib = weight_sib / 2 * ((double) ud_ij / li_ij + (double) ud_ji / li_ji),
								lower_sib = weight_sib * ((double) ld_ij / ((ui_ij * (ld_ij + 1)))
										+ (double) ld_ji / ((ui_ji * (ld_ji + 1))));

						double upper = upper_acs + upper_sib + visDis[i][j];
						double lower = lower_acs + lower_sib + visDis[i][j];
						bg[i][j] = new Bg();
						Edge eij = new Edge(i, j, lower, upper,src[i],ref[j]);
						eij.setBounds(ud_ij, ui_ij, ua_ij, ud_ji, ui_ji, ua_ji, ld_ij, li_ij, la_ij, ld_ji, li_ji,
								la_ji);
						bg[i][j].edge = eij;
						bg[i][j].srcCNum = src[i].getChildren().size();
						bg[i][j].refCNum = ref[j].getChildren().size();
						bg[i][j].srcSibNum = src[i].getSiblings().size();
						bg[i][j].refSibNum = ref[j].getSiblings().size();
					} else {
						int ua_ij = 0, ua_ji = 0, la_ij = 0, la_ji = 0, ud_ij = 0, ud_ji = 0, ld_ij = 0, ld_ji = 0,
								ui_ij = 1, ui_ji = 1, li_ij = 1, li_ji = 1;
						bg[i][j] = new Bg();
						if (ref.length == j && src.length != i) {
							ua_ij = src[i].getChildren().size() - 1;
							ud_ij = src[i].getSiblings().size() - 1;
							ld_ij = 1;
							ui_ij = src[i].getSiblings().size() - 1;
							li_ij = 1;
							bg[i][j].srcCNum = src[i].getChildren().size();
							bg[i][j].refCNum = 0;
							bg[i][j].srcSibNum = src[i].getSiblings().size();
							bg[i][j].refSibNum = 0;
						} else if (src.length == i && ref.length != j) {
							ua_ji = ref[j].getChildren().size() - 1;
							ud_ji = ref[j].getSiblings().size() - 1;
							ld_ji = 1;
							ui_ji = ref[j].getSiblings().size() - 1;
							li_ji = 1;
							bg[i][j].srcCNum = 0;
							bg[i][j].refCNum = ref[j].getChildren().size();
							bg[i][j].srcSibNum = 0;
							bg[i][j].refSibNum = ref[j].getSiblings().size();
						}
						double upper_acs = (double) weight_acs * (ua_ij + ua_ji),
								lower_acs = (double) weight_acs * (la_ij + la_ji);
						double upper_sib = weight_sib / 2 * ((double) ud_ij / li_ij + (double) ud_ji / li_ji),
								lower_sib = weight_sib * ((double) ld_ij / ((ui_ij * (ld_ij + 1)))
										+ (double) ld_ji / ((ui_ji * (ld_ji + 1))));

						double upper = upper_acs + upper_sib + visDis[i][j];
						double lower = lower_acs + lower_sib + visDis[i][j];
						Edge eij = new Edge(i, j, lower, upper,src[i],ref[j]);
						eij.setBounds(ud_ij, ui_ij, ua_ij, ud_ji, ui_ji, ua_ji, ld_ij, li_ij, la_ij, ld_ji, li_ji,
								la_ji);
						bg[i][j].edge = eij;
					}
				} else if (!bg[i][j].decided) { // this indicates the bigraph has been initialized

					if (i != src.length && j != ref.length) {
						Vector<Integer> ci = src[i].getChildren();
						Vector<Integer> si = src[i].getSiblings();
						Vector<Integer> cj = ref[j].getChildren();
						Vector<Integer> sj = ref[j].getSiblings();

						Vector<Integer> candidates_ci = getCandidates(ci, bg, true);
						Vector<Integer> candidates_si = getCandidates(si, bg, true);
						Vector<Integer> candidates_cj = getCandidates(cj, bg, false);
						Vector<Integer> candidates_sj = getCandidates(sj, bg, false);

						int va = countNodes(bg, candidates_ci, candidates_cj, true);
						int vd = countNodes(bg, candidates_si, candidates_sj, true);
						int vi = countNodes(bg, candidates_si, candidates_sj, false);
						// initialize the upper and lower bounds of ancestry cost;
						int ua_ij = src[i].getChildren().size() - candidates_ci.size() + va,
//						la_ij = Math.max(src[i].getChildren().size()-candidates_ci.size()-ref[j].getChildren().size()+candidates_cj.size(), 0),
								la_ij = va, ua_ji = ref[j].getChildren().size() - candidates_cj.size() + va,
//					    la_ji = Math.max(ref[j].getChildren().size()-candidates_cj.size()-src[i].getChildren().size()+candidates_ci.size(), 0);
								la_ji = va;
						double upper_acs = (double) weight_acs * (ua_ij + ua_ji),
								lower_acs = (double) weight_acs * (la_ij + la_ji);

						// initialize the upper and lower bounds of sibling cost;
						// 1. the bounds of D(e)
						int ud_ij = src[i].getSiblings().size() + 1 - candidates_si.size() + vd,
//							ld_ij = Math.max(src[i].getSiblings().size() - candidates_si.size()
//									- ref[j].getSiblings().size() + candidates_sj.size(), 0) + vd,
								ld_ij = vd, ud_ji = ref[j].getSiblings().size() + 1 - candidates_sj.size() + vd,
//							ld_ji = Math.max(ref[j].getSiblings().size() - candidates_si.size()
//									- src[i].getSiblings().size() + candidates_sj.size(), 0) + vd;
								ld_ji = vd;
						// 2. the bounds of I(e)
//					int ui_ij = Math.min(src[i].getSiblings().size() - candidates_si.size(),
//							src[j].getSiblings().size() - candidates_sj.size()) + vi, 
						int ui_ij = src[i].getSiblings().size() + 1 - candidates_si.size() + vi, li_ij = 1 + vi,
//							ui_ji = Math.min(src[i].getSiblings().size() - candidates_si.size(),
//									src[j].getSiblings().size() - candidates_sj.size()) + vi,
								ui_ji = src[i].getSiblings().size() + 1 - candidates_si.size() + vi, li_ji = 1 + vi;
						double upper_sib = weight_sib / 2 * ((double) ud_ij / li_ij + (double) ud_ji / li_ji),
								lower_sib = weight_sib * ((double) ld_ij / ((ui_ij * (ld_ij + 1)))
										+ (double) ld_ji / ((ui_ji * (ld_ji + 1))));

						double upper = upper_acs + upper_sib + visDis[i][j];
						double lower = lower_acs + lower_sib + visDis[i][j];
						Edge eij = new Edge(i, j, lower, upper);
						bg[i][j].edge = eij;
						eij.setBounds(ud_ij, ui_ij, ua_ij, ud_ji, ui_ji, ua_ji, ld_ij, li_ij, la_ij, ld_ji, li_ji,
								la_ji);
						bg[i][j].srcCNum = src[i].getChildren().size() - candidates_ci.size();
						bg[i][j].refCNum = ref[j].getChildren().size() - candidates_cj.size();
						bg[i][j].srcSibNum = src[i].getSiblings().size() - candidates_si.size();
						bg[i][j].refSibNum = ref[j].getSiblings().size() - candidates_sj.size();
					} else {
						int ua_ij = 0, ua_ji = 0, la_ij = 0, la_ji = 0, ud_ij = 0, ud_ji = 0, ld_ij = 0, ld_ji = 0,
								ui_ij = 1, ui_ji = 1, li_ij = 1, li_ji = 1;
						Vector<Integer> candidates_ci = new Vector<>();
						Vector<Integer> candidates_si = new Vector<>();
						Vector<Integer> candidates_cj = new Vector<>();
						Vector<Integer> candidates_sj = new Vector<>();
						if (ref.length == j && src.length != i) {
							Vector<Integer> ci = src[i].getChildren();
							Vector<Integer> si = src[i].getSiblings();

							 candidates_ci = getCandidates(ci, bg, true);
							 candidates_si = getCandidates(si, bg, true);

							ua_ij = src[i].getChildren().size() - candidates_ci.size();
							ud_ij = src[i].getSiblings().size() - candidates_si.size();
							ui_ij = src[i].getSiblings().size() - candidates_si.size() + 1;
							li_ij = 1 + candidates_si.size();
						} else if (src.length == i && ref.length != j) {
							Vector<Integer> cj = ref[j].getChildren();
							Vector<Integer> sj = ref[j].getSiblings();

							 candidates_cj = getCandidates(cj, bg, false);
							candidates_sj = getCandidates(sj, bg, false);

							ua_ji = ref[j].getChildren().size() - candidates_cj.size();
							ud_ji = ref[j].getSiblings().size() - candidates_sj.size();
							ui_ji = ref[j].getSiblings().size() - candidates_sj.size() + 1;
							li_ji = 1 + candidates_sj.size();
						}
						double upper_acs = (double) weight_acs * (ua_ij + ua_ji),
								lower_acs = (double) weight_acs * (la_ij + la_ji);
						double upper_sib = weight_sib / 2 * ((double) ud_ij / li_ij + (double) ud_ji / li_ji),
								lower_sib = weight_sib * ((double) ld_ij / ((ui_ij * (ld_ij + 1)))
										+ (double) ld_ji / ((ui_ji * (ld_ji + 1))));
						double upper = upper_acs + upper_sib + visDis[i][j];
						double lower = lower_acs + lower_sib + visDis[i][j];
						bg[i][j] = new Bg();
						Edge eij = new Edge(i, j, lower, upper);
						eij.setBounds(ud_ij, ui_ij, ua_ij, ud_ji, ui_ji, ua_ji, ld_ij, li_ij, la_ij, ld_ji, li_ji,
								la_ji);
						bg[i][j].edge = eij;
						bg[i][j].srcCNum = i!=src.length?src[i].getChildren().size()- candidates_ci.size():0;
						bg[i][j].refCNum = j!=ref.length?ref[j].getChildren().size()-candidates_cj.size():0;
						bg[i][j].srcSibNum = i!=src.length?src[i].getSiblings().size()-candidates_si.size():0;
						bg[i][j].refSibNum = j!=ref.length?ref[j].getSiblings().size()-candidates_sj.size():0;
					}
				}
			}
	}

	private static void print(Bg[][] bg, Elem[] src, Elem[] ref, Object... onlyOne) {
		for (int i = 0; i < bg.length; i++) {
			for (int j = 0; j < bg[i].length; j++) {
				Edge edge = bg[i][j].edge;
				Vector<Integer> info = new Vector<>();
				info.add(edge.ref_lower_acs);
				info.add(edge.ref_lower_sibD);
				info.add(edge.ref_lower_sibI);
				info.add(edge.ref_upper_acs);
				info.add(edge.ref_upper_sibD);
				info.add(edge.ref_upper_sibI);

				info.add(edge.src_lower_acs);
				info.add(edge.src_lower_sibD);
				info.add(edge.src_lower_sibI);
				info.add(edge.src_upper_acs);
				info.add(edge.src_upper_sibD);
				info.add(edge.src_upper_sibI);

				String[] name = { "edge.ref_lower_acs", "edge.ref_lower_sibD", "edge.ref_lower_sibI",
						"edge.ref_upper_acs", "edge.ref_upper_sibD", "edge.ref_upper_sibI",

						"edge.src_lower_acs", "edge.src_lower_sibD", "edge.src_lower_sibI", "edge.src_upper_acs",
						"edge.src_upper_sibD", "edge.src_upper_sibI" };
				if (i < src.length && j < ref.length && !bg[i][j].decided) {
					System.out.println("bg(" + src[i].getId() + "," + ref[j].getId() + ")" + i + " " + j);
					System.out.println(src[i].getChildren().size() + " " + ref[j].getChildren().size());
					System.out.println(src[i].getSiblings().size() + " " + ref[j].getSiblings().size());
					if (onlyOne != null) {
						for (int k = 0; k < info.size(); k++) {
							System.out.println("bg(" + src[i].getId() + "," + ref[j].getId() + ")" + name[k] + " me:"
									+ info.get(k));

						}
					} else {
						for (int k = 0; k < info.size(); k++) {
							if (i < src.length && j < ref.length)
								if (src[i].getId() == 52 && ref[j].getId() == 130) {
									System.out.println("bg(" + src[i].getId() + "," + ref[j].getId() + ")" + name[k]
											+ " me:" + info.get(k));
									System.out.println(src[i].getChildren().size() + " " + ref[j].getChildren().size());
									System.out.println(src[i].getSiblings().size() + " " + ref[j].getSiblings().size());
								}
						}
					}
				}
			}
		}
	}
	private static int[][] toint(Bg[][] bg){
		int[][] res = new int[bg.length][];
		for(int i=0;i<bg.length;i++){
			res[i] = new int[bg[0].length];
			for(int j=0;j<bg[0].length;j++){
				res[i][j] = bg[i][j].connected?1:0;
			}
		}
		return res;
	}
	private static void print(Bg[][] bg) {
		System.out.println("----------------");
		for(int i=0;i<bg.length;i++){
			String msg = "";
			for(int j=0;j<bg[0].length;j++){
				msg+= bg[i][j].connected?"1"+" ":"0"+" ";
			}
			System.out.println(msg);
		}	
	}
	private static void print(int[][] bg) {
		System.out.println("===================");
		for(int i=0;i<bg.length;i++){
			String msg = "";
			for(int j=0;j<bg[0].length;j++){
				msg+= bg[i][j]+" ";
			}
			System.out.println(msg);
		}	
	}
	private static void print(double[][] bg) {
		for(int i=0;i<bg.length;i++){
			String msg = "";
			for(int j=0;j<bg[0].length;j++){
				msg+= String.format("%.2f", bg[i][j])+" ";
			}
			System.out.println(msg);
		}	
	}
	
	private static void update(Elem[] src, Elem[] ref, int[][] method_bg){
		int [] match = new int[src.length];
		for(int i=0;i<src.length;i++) {
			for(int j=0;j<method_bg[0].length;j++) {
				if(method_bg[i][j]==1) {
					//match[i] = j==ref.length?0:ref[j].getId();
					match[i] = j==ref.length?0:ref[j].getCid();
				}
			}
		}
		String pre = "tar1_ref2";
		String update = "update "+pre+" set matchID = 0, matchCls=0";
		Util.update(update);

		
		for(int i=0;i<match.length;i++) {
			String sql = "update "+pre+" set matchCls = "+match[i]+" where ID="+src[i].getId();
			Util.update(sql);
		}
	}
	private static void edgeInfo(int [][] bg){
		String msg="";
		for(int i=0;i<bg.length;i++){
			for(int j=0;j<bg[0].length;j++){
				if(bg[i][j]==1){
					msg+="("+i+","+j+")"+",";
				}	
			}
		}
		System.out.println(msg);
	}
	public static void main(String[] args) throws IOException, SQLException {
		Connection conn = DBConnect.getConnect();
		String srcImg = "img/tar1.png";
		String refImg = "img/ref2.png";
		Elem[] src = (Elem[]) Util.getElems(srcImg, "select * from tar1_ref2 where isZero=0", conn)
				.toArray(new Elem[1]);
		ElemUtil.init(src);
		Elem[] ref = (Elem[]) Util.getElems(refImg, "select * from ref2 where isZero=0", conn).toArray(new Elem[1]);
		ElemUtil.init(ref);
		Vector<Double> weights = VecUtil.getVec(Compare.getProperties(src[0]).size() + 3);
		VecUtil.eval(weights, 1);
		weights.setElementAt(10.0, weights.size() - 3);
		weights.setElementAt(10.0, weights.size() - 2);
		weights.setElementAt(1000.0, weights.size() - 1);
		Bg[][] bg = new Bg[src.length + 1][];
		for (int i = 0; i < bg.length; i++) {
			bg[i] = new Bg[ref.length + 1];
			for (int j = 0; j < bg[i].length; j++) {
				bg[i][j] = null;
			}
		}
		double[][] visDis = visualCompare(bg, src, ref, weights);
		initBg(bg, src, ref, weights, visDis);
		
		System.out.println(src.length+" "+ref.length);
		
		
//		Vector<Edge> res = fxm_one_iter(bg, src, ref, weights, visDis);
		int [][] res = fxm(src, ref, weights);
		print(res);
		edgeInfo(res);
		Vector<Double> paras = FlxmUtil.calViolations(src, ref, res);
		String msg = "";
		for(double i:paras){
			msg+=i+" ";
		}
		System.out.println(noMoreEdges(bg));
		System.out.println(msg);

		update(src, ref, res);
		
//		System.out.println(noMoreEdges(bg) +" "+ srcImg.length()+" "+ref.length+" "+res.size());
//		for(Edge e:res){
//			System.out.println("edge: src="+e.getSrc()+" ref="+e.getRef()+" "+e.getMinCost()+" "+e.getMaxCost());
//		}
	}
}
