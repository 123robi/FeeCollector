package eu.rkosir.feecollector.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.teamManagement.UserDetail;
import eu.rkosir.feecollector.entity.Event;
import eu.rkosir.feecollector.entity.MemberFee;
import eu.rkosir.feecollector.entity.Place;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;

public class ShowMemberFeesAdapter extends RecyclerView.Adapter<ShowMemberFeesAdapter.ViewHolder> {
	private List<MemberFee> membersFees;
	private ShowMemberFeesAdapter.OnItemClickListener mListener;
	private UserDetail userDetail;
	public interface OnItemClickListener {
		void onItemClick(int position);
	}
	public void setOnItemClickListener(ShowMemberFeesAdapter.OnItemClickListener listener) {
		mListener = listener;
	}

	public ShowMemberFeesAdapter(List<MemberFee> membersFees, UserDetail userDetail) {
		this.membersFees = membersFees;
		this.userDetail = userDetail;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public UserDetail userDetail;
		public CardView cardView;
		public TextView mFeeName;
		public TextView mFeeAmount;
		public TextView mFeeDate;
		public TextView mFeeIsPaid;
		public CheckBox mCheckBox;

		public ViewHolder(View itemView, ShowMemberFeesAdapter.OnItemClickListener listener, UserDetail userDetail) {
			super(itemView);
			cardView = itemView.findViewById(R.id.cardview);
			mFeeName = itemView.findViewById(R.id.fee_name);
			mFeeAmount = itemView.findViewById(R.id.fee_amount);
			mFeeDate = itemView.findViewById(R.id.date);
			mFeeIsPaid = itemView.findViewById(R.id.isPaid);
			mCheckBox = itemView.findViewById(R.id.select);
			this.userDetail = userDetail;

			cardView.setOnLongClickListener(userDetail);

			itemView.setOnClickListener(view -> {
				if (listener != null) {
					int position = getAdapterPosition();
					if (position != RecyclerView.NO_POSITION) {
						listener.onItemClick(position);
					}
				}
			});
			cardView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (userDetail.is_in_action_mode) {
						if (mCheckBox.isChecked()) {
							mCheckBox.setChecked(false);
						} else {
							mCheckBox.setChecked(true);
						}
					}
				}
			});
			mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					userDetail.prepareSelection(isChecked,getAdapterPosition());
				}
			});

		}
	}

	@NonNull
	@Override
	public ShowMemberFeesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_member_fee, parent, false);
		ShowMemberFeesAdapter.ViewHolder viewHolder = new ShowMemberFeesAdapter.ViewHolder(v,mListener, userDetail);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(@NonNull ShowMemberFeesAdapter.ViewHolder holder, int position) {
		RelativeLayout.LayoutParams parameter = (RelativeLayout.LayoutParams) holder.mFeeDate.getLayoutParams();
		if (!holder.userDetail.is_in_action_mode) {
			holder.mCheckBox.setVisibility(View.GONE);
			parameter.setMargins(30, parameter.topMargin, parameter.rightMargin, parameter.bottomMargin);
			holder.mFeeDate.setLayoutParams(parameter);
		} else {
			holder.mCheckBox.setVisibility(View.VISIBLE);
			parameter.setMargins(110, parameter.topMargin, parameter.rightMargin, parameter.bottomMargin);
			holder.mFeeDate.setLayoutParams(parameter);
			holder.mCheckBox.setChecked(false);
		}
		holder.mFeeName.setText(membersFees.get(position).getName());
		holder.mFeeAmount.setText(membersFees.get(position).getAmount() + SharedPreferencesSaver.getCurrencySymbol(holder.userDetail));
		if (membersFees.get(position).isPaid()) {
			holder.mFeeIsPaid.setTextColor(holder.userDetail.getResources().getColor(R.color.green));
			holder.mFeeIsPaid.setText(holder.userDetail.getResources().getString(R.string.user_detail_paid));
		} else {
			holder.mFeeIsPaid.setTextColor(holder.userDetail.getResources().getColor(R.color.red));
			holder.mFeeIsPaid.setText(holder.userDetail.getResources().getString(R.string.user_detail_not_paid));
		}
		holder.mFeeDate.setText(getTimeFormat(membersFees.get(position).getDate()));

	}

	@Override
	public int getItemCount() {
		return membersFees.size();
	}

	private String getTimeFormat(Calendar c) {
		Date date = c.getTime();
		return AppConfig.membersFeeFormat.format(date);
	}
}