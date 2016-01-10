package com.wendy.quantumwolf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {
	NumberPicker numWolves;
	NumberPicker numPlayer;
	Button b;
	Spinner playerSpinner;
	Spinner targetSpinner;
	public static GamePlay gp;

	private View.OnClickListener startClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			if (numWolves.getValue() >= numPlayer.getValue()) {
				Toast t = Toast.makeText(getApplicationContext(), "Cannot have more Wolves than players",
						Toast.LENGTH_SHORT);
				t.show();
				return;
			}

			gp = new GamePlay(numPlayer.getValue(), numWolves.getValue());
			Intent i = new Intent(v.getContext(), SeerActivity.class);
			startActivity(i);
		}
	};

	private View.OnClickListener seerClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		b = (Button) findViewById(R.id.startButton);
		numPlayer = (NumberPicker) findViewById(R.id.numPlayers);
		numWolves = (NumberPicker) findViewById(R.id.numWolves);
		numPlayer.setMaxValue(20);
		numWolves.setMaxValue(4);
		b.setOnClickListener(startClickListener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

}
