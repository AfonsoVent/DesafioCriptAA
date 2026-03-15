/* hjUDPproxy, 20/Mar/18
 *
 * This is a very simple (transparent) UDP proxy
 * The proxy can listening on a remote source (server) UDP sender
 * and transparently forward received datagram packets in the
 * delivering endpoint
 *
 * Possible Remote listening endpoints:
 *    Unicast IP address and port: configurable in the file config.properties
 *    Multicast IP address and port: configurable in the code
 *  
 * Possible local listening endpoints:
 *    Unicast IP address and port
 *    Multicast IP address and port
 *       Both configurable in the file config.properties
 */

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

class hjUDPproxy {
    public static void main(String[] args) throws Exception {
        // Open props
        InputStream inputStream = new FileInputStream("config.properties");
        if (inputStream == null) {
            System.err.println("Configuration file not found!");
            System.exit(1);
        }

        // load them
        Properties properties = new Properties();
        properties.load(inputStream);
	    
        // Define AES key (the same one)
        byte[] keyBytes = "0123456789abcdef".getBytes();
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

        // Read endpoints (remote of prop and localdelivery of prop)
        String remote = properties.getProperty("remote");
        String destinations = properties.getProperty("localdelivery");

        // Convert String -> socketAddress
        SocketAddress inSocketAddress = parseSocketAddress(remote);
        Set<SocketAddress> outSocketAddressSet = Arrays.stream(destinations.split(",")).map(s -> parseSocketAddress(s)).collect(Collectors.toSet());

        // Create socket server (in) and player (out)
	    DatagramSocket inSocket = new DatagramSocket(inSocketAddress); 
        DatagramSocket outSocket = new DatagramSocket();
        
        // Buffer
        byte[] buffer = new byte[4 * 1024];
       
        while (true) {
            // Prepare packet
            DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
 	        inSocket.receive(inPacket);  // if remote is unicast // and recived

            // Debug "."
            System.out.print(".");
            for(SocketAddress outSocketAddress : outSocketAddressSet) 
            {   
                // The Data
                byte[] data = Arrays.copyOf(inPacket.getData(), inPacket.getLength());
                
                // [0, 12] - IV; [12, until end] - ciphertext
                byte[] iv = Arrays.copyOfRange(data, 0, 12);
                byte[] cipherTxt = Arrays.copyOfRange(data, 12, data.length);

                // Prepare Key
                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                GCMParameterSpec spec = new GCMParameterSpec(128, iv);
                cipher.init(Cipher.DECRYPT_MODE, key, spec);

                // ""Cypher"" to plain
                byte[] plain = cipher.doFinal(cipherTxt);

                // Sends to player
                DatagramPacket outPacket = new DatagramPacket(plain, plain.length, outSocketAddress);
                outSocket.send(outPacket);            
            }
        }
    }

    // ---------------------------------------------------------------------------------------------------
    // Function Auxliar to convert ("IP:Port" -> InetSocketAddress type)
    private static InetSocketAddress parseSocketAddress(String socketAddress) 
    {
        String[] split = socketAddress.split(":");
        String host = split[0];
        int port = Integer.parseInt(split[1]);
        return new InetSocketAddress(host, port);
    }
}
