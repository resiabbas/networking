//Client
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.util.*;
import java.net.*;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class UClient extends JFrame 
{
   private JTextField enterField = new JTextField(); // enters information from user
   private JTextArea displayArea = new JTextArea(); // display information to user
   private Scanner in;
   private DatagramSocket client; 
   private DatagramPacket receivePacket;
   private DatagramPacket sendPacket;
   private String message;
   public String hostname;
   private byte[] sendData1;
   private byte[] receiveData1 = new byte[1024];
   InetAddress ip;

   public UClient( String host )
   {
       super( "Client" );
      hostname=host;
      try
      { 
      client = new DatagramSocket();
      }
      catch(SocketException s)
      {
        System.out.println("Socket Exception");
      }
      enterField.setEditable( false );
      enterField.addActionListener(
         new ActionListener() 
         {
            
             @Override
            public void actionPerformed( ActionEvent event )
            {
               sendData( event.getActionCommand() );
               enterField.setText( "" );
            } 
         } 
      ); 

      add( enterField, BorderLayout.NORTH );
      add( new JScrollPane( displayArea ), BorderLayout.CENTER );

      setSize(500,400); 
      setVisible( true ); 
   } 
   public void runClient() 
   {
      try 
      {
         process(); 
      } 
      catch ( EOFException eofException ) 
      {
         displayMessage( "\nClient terminated connection" );
      } 
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      }
       finally 
         {
         closeConnection(); 
         }
   } 
   private void process() throws IOException
   {
      // enable enterField so client user can send messages
      setTextFieldEditable( true );

      do // process messages sent from server
      { 
        
         receivePacket = new DatagramPacket(receiveData1,receiveData1.length);
         client.receive(receivePacket);
         message =new String(receivePacket.getData(),0,receivePacket.getLength());
         displayMessage( "\nSERVER :" + message );
      
      } while ( !message.equals( "SERVER: TERMINATE" ) );
   } 
   // send message to server
   private void sendData( String message )
   {
       try{ 
            ip = InetAddress.getByName(hostname);
            message=enterField.getText();
            sendData1=message.getBytes();
            sendPacket = new DatagramPacket(sendData1, sendData1.length,ip,9836);
            client.send(sendPacket);
            displayMessage( "\nCLIENT :" + message ); 
         }
         catch(IOException ioException)
         {
            System.out.println(" IO Error");
         }
   } 
   private void displayMessage( final String messageToDisplay )
   {
      SwingUtilities.invokeLater(
         new Runnable()
         {
             @Override
            public void run() 
            {
               displayArea.append( messageToDisplay );
            } 
         }  
      ); 
   } 
   private void setTextFieldEditable( final boolean editable )
   {
      SwingUtilities.invokeLater(
         new Runnable() 
         {
             @Override
            public void run() 
            {
               enterField.setEditable( editable );
            } 
         } 
      ); 
   } 
   private void closeConnection() 
   {
      displayMessage( "\nClosing" );
      setTextFieldEditable( false ); 
         client.close(); 
         dispose();
   } 
   public static void main( String[] args )
   {
      String serverHostName = args[0] == null ? "127.0.0.1" : args[0];
      UClient application = new UClient( serverHostName );
      application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      application.runClient(); 
   } 
} 