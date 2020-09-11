package com.example.cov_news.ui.scholar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cov_news.R;

import java.io.InputStream;

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
            mBody.setText(Html.fromHtml(String.format(
                    "<p><small>个人主页 : <small>%s</small></small></p>" +
                    "<p><small>职位: %s</small> </p>"+
                    "<p><small>所属机构: %s</small></p>"+
                    "<p>%s</p>" +
                    "<p>%s</p>",scholar.homepage, scholar.position, scholar.affiliation,scholar.bio, scholar.note)));
        }
        new DownloadImageTask(imageView).execute(scholar.img_url);
    }
}