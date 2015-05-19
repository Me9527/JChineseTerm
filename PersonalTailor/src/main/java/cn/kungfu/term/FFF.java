package cn.kungfu.term;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.sql.Connection;

import javax.swing.JPanel;

//import com.jcraft.jcterm.Configuration;
//import com.jcraft.jcterm.ConfigurationRepository;

public class FFF extends JPanel {
	
	private static final long serialVersionUID = -1998368855028962273L;
	private BufferedImage img;
	private BufferedImage background;
	private Graphics2D cursor_graphics;
	private Graphics2D graphics;
	private Color defaultbground = Color.black;
	private Color defaultfground = Color.white;
	private Color bground = Color.black;
	private Color fground = Color.white;
	private Font font;
//	private Component term_area = null;
	
	private boolean bold = false;
	private boolean underline = false;
	private boolean reverse = false;
	private int term_width = 80;
	private int term_height = 24;
	private int descent = 0;
	private int x = 0;
	private int y = 0;
	private int char_width;
	private int char_height;
	// private int line_space=0;
	private int line_space = -2;
//	private int compression = 0;
	private boolean antialiasing = true;
	
//	private Splash splash = null;
	private final Object[] colors = { Color.black, Color.red, Color.green, Color.yellow, Color.blue, Color.magenta, Color.cyan, Color.white };
	
	
	public FFF() {
		// enableEvents(AWTEvent.KEY_EVENT_MASK);
		// addKeyListener(this);
		setFont("宋体");
		setSize(getTermWidth(), getTermHeight());
//		if (splash != null)
//			splash.draw(img, getTermWidth(), getTermHeight());
//		else
		clear();
//		term_area = this;
		setPreferredSize(new Dimension(getTermWidth(), getTermHeight()));
		setSize(getTermWidth(), getTermHeight());
		setFocusable(true);
		enableInputMethods(true);
		setFocusTraversalKeysEnabled(false);
		// setOpaque(true);
	}
	
	void setFont(String fontName) {
		font = new Font( fontName, Font.PLAIN, 18 );
//		font = Font.decode(fontName);
//		font.deriveFont(28);
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D) (img.getGraphics());
		graphics.setFont(font);
		FontMetrics fo = graphics.getFontMetrics();
		descent = fo.getDescent();
		/*
		 * System.out.println(fo.getDescent());
		 * System.out.println(fo.getAscent());
		 * System.out.println(fo.getLeading());
		 * System.out.println(fo.getHeight());
		 * System.out.println(fo.getMaxAscent());
		 * System.out.println(fo.getMaxDescent());
		 * System.out.println(fo.getMaxDecent());
		 * System.out.println(fo.getMaxAdvance());
		 */
		char_width = (int) (fo.charWidth((char) '@'));
		char_height = (int) (fo.getHeight()) + (line_space * 2);
		descent += line_space;
		img.flush();
		// graphics.dispose();
		background = new BufferedImage(char_width, char_height, BufferedImage.TYPE_INT_RGB);
		Graphics2D foog = (Graphics2D) (background.getGraphics());
		foog.setColor(getBackGround());
		foog.fillRect(0, 0, char_width, char_height);
		// foog.dispose();
	}
	
	public void setSize(int w, int h) {
		super.setSize(w, h);
		BufferedImage imgOrg = img;
		if (graphics != null)
			graphics.dispose();
		int column = w / getCharWidth();
		int row = h / getCharHeight();
		term_width = column;
		term_height = row;
//		if (emulator != null)
//			emulator.reset();
		img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		graphics = (Graphics2D) (img.getGraphics());
		graphics.setFont(font);
		clear_area(0, 0, w, h);
		redraw(0, 0, w, h);
		if (imgOrg != null) {
			Shape clip = graphics.getClip();
			graphics.setClip(0, 0, getTermWidth(), getTermHeight());
			graphics.drawImage(imgOrg, 0, 0, this);
			graphics.setClip(clip);
		}
		resetCursorGraphics();
		setAntiAliasing(antialiasing);
//		if (connection != null) {	//TODO 重新协商窗口大小, telnet 31
//			connection.requestResize(this);
//		}
		if (imgOrg != null) {
			imgOrg.flush();
			imgOrg = null;
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (img != null) {
			g.drawImage(img, 0, 0, this);
		}
	}
	
	public void paint(Graphics g) {
		super.paint(g);
	}
	
	public void clear() {
		graphics.setColor(getBackGround());
		graphics.fillRect(0, 0, char_width * term_width, char_height * term_height);
		graphics.setColor(getForeGround());
	}
	
	public void setCursor(int x, int y) {
		// System.out.println("setCursor: "+x+","+y);
		this.x = x;
		this.y = y;
	}
	
	public void draw_cursor() {
		cursor_graphics.fillRect(x, y - char_height, char_width, char_height);
		repaint(x, y - char_height, char_width, char_height);
	}
	
	public void redraw(int x, int y, int width, int height) {
		repaint(x, y, width, height);
	}
	
	public void clear_area(int x1, int y1, int x2, int y2) {
		// System.out.println("clear_area: "+x1+" "+y1+" "+x2+" "+y2);
		graphics.setColor(getBackGround());
		graphics.fillRect(x1, y1, x2 - x1, y2 - y1);
		graphics.setColor(getForeGround());
	}
	
	public void scroll_area(int x, int y, int w, int h, int dx, int dy) {
		// System.out.println("scroll_area: "+x+" "+y+" "+w+" "+h+" "+dx+" "+dy);
		graphics.copyArea(x, y, w, h, dx, dy);
		repaint(x + dx, y + dy, w, h);
	}
	
	public void drawString(String str, int x, int y) {
		// clear_area(x, y, x+str.length()*char_width, y+char_height);
		// graphics.setColor(getForeGround());
		graphics.drawString(str, x, y - descent);
		if (bold)
			graphics.drawString(str, x + 1, y - descent);
		if (underline) {
			graphics.drawLine(x, y - 1, x + str.length() * char_width, y - 1);
		}
	}
	
	public void setDefaultForeGround(Object f) {
		defaultfground = toColor(f);
	}
	
	public void setDefaultBackGround(Object f) {
		defaultbground = toColor(f);
	}
	
	public void setForeGround(Object f) {
		fground = toColor(f);
		graphics.setColor(getForeGround());
	}
	
	public void setBackGround(Object b) {
		bground = toColor(b);
		Graphics2D foog = (Graphics2D) (background.getGraphics());
		foog.setColor(getBackGround());
		foog.fillRect(0, 0, char_width, char_height);
		foog.dispose();
	}
	
	private Color getForeGround() {
		if (reverse)
			return bground;
		return fground;
	}
	
	private Color getBackGround() {
		if (reverse)
			return fground;
		return bground;
	}
	
	void resetCursorGraphics() {
		if (cursor_graphics != null)
			cursor_graphics.dispose();
		cursor_graphics = (Graphics2D) (img.getGraphics());
		cursor_graphics.setColor(getForeGround());
		cursor_graphics.setXORMode(getBackGround());
	}
	
	public Object getColor(int index) {
		if (colors == null || index < 0 || colors.length <= index)
			return null;
		return colors[index];
	}
	
	public void setBold() {
		bold = true;
	}
	
	public void setUnderline() {
		underline = true;
	}
	
	public void setReverse() {
		reverse = true;
		if (graphics != null)
			graphics.setColor(getForeGround());
	}
	
	public void resetAllAttributes() {
		bold = false;
		underline = false;
		reverse = false;
		bground = defaultbground;
		fground = defaultfground;
		if (graphics != null)
			graphics.setColor(getForeGround());
	}
	
	public void setAntiAliasing(boolean foo) {
		if (graphics == null)
			return;
		antialiasing = foo;
		java.lang.Object mode = foo ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, mode);
		graphics.setRenderingHints(hints);
	}
	
	public int getTermWidth() {
		return char_width * term_width;
	}
	
	public int getTermHeight() {
		return char_height * term_height;
	}
	
	public int getCharWidth() {
		return char_width;
	}
	
	public int getCharHeight() {
		return char_height;
	}
	
	public int getColumnCount() {
		return term_width;
	}
	
	public int getRowCount() {
		return term_height;
	}
	
	static Color toColor(Object o) {
		if (o instanceof String) {
			try {
				return Color.decode(((String) o).trim());
			} catch (java.lang.NumberFormatException e) {
			}
			return Color.getColor(((String) o).trim());
		}
		if (o instanceof Color) {
			return (Color) o;
		}
		return Color.white;
	}
	
	public void start(Connection connection) {
//		this.connection = connection;
//		in = connection.getInputStream();
//		out = connection.getOutputStream();
//		InDataHandler4 inDh = new InDataHandler4(in, out);
//		emulator = new EmulatorVT100(null, null);
//		emulator.reset();
//		emulator.start();
//		if (splash != null)
//			splash.draw(img, getTermWidth(), getTermHeight());
//		else
//		clear();
//		redraw(0, 0, getTermWidth(), getTermHeight());
	}
	
}
