package com.dqs.shangri.photolistview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by admin on 2017/3/1.
 */

public class ListApdater extends BaseAdapter {

    public ListApdater(List<ListInfo> list, Context context) {
        this.list = list;
        this.context = context;
    }
    private List<ListInfo> list;
    private Context context;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder holder = null;

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
            view = convertView;
            holder.mTextView.setTag(position);
            holder.mTextView.setClickable(false);
        } else {
            view = View.inflate(context, R.layout.item_show_public_chat_redpacket_message, null);
            holder = new ViewHolder(view);

            view.setTag(holder);
            holder.mTextView.setTag(position);
        }

        final int targetPosition = (int) holder.mTextView.getTag();
        // fixed IndexOutOfBoundsException
        if(targetPosition < 0 || targetPosition >= list.size()){
            return view;
        }

        holder.mTextView.setText(list.get(targetPosition).str);
        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        if (list != null) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    class ViewHolder {
        TextView mTextView;

        ViewHolder(View root) {
            mTextView = (TextView) root.findViewById(R.id.tv_show_publicchat_msg_content);
        }
    }
}
