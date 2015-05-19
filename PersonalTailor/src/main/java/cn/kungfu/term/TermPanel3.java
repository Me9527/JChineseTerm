package cn.kungfu.term;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JPanel;

public class TermPanel3 extends JPanel {
	private static final long serialVersionUID = -5348366141749264758L;
	private Color bground = Color.black;
	private Color fground = Color.yellow;
	private Color curBground = Color.gray;
	
	private Font font;
	private String fontName = "宋体";
//	private boolean antialiasing = true;
	private Graphics2D grp;
	private BufferedImage img;
	
	private int term_width = 80;
//	private int term_height = 24;
	private int currColumn = 0;
	private int currRow = 0;
	
	private int descent = 0;
	private int aescent = 0;
	private int x = 0;
	private int y = 0;
	private int char_width;
	private int char_height;
	
	private int selectBeginX;
	private int selectBeginY;
	private int selectEndX;
	private int selectEndY;
	private int selectCurrX;
	private int selectCurrY;
	private int currentXSelectDirection = 0;
	private int selectLastX = 0;
	private int currentYSelectDirection = 0;
	private int selectLastY = 0;
//	private int resizeFlag = 0;
//	private Component term_area = null;
	
	private int charContentIdx = 0;
	private char charContent[] = null;
	private char[]  displayBuffer =  new char[16384];
	private int bufferIndex = 0;
	private int[][] bufferMap = new int[8192][];
	private final int CHINESE_MASK = -1;
	private final int NULL_MASK = -2;
//	private boolean pressFlag = false;
//	private int selectLength = 0;
	
	private int upOrDown = 0;
	private int leftOrRight = 0;
	
	public TermPanel3() {
//		term_area = this;
	}
	
	public void init(){
		setFont();
        addMouseListener(new SelectionMouseListener());
        addMouseMotionListener(new SelectionMouseMotionListener());
//        this.setAutoscrolls(true);
	}
	
	
    private class SelectionMouseMotionListener extends MouseMotionAdapter {
        public void mouseDragged(MouseEvent e) {
        	selectCurrX = e.getX()/char_width;
        	if(selectCurrX >= term_width )	selectCurrX = term_width - 1;
        	if(e.getX() < 0)	selectCurrX = -1;
//        	if(selectCurrX < 0  )	selectCurrX = 0;
        	
        	selectCurrY = e.getY()/char_height;
        	if(selectCurrY > currRow )	selectCurrY = currRow;
        	if(selectCurrY < 0  )	selectCurrY = 0;
//        	System.out.println(e.getX());
//        	if(selectCurrX < lastXSelectDirection){
////        		unSelectChars() ;
////        		System.out.println("selectCurrX="+selectCurrX + ", lastXSelectDirection=" + lastXSelectDirection);
//        	}else
            selectChars();
            repaint();
        }
    }
    
	private void drawSelectedLeft(){
		if(selectLastX >= term_width ) return;
//		if(selectLastX > selectCurrX){
//			fground = Color.black;
//			bground = Color.yellow;
//		}
		while( selectLastX > 0 && NULL_MASK == bufferMap[selectLastY][selectLastX] )	//不满一行的问题处理
			selectLastX-- ;
		if(-1 == selectCurrX) 
			selectCurrX = 0;
		else
			selectCurrX = CHINESE_MASK == bufferMap[selectLastY][selectCurrX] ? selectCurrX + 2: selectCurrX + 1 ;
    	selectLastX = CHINESE_MASK == bufferMap[selectLastY][selectLastX] ? selectLastX + 2: selectLastX + 1 ;	
    	if( selectCurrX >= selectLastX) return;
    	
		int selectChars = 0;
		for (int i = selectCurrX == -1 ? 0 : selectCurrX; i < selectLastX; i++) {
			selectChars = selectChars + ((CHINESE_MASK == bufferMap[selectLastY][i]) ? 0 : 1);
		}
		if( selectChars > 0){
			grp.setColor(fground);
	    	grp.fillRect(selectCurrX * char_width, selectLastY * char_height + descent, (selectLastX - selectCurrX) * char_width, char_height + descent);
	    	
//	    	System.out.println("selectLastY = " + selectLastY + ",selectCurrX=" + selectCurrX + ",selectLastX=" + selectLastX + ",selectChars=" + selectChars + ",String = " +
//			 new String(displayBuffer, CHINESE_MASK != bufferMap[selectLastY][selectCurrX] ? bufferMap[selectLastY][selectCurrX] : bufferMap[selectLastY][selectCurrX+1], selectChars) );
	    	
	    	grp.setColor(bground);
	    	grp.drawChars(displayBuffer, CHINESE_MASK != bufferMap[selectLastY][selectCurrX] ? bufferMap[selectLastY][selectCurrX] : bufferMap[selectLastY][selectCurrX+1], 
	    			selectChars, selectCurrX * char_width, (selectLastY+ 1) * char_height);
	    	
	    	leftOrRight = -1;
	    	selectLastX = selectCurrX;
          	selectLastY = selectCurrY;
		}
		
	}
	
	private void selectChars() {
		
		if(selectCurrY == selectLastY){ 		//框选一行
			if(selectCurrX == selectLastX){
//				System.out.println("selectCurrY == selectBeginY && selectCurrX == lastXSelectDirection");
				return;
			}else if (selectCurrX > selectLastX){	//同一行向->选择
				drawSelectedRight();
				return;
			}else {									//同一行向<-选择
				drawSelectedLeft();
				return;
			}
		}else if (selectCurrY > selectLastY){	//向下框选多行
			//处理起始行
			//选中中间各行
			//处理截至行
			
		}else{									//向上框选多行
			
		}
    }
	
	private void drawSelectedRight(){
//		pressFlag = false;
    	if(bufferMap[selectLastY] [ selectLastX ] < 0)	// CHINESE_MASK or NULL_MASK
    		;
    	else if( (0x20 <= displayBuffer[ bufferMap[selectLastY] [ selectLastX ] ] && displayBuffer[ bufferMap[selectLastY] [ selectLastX ] ]< 0x7f) )	//ASCII character
    		;
    	else
    		selectLastX --;	//中文的右部分上点击,需要向左一个位置,才能选中这个中文字
//		if(selectLastX < selectCurrX){
//			fground = Color.yellow;
//			bground = Color.black;
//		}
		int selectChars = 0;
		for(int i = selectLastX; i < selectCurrX; i++){
			selectChars = selectChars + ((CHINESE_MASK == bufferMap[selectLastY][i] ) ? 0 : 1);
		}
		if( term_width - 1 == selectCurrX && NULL_MASK != bufferMap[selectLastY][selectCurrX] ){
			selectChars ++ ;  			selectCurrX++;
		}
		if( selectChars > 0){
			grp.setColor(fground);
	    	grp.fillRect(selectLastX * char_width, selectLastY * char_height + descent, (selectCurrX-selectLastX) * char_width, char_height + descent);
	    	
//	    	System.out.println("selectLastX=" + selectLastX + ",selectCurrX=" + selectCurrX + ",selectChars=" + selectChars +
//	    			",String = " + new String(displayBuffer, ( CHINESE_MASK != bufferMap[selectLastY][selectLastX] ) ? bufferMap[selectLastY][selectLastX] : bufferMap[selectLastY][selectLastX+1], selectChars));
	    	grp.setColor(bground);
	    	grp.drawChars(displayBuffer, ( CHINESE_MASK != bufferMap[selectLastY][selectLastX] ) ? bufferMap[selectLastY][selectLastX] : bufferMap[selectLastY][selectLastX+1], 
	    			selectChars, selectLastX * char_width, (selectLastY+ 1) * char_height);
	    	
	    	leftOrRight = 1;
        	selectLastX = ( CHINESE_MASK == bufferMap[selectLastY][selectCurrX - 1] ) ? selectCurrX  - 1: selectCurrX ;
        	selectLastX = selectLastX >= term_width ? term_width - 1 :  selectLastX;
          	selectLastY = selectCurrY;
		}
	}
	
    private class SelectionMouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
//        	pressFlag = true;
        	selectBeginX = e.getX()/char_width;
        	selectLastX = selectBeginX;
        	if(selectLastX < 0) selectLastX  = 0;
        	if(selectLastX >= term_width ) selectLastX = term_width - 1;
        	
        	selectBeginY = e.getY()/char_height;
        	if(selectBeginY < 0) selectBeginY  = 0;
        	selectLastY = selectBeginY;
        	
        }
        
		public void mouseReleased(MouseEvent e) {
        	selectEndX = e.getX()/char_width;
        	selectEndY = e.getY()/char_height;
        	if( selectBeginX > selectEndX )
        		return;
//			int bDisplayBufferIdx = (CHINESE_MASK == bufferMap[selectBeginY][selectBeginX])? bufferMap[selectBeginY][selectBeginX+1] : bufferMap[selectBeginY][selectBeginX];
//			int eDisplayBufferIdx = (CHINESE_MASK == bufferMap[selectEndY][selectEndX])? bufferMap[selectEndY][selectEndX-1] : bufferMap[selectEndY][selectEndX];
//			eDisplayBufferIdx = (NULL_MASK == bufferMap[selectCurrY][selectCurrX])? bufferMap[selectCurrY][selectCurrX-1] : bufferMap[selectCurrY][selectCurrX];
//			if(bDisplayBufferIdx < 0 ) 	bDisplayBufferIdx = 0;
//			if(eDisplayBufferIdx < 0 ) 	eDisplayBufferIdx = 0;
//        	System.out.println("rs = " + new String(displayBuffer, bDisplayBufferIdx,  eDisplayBufferIdx) );
        }
    }
	
	public void drawChars(char[] chars) {
		charContent = chars;
		charContentIdx = 0;
		if(0 == currColumn){
			int[] t = new int[term_width];
			Arrays.fill(t, NULL_MASK);
			bufferMap[currRow] = t;
		}
		drawChars();

	}
	
	private void drawChars() {
		int[] oneRow = bufferMap[currRow];
		int offset = charContentIdx;
		for(; charContentIdx < charContent.length; charContentIdx++){
			char utfChar = charContent[charContentIdx];
			displayBuffer[bufferIndex++] = utfChar;
			if (0x20 <= utfChar && utfChar < 0x7f) {	//ASCII Char, 0x20 = 空格(space), 0x7f = 删除(delete)
				if(currColumn < term_width - 1)
					oneRow[currColumn] = bufferIndex - 1;
				currColumn = currColumn + 1;
				if(currColumn == term_width){
					grp.drawChars(charContent, offset, charContentIdx - offset, x, y);
					x = 0;	currColumn = 0;		
					y = y + char_height;
					oneRow = new int[term_width];
					Arrays.fill(oneRow, NULL_MASK);
					bufferMap[++currRow] = oneRow;
					drawChars();
					return;
				}else
					continue;
			}else{
				if(currColumn < term_width - 1){
					oneRow[currColumn] =CHINESE_MASK;
					oneRow[currColumn + 1] = bufferIndex - 1;
				}
				currColumn = currColumn + 2;
				if(currColumn > term_width){
					grp.drawChars(charContent, offset, charContentIdx - offset, x, y);
					x = 0;	currColumn = 0;		
					y = y + char_height;
					oneRow = new int[term_width];
					Arrays.fill(oneRow, NULL_MASK);
					bufferMap[++currRow] = oneRow;
					drawChars();
					return;
				}else
					continue;
			}
		}
		
		if(offset < charContentIdx ){
			grp.drawChars(charContent, offset, charContentIdx - offset, x, y);
			x = currColumn * char_width;	
		}
	}
	
	private void setFont() {
		font = new Font( "宋体", Font.PLAIN, 22 );
//		font = new Font( "Consolas", Font.PLAIN, 22 );	//Consolas
		Map<TextAttribute,Object> attr = new Hashtable<TextAttribute,Object>();
//		attr.put(TextAttribute.UNDERLINE,TextAttribute.UNDERLINE_ON);
//		attr.put(TextAttribute.POSTURE,TextAttribute.POSTURE_OBLIQUE);
//		attr.put(TextAttribute.SUPERSCRIPT,TextAttribute.SUPERSCRIPT);
//		attr.put(TextAttribute.JUSTIFICATION,TextAttribute.JUSTIFICATION_FULL);
//		attr.put(TextAttribute.SWAP_COLORS,TextAttribute.SWAP_COLORS_ON);
//		attr.put(TextAttribute.LIGATURES,TextAttribute.LIGATURES_ON);
//		attr.put(TextAttribute.TRACKING,TextAttribute.TRACKING_TIGHT);
//		attr.put(TextAttribute.WIDTH,TextAttribute.WIDTH_SEMI_EXTENDED);
//		attr.put(TextAttribute.KERNING,TextAttribute.KERNING_ON);
//		attr.put(TextAttribute.STRIKETHROUGH,TextAttribute.STRIKETHROUGH_ON);
//		TextAttribute.WIDTH_SEMI_CONDENSED
		font = font.deriveFont(attr);
		
		Object a = font.getAvailableAttributes();
		Map t = font.getAttributes();
		img = new BufferedImage(1200, 400, BufferedImage.TYPE_INT_RGB);
		grp = (Graphics2D) (img.getGraphics());
		grp.setFont(font);
		grp.setBackground(bground);
		grp.setColor(fground);
//		setAntiAliasing(true);
		FontMetrics fo = grp.getFontMetrics();
		char_width = (fo.charWidth((char) '@'));
//		char_width = 11;
		char_height = (fo.getHeight());
		descent = fo.getDescent();
		aescent = fo.getAscent();
		
		term_width = 330/char_width;		//真是列宽为term_width - 1 个字符.
//		term_width = term_width - 1;	//汉字占2个字符
//		if(term_width % 2 != 0)
//			term_width = term_width - 1;//奇数会显示半个汉字
//		x = x + char_height + char_height;
		y = y + char_height;
		
	}
	
	
	
	
	
	public void drawSlash() {
//		boolean showCursor = true;
		int k = 0;
		try {
			while (k < 10) {
				grp.fillRect(x, y - char_height, char_width, char_height);
				Thread.sleep(100);
				Color tmp = bground;
				bground = fground;
				fground = tmp;
	        	grp.setColor(bground);
	        	grp.setBackground(fground);
				k++;
				this.repaint();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
        	grp.setColor(fground);
        	grp.setBackground(bground);
		}

//		grp.drawRect(x, y, char_width, char_height);
		
	}
	
	public void newLine() {
		x = 0;
		currColumn = 0;	
		y = y + char_height;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (img != null) {
			g.drawImage(img, 0, 0, this);
		}
	}
	
	public void setAntiAliasing(boolean foo) {
		if (grp == null)
			return;
		Object mode = foo ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, mode);
		grp.setRenderingHints(hints);
	}
	
	
	
	
	
	
	
	public static void main(String[] args) {
		
		MainGUI.main(null);
		
	}

}

