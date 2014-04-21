package com.codeboy.app.oschina;

import com.codeboy.app.oschina.ui.NewsLatestNewsFragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends BaseActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if(savedInstanceState == null) {
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.container, NewsLatestNewsFragment.newInstance())
			.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.action_question_ask) {
			
		} else if(id == R.id.action_tweet) {
			
		} else if(id == R.id.action_active) {
			
		} else if(id == R.id.action_myinfo) {
			
		} else if (id == R.id.action_settings) {
			return true;
		} 
		return super.onOptionsItemSelected(item);
	}
}
