package com.appjombang.myskripsi.Bantuan;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appjombang.myskripsi.R;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listDataParent;
    private HashMap<String, List<String>> listHashMap;

    public ExpandableListAdapter(Context context, List<String> listDataParent, HashMap<String, List<String>> listHashMap) {
        this.context = context;
        this.listDataParent = listDataParent;
        this.listHashMap = listHashMap;
    }

    @Override
    public int getGroupCount() {
        return listDataParent.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return listHashMap.get(listDataParent.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return listDataParent.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listHashMap.get(listDataParent.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    Integer[] imgid={
            R.drawable.ic_visibility_black_24dp,
            R.drawable.ic_view_list_24dp,
            R.drawable.ic_insert_chart_24dp,
            R.drawable.ic_access_alarms_24dp,
            R.drawable.ic_phone_black_24dp
    };
    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String parentTitle = (String) getGroup(i);
        if (view==null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_parent, null);
        }
        ImageView imPat = (ImageView) view.findViewById(R.id.imgparent);
        imPat.setImageResource(imgid[i]);
        TextView lblParent = (TextView) view.findViewById(R.id.parent);
        lblParent.setTypeface(null, Typeface.BOLD);
        lblParent.setText(parentTitle);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final String childText = (String)getChild(i, i1);
        if (view==null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_child, null);
        }
        TextView lblChild = (TextView) view.findViewById(R.id.child);
        lblChild.setText(childText);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
