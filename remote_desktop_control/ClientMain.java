
import java.awt.*;	// robot class
import java.awt.event.*;	
import javax.swing.*;	
import java.net.*;	
import java.io.*;	
import java.awt.image.*;
import javax.imageio.*;	
import java.util.Scanner;

// main class of client
public class ClientMain {

    Socket socket = null;

    public static void main(String[] args){
		
		
        String ip = JOptionPane.showInputDialog("Please enter server IP");
        String port = JOptionPane.showInputDialog("Please enter server port");
        new ClientMain().initialize(ip, Integer.parseInt(port));
    }

    public void initialize(String ip, int port ){

        Robot robot ; //Used to capture the screen
        Rectangle rectangle; //Used to represent screen dimensions

        try {
            System.out.println("Connecting to server ..");
            socket = new Socket(ip, port);
            System.out.println("Connection done.");

          
            
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            rectangle = new Rectangle(dim);

            
            robot = new Robot();

            
            
            new ScreenSender(socket,robot,rectangle);
			
			
            
            new ServerCmdExecution(socket,robot);
        }

		catch (Exception ex) {
            
        } 
    }
}
 

//send sshots
class ScreenSender extends Thread {

    Socket socket ; 
    Robot robot; 
    Rectangle rectangle; 
    boolean continueLoop = true; 
    
    public ScreenSender(Socket socket, Robot robot,Rectangle rect) {
        this.socket = socket;
        this.robot = robot;
        rectangle = rect;
        start();	//to start the thread
    }

	
    public void run()
	{
        ObjectOutputStream oos = null ; 


        try{
            
            oos = new ObjectOutputStream(socket.getOutputStream());
            
            oos.writeObject(rectangle);
        }catch(Exception ex){
            ////
        }

       while(continueLoop){
            //Capture screen
            BufferedImage image = robot.createScreenCapture(rectangle);
           
		   
		   
            ImageIcon imageIcon = new ImageIcon(image);

            
            try {
                System.out.println("before sending image");
                oos.writeObject(imageIcon);
                oos.reset(); 
                System.out.println("New screenshot sent");
            } 
			catch (Exception ex) {
               ////
            }

            
            try{
                Thread.sleep(100);
            }
			catch(Exception e){
                /////
            }
        }

    }

}

class ServerCmdExecution extends Thread {

    Socket socket = null;
    Robot robot = null;
    boolean continueLoop = true;

    public ServerCmdExecution(Socket socket, Robot robot) {
        this.socket = socket;
        this.robot = robot;
        start(); //Start the thread and hence calling run method
    }

    public void run(){
        Scanner scanner = null;
        try {
            //prepare Scanner object
            System.out.println("Preparing InputStream");
            scanner = new Scanner(socket.getInputStream());

            while(continueLoop){
                //recieve commands and respond accordingly
                System.out.println("Waiting for command");
                int command = scanner.nextInt();
                System.out.println("New command: " + command);
                switch(command){
                    case -1:
                        robot.mousePress(scanner.nextInt());
                    break;
                    case -2:
                        robot.mouseRelease(scanner.nextInt());
                    break;
                    case -3:
                        robot.keyPress(scanner.nextInt());
                    break;
                    case -4:
                        robot.keyRelease(scanner.nextInt());
                    break;
                    case -5:
                        robot.mouseMove(scanner.nextInt(), scanner.nextInt());
                    break;
                }
            }
        } catch (Exception e) {
            ///
        }
    }

}
