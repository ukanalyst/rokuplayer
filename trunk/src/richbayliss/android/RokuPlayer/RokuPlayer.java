package richbayliss.android.RokuPlayer;

import richbayliss.android.RokuPlayer.Provider.RokuRCP;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RokuPlayer extends ListActivity {
	
	// The communications class
	RokuRCP rcp = null;
	
	// UI states and variables
	public static final int SERVER_LIST = 0;
	public static final int ALBUM_LIST = 1;
	public static final int SONG_LIST = 2;
	
	int current_state = RokuPlayer.ALBUM_LIST;
	String[] current_list;
	String[] current_album_list;
	int current_server_id;
	
	
	// Run when the Activity is started
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Set the title bar look-and-feel
        TextView titlebar = (TextView)this.findViewById(R.id.pagetitle); 
        titlebar.setTextAppearance(this, android.R.style.TextAppearance_Large);
        
        // Connect to the Roku device
        rcp = new RokuRCP("192.168.1.66", 5555);
        
        if (!rcp.isConnected)
        {
        	new android.app.AlertDialog.Builder(RokuPlayer.this)
        			.setTitle("No Connection")
        			.setMessage("Unable to find server!")
        			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        		public void onClick(DialogInterface dialog, int whichButton)
		        		{
		        			finish();
		        		}
        			})
        			.show();
        }else{
	        if (!rcp.ReadResponse().contains("roku: ready"))
	        	finish();
	        
	        this.ShowServerList();
        }
    }
    
    private void ShowServerList()
    {
    	TextView titlebar = (TextView)this.findViewById(R.id.pagetitle); 
        titlebar.setText("Servers");
        
        this.current_state = RokuPlayer.SERVER_LIST;
        this.current_list = rcp.GetServerList();        
        this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.current_list));
    }
    private void ShowAlbumList(int ServerID)
    {
    	TextView titlebar = (TextView)this.findViewById(R.id.pagetitle); 
        titlebar.setText("Albums");
        
        this.current_server_id = ServerID;
        this.current_state = RokuPlayer.ALBUM_LIST;
        rcp.SelectServer(ServerID);
        this.current_list = rcp.GetAlbumList();
        this.current_album_list = this.current_list; 
        this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.current_list));
    }
    private void ShowExistingAlbumList()
    {
    	TextView titlebar = (TextView)this.findViewById(R.id.pagetitle); 
        titlebar.setText("Albums");
        
        this.current_state = RokuPlayer.ALBUM_LIST;
        
        this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.current_album_list));
    }
    private void ShowSongList(String AlbumName)
    {
    	TextView titlebar = (TextView)this.findViewById(R.id.pagetitle); 
        titlebar.setText(AlbumName);
        
        this.current_state = RokuPlayer.SONG_LIST;
        rcp.SelectAlbum(AlbumName);
        this.current_list = rcp.GetAlbumSongs();
        this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.current_list));
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
     super.onListItemClick(l, v, position, id);
     
     // Get the item that was clicked
     Object o = this.getListAdapter().getItem(position);
     String s = o.toString();
     
     switch (this.current_state)
     {
     	case RokuPlayer.SERVER_LIST:
     		ShowAlbumList(position);
     		break;
     	case RokuPlayer.ALBUM_LIST:
     		ShowSongList(s);
     		break;
     	case RokuPlayer.SONG_LIST:
     		this.rcp.PlayAlbum(position);
     		break;
     }
    }

    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if (rcp.isConnected)
		{
			ShowServerList();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            rcp.VolUp();
           return true;
        }else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
        	rcp.VolDown();
        	return true;
        }else if (keyCode == KeyEvent.KEYCODE_BACK) {
        	switch (this.current_state)
            {
            	case RokuPlayer.SERVER_LIST:
            		finish();
            		break;
            	case RokuPlayer.ALBUM_LIST:
            		ShowServerList();
            		break;
            	case RokuPlayer.SONG_LIST:
            		ShowExistingAlbumList();
            		break;
            }
        	return true;
        }else{
        	return false;
        }
    }

}