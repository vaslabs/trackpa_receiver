package com.vaslabs.trackpa_receiver.qr;

import android.graphics.Bitmap;

import net.glxn.qrgen.android.QRCode;

/**
 * Created by vnicolaou on 09/01/16.
 */
public class QRCodeManager {

    public static Bitmap generateQRCode(String data) {
        return QRCode.from(data).withSize(512, 512).bitmap();
    }

}
