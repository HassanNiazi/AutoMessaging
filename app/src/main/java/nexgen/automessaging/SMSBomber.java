package nexgen.automessaging;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Hassan Niazi on 10/3/2015.
 */
public class SMSBomber extends Activity {

    EditText phoneNumber,noOfBombs,messageBody,delayET;
    Button attack;
    String phNumber,message;
    int noofAttacks,delay=100;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smsbomber);
        InitializeGUI();

        attack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(delayET.getText().length()>0)
                {
                    delay = Integer.parseInt(delayET.getText().toString());
                    if(delay==0 || delay <80)
                    {
                        Toast.makeText(getBaseContext(), "Low delay may cause problem sending messages", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                     delay = 100;
                    delayET.setText(delay+"");
                    Toast.makeText(getBaseContext(), "invalid Delay! Value Set to "+ delay+" milliseconds", Toast.LENGTH_SHORT).show();
                }
                if(phoneNumber.getText().length()>0)
                {
                    phNumber = phoneNumber.getText().toString();
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Enter a phone number... :/", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(noOfBombs.getText().length()>0)
                {
                    noofAttacks = Integer.parseInt(noOfBombs.getText().toString());
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Please enter No of Messages :)", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(messageBody.getText().length()>0)
                {
                    message = messageBody.getText().toString();
                }
                else
                {

                    Toast.makeText(getBaseContext(), "Empty Message", Toast.LENGTH_SHORT).show();
                    return;
                }

                Thread timer = new Thread(){
                    public void run(){

                            SmsManager myMessanger = SmsManager.getDefault();

                            for (int i =0;i<noofAttacks;i++)
                            {
                                try {
                                    sleep(delay);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                myMessanger.sendTextMessage(phNumber, null, message, null, null);
                               // Toast.makeText(getBaseContext(),i + " Message(s) Sent", Toast.LENGTH_SHORT).show();
                            }
                          //  Toast.makeText(getBaseContext(),noofAttacks + " Message(s) Sent", Toast.LENGTH_SHORT).show();


                            }
                };
                timer.start();
                 Toast.makeText(getBaseContext(),noofAttacks + " Message(s) Sent", Toast.LENGTH_SHORT).show();



            }
        });

    }

    private void InitializeGUI()
    {
        phoneNumber=(EditText) findViewById(R.id.PhoneNumber);
        noOfBombs=(EditText) findViewById(R.id.NoOfBombs);
        messageBody=(EditText) findViewById(R.id.messageBody);
        attack = (Button) findViewById(R.id.Attack);
        delayET = (EditText) findViewById(R.id.Delay);
    }




}
