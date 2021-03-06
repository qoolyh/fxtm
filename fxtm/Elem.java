package fxtm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.testng.junit.IJUnitTestRunner;

public class Elem {
	// The basic properties:
	private int id;
	private int pid;
	private int pidx;
	private String selector;
	private String display;
	private String clsName;
	private String idName;
	private String tagName;
	private String overflow;
	private int isZero;
	private int cid;
	private int pcid;
	private int mid;
	private int mcls;

	// The visual properties include:
	private int width;
	private int height;
	private int area;
	private double aspectRatio;
	private int fontSize;
	private double fontWeight;
	private String mainColor;
	private String bkColor;
	private double coverage;
	private double img_ratio;
	private double txt_ratio;
	private int x;
	private int y;
	private double rx,ry,rw,rh;
	private int numLinks;
	private int numColors;
	private int numChildren;
	private int numImages;
	private int numSiblings;
	private int siblingOrder;
	private int textArea;
	private int wordCount;
	private int level;
	private double verticalSidedness; // (normal- ized distance from the horizon of the page)
	private double horizontalSidedness; // (normalized distance from the midline of the page);
	private double leftSidedness; // (normalized distance from the left border of the page);
	private double topSidedness; // (normalized distance from the top border of the page);
	private double shapeAppearance; // (the minimum of the aspect ratio and its inverse).

	// The semantic properties include:
	private int isImg;
	private int isTxt;
	private int search;
	private int footer;
	private int header;
	private int isInput;
	private int logo; // how?
	private int navigation;
	private int bottom; // (if the node is in the bottom 10% of the page);
	private int top; // (if the node is in the top 10% of the page);
	private int fillsHeight; // (if the node extends more than 90% down the page);
	private int fillsWidth; // (if the node extends more than 90% across the page).
	private int containImg;
	private int containTXT;
	private int containInput;
	private int typeCls;
	private Vector<Integer> children;
	private Vector<Integer> siblings;

	public Elem(ResultSet rs, int pageWidth, int pageHeight) throws SQLException {
		id = rs.getInt("ID");
		pid = rs.getInt("parentID");

		isZero = rs.getInt("isZero");
		cid = rs.getInt("clusterID");
		mid = rs.getInt("matchID");
		mcls = rs.getInt("matchCls");
		
		width = rs.getInt("width");
		height = rs.getInt("height");
		area = width * height;
		aspectRatio = height != 0 ? (double) width / height : 0;
		fontSize = rs.getInt("font_size");
		fontWeight = rs.getInt("font_weight");
		mainColor = rs.getString("color");
		tagName = rs.getString("tag");
		
//		numLinks = rs.getInt("num_links");
//		numColors = rs.getInt("num_colors");
		numLinks = 1;
		numColors = 1;
		numChildren = rs.getInt("children_num");
//		numImages = rs.getInt("num_img");
		numImages = 1;
		numSiblings = rs.getInt("sib_num");
		siblingOrder = rs.getInt("sib_order");
		textArea = rs.getInt("textarea");
		wordCount = rs.getInt("word_count");
		level = rs.getInt("lv");
		verticalSidedness = rs.getDouble("v_side"); // (normalized distance from the horizon of the page)
		horizontalSidedness = rs.getDouble("h_side"); // (normalized distance from the midline of the page);
		leftSidedness = rs.getDouble("l_side"); // (normalized distance from the left border of the page);
		topSidedness = rs.getDouble("t_side"); // (normalized distance from the top border of the page);
		shapeAppearance = rs.getDouble("shapeAprnc"); // (the minimum of the aspect ratio and its
		
		// The semantic properties include:
		search = rs.getInt("search");
		footer = rs.getInt("footer");
//		header = rs.getInt("isHeader");
		header = 0;
		isImg = rs.getInt("image");
		logo = rs.getInt("logo");
		navigation = rs.getInt("navigation");
		bottom = rs.getInt("bottom");
		fillsHeight = rs.getInt("fills_height");
		fillsWidth = rs.getInt("fills_width");
		x = rs.getInt("offsetLeft");
		y = rs.getInt("offsetTop");
		rx = (double) x/pageWidth;
		ry = (double) y/pageHeight;
		rw = (double) width/pageWidth;
		rh = (double) height/pageHeight;
	}
	public double getRx() {
		return rx;
	}

	public void setRx(double rx) {
		this.rx = rx;
	}

	public double getRy() {
		return ry;
	}

	public void setRy(double ry) {
		this.ry = ry;
	}

	public double getRw() {
		return rw;
	}

	public void setRw(double rw) {
		this.rw = rw;
	}

	public double getRh() {
		return rh;
	}

	public void setRh(double rh) {
		this.rh = rh;
	}

	public Elem() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getClsName() {
		return clsName;
	}

	public void setClsName(String clsName) {
		this.clsName = clsName;
	}

	public String getIdName() {
		return idName;
	}

	public void setIdName(String idName) {
		this.idName = idName;
	}

	public String getOverflow() {
		return overflow;
	}

	public void setOverflow(String overflow) {
		this.overflow = overflow;
	}

	public int getIsZero() {
		return isZero;
	}

	public void setIsZero(int isZero) {
		this.isZero = isZero;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public int getPcid() {
		return pcid;
	}

	public void setPcid(int pcid) {
		this.pcid = pcid;
	}

	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public int getMcls() {
		return mcls;
	}

	public void setMcls(int mcls) {
		this.mcls = mcls;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public double getAspectRatio() {
		return aspectRatio;
	}

	public void setAspectRatio(double aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public double getFontWeight() {
		return fontWeight;
	}

	public void setFontWeight(double fontWeight) {
		this.fontWeight = fontWeight;
	}

	public String getMainColor() {
		return mainColor;
	}

	public void setMainColor(String mainColor) {
		this.mainColor = mainColor;
	}

	public String getBkColor() {
		return bkColor;
	}

	public void setBkColor(String bkColor) {
		this.bkColor = bkColor;
	}

	public double getCoverage() {
		return coverage;
	}

	public void setCoverage(double coverage) {
		this.coverage = coverage;
	}

	public double getImg_ratio() {
		return img_ratio;
	}

	public void setImg_ratio(double img_ratio) {
		this.img_ratio = img_ratio;
	}

	public double getTxt_ratio() {
		return txt_ratio;
	}

	public void setTxt_ratio(double txt_ratio) {
		this.txt_ratio = txt_ratio;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getNumLinks() {
		return numLinks;
	}

	public void setNumLinks(int numLinks) {
		this.numLinks = numLinks;
	}

	public int getNumColors() {
		return numColors;
	}

	public void setNumColors(int numColors) {
		this.numColors = numColors;
	}

	public int getNumChildren() {
		return numChildren;
	}

	public void setNumChildren(int numChildren) {
		this.numChildren = numChildren;
	}

	public int getNumImages() {
		return numImages;
	}

	public void setNumImages(int numImages) {
		this.numImages = numImages;
	}

	public int getNumSiblings() {
		return numSiblings;
	}

	public void setNumSiblings(int numSiblings) {
		this.numSiblings = numSiblings;
	}

	public int getSiblingOrder() {
		return siblingOrder;
	}

	public void setSiblingOrder(int siblingOrder) {
		this.siblingOrder = siblingOrder;
	}

	public int getTextArea() {
		return textArea;
	}

	public void setTextArea(int textArea) {
		this.textArea = textArea;
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public double getVerticalSidedness() {
		return verticalSidedness;
	}

	public void setVerticalSidedness(double verticalSidedness) {
		this.verticalSidedness = verticalSidedness;
	}

	public double getHorizontalSidedness() {
		return horizontalSidedness;
	}

	public void setHorizontalSidedness(double horizontalSidedness) {
		this.horizontalSidedness = horizontalSidedness;
	}

	public double getLeftSidedness() {
		return leftSidedness;
	}

	public void setLeftSidedness(double leftSidedness) {
		this.leftSidedness = leftSidedness;
	}

	public double getTopSidedness() {
		return topSidedness;
	}

	public void setTopSidedness(double topSidedness) {
		this.topSidedness = topSidedness;
	}

	public double getShapeAppearance() {
		return shapeAppearance;
	}

	public void setShapeAppearance(double shapeAppearance) {
		this.shapeAppearance = shapeAppearance;
	}

	public int getIsImg() {
		return isImg;
	}

	public void setIsImg(int isImg) {
		this.isImg = isImg;
	}

	public int getIsTxt() {
		return isTxt;
	}

	public void setIsTxt(int isTxt) {
		this.isTxt = isTxt;
	}

	public int getSearch() {
		return search;
	}

	public void setSearch(int search) {
		this.search = search;
	}

	public int getFooter() {
		return footer;
	}

	public void setFooter(int footer) {
		this.footer = footer;
	}

	public int getHeader() {
		return header;
	}

	public void setHeader(int header) {
		this.header = header;
	}

	public int getIsInput() {
		return isInput;
	}

	public void setIsInput(int isInput) {
		this.isInput = isInput;
	}

	public int getLogo() {
		return logo;
	}

	public void setLogo(int logo) {
		this.logo = logo;
	}

	public int getNavigation() {
		return navigation;
	}

	public void setNavigation(int navigation) {
		this.navigation = navigation;
	}

	public int getBottom() {
		return bottom;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getFillsHeight() {
		return fillsHeight;
	}

	public void setFillsHeight(int fillsHeight) {
		this.fillsHeight = fillsHeight;
	}

	public int getFillsWidth() {
		return fillsWidth;
	}

	public void setFillsWidth(int fillsWidth) {
		this.fillsWidth = fillsWidth;
	}

	public int getContainImg() {
		return containImg;
	}

	public void setContainImg(int containImg) {
		this.containImg = containImg;
	}

	public int getContainTXT() {
		return containTXT;
	}

	public void setContainTXT(int containTXT) {
		this.containTXT = containTXT;
	}

	public int getContainInput() {
		return containInput;
	}

	public void setContainInput(int containInput) {
		this.containInput = containInput;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public int getTypeCls() {
		return typeCls;
	}

	public void setTypeCls(int typeCls) {
		this.typeCls = typeCls;
	}
	public Vector<Integer> getChildren() {
		return children;
	}
	public void setChildren(Vector<Integer> children) {
		this.children = children;
	}
	public Vector<Integer> getSiblings() {
		return siblings;
	}
	public void setSiblings(Vector<Integer> siblings) {
		this.siblings = siblings;
	}
	public int getPidx() {
		return pidx;
	}
	public void setPidx(int pidx) {
		this.pidx = pidx;
	}
	
	
}
