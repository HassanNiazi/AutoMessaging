package nexgen.automessaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Hassan Niazi on 10/6/2015.
 */
public class ReceivedSMS extends BroadcastReceiver {
    private EncryptedMessaging encryptedMessaging;
    private static Cipher decryptCipher;
    public ReceivedSMS(EncryptedMessaging encryptedMessaging){

        this.encryptedMessaging = encryptedMessaging;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        if (bundle == null) return;

        Object[] pdus = (Object[]) bundle.get("pdus");
        for (Object pdu : pdus) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
            String sender = sms.getOriginatingAddress();
            String message = sms.getMessageBody();
            byte[] returnArray = new byte[0];



            if (sender.length()>0) {
                if (message.length() > 0) {
                    Cipher c = null;
                    String SecretKey = encryptedMessaging.getSecretKey();
                    try {
                         c = Cipher.getInstance("AES");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    }

                    javax.crypto.SecretKey key = new SecretKeySpec(SecretKey.getBytes(), "AES"); // Declaration of new secret key for the AES

                    try {
                        c.init(Cipher.DECRYPT_MODE, key); // Config/Initialize/SetProperties  of the cipher object to Decrypt Mode and set the key
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }


                    returnArray = hex2byte(message.getBytes()); // See hex2Bytes Declaration for details.


                    try {
                        returnArray = c.doFinal(returnArray); // Apply Decryption on the returnArray and get the result in returnArray again.
                        try {
                            message = new String(returnArray,"UTF-8"); // convert the bytes to meaningfull string can't  use .toString here because that will repre-
                            //-sent the bytes as string content but wont actually convert the data type of the data
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    }

                }




            if (PhoneNumberUtils.compare(sender, encryptedMessaging.getNumber())) {
                encryptedMessaging.addToMessages(sender, message);  // Add message to main meesage field. i.e. Two line array Adapter.
            }
        }
    }
}

    //// generic method to convert received data which is actually hex represented in byte array to actual byte array
    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException();
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }
}
