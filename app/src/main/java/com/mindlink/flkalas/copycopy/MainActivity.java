package com.mindlink.flkalas.copycopy;

import android.app.ActivityManager;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity implements ReportDialogFragment.ReportDialogListener {
    private GoogleApiClient client;
    TextView tvTweetContent;
    static final int REQUEST_COOKIE = 1;
    static final int SHOW_MANUAL = 2;
    static final int SHOW_MANUAL_NO_LOGIN = 3;
    static final String FILE_NAME_COOKIE = "cookie.dat";
    static final String FILE_NAME_INIT = "init";
    boolean isShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isFirstTime()) {
            viewManual();
        }

        setTweetContentTextView();
        setAppBar();
        setOverview();
        setADmob();
        handleSharedText();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_COOKIE) {
                String cookie = data.getExtras().getString("Cookie");
                saveCookie(cookie);
            }else if(requestCode == SHOW_MANUAL){
                if(data.getExtras().getBoolean("Return")){
                    openLoginPage();
                }
            }
        }
    }

    @Override
    protected void onResume (){
        super.onResume();
        if(!isShare) {
            String url = getClipboard();
            if (url != null) {
                TextView tvTweetAddr = (TextView) findViewById(R.id.tweet_src);
                if(isTweetAddress(url)) {
                    tvTweetAddr.setText(url);
                    setMainButtonReady();
                    setSubButtonLoading();
                }
                else{
                    tvTweetAddr.setText(R.string.txt_clip_is_not);
                    setMainButtonExit();
                    setSubButtonManual();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItemSession = menu.findItem(R.id.action_session);
        File file = new File(getFilesDir(), FILE_NAME_COOKIE);
        if(file.exists()){
            menuItemSession.setTitle(R.string.str_menu_logout);
        } else {
            menuItemSession.setTitle(R.string.str_menu_login);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_session) {
            File file = new File(getFilesDir(), FILE_NAME_COOKIE);
            if(file.exists()){
                Toast.makeText(this, R.string.str_t_logout, Toast.LENGTH_SHORT).show();
                file.delete();
            }
            else{
                openLoginPage();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.mindlink.flkalas.copycopy/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.mindlink.flkalas.copycopy/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }





    public boolean isFirstTime(){
        File file = new File(getFilesDir(), FILE_NAME_INIT);
        return !file.exists();
    }

    public boolean viewManual() {
        Intent intent = new Intent(this, ManualActivity.class);
        try{
            File file = new File(getFilesDir(), FILE_NAME_INIT);
            file.createNewFile();
        }catch (Exception e){
            return false;
        }
        intent.putExtra("REQUEST",SHOW_MANUAL);
        startActivityForResult(intent, SHOW_MANUAL);

        return true;
    }

    public void setTweetContentTextView(){
        tvTweetContent = (TextView) findViewById(R.id.receiveText);
        tvTweetContent.setMovementMethod(new ScrollingMovementMethod());
        tvTweetContent.setLongClickable(true);
        tvTweetContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyTweet(tvTweetContent.getText().toString());
                return true;
            }
        });
    }

    public void setAppBar(){
        getSupportActionBar().setTitle("  " + getString(R.string.app_name));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    public void setOverview(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
            ActivityManager.TaskDescription tDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, Color.parseColor("#2795E9"));
            setTaskDescription(tDesc);
        }
    }

    public void setADmob(){
        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void handleSharedText(){
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }
        }
    }

    public boolean isTweetAddress(String urlTweet){
        String[] removePara = urlTweet.split("\\?");
        if(removePara[0].contains("https://twitter.com/")){
            String[] sepaSlash = removePara[0].split("/");
            if(sepaSlash[sepaSlash.length-2].contains("status")){
                try {
                    Long.parseLong(sepaSlash[sepaSlash.length - 1].trim());
                    return true;
                } catch (NumberFormatException ex) {
                    return false;
                }
            }
        }
        return false;
    }

    public void clickMain(View v){
        TextView tvButtonMain = (TextView)findViewById(R.id.btn_main);
        if(tvButtonMain.getText().toString().contains(getString(R.string.txt_load_copy))){
            loadTweet();
        }else if(tvButtonMain.getText().toString().contains(getString(R.string.btn_exit))){
            finish();
        }
    }

    public void clickSub(View v){
        TextView tvButtonNega = (TextView)findViewById(R.id.btn_sub);
        if(tvButtonNega.getText().toString().contains(getString(R.string.btn_bug))){
            popupReportWarningWindow();
        }else if(tvButtonNega.getText().toString().contains(getString(R.string.btn_manual))){
            Intent intent = new Intent(this, ManualActivity.class);
            intent.putExtra("REQUEST",SHOW_MANUAL_NO_LOGIN);
            startActivityForResult(intent, SHOW_MANUAL_NO_LOGIN);
        }
    }

    public void popupReportWarningWindow(){
        DialogFragment ldf = new ReportDialogFragment();
        ldf.show(getFragmentManager(), "Report");
    }

    @Override
    public void onRdPositiveClick(DialogFragment dialog){
        reporteNowTweetURL();
    }

    @Override
    public void onRdNegativeClick(DialogFragment dialog){

    }

    public void reporteNowTweetURL(){
        TextView tvMain = (TextView)findViewById(R.id.tweet_src);
        String nowURL = tvMain.getText().toString();
        ReportAsyncTask nas = new ReportAsyncTask();
        nas.execute(nowURL);
    }

    public class ReportAsyncTask extends AsyncTask<String,String,String > {
        @Override
        protected String doInBackground(String... params) {
            try {
                String parameters = "url="+params[0];
                URL url = new URL("http://copycopybugreportserver.azurewebsites.net/report");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setUseCaches(false);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

                wr.writeBytes(parameters);
                wr.flush();
                wr.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            }catch(Exception e){
            }
            return " ";
        }

        protected void onPostExecute (String result) {
            Toast.makeText(getApplicationContext(), R.string.str_t_report, Toast.LENGTH_SHORT).show();
        }
    }

    public void loadTweet(){
        TextView tv = (TextView)findViewById(R.id.tweet_src);
        String srcURL = tv.getText().toString();
        if(isTweetAddress(srcURL)){
            getTweet(srcURL);
            Toast.makeText(getApplicationContext(), R.string.str_t_load_tweet, Toast.LENGTH_SHORT).show();
        }
    }

    public String getClipboard (){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cData = clipboard.getPrimaryClip();
        if (cData != null) {
            return cData.getItemAt(0).getText().toString();
        }
        return null;
    }



    public class TweetDownloadAsyncTask extends AsyncTask<String,String,String > {
        @Override
        protected String doInBackground(String... params) {
            URL url;
            InputStream is = null;
            BufferedReader br;
            StringBuffer outData = new StringBuffer();

            String[] devidedURL = params[0].split("/");
            String[] paraRemoved = devidedURL[devidedURL.length-1].split("\\?");

            String totalString = paraRemoved[0]+"<";
            outData.append(totalString);

            try {
                url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                CookieHandler.setDefault(new CookieManager());
                setConnection(connection);

                connection.connect();
                is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

                int app = -1;
                while ((app = br.read()) != -1) {
                    outData.append((char)app);
                }

                connection.disconnect();
                is.close();

                return outData.toString();
            }catch (Exception e){
                e.printStackTrace();
            }
            return outData.toString();
        }

        protected void onPostExecute (String result) {
            TweetParser tp = new TweetParser();
            String[] tweetTags = tp.getTweetContext(result.split("<"));
            if(tweetTags.length > 0){
                String strTweet = tp.parseTweet(tweetTags);
                tvTweetContent.setText(strTweet);
                copyTweet(strTweet);
                setMainButtonExit();
                setSubButtonReport();
            }
            else{
                tvTweetContent.setText(R.string.txt_can_not_load);
                setMainButtonReady();
                setSubButtonLoading();
            }

        }

        public boolean setConnection(HttpURLConnection connection){
            try {
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
                connection.setRequestProperty("Content-Type", "text/html");
                connection.setRequestProperty("Cache-Control", "no-cache");
                connection.setDoInput(true);
                String cookie = loadCookie();
                if (cookie != null) {
                    connection.setRequestProperty("Cookie", cookie);
                }
            }catch (Exception e){
                return false;
            }
            return true;
        }
    }

    public boolean getTweet(String url){
        try {
            setMainButtonLoading();
            setSubButtonLoading();
            TweetDownloadAsyncTask nas = new TweetDownloadAsyncTask();
            nas.execute(url);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    public String getTweetLinkFromShared(String shared){
        int indexStart = shared.indexOf("https://twitter.com/");
        if(indexStart > 0) {
            int indexEnd = shared.indexOf("?", indexStart);
            return shared.substring(indexStart,indexEnd);
        }
        return null;
    }

    public void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            isShare = true;
            String url = getTweetLinkFromShared(sharedText);
            TextView tv = (TextView)findViewById(R.id.tweet_src);
            if(url != null) {
                if (isTweetAddress(url)) {
                    tv.setText(url);
                    setMainButtonReady();
                    setSubButtonLoading();
                } else {
                    tv.setText(R.string.txt_shared_not_found);
                }
            }
            else {
                tv.setText(R.string.txt_shared_is_not);
            }
        }
    }

    public void setMainButtonReady(){
        TextView tvButtonMain = (TextView)findViewById(R.id.btn_main);
        tvButtonMain.setText(R.string.txt_load_copy);
        tvButtonMain.setBackgroundColor(Color.parseColor("#FF96FF8A"));
    }

    public void setMainButtonLoading(){
        TextView tvButtonMain = (TextView)findViewById(R.id.btn_main);
        tvButtonMain.setText(R.string.txt_loading);
        tvButtonMain.setBackgroundColor(Color.parseColor("#FF838383"));
    }

    public void setMainButtonExit() {
        TextView tvButtonMain = (TextView)findViewById(R.id.btn_main);
        tvButtonMain.setText(R.string.btn_exit);
        tvButtonMain.setBackgroundColor(Color.parseColor("#FFA7E3FF"));
    }

    public void setSubButtonLoading(){
        TextView tvButtonSub = (TextView)findViewById(R.id.btn_sub);
        tvButtonSub.setText("");
        tvButtonSub.setBackgroundColor(Color.parseColor("#FF838383"));
    }

    public void setSubButtonManual(){
        TextView tvButtonSub = (TextView)findViewById(R.id.btn_sub);
        tvButtonSub.setText(R.string.btn_manual);
        tvButtonSub.setBackgroundColor(Color.parseColor("#ABFFA5"));
    }

    public void setSubButtonReport() {
        TextView tvButtonSub = (TextView)findViewById(R.id.btn_sub);
        tvButtonSub.setText(R.string.btn_bug);
        tvButtonSub.setBackgroundColor(Color.parseColor("#FFC0C0"));
    }

    public void openLoginPage(){
        File file = new File(this.getFilesDir(), FILE_NAME_COOKIE);
        if(!file.exists()){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_COOKIE);
        }
    }

    public boolean saveCookie(String cookie){
        File file = new File(this.getFilesDir(), FILE_NAME_COOKIE);
        if(file != null){
            try{
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(cookie.getBytes());
                fos.flush();
                fos.close();
                Toast.makeText(getApplicationContext(), R.string.str_t_login, Toast.LENGTH_LONG).show();
                return true;
            }catch (Exception e){
                return false;
            }
        }
        return false;
    }

    public String loadCookie(){
        File file = new File(getFilesDir(), FILE_NAME_COOKIE);
        if(file.exists()){
            try {
                FileInputStream fis = new FileInputStream(file);
                int readcount = (int)file.length();
                byte[] buffer = new byte[readcount];
                fis.read(buffer);
                String cookie = new String(buffer);
                fis.close();
                return cookie;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public void copyTweet(String strTweet){
        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text",strTweet);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), R.string.txt_copied, Toast.LENGTH_LONG).show();
    }
}