package com.example.boanergepro.pagomovil.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.boanergepro.pagomovil.R;
import com.example.boanergepro.pagomovil.adapters.ContactAdapter;
import com.example.boanergepro.pagomovil.models.Contact;
import com.example.boanergepro.pagomovil.resources.Bank;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ContactActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, RealmChangeListener<RealmResults<Contact>>{

    private FloatingActionButton fab;
    private Spinner spinnerAlert;
    //Codigo del banco
    private String code;

    private Realm realm;
    private ListView listView;
    private ContactAdapter adapter;
    private RealmResults<Contact> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        //Colocar flecha ir atras en el navbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toast.makeText(this, "Seleccione un destinatario", Toast.LENGTH_LONG).show();

        // DB realm
        realm = Realm.getDefaultInstance();
        // Consulta
        contacts = realm.where(Contact.class).findAll();
        // Actualizar la consulta sobre la marcha
        contacts.addChangeListener(this);

        adapter = new ContactAdapter(this, contacts, R.layout.list_view_contact_item);
        Log.e("Debug", contacts.toString());
        listView = (ListView) findViewById(R.id.listViewContacts);
        listView.setAdapter(adapter);

        // Setear contexto en los eventos
        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fabAddContact);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAlertForCreatingContact("Agregar un nuevo contacto.", "Rellene todos los campos por favor");
            }
        });
    }

    // ** CRUD Actions
    private void createCreateNewContact(String namesValue, String codeValue, String cedulaValue, String phoneValue) {
        realm.beginTransaction();
        Contact contact = new Contact(namesValue, codeValue, cedulaValue, phoneValue);
        realm.copyToRealm(contact);
        realm.commitTransaction();
    }

    // ** Dialog crear contacto
    private void ShowAlertForCreatingContact(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_contact, null);
        builder.setView(viewInflated);

        spinnerAlert = (Spinner) viewInflated.findViewById(R.id.spinnerAlertContact);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Bank.bancos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAlert.setAdapter(adapter);

        //Accion al presionar un item del spinner
        spinnerAlert.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //Toast.makeText(adapterView.getContext(), (String) adapterView.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                code = Bank.codes[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final EditText names = (EditText) viewInflated.findViewById(R.id.namesAlertContact);
        final EditText cedula = (EditText) viewInflated.findViewById(R.id.cedulaAlertContact);
        final EditText phone = (EditText) viewInflated.findViewById(R.id.phoneAlertContact);

        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String namesValue = names.getText().toString().trim();
                String codeValue = code.trim();
                String cedulaValue = cedula.getText().toString().trim();
                String phoneValue = phone.getText().toString().trim();

                if (namesValue.length() > 0 && codeValue.length() > 0 && cedulaValue.length() > 0 && phoneValue.length() > 0) {
                    createCreateNewContact(namesValue, codeValue, cedulaValue, phoneValue);
                } else {
                    Toast.makeText(getApplicationContext(), "Todos los campos son requeridos.!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // ** Dialog opciones del contacto
    private void ShowAlertForOptinonsContac() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ContactActivity.this, "Presionado el boton cancelar", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ContactActivity.this, "Presionado el boton editar", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ContactActivity.this, "Presionado el boton borrar", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ShowAlertForOptinonsContac();
        Toast.makeText(ContactActivity.this, id + "", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(ContactActivity.this, FormActivity.class);
        intent.putExtra("code", contacts.get(position).getCode());
        intent.putExtra("cedula", contacts.get(position).getCedula());
        intent.putExtra("phone", contacts.get(position).getPhone());
        startActivity(intent);
    }

    @Override
    public void onChange(RealmResults<Contact> element) {
        adapter.notifyDataSetChanged();
    }
}
