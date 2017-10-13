package com.mask.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mask.R;
import com.mask.base.MyBaseAdapter;
import com.mask.bean.MyDevice;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/3/25.
 */
public class EquipmentAdapter extends MyBaseAdapter<MyDevice> {
    Context mContext;
    List<MyDevice> mList = new ArrayList<>();

    public EquipmentAdapter(List<MyDevice> list, Context context) {
        super(list);
        mContext = context;
        mList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(mContext,
                    R.layout.equipment_item, null);
            new ViewHolder(convertView);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.device_name.setText(mList.get(position).getName());
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
