package org.schulcloud.mobile.ui.homework;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BaseAdapter;
import org.schulcloud.mobile.util.ViewUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@ConfigPersistent
public class HomeworkAdapter extends BaseAdapter<HomeworkAdapter.HomeworkViewHolder> {

    private List<Homework> mHomework;

    @Inject
    HomeworkPresenter mHomeworkPresenter;

    @Inject
    public HomeworkAdapter() {
        mHomework = new ArrayList<>();
    }

    public void setHomework(@NonNull List<Homework> homework) {
        mHomework = homework;
        notifyDataSetChanged();
    }

    @Override
    public HomeworkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_homework, parent, false);
        return new HomeworkViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(HomeworkViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Homework homework = mHomework.get(position);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        SimpleDateFormat dateFormatDeux = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        if (homework.courseId != null && homework.courseId.color != null)
            holder.vV_color.setBackgroundColor(Color.parseColor(homework.courseId.color));

        if (homework.courseId != null && homework.courseId.name != null)
            holder.vTv_name.setText(context.getString(R.string.homework_homework_name_format,
                    homework.courseId.name, homework.name));
        else
            holder.vTv_name.setText(homework.name);

        holder.vTv_description.setText(Html.fromHtml(homework.description));

        Date untilDate = null;
        try {
            untilDate = dateFormat.parse(homework.dueDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ViewUtil.setVisibility(holder.vTv_due, untilDate != null);
        if (untilDate != null) {
            String dateTitle = context.getString(R.string.homework_homework_due);
            SpannableString dateText =
                    new SpannableString(dateTitle + dateFormatDeux.format(untilDate));
            if (new Date().before(untilDate))
                holder.vCard
                        .setCardBackgroundColor(
                                ContextCompat.getColor(context, android.R.color.white));
            else {
                dateText.setSpan(new StrikethroughSpan(), dateTitle.length(), dateText.length(),
                        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                holder.vCard
                        .setCardBackgroundColor(ContextCompat.getColor(context, R.color.gray_dark));
            }
            holder.vTv_due.setText(dateText);
        }

        ViewUtil.setVisibility(holder.vAtv_private,
                homework.restricted == null || !homework.restricted);

        holder.vCard.setOnClickListener(v ->
                mHomeworkPresenter.showHomeworkDetail(homework._id));
    }
    @Override
    public int getItemCount() {
        return mHomework.size();
    }

    class HomeworkViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.homework_tv_name)
        TextView vTv_name;
        @BindView(R.id.homework_tv_description)
        TextView vTv_description;
        @BindView(R.id.homework_v_color)
        View vV_color;
        @BindView(R.id.homework_atv_private)
        AwesomeTextView vAtv_private;
        @BindView(R.id.homework_card)
        CardView vCard;
        @BindView(R.id.homework_tv_due)
        TextView vTv_due;

        public HomeworkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
