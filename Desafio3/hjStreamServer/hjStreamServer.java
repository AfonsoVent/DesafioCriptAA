/*
* hjStreamServer.java 
* Streaming server: streams video frames in UDP packets
* for clients to play in real time the transmitted movies
*/

import java.io.*;
import java.net.*;

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
		DataInputStream g = new DataInputStream( new FileInputStream(args[0]));
		byte[] buff = new byte[4096];

		// Send socket UDP
		DatagramSocket s = new DatagramSocket();
		InetSocketAddress addr = new InetSocketAddress(args[1], Integer.parseInt(args[2]));
		
		// Create key
		byte[] key = "0123456789abcdef0123456789abcdef".getBytes();
        DPRG dprg = new DPRG(key); 

		// Packet UDP sent  
		DatagramPacket p = new DatagramPacket(buff, buff.length, addr);

		long t0 = System.nanoTime(); // Ref. time 
		long q0 = 0;

		// While there is movie to stream, them send...
		while (g.available() > 0) {

		    size = g.readShort(); // size of the frame
		    csize=csize+size;
		    time = g.readLong();  // timestamp of the frame

			if (count == 0) q0 = time; // ref. time in the stream
			count += 1;
			
			// Read the bytes
			g.readFully(buff, 0, size);

			// gen keystream
			byte[] keystream = dprg.generate(size);

			byte[] cipher = new byte[size];

			// Op Xor
			for(int i=0;i<size;i++) cipher[i] = (byte)(buff[i] ^ keystream[i]);
			
			p.setData(cipher,0,size);
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



