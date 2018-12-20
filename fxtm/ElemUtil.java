package fxtm;

import java.util.ArrayList;

import java.util.List;

import java.util.Vector;

import javax.swing.JTabbedPane;


public class ElemUtil {

	public static void init(Elem[] e) {
		for(int i=0;i<e.length;i++) {
			e[i].setSiblings(getSibIdx(e[i], e));
			e[i].setChildren(getChildrenIdx(e[i], e));
			e[i].setPidx(getParentIdx(e[i], e));
		}
	}
	public static Vector<Integer> getLeavesIdx(Elem[] e){
		Vector<Integer> pid = new Vector<>();
		Vector<Integer> leaves = new Vector<>();
		for(int i =0; i<e.length;i++) {
			if(!pid.contains(e[i].getPid())) {
				pid.add(e[i].getPid());
			}
		}
		for(int i=0; i<e.length;i++) {
			if(!pid.contains(e[i].getId())){
				leaves.add(i);
			}
		}
		return leaves;
	}
public static Vector<Integer> getroot(Elem[] e){
		
		Vector<Integer> al = new Vector<Integer>();
		for(int i=0;i<e.length;i++){
			al.add(e[i].getId());
		}
		Vector<Integer> idx = new Vector<Integer>();
		for(int i =0;i<e.length;i++){
			if(!al.contains(e[i].getPid())){
				idx.add(i);
			}
		}
		return idx;
	}
	public static Vector<Elem> getChildren(Elem e, boolean txtOrImg, Elem[] set) {
		Vector<Elem> c = new Vector<Elem>();
		

		for (int i = 0; i < set.length; i++) {
			if (set[i].getPid() == e.getId() &&(set[i].getIsTxt()==1||set[i].getIsImg()==1)){
				if(txtOrImg){
					if(set[i].getIsTxt()==1){
						c.add(set[i]);
					}
				}else {
					if(set[i].getIsImg()==1){
						c.add(set[i]);
					}
				}
					
			}
		}
		return c;
	}
	
	public static Vector<Elem> getChildren(Elem e, Elem[] set) {
		Vector<Elem> c = new Vector<Elem>();
		

		for (int i = 0; i < set.length; i++) {
			if (set[i].getPid() == e.getId()){
				c.add(set[i]);
			}
		}
		return c;
	}

	public static Elem[] getChildrenSet(Elem[] e, Elem[] set) {
		if (e == null) {
			return null;
		}
		int len = 0;
		for (int j = 0; j < e.length; j++) {
			for (int i = 0; i < set.length; i++) {
				if (set[i].getPid() == e[j].getId()) {
					++len;
				}
			}
		}
		if (len == 0) {
			return null;
		}
		Elem[] s = new Elem[len];
		for (int i = 0; i < len; i++) {
			s[i] = new Elem();
		}
		int idx = 0;
		for (int j = 0; j < e.length; j++) {
			for (int i = 0; i < set.length; i++) {
				if (set[i].getPid() == e[j].getId()) {
					s[idx] = set[i];
					++idx;
				}
			}
		}
		return s;
	}

	public static Elem[] getSiblingSet(Elem[] e, Elem[] set) {
		if (e == null) {
			return null;
		}
		int len = 0;
		for (int j = 0; j < e.length; j++) {
			for (int i = 0; i < set.length; i++) {
				if (set[i].getPid() == e[j].getPid()) {
					++len;
				}
			}
		}
		if (len == 0) {
			return null;
		}
		Elem[] s = new Elem[len];
		for (int i = 0; i < len; i++) {
			s[i] = new Elem();
		}
		int idx = 0;
		for (int j = 0; j < e.length; j++) {
			for (int i = 0; i < set.length; i++) {
				if (set[i].getPid() == e[j].getPid()) {
					s[idx] = set[i];
					++idx;
				}
			}
		}
		return s;
	}

	public static Elem[] getSibling(Elem e, Elem[] set) {
		if (e == null) {
			return null;
		}
		int len = 0;

		for (int i = 0; i < set.length; i++) {
			if (set[i].getPid() == e.getPid() && set[i].getId()!=e.getId()) {
				++len;
			}

		}
		if (len == 0) {
			return null;
		}
		Elem[] s = new Elem[len];
		for (int i = 0; i < len; i++) {
			s[i] = new Elem();
		}
		int idx = 0;

		for (int i = 0; i < set.length; i++) {
			if (set[i].getPid() == e.getPid()&&  set[i].getId()!=e.getId()) {
				s[idx] = set[i];
				++idx;
			}

		}
		return s;
	}
	public static Vector<Elem> getSibling_V(Elem e, Elem[] set){
		Vector<Elem> sib = new Vector<Elem>();
		for(int i=0;i<set.length;i++){
			if(set[i].getPid()==e.getPid()&&set[i].getId()!=e.getId()){
				sib.add(set[i]);
			}
		}
		return sib;
	}
	public static Vector<Elem> getClusSibling_V(Elem e, Elem[] set){
		Vector<Elem> sib = new Vector<Elem>();
		if(e==null){
			return sib;
		}
		for(int i=0;i<set.length;i++){
			if(set[i].getCid()==e.getCid()&&set[i].getId()!=e.getId()){
				sib.add(set[i]);
			}
		}
		return sib;
	}
	public static Vector<Integer> getClusSibIdx(Elem e, Elem[] set){
		Vector<Integer> sib = new Vector<Integer>();
		if(e==null){
			return sib;
		}
		for(int i=0;i<set.length;i++){
			if(set[i].getCid()==e.getCid()&&set[i].getId()!=e.getId()){
				sib.add(i);
			}
		}
		return sib;
	}

	public static Elem[] getParentSet(Elem[] e, Elem[] set) {
		if (e == null) {
			return null;
		}
		int len = 0;
		for (int j = 0; j < e.length; j++) {
			for (int i = 0; i < set.length; i++) {
				if (set[i].getId() == e[j].getPid()) {
					++len;
				}
			}
		}
		if (len == 0) {
			return null;
		}
		Elem[] s = new Elem[len];
		for (int i = 0; i < len; i++) {
			s[i] = new Elem();
		}
		int idx = 0;
		for (int j = 0; j < e.length; j++) {
			for (int i = 0; i < set.length; i++) {
				if (set[i].getId() == e[j].getPid()) {
					s[idx] = set[i];
					++idx;
				}
			}
		}
		return s;
	}

	public static Elem getParent(Elem e, Elem[] set) {
		if (e == null) {
			return null;
		}
		Elem p = null;
		for (int i = 0; i < set.length; i++) {
			if (set[i].getId() == e.getPid()) {
				p = set[i];
			}

		}
		return p;
	}

	public static Elem[] getElemsByTag(String tag, Elem[] set) {
		if (tag == null) {
			return null;
		}
		int len = 0;

		for (int i = 0; i < set.length; i++) {
			if (set[i].getTagName() == tag) {
				++len;
			}

		}
		if (len == 0) {
			return null;
		}
		Elem[] s = new Elem[len];
		for (int i = 0; i < len; i++) {
			s[i] = new Elem();
		}
		int idx = 0;

		for (int i = 0; i < set.length; i++) {
			if (set[i].getTagName() == tag) {
				s[idx] = set[i];
				++idx;
			}

		}
		return s;
	}

	private static void getSubtree(Elem root, Elem[] set, List<Elem> subtreeNode) {
		Elem[] children = getChildren(root, set).toArray(new Elem[1]);
		if (children == null) {
			return;
		} else {
			for (int i = 0; i < children.length; i++) {
				subtreeNode.add(children[i]);
			}
			for (int i = 0; i < children.length; i++) {
				getSubtree(children[i], set, subtreeNode);
			}
		}
	}

	public static Elem[] getSubtree(Elem root, Elem[] set) {
		List<Elem> subtreeNode = new ArrayList<Elem>();
		subtreeNode.add(root);
		getSubtree(root, set, subtreeNode);
		Elem[] subtree = new Elem[subtreeNode.size()];

		for (int i = 0; i < subtreeNode.size(); i++) {
			subtree[i] = new Elem();
			subtree[i] = subtreeNode.get(i);
		}
		return subtree;
	}
	private static void getSubtree(Elem root, Elem[] set, boolean txtOrImg, List<Elem> subtreeNode) {
		Vector<Elem> speChildren = getChildren(root, txtOrImg, set);
		Elem[] children = getChildren(root, set).toArray(new Elem[1]);
	//	System.err.println("p="+root.getId()+" c.size="+children.size());
		
		if (children == null || children.length==0) {
			return;
		} else {
			for (int i = 0; i < speChildren.size(); i++) {
				//System.out.println("c["+i+"]="+speChildren.get(i));
				subtreeNode.add(speChildren.get(i));
			}
			for (int i = 0; i < children.length; i++) {
				getSubtree(children[i], set, txtOrImg,subtreeNode);
			}
		}
	}

	public static Elem[] getSubtree(Elem root, boolean txtOrImg, Elem[] set) {
		List<Elem> subtreeNode = new ArrayList<Elem>();
		subtreeNode.add(root);
		getSubtree(root, set, subtreeNode);
		Elem[] subtree = new Elem[subtreeNode.size()];

		for (int i = 0; i < subtreeNode.size(); i++) {
			subtree[i] = new Elem();
			subtree[i] = subtreeNode.get(i);
		}
		return subtree;
	}
	public static Elem[] getDescendant(Elem root, Elem[] set) {
		List<Elem> subtreeNode = new ArrayList<Elem>();
		//subtreeNode.add(root);
		getSubtree(root, set, subtreeNode);
		Elem[] subtree = new Elem[subtreeNode.size()];

		for (int i = 0; i < subtreeNode.size(); i++) {
			subtree[i] = new Elem();
			subtree[i] = subtreeNode.get(i);
		}
		return subtree;
	}
	public static Elem[] getDescendant(Elem root, boolean txtOrImg, Elem[] set) {
		List<Elem> subtreeNode = new ArrayList<Elem>();
		//subtreeNode.add(root);
		getSubtree(root, set,txtOrImg, subtreeNode);
		Elem[] subtree = new Elem[subtreeNode.size()];

		for (int i = 0; i < subtreeNode.size(); i++) {
			subtree[i] = new Elem();
			subtree[i] = subtreeNode.get(i);
		}
		return subtree;
	}
	
	public static Elem getElemById(Elem[] e, int id) {
		for (int i = 0; i < e.length; i++) {
			if (e[i].getId() == id) {
				return e[i];
			}
		}
		return null;

	}

	public static int getElemIdxById(Elem[] e, int id) {
		for (int i = 0; i < e.length; i++) {
			if (e[i].getId() == id) {
				return i;
			}
		}
		return -1;

	}

	public static Vector<Elem> getElemByClusID(Elem[] e, int cid) {

		Vector<Elem> v = new Vector<Elem>();
		for (int i = 0; i < e.length; i++) {
			if (e[i].getCid() == cid) {
				
				v.add(e[i]);
			}
		}
		return v;
	}
	public static Vector<Integer> getElemIdxByCid(Elem[] e, int cid) {

		Vector<Integer> v = new Vector<Integer>();
		for (int i = 0; i < e.length; i++) {
			if (e[i].getCid() == cid) {
				
				v.add(i);
			}
		}
		return v;
	}

	public static Elem[] getElemByParentID(Elem[] e, int pid) {
		int len = 0;
		Vector<Object> v = new Vector<Object>();
		for (int i = 0; i < e.length; i++) {
			if (e[i].getPid() == pid) {
				++len;
				v.add(e[i]);
			}
		}
		Elem[] res = new Elem[len];
		for (int i = 0; i < v.size(); i++) {
			res[i] = new Elem();
			res[i] = (Elem) v.elementAt(i);
		}
		return res;
	}

	
	public static Vector<Integer> getChildrenIdx(Elem e, Elem[] set) {
		Vector<Integer> idx = new Vector<Integer>();
		for (int i = 0; i < set.length; i++) {
			if (set[i].getPid() == e.getId()) {
				idx.add(i);
			}

		}
		return idx;
	}

	public static int getParentIdx(Elem e, Elem[] set) {
		int idx = -1;
		for (int i = 0; i < set.length; i++) {
			if (e.getPid() == set[i].getId()) {
				idx = i;
			}

		}
		return idx;
	}

	public static Vector<Integer> getSibIdx(Elem e, Elem[] set) {
		Vector<Integer> idx = new Vector<Integer>();
		if (e == null) {
			return null;
		}
		for (int i = 0; i < set.length; i++) {
			if (set[i].getPid() == e.getPid() && set[i].getId()!=e.getId()) {
				idx.add(i);
			}

		}
		return idx;
	}

	
	

	public static void updatePcid(Elem[] e){
		for(int i=0;i<e.length;i++){
			Elem p = getParent(e[i], e);
			e[i].setPcid(p==null?0:p.getCid());
		}
	}
	public static int getMaxCid(Elem[] e){
		int mcid = 0;
		for(int i=0;i<e.length;i++){
			if(e[i].getCid()>mcid){
				mcid = e[i].getCid();
			}
		}
		return mcid;
		
	}
	public static int getRootIdx(Elem[] e) {
		int root = -1;
		for(int i=0;i<e.length;i++) {
			if(getElemById(e, e[i].getPid())==null) {
				root = i;
			}
		}
		return root;
	}
}
