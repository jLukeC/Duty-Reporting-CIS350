package com.example.swords.dutyreporting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;


public class MainActivity extends ActionBarActivity {

    private Button b_log_in;
    private EditText et_username;
    private EditText et_password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize Parse
        Parse.initialize(this, "2DR7xvqsx4YcYsgiZ7HGfy5XBLF1fWudmD21ykku", "75gq1Es8M4imxD1SQHVWG1e1CqvNSlTYtNxRbk0T");


        //initialize button and text fields
        b_log_in = (Button)findViewById(R.id.log_in_button);
        et_username = (EditText)findViewById(R.id.username_prompt);
        et_password = (EditText)findViewById(R.id.password_prompt);

        //if "Log In" is clicked with correct username/password combo, go to next activity
        b_log_in.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        String username = et_username.getText().toString();
                        String password = et_password.getText().toString();
                        ParseHandler handler = new ParseHandler(username);
                        if (ParseHandler.getSupervisors().contains(username)) {
                            if (handler.getPassword().contains(password)) {
                                pd_log_in();
                            } else {
                                Context context = getApplicationContext();
                                int duration = Toast.LENGTH_SHORT;
                                CharSequence text = "Incorrect username/password combination";
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        } else if (ParseHandler.getResidents().contains(username)) {
                            if (handler.getPassword().contains(password)) {
                                log_in();
                            } else {
                                Context context = getApplicationContext();
                                int duration = Toast.LENGTH_SHORT;
                                CharSequence text = "Incorrect username/password combination";
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        } else {
                            Context context = getApplicationContext();
                            int duration = Toast.LENGTH_SHORT;
                            CharSequence text = "Incorrect username/password combination";
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                });

    }





    public void log_in () {
        Intent intent = new Intent(this, LoggedInActivity.class);
        //pass username to LoggedInActivity
        intent.putExtra("USERNAME", et_username.getText().toString());
        startActivity(intent);
    }

    public void pd_log_in () {
        Intent intent = new Intent(this, PDLogginInActivity.class);
        //pass username to LoggedInActivity
        intent.putExtra("USERNAME", et_username.getText().toString());
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
