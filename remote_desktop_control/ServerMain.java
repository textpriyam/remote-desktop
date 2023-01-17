
import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
/**
 * entry class of the server
 */
public class ServerMain {
    
    private JFrame frame = new JFrame("SERVER MAIN");
    
    private JDesktopPane desktop = new JDesktopPane();
	
    public static void main(String args[]){
		
        String port = JOptionPane.showInputDialog("Please enter listening port");
        new ServerMain().initialize(Integer.parseInt(port));
    }

    public void initialize(int port){

        try {
            ServerSocket sc = new ServerSocket(port);
            
            drawGUI();
            
            while(true){
                Socket client = sc.accept();
                System.out.println("New client Connected to the server");
                
                new ClientCreation(client,desktop);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /*
     * Draws the main server GUI
     */
    public void drawGUI(){
            frame.add(desktop,BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            frame.setExtendedState(frame.getExtendedState());		/// |JFrame.MAXIMIZED_BOTH
            frame.setVisible(true);
    }

}
class ClientCreation extends Thread {

    JInternalFrame interFrame = new JInternalFrame("Client Screen",true, true, true);
     JPanel cPanel = new JPanel();
	 Socket client;
	 JDesktopPane desktop;
    
    ClientCreation(Socket client, JDesktopPane desktop) {
        
		this.client = client;
		this.desktop = desktop;
        start();
    }
    /*
     * Draw GUI per each connected client
     */
    public void drawGUI(){
        interFrame.setLayout(new BorderLayout());
        interFrame.getContentPane().add(cPanel,BorderLayout.CENTER);
        interFrame.setSize(100,100);
        desktop.add(interFrame);
        try {
            
            interFrame.setMaximum(true);
        } catch (Exception e) {
           
        }
        
        cPanel.setFocusable(true);
        interFrame.setVisible(true);
    }

    public void run(){

        
        Rectangle clientScreenDim = null;
        
        ObjectInputStream ois = null;
        
        drawGUI();

        try{
            
            ois = new ObjectInputStream(client.getInputStream());
            clientScreenDim =(Rectangle) ois.readObject();
        }catch(Exception e){
            
        }
        
        new ClientScreenReciever(ois,cPanel);
       
        new ClientCommandsSender(client,cPanel,clientScreenDim);
    }
}

class ClientScreenReciever extends Thread {

     ObjectInputStream ois;
     JPanel p;
     boolean continueLoop = true;

    public ClientScreenReciever(ObjectInputStream ois, JPanel p) {
        this.ois = ois;
        this.p = p;
        
        start();
    }

    public void run(){
        
            try {
                
                
                while(continueLoop){
                    
                    ImageIcon imageIcon = (ImageIcon) ois.readObject();
                    System.out.println("New image recieved");
                    Image image = imageIcon.getImage();
                    image = image.getScaledInstance(p.getWidth(),p.getHeight()
                                                        ,Image.SCALE_SMOOTH);
                    
                    Graphics graphics = p.getGraphics();
                    graphics.drawImage(image, 0, 0, p.getWidth(),p.getHeight(),p);
                }
            } catch (Exception e) {
               
          } 
     }
}

class ClientCommandsSender implements KeyListener,
        MouseMotionListener,MouseListener {

     Socket s;
     JPanel p ;
     PrintWriter writer ;
     Rectangle r ;

    ClientCommandsSender(Socket s, JPanel p, Rectangle r) {
        this.s = s;
        this.p = p;
        this.r = r;
        
        p.addKeyListener(this);
        p.addMouseListener(this);
        p.addMouseMotionListener(this);
        try {
            
            writer = new PrintWriter(s.getOutputStream());
        } catch (Exception e) {
          
        }
        
    }
 
    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
		
		
        double xScale = r.getWidth()/p.getWidth();
        System.out.println("xScale: " + xScale);
        double yScale = r.getHeight()/p.getHeight();
        System.out.println("yScale: " + yScale);
        System.out.println("Mouse Moved");
        writer.println(-5);
        writer.println((int)(e.getX() * xScale));
        writer.println((int)(e.getY() * yScale));
        writer.flush();
    }

    public void mouseClicked(MouseEvent e) {
    }

	public void mousePressed(MouseEvent e) {
        System.out.println("Mouse Pressed");
        writer.println(-1);
        int button = e.getButton();
		//first we assume left button is clicked
        int xButton = 16;
        if (button == 3) // if right button is clciked
		{
            xButton = 4;
        }
		
		
        writer.println(xButton);
        writer.flush();
    }

    public void mouseReleased(MouseEvent e) {
        System.out.println("Mouse Released");
        writer.println(-2);
        int button = e.getButton();
		System.out.println(button);
        int xButton = 16;
        if (button == 3) {
            xButton = 4;
        }
        writer.println(xButton);
        writer.flush();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {

    }    
    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        System.out.println("Key Pressed");
        writer.println(-3);
        writer.println(e.getKeyCode());
        writer.flush();
    }

    public void keyReleased(KeyEvent e) {
        System.out.println("Mouse Released");
        writer.println(-4);
        writer.println(e.getKeyCode());
        writer.flush();
    }  

}

