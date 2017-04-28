package org.schulcloud.mobile.ui.base;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.schulcloud.mobile.R;

public class NavItemAdapter extends BaseAdapter {

    private Activity activity;
    private String[] titles;
    private int[] icIds;

    public NavItemAdapter(Activity context, String[] titles, int[] icIds) {
        this.activity = context;
        this.titles = titles;
        this.icIds = icIds;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return titles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout relativeLayout =
                (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.drawer_list_item, null);
        ((TextView) relativeLayout.findViewById(R.id.drawer_item_textView)).setText(titles[position]);
        ((ImageView) relativeLayout.findViewById(R.id.drawer_item_icon)).setImageResource(icIds[position]);
        return relativeLayout;
    }
}
