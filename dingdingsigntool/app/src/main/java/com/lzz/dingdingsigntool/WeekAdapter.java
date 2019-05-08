package com.lzz.dingdingsigntool;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;


/**
 * @author lzz
 * @time 19-5-6 下午2:41
 */
public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.WeekInfoViewHolder> {

    private Context context;

    private List<WeekDayInfo> weekDayInfoList;

    private ItemCheckBoxSelect listerner;

    public WeekAdapter(Context context, List<WeekDayInfo> weekDayInfoList,ItemCheckBoxSelect listerner) {
        this.context = context;
        this.weekDayInfoList = weekDayInfoList;
        this.listerner = listerner;
    }

    @Override
    public WeekInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_week, parent, false);
        return new WeekInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeekInfoViewHolder holder, int position) {
        final WeekDayInfo info = weekDayInfoList.get(position);
        holder.cb1.setText(info.getDataNumber()+"");
        holder.week.setText(info.getWeekIndext());
        holder.cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listerner.click(info.getDataNumber(),isChecked);
            }
        });
    }


    @Override
    public int getItemCount() {
        return weekDayInfoList != null ? weekDayInfoList.size() : 0;
    }

    class WeekInfoViewHolder extends RecyclerView.ViewHolder {
        public CheckBox cb1;
        public TextView week;
        public WeekInfoViewHolder(View itemView) {
            super(itemView);
            this.cb1 = (CheckBox) itemView.findViewById(R.id.cb_1);
            this.week = (TextView) itemView.findViewById(R.id.week);
        }
    }

    public interface ItemCheckBoxSelect{
        public void click(int position ,boolean isChecked);
    }

}
