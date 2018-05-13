package com.boanergepro.pagomovil.app;

import android.app.Application;
import android.os.SystemClock;

import com.boanergepro.pagomovil.models.Contact;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by boanergepro on 12/04/18.
 */

public class MyApplication  extends Application {

    public static AtomicInteger ContactID = new AtomicInteger();

    @Override
    public void onCreate() {
        setUpRealmConfig();
        //Retardo para que el splash dure por 2600 ms
        SystemClock.sleep(2600);

        Realm realm = Realm.getDefaultInstance();

        ContactID = getIdByTable(realm, Contact.class);

        realm.close();
        super.onCreate();

    }

    // Configuracion de realm
    private void setUpRealmConfig() {

        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .name("pago-movil.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    // Configuracion para autoincrmentar el id de las tablas
    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass) {
        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0) ? new AtomicInteger(results.max("id").intValue()) : new AtomicInteger();
    }
}
