package fxtm;

import java.util.Vector;

public class GraphUtil {
	
	/**
	 * 
	 * @param e1 source tree of elements
	 * @param e2 reference tree of elements
	 * @param getManualRes a boolean value indicates whether you want to get manually matching results (if so, the binary graph only considers the matching edges in G)
	 * @return the complete binary graph between e1 and e2
	 */ 
	public static int[][] getBinaryGraph(Elem[] e1, Elem[] e2, boolean getManualRes){
		int[][] bg = new int[e1.length+1][];
		boolean[] used = new boolean[e2.length];
		for(int i=0;i<used.length;i++) {
			used[i] = false;
		}
		for(int i=0;i<bg.length;i++) {
			bg[i] = new int[e2.length+1];
			if(i==e1.length){
				for(int j=0;j<e2.length;j++){
					bg[i][j] = used[j]?0:1;
				}
			}else{
			
			int matchid = e1[i].getMid();
			//int matchid = e1[i].getMcls();
			//System.out.println("i: " + i + " matchid: " + matchid);
			
			for(int j=0;j<e2.length+1;j++) {
				if(getManualRes) {
					if(matchid ==0){
						bg[i][j]=j==e2.length?1:0;
					}else{
						if(j<e2.length){
							if(matchid == e2[j].getId()){
								bg[i][j] = 1;
								used[j] = true;
							}
							else{
								bg[i][j] = 0;
							}
						}
					}
					
				}else {
					//System.out.println(i + " " + j);
					bg[i][j] = 1;
				}	
			}
			}
		}
		return bg;
	}
	
	
	public static void update_bg_by_value(int[][] bg, int value) {
		for(int i=0;i<bg.length;i++) {
			for(int j=0;j<bg[i].length;j++) {
				bg[i][j] = bg[i][j]!=-1?value:-1;
			}
		}
	}
	/**
	 * A function to count the existing or missing edge between the children or siblings of e1 and e2 in the binary graph
	 * @param bg the binary graph between src and ref
	 * @param e1 current source element
	 * @param e2 the matching element in reference
	 * @param src 
	 * @param ref
	 * @param ansOrSib a boolean value to decide what kind of elements' edge you want to count (ancestor? or sibling)
	 * @param findConnect a boolean value to decide what kind of edge you want to count (the existing edge between elements? or the disconnect edge) 
	 * @return
	 */
	public static int countValidEdge(int[][] bg, Elem e1, Elem e2, Elem[] src, Elem[] ref, boolean ansOrSib, boolean findConnect, boolean e1_or_e2) {
		int edge = 0;
		Vector<Integer> src_idx = new Vector<Integer>();
		Vector<Integer> ref_idx = new Vector<Integer>();
		if(ansOrSib) {
			src_idx = ElemUtil.getChildrenIdx(e1, src);
			ref_idx = ElemUtil.getChildrenIdx(e2, ref);
		}
		else {
			src_idx = ElemUtil.getSibIdx(e1, src);
			ref_idx = ElemUtil.getSibIdx(e2, ref);
		}
		if(e1_or_e2) {
			if(findConnect) {
				for(int i=0; i<src_idx.size();i++) {
					for(int j=0; j< ref_idx.size();j++) {
						if(bg[src_idx.get(i)][ref_idx.get(j)] == 1) {
							++edge;
							break;
						}
					}
				}
			}else {		
				for(int i=0; i<src_idx.size();i++) {
					for(int j=0; j< ref_idx.size();j++) {
						if(bg[src_idx.get(i)][ref_idx.get(j)] == 0) ++edge;
					}
				}
			}
		}
		return edge;
	}
	
	/**
	 * A function to count the existing or missing edge between the violation edges between the children or sibling or e1 and e2<br>
	 * e.g. e_m is the child of e1, if e_m has an edge connect to an element which is not a child of e2, then this edge is a violating edge.
	 * @param bg the binary graph between src and ref
	 * @param e1 current source element
	 * @param e2 the matching element in reference
	 * @param src
	 * @param ref
	 * @param ansOrSib a boolean value to decide what kind of elements' edge you want to count (ancestor? or sibling)
	 * @param findConnect a boolean value to decide what kind of violating-edge you want to count (the existing edge between elements? or the disconnect edge)
	 * @return
	 */
	public static int countViolateEdge(int[][] bg, Elem e1, Elem e2, Elem[] src, Elem[] ref, boolean ansOrSib, boolean findConnect, boolean e1_or_e2) {
		int edge = 0;
		Vector<Integer> src_idx = new Vector<Integer>();
		Vector<Integer> ref_idx = new Vector<Integer>();
		if(ansOrSib) {
			src_idx = ElemUtil.getChildrenIdx(e1, src);
			ref_idx = ElemUtil.getChildrenIdx(e2, ref);
		}
		else {
			src_idx = ElemUtil.getSibIdx(e1, src);
			ref_idx = ElemUtil.getSibIdx(e2, ref);
		}
		if(e1_or_e2) {
			if(findConnect) {
				for(int i=0; i<src_idx.size();i++) {
					for(int j=0; j<bg[i].length;j++) {
						if(bg[src_idx.get(i)][j] == 1 && !ref_idx.contains(j)) ++edge;
					}
				}
			}else {		
				for(int i=0; i<src_idx.size();i++) {
					for(int j=0; j<bg[i].length;j++) {
						if(bg[src_idx.get(i)][j] == 0  && !ref_idx.contains(j) ) ++edge;
					}
				}
			}
		}
		else {
			if(findConnect) {
				for(int i=0; i<bg.length;i++) {
					for(int j=0; j<ref_idx.size();j++) {
						if(bg[i][ref_idx.get(j)] == 1 && !src_idx.contains(i)) ++edge;
					}
				}
			}else {		
				for(int i=0; i<src_idx.size();i++) {
					for(int j=0; j<bg[i].length;j++) {
						if(bg[i][ref_idx.get(j)] == 0  && !src_idx.contains(i) ) ++edge;
					}
				}
			}
		}
		return edge;
	}
	/**
	 * Returns the number of nodes which may map to the non-children of p2 if p1 is mapping to p2. 
	 * @param p1 the src element
	 * @param p2 the mapping element in ref
	 * @param src 
	 * @param ref
	 * @param bg the binary graph between src and ref
	 * @param acs_or_sib a boolean value for choosing which node you wanna get. (ancestor or sibling)
	 * @param e1_or_e2 a boolean value for choosing whose node you wanna get (p1's children or p2's children)
	 * @param potential_or_unavoidable a boolean value for choosing the kind of violate nodes (is this element has chance to map to a non-children or it has no choice to map to a children)
	 * @return
	 */
	public static int countViolateNodes(Elem p1, Elem p2, Elem[] src, Elem[] ref, int[][] bg, boolean acs_or_sib, boolean p1_or_p2, boolean potential_or_unavoidable) {
		int num = 0;
		Vector<Integer> src_idx = new Vector<Integer>();
		Vector<Integer> ref_idx = new Vector<Integer>();
		if(acs_or_sib) {
			src_idx = ElemUtil.getChildrenIdx(p1, src);
			ref_idx = ElemUtil.getChildrenIdx(p2, ref);
		}
		else {
			src_idx = ElemUtil.getSibIdx(p1, src);
			ref_idx = ElemUtil.getSibIdx(p2, ref);
		}
		if(potential_or_unavoidable) {
		if(p1_or_p2) {
			for(int i=0; i<src_idx.size();i++) {
				for(int j=0; j<bg[i].length;j++) {
					if(bg[src_idx.get(i)][j] == 1 && !ref_idx.contains(j)) { // that means this element has a chance to map to a non-children of p2
						++num; break;
					}
				}
			}
		}
		else {
			for(int i=0; i<bg.length;i++) {
				for(int j=0; j<ref_idx.size();j++) {
					if(bg[i][ref_idx.get(j)] == 1 && !src_idx.contains(i)) { // that means this element has a chance to map to a non-children of p2
						++num; break;
					}
				}
			}
		}
		}
		else {
			if(p1_or_p2) {
				for(int i=0; i<src_idx.size();i++) {
					boolean unavoidable = false;
					for(int j=0; j<bg[i].length;j++) {
						if(bg[src_idx.get(i)][j] == 1 && ref_idx.contains(j)) { // that means this element has a chance to map to a children of p2
							break;
						}else if(j==bg[i].length-1) {
							if(bg[src_idx.get(i)][j] == 0 || (bg[src_idx.get(i)][j] == 1 && !ref_idx.contains(j))) { // that means this element has no change to map to p2's children
								unavoidable = true;
							}
						}
					}
					if(unavoidable) ++num;
				}
			}
			else {
				for(int i=0; i<bg.length;i++) {
					boolean unavoidable = false;
					for(int j=0; j<ref_idx.size();j++) {
						if(bg[i][ref_idx.get(j)] == 1 && src_idx.contains(i)) { // that means this element has a chance to map to a children of p2
							break;
						}else if(j==bg[i].length-1) {
							if(bg[i][ref_idx.get(j)] == 0 || (bg[i][ref_idx.get(j)] == 1 && !src_idx.contains(i))) { // that means this element has no change to map to p2's children
								unavoidable = true;
							}
						}
					}
					if(unavoidable) ++num;
				}
			}
		}
		return num;
	}
	
	/**
	 * Get the number of nodes which may generate a correct mapping (p1's children or sibling will map to p2's children or sibling)
	 * @param p1 the src element
	 * @param p2 the mapping element in ref
	 * @param src 
	 * @param ref
	 * @param bg the binary graph between src and ref
	 * @param acs_or_sib a boolean value for choosing which node you wanna get. (ancestor or sibling)
	 * @param e1_or_e2 a boolean value for choosing whose node you wanna get (p1's children or p2's children)
	 * @param potential_or_unavoidable a boolean value for choosing the kind of valid nodes (is this element has chance to map to a children or it must map to a children)
	 * @return
	 */
	public static int countValidNodes(Elem p1, Elem p2, Elem[] src, Elem[] ref, int[][] bg, boolean acs_or_sib, boolean p1_or_p2, boolean potential_or_unavoidable) {
		int num = 0;
		Vector<Integer> src_idx = new Vector<Integer>();
		Vector<Integer> ref_idx = new Vector<Integer>();
		if(acs_or_sib) {
			src_idx = ElemUtil.getChildrenIdx(p1, src);
			ref_idx = ElemUtil.getChildrenIdx(p2, ref);
		}
		else {
			src_idx = ElemUtil.getSibIdx(p1, src);
			ref_idx = ElemUtil.getSibIdx(p2, ref);
		}
		
		if(potential_or_unavoidable) {
			if(p1_or_p2) {
				for(int i=0; i<src_idx.size();i++) {
					for(int j=0; j<bg[i].length;j++) {
						if(bg[src_idx.get(i)][j] == 1 && ref_idx.contains(j)) { // that means this element has a chance to map to a non-children of p2
							++num; break;
						}
					}
				}
			}
			else {
				for(int i=0; i<bg.length;i++) {
					for(int j=0; j<ref_idx.size();j++) {
						if(bg[i][ref_idx.get(j)] == 1 && src_idx.contains(i)) { // that means this element has a chance to map to a non-children of p2
							++num; break;
						}
					}
				}
			}
		}
		else {
		if(p1_or_p2) {
			for(int i=0; i<src_idx.size();i++) {
				boolean unavoidable = false;
				for(int j=0; j<bg[i].length;j++) {
					if(bg[src_idx.get(i)][j] == 0 && ref_idx.contains(j)) { // that means this element has a chance to map to a children of p2
						break;
					}else if(j==bg[i].length-1) {
						if(bg[src_idx.get(i)][j] == 1 || (bg[src_idx.get(i)][j] == 0 && !ref_idx.contains(j))) { // that means this element has no change to map to p2's children
							unavoidable = true;
						}
					}
				}
				if(unavoidable) ++num;
			}
		}
		else {
			for(int i=0; i<bg.length;i++) {
				boolean unavoidable = false;
				for(int j=0; j<ref_idx.size();j++) {
					if(bg[i][ref_idx.get(j)] == 0 && src_idx.contains(i)) { // that means this element has a chance to map to a children of p2
						break;
					}else if(j==bg[i].length-1) {
						if(bg[i][ref_idx.get(j)] == 1 || (bg[i][ref_idx.get(j)] == 0 && !src_idx.contains(i))) { // that means this element has no change to map to p2's children
							unavoidable = true;
						}
					}
				}
				if(unavoidable) ++num;
			}
		}
		}
		return num;
	}
	/**
	 * A function to update the binary graph once the element src[idx1] is matched to ref[idx2], the other edges which connect to src[idx1] or ref[idx2] should be removed
	 * @param idx1 the index of source element
	 * @param idx2 the index of the matching element
	 * @param bg binary graph
	 */
	public static void updateBG(int idx1, int idx2, int[][] bg) {
		for(int i=0;i<bg[idx1].length;i++) {
			bg[idx1][i] = i==idx2?1:0;
		}
		for(int i=0;i<bg.length;i++) {
			bg[i][idx2] = i==idx1?1:0;
		}
	}
	
	public static int count_nodes(Elem p1, Elem p2, Elem[] src, Elem[] ref, int[][] bg, boolean acs_or_sib, boolean valid_or_violate, boolean p1_or_p2) {
		int nodeNum = 0;
		Vector<Integer> src_idx = new Vector<Integer>();
		Vector<Integer> ref_idx = new Vector<Integer>();
		if(acs_or_sib) {
			src_idx = ElemUtil.getChildrenIdx(p1, src);
			ref_idx = ElemUtil.getChildrenIdx(p2, ref);
		}
		else {
			src_idx = ElemUtil.getSibIdx(p1, src);
			ref_idx = ElemUtil.getSibIdx(p2, ref);
		}
		
		if(p1_or_p2) {
			for(int i=0; i<src_idx.size();i++) {
				for(int j=0; j<bg[i].length-1;j++) {
					boolean flag = valid_or_violate? (bg[src_idx.get(i)][j] == 1 && ref_idx.contains(j)):(bg[src_idx.get(i)][j] == 1 && !ref_idx.contains(j));
					if(flag) { // that means this element has a chance to map to a non-children of p2
						++nodeNum; break;
					}
				}
			}
		}
		else {
			for(int i=0; i<bg.length-1;i++) {
				for(int j=0; j<ref_idx.size();j++) {
					boolean flag = valid_or_violate?
							(bg[i][ref_idx.get(j)] == 1 && src_idx.contains(i)):
								(bg[i][ref_idx.get(j)] == 1 && !src_idx.contains(i));
					if(flag) { // that means this element has a chance to map to a non-children of p2
						++nodeNum; break;
					}
				}
			}
		}
		return nodeNum;
	}
	public static int count_f(Elem e1, Elem e2, Elem[] src, Elem[] ref, int[][] bg, boolean e1_or_e2) {
		Vector<Integer> match_parents = new Vector<>();
		Vector<Integer> src_idx = new Vector<Integer>();
		Vector<Integer> ref_idx = new Vector<Integer>();
		src_idx = ElemUtil.getSibIdx(e1, src);
		ref_idx = ElemUtil.getSibIdx(e2, ref);
		
		if(e1_or_e2) {
		for(int i=0;i<src_idx.size();i++) {
			int idx = src_idx.get(i);
			for(int j=0;j<bg[idx].length-1;j++) {
				if(bg[idx][j]==1) {
					Elem match = ref[j];
					if(!match_parents.contains(match.getPid())) {
						match_parents.add(match.getPid());
					}
				}
			}
			if(bg[idx][ref.length]==1) {
				if(!match_parents.contains(-1)) {
					match_parents.add(-1);
				}
			}
		}
		}else {
			for(int i=0;i<ref_idx.size();i++) {
				int idx = ref_idx.get(i);
				for(int j=0;j<bg.length-1;j++) {
					if(bg[j][idx]==1) {
						Elem match = src[j];
						if(!match_parents.contains(match.getPid())) {
							match_parents.add(match.getPid());
						}
					}
				}
				if(bg[bg.length-1][idx]==1) {
					if(!match_parents.contains(-1)) {
						match_parents.add(-1);
					}
				}
			}
		}
		return match_parents.size();
		
	}
	public static void update_none_match_refs(int[][] bg) {
		for(int i=0;i<bg.length;i++) {
			boolean none = true;
			for(int j=0;j<bg[i].length-1;j++) {
				if(bg[i][j]==1) {
					none = false;
					break;
				}
			}
			if(none) {
				bg[i][bg[i].length-1]=1;
			}
		}
	}
}
