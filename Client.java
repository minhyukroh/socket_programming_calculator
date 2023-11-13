import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	public static void main(String args[]) throws IOException {
 
		Socket socket = null; // Socket for connecting to the server
		DataInputStream in = null; // Input stream for reading data from the server
		DataOutputStream out = null; // Output stream for sending data to the server
		BufferedReader bf = null; // Buffered reader for reading input from the console(keyboard)
		
		String sendingMessage = ""; // String to be sent to the server
		String receivedMessage = ""; // String received from the server

		String IP = "localhost"; // Default value for the IP address
		String portNum; 
		int port = 8888; // Default value for the port number
		
		String fileName = "server_info.txt"; // File name to retrieve server information (ip_address, port_number)
		File file = new File(fileName);
		
		boolean isExists = file.exists();

		if(isExists) { // If the file exists
			Scanner scanner = new Scanner(file);
			IP = scanner.nextLine();
			portNum = scanner.nextLine();
			port = Integer.parseInt(portNum);
			scanner.close(); // Close the file after use	
		}
		
		try {
			socket = new Socket(IP, port); 
			System.out.println(" [ Client is connected to the server. (Exit: type \"bye\")] ");
			System.out.println(" [Exit: type \"bye\"]");
			System.out.println(" [ Format: operator(ADD/MINUS/MUL/DIV) operand1 operand2] e.g., ADD 3 2 / MUL 4 7");
			
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			bf = new BufferedReader(new InputStreamReader(System.in));

			while(true){
				receivedMessage = in.readUTF();
				if(receivedMessage.equalsIgnoreCase("full")){ // If "full" is received from the server (lowercase / uppercase), this client cannot connect.
					System.out.println("< ERROR: Already clients are full. >");
					break;
				}
    
				System.out.print("Expression : ");
				sendingMessage = bf.readLine(); // Input the expression to be sent to the server from the keyboard.
				
				// If 'bye' is entered, terminate the client
				if(sendingMessage.equalsIgnoreCase("bye")){ 
					System.out.println(" [ The client is finished. ] ");
					break;
				}
				
				try{
					out.writeUTF(sendingMessage); // Send the message to the server
				}
				catch(Exception e){
					System.out.println("An error has occurred.");
				}
				
				try{
					receivedMessage = in.readUTF(); // Receive a message from the server
				}
				catch(Exception e){
					System.out.println("An error has occurred.");
				}
				
				if(receivedMessage.indexOf(" ") == -1) { // If true, print the answer for the requested expression / If false, print the appropriate error message
					System.out.println("Result : " + receivedMessage); // Print the answer for the expression
				}
				else {
					System.out.println("Error message : " + receivedMessage); // Print the appropriate error message
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
