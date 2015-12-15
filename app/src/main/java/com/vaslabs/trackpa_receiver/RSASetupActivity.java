package com.vaslabs.trackpa_receiver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class RSASetupActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsasetup);
        imageView = (ImageView)(findViewById(R.id.qr_code_image_view));

    }
}
