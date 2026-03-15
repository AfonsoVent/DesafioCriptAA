/*
* hjStreamServer.java 
* Streaming server: streams video frames in UDP packets
* for clients to play in real time the transmitted movies
*/

import java.io.*;
import java.net.*;
import javax.crypto.Cipher;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

class hjStreamServer {
	static public void main( String []args ) throws Exception {
		// Valid the input of user
	    if (args.length != 3)
	    {
            System.out.println("Erro, usar: mySend <movie> <ip-multicast-address> <port>");
	       	System.out.println("        or: mySend <movie> <ip-unicast-address> <port>");
	       	System.exit(-1);
        }
		
		// Auxiliar Var
		int size;	// of each frame
		int csize = 0;	// size sent
		int count = 0;	// counter frames
 		long time;	// timer

		// Opens the movie
		DataInputStream g = new DataInputStream( new FileInputStream(args[0]) );
		byte[] buff = new byte[4096];

		// Send socket UDP
		DatagramSocket s = new DatagramSocket();
		InetSocketAddress addr = new InetSocketAddress( args[1], Integer.parseInt(args[2]));
		
		// Create key
		byte[] keyBytes = "0123456789abcdef0123456789abcdef".getBytes();
        SecretKeySpec key = new SecretKeySpec(keyBytes, "ChaCha20"); // Now ChaCha20

		// Packet UDP sent  
		DatagramPacket p = new DatagramPacket(buff, buff.length, addr);

		long t0 = System.nanoTime(); // Ref. time 
		long q0 = 0;

		SecureRandom random = new SecureRandom();

		// While there is movie to stream, them send...
		while (g.available() > 0) {
		    size = g.readShort(); // size of the frame
		    csize=csize+size;
		    time = g.readLong();  // timestamp of the frame
			if (count == 0) q0 = time; // ref. time in the stream
			count += 1;
			
			// Read the bytes
			g.readFully(buff, 0, size);
			
			// IV frame
			byte[] iv = new byte[12];
			random.nextBytes(iv);

			// Starts ChaCha20
			Cipher cipher = Cipher.getInstance("ChaCha20-Poly1305"); // Now ChaCha20
			IvParameterSpec spec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, key, spec);

			// Cypher frame
			byte[] cipherText = cipher.doFinal(buff, 0, size);

			// Prepare array - Send: [IV, ciphertext]
			byte[] packetData = new byte[iv.length + cipherText.length];
			System.arraycopy(iv, 0, packetData, 0, iv.length);   
			System.arraycopy(cipherText, 0, packetData, iv.length, cipherText.length);

			// Data
			p.setData(packetData, 0, packetData.length);
			p.setSocketAddress(addr);

			// Timer to streaming
			long t = System.nanoTime(); // what time is it?
			Thread.sleep( Math.max(0, ((time-q0)-(t-t0))/1000000));
		   
			// Frames sent encrypted using ChacHa20
			s.send(p); 

	        // Debug "."
			System.out.print( "." );
		}

		long tend = System.nanoTime(); // "The end" time 
        System.out.println();
		System.out.println("DONE! all frames sent: "+ count);

		long duration=(tend-t0)/1000000000;
		System.out.println("Movie duration "+ duration + " s");
		System.out.println("Throughput "+ count/duration + " fps");
     	System.out.println("Throughput "+ (8*(csize)/duration)/1000 + " Kbps");
	}
}



