package eu.rkosir.feecollector.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.entity.Event;
import eu.rkosir.feecollector.entity.MemberFee;
import eu.rkosir.feecollector.entity.Place;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;

public class ShowMemberFeesAdapter extends RecyclerView.Adapter<ShowMemberFeesAdapter.ViewHolder> {
    private List<MemberFee> membersFees;
    private Context context;
    private ShowMemberFeesAdapter.OnItemClickListener mListener;
    private Place place;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(ShowMemberFeesAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public ShowMemberFeesAdapter(List<MemberFee> membersFees, Context context) {
        this.membersFees = membersFees;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mFeeName;
        public TextView mFeeAmount;
        public TextView mFeeDate;
        public TextView mFeeIsPaid;

        public ViewHolder(View itemView, ShowMemberFeesAdapter.OnItemClickListener listener) {
            super(itemView);
            mFeeName = itemView.findViewById(R.id.fee_name);
            mFeeAmount = itemView.findViewById(R.id.fee_amount);
            mFeeDate = itemView.findViewById(R.id.date);
            mFeeIsPaid = itemView.findViewById(R.id.isPaid);

            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ShowMemberFeesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_member_fee, parent, false);
        ShowMemberFeesAdapter.ViewHolder viewHolder = new ShowMemberFeesAdapter.ViewHolder(v,mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShowMemberFeesAdapter.ViewHolder holder, int position) {
        holder.mFeeName.setText(membersFees.get(position).getName());
        holder.mFeeAmount.setText(membersFees.get(position).getAmount() + SharedPreferencesSaver.getCurrencySymbol(context));
        if (membersFees.get(position).isPaid()) {
            holder.mFeeIsPaid.setTextColor(context.getResources().getColor(R.color.green));
            holder.mFeeIsPaid.setText(context.getResources().getString(R.string.user_detail_paid));
        } else {
            holder.mFeeIsPaid.setTextColor(context.getResources().getColor(R.color.red));
            holder.mFeeIsPaid.setText(context.getResources().getString(R.string.user_detail_not_paid));
        }
        holder.mFeeDate.setText(getTimeFormat(membersFees.get(position).getDate()));
    }

    @Override
    public int getItemCount() {
        return membersFees.size();
    }

    private String getTimeFormat(Calendar c) {
        Date date = c.getTime();
        DateFormat timeFormatter =
                DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        return timeFormatter.format(date);
    }
}