package com.example.haizhu.myvoiceassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.haizhu.myvoiceassistant.R;
import com.example.haizhu.myvoiceassistant.bean.Result;

import java.util.List;

/**
 * Created by sshunsun on 2017/5/28.
 */
public class RobotChatAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Result> mDatas;

    public RobotChatAdapter(Context context, List<Result> datas)
    {
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Result result = mDatas.get(position);

        ViewHolder viewHolder = null;

        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            if (result.getType() == Result.TYPE_MY)
            {
                convertView = mInflater.inflate(R.layout.chat_item_type_my,
                        parent, false);
                viewHolder.content = (TextView) convertView
                        .findViewById(R.id.type_me_content);
                convertView.setTag(viewHolder);
            } else
            {
                convertView = mInflater.inflate(R.layout.chat_item_type_text,
                        null);


                viewHolder.content = (TextView) convertView
                        .findViewById(R.id.type_text_content);
                convertView.setTag(viewHolder);
            }

        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.content.setText(result.getText());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDatas.get(position).getType() == Result.TYPE_MY) {
            return Result.TYPE_MY;
        }
        return Result.TYPE_TEXT;
    }


    private class ViewHolder
    {
        public TextView content;
    }
}
