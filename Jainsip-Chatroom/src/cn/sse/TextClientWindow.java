package cn.sse;

import javax.sip.*;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.*;

import javax.swing.*;

import java.awt.font.*;
import javafx.scene.text.Font;

class MyLayout extends JFrame {

	// Online List
	List<JCheckBox> onlineListCheck;

	// Name Panel
	JLabel nameDisplay = new JLabel("您的名称为：");
	JButton btnNameChange = new JButton("更改");
	JPanel namePanel = new JPanel();

	// Chat Panel
	JTextArea chatContentDisplay = new JTextArea("");
	JScrollPane chatContentScroll = new JScrollPane(chatContentDisplay);
	JTextArea onlineListDisplay = new JTextArea("选择收信人（若不选则表示全体消息）：");
	JButton btnReset = new JButton("重置");
	JPanel onlineListPanel = new JPanel();
	JPanel chatPanel = new JPanel();

	// Send Panel
	JTextField sendContentDisplay = new JTextField("");
	JButton btnSend = new JButton("发送");
	JPanel sendPanel = new JPanel();

	MyLayout() {
		init();
		this.setTitle("聊天室");
		this.setResizable(true);
		this.setSize(960, 540);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	void init() {
		// Overall Layout
		this.setLayout(new BorderLayout(10, 5)); // 默认为0，0；水平间距10，垂直间距5

		// Data Initialization
		onlineListCheck = new ArrayList<JCheckBox>();

		// Name Panel
		namePanel.setLayout(new BorderLayout(10, 5));
		namePanel.add(nameDisplay, BorderLayout.CENTER);
		namePanel.add(btnNameChange, BorderLayout.EAST);
		this.add(namePanel, BorderLayout.NORTH);

		// Chat Panel
		chatContentDisplay.setEditable(false);
		chatPanel.setLayout(new BorderLayout(10, 5));
		
		onlineListPanel.add(onlineListDisplay);
		for (int i = 0; i < onlineListCheck.size(); i++) {
			onlineListPanel.add(onlineListCheck.get(i));
		}
		onlineListPanel.add(btnReset);
		
		chatPanel.add(chatContentScroll, BorderLayout.CENTER);
		chatPanel.add(onlineListPanel, BorderLayout.SOUTH);
		this.add(chatPanel, BorderLayout.CENTER);

		// Send Panel
		sendPanel.setLayout(new BorderLayout(10, 5));
		sendPanel.add(sendContentDisplay, BorderLayout.CENTER);
		sendPanel.add(btnSend, BorderLayout.EAST);
		this.add(sendPanel, BorderLayout.SOUTH);
	}
}


public class TextClientWindow implements SipMessageListener {
	private static String hostIP = "127.0.0.1";
	private static String serverAddress = "Server@127.0.0.1:8080";
	private static HashSet<String> clientList; 
	private static HashSet<String> toSendList;
	private static SipLayerFacade sipLayer;
	private static BufferedReader keyboard;
	private static MyLayout layout;
	private static String grantPermission = "********";
	private static String updateOnlineList = "&&&&&&&&";
	private static String updateNotice = "UPD";
	private static String AllNotice = "ALL";
	private static String SomeNotice = "SOM";
	private static String P2pNotice = "P2P";
	
	class CheckOnlineThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class EnterListener implements KeyListener{

		@Override
		public void keyPressed(KeyEvent arg0) {
			// TODO Auto-generated method stub
			if(arg0.getKeyCode() == KeyEvent.VK_ENTER && !layout.sendContentDisplay.getText().equals("")) {
				sendMessage();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class ButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String name = e.getActionCommand();
			if(name.equals("重置")) {
				//取消勾选
				for(int i=0;i<layout.onlineListCheck.size();i++) {
					if(layout.onlineListCheck.get(i).isSelected() == true) {
						layout.onlineListCheck.get(i).doClick();
					}
				}
			}
			else if(name.equals("发送")) {
				sendMessage();
			}
			else {
				
			}
		}
		
	}
	
	void sendMessage() {
		String toSend = sipLayer.getUsername() + "&";
		for(int i=0;i<layout.onlineListCheck.size();i++) {
			if(layout.onlineListCheck.get(i).isSelected()) {
				toSend += layout.onlineListCheck.get(i).getText() + "&";
			}
		}
		toSend += layout.sendContentDisplay.getText();
		layout.sendContentDisplay.setText("");
		try {
			sipLayer.sendMessage(serverAddress, toSend);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SipException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public TextClientWindow(SipLayerFacade sipLayer) {
		super();
		layout = new MyLayout();
		clientList = new HashSet<String>();
		toSendList = new HashSet<String>();
		
		layout.sendContentDisplay.addKeyListener(this.new EnterListener());
		layout.btnReset.addActionListener(this.new ButtonListener());
		layout.btnSend.addActionListener(this.new ButtonListener());
	}

	public static void main(String[] args) throws Exception {
		if(args.length != 2) {
            System.exit(-1);            
        }
		
		//建立SipLayerFacade
		try {
			String username = args[0];
			String hostIP = "127.0.0.1";
			int port = Integer.parseInt(args[1]);
			sipLayer = new SipLayerFacade(username, hostIP, port);
			System.out.println("Client Started.");
		} catch (Throwable e) {
			System.out.println("Problem initializing the SIP stack.");
			e.printStackTrace();
			System.exit(-1);
		}
		//建立窗口，建立监听（此时准备好了进行收发操作）
		TextClientWindow window = new TextClientWindow(sipLayer);
		keyboard = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
		
		sipLayer.addSipMessageListener(window);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				layout.nameDisplay.setText(layout.nameDisplay.getText() + sipLayer.getUsername());
				System.out.println("My address is sip:" + sipLayer.getUsername() + "@" + sipLayer.getHost() + ":" + sipLayer.getPort());
				layout.chatContentDisplay.setText(layout.chatContentDisplay.getText()+"My address is sip:" + sipLayer.getUsername() + "@" + sipLayer.getHost() + ":" + sipLayer.getPort());
				
			}
		});
		
		//向服务器发起请求，加入在线名单。
		sipLayer.sendMessage("Server@127.0.0.1:8080", grantPermission);
		System.out.println("Message sent.");
	}
	
	//从发来的消息里解析sip地址，如"Alias" <sip:Alias@127.0.0.1:8090>
	public String getSipAddress(String sender) {
		String temp = sender.substring(sender.indexOf('<'), sender.indexOf('>'));
		temp = temp.substring(temp.indexOf(':')+1);
		return temp;
	}

	@Override
	public void processReceivedMessage(final String sender, final String message) {
		// TODO Auto-generated method stub
		//"Server" <sip:Server@127.0.0.1:8080>
		//System.out.println(message);
		if(getSipAddress(sender).equals("Server@127.0.0.1:8080")) {
			final String[] messageList = message.split("&");
			if(messageList[0].equals(updateNotice)) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						// 更新在线列表和UI
						layout.onlineListCheck.clear();
						layout.onlineListPanel.removeAll();
						layout.onlineListPanel.add(layout.onlineListDisplay);
						for(int i=1;i<messageList.length;i++) {
							clientList.add(messageList[i]);
							JCheckBox jtemp = new JCheckBox(messageList[i]);
							layout.onlineListCheck.add(jtemp);
							layout.onlineListPanel.add(jtemp);
						}		
						layout.onlineListPanel.add(layout.btnReset);
						layout.toFront();
						layout.revalidate();
						layout.repaint();
					}

				});
			}
			else if(messageList[0].equals(AllNotice)) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						layout.chatContentDisplay.setText(layout.chatContentDisplay.getText() + "\n" + getFormattedMessage(message));
					}

				});
			}
			else if(messageList[0].equals(SomeNotice)) {
				
			}
			else if(messageList[0].equals(P2pNotice)) {
				
			}
			else {
				
			}
			
		}
		
	}
	
	public String getFormattedMessage(String message) {
		String[] list = message.split("&");
		String s = "";
		s += "[" + list[1] + "]: ";
		//s += "[To " + list[0] + "] ";
		s += list[2];
		return s;
	}
	
	@Override
	public void processError(String errorMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processInfo(String infoMessage) {
		// TODO Auto-generated method stub

	}

}
