package cn.sse;
import java.io.BufferedReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.*;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;

public class Server implements SipMessageListener{
	private static SipLayerFacade sipLayer;
	//这两个list存储的是sip地址
	private static List<String> clientList;
	private static List<String> toSendList;
	private static String grantPermission = "********";
	private static String updateOnlineList = "&&&&&&&&";
	private static String UPDATE_NOTICE = "UPD";
	private static String ALL_NOTICE = "ALL";
	private static String SOME_NOTICE = "SOM";
	private static String P2P_NOTICE = "P2P";
	
	public Server() {
		clientList = new ArrayList<String>();
		toSendList = new ArrayList<String>();
	}
	
	//获取客户列表
	public List<String> getClientList(){
		return clientList;
	}
	
	//从发来的消息里解析sip地址，如"Alias" <sip:Alias@127.0.0.1:8090>
	public String getSipAddress(String sender) {
		String temp = sender.substring(sender.indexOf('<'), sender.indexOf('>'));
		temp = temp.substring(temp.indexOf(':')+1);
		return temp;
	}
	
	void copyToAll(String s) {
		try {
			for(int j=0;j<clientList.size();j++) {
				sipLayer.sendMessage(clientList.get(j), s);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void processReceivedMessage(String sender, String message) {
		// TODO Auto-generated method stub
		//收到消息
		String senderSipAddress = getSipAddress(sender);
		System.out.println("[Received message from " + sender + "]: " + message);
		//System.out.println(senderSipAddress);
		
		
		//若为新登陆成员并且消息为grantPermission，则加入在线列表
		boolean isNew = true;
		for(int i=0;i<clientList.size();i++) {
			if(clientList.get(i).equals(senderSipAddress)) {
				isNew = false;
			}
		}
		System.out.println("isNew: "+ isNew);
		//若更新了在线列表，则对所有在线用户发消息更新在线列表
		if(isNew) {
			clientList.add(senderSipAddress);
			String onlineListString = UPDATE_NOTICE + "&";
			int i=0;
			while(i<clientList.size()-1) {
				onlineListString += clientList.get(i) + "&";
				i++;
			}
			onlineListString += clientList.get(clientList.size()-1);
			copyToAll(onlineListString);
		}
		
		//服务器收到客户端发来的消息list：“作者地址&地址1&地址2&消息内容”
		//若未指定收件人，则消息为”作者地址&消息内容“
		//服务器向客户端转发：“消息类型（全体、私聊、部分）&消息作者&消息内容”
		else {
			String[] list = message.split("&");
			//若客户端发来的消息list长度为2（”作者地址&消息内容“），说明是群发
			//服务器端转发格式为 ALL_NOTICE&作者地址&消息内容
			//若客户端发来的消息list长度为3，说明是私发，因为只有一个收信人
			String toSend = "";
			if(list.length == 2) {
				toSend = ALL_NOTICE + "&" + list[0] + "&" + list[list.length-1];
				copyToAll(toSend);
				return;
			}
			else if(list.length == 3) {
				toSend = P2P_NOTICE + "&" + list[0] + "&" + list[list.length-1];
				try {
					sipLayer.sendMessage(list[1], toSend);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SipException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//copyToAll(toSend);
				return;
			}
			else {
				toSend = SOME_NOTICE + "&" + list[0] + "&" + list[list.length-1];
				//服务器接收到的消息为“作者地址&地址1&地址2...&消息内容”，所以目标地址在1到length-2的下标区间
				for(int i=1;i<list.length-1;i++) {
					try {
						sipLayer.sendMessage(list[i], toSend);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SipException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
		
		
	}
	@Override
	public void processError(String errorMessage) {
		// TODO Auto-generated method stub
		System.out.println("Server error.");
	}
	@Override
	public void processInfo(String infoMessage) {
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args) throws Exception {
		try {
		    String username = "Server";
		    String hostIP = "127.0.0.1";
		    int port = 8080;   
			sipLayer = new SipLayerFacade(username, hostIP, port);	

        } catch (Throwable e) {
            System.out.println("Problem initializing the SIP stack.");
            e.printStackTrace();
            System.exit(-1);
        }
		Server server = new Server();
		System.out.println("Server started.");
		System.out.println("local ip: " + InetAddress.getLocalHost().getHostAddress());
		
		//添加监听
		sipLayer.addSipMessageListener(server);
	}
}
