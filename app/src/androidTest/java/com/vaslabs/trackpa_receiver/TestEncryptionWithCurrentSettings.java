package com.vaslabs.trackpa_receiver;

import com.vaslabs.trackpa_receiver.encryption.EncryptionManager;

/**
 * Created by vnicolaou on 09/01/16.
 */
public class TestEncryptionWithCurrentSettings extends TestEncryption {

    @Override
    public void setUp() {
        encryptionManager = new EncryptionManager();
    }

    @Override
    public void tearDown() {

    }
}
