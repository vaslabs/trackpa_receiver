package com.vaslabs.trackpa_receiver;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.vaslabs.trackpa_receiver.encryption.EncryptionManager;
import com.vaslabs.trackpa_receiver.qr.QRCodeManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PublicKey;

public class RSASetupActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsasetup);
        imageView = (ImageView) (findViewById(R.id.qr_code_image_view));
        GenerateEncryptionKeysAsyncTask generateEncryptionKeysAsyncTask = new GenerateEncryptionKeysAsyncTask();
        generateEncryptionKeysAsyncTask.execute(this);
    }

    private class GenerateEncryptionKeysAsyncTask extends AsyncTask<Context, Void, String> {
        private Exception exception = null;

        @Override
        protected String doInBackground(Context... params) {
            Context context = params[0];
            EncryptionManager em = new EncryptionManager();
            PublicKey pk;
            try {
                pk = em.getPublicKey(context);

            } catch (FileNotFoundException fnfe) {
                try {
                    em.generateKeys(context);
                    pk = em.getPublicKey(context);
                } catch (Exception e) {
                    exception = e;
                    return null;
                }
            } catch (IOException e) {
                exception = e;
                return null;
            }
            String publicKey = EncryptionManager.encodePublicKey(pk);
            return publicKey;
        }

        @Override
        protected void onPostExecute(String result) {
            if (exception != null) {
                Toast.makeText(imageView.getContext(), exception.toString(), Toast.LENGTH_LONG);
            }
            Bitmap bitmap = QRCodeManager.generateQRCode(result);
            imageView.setImageBitmap(bitmap);
            Toast.makeText(imageView.getContext(), imageView.getContext().getString(R.string.scan_guide),
                    Toast.LENGTH_LONG);
        }
    }
}


