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
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.JPanel;

public class TermPanel33 extends JPanel {
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
	private int lastXSelectDirection = 0;
	private int currentYSelectDirection = 0;
	private int lastYSelectDirection = 0;
//	private int resizeFlag = 0;
//	private Component term_area = null;
	
	private int charContentIdx = 0;
	private char charContent[] = null;
	private char[]  displayBuffer =  new char[16384];
	private int bufferIndex = 0;
	private int[][] bufferMap = new int[8192][];
	private final int CHINESE_MASK = -1;
	private final int NULL_MASK = -2;
	
	public TermPanel33() {
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
        	selectCurrY = e.getY()/char_height;
        	if(selectCurrX >= term_width )	selectCurrX = term_width - 1;
        	if(selectCurrX < 0  )	selectCurrX = 0;
        	if(selectCurrY > currRow )	selectCurrY = currRow;
        	if(selectCurrY < 0  )	selectCurrY = 0;
        	
        	if(selectCurrX < lastXSelectDirection){
        		unSelectChars() ;
//        		System.out.println("selectCurrX="+selectCurrX + ", lastXSelectDirection=" + lastXSelectDirection);
        	}else
            	selectChars();

        	lastYSelectDirection = selectCurrY;
            repaint();
        }
    }
    
	private void unSelectChars() {
		int chIdx = bufferMap[selectCurrY][lastXSelectDirection];
		if(CHINESE_MASK == chIdx)
			return;
		char ch = displayBuffer[chIdx];
		
		if (0x20 <= ch && ch < 0x7f) {
			grp.setColor(bground);
	    	grp.fillRect(lastXSelectDirection * char_width, selectCurrY * char_height + descent + descent, char_width, char_height + descent);
	    	
	    	grp.setColor(fground);
	    	grp.drawChars(displayBuffer, chIdx, 1, lastXSelectDirection * char_width, (selectCurrY + 1) * char_height);
//	    	selected.append(ch);
//	    	System.out.println("ascii="+ch);
		}else{
	    	grp.setColor(bground);
	    	grp.fillRect((lastXSelectDirection -1)* char_width, selectCurrY * char_height + descent + descent, char_width + char_width, char_height + descent);
	    	grp.setColor(fground);
	    	grp.drawChars(displayBuffer, chIdx, 1, (lastXSelectDirection-1) * char_width, (selectCurrY + 1) * char_height);
//	    	selected.append(ch);
//	    	System.out.println("chinese="+ch);
		}
    }
	
	private void selectChars() {
		int bDisplayBufferIdx = -1;
		int eDisplayBufferIdx = -1;
		
		if(selectCurrY == lastYSelectDirection){ 		//框选一行
			if(selectCurrX == lastXSelectDirection){
//				System.out.println("selectCurrY == selectBeginY && selectCurrX == lastXSelectDirection");
				return;
			}else if (selectCurrX > lastXSelectDirection){	//同一行向->选择
//				bDisplayBufferIdx = (CHINESE_MASK == bufferMap[selectCurrY][lastXSelectDirection])? bufferMap[selectCurrY][lastXSelectDirection+1] : bufferMap[selectCurrY][lastXSelectDirection];
//				eDisplayBufferIdx = (CHINESE_MASK == bufferMap[selectCurrY][selectCurrX])? bufferMap[selectCurrY][selectCurrX-1] : bufferMap[selectCurrY][selectCurrX];
				drawSelectedaaa();
			}else {									//同一行向<-选择
				
			}
		}else if (selectCurrY > lastYSelectDirection){	//向下框选多行
			//处理起始行
			bDisplayBufferIdx = (CHINESE_MASK == bufferMap[lastYSelectDirection][selectCurrX])? bufferMap[lastYSelectDirection][selectCurrX+1] : bufferMap[lastYSelectDirection][selectCurrX];
			eDisplayBufferIdx = (CHINESE_MASK == bufferMap[lastYSelectDirection][term_width - 1])? bufferMap[lastYSelectDirection][selectCurrX-1] : bufferMap[lastYSelectDirection][term_width - 1];
//			drawSelectedBackGround(bDisplayBufferIdx, eDisplayBufferIdx, selectCurrX, term_width - 1);
			//选中中间各行
			//处理截至行
			
		}else{									//向上框选多行
			
		}
    }
	
	private void drawSelectedaaa(){
		int length = 0;
		int end = (CHINESE_MASK == bufferMap[lastYSelectDirection][selectCurrX]) ? selectCurrX-1 : selectCurrX;
//		end = (NULL_MASK == bufferMap[lastYSelectDirection][selectCurrX]) ? term_width - 1 : selectCurrX;
		for(int i=lastXSelectDirection; i<end; i++){
			length = length + ((CHINESE_MASK == bufferMap[lastYSelectDirection][i]) ? 0 : 1);
		}
		
		if( length > 0){
			grp.setColor(fground);
	    	grp.fillRect(lastXSelectDirection * char_width,  lastYSelectDirection * char_height + descent, length * char_width, char_height + descent);
	    	
//	    	System.out.println("bDisplayBufferIdx" + bDisplayBufferIdx + ",eDisplayBufferIdx-bDisplayBufferIdx+1=" + (eDisplayBufferIdx-bDisplayBufferIdx+1));
	    	grp.setColor(bground);
	    	grp.drawChars(displayBuffer, bufferMap[lastYSelectDirection][lastXSelectDirection], length, lastXSelectDirection * char_width, (lastYSelectDirection+ 1) * char_height);
        	lastXSelectDirection = (CHINESE_MASK == bufferMap[lastYSelectDirection][selectCurrX]) ? selectCurrX-1 : selectCurrX;
		}
		
		
//		if(CHINESE_MASK == bufferMap[lastYSelectDirection][selectCurrX]){
//			for(int i=bufferMap[lastYSelectDirection][lastXSelectDirection]; i<bufferMap[lastYSelectDirection][selectCurrX]; i++){
//				
//			}
//		}
//		if(NULL_MASK == bufferMap[lastYSelectDirection][selectCurrX])
//			return;
//		int bDisplayBufferIdx = bufferMap[lastYSelectDirection][lastXSelectDirection];
//		
//		int eDisplayBufferIdx = bufferMap[selectCurrY][selectCurrX];
//		if(CHINESE_MASK == bufferMap[selectCurrY][selectCurrX])
//			eDisplayBufferIdx = bufferMap[selectCurrY][--selectCurrX];
//		if(NULL_MASK == bufferMap[selectCurrY][selectCurrX])
//			eDisplayBufferIdx = bufferMap[selectCurrY][--selectCurrX];
//		
//		char uTF8Char = displayBuffer[bDisplayBufferIdx];
////		int b = -1, e = -1;
//		lastXSelectDirection = (0x20 <= uTF8Char && uTF8Char < 0x7f) ? lastXSelectDirection : (lastXSelectDirection - 1) < 0 ? 0 : lastXSelectDirection - 1;
//		uTF8Char = displayBuffer[eDisplayBufferIdx];
//		selectCurrX = (0x20 <= uTF8Char && uTF8Char < 0x7f) ? selectCurrX : selectCurrX - 1;
//		
//		int t = 1;
//		if(selectCurrX == lastXSelectDirection && (0x20 > uTF8Char && uTF8Char >= 0x7f));
//		else t = selectCurrX - lastXSelectDirection;
//		
//		grp.setColor(fground);
//    	grp.fillRect(lastXSelectDirection * char_width,  lastYSelectDirection * char_height + descent, t * char_width, char_height + descent);
//    	
////    	System.out.println("bDisplayBufferIdx" + bDisplayBufferIdx + ",eDisplayBufferIdx-bDisplayBufferIdx+1=" + (eDisplayBufferIdx-bDisplayBufferIdx+1));
//    	grp.setColor(bground);
//    	grp.drawChars(displayBuffer, bDisplayBufferIdx, (eDisplayBufferIdx-bDisplayBufferIdx) <= 0 ? 1 : eDisplayBufferIdx-bDisplayBufferIdx, lastXSelectDirection * char_width, (lastYSelectDirection+ 1) * char_height);
//    	
	}
	
	private void drawSelectedBackGround(int bDisplayBufferIdx, int eDisplayBufferIdx, int lastX, int currX){
		if(NULL_MASK == eDisplayBufferIdx)	{
			eDisplayBufferIdx = eDisplayBufferIdx - 1;
			currX = currX - 1;
		}
		
		char utfChar = displayBuffer[bDisplayBufferIdx];
		int b = -1, e = -1;
		if (0x20 <= utfChar && utfChar < 0x7f) {	//ASCII Char, 0x20 = 空格(space), 0x7f = 删除(delete)
			b = lastX;
		}else{	//处理中文字符
			b = (CHINESE_MASK == bufferMap[selectCurrY][lastX])? lastX : (lastX-1) < 0 ? 0 : lastX-1;
		}
		
		utfChar = displayBuffer[eDisplayBufferIdx];
		if (0x20 < utfChar && utfChar < 0x7f) {	//ASCII Char, 0x20 = 空格(space), 0x7f = 删除(delete)
			e = currX;
		}else{	//处理中文字符
			e = (CHINESE_MASK != bufferMap[selectCurrY][currX])? currX : (currX-1) < 0 ? 0 : currX-1;
		}
		
		grp.setColor(fground);
    	grp.fillRect(b * char_width,  lastYSelectDirection * char_height + descent, (e - b + 1) * char_width, char_height + descent);
    	
//    	System.out.println("bDisplayBufferIdx" + bDisplayBufferIdx + ",eDisplayBufferIdx-bDisplayBufferIdx+1=" + (eDisplayBufferIdx-bDisplayBufferIdx+1));
    	grp.setColor(bground);
    	grp.drawChars(displayBuffer, bDisplayBufferIdx, (eDisplayBufferIdx-bDisplayBufferIdx+1) < 0 ? 0 : eDisplayBufferIdx-bDisplayBufferIdx+1, b * char_width, (lastYSelectDirection+ 1) * char_height);
		
	}
	
	private void drawSelected(int bDisplayBufferIdx, int bDisplayCount, int x, int y, boolean isASCII){
		if(isASCII){
			grp.setColor(fground);
	    	grp.fillRect(x * char_width, y * char_height + descent, char_width * bDisplayCount, char_height + descent);
	    	
	    	grp.setColor(bground);
	    	grp.drawChars(displayBuffer, bDisplayBufferIdx, bDisplayCount, x * char_width, (y + 1) * char_height);
		}else{
			grp.setColor(fground);
	    	grp.fillRect(x * char_width, y * char_height + descent, char_width * (bDisplayCount+1), char_height + descent);
	    	
	    	grp.setColor(bground);
	    	grp.drawChars(displayBuffer, bDisplayBufferIdx, bDisplayCount, x * char_width, (y + 1) * char_height);
		}

//		
//		if (0x20 <= ch && ch < 0x7f) {
//			grp.setColor(fground);
//	    	grp.fillRect(selectCurrX * char_width, selectCurrY * char_height + descent + descent, char_width, char_height + descent);
//	    	
//	    	grp.setColor(bground);
//	    	grp.drawChars(displayBuffer, chIdx, 1, selectCurrX * char_width, (selectCurrY + 1) * char_height);
////	    	selected.append(ch);
////	    	System.out.println("ascii="+ch);
//		}else{
//	    	grp.setColor(fground);
//	    	grp.fillRect((selectCurrX -1)* char_width, selectCurrY * char_height + descent + descent, char_width + char_width, char_height + descent);
//	    	grp.setColor(bground);
//	    	grp.drawChars(displayBuffer, chIdx, 1, (selectCurrX-1) * char_width, (selectCurrY + 1) * char_height);
////	    	selected.append(ch);
////	    	System.out.println("chinese="+ch);
//		}
	}
	
	
    private class SelectionMouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {

        	selectBeginX = e.getX()/char_width;
        	selectBeginY = e.getY()/char_height;
        	
        	lastXSelectDirection = selectBeginX;
        	lastYSelectDirection = selectBeginY;
        	
        }
        
		public void mouseReleased(MouseEvent e) {
        	selectEndX = e.getX()/char_width;
        	selectEndY = e.getY()/char_height;
        	if( selectBeginX > selectEndX )
        		return;
			int bDisplayBufferIdx = (CHINESE_MASK == bufferMap[selectCurrY][selectBeginX])? bufferMap[selectCurrY][selectBeginX+1] : bufferMap[selectCurrY][selectBeginX];
			int eDisplayBufferIdx = (CHINESE_MASK == bufferMap[selectCurrY][selectCurrX])? bufferMap[selectCurrY][selectCurrX-1] : bufferMap[selectCurrY][selectCurrX];
			if(bDisplayBufferIdx < 0 ) 	bDisplayBufferIdx = 0;
			if(eDisplayBufferIdx < 0 ) 	eDisplayBufferIdx = 0;
        	System.out.println("rs = " + new String(displayBuffer, bDisplayBufferIdx,  (eDisplayBufferIdx - bDisplayBufferIdx) < 0 ? 0 : eDisplayBufferIdx - bDisplayBufferIdx));
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
		font = new Font( fontName, Font.PLAIN, 14 );
		img = new BufferedImage(600, 400, BufferedImage.TYPE_INT_RGB);
		grp = (Graphics2D) (img.getGraphics());
		grp.setFont(font);
		grp.setBackground(bground);
		grp.setColor(fground);
//		setAntiAliasing(true);
		FontMetrics fo = grp.getFontMetrics();
		char_width = (fo.charWidth((char) '@'));
		char_height = (fo.getHeight());
		descent = fo.getDescent();
		aescent = fo.getAscent();
		
		term_width = 500/char_width;		//真是列宽为term_width - 1 个字符.
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

}
