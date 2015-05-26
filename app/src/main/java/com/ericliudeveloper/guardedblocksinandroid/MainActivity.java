package com.ericliudeveloper.guardedblocksinandroid;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    TextView tvDisplay;
    Button btUpdate;
    Drop mDropbox;

    static Handler mHandler;


    private static class MyHandler extends Handler{
        TextView mTv;
        public MyHandler(TextView textView){
            mTv = textView;
        }

        @Override
        public void handleMessage(Message msg) {
            String text = (String) msg.obj;
            mTv.setText(text);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDropbox = new Drop();
        tvDisplay = (TextView) findViewById(R.id.tvDisplay);
        btUpdate = (Button) findViewById(R.id.btUpdate);

        btUpdate.setOnClickListener(this);

        mHandler = new MyHandler(tvDisplay);
    }

    @Override
    public void onClick(View v) {
        UpdateTask updateTask = new UpdateTask(mDropbox);
        String[] msgs = {"Today is a good day."};
        updateTask.execute(msgs);

        new ReadMessageTask(mDropbox).start();
    }

    private static class ReadMessageTask extends Thread{
        Drop dropbox;

        public ReadMessageTask(Drop dropbox){
            this.dropbox = dropbox;
        }

        @Override
        public void run() {
            String text = dropbox.take();
            Message msg = Message.obtain();
            msg.obj = text;
            mHandler.sendMessage(msg);
        }
    }

    private static class UpdateTask extends AsyncTask<String, Void, Void> {

        Drop dropbox;

        public UpdateTask(Drop dropbox) {
            this.dropbox = dropbox;
        }


        @Override
        protected Void doInBackground(String... params) {
            String message = params[0];
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dropbox.put(message);
            return null;
        }


    }


    private static class Drop {
        private boolean empty = true;
        private String message = "";

        public synchronized void put(String message) {
            while (!empty) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            empty = false;
            this.message = message;
            // DO NOT forget to notify all
            notifyAll();
        }


        public synchronized String take() {
            while (empty) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            empty = true;
            notifyAll();
            return message;
        }
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
