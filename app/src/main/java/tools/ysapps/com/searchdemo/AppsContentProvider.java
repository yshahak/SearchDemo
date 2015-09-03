package tools.ysapps.com.searchdemo;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class AppsContentProvider extends ContentProvider {
	
	 public static final String AUTHORITY = "tools.ysapps.com.searchdemo.CountryContentProvider";
	 public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/apps" );

     appsDB mAppDB = null;

     private static final int SUGGESTIONS_APP = 1;
     private static final int SEARCH_APP = 2;
     private static final int GET_APP = 3;
     
     UriMatcher mUriMatcher = buildUriMatcher();
     
     private UriMatcher buildUriMatcher(){
    	 UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);    	 
    	 
    	 // Suggestion items of Search Dialog is provided by this uri
    	 uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SUGGESTIONS_APP);
    	 
    	 // This URI is invoked, when user presses "Go" in the Keyboard of Search Dialog
    	 // Listview items of SearchableActivity is provided by this uri    	 
    	 // See android:searchSuggestIntentData="content://in.wptrafficanalyzer.searchdialogdemo.provider/countries" of searchable.xml
    	 uriMatcher.addURI(AUTHORITY, "countries", SEARCH_APP);
    	 
    	 // This URI is invoked, when user selects a suggestion from search dialog or an item from the listview
    	 // Country details for CountryActivity is provided by this uri    	 
    	 // See, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID in CountryDB.java
    	 uriMatcher.addURI(AUTHORITY, "countries/#", GET_APP);
    	 
    	 return uriMatcher;
     }
     
     
     @Override
	 public boolean onCreate() {
		 	mAppDB = new appsDB(getContext());
		 	return true;
	 }
     
     @Override
	 public Cursor query(Uri uri, String[] projection, String selection,
			 String[] selectionArgs, String sortOrder) {
    	 
    	 Cursor c = null;
    	 switch(mUriMatcher.match(uri)){
    	 case SUGGESTIONS_APP:
    		 c = mAppDB.getCountries(selectionArgs);
    		 break;
    	 case SEARCH_APP:
    		 c = mAppDB.getCountries(selectionArgs);
    		 break;
    	 case GET_APP:
    		 String id = uri.getLastPathSegment();
    		 c = mAppDB.getCountry(id);
    	 }

    	 return c;
    	 
	}     

	 @Override
	 public int delete(Uri uri, String selection, String[] selectionArgs) {
		 	throw new UnsupportedOperationException();
	 }

	 @Override
	 public String getType(Uri uri) {
		 	throw new UnsupportedOperationException();
	 }

	 @Override
	 public Uri insert(Uri uri, ContentValues values) {
		 	throw new UnsupportedOperationException();
	 }	 
	 

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
	 		throw new UnsupportedOperationException();
	}
}