package com.nanddgroup.contactmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int EDIT = 0, DELETE = 1;

    @Bind(R.id.txtName)
    EditText nameTxt;
    @Bind(R.id.txtPhone)
    EditText phoneTxt;
    @Bind(R.id.txtEmail)
    EditText emailTxt;
    @Bind(R.id.txtAddress)
    EditText addressTxt;
    @Bind(R.id.listView)
    ListView contactListView;
    @Bind(R.id.imgViewContactImage)
    ImageView contactImageImgView;
    @Bind(R.id.btnAdd)
    Button addBtn;
    @Bind(R.id.tabHost)
    TabHost tabHost;
    Uri imageUri = Uri.parse("android.resource://com.nanddgroup.contactmanager/drawable/no_user_logo.png");
    DatabaseHandler dbHandler;
    List<Contact> Contacts = new ArrayList<Contact>();
    int longClickedItemIndex;
    ArrayAdapter<Contact> contactAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHandler = new DatabaseHandler(getApplicationContext());

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("creator"); // make TAG
        tabSpec.setContent(R.id.tabCreator);// pokazivaeem chem zapolnyaem, vse chto nujno (vse View) lojim v tab creator
        tabSpec.setIndicator("Creator");// make name of TAB
        tabHost.addTab(tabSpec);// add TAB to root tab

        tabSpec = tabHost.newTabSpec("list");
        tabSpec.setContent(R.id.tabContactList);
        tabSpec.setIndicator("List");
        tabHost.addTab(tabSpec);

        registerForContextMenu(contactListView);
        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getApplicationContext(),"long click", Toast.LENGTH_LONG).show();
                longClickedItemIndex = position;
                return false;
            }
        });

        nameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addBtn.setEnabled(String.valueOf(nameTxt.getText()).trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (dbHandler.getContactsCount() != 0)
            Contacts.addAll(dbHandler.getAllContacts());

        populateList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    //creating context menu
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.setHeaderIcon(R.drawable.pencil_icon);
        menu.setHeaderTitle("Contact Options");
        menu.add(Menu.NONE, EDIT, menu.NONE, "Edit Contact");
        menu.add(Menu.NONE, DELETE, menu.NONE, "Delete Contact");
    }
    //get selected item
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case EDIT:
                // TODO: Implement editing a contact
                break;
            case DELETE:
                dbHandler.deleteContact(Contacts.get(longClickedItemIndex));
                Contacts.remove(longClickedItemIndex);
                contactAdapter.notifyDataSetChanged();
                break;
        }

        return super.onContextItemSelected(item);
    }

    private boolean contactExists(Contact contact) {
        String name = contact.get_name();
        int contactCount = Contacts.size();

        for (int i = 0; i < contactCount; i++) {
            if (name.compareToIgnoreCase(Contacts.get(i).get_name()) == 0) {
                return true;
            }
        }
        return false;
    }

    private void populateList() {
        contactAdapter = new ContactListAdapter();
        contactListView.setAdapter(contactAdapter);
    }

    @OnClick(R.id.imgViewContactImage)
    public void onImageClicked() {
        Intent intent = new Intent();
        intent.setType("image/*"); // to open gallery
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Contact Image"), 1);
    }

    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == RESULT_OK) {
            if (reqCode == 1) {
                imageUri = data.getData();
                contactImageImgView.setImageURI(data.getData());// set image that was chosen
            }
        }
    }

    @OnClick(R.id.btnAdd)
    public void onButtonClicked() {
        Contact contact = new Contact(dbHandler.getContactsCount(), String.valueOf(nameTxt.getText()), String.valueOf(phoneTxt.getText()), String.valueOf(emailTxt.getText()), String.valueOf(addressTxt.getText()), imageUri);
        if (!contactExists(contact)) {
            dbHandler.createContact(contact);
            Contacts.add(contact);
            contactAdapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), String.valueOf(nameTxt.getText()) + " has been added to your Contact List", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getApplicationContext(), String.valueOf(nameTxt.getText()) + "Already exists. Use a differernt name", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class ContactListAdapter extends ArrayAdapter<Contact> {
        public ContactListAdapter() {
            super(MainActivity.this, R.layout.listview_item, Contacts);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);

            Contact currentContact = Contacts.get(position);

            TextView name = (TextView) view.findViewById(R.id.contactName);
            name.setText(currentContact.get_name());
            TextView phone = (TextView) view.findViewById(R.id.phoneNumber);
            phone.setText(currentContact.get_phone());
            TextView email = (TextView) view.findViewById(R.id.emailAddress);
            email.setText(currentContact.get_email());
            TextView address = (TextView) view.findViewById(R.id.cAddress);
            address.setText(currentContact.get_address());
            ImageView ivContactImage = (ImageView) view.findViewById(R.id.ivContactImage);
            ivContactImage.setImageURI(currentContact.get_imageURI());

            return view;

        }
    }
}
