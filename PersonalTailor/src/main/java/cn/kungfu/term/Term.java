package cn.kungfu.term;

public interface Term {
	
	public int getTermWidth();
	
	public int getTermHeight();
	
	public int getRowCount();
	
	public int getColumnCount();
	
	public int getCharWidth();
	
	public int getCharHeight();
	
	public void setCursor(int x, int y);
	
	public void clear();
	
	public void draw_cursor();
	
	public void redraw(int x, int y, int width, int height);
	
	public void clear_area(int x1, int y1, int x2, int y2);
	
	public void scroll_area(int x, int y, int w, int h, int dx, int dy);
	
	public void drawBytes(byte[] buf, int s, int len, int x, int y);
	
	public void drawString(String str, int x, int y);
	
	public void setDefaultForeGround(Object foreground);
	
	public void setDefaultBackGround(Object background);
	
	public void setForeGround(Object foreground);
	
	public void setBackGround(Object background);
	
	public void setBold();
	
	public void setUnderline();
	
	public void setReverse();
	
	public void resetAllAttributes();
	
	public void beep();
	
	public Object getColor(int index);
	
	// void redraw();
	// void start(Connection connection);
	
}
