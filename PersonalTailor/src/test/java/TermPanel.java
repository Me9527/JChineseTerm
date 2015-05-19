

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class TermPanel extends JPanel {
	private Color bground = Color.black;
	private Color fground = Color.yellow;
	private Color curBground = Color.gray;
	
	private Font font;
	private String fontName = "宋体";
	private boolean antialiasing = true;
	private Graphics2D grp;
	private BufferedImage img;
	
	private int term_width = 80;
	private int term_height = 24;
	private int currColumn = 0;
	
//	private int descent = 0;
	private int x = 0;
	private int y = 0;
	private int char_width;
	private int char_height;
	private int resizeFlag = 0;
	private Component term_area = null;
	
	public TermPanel() {
		term_area = this;
	}
	
	public void init(){
		setFont();
        addMouseListener(new SelectionMouseListener());
        addMouseMotionListener(new SelectionMouseMotionListener());
//        this.setAutoscrolls(true);
	}
	
	
    private class SelectionMouseMotionListener extends MouseMotionAdapter {
        public void mouseDragged(MouseEvent e) {
        	grp.setColor(curBground);
        	grp.drawOval(e.getX(), e.getY(), 10, 10);

            repaint();
        }
    }

    private class SelectionMouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
        	grp.setColor(curBground);
        	grp.drawOval(e.getX(), e.getY(), 10, 10);

            repaint();
        }
    }

    public void mouseReleased(MouseEvent e) {
    	
    	//TextAttribute 
    }
    
    
//	public void drawString(String str, int x, int y) {
//		// clear_area(x, y, x+str.length()*char_width, y+char_height);
//		// graphics.setColor(getForeGround());
//		grp.drawString(str, x, y);
//	}
//	
//	public void drawString(String str) {
//		
//		grp.drawString(str, x, y);
//		
//	}
	
	private int charContentIdx = 0;
	private char charContent[] = null;
	
	public void drawChars(char[] chars) {
		charContent = chars;
		charContentIdx = 0;
		drawChars();
	}
	
	private void drawChars() {
		int offset = charContentIdx;
		for(; charContentIdx < charContent.length; charContentIdx++){
			char ascii = charContent[charContentIdx];
			if (0x20 <= ascii && ascii < 0x7f) {	//ASCII, 0x20 = 空格(space), 0x7f = 删除(delete)
				currColumn = currColumn + 1;
				if(currColumn == term_width){
//					y = begColumn * char_width;		
					grp.drawChars(charContent, offset, charContentIdx - offset, x, y);
					x = 0;	currColumn = 0;		
					y = y + char_height;
					drawChars();
					return;
				}else
					continue;
			}else{
				currColumn = currColumn + 2;
				if(currColumn >= term_width){
					grp.drawChars(charContent, offset, charContentIdx - offset, x, y);
					x = 0;	currColumn = 0;		
					y = y + char_height;
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
		img = new BufferedImage(800,600, BufferedImage.TYPE_INT_RGB);
		grp = (Graphics2D) (img.getGraphics());
		grp.setFont(font);
		grp.setBackground(bground);
		grp.setColor(fground);
		FontMetrics fo = grp.getFontMetrics();
		char_width = (fo.charWidth((char) '@'));
		char_height = (fo.getHeight());
		
		term_width = 800/char_width;
		term_width = term_width - 1;
//		x = x + char_height + char_height;
		y = y + char_height;
		
	}
	
	public void newLine() {
		x = 0;
		currColumn = 0;	
		y = y + char_height;
	}
	
//	public void setSize(int w, int h) {
//		super.setSize(w, h);
//		img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//		grp = (Graphics2D) (img.getGraphics());
//		grp.setFont(font);
//		grp.setBackground(bground);
//		grp.setColor(fground);
//		
//		BufferedImage imgOrg = img;
//		if (grp != null)
//			grp.dispose();
////		int column = w / getCharWidth();
////		int row = h / getCharHeight();
////		term_width = column;
////		term_height = row;
////		if (emulator != null)
////			emulator.reset();
//		img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//		grp = (Graphics2D) (img.getGraphics());
//		grp.setFont(font);
//		clear_area(0, 0, w, h);
//		redraw(0, 0, w, h);
//		if (imgOrg != null) {
//			Shape clip = grp.getClip();
//			grp.setClip(0, 0, 400, 200);	//getTermWidth(), getTermHeight()
//			grp.drawImage(imgOrg, 0, 0, term_area);
//			grp.setClip(clip);
//		}
////		resetCursorGraphics();
////		setAntiAliasing(antialiasing);
////		if (connection != null) {
////			connection.requestResize(this);
////		}
//		if (imgOrg != null) {
//			imgOrg.flush();
//			imgOrg = null;
//		}
//	}
	
//	public void clear_area(int x1, int y1, int x2, int y2) {
//		grp.setColor(bground);
//		grp.fillRect(x1, y1, x2 - x1, y2 - y1);
//		grp.setColor(fground);
//	}
//	
//	public void redraw(int x, int y, int width, int height) {
//		repaint(x, y, width, height);
//	}
//	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (img != null) {
			g.drawImage(img, 0, 0, this);
		}
	}
	
	
//    public void paint(Graphics g) {
//
//    	grp = (Graphics2D) g;
//    }
    
}
