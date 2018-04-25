package com.boanergepro.pagomovil.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import com.boanergepro.pagomovil.R;
import com.boanergepro.pagomovil.adapters.ContactAdapter;
import com.boanergepro.pagomovil.models.Contact;
import com.boanergepro.pagomovil.resources.Bank;

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Colocar flecha ir atras en el navbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Contactos");
        Toast.makeText(this, "Seleccione un destinatario", Toast.LENGTH_LONG).show();

        // DB realm
        realm = Realm.getDefaultInstance();
        // Consulta
        contacts = realm.where(Contact.class).findAll();
        // Actualizar la consulta sobre la marcha
        contacts.addChangeListener(this);

        adapter = new ContactAdapter(this, contacts, R.layout.list_view_contact_item);
        listView = (ListView) findViewById(R.id.listViewContacts);
        listView.setAdapter(adapter);

        // Setear contexto en los eventos
        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fabAddContact);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAlertForCreatingContact("Agregar un nuevo contacto.", "Rellene todos los campos por favor", "create", 0);
            }
        });
    }

    // ************************** CRUD Actions **************************
    private void CreateNewContact(String namesValue, String codeValue, String cedulaValue, String phoneValue) {
        realm.beginTransaction();
        Contact contact = new Contact(namesValue, codeValue, cedulaValue, phoneValue);
        realm.copyToRealm(contact);
        realm.commitTransaction();
    }

    private void EditContact(String namesValue, String codeValue, String cedulaValue, String phoneValue,int position) {
        realm.beginTransaction();
        Contact singleContac = contacts.get(position);
        singleContac.setNames(namesValue);
        singleContac.setCode(codeValue);
        singleContac.setCedula(cedulaValue);
        singleContac.setPhone(phoneValue);
        realm.copyToRealmOrUpdate(singleContac);
        realm.commitTransaction();
    }

    private void DeleteContact(int position) {
        // postion equivale al id
        realm.beginTransaction();
        Contact singleContact = contacts.get(position);
        singleContact.deleteFromRealm();
        realm.commitTransaction();
    }

    // ************************** Dialog crear contacto **************************
    private void ShowAlertForCreatingContact(String title, String message, String action, final int position) {
        /*
        * parametros
        * title = Titulo del cuadro de dialogo
        * message = Mensaje del cuadro de dialogo
        * action = (edit o create) denotara el comportamiento del cuadro de dialogo
        * position = denota la posicion del elemento presionado en el list view, si la action es crear
        * el valor por defecto sera 0 si es editar sera diferente.
        * */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_contact, null);
        builder.setView(viewInflated);

        if (action != null) {
            spinnerAlert = (Spinner) viewInflated.findViewById(R.id.spinnerAlertContact);
            final EditText names = (EditText) viewInflated.findViewById(R.id.namesAlertContact);
            final EditText cedula = (EditText) viewInflated.findViewById(R.id.cedulaAlertContact);
            final EditText phone = (EditText) viewInflated.findViewById(R.id.phoneAlertContact);

            if (action.equals("create")) {
                // Crear spinner vacio
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

                builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String namesValue = names.getText().toString().trim();
                        String codeValue = code.trim();
                        String cedulaValue = cedula.getText().toString().trim();
                        String phoneValue = phone.getText().toString().trim();

                        if (namesValue.length() > 0 && codeValue.length() > 0 && cedulaValue.length() > 0 && phoneValue.length() > 0) {
                            CreateNewContact(namesValue, codeValue, cedulaValue, phoneValue);
                        } else {
                            Toast.makeText(getApplicationContext(), "Todos los campos son requeridos.!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else if (action.equals("edit")) {
                // Obtener de la base de datos el registro que se desea editar
                final String namesValue = contacts.get(position).getNames();
                final String codeValue = contacts.get(position).getCode();
                final String cedulaValue = contacts.get(position).getCedula();
                final String phoneValue = contacts.get(position).getPhone();

                // Cargar los datos en el formulario

                // Obtener la posicion del codigo en el arreglo para obtener el banco
                int itemBank = 0;
                for (int i = 0; i < Bank.codes.length; i++) {
                    if (Bank.codes[i].equals(codeValue)) {
                        itemBank = i;
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Bank.bancos);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAlert.setAdapter(adapter);
                spinnerAlert.setSelection(itemBank);

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

                names.setText(namesValue);
                cedula.setText(cedulaValue);
                phone.setText(phoneValue);

                builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String namesEdit = names.getText().toString().trim();
                        String cedulaEdit = cedula.getText().toString().trim();
                        String phoneEdit = phone.getText().toString().trim();

                        if (namesEdit.length() > 0 && code.length() > 0 && cedulaEdit.length() > 0 && phoneEdit.length() > 0) {
                            EditContact(namesEdit, code, cedulaEdit, phoneEdit, position);
                        } else {
                            Toast.makeText(getApplicationContext(), "Ningun campo puede estar vacio.!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // ************************** Dialog opciones del contacto **************************
    private void ShowAlertForOptinonsContac(String title, String message, final int position) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_options_contact, null);
        builder.setView(viewInflated);

        final AlertDialog dialog = builder.create();

        // Instancia de botones
        ImageButton imgBtnEditar = (ImageButton) viewInflated.findViewById(R.id.alertImageButtonEdit);
        ImageButton imgBtnDelete = (ImageButton) viewInflated.findViewById(R.id.alertImageButtonDelete);

        imgBtnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Editar
                ShowAlertForCreatingContact("Editar cotacto", "Rellene los campos que desea actualizar","edit", position);
                dialog.dismiss();
            }
        });

        imgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Borrar el registro seleccionado
                DeleteContact(position);
                dialog.dismiss();
            }
        });

        // Mostrar el cuadrode dialogo
        dialog.show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ShowAlertForOptinonsContac("Menu de opciones","Por favor seleccione lo que desea hacer", position);
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
