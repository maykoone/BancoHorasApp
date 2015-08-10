package br.com.maykoone.app.bancohoras;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by maykoone on 09/08/15.
 */
public class ExpandableAdapter extends BaseExpandableListAdapter {
    private Map<String, List<String>> mListChildren;
    private List<String> mListGroup;
    private Context mContext;
    private LayoutInflater mInflater;

    public ExpandableAdapter(Context context, List<String> listGroup, Map<String, List<String>> list) {
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
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(mListGroup.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //view holder for recycling
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.child_item_expandable_list, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);

            viewHolder.textView = (TextView) convertView.findViewById(R.id.textViewChild);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText((String) getChild(groupPosition, childPosition));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class ViewHolder {
        TextView textView;
    }
}