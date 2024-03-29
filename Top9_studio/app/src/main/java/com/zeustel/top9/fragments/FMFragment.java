package com.zeustel.top9.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zeustel.top9.FMDetailActivity;
import com.zeustel.top9.R;
import com.zeustel.top9.base.IAnimFragment;
import com.zeustel.top9.base.WrapOneAndHandleFragment;
import com.zeustel.top9.bean.Compere;
import com.zeustel.top9.bean.FM;
import com.zeustel.top9.bean.Program;
import com.zeustel.top9.result.FMResult;
import com.zeustel.top9.utils.AnimUtils;
import com.zeustel.top9.utils.Constants;
import com.zeustel.top9.utils.Tools;
import com.zeustel.top9.utils.operate.DBBaseOperate;
import com.zeustel.top9.utils.operate.DBFMImp;
import com.zeustel.top9.utils.operate.DataBaseOperate;
import com.zeustel.top9.utils.operate.DataFM;
import com.zeustel.top9.widgets.LoadingView;

import java.util.ArrayList;

/**
 * 电台页面
 *
 * @author NiuLei
 * @email niulei@zeustel.com
 * @date 2015/6/26
 */
public class FMFragment extends WrapOneAndHandleFragment implements View.OnClickListener, LoadingView.OnFailedClickListener, IAnimFragment {
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageView fm_theme;
    private View fm_line_01;
    private View fm_line_02;
    private ImageView fm_icon;
    private TextView fm_current_program;
    private Button fm_program;
    private Button fm_compere;
    private Button fm_float;
    private ImageView fm_toggle;
    //数据操作
    private DataBaseOperate dataOperate;
    //数据库操作
    private DBBaseOperate dbOperate;
    private FM fm;
    //主播
    private Compere compere;
    //节目列表
    private ArrayList<Program> programs;
    private Animation scaleAnim = null;
    private Animation iconIn = null;
    private Animation rotate = null;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (programs != null) {
            programs.clear();
            programs = null;
        }
    }

    private Animation.AnimationListener iconInListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (rotate == null) {
                rotate = AnimUtils.getInstance(getContext()).getRotate(0);
            }
            fm_icon.startAnimation(rotate);
            fm_line_01.setVisibility(View.VISIBLE);
            fm_line_02.setVisibility(View.VISIBLE);

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    public FMFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = new FM();
        Program mProgram = new Program();
        mProgram.setTitle("小M陪你玩游戏之《梦幻西游》");
        fm.setPlayingProgram(new Program());
        compere = new Compere();
        compere.setNickName("艾利");
        compere.setNameEN("AILEY");
        compere.setBirthday("12月1日");
        compere.setBloodType("O");
        compere.setConstellation("射手");
        compere.setHobby("吃饭睡觉打豆豆");
        compere.setManifesto("很高兴认识大家,我在TOP9平台哦~ 喜欢我的就点我吧,千万别错过每天的抽奖活动哦 !");
        compere.setVideo("android.resource://" + getActivity().getPackageName() + "/" + R.raw.fm09);
        compere.setVideoCover(String.valueOf(R.mipmap.fm08));

        compere.setPhotos(R.mipmap.fm04 + "," + R.mipmap.fm05 + "," + R.mipmap.fm06 + "," + R.mipmap.fm07);
        fm.setCompere(compere);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onAnimIn() {
        if (fm_line_01 != null) {
            fm_line_01.setVisibility(View.INVISIBLE);
        }
        if (fm_line_02 != null) {
            fm_line_02.setVisibility(View.INVISIBLE);
        }
        if (fm_theme != null) {
            AnimUtils.getInstance(getContext()).translateIn(fm_theme, AnimUtils.TranslateDirection.RIGHT, false);
        }

        if (iconIn == null) {
            iconIn = AnimUtils.getInstance(getContext()).getTranslate(AnimUtils.TranslateDirection.BOTTOM, true);
            iconIn.setStartOffset(200);
            iconIn.setAnimationListener(iconInListener);
        }
        if (fm_icon != null) {
            fm_icon.startAnimation(iconIn);
        }
        AnimUtils.getInstance(getContext()).translateIn(fm_current_program, AnimUtils.TranslateDirection.RIGHT, true, 400);

        AnimUtils.getInstance(getContext()).translateIn(fm_program, AnimUtils.TranslateDirection.BOTTOM, true, 450);
        AnimUtils.getInstance(getContext()).translateIn(fm_compere, AnimUtils.TranslateDirection.BOTTOM, true, 500);
        AnimUtils.getInstance(getContext()).translateIn(fm_float, AnimUtils.TranslateDirection.BOTTOM, true, 550);
        if (scaleAnim == null) {
            scaleAnim = AnimUtils.getInstance(getContext()).getScaleIn();
            scaleAnim.setStartOffset(600);
        }
        if (fm_toggle != null) {
            fm_toggle.startAnimation(scaleAnim);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_fm, container, false);
        fm_theme = (ImageView) contentView.findViewById(R.id.fm_theme);
        fm_line_01 = contentView.findViewById(R.id.fm_line_01);
        fm_line_02 = contentView.findViewById(R.id.fm_line_02);
        fm_icon = (ImageView) contentView.findViewById(R.id.fm_icon);
        fm_current_program = (TextView) contentView.findViewById(R.id.fm_current_program);
        fm_program = (Button) contentView.findViewById(R.id.fm_program);
        fm_compere = (Button) contentView.findViewById(R.id.fm_compere);
        fm_float = (Button) contentView.findViewById(R.id.fm_float);
        fm_toggle = (ImageView) contentView.findViewById(R.id.fm_toggle);
        fm_program.setOnClickListener(this);
        fm_compere.setOnClickListener(this);
        fm_float.setOnClickListener(this);
        fm_toggle.setOnClickListener(this);
        dbOperate = new DBFMImp(getActivity());
        dataOperate = new DataFM(getHandler(), dbOperate);
        try {
            dataOperate.getSingleData(dataOperate.createSingleBundle(Constants.TEST_FM, 0), FMResult.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        fm_current_program.setVisibility(View.VISIBLE);
        Program playingProgram = fm.getPlayingProgram();
        fm_current_program.setText(playingProgram.getTitle());
        return contentView;
    }

    private void set() {
        if (fm != null) {
            Program playingProgram = fm.getPlayingProgram();
            compere = fm.getCompere();
            programs = fm.getPrograms();
            if (playingProgram != null) {
                imageLoader.displayImage(Constants.TEST_IMG + playingProgram.getTheme(), fm_theme, Tools.options);
                imageLoader.displayImage(Constants.TEST_IMG + compere.getIcon(), fm_icon, Tools.newOptions(Tools.getDimension(getResources(), R.dimen.fm_icon_size) / 2));
                fm_current_program.setVisibility(View.VISIBLE);
                fm_current_program.setText(playingProgram.getTitle());
            }
        }
    }

    @Override
    public void onHandleFailed() {
        if (fm == null) {
        } else {
            Tools.showToast(getActivity(), R.string.load_request_failed);
        }
    }

    @Override
    public void onHandleNo() {
        if (fm == null) {
        }
    }

    @Override
    public void onHandleSingle(Object obj) {
        if (fm == null) {
        }
        fm = (FM) obj;
        set();
    }

    @Override
    public String getFloatTitle() {
        return getString(R.string.title_fm_detail);
    }

    @Override
    public int getBackgroundColor() {
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fm_program:
            case R.id.fm_compere:
                Intent intent = new Intent(getActivity(), FMDetailActivity.class);
                intent.putExtra(FMDetailActivity.EXTRA_NAME_DATA, fm);
                if (R.id.fm_program == v.getId()) {
                    intent.putExtra(FMDetailActivity.EXTRA_NAME_POSITION, 0/*position*/);
                } else {
                    intent.putExtra(FMDetailActivity.EXTRA_NAME_POSITION, 1/*position*/);
                }
                startActivity(intent);
                break;
            case R.id.fm_float:
                break;
            case R.id.fm_toggle:
                break;
        }
    }

    @Override
    public void onFailedClick() {
        try {
            dataOperate.getSingleData(dataOperate.createSingleBundle(Constants.TEST_FM, 0), FMResult.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public RecyclerView getRecyclerView() {
        return null;
    }

}
