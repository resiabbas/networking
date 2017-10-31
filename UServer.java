// Server
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

public class UServer extends JFrame 
{
   private JTextField enterField = new JTextField(); // inputs message from user
   private JTextArea displayArea = new JTextArea(); // display information to user
   private Scanner in;
   int port;
   private DatagramSocket server; // server socket
   private DatagramPacket receivePacket;
   private DatagramPacket sendPacket;
   private String message;
   private byte[] sendData1;
   private byte[] receiveData1 = new byte[1024];
   InetAddress ip;

   public UServer()
   {
      super( "Server" );

      enterField.setEditable( false );
      enterField.addActionListener(
         new ActionListener() 
         {
            // send message to client
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

   // set up and run server 
   public void runServer()
   {
      try 
      {
         server = new DatagramSocket(9836);
         while ( true ) 
         {
            try 
            {
               process(); 
            } 
            catch ( EOFException eofException ) 
            {
               displayMessage( "\nServer terminated connection" );
            }  
         } 
      } 
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } 
   } 
   // process connection with client
   private void process() throws IOException
   {
      message = "Connection successful";
      displayMessage(message);

      // enable enterField so server user can send messages
      setTextFieldEditable( true );

      do // process messages sent from client
      { 
         try // read message and display it
         {
         receivePacket = new DatagramPacket(receiveData1,receiveData1.length);
         server.receive(receivePacket);
         ip = receivePacket.getAddress();
         port = receivePacket.getPort();
         message =new String(receivePacket.getData(),0,receivePacket.getLength());
         displayMessage( "\nCLIENT :" + message ); // display message
           
         } 
         catch (IOException e) 
         {
            displayMessage( "\nUnknown object type received"+e);
         } 

      } while ( ! message.equals( "CLIENT: TERMINATE" ) );
   } 
   // send message to client
   private void sendData( String message )
   {
      try 
      {
            message=enterField.getText();
            sendData1=message.getBytes();
            sendPacket = new DatagramPacket(sendData1,sendData1.length,ip,port);
            server.send(sendPacket);
            displayMessage( "\nSERVER :" + message );    
      } 
      catch ( IOException ioException ) 
      {
         displayArea.append( "\nError writing object" );
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
               displayArea.append(messageToDisplay); // append message
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
   
   public static void main( String[] args )
   {
      UServer application = new UServer();
      application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      application.runServer();
   } 
} 