package com.zeustel.top9.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zeustel.top9.R;
import com.zeustel.top9.base.IAnimViewHolder;
import com.zeustel.top9.bean.GameEvaluating;
import com.zeustel.top9.utils.AnimUtils;
import com.zeustel.top9.utils.Constants;
import com.zeustel.top9.utils.FontHelper;
import com.zeustel.top9.utils.Tools;
import com.zeustel.top9.widgets.PrintTextView;

/**
 * ...
 *
 * @author NiuLei
 * @email niulei@zeustel.com
 * @date 2015/7/31 14:59
 */
public class HolderEvaluating extends OverallRecyclerHolder<GameEvaluating> implements IAnimViewHolder {
    private ImageView evaluating_item_banner;
    private ImageView evaluating_item_marks;
    private TextView evaluating_item_time;
    private PrintTextView evaluating_item_subTitle;
    private TextView evaluating_item_title;
    private FrameLayout evaluating_item_line;
    private String banner;
    private String gameName;
    private int evaluatingType;
    private String subTitle;
    private String title;
    private AnimUtils animUtils;
    private Typeface typeface;
    private GameEvaluating gameEvaluating;

    public HolderEvaluating(Context context, View itemView) {
        super(context, itemView);
        if (animUtils == null) {
            animUtils = AnimUtils.getInstance(context);
        }
        if (typeface == null) {
            typeface = FontHelper.getInstance(getContext()).getTypeface();
        }
    }
/*
    @Override
    public void onDetached() {
        Tools.recycleImg(evaluating_item_banner);
        super.onDetached();
        banner = null;
        gameName = null;
        evaluatingType = -1;
        subTitle = null;
        typeface = null;
        gameEvaluating = null;
    }*/

    @Override
    protected void initItemView(View itemView) {
        evaluating_item_banner = (ImageView) itemView.findViewById(R.id.evaluating_item_banner);
        evaluating_item_marks = (ImageView) itemView.findViewById(R.id.evaluating_item_marks);
        evaluating_item_time = (TextView) itemView.findViewById(R.id.evaluating_item_time);
        evaluating_item_subTitle = (PrintTextView) itemView.findViewById(R.id.evaluating_item_subTitle);
        //        evaluating_item_subTitle = (TextView) itemView.findViewById(R.id.evaluating_item_subTitle);
        evaluating_item_title = (TextView) itemView.findViewById(R.id.evaluating_item_title);
        evaluating_item_line = (FrameLayout) itemView.findViewById(R.id.evaluating_item_line);
        if (typeface != null) {
            evaluating_item_time.setTypeface(typeface);
        }
        evaluating_item_line.setVisibility(View.INVISIBLE);
    }

    @Override
    public void set(OverallRecyclerAdapter<GameEvaluating> adapter, int position) {
        gameEvaluating = adapter.getItem(position);
        if (gameEvaluating == null) {
            return;
        }
        banner = gameEvaluating.getBanner();
        gameName = gameEvaluating.getGameName();
        evaluatingType = gameEvaluating.getEvaluatingType();
        subTitle = gameEvaluating.getSubTitle();
        title = gameEvaluating.getTitle();
        getImageLoader().displayImage(Constants.TEST_IMG + banner, evaluating_item_banner, Tools.options);
        evaluating_item_time.setText(gameName);


        if (GameEvaluating.EvaluatingType.HAND_GAME == evaluatingType) {
            evaluating_item_time.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
        } else if (GameEvaluating.EvaluatingType.ONLINE_GAME == evaluatingType) {
            evaluating_item_time.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
        } else {
            evaluating_item_time.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }
        evaluating_item_subTitle.setText(subTitle);

        if (getLightText() != null) {
            SpannableStringBuilder spannableStringBuilder = Tools.highLight(title, getLightText(), ContextCompat.getColor(getContext(), R.color.red));
            if (spannableStringBuilder != null) {
                evaluating_item_title.setText(spannableStringBuilder);
                return;
            }
        }
        evaluating_item_title.setText(title == null ? "" : title);
        evaluating_item_line.setVisibility(View.INVISIBLE);
    }

    @Override
    public void startUpAnim(int startOffset) {
        if (animUtils != null) {
            animUtils.translateIn(evaluating_item_banner, AnimUtils.TranslateDirection.BOTTOM, false);
            animUtils.translateIn(evaluating_item_title, AnimUtils.TranslateDirection.BOTTOM, false);
            animUtils.translateIn(evaluating_item_subTitle, AnimUtils.TranslateDirection.BOTTOM, false, 200);
            animUtils.translateIn(evaluating_item_time, AnimUtils.TranslateDirection.TOP, true, 500);
        }

    }

    @Override
    public void startAnim(int startOffset) {
        if (animUtils != null) {
            animUtils.setIn(evaluating_item_title, AnimUtils.TranslateDirection.LEFT, true, startOffset);
            animUtils.setIn(evaluating_item_banner, AnimUtils.TranslateDirection.RIGHT, true, startOffset);
            animUtils.Rotate3dAnimation(evaluating_item_time, 90f, 0f, 0.0f, false, startOffset + 1000);
            animUtils.scaleLineIn(evaluating_item_line, startOffset + 800);
            animUtils.translateIn(evaluating_item_marks, AnimUtils.TranslateDirection.BOTTOM, false, startOffset + 800, 300);
            animUtils.translateIn(evaluating_item_subTitle, AnimUtils.TranslateDirection.TOP, false, startOffset + 800, 300);
        }
    }
}
