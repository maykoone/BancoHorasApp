package br.com.maykoone.app.bancohoras;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import br.com.maykoone.app.bancohoras.db.RegistroPontoEntity;

import static br.com.maykoone.app.bancohoras.Util.calculeTime;
import static br.com.maykoone.app.bancohoras.Util.formatTime;

/**
 * Created by maykoone on 09/08/15.
 */
public class ExpandableAdapter extends BaseExpandableListAdapter {
    private Map<String, List<RegistroPontoEntity>> mListChildren;
    private List<String> mListGroup;
    private Context mContext;
    private LayoutInflater mInflater;

    public ExpandableAdapter(Context context, List<String> listGroup, Map<String, List<RegistroPontoEntity>> list) {
        mContext = context;
        mListChildren = list;
        mListGroup = listGroup;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getGroupCount() {
        return mListGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListChildren.get(mListGroup.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListChildren.get(mListGroup.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //view holder for recycling
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.group_item_expandable_list, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);

            viewHolder.textView = (TextView) convertView.findViewById(R.id.textViewGroup);
            viewHolder.tvTotalTime = (TextView) convertView.findViewById(R.id.tvTotalTime);
            viewHolder.tvCountTime = (TextView) convertView.findViewById(R.id.tvCountTime);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(mListGroup.get(groupPosition));
        long totalTimeMillis = calculeTime(mListChildren.get(mListGroup.get(groupPosition)));
        String formattedTotalTime = formatTime(totalTimeMillis);
        viewHolder.tvTotalTime.setText("Total: " + formattedTotalTime);
        String formattedCountTime = formatTime(totalTimeMillis - (8 * 60 * 60 * 1000));//8 hours
        viewHolder.tvCountTime.setText("Saldo: " + formattedCountTime);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //view holder for recycling
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_custom_item, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);

            viewHolder.textView = (TextView) convertView.findViewById(R.id.textview);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageview);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(((RegistroPontoEntity) getChild(groupPosition, childPosition)).getDataEvento());
        if (childPosition % 2 == 0) {
            viewHolder.imageView.setImageResource(R.drawable.ic_action_in_green);
        } else {
            viewHolder.imageView.setImageResource(R.drawable.ic_action_out_red);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void updateData(List<String> listHeaders, Map<String, List<RegistroPontoEntity>> listChildren) {
        Log.i(ExpandableAdapter.class.getName(), "updateData");
        this.mListGroup = listHeaders;
        this.mListChildren = listChildren;
        this.notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
        TextView tvTotalTime;
        TextView tvCountTime;
    }
}