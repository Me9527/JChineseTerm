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
import java.nio.CharBuffer;

import javax.swing.JPanel;

public class TermPanel extends JPanel {
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
	
//	private int resizeFlag = 0;
//	private Component term_area = null;
	
	private int charContentIdx = 0;
	private char charContent[] = null;
//	private int charMap[] = new int[8192];
	private CharBuffer displayBuffer = CharBuffer.allocate(8192);
	private char[][] displayBuffer2 = new char[8192][];
	
	public TermPanel() {
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
//        	System.out.println("drag x=" + e.getX() + ", charX = " + (e.getX()/char_width));
//        	System.out.println("drag y=" + e.getY() + ", charY = " + (e.getY()/char_height));
        	
        	selectCurrX = e.getX()/char_width;
        	selectCurrY = e.getY()/char_height;
//        	System.out.println("drag x=" + e.getX() + ", charX = " + selectBeginX);
//        	System.out.println("drag y=" + e.getY() + ", charY = " + selectBeginY);
        	
        	selectChars();


            repaint();
        }
    }

	private void selectChars() {
		char ch = displayBuffer.get(selectCurrX);
		if (0x20 <= ch && ch < 0x7f) {
	    	grp.setColor(fground);
	    	grp.fillRect(selectCurrX * char_width, selectCurrY * char_height + descent + descent, char_width, char_height + descent);
	    	grp.setColor(bground);
	    	grp.drawChars(displayBuffer.array(), selectCurrX, 1, selectCurrX * char_width, (selectCurrY + 1) * char_height);
		}else{
	    	grp.setColor(fground);
	    	grp.fillRect(selectCurrX * char_width, selectCurrY * char_height + descent + descent, char_width, char_height + descent);
	    	grp.setColor(bground);
	    	grp.drawChars(displayBuffer.array(), selectCurrX, 1, selectCurrX * char_width, (selectCurrY + 1) * char_height);
		}

    }
    
    private class SelectionMouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
        	if(selectEndX >=0 ){
        		for(int i=selectBeginX; i<selectEndX; i++) {
        	    	grp.setColor(bground);
        	    	grp.fillRect(i * char_width, selectBeginY * char_height + descent + descent, char_width, char_height + descent);
        	    	grp.setColor(fground);
        	    	grp.drawChars(charContent, 28, 1, i * char_width, (selectBeginY + 1) * char_height);
        		}
        		selectEndX = -1;	selectEndY = -1;
        	}
        	
        	selectBeginX = e.getX()/char_width;
//        	if((e.getX() % char_width) != 0)
//        		selectBeginX = 0 == selectBeginX ? 0 : selectBeginX + 1;
        	System.out.println("char_width=" + char_width + ", getX=" + e.getX() + ", selectBeginX=" + selectBeginX + ", Mod=" + e.getX() % char_width);
        	
        	selectBeginY = e.getY()/char_height;
//        	if((e.getY() % char_height) != 0)
//        		selectBeginY = 0 == selectBeginY ? 0 : selectBeginY + 1;
        	System.out.println("char_height=" + char_height + ", getY=" + e.getY() + ", selectBeginY=" + selectBeginY + ", Mod=" + e.getY() % char_height);
        	
//            repaint();
        }
        
		public void mouseReleased(MouseEvent e) {
        	selectEndX = e.getX()/char_width;
        	selectEndY = e.getY()/char_height;
        	System.out.println("rs = " + selectEndX);
//        	grp.setColor(fground);
//        	grp.setBackground(bground);
//        	grp.drawChars(charContent, 28, 1, selectBeginX * char_width, (selectBeginY + 1) * char_height);
        }
    }
	
	public void drawChars(char[] chars) {
		charContent = chars;
		displayBuffer.put(chars);
		charContentIdx = 0;
		if(0 == currColumn){
			char[] t = new char[term_width];
			displayBuffer2[currRow] = t;
		}
		drawChars();

	}
	
	private void drawChars() {
		char[] oneRow = displayBuffer2[currRow];
		int offset = charContentIdx;
		for(; charContentIdx < charContent.length; charContentIdx++){
			char ascii = charContent[charContentIdx];
			if (0x20 <= ascii && ascii < 0x7f) {	//ASCII, 0x20 = 空格(space), 0x7f = 删除(delete)
				if(currColumn < term_width - 1)
					oneRow[currColumn] = ascii;
				currColumn = currColumn + 1;
				if(currColumn == term_width){
//					y = begColumn * char_width;		
					grp.drawChars(charContent, offset, charContentIdx - offset, x, y);
					x = 0;	currColumn = 0;		
					y = y + char_height;
					oneRow = new char[term_width];
					displayBuffer2[++currRow] = oneRow;
					drawChars();
					return;
				}else
					continue;
			}else{
				if(currColumn < term_width - 1){
					oneRow[currColumn] = ' ';
					oneRow[currColumn + 1] = ascii;
				}
				currColumn = currColumn + 2;
				if(currColumn > term_width){
					grp.drawChars(charContent, offset, charContentIdx - offset, x, y);
					x = 0;	currColumn = 0;		
					y = y + char_height;
					oneRow = new char[term_width];
					displayBuffer2[++currRow] = oneRow;
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
		img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
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
		
		term_width = 630/char_width;		//真是列宽为term_width - 1 个字符.
//		term_width = term_width - 1;	//汉字占2个字符
//		if(term_width % 2 != 0)
//			term_width = term_width - 1;//奇数会显示半个汉字
//		x = x + char_height + char_height;
		y = y + char_height;
		
	}
	
	public void drawSlash() {
		boolean showCursor = true;
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
    
	
//	public void drawChars(char[] chars) {
//		charContent = chars;
//		displayBuffer.put(chars);
//		charContentIdx = 0;
//		drawChars();
//	}
//	
//	private void drawChars() {
//		int offset = charContentIdx;
//		for(; charContentIdx < charContent.length; charContentIdx++){
//			char ascii = charContent[charContentIdx];
//			if (0x20 <= ascii && ascii < 0x7f) {	//ASCII, 0x20 = 空格(space), 0x7f = 删除(delete)
//				currColumn = currColumn + 1;
//				if(currColumn == term_width){
////					y = begColumn * char_width;		
//					grp.drawChars(charContent, offset, charContentIdx - offset, x, y);
//					x = 0;	currColumn = 0;		
//					y = y + char_height;
//					drawChars();
//					return;
//				}else
//					continue;
//			}else{
//				currColumn = currColumn + 2;
//				if(currColumn > term_width){
//					grp.drawChars(charContent, offset, charContentIdx - offset, x, y);
//					x = 0;	currColumn = 0;		
//					y = y + char_height;
//					drawChars();
//					return;
//				}else
//					continue;
//			}
//		}
//		
//		if(offset < charContentIdx ){
//			grp.drawChars(charContent, offset, charContentIdx - offset, x, y);
//			x = currColumn * char_width;	
//		}
//	}
}
