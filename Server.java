import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

static final int MAX_CLIENT = 10; //Number of clients that can connect to one server simultaneously
static int serverCount = 0; //Number of clients connected to the server

public static void main(String args[]) throws IOException {
	// Create a server socket and combine with port 8888
   ServerSocket serverSocket = new ServerSocket(8888);
   System.out.println("Start Server...");
   System.out.println("Waiting for clients");
   for (int i = 0; i < MAX_CLIENT + 1; i++) { //Check the number of clients
      new Connection(serverSocket);
   }
}

static class Connection extends Thread { //Thread for server redundancy for multiple clients
   private ServerSocket listener; //Socket to be connected to the client
   
   public Connection (ServerSocket listener) {
      this.listener = listener;
      start();
   }
   
   
// Function that verifies that the operand is a number 
   public static boolean isDisit(String temp) { 
       try {
           Double.parseDouble(temp); 
           return true;
       }
       catch (Exception e) {
           return false;
       }
   }
   
   public void run() {
      Socket socket = null;
      String inputMessage = null;
      String isFull = ""; //Flag informing clients whether they can connect to the server
      double res = 0;
      
      try {
         while (true) {
            socket = listener.accept(); // Accept server and client connections
            serverCount++;
            int order = serverCount; // Order of current clients
            if(order<MAX_CLIENT + 1) {
            	 System.out.println(" [ "+order+"th client is connected.] "); 	
            }
           
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             
            while (true) {
            	if (serverCount > MAX_CLIENT) { // If the number of clients currently connected to the server is greater than MAX_CLEIENT
                   System.out.println("Client is already Full!\n");
                   isFull = "Full";
                   out.writeUTF(isFull); 
                   socket.close();
                   serverCount--;
                   break;
                }
                out.writeUTF(res + "");
                
                try {
                   inputMessage = in.readUTF(); 
                } catch (Exception e) {
                	System.out.println(" [ "+order+"th client is disconnected.] ");
                	serverCount--;
                	break;
                }
  
                
                String expression[] = inputMessage.split(" "); //Split strings based on "(space)"
                if (expression[0].equalsIgnoreCase("bye")) { 
                	break;
                }
                
				if (expression.length < 3) { // Error: if less than required arguments are entered
					System.out.println("Incorrect: Too few arguments");		
					out.writeUTF("too few arguments"); ////Send error messages to clients
					continue;
				}
				else if (expression.length > 3) { // Error: if more than necessary arguments are entered
					System.out.println("Incorrect: Too many arguments");
					out.writeUTF("too many arguments"); ////Send error messages to clients
					continue;
				}
				
				String temp1 = expression[1];  
				String temp2 = expression[2]; 
				// Determine if operand 1 and operand 2 are numbers
				if(isDisit(temp1)==false) { 
					System.out.println("Incorrect: operand1 is not number");
					out.writeUTF("operand1 is not number"); 
					out.flush();
					continue;	
				}
				else if(isDisit(temp2)==false) { 
					System.out.println("Incorrect: operand2 is not number");
					out.writeUTF("operand2 is not number"); 
					out.flush();
					continue;	
				}

				
				String operator = expression[0]; // operator				
				double op1 = Integer.parseInt(expression[1]); //store operand1 in new variable
				double op2 = Integer.parseInt(expression[2]); //store operand2 in new variable

				switch (operator.toUpperCase()) {
					case "ADD": // +
						res = op1 + op2;
						System.out.println(op1 + " + " + op2 + " = " + res);
						break; 
					case "MINUS": // -
						res = op1 - op2;
						System.out.println(op1 + " - " + op2 + " = " + res);
						break;
					case "MUL": // *
						res = op1 * op2;
						System.out.println(op1 + " * " + op2 + " = " + res);
						break;
					case "DIV": // /
						if (op2 == 0) { // divided by 0
							System.out.println("Incorrect: divided by zero");
							out.writeUTF("dividing number must not be zero");
							out.flush();
							continue;
						} else {
							res = op1 / op2;
							System.out.println(op1 + " / " + op2 + " = " + res);
						}
						break;
					default: // The case that operator is not valid
						System.out.println("Incorrect: operation error");
						out.writeUTF("operation error");
						out.flush();
						continue;
				}
				
				out.writeUTF(res + ""); // Send a response to a client's request to the client.
				out.flush();
				
            }
         }
      } catch (IOException e) {
    	  e.getMessage();
      }
   }
}

public void run() {}

}
