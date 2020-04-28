package org.iptime.seromloan.registeration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class DetailActivity extends AppCompatActivity {

    private AlertDialog dialog;

    Intent intent;

    EditText custnoText;
    EditText custdetailText;
    EditText custnameText;
    EditText depositText;
    EditText resultText;
    EditText repayamtText;

    String result = "";
    String sndNo;
    String sndName;
    String sndAmt;
    String sndYN;

    Button confirmButton;
    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        intent = getIntent();

        custnoText      = (EditText) findViewById(R.id.custnoText);
        custdetailText  = (EditText) findViewById(R.id.custdetailText);
        custnameText    = (EditText) findViewById(R.id.custnameText);
        repayamtText    = (EditText) findViewById(R.id.repayamtText);
        resultText      = (EditText) findViewById(R.id.resultText);
        depositText     = (EditText) findViewById(R.id.depsitText);

        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 키보드 감추기
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                finish();
            }
        });

        confirmButton = (Button) findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmMessage();
            }
        });

        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancleMessage();
            }
        });

        depositText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().equals(result)) {
                    result = setStrDataToComma(s.toString().replace(",", ""));
                    depositText.setText(result);
                    Editable e = depositText.getText();
                    Selection.setSelection(e, result.length());

                    String temp = result.toString().replaceAll(",", "").trim();
                    if(!temp.equals("")) {

                        long repayCount = Long.parseLong(temp);
                        long repayAmt = Long.parseLong(repayamtText.getText().toString().replaceAll(",", ""));
                        long resultAmt = 0;
                        String resultStr = "0";

                        if(repayCount < 100) {
                            resultAmt = repayAmt * repayCount;
                            resultStr = Long.toString(resultAmt);
                            resultText.setText(setStrDataToComma(resultStr.toString().replace(",", "")));
                        } else {
                            resultText.setText(setStrDataToComma(temp.toString().replace(",", "")));
                        }

                    } else {
                        resultText.setText("");
                    }
                }
            }

            protected String setStrDataToComma(String str) {
                if (str.length() == 0)
                    return "";
                long value = Long.parseLong(str);
                DecimalFormat format = new DecimalFormat("###,###");
                return format.format(value);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        String cust_no = intent.getExtras().getString("cust_no");
        String cust_name = intent.getExtras().getString("cust_name");
        String repay_amt = intent.getExtras().getString("repay_amt");
        String deposit_amt = intent.getExtras().getString("deposit_amt");
        String send_yn = intent.getExtras().getString("send_yn");

        // 고객 입금정보 Query
        ProcessCustDetail(cust_no);

        // SMS Message  Parameter
        sndYN = send_yn;

        custnoText.setText(cust_no);
        custnameText.setText(cust_name);
        repayamtText.setText(repay_amt);

        depositText.setText("");
        depositText.setHint("0");

        if ( deposit_amt.equals("") || deposit_amt.equals("0") ) {

            // 키보드 보이기
            depositText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            cancelButton.setEnabled(false);
            cancelButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorGray));

        } else {

            depositText.setEnabled(false);
            resultText.setText(deposit_amt);
            confirmButton.setEnabled(false);
            confirmButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorGray));
        }

    }

    public void showConfirmMessage(){

        sndNo   = custnoText.getText().toString();
        sndName = custnameText.getText().toString();
        sndAmt  = resultText.getText().toString();

        if ( sndAmt.equals("") ) {
            return;
        }

        confirmButton.setEnabled(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setIcon(R.drawable.alert);
        builder.setCancelable(false);
        builder.setTitle("전송");
        builder.setMessage(sndName + ", " + sndAmt + "원\n" + "전송 합니다.");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProcessConfirmDataSend(sndName);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmButton.setEnabled(true);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void  ProcessConfirmDataSend(final String sndName) {

        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Toast.makeText(getApplicationContext(), sndName + " : 전송 되었습니다.",  Toast.LENGTH_LONG).show();

                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                        dialog = builder.setMessage("전송에 실패했습니다.\n앱 종료후 다시 실행하세요.")
                                .setCancelable(false)
                                .setNegativeButton("확인", null)
                                .create();
                        dialog.show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        ConfirmSendRequest sendRequest = new ConfirmSendRequest(sndNo, sndName, sndAmt, sndYN, responseListner);
        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
        queue.add(sendRequest);
    }

    public void showCancleMessage(){

        sndNo   = custnoText.getText().toString();
        sndName = custnameText.getText().toString();
        sndAmt  = resultText.getText().toString();

        if ( sndAmt.equals("") ) {
            return;
        }

        cancelButton.setEnabled(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setIcon(R.drawable.alert);
        builder.setCancelable(false);
        builder.setTitle("취소");
        builder.setMessage(sndName + ", " + sndAmt + "원\n" + "취소 합니다.");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProcessCancleDataSend(sndName);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelButton.setEnabled(true);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void  ProcessCancleDataSend(final String sndName) {

        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Toast.makeText(getApplicationContext(), sndName + " : 취소 되었습니다.",  Toast.LENGTH_LONG).show();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                        dialog = builder.setMessage("전송에 실패했습니다.\n앱 종료후 다시 실행하세요.")
                                .setCancelable(false)
                                .setNegativeButton("확인", null)
                                .create();
                        dialog.show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        CancleSendRequest sendRequest = new CancleSendRequest(sndNo, sndAmt, sndYN, responseListner);
        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
        queue.add(sendRequest);
    }

    public void ProcessCustDetail(final String sndNo) {

        Response.Listener<String> responseListner = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    String cust_detail = jsonResponse.getString("cust_detail");
                    custdetailText.setText( cust_detail );

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        CustDetailRequest sendRequest = new CustDetailRequest(sndNo, responseListner);
        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
        queue.add(sendRequest);
    }

}

