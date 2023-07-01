package com.bistucalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// 继承事件监听器，实现点击事件
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView inputText, resultText;     // 输入文本、结果文本
    Boolean isNew = false;              // 是否应该是新的一次计算

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        inputText  = findViewById(R.id.input_text);
        resultText = findViewById(R.id.result_text);
        initialListener();  // 初始化所有按钮的点击事件
    }

    // 点击事件
    @Override
    public void onClick(View view) {
        // 通过 view 的 id 来判断是哪个按钮被点击了
        String text = ((Button) view).getText().toString();

        // 按下的是数字键
        if (text.matches("\\d")) {
            String temp = inputText.getText().toString();   // 获取 inputText 中的内容
            // 如果此时 inputText 中只有一个 0，或者 isNew 为 true，说明此时的操作应该是重新输入数字
            if ("0".equals(temp) || isNew) {
                inputText.setText(text);
                isNew = false;
            } else if (temp.matches("\\d|\\d*\\.\\d*") && resultText.getText().toString().isEmpty()) {
                // 如果此时 inputText 中没有运算符，且 resultText 为空，说明此时的操作应该是重新输入数字
                inputText.setText(text);
            } else if ("0".equals(text) && temp.matches(".*/")) {
                Toast.makeText(this, "除数不能为 0", Toast.LENGTH_SHORT).show();
            } else {
                inputText.append(text);
            }
            try {
                Double calculate = PolandExpress.calculate(inputText.getText().toString());
                resultText.setText("= " + calculate);
            } catch (Exception e) {
                e.printStackTrace();
                resultText.setText("");
            }
        }
        // 负数按钮：将整个表达式变为负数
        else if ("neg".equals(text)) {
            try {
                String s         = inputText.getText().toString();  // 获取 inputText 中的内容
                Double calculate = PolandExpress.calculate(s);      // 通过逆波兰式计算结果
                String toString  = calculate.toString();
                // 如果结果是负数，直接去掉负号
                if (toString.startsWith("-")) {
                    toString = toString.substring(1);
                } else {    // 如果结果是正数，加上负号
                    toString = "-" + toString;
                }
                // 将结果更新到 inputText 和 resultText 中
                inputText.setText(toString);
                resultText.setText("= " + toString);
            } catch (Exception e) {
                e.printStackTrace();
                resultText.setText("");
            }
        }
        // 按下的是运算符
        else if (text.matches("[+\\-*/]")) {
            isNew = false;
            String s = inputText.getText().toString();  // 获取 inputText 中的内容
            // 如果 inputText 中只有一个 0，不能输入*/+符号，只能输入 - 符号
            if ("0".equals(s)) {
                if (!text.matches("[*/+]")) {
                    inputText.setText(text);
                }
            } else if (s.matches(".*[+\\-*/]") || s.matches(".*\\.")) {
                // 对不合法的操作不进行处理，包括不能出现两个符号连在一起，以及符号前面不能是 . 符号
            } else {
                inputText.append(text);
            }
        }
        // 按下的是小数点
        else if (".".equals(text)) {
            String s = inputText.getText().toString();
            // 检查小数点是否合规，即不应存在这几种形式：6.. 与 6.6. 、 6-. 、 6+.、 6*.、 6/.
            if (!s.matches(".*\\d\\.") && !s.matches(".*\\d\\.\\d") && !s.matches(".*[+\\-*/]")) {
                inputText.append(text);
            }
        }
        // 按下的是等号
        else if ("=".equals(text)) {
            // 如果以运算符结尾，不做任何处理
            if (inputText.getText().toString().matches(".*[+\\-*/\\.]")) {
                return;
            }
            // 将resultText的内容复制到inputText
            if (resultText.getText().toString().length() > 0) {
                inputText.setText(resultText.getText().toString().substring(2));
                resultText.setText("");
            }
        }
        // 开平方根
        else if ("√".equals(text)) {
            isNew = true;
            try {
                double calculate = PolandExpress.calculate(inputText.getText().toString());
                if (calculate < 0 || inputText.getText().toString().startsWith("-")) {
                    Toast.makeText(this, "负数不能开平方根", Toast.LENGTH_SHORT).show();
                } else {
                    // 计算平方根，结果最多为9位，避免显示越界
                    Double sqrt = Math.sqrt(calculate);
                    String s    = sqrt.toString();
                    if (s.length() > 9) {
                        s = s.substring(0, 9);
                    }
                    inputText.setText(s);
                    resultText.setText("= " + s);
                    Toast.makeText(this, "对" + calculate + "开平方根", Toast.LENGTH_SHORT).show();

                }
            } catch (Exception e) {
                e.printStackTrace();
                resultText.setText("");
            }
        }
        // 按键 < ：清除一位数，每次清除都应该重新进行一次计算
        else if ("<".equals(text)) {
            String input = inputText.getText().toString();
            if (input.length() > 1) {
                inputText.setText(input.substring(0, input.length() - 1));
                try {
                    Double calculate = PolandExpress.calculate(inputText.getText().toString());
                    resultText.setText("= " + calculate);
                } catch (Exception e) {
                    resultText.setText("");
                    e.printStackTrace();
                    resultText.setText("");
                }
            } else {
                inputText.setText("0");
                resultText.setText("");
            }
        }
        // 按键 AC：全部清除
        else if ("AC".equals(text)) {
            inputText.setText("0");
            resultText.setText("");
        }
    }

    private void initialListener() {
        // 注册所有按钮的监听器
        findViewById(R.id.button_0).setOnClickListener(this);
        findViewById(R.id.button_1).setOnClickListener(this);
        findViewById(R.id.button_2).setOnClickListener(this);
        findViewById(R.id.button_3).setOnClickListener(this);
        findViewById(R.id.button_4).setOnClickListener(this);
        findViewById(R.id.button_5).setOnClickListener(this);
        findViewById(R.id.button_6).setOnClickListener(this);
        findViewById(R.id.button_7).setOnClickListener(this);
        findViewById(R.id.button_8).setOnClickListener(this);
        findViewById(R.id.button_9).setOnClickListener(this);
        findViewById(R.id.button_add).setOnClickListener(this);
        findViewById(R.id.button_sub).setOnClickListener(this);
        findViewById(R.id.button_mul).setOnClickListener(this);
        findViewById(R.id.button_div).setOnClickListener(this);
        findViewById(R.id.button_back).setOnClickListener(this);
        findViewById(R.id.button_dot).setOnClickListener(this);
        findViewById(R.id.button_result).setOnClickListener(this);
        findViewById(R.id.button_clear).setOnClickListener(this);
        findViewById(R.id.button_sqrt).setOnClickListener(this);
        findViewById(R.id.button_neg).setOnClickListener(this);
    }
}