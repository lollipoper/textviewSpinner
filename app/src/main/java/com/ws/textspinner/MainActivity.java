package com.ws.textspinner;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextSpinnerView viewById = findViewById(R.id.ts_view);
        String message = "There is a<small/moderate/large> joint effusion <with/without> synovial hypertrophy.  There <is/is no> intra-articular body. ";
//        String message = "首先通过getViewTreeObserver()获取到ViewTreeObserver实例，然后调用addOnGlobalLayoutListener()添加监听。这个监听会在全局layout状态和在该view树里的view的visibility的改变时进行回调。很显然这个获取View宽高的一个很好的时机。但是这个方法也会被调用多次，所以也不太建议在该方法中获取view的宽高。\n" +
//                "\n" +
//                "View.measure()\n" +
//                "\n" +
//                "这种方法的实现方式是手动进行measure来得到view的宽高。这个流程其实和自定义view重写onMeasure()方法类似，需要分情况处理：match_parent,具体的数值，wrap_content;对于第一种，很显然无法测出，第二种就比较简单，直接传入具体的数值进行测量即可；对于第三种我们需要通过(1<<30) - 1的方式得到。\n" +
//                "————————————————\n" +
//                "版权声明：本文为CSDN博主「零下0814」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。\n" +
//                "原文链接：https://blog.csdn.net/ChrisSen/article/details/81328671";
        viewById.formatMessage(message);
        viewById.showContent();
    }
}
