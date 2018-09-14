package com.thang.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

  EditText etNumber1,etNumber2;
  Button btnCal;
  RadioButton rbAdd, rbSub, rbMul, rbDiv;
  TextView tvResult;
  RadioGroup radioGroup;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initView();
    btnCal.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if(etNumber1.getText().toString().equals("") || etNumber2.getText().toString().equals("")){
          Toast.makeText(MainActivity.this, "Number must not empty!!", Toast.LENGTH_SHORT).show();
        }else{
          int number1 = Integer.parseInt(etNumber1.getText().toString());
          int number2 = Integer.parseInt(etNumber2.getText().toString());
          int result = 0;
          if(rbAdd.isChecked()){
            result = number1+number2;
          }else if(rbSub.isChecked()){
            result = number1-number2;
          }else if(rbMul.isChecked()){
            result = number1*number2;
          }else if(rbDiv.isChecked()){
            if(number2==0){
              Toast.makeText(MainActivity.this, "invalid calculation", Toast.LENGTH_SHORT).show();
              return;
            }else{
              result = number1/number2;
            }
          }else{
            Toast.makeText(MainActivity.this, "You must choose radio button", Toast.LENGTH_SHORT).show();
          }
          tvResult.setText(String.valueOf(result));

        }
      }
    });
  }

  private void initView() {
    radioGroup = findViewById(R.id.radio_group);
    etNumber1 = findViewById(R.id.et_number1);
    etNumber2 = findViewById(R.id.et_number2);
    rbAdd = findViewById(R.id.rb_cong);
    rbSub = findViewById(R.id.rb_tru);
    rbMul = findViewById(R.id.rb_nhan);
    rbDiv = findViewById(R.id.rb_Chia);
    tvResult = findViewById(R.id.tv_result);
    btnCal = findViewById(R.id.btn_calculate);
  }
}
