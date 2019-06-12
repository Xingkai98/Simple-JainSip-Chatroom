package cn.sse;
import java.io.BufferedReader;
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
	private static String updateNotice = "UPD";
	private static String AllNotice = "ALL";
	private static String SomeNotice = "SOM";
	private static String P2pNotice = "P2P";
	
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
	
	@Override
	public void processReceivedMessage(String sender, String message) {
		// TODO Auto-generated method stub
		//收到消息
		String senderSipAddress = getSipAddress(sender);
		System.out.println("[Received message from " + sender + "]: " + message);
		//System.out.println(senderSipAddress);
		
		//客户端向服务器发送：“作者地址&地址1&地址2&消息内容”
		//服务器收到消息后立刻给这些地址转发消息内容。
		//服务器向客户端转发：“消息类型（全体、私聊、部分）&消息作者&消息内容”
		
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
			String onlineListString = updateNotice + "&";
			int i=0;
			while(i<clientList.size()-1) {
				onlineListString += clientList.get(i) + "&";
				i++;
			}
			onlineListString += clientList.get(clientList.size()-1);
			for(int j=0;j<clientList.size();j++) {
				try {
					sipLayer.sendMessage(clientList.get(j), onlineListString);
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
		
		//若非新成员，则按照格式转发消息
		//服务器向客户端转发：“消息类型（全体、私聊、部分）&消息作者&消息内容”
		//先默认为全体
		else {
			String[] list = message.split("&");
			String toSend = AllNotice + "&" + list[0] + "&" + list[list.length-1];
			
			//客户端向服务器发送：“作者地址&地址1&地址2&消息内容”，所以目标地址在1到length-2的下标区间
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
	@Override
	public void processError(String errorMessage) {
		// TODO Auto-generated method stub
		System.out.println("Server error.");
	}
	@Override
	public void processInfo(String infoMessage) {
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args) {
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
		
		//添加监听
		sipLayer.addSipMessageListener(server);
	}
}
