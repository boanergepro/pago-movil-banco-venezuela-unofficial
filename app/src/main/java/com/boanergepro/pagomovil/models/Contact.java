package com.boanergepro.pagomovil.models;

import com.boanergepro.pagomovil.app.MyApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by boanergepro on 12/04/18.
 */

public class Contact extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String names;
    @Required
    private String code;
    @Required
    private String cedula;
    @Required
    private String phone;

    public Contact() {

    }
    public Contact(String names, String code, String cedula, String phone) {

        this.id = MyApplication.ContactID.incrementAndGet();
        this.names = names;
        this.code = code;
        this.cedula = cedula;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public String getNames() { return names;}
    public void  setNames(String names) { this.names = names;}

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getCedula() {
        return cedula;
    }
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String cedula) {
        this.phone = phone;
    }
}