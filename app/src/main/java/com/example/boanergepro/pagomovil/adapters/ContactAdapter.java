package com.example.boanergepro.pagomovil.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.boanergepro.pagomovil.R;
import com.example.boanergepro.pagomovil.models.Contact;

import java.util.List;

/**
 * Created by boanergepro on 17/04/18.
 */

public class ContactAdapter extends BaseAdapter{

    private Context context;
    private List<Contact> list;
    private int layout;

    public ContactAdapter(Context context, List<Contact> contacts, int layout) {
        this.context = context;
        this.list = contacts;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Contact getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh;

        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(layout, null);
            vh = new ViewHolder();
            vh.names = (TextView) convertView.findViewById(R.id.namesListView);
            vh.cedula = (TextView) convertView.findViewById(R.id.cedulaListView);
            vh.phone = (TextView) convertView.findViewById(R.id.phoneListView);
            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Contact contact = list.get(position);
        vh.names.setText(contact.getNames());
        vh.cedula.setText(contact.getCedula());
        vh.phone.setText(contact.getPhone());

        return convertView;
    }

    public class ViewHolder{
        TextView names;
        TextView cedula;
        TextView phone;
    }
}
