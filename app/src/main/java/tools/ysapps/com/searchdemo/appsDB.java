package tools.ysapps.com.searchdemo;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.HashMap;
import java.util.List;

public class appsDB {
	
	private static final String DBNAME = "apps";
	
	private static final int VERSION = 1;
	
	private CountryDBOpenHelper mCountryDBOpenHelper;
	
	private static final String FIELD_ID = "_id";
	private static final String FIELD_NAME = "name";
	private static final String FIELD_ICON = "flag";
	private static final String FIELD_ACTIVITY_NAME = "activityName";
    private static final String FIELD_PACKAGE_NAME = "packageName";

    private static final String TABLE_NAME = "apps";
	private HashMap<String, String> mAliasMap;
	
	
	public appsDB(Context context){
		mCountryDBOpenHelper = new CountryDBOpenHelper(context, DBNAME, null, VERSION);
		
		// This HashMap is used to map table fields to Custom Suggestion fields
    	mAliasMap = new HashMap<>();
    	
    	// Unique id for the each Suggestions ( Mandatory ) 
    	mAliasMap.put("_ID", FIELD_ID + " as " + "_id" );
    	
    	// Text for Suggestions ( Mandatory )
    	mAliasMap.put(SearchManager.SUGGEST_COLUMN_TEXT_1, FIELD_NAME + " as " + SearchManager.SUGGEST_COLUMN_TEXT_1);
    	
    	// Icon for Suggestions ( Optional ) 
    	mAliasMap.put( SearchManager.SUGGEST_COLUMN_ICON_1, FIELD_ICON + " as " + SearchManager.SUGGEST_COLUMN_ICON_1);
    	
    	// This value will be appended to the Intent data on selecting an item from Search result or Suggestions ( Optional )
    	mAliasMap.put( SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, FIELD_ID + " as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID );
        mAliasMap.put( SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA, FIELD_ACTIVITY_NAME + " as " + SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA );

	}
		

	/** Returns Countries  */
    public Cursor getCountries(String[] selectionArgs){    	
    	
    	String selection = FIELD_NAME + " like ? ";
    	
    	if(selectionArgs!=null){
    		selectionArgs[0] = "%"+selectionArgs[0] + "%";   		
    	}
    	
    	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    	queryBuilder.setProjectionMap(mAliasMap);
    	
    	queryBuilder.setTables(TABLE_NAME);    	
    	
    	Cursor c = queryBuilder.query(mCountryDBOpenHelper.getReadableDatabase(), 
    									new String[] { "_ID", 
    													SearchManager.SUGGEST_COLUMN_TEXT_1 , 
    													SearchManager.SUGGEST_COLUMN_ICON_1 , 
    													SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
                                                        SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA} ,
    									selection, 
    									selectionArgs, 
    									null, 
    									null,
    									FIELD_NAME + " asc ","10"
    								);  	    	
	    return c;
	    
    }
    
    /** Return Country corresponding to the id */
    public Cursor getCountry(String id){
    	
    	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();    	
    	
    	queryBuilder.setTables(TABLE_NAME);    	
    	
    	Cursor c = queryBuilder.query(mCountryDBOpenHelper.getReadableDatabase(), 
    									new String[] { "_id", "name", "flag", "packageName", "activityName" } ,
    									"_id = ?", new String[] { id } , null, null, null ,"1"
    								);  	
    	
    	return c;
    }

	
	class CountryDBOpenHelper extends SQLiteOpenHelper{

		List<ResolveInfo> list;
		PackageManager pm;


		public CountryDBOpenHelper(		Context context, 
										String name,
										CursorFactory factory, 
										int version ) {
			super(context, DBNAME, factory, VERSION);
			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
			mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			pm = context.getPackageManager();
			list = pm.queryIntentActivities(mainIntent, 0);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "";
			
			// Defining table structure
			sql = " create table " + TABLE_NAME + "" +
											" ( " +
												FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
												FIELD_NAME + " varchar(100), " +
					                            FIELD_ICON + "  int, " +
												FIELD_PACKAGE_NAME + " varchar(100), " +
                                                FIELD_ACTIVITY_NAME + " varchar(100) " +
											" ) " ;
			
			// Creating table
			db.execSQL(sql);
			
			for(ResolveInfo resolveInfo: list){
				ActivityInfo actInfo = resolveInfo.activityInfo;
				String label = resolveInfo.loadLabel(pm).toString();
				String packageName = actInfo.packageName;
				String activityName = actInfo.name;
				int resId = actInfo.icon;
				if (resId == 0)
					resId = actInfo.applicationInfo.icon;
				// Defining insert statement
				sql = "insert into " + TABLE_NAME + " ( " +
						FIELD_NAME + " , " +
						FIELD_ICON + " , " +
                        FIELD_PACKAGE_NAME + " , " +
						FIELD_ACTIVITY_NAME + " ) " +
						" values ( " + 
						" '" + label + "' ," +
						"  " + resId + "  ," +
                        " '" + packageName + "' ," +
						" '" + activityName + "' ) ";
				
				// Inserting values into table
				db.execSQL(sql);					
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub			
		}		
	}	
}