package com.example.cov_news.ui.scholar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cov_news.News;
import com.example.cov_news.R;

import java.io.InputStream;
import java.util.Date;

public class ScholarItem extends AppCompatActivity {
    Scholar scholar;
    private TextView mHeader;
    private TextView mBody;
    private ImageView imageView;
    @SuppressWarnings("deprecation")
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scholar_item);
        Intent intent = getIntent();
        scholar = (Scholar) intent.getSerializableExtra("scholar");
        mHeader = this.findViewById(R.id.title);
        mBody = this.findViewById(R.id.content);
        imageView = this.findViewById(R.id.figure);
        if (scholar != null) {
            mHeader.setText(scholar.name_zh);
            mBody.setText(Html.fromHtml(String.format("<table><tbody>" +
                    "<small><tr><th>个人主页</th> <td> <small>%s</small></td></tr></small>" +
                    "<>"+
                    "</tbody></table>" +
                    "<p>%s</p>" +
                    "<p>%s</p>",scholar.homepage, scholar.bio, scholar.note)));
        }
        new DownloadImageTask(imageView).execute(scholar.img_url);
    }
}