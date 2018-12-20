package fxtm;

import java.util.Vector;


public class Compare {
	public static double sigmoid(double v){
		return (double)1/(1+Math.exp(0-v));
	}
	public static double dist(double v1, double v2){
		double delta = Math.abs(v1-v2);
		return Math.max(v1, v2)!=0?(double)delta/Math.max(v1, v2):0;
	}
	
	public static Vector<String> getProperties_oldVer(Elem e){
		Vector<String> prop = new Vector<>();
		prop.add(""+ e.getArea());
		prop.add(""+e.getAspectRatio());
		prop.add(""+ e.getBottom());
		prop.add(""+e.getContainImg());
		prop.add(""+e.getContainInput());
		prop.add(""+e.getContainTXT());
		prop.add(""+e.getCoverage());
		prop.add(""+e.getFillsHeight());
		prop.add(""+e.getFillsWidth());
		prop.add(""+e.getFontSize());
		prop.add(""+e.getFontWeight());
		prop.add(""+e.getFooter());
		prop.add(""+e.getHeader());
		prop.add(""+e.getHeight());
		prop.add(""+e.getHorizontalSidedness());
		prop.add(""+e.getImg_ratio());
		prop.add(""+e.getIsImg());		
		prop.add(""+e.getIsInput());
		prop.add(""+e.getIsTxt());
		prop.add(""+e.getImg_ratio());
		prop.add(""+e.getLeftSidedness());
		prop.add(""+e.getLevel());
		prop.add(""+e.getLogo());
		prop.add(""+e.getLeftSidedness());
		prop.add(""+e.getNavigation());
		prop.add(""+e.getNumChildren());
		prop.add(""+e.getNumImages());
		prop.add(""+e.getNumImages());
		prop.add(""+e.getNumLinks());
		prop.add(""+e.getNumSiblings());
		prop.add(""+e.getSearch());
		prop.add(""+e.getShapeAppearance());
		prop.add(""+e.getTagName());
		prop.add(""+e.getTextArea());
		prop.add(""+e.getTop());
		prop.add(""+e.getTopSidedness());
		prop.add(""+e.getVerticalSidedness());
		prop.add(""+e.getTxt_ratio());
		prop.add(""+e.getWidth());
		prop.add(""+e.getWordCount());
		prop.add(""+e.getX());
		prop.add(""+e.getY());
		return prop;
	}
	
	public static Vector<String> getProperties(Elem e){
		Vector<String> prop = new Vector<>();
			prop.add(""+e.getWidth());
			prop.add(""+e.getHeight());	
			prop.add(""+ e.getArea());
			prop.add(""+e.getAspectRatio());
			prop.add(""+e.getFontSize());
			prop.add(""+e.getFontWeight());
			prop.add(""+e.getMainColor());
			prop.add(""+e.getNumLinks());
			prop.add(""+e.getNumColors());
			prop.add(""+e.getNumChildren());
			prop.add(""+e.getNumImages());	
			prop.add(""+e.getNumSiblings());
			prop.add(""+e.getSiblingOrder());
			prop.add(""+e.getTextArea());
			prop.add(""+e.getWordCount());
			prop.add(""+e.getLevel());
			prop.add(""+e.getVerticalSidedness());
			prop.add(""+e.getHorizontalSidedness());
			prop.add(""+e.getLeftSidedness());
			prop.add(""+e.getTopSidedness());
			prop.add(""+e.getShapeAppearance());

		
			prop.add(""+e.getSearch());
			prop.add(""+e.getFooter());
			prop.add(""+e.getHeader());
			prop.add(""+e.getIsImg());	
			prop.add(""+e.getLogo());
			prop.add(""+e.getNavigation());	
			prop.add(""+ e.getBottom());
			prop.add(""+e.getFillsHeight());
			prop.add(""+e.getFillsWidth());	
		return prop;
	}
	
	/**
	 * return the element's properties
	 * @param e the element
	 * @param visualOrSemantic  a boolean value indicates which type of property you wanna get, true for visual, false for semantic 
	 * @return
	 */
	public static Vector<String> getProperties(Elem e, boolean visualOrSemantic){
		Vector<String> prop = new Vector<>();
		if(visualOrSemantic) { // get visual properties
			prop.add(""+e.getWidth());
			prop.add(""+e.getHeight());	
			prop.add(""+ e.getArea());
			prop.add(""+e.getAspectRatio());
			prop.add(""+e.getFontSize());
			prop.add(""+e.getFontWeight());
			prop.add(""+e.getMainColor());
			prop.add(""+e.getNumLinks());
			prop.add(""+e.getNumColors());
			prop.add(""+e.getNumChildren());
			prop.add(""+e.getNumImages());	
			prop.add(""+e.getNumSiblings());
			prop.add(""+e.getSiblingOrder());
			prop.add(""+e.getTextArea());
			prop.add(""+e.getWordCount());
			prop.add(""+e.getLevel());
			prop.add(""+e.getVerticalSidedness());
			prop.add(""+e.getHorizontalSidedness());
			prop.add(""+e.getLeftSidedness());
			prop.add(""+e.getTopSidedness());
			prop.add(""+e.getShapeAppearance());

		}else { // get semantic properties
			prop.add(""+e.getSearch());
			prop.add(""+e.getFooter());
			prop.add(""+e.getHeader());
			prop.add(""+e.getIsImg());	
			prop.add(""+e.getLogo());
			prop.add(""+e.getNavigation());	
			prop.add(""+ e.getBottom());
			prop.add(""+e.getFillsHeight());
			prop.add(""+e.getFillsWidth());	
		}

		return prop;
	}
	/**
	 * return a vector of distance between two elements
	 * @param e1
	 * @param e2
	 * @return
	 */
	public static Vector<Double> elemComp(Elem e1, Elem e2){
		Vector<Double> dis = new Vector<>();
		
		Vector<String> visual_1 =  getProperties(e1,true);
		Vector<String> semantic_1 =  getProperties(e1,false);
		
		Vector<String> visual_2 =  getProperties(e2,true);
		Vector<String> semantic_2 =  getProperties(e2,false);
		
		for(int i=0;i<visual_1.size();i++) {
			if(Util.isNumeric(visual_1.get(i))) {
				dis.add(dist(Double.parseDouble(visual_1.get(i)), Double.parseDouble(visual_2.get(i))));
			}else {
				dis.add(visual_1.get(i).equals(visual_2.get(i))?0.0:1.0);
			}
		}
		for(int i=0;i<semantic_1.size();i++){	
			dis.add(semantic_1.get(i).equals(semantic_2.get(i))?0.0:1.0);
	
		}
		return dis;
	}
	
	public static Vector<Double> getSigmoid(Vector<Double> e, double alpha) {
		
		for (int i=0; i<e.size(); i++) {
			e.setElementAt( 1.0 / (1+ alpha*Math.pow(Math.E, e.get(i)*(-1.0))), i);
		}
		
		return e;
	}
	
}
