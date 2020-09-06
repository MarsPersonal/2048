package com.lear.game2048.view;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.lear.game2048.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

/**
 * author: song
 * created on : 2020/8/26 1:46
 * description:封装添加文本View
 */
public class AddTextItemView extends BaseAddItemView implements View.OnClickListener, TextWatcher {

    public static final String TAG = "AddTextItemView";

    private TextView mLevelText;
    private EditText mEdit;
    private ImageButton mRemoveView;

    private int mLevel;

    private boolean isThisSetText = false;


    public AddTextItemView(Context context) {
        this(context, null);
    }

    public AddTextItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddTextItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AddTextItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 初始化
     *
     * @param context      上下文
     * @param attrs        属性集合
     * @param defStyleAttr 默认样式
     * @param defStyleRes  默认主题
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        View view = inflate(context, R.layout.item_add_level_text, this);

        mLevelText = view.findViewById(R.id.level);
        mEdit = view.findViewById(R.id.level_edit);
        mRemoveView = view.findViewById(R.id.remove);

        mEdit.addTextChangedListener(this);
        mRemoveView.setOnClickListener(this);

    }

    @Override
    public void setLevel(int level) {
        mLevel = level;
        String str = level + ".";
        mLevelText.setText(str);
    }

    @Override
    public int getLevel() {
        return mLevel;
    }

    @Override
    public void hideRemove() {
        mRemoveView.setVisibility(View.INVISIBLE);
    }

    public String getLevelContent() {
        return mEdit.getText().toString();
    }

    @Override
    public void onClick(View v) {
        if (getOnRemoveListener() != null) {
            getOnRemoveListener().onRemoveClick(this, mLevel);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        //如果是自己设置的文本，那就不需要向下执行
        if (isThisSetText) {
            isThisSetText = false;
            return;
        }

        int max = 8;
        //去除特殊字符
        String editText = mEdit.getText().toString();
        String temp = editText.replaceAll("[^a-zA-Z0-9\u4E00-\u9FA5]", "");

        //如果最大长度大于4
        //则判断中文与英文的数量
        //中文算占两个字符
        if (temp.length() > (max >> 1)) {

            int sum = 0;
            char c;
            int size;
            for (int i = 0; i < temp.length(); i++) {
                c = temp.charAt(i);
                size = 0;
                //如果是数字或字母
                if ((c >= 48 && c <= 57)
                        || (c >= 65 && c <= 90)
                        || (c >= 97 && c <= 122)) {
                    size = 1;
                } else if (c >= 0x4e00 && c <= 0x9fa5) {//如果是中文
                    size = 2;
                }

                //如果小于最大值
                if (sum + size <= max) sum += size;
                else {
                    temp = temp.substring(0, i);
                    break;
                }


            }
        }

        if (!editText.equals(temp)) {
            isThisSetText = true;
            mEdit.setText(temp);
            mEdit.setSelection(temp.length());
        }


    }

    @Override
    public void afterTextChanged(Editable s) {
    }

}
