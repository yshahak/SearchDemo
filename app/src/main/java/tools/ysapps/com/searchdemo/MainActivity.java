package tools.ysapps.com.searchdemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends FragmentActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btn = (Button) findViewById(R.id.btn_search);	
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {			
				onSearchRequested();				
			}
		});		
	}
}
