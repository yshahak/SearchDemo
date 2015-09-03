package tools.ysapps.com.searchdemo;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SearchableActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {
	
	ListView mLVCountries;
	SimpleCursorAdapter mCursorAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_searchable);
		
		// Getting reference to Country List
		mLVCountries = (ListView)findViewById(R.id.lv_countries);		
		
		// Setting item click listener		
		mLVCountries.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
                Intent countryIntent = new Intent(getApplicationContext(), CountryActivity.class);
                
                // Creating a uri to fetch country details corresponding to selected listview item
                Uri data = Uri.withAppendedPath(AppsContentProvider.CONTENT_URI, String.valueOf(id));
                
                // Setting uri to the data on the intent
                countryIntent.setData(data);
                
                // Open the activity
                startActivity(countryIntent);
            }
        });

		// Defining CursorAdapter for the ListView		
		mCursorAdapter = new SimpleCursorAdapter(getBaseContext(),
				android.R.layout.simple_list_item_1,
	            null,
	            new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1},
	            new int[] { android.R.id.text1}, 0);
		
		// Setting the cursor adapter for the country listview
		mLVCountries.setAdapter(mCursorAdapter);
		
		// Getting the intent that invoked this activity
		Intent intent = getIntent();		
		
		// If this activity is invoked by selecting an item from Suggestion of Search dialog or 
		// from listview of SearchActivity
		if(intent.getAction().equals(Intent.ACTION_VIEW)){ 
			Intent launchIntent;
			Uri data = intent.getData();
			launchIntent = new Intent();
			Cursor c = getContentResolver().query(data, null, null, null, null);
			if (c.moveToFirst()) {
                String packageName = c.getString(3);

                String activityName = c.getString(4);
                launchIntent.setClassName(packageName, activityName);
                startActivity(launchIntent);
            }
            c.close();
            finish();			
		}else if(intent.getAction().equals(Intent.ACTION_SEARCH)){ // If this activity is invoked, when user presses "Go" in the Keyboard of Search Dialog
			String query = intent.getStringExtra(SearchManager.QUERY);
			doSearch(query);
		}		
	}	
	
	private void doSearch(String query){
		Bundle data = new Bundle();
		data.putString("query", query);
		
		// Invoking onCreateLoader() in non-ui thread
		getSupportLoaderManager().initLoader(1, data, this);		
	}


	/** This method is invoked by initLoader() */
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle data) {
		Uri uri = AppsContentProvider.CONTENT_URI;
		return new CursorLoader(getBaseContext(), uri, null, null , new String[]{data.getString("query")}, null);	
	}

	/** This method is executed in ui thread, after onCreateLoader() */
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {	
		mCursorAdapter.swapCursor(c);		
	}


	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub		
	}		
}