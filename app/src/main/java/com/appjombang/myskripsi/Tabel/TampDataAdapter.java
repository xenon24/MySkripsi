package com.appjombang.myskripsi.Tabel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appjombang.myskripsi.R;

import java.util.ArrayList;

public class TampDataAdapter extends RecyclerView.Adapter<TampDataAdapter.TampDataHolder> {
    private Context mContext;
    private ArrayList<TampData> mTampDataList;

    public TampDataAdapter (Context context, ArrayList<TampData> tampDataList){
        mContext = context;
        mTampDataList = tampDataList;
    }

    @NonNull
    @Override
    public TampDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_tamdata, parent, false);
        return new TampDataHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TampDataHolder holder, int position) {
        TampData currentItem = mTampDataList.get(position);
        String suhu = currentItem.getSuhu();
        String kelembaban = currentItem.getKelembaban();
        String waktu = currentItem.getWaktu();

        holder.mSuhu.setText(suhu);
        holder.mKelembaban.setText(kelembaban);
        holder.mWaktu.setText(waktu);
    }

    @Override
    public int getItemCount() {
        return mTampDataList.size();
    }

    public class TampDataHolder extends RecyclerView.ViewHolder{
        public TextView mSuhu;
        public TextView mKelembaban;
        public TextView mWaktu;

        public TampDataHolder(View itemView) {
            super(itemView);
            mSuhu = itemView.findViewById(R.id.textView1);
            mKelembaban = itemView.findViewById(R.id.textView2);
            mWaktu = itemView.findViewById(R.id.textView3);
        }
    }
}
