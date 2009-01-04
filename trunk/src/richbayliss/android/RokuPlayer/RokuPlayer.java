package richbayliss.android.RokuPlayer;

import richbayliss.android.RokuPlayer.Provider.RokuProvider;
import richbayliss.android.RokuPlayer.Provider.RokuRCP;
import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RokuPlayer extends ListActivity {
	
	RokuRCP rcp = null;
	String state = "servers";
	String[] lst;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        rcp = new RokuRCP("192.168.1.66", 5555);
        
        if (!rcp.ReadResponse().contains("roku: ready"))
        	System.exit(100);
        
        this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rcp.GetServerList()));
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
     super.onListItemClick(l, v, position, id);
     
     // Get the item that was clicked
     Object o = this.getListAdapter().getItem(position);
     String s = o.toString();
     
     if (state == "servers")
        {
       	 rcp.SelectServer(position);
       	 lst = rcp.GetAlbumList();
       	 
       	 state = "albums";
        }else if (state == "albums")
        {
       	 rcp.SelectAlbum(s);
       	 lst = rcp.GetAlbumSongs();
       	 
       	 state = "songs";
        }else if (state == "songs")
        {
       	 rcp.PlayAlbum(position);    	 
       	 state = "songs";
       	 
        }else{
       	 lst = new String[1];
       	 lst[0] = "Nothing to see here!";
        }
        
        this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lst));
    }
    
    
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		state = "servers";
		this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rcp.GetServerList()));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            rcp.VolUp();
           return true;
        }else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
        	rcp.VolDown();
        	return true;
        }else{
        	return false;
        }
    }

}