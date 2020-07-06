package com.example.chapter_1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_submit = findViewById(R.id.submit_info);
        final TextView sys_name = findViewById(R.id.system_name);
        final EditText info_input = findViewById(R.id.input_data);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable input = info_input.getText();
                info_input.setText("");
                if (input.length()>0) {
                    sys_name.setText(input);
                    Log.i("Button_Event", "输入完成，系统名称已修改为 "+input);
                } else {
                    sys_name.setText(R.string.system_name);
                    Log.i("Button_Event", "用户输入为空，恢复默认值");
                }
            }
        });

        final Switch music_switch = findViewById(R.id.music_switch);
        music_switch.setChecked(false);
        final TextView mu_info = findViewById(R.id.Info);
        music_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mu_info.setText(R.string.try_to_start_music);
                    Log.i("music", "试图打开音频文件，但是失败了！");
                } else {
                    mu_info.setText("");
                    Log.i("music", "音频文件关闭");
                }
            }
        });

    }
}