package com.example.boanergepro.pagomovil.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.boanergepro.pagomovil.R;
import com.example.boanergepro.pagomovil.resources.Bank;
import com.example.boanergepro.pagomovil.app.SendBroadcastReceiver;

import java.lang.reflect.Array;
import java.util.LinkedHashMap;

import io.realm.Realm;

public class FormActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText cedula;
    private EditText phone;
    private EditText amount;
    private Button btnSendSMS;

    //Codigo del banco
    private String code;

    private static String ACTION_SMS_SENT = "SMS_SENT";
    private static Context mContext;

    // Para intent enviados desde el activity ContacActivity
    private String codeIntent;
    private String cedulaIntent;
    private String phoneIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        // Instancias campos
        spinner = (Spinner) findViewById(R.id.spinner);
        cedula = (EditText) findViewById(R.id.cedula);
        phone = (EditText) findViewById(R.id.phone);
        amount = (EditText) findViewById(R.id.amount);
        btnSendSMS = (Button) findViewById(R.id.button);

        // Verificar si hay intent pasados desde ContacActivity
        if (getIntent().getExtras() != null) {
            codeIntent = getIntent().getExtras().getString("code");
            cedulaIntent = getIntent().getExtras().getString("cedula");
            phoneIntent = getIntent().getExtras().getString("phone");

            // Setear valores en el formulario
            cedula.setText(cedulaIntent);
            phone.setText(phoneIntent);

            // Obtener la posicion del codigo en el arreglo para obtener el banco
            int position = 0;
            for (int i = 0; i < Bank.codes.length; i++) {
                if (Bank.codes[i] == codeIntent) {
                    position = i;
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Bank.bancos);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(position);

            // Colocarle el focus a este input.
            amount.requestFocus();

        } else {

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Bank.bancos);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }


        //Accion al presionar un item del spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //Toast.makeText(adapterView.getContext(), (String) adapterView.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                code = Bank.codes[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Registrar SendBroadcastReceiver para ver si el mesaje se envia.
        registerReceiver(new SendBroadcastReceiver(), new IntentFilter(ACTION_SMS_SENT));

        //Enviar mensaje
        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Recojer los valores del formulario
                String cedulaValue = cedula.getText().toString();
                String phoneValue = phone.getText().toString();
                String amountValue = amount.getText().toString();

                // Validar que los campos no esten vacios.
                if (!cedulaValue.isEmpty() && !phoneValue.isEmpty() && !amountValue.isEmpty()) {
                    // Enviando mensaje
                    sendSMS("PAGAR " + code + " " + phoneValue + " " + cedulaValue + " " + amountValue);
                   // Borrar los campos una vez enviado el mensaje.
                    cedula.setText("");
                    phone.setText("");
                    amount.setText("");
                } else {
                    Toast.makeText(FormActivity.this, "Ningun campo puede estar vacio", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Crear item menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contact, menu);
        return true;
    }

    // Ver el evneto click en el menu contact.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.contacts:
                // Redirigir al activity ContactActivity
                Intent intent =  new Intent(FormActivity.this, ContactActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendSMS(String message) {
        Intent sendIntent = new Intent(ACTION_SMS_SENT);
        PendingIntent piSent = PendingIntent.getBroadcast(mContext, 0, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage("2662", null, message, piSent, null);
    }
}