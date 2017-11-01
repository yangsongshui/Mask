package com.mask.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mask.R;
import com.mask.bean.Light;
import com.mask.bean.Lights;


/**
 * Created by Administrator on 2016/3/25.
 */
public class EquipmentAdapter extends BaseAdapter {
    Context mContext;

    public EquipmentAdapter(Context context) {
        mContext = context;

    }

    @Override
    public int getCount() {
        return Lights.getInstance().size();
    }

    @Override
    public Light getItem(int position) {
        return Lights.getInstance().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(mContext,
                    R.layout.equipment_item, null);
            new ViewHolder(convertView);
        }
        Light light = this.getItem(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.device_name.setText("口罩"+light.getLabel2());
        return convertView;
    }


    class ViewHolder {
        TextView device_name;

        public ViewHolder(View view) {
            device_name = (TextView) view.findViewById(R.id.device_name);
            view.setTag(this);
        }
    }
}
