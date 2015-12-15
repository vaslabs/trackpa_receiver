package com.vaslabs.trackpa_receiver;

import android.test.AndroidTestCase;
import android.util.Base64;

import com.vaslabs.trackpa_receiver.encryption.EncryptionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * Created by vnicolaou on 15/12/15.
 */
public class TestEncryption extends AndroidTestCase{

    EncryptionManager encryptionManager;

    @Override
    public void setUp() throws Exception {
        encryptionManager = new EncryptionManager();
        encryptionManager.generateKeys(this.getContext());
    }

    @Override
    public void tearDown() {
        this.getContext().getFileStreamPath("rsa").delete();
        this.getContext().getFileStreamPath("rsa.pub").delete();
    }

    public void test_encryption_consistency() throws Exception {
        PublicKey pk = encryptionManager.getPublicKey(this.getContext());
        String base64 = EncryptionManager.encodePublicKey(pk);
        pk = get(base64);
        byte[] encryptionData = encrypt("Hello world", pk);
        String base64String = Base64.encodeToString(encryptionData, Base64.DEFAULT);
        base64String = base64String.replaceAll("\n", "");
        String decryptedData = encryptionManager.decrypt(Base64.decode(base64String, Base64.DEFAULT), this.getContext());
        assertEquals("Hello world", decryptedData);
    }

    private static PublicKey get(String base64) throws NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] keyBytes = Base64.decode(base64, Base64.DEFAULT);
        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public byte[] encrypt(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherData = cipher.doFinal(data.getBytes());
        return cipherData;
    }
}
