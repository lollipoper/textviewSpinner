package com.ws.textspinner;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSpinnerView extends LinearLayoutCompat {
    private static final String TAG = "TextSpinnerView";
    private List<TextSpinnerItem> items;
    private LinearLayoutCompat rowView;
    private int textSize = 50;//像素

    public TextSpinnerView(Context context) {
        this(context, null);
    }

    public TextSpinnerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextSpinnerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        rowView = createRowView();
    }

    /**
     * 处理数据
     *
     * @param message 数据 eg:There is a<small/moderate/large> joint effusion <with/without> synovial hypertrophy.  There <is/is no> intra-articular body.
     */
    public void formatMessage(String message) {
        if (TextUtils.isEmpty(message)) return;
        String regex = "<.*?>";//通过正则把TextView和Spinner区分分组
        String[] textViewArray = message.split(regex);//TextView数组
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(message);
        List<String> spinnersArray = new ArrayList<>();//Spinner列表
        while (matcher.find()) {
            String group = matcher.group();
            spinnersArray.add(group);
        }
        //分类型重新组合成一个数组
        TextSpinnerItem[] temp = new TextSpinnerItem[textViewArray.length + spinnersArray.size()];
        for (int i = 0; i < textViewArray.length; i++) {
            temp[2 * i] = new TextSpinnerItem(TextSpinnerType.TEXT_VIEW, textViewArray[i]);
        }
        for (int i = 0; i < spinnersArray.size(); i++) {
            temp[2 * i + 1] = new TextSpinnerItem(TextSpinnerType.SPINNER, spinnersArray.get(i));
        }
        //转换为list方便读取数据，不转也可以
        items = Arrays.asList(temp);
    }

    /**
     * 显示文本
     */
    public void showContent() {
        post(new Runnable() {
            @Override
            public void run() {
                show();
            }
        });
    }

    private void show() {
        //计算可以显示的最大宽度
        int width = getWidth();
        if (width == 0) width = getMeasuredWidth();
        int measuredMaxWidth = width - getPaddingLeft() - getPaddingRight();
        //计算一行可以放置的最大字数
        float displayWidth = measuredMaxWidth;
        //遍历Item
        for (TextSpinnerItem item : items) {
            Log.d(TAG, "draw type:" + item.getType());
            if (item.getType() == TextSpinnerType.SPINNER) {
                //测量即将绘制的控件宽度
                Spinner spinner = createSpinner(item.getContent());
                spinner.measure(0, 0);
                if (spinner.getMeasuredWidth() > displayWidth) {//大于可以显示的宽度，换行处理
                    rowView = createRowView();
                    displayWidth = measuredMaxWidth;
                }
                rowView.addView(spinner);
                displayWidth = displayWidth - spinner.getMeasuredWidth();//计算剩余可显示宽度
            } else if (item.getType() == TextSpinnerType.TEXT_VIEW) {
                String name = item.getContent();//需要绘制的字符串
                Log.d(TAG, "displayWidth:" + displayWidth);
                if (measureText(name) > displayWidth) {//超过屏幕宽度
                    //计算屏幕可以显示的字数
                    int endIndex = (int) (displayWidth * name.length() / measureText(name));
                    //裁剪字符串并单行显示
                    createTextView(rowView, name.substring(0, endIndex));
                    //重置可显示宽度
                    displayWidth = measuredMaxWidth;
                    //截取未显示的字符串
                    String endStr = name.substring(endIndex);
                    //字符串可能很长（显示多行）
                    while (measureText(endStr) > displayWidth) {
                        endIndex = (int) (displayWidth * endStr.length() / measureText(endStr));
                        createTextView(null, endStr.substring(0, endIndex));
                        endStr = endStr.substring(endIndex);
                    }
                    //剩余长度直接展示屏幕宽度，需要根据屏幕宽度继续进行裁剪
                    rowView = createRowView();
                    createTextView(rowView, endStr);
                    displayWidth = displayWidth - measureText(endStr);//计算剩余可展示宽度
                } else {//没有超过屏幕宽度，直接渲染
                    createTextView(rowView, name);
                    displayWidth = displayWidth - measureText(name);
                    if (displayWidth < 0) {
                        displayWidth = measuredMaxWidth;
                        rowView = createRowView();
                    }
                }
            }
        }
    }

    private LinearLayoutCompat createRowView() {
        LinearLayoutCompat linearLayoutCompat = new LinearLayoutCompat(getContext());
        linearLayoutCompat.setOrientation(HORIZONTAL);
        linearLayoutCompat.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(linearLayoutCompat);
        return linearLayoutCompat;
    }

    private void createTextView(ViewGroup viewGroup, String textItem) {
        TextView textView = new TextView(getContext());
        textView.setText(textItem);
        textView.setSingleLine();
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (viewGroup != null) {
            viewGroup.addView(textView);
        } else {
            addView(textView);
        }
    }

    private Spinner createSpinner(String textItem) {
        Spinner spinner = new Spinner(getContext());
        String[] split = textItem.substring(1, textItem.length() - 1).split("/");
        int max = split[0].length();
        for (String item : split) {
            max = Math.max(item.length(), max);
        }
        spinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        spinner.setAdapter(new ArrayAdapter<>(getContext(), R.layout.item_spinner, R.id.tv_spinner, split));
        return spinner;
    }

    private float measureText(String word) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        return paint.measureText(word);
    }
}
