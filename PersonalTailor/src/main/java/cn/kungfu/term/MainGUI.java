package cn.kungfu.term;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainGUI {
	
	private FFF term;
	
	private JFrame frame;
	
	private JTabbedPane tabbedPane;
	private JMenuItem tabComponentsItem;
	private JMenuItem scrollLayoutItem;
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				MainGUI gui = new MainGUI();
				gui.makeJFrame();
				gui.initMenu();
				gui.start();
			}
		});
		
	}
	
	public MainGUI() {

	}
	
	String s1 = "结算项目组将项目所有代码提交SVN";
	String s2 = "2015-03-09 08:57:51  INFO XmlConfigurationProvider.info(42) | Parsing configuration file [/home/bea/mike/tomcat6035/webapps/PostalSettlement-eyb/WEB-INF/classes/modules/epacket/struts-conf/struts-config.xml]";
	String s3 = "012中缺少部分中34567890 1234567890 1234567890123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ  中   缺少部分中缺少部分中缺少部分中缺少部分中缺少部缺少部分中缺缺少部分中缺缺少部分中缺分簿~!@#$%^&*()_+=[]{}|'它们都代表了需要abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN"
			+ "OPQRSTUVWXYZ中缺少部分中缺少部分中缺少abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ中缺少部分中缺少部分中缺少a";
	
	private void start() {
		String tmp = "";
//		for(int i=0; i<30; i++)
			tmp = tmp + s3;
//		tabComponentsItem.setSelected(true);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		scrollLayoutItem.setSelected(false);
		frame.setSize(new Dimension(600, 400));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		tabbedPane.removeAll();
		int tabNumber = 1;
		for (int i = 0; i < tabNumber; i++) {
			final TermPanel3 term = new TermPanel3();
//			JScrollPane scrollPane = new JScrollPane(term);
//			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
//			scrollPane.setOpaque(true); 
			
			ComponentAdapter l = new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					Component c = e.getComponent();
					Container cp = ((JFrame) c).getContentPane();
					int cw = c.getWidth();
					int ch = c.getHeight();
					int cwm = c.getWidth() - cp.getWidth();
					int chm = c.getHeight() - cp.getHeight();
					cw -= cwm;
					ch -= chm;
					term.setSize(cw, ch);
				}
			};
//			frame.addComponentListener(l);
			
			term.init();
			tabbedPane.add("Tab"+i, term);
			char[] t = tmp.toCharArray();
			term.drawChars(t);
//			term.newLine();
//			term.newLine();
//			term.drawChars(t);
//			term.newLine();
//			term.drawChars(t);
//			term.drawSlash();
			
			int h = term.getHeight();
			int w = term.getWidth();
//			String title =term "Tab " + i;
//			tabbedPane.add(title, new JLabel(title));
//			initTabComponent(i);
		}
	}
	
	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
//		tabComponentsItem = new JCheckBoxMenuItem("Use TabComponents", true);
//		tabComponentsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_MASK));
//		tabComponentsItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				for (int i = 0; i < tabbedPane.getTabCount(); i++) {
//					if (tabComponentsItem.isSelected()) {
//						initTabComponent(i);
//					} else {
//						tabbedPane.setTabComponentAt(i, null);
//					}
//				}
//			}
//		});
		scrollLayoutItem = new JCheckBoxMenuItem("Set ScrollLayout");
		scrollLayoutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK));
		scrollLayoutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tabbedPane.getTabLayoutPolicy() == JTabbedPane.WRAP_TAB_LAYOUT) {
					tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
				} else {
					tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
				}
			}
		});
		JMenuItem resetItem = new JMenuItem("Reset JTabbedPane");
		resetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_MASK));
		resetItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				start();
			}
		});
		
		JMenu optionsMenu = new JMenu("选项");
//		optionsMenu.add(tabComponentsItem);
		optionsMenu.add(scrollLayoutItem);
		optionsMenu.add(resetItem);
		menuBar.add(optionsMenu);
		frame.setJMenuBar(menuBar);
		
	}
	
//	private void initTabComponent(int i) {
//		tabbedPane.setTabComponentAt(i, new ButtonTabComponent(pane));
//	}
	
	private void makeJFrame() {
		frame = new JFrame();
		frame.setTitle("Kungfu Term"); // 私人定制
//		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		frame.add(tabbedPane);
		initMenu();
		
	}
}
