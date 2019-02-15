package eu.rkosir.feecollector.activity.teamManagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import eu.rkosir.feecollector.R;

public class FullScreenImage extends AppCompatActivity {

    private ImageView mQrImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        getSupportActionBar().setTitle("QR Code");
        mQrImage = findViewById(R.id.qr);

        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle!=null)
        {
            String url =(String) bundle.get("url");
            Picasso.get().load(url).error(R.mipmap.ic_team_member_no_photo).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(mQrImage);
        }
    }
}
