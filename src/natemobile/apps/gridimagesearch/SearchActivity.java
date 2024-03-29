package natemobile.apps.gridimagesearch;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * SearchActivity
 * 
 * Query to find an image activity/controller.
 * @author nkemavaha
 *
 */
public class SearchActivity extends ActionBarActivity {
	
	private EditText etQuery;
	
	private GridView gvResult; 
	
	private Button btnSearch;
	
	ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();
	ImageResultArrayAdapter imageAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupViews();
		
		imageAdapter = new ImageResultArrayAdapter( this , imageResults );
		gvResult.setAdapter( imageAdapter );
		
		// Click on item and open full screen activity
		gvResult.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View parent, int position, long arg3) {
				Intent i = new Intent( getApplicationContext(), ImageDisplayActivity.class );
				ImageResult imageResult = imageResults.get( position );
				i.putExtra( "result" , imageResult );
				startActivity(i);
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void setupViews() {
		etQuery = (EditText) findViewById(R.id.etQuery);
		gvResult = (GridView) findViewById(R.id.gvResult);
		btnSearch = (Button) findViewById(R.id.btnSearch);
	}
	
	public void onImageSearch(View v) {
		String query = etQuery.getText().toString();
		
		Toast.makeText( this, "Searching for " + query, Toast.LENGTH_SHORT).show();
		// Send Async http query
		AsyncHttpClient client = new AsyncHttpClient();
		//https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=barack%20obama";
		client.get("https://ajax.googleapis.com/ajax/services/search/images?" +
                  "v=1.0&q=" + Uri.encode( query ),
                  new JsonHttpResponseHandler() {
			
			// Upon success query
			@Override
			public void onSuccess(JSONObject response) {
				JSONArray imageJsonResults = null;
				try {
					// Get data coming back from API response
					imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
					
					imageResults.clear();	// clear result
					imageAdapter.addAll( ImageResult.fromJSONArray(imageJsonResults)); // parse result
					
					
					Log.d("DEBUG", imageResults.toString() );
							
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		});
	}

}
