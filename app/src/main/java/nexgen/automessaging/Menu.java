package nexgen.automessaging;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Hassan Niazi on 10/3/2015.
 */
public class Menu extends ListActivity {


   String classes[] = {"SMSBomber","EncryptedMessaging"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(Menu.this,android.R.layout.simple_list_item_1,classes));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Class ourClass = null;
        String local = classes[position];
        try {
            ourClass = Class.forName("nexgen.automessaging."+local);
            Intent ourIntent = new Intent(Menu.this,ourClass);
            startActivity(ourIntent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
