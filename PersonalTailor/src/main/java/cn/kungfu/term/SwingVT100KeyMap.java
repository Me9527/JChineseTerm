package cn.kungfu.term;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class SwingVT100KeyMap implements KeyListener {
	
	// public void processKeyEvent(KeyEvent e) {
	// // System.out.println(e);
	// int id = e.getID();
	// if (id == KeyEvent.KEY_PRESSED) {
	// keyPressed(e);
	// } else if (id == KeyEvent.KEY_RELEASED) {
	// /* keyReleased(e); */
	// } else if (id == KeyEvent.KEY_TYPED) {
	// keyTyped(e);/* keyTyped(e); */
	// }
	// e.consume(); // ??
	// }
	
	private static final byte[] ENTER = { (byte) 0x0d };
	private static final byte[] UP = { (byte) 0x1b, (byte) 0x4f, (byte) 0x41 };
	private static final byte[] DOWN = { (byte) 0x1b, (byte) 0x4f, (byte) 0x42 };
	private static final byte[] RIGHT = { (byte) 0x1b, (byte) /* 0x5b */0x4f, (byte) 0x43 };
	private static final byte[] LEFT = { (byte) 0x1b, (byte) /* 0x5b */0x4f, (byte) 0x44 };
	private static final byte[] F1 = { (byte) 0x1b, (byte) 0x4f, (byte) 'P' };
	private static final byte[] F2 = { (byte) 0x1b, (byte) 0x4f, (byte) 'Q' };
	private static final byte[] F3 = { (byte) 0x1b, (byte) 0x4f, (byte) 'R' };
	private static final byte[] F4 = { (byte) 0x1b, (byte) 0x4f, (byte) 'S' };
	private static final byte[] F5 = { (byte) 0x1b, (byte) 0x4f, (byte) 't' };
	private static final byte[] F6 = { (byte) 0x1b, (byte) 0x4f, (byte) 'u' };
	private static final byte[] F7 = { (byte) 0x1b, (byte) 0x4f, (byte) 'v' };
	private static final byte[] F8 = { (byte) 0x1b, (byte) 0x4f, (byte) 'I' };
	private static final byte[] F9 = { (byte) 0x1b, (byte) 0x4f, (byte) 'w' };
	private static final byte[] F10 = { (byte) 0x1b, (byte) 0x4f, (byte) 'x' };
	private static final byte[] TAB = { (byte) 0x09 };
	
	private final byte[] obuffer = new byte[3];
	private OutputStream out = null;
	
	public void keyPressed(KeyEvent event) {
		byte[] code = null;
		int keycode = event.getKeyCode();
		
		switch (keycode) {
		case KeyEvent.VK_CONTROL:
		case KeyEvent.VK_SHIFT:
		case KeyEvent.VK_ALT:
		case KeyEvent.VK_CAPS_LOCK:
			return;
		case KeyEvent.VK_ENTER:
			code = ENTER;
			break;
		case KeyEvent.VK_UP:
			code = UP;
			break;
		case KeyEvent.VK_DOWN:
			code = DOWN;
			break;
		case KeyEvent.VK_RIGHT:
			code = RIGHT;
			break;
		case KeyEvent.VK_LEFT:
			code = LEFT;
			break;
		case KeyEvent.VK_F1:
			code = F1;
			break;
		case KeyEvent.VK_F2:
			code = F2;
			break;
		case KeyEvent.VK_F3:
			code = F3;
			break;
		case KeyEvent.VK_F4:
			code = F4;
			break;
		case KeyEvent.VK_F5:
			code = F5;
			break;
		case KeyEvent.VK_F6:
			code = F6;
			break;
		case KeyEvent.VK_F7:
			code = F7;
			break;
		case KeyEvent.VK_F8:
			code = F8;
			break;
		case KeyEvent.VK_F9:
			code = F9;
			break;
		case KeyEvent.VK_F10:
			code = F10;
			break;
		case KeyEvent.VK_TAB:
			code = TAB;
			break;
		}
		
		if (code != null) {
			writeOut(code, 0, code.length);
			return;
		}
		char keychar = event.getKeyChar();
		if ((keychar & 0xff00) == 0) {
			obuffer[0] = (byte) (event.getKeyChar());
			writeOut(obuffer, 0, 1);
		}
	}
	
	public void keyReleased(KeyEvent event) {
		
	}
	
	public void keyTyped(KeyEvent event) {
		char keychar = event.getKeyChar();
		if ((keychar & 0xff00) != 0) {
			char[] foo = new char[1];
			foo[0] = keychar;
			try {
				byte[] goo = new String(foo).getBytes("GBK"); // //EUC-JP
				writeOut(goo, 0, goo.length);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void writeOut(byte[] content, int begin, int length) {
		try {
			out.write(content, begin, length);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
