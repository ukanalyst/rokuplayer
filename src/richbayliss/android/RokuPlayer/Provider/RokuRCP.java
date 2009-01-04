package richbayliss.android.RokuPlayer.Provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class RokuRCP {

	// Socket to talk to the RCP server
	Socket sock;
	PrintWriter out;
	BufferedReader in;
	
	int Vol = 20;
	
	public boolean isConnected = false;
	
	public RokuRCP(String ServerAddr, int ServerPort)
	{
		try {
			
			this.sock = new Socket();
			this.sock.connect(new InetSocketAddress(ServerAddr,ServerPort), 3000);
			this.in = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
			this.out = new PrintWriter(this.sock.getOutputStream());
			
			this.isConnected = true;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public String[] GetServerList()
	{
		// Write command to server
		out.println("ListServers");
		out.flush();
		
		// Wait for the response
		String serverlist_raw = ReadResponse();
		
		// Flush the tags off
		serverlist_raw = serverlist_raw.replaceAll("ListServers: ", "");
		String[] lines = serverlist_raw.split("\r\n");
		
		// Remove the first and last lines
		String[] clean_lines = new String[lines.length-2];
		for (int i = 1; i < lines.length-1; i++)
		{
			clean_lines[i-1] = lines[i];
		}
		
		return clean_lines;
	}
	public void SelectServer(int ID)
	{
		// Connect to the existing server, then disconnect
		this.out.println("GetConnectedServer");
		this.out.flush();
		
		ReadResponse();
		
		this.out.println("ServerDisconnect");
		this.out.flush();
		
		ReadResponse();
		
		// Write command to server
		this.out.println("ServerConnect "+String.valueOf(ID));
		this.out.flush();
		
		ReadResponse();
			
	}
	public void SelectAlbum(String Name)
	{
		// Connect to the existing server, then disconnect
		this.out.println("SetBrowseFilterAlbum " + Name);
		this.out.flush();
		
		ReadResponse();
			
	}
	public void PlayAlbum(int ID)
	{
		// Connect to the existing server, then disconnect
		this.out.println("NowPlayingClear");
		this.out.flush();
		ReadResponse();
		
		// Play all songs
		this.out.println("NowPlayingInsert all");
		this.out.flush();
		ReadResponse();
		
		// Start at the one you picked
		this.out.println("PlayIndex " + String.valueOf(ID));
		this.out.flush();
		ReadResponse();
			
	}
	public void VolUp()
	{
		this.Vol += 5;
		
		// Connect to the existing server, then disconnect
		this.out.println("SetVolume " + this.Vol);
		this.out.flush();
		ReadResponse();
	}
	public void VolDown()
	{
		this.Vol -= 5;
		
		// Connect to the existing server, then disconnect
		this.out.println("SetVolume " + this.Vol);
		this.out.flush();
		ReadResponse();
	}
	public String[] GetAlbumList()
	{
		// Write command to server
		this.out.println("ListAlbums");
		this.out.flush();
		
		// Wait for the response
		String albumlist_raw = ReadResponse();
		
		// Flush the tags off
		albumlist_raw = albumlist_raw.replaceAll("ListAlbums: ", "");
		String[] lines = albumlist_raw.split("\r\n");
		
		if (lines[1].contains("GenericError"))
		{
			String[] error = new String[1];
			error[0] = "Error";
		}
		
		// Remove the first and last lines
		String[] clean_lines = new String[lines.length-4];
		for (int i = 2; i < lines.length-2; i++)
		{
			clean_lines[i-2] = lines[i];
		}
		
		return clean_lines;
	}
	
	public String[] GetAlbumSongs()
	{
		// Write command to server
		this.out.println("ListSongs");
		this.out.flush();
		
		// Wait for the response
		String songlist_raw = ReadResponse();
		
		// Flush the tags off
		songlist_raw = songlist_raw.replaceAll("ListSongs: ", "");
		String[] lines = songlist_raw.split("\r\n");
		
		if (lines[1].contains("GenericError"))
		{
			String[] error = new String[1];
			error[0] = "Error";
		}
		
		// Remove the first and last lines
		String[] clean_lines = new String[lines.length-4];
		for (int i = 2; i < lines.length-2; i++)
		{
			clean_lines[i-2] = lines[i];
		}
		
		return clean_lines;
	}
	
	
	public String ReadResponse()
	{
		String response = "";
		boolean stopflag = true;
		
		try {
			String line = this.in.readLine();
			
			if (line.contains("TransactionInitiated"))
			{
				response = response + line + "\r\n";
				
				while (stopflag)
				{
					line = this.in.readLine();
					if (line.contains("TransactionComplete"))
						stopflag = false;
					
					response = response + line + "\r\n";
				}
			}else{
				response = response + line + "\r\n";
				
				while (this.in.ready()) {
					response = response + this.in.readLine() + "\r\n";
				}
			}
		}
		catch (IOException ex) {
		  return response;
		}
		return response;

	}
}
