package org.iptime.seromloan.registeration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import static org.iptime.seromloan.registeration.Common.server;

public class LoginActivity extends AppCompatActivity {

    private AlertDialog dialog;

    String empNo;
    String sfName = "myFile";

    EditText serverText;
    EditText idText;
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

         // 회원가입 버튼 처리
        TextView registerButton = (TextView) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        serverText   = (EditText) findViewById(R.id.serverText);
        idText       = (EditText) findViewById(R.id.idText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        final Button loginButton = (Button) findViewById(R.id.loginButton);

        // 저장된 사용자 입력값을 꺼내서 보여주기
        SharedPreferences sf = getSharedPreferences(sfName, 0);

        String ip_address = sf.getString("ip_address", "");
        serverText.setText(ip_address);

        String emp_name = sf.getString("emp_name", "");
        idText.setText(emp_name);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverIp = serverText.getText().toString();
                server = "http://" + serverIp + "/~serom/";

                String userName = idText.getText().toString();
                String userPassword = passwordText.getText().toString();

                if (userName.equals("") || userPassword.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    dialog = builder.setMessage("아이디와 비밀번호를 입력해주세요.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();

                    return;
                }

                // Login Button Lock
                loginButton.setEnabled(false);

                Response.Listener<String> responseListner = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                Intent intent = new Intent(LoginActivity.this, ListActivity.class);

                                empNo = jsonResponse.getString("emp_no").toString();
                                intent.putExtra("emp_no",empNo.toString());

                                LoginActivity.this.startActivity(intent);
                                finish();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("로그인에 실패했습니다.")
                                        .setNegativeButton("다시시도", null)
                                        .create();
                                dialog.show();

                                // Password Clear
                                passwordText.setText("");

                                // Login Button unLock
                                loginButton.setEnabled(true);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(userName, userPassword, responseListner);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });

        passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                /*
                if ( start == 3 ) {
                    // 키보드 감추기
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
                */
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

        SharedPreferences sf = getSharedPreferences(sfName, 0);
        SharedPreferences.Editor editor = sf.edit();

        String strTmp = serverText.getText().toString();
        editor.putString("ip_address", strTmp);

        strTmp = idText.getText().toString();
        editor.putString("emp_name", strTmp);

        editor.commit();

    }

    @Override
    protected void onStart() {
        super.onStart();

        // 키보드 보이기 : 작동안함
        if ( !idText.getText().toString().equals("") ) {

            passwordText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

    }

}
