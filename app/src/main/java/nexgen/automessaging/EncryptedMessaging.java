package nexgen.automessaging;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class EncryptedMessaging extends Activity {

    private static ArrayList<Message> list;
    private static MessageArrayAdapter adapter;
    private static Cipher encryptCipher;
    Button send;
    ToggleButton showkey;
    EditText phoneNumber, secretKey, messageBody;
    TextView keyLength;

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp;
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs += ("0" + stmp);
            else
                hs += stmp;
        }
        return hs.toUpperCase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.encryptedmessaging);
        initialize();
        secretKey.setBackgroundColor(Color.RED);
        send.setEnabled(false);
        final ListView listview = (ListView) findViewById(R.id.messageList);
        list = new ArrayList<>();
        adapter = new MessageArrayAdapter(this, list);
        listview.setAdapter(adapter);
        ReceivedSMS receivedSMS = new ReceivedSMS(this);
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED"); // intent to recieve message ! Check again the concept of intents Activities and ...
        registerReceiver(receivedSMS, filter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(v);
            }
        });

        showkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showkey.isChecked()) {
                    secretKey.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    secretKey.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        secretKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (secretKey.getText().length() != 16) {
                    Toast.makeText(EncryptedMessaging.this, "Please enter a valid 16 char Key", Toast.LENGTH_SHORT).show();
                }
            }
        });

        secretKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                keyLength.setText(secretKey.getText().length()+"");

                if (secretKey.getText().length() != 16) {
                    secretKey.setBackgroundColor(Color.RED);
                    send.setEnabled(false);
                } else {
                    secretKey.setBackgroundColor(Color.GREEN);
                    send.setEnabled(true);
                }
                //keyLength.setText(count+"");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

public void initialize()
{
    send = (Button) findViewById(R.id.SendButton);
    showkey = (ToggleButton) findViewById(R.id.ShowKey);
    phoneNumber = (EditText) findViewById(R.id.PhoneNo);
    secretKey = (EditText) findViewById(R.id.Key);
    keyLength = (TextView) findViewById(R.id.KeyLength);
    messageBody = (EditText) findViewById(R.id.messageBody);
}

    public void sendMessage(View view) {
        String number = phoneNumber.getText().toString();
       String message = messageBody.getText().toString();


        byte[] returnArray = new byte[0];
        try {
            String SecretKey = secretKey.getText().toString();
            Cipher c = Cipher.getInstance("AES"); // Cipher object declaration and initialization with AES Algorithm
            javax.crypto.SecretKey key = new SecretKeySpec(SecretKey.getBytes(), "AES"); // Declaration and init of secret key object

            try {
                c.init(Cipher.ENCRYPT_MODE, key);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }

            try {
                returnArray = c.doFinal(message.getBytes()); // running Algorithm on string data
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        // message = returnArray.toString();
        message = byte2hex(returnArray);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(
                number, null, message, null, null);
        Toast.makeText(getBaseContext(), "Message Sent", Toast.LENGTH_SHORT).show();
        addToMessages("Me", messageBody.getText().toString());
        messageBody.setText("");
    }

    public void addToMessages(String sender, String message) {

        list.add(new Message(sender, message));
        adapter.notifyDataSetChanged();
    }

    public String getNumber() {
        return phoneNumber.getText().toString();
    }

    public String getSecretKey(){return secretKey.getText().toString(); }

    public class MessageArrayAdapter extends TwoLineArrayAdapter<Message> {

        public MessageArrayAdapter(Context context, ArrayList<Message> Messages) {
            super(context, Messages);
        }

        @Override
        public String lineOneText(Message message) {
            return message.getMessage();
        }

        @Override
        public String lineTwoText(Message message) {
            return message.getSender();
        }
    }

    public class Message {
        private final String sender;
        private final String message;

        public Message(String sender, String message) {

            this.sender = sender;
            this.message = message;
        }

        public String getSender() {
            return sender;
        }

        public String getMessage() {
            return message;
        }
    }

    public abstract class TwoLineArrayAdapter<T> extends ArrayAdapter<T> {
        private int mListItemLayoutResId;

        public TwoLineArrayAdapter(Context context, ArrayList<T> ts)
        {
            this(context, android.R.layout.two_line_list_item, ts);
        }

        public TwoLineArrayAdapter(Context context,int listItemLayoutResourceId, ArrayList<T> ts)
        {
            super(context, listItemLayoutResourceId, ts);
            mListItemLayoutResId = listItemLayoutResourceId;
        }

        @Override
        public android.view.View getView(
                int position,
                View convertView,
                ViewGroup parent) {


            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View listItemView = convertView;
            if (null == convertView) {
                listItemView = inflater.inflate(
                        mListItemLayoutResId,
                        parent,
                        false);
            }

            TextView lineOneView = (TextView) listItemView.findViewById(
                    android.R.id.text1);
            TextView lineTwoView = (TextView) listItemView.findViewById(
                    android.R.id.text2);

            T t = getItem(position);
            lineOneView.setText(lineOneText(t));
            lineTwoView.setText(lineTwoText(t));

            // TODO: Change color
            lineOneView.setTextColor(Color.BLACK);
            lineOneView.setTextSize(18);
            lineTwoView.setTextColor(Color.BLUE);
            lineTwoView.setTextSize(19);


            return listItemView;

        }

        public abstract String lineOneText(T t);

        public abstract String lineTwoText(T t);
    }

}
