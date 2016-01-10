package com.wendy.quantumwolf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SeerActivity extends Activity {
	private View.OnClickListener seerClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

		}
	};
	Spinner playerSpinner;
	Spinner targetSpinner;
	GamePlay gp = MainActivity.gp;
	Map<Integer, Integer> seeMap = new HashMap<>();
	private DialogInterface.OnClickListener addSeer = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			seeMap.put(Integer.parseInt(((String) playerSpinner.getSelectedItem()).replaceAll("^\\d", "")) - 1,
					Integer.parseInt(((String) targetSpinner.getSelectedItem()).replaceAll("^\\d", "")) - 1);

		}
	};

	private View.OnClickListener addClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
			builder.setTitle("Who do you want a vision of?");
			builder.setView(R.layout.selection);
			builder.setPositiveButton("add", addSeer);
			AlertDialog ad = builder.create();
			ad.show();

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seer1);
		playerSpinner = (Spinner) findViewById(R.id.Playersplinner);
		ArrayAdapter<String> choices = new ArrayAdapter<>(getApplicationContext(), R.id.Playersplinner);
		List<Integer> alive = gp.getAlivePlayers();
		for (Integer i : alive)
			choices.add("Player " + (i + 1));
		playerSpinner.setAdapter(choices);

		targetSpinner = (Spinner) findViewById(R.id.TargetSpinner);
		ArrayAdapter<String> choices2 = new ArrayAdapter<>(getApplicationContext(), R.id.Playersplinner);

		for (Integer i : alive)
			choices2.add("Player " + (i + 1));
		targetSpinner.setAdapter(choices2);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.seer, menu);
		Button b2 = (Button) findViewById(R.id.See);
		b2.setOnClickListener(seerClickListener);
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
