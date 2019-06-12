package cn.sse;
import javax.sip.*;
import java.io.*;
import java.text.ParseException;

public class TextClientConsole implements SipMessageListener {
	private static String 	hostIP = "127.0.0.1";
	private static String 	chatRoomAddress = "ChatRoom@127.0.0.1:5050";
	private static SipLayerFacade sipLayer;
	private static BufferedReader keyboard;
	
	// Launch the application.
	public static void main(String[] args) throws ParseException, InvalidArgumentException, SipException, IOException {
        if(args.length != 2) {
            printUsage();
            System.exit(-1);            
        }
        
		try {
		    String username = args[0];
		    int port = Integer.parseInt(args[1]);   
			sipLayer = new SipLayerFacade(username, hostIP, port);		    
        } catch (Throwable e) {
            System.out.println("Problem initializing the SIP stack.");
            e.printStackTrace();
            System.exit(-1);
        }
		keyboard = new BufferedReader(new InputStreamReader(System.in));
		
		TextClientConsole sipListener = new TextClientConsole(sipLayer);
		sipLayer.addSipMessageListener(sipListener);
		
		System.out.println("Please enter other Chatter SIP address finished by return key:");
		String otherChatterSipAddress = keyboard.readLine();
		System.out.println("Ready to chat if other side is also ready");
		System.out.println(otherChatterSipAddress);
		while(true){
			String s = keyboard.readLine();
			System.out.println(s);
			sipLayer.sendMessage(otherChatterSipAddress, s); 
		}

//			sipLayer.sendMessage(chatRoomAddress, keyboard.readLine()); 
	}

    public TextClientConsole(SipLayerFacade sip) {
        super();   
		keyboard = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("My address is sip:" + sip.getUsername() + "@" + sip.getHost() + ":" + sip.getPort());
    }
	
    private static void printUsage() {
        System.out.println("Syntax:");
        System.out.println("  java -jar textclient.jar <username> <port>");
        System.out.println("where <username> is the nickname of this user");
        System.out.println("and <port> is the port number to use. Usually 5060 if not used by another process.");
        System.out.println("Example:");
        System.out.println("  java -jar TextClientConsole.jar snoopy71 5061");
    }
    
    // implementation of interface methods defined in MessageProcessor
    public void processReceivedMessage(String sender, String message) {
        System.out.println("From " + sender + ": " + message + "\n");
    }

    public void processError(String errorMessage) {
    	System.out.println("ERROR: " + errorMessage + "\n");
    }
    
    public void processInfo(String infoMessage) {
    	System.out.println( infoMessage + "\n");
    }
}
