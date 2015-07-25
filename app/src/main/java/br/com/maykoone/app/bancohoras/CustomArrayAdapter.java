package br.com.maykoone.app.bancohoras;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.maykoone.app.bancohoras.db.RegistroPontoEntity;

/**
 * Created by maykoone on 09/07/15.
 */
public class CustomArrayAdapter extends ArrayAdapter<RegistroPontoEntity> {
    public CustomArrayAdapter(Context context, List<RegistroPontoEntity> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RegistroPontoEntity item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_custom_item, parent, false);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.textview);
        tv.setText(item.getDataEvento());
        ImageView iv = (ImageView) convertView.findViewById(R.id.imageview);

        if (position % 2 == 0) {
            iv.setImageResource(R.drawable.ic_action_in_green);
        } else {
            iv.setImageResource(R.drawable.ic_action_out_red);
        }
        return convertView;
    }
}
