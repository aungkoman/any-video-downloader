package com.teamcs.anyvideodownloader;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    int[] site_image;
    String TAG = "teamcs_log";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final EditText edittext = (EditText) findViewById(R.id.editText);
        edittext.setImeActionLabel("Go", KeyEvent.KEYCODE_ENTER);
        edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    String url = edittext.getText().toString();
                    Toast.makeText(getBaseContext(), "URL is "+url, Toast.LENGTH_SHORT).show();
                    if(Patterns.WEB_URL.matcher(url).matches()){
                        //Toast.makeText(getBaseContext(), "URL is TURE", Toast.LENGTH_SHORT).show();
                        // start next activity
                        if(url.startsWith("http://") || url.startsWith("https://")){

                        }else{
                            url = "https://"+url;
                        }
                        Intent web_view_intent = new Intent(getBaseContext(), webBrowser.class);
                        web_view_intent.putExtra("url",url);
                        startActivity(web_view_intent);
                    } else{
                        String str = getString(R.string.valid_url);
                        Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();

                    }
                    return true;
                }
                return false;
            }
        });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // Ref ; https://stackoverflow.com/questions/10903754/input-text-dialog-android
//                AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
//                builder.setTitle("Title");
//
//                // Set up the input
//                final EditText input = new EditText(getBaseContext());
//                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                builder.setView(input);
//
//                // Set up the buttons
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        m_Text = input.getText().toString();
//                        // start activity here
//                        Log.i(TAG,"m_Text is "+m_Text);
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//                builder.show();

            }
        });
        fab.hide();
//
//        listview = (ListView) findViewById(R.id.listview);
//        site_name = new String[] {"Youtube","Facebook","Instagram","Twitter","Pornhub","xvideos","sex","XNXX","RED TUBE","X TUBE"};
//        site_url = new String[] {"https://youtube.com","https://facebook.com","https://instagram.com","https://twitter.com","https://pornhub.com","https://xvideos.com","https://sex.com","https://www.xnxx.com/","https://www.redtube.com/","https://www.xtube.com/"};
//        //site_image = new int[] {R.mipmap.ic_launcher,R.mipmap.ic_launcher_round,R.mipmap.ic_launcher,R.mipmap.ic_launcher_round,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher} ;
//
//        site_image = new int[] { R.drawable.youtube,R.drawable.facebook,R.drawable.instagram,R.drawable.twitter,R.drawable.pornhub, R.drawable.xvideos, R.drawable.sex, R.drawable.xnxx, R.drawable.redtube, R.drawable.xtube};
//
//        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, site_name);
//        listview.setAdapter(adapter);
//
//        // ListView Item Click Listener
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                // start next activity
//                Intent web_view_intent = new Intent(getBaseContext(), webBrowser.class);
//                web_view_intent.putExtra("url",site_url[position]);
//                startActivity(web_view_intent);
//            }
//
//        });

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


    public class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public MySimpleArrayAdapter(Context context, String[] values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.simple_listview_layout_item, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.text1);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.listview_image);
            //Typeface tf= Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/pyidaungsu.ttf");
            //textView.setTypeface(tf);
            textView.setText(values[position]);
            //imageView.setImageResource(R.mipmap.ic_launcher);
            imageView.setImageResource(site_image[position]);
            return rowView;
        }
    }
}
