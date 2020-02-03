package com.teamcs.anyvideodownloader;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DownloadView extends AppCompatActivity {
    //String current_url = "https://www.pornhub.com/view_video.php?viewkey=ph5cd0b3f4ec6c2"; // cum twice
    String current_url = "https://www.youtube.com/watch?v=EkHTsc9PU2A"; // i'm yours
    //String current_url = "https://www.xvideos.com/video53615743/horny_wet_squirt_cremie"; // xvideos
    String TAG = "teamcs_log";
    String loading_message;
    String html;
    List<PyObject> video_object_list;
    List<String> video_urls = new ArrayList<>();
    List<String> video_formats = new ArrayList<>();
    ProgressDialog pd;
    ListView listview;
    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.hide();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            current_url = extras.getString("url");
        }

        Log.i(TAG,"current_url is "+current_url);
        loading_message =  "loading..";// getString(R.string.loading_message);
        pd = ProgressDialog.show(this, "", loading_message,true);




        new Thread(new Runnable() {
            @Override
            public void run() {
                if (! Python.isStarted()) {
                    Python.start(new AndroidPlatform(getBaseContext()));
                }
                String webview_url = current_url; // current_url is always update on page start and finished
                Python py = Python.getInstance();
                PyObject youtube_dl = py.getModule("youtube_dl");
                PyObject ydl = youtube_dl.callAttr("YoutubeDL");
                boolean tf = false;
                PyObject result;
                try{
                    result = ydl.callAttr("extract_info",webview_url, new Kwarg("download",tf));
                } catch (Exception e){
                    // return could not download
                    Log.i(TAG,"extract_info exception :"+e.toString());
                    String html;
                    html = "<html><head></head><body><p>Cannot Download Now, try again later."+e.toString()+"</p></body></html>";
                    //webview_two.loadDataWithBaseURL("", html, "text/html", "utf-8", "");
                    return;
                }
                String video_url = ""; // best download url
                try{
                    video_url = result.get("entries").asList().get(0).asMap().get("url").toString();

                }catch(Exception e){
                    try{
                        video_url = result.asMap().get("url").toString(); // not compactable for youtube
                    } catch(Exception el){
                        // this is youtube
                    }
                }

                Log.i(TAG,"hello world");
                // we have list of map in java
//                        Map<String, String> mapContacts = new LinkedHashMap<>();
//
//                        mapContacts.put("0169238175", "Tom");
//                        mapContacts.put("0904891321", "Peter");
//                        mapContacts.put("0945678912", "Mary");
//                        mapContacts.put("0981127421", "John");
//
//                        System.out.println(mapContacts);

                // retrieve object list by using formats

                Log.i(TAG,"video_object_list is started");
                try{
                    video_object_list = result.get("entries").asList().get(0).asMap().get("formats").asList();;
                    Log.i(TAG,"yeah we are trying");

                }catch (Exception e){
                    Log.i(TAG,"n exception "+e);
                    video_object_list = result.asMap().get("formats").asList();
                    Log.i(TAG,"another way");
                }

                // we have to loop through video object list
                for(int i = 0 ; i < video_object_list.size(); i++){
                    Log.i(TAG,"Loop for i "+i);
                    // filter ext
                    String ext = video_object_list.get(i).asMap().get("ext").toString();
                    Log.i(TAG,"EXT is "+ext);
                    String format_id = "";// for pornhub
                    String format_note = ""; // for youtube

                    try{
                        format_note = video_object_list.get(i).asMap().get("format_note").toString(); // if youtube , it's ok
                    }catch (Exception e){
                        format_note = video_object_list.get(i).asMap().get("format_id").toString(); // it is pornhub
                    }
                    Log.i(TAG,"format_note is "+format_note);
                    if(ext.equals("mp4") && (format_note.equals("240p") || format_note.equals("480p") || format_note.equals("720p")  || format_note.equals("mp4-low")   || format_note.equals("mp4-high") )){
                        // it is mp4 and get url
                        String mp4_url = video_object_list.get(i).asMap().get("url").toString();
                        Log.i(TAG,"format_note "+format_note);
                        Log.i(TAG,"mp4 url is "+mp4_url);
                        video_urls.add(mp4_url);
                        video_formats.add(format_note);
                    } else{
                        Log.i(TAG,"ext is not mp4 "+ext);
                    }
                }
                //webView.loadUrl(video_url);
                // If you know file name
                //String fileName = "filename.xyz";

                //Alternative if you don't know filename
                //String fileName = URLUtil.guessFileName(url, null,MimeTypeMap.getFileExtensionFromUrl(url));


                // we show list of available download


                Log.i(TAG,"video_url is "+video_url);
                if(video_url.equals("")){
                    video_url = video_urls.get(0);

                }
                video_urls.add(video_url); // add best url at last :P
                video_formats.add("best");

                handler.post(new Runnable() {
                    public void run() {
                        pd.hide();
                        listview = (ListView) findViewById(R.id.download_resolution_list);
                        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getBaseContext(), video_formats, video_urls);
                        listview.setAdapter(adapter);

                        // ListView Item Click Listener
                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                // start next activity
                                Intent web_view_intent = new Intent(getBaseContext(), webBrowser.class);
                                //web_view_intent.putExtra("url",site_url[position]);
                                //startActivity(web_view_intent);
                                Log.i(TAG,"DOwnload URL is "+video_urls.get(position));
                                DownloadManager.Request request = new DownloadManager.Request(
                                        Uri.parse(video_urls.get(position)));
                                //final String filename= "video_downloader.mp4";
                                String filename = URLUtil.guessFileName(video_urls.get(position), null, MimeTypeMap.getFileExtensionFromUrl(video_urls.get(position)));
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                dm.enqueue(request);
                                Toast.makeText(getApplicationContext(), "Downloading File", //To notify the Client that the file is being downloaded
                                        Toast.LENGTH_LONG).show();
                                //webview_two.loadUrl(video_url);
                            }

                        });
                    }
                });

                // so , we just need to show resolution and link
                // AS LIST VIEW
                // we have data VIDEO_URLS
                // name those link as Format
                Log.i(TAG,"best video_url is "+video_url);
            }
        }).start();
//        video_formats.add("240p");
//        video_formats.add("480p");
//        video_formats.add("720p");
//        video_formats.add("mp4-low");
//        video_formats.add("mp4-high");

//        video_urls.add("https://google.com");
//        video_urls.add("https://google.com");
//        video_urls.add("https://google.com");
//        video_urls.add("https://google.com");
//        video_urls.add("https://google.com");
//        video_urls.add("https://google.com");




//        html = "<html><head></head><body><p>Downloading.... check in notification bar</p></body></html>";
//        webview_two.loadDataWithBaseURL("", html, "text/html", "utf-8", "");

    }

    public class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        //private final String[] values;
        private List<String> format_list;
        private List<String> url_list;

        public MySimpleArrayAdapter(Context context, List<String> format_list, List<String> url_list) {
            super(context, -1, format_list);
            this.context = context;
            this.format_list = format_list;
            this.url_list = url_list;
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
            textView.setText(format_list.get(position));
            //imageView.setImageResource(R.mipmap.ic_launcher);
            //imageView.setImageResource(site_image[position]);
            return rowView;
        }
    }
}
