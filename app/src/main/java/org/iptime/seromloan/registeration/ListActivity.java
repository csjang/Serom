package org.iptime.seromloan.registeration;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;

import static org.iptime.seromloan.registeration.Common.server;

public class ListActivity extends AppCompatActivity {

    static final int REQUEST_ACT = 1;

    Intent intent;

    CustomerAdapter adapter;
    ListView listView;

    TextView todayView;
    TextView totalAmtView;
    long totalAmt = 0;

    int scrollPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        intent = getIntent();

        // 오늘 일자 가져오기
        todayView = (TextView) findViewById(R.id.todayView);
        todayView.setText( getTodayDate() );

        totalAmtView = (TextView) findViewById(R.id.totalamtView);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new CustomerAdapter();
        listView.setAdapter(adapter);

        new BackgroundTast().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if ( !todayView.getText().toString().equals(getTodayDate()) ) {
                    new AlertDialog.Builder(ListActivity.this)
                            .setIcon(R.drawable.alert)
                            .setTitle("경고")
                            .setCancelable(false)
                            .setMessage("일자가 변동 되었습니다.\n어플를 다시 실행해주세요.")
                            .setNegativeButton("확인", null)
                            .show();
                    return;

                }

                Intent intent = new Intent(ListActivity.this, DetailActivity.class);
                CustomerItems items = (CustomerItems) adapter.getItem(position);

                String custNo;
                String custName;
                String repayAmt;
                String depositAmt;
                String sendYN;

                scrollPosition = listView.getFirstVisiblePosition();

                custNo = items.getCustNo();
                custName = items.getShopName();
                repayAmt = items.getRepayAmt();
                depositAmt = items.getDepositAmt();
                sendYN = items.getSendYN();

                if ( repayAmt.equals("") || repayAmt.equals("0") ) {

                    new AlertDialog.Builder(ListActivity.this)
                            .setIcon(R.drawable.alert)
                            .setTitle("안내")
                            .setCancelable(false)
                            .setMessage("일상환액이 0원 입니다.\n고객정보를 확인해 주세요.")
                            .setPositiveButton("확인", null)
                            .show();

                    return;
                }

                intent.putExtra("cust_no", custNo);
                intent.putExtra("cust_name", custName);
                intent.putExtra("repay_amt", repayAmt);
                intent.putExtra("deposit_amt", depositAmt);
                intent.putExtra("send_yn", sendYN);

                ListActivity.this.startActivityForResult(intent, REQUEST_ACT);
            }
        });
    }

    public String getTodayDate() {

        SimpleDateFormat df = new SimpleDateFormat("MM/dd", Locale.KOREA);
        String str_date = df.format(new Date());

        return str_date;
    }

    class CustomerAdapter extends BaseAdapter {

        ArrayList<CustomerItems> items = new ArrayList<CustomerItems>();

        public void addItem(CustomerItems item) {
            items.add(item);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            CustomerItemsView view = null;

            // View 재사용 : 리스트가 길어질때 화면에 안보여지는 경우 재사용
            if (convertView == null) {
                view = new CustomerItemsView(getApplicationContext());
            } else {
                view = (CustomerItemsView) convertView;
            }

            CustomerItems item =  items.get(position);

            view.setShopname(item.getShopName());
            view.setCustno(item.getCustNo());
            view.setRepayAmt(item.getRepayAmt());
            view.setDepositAmt(item.getDepositAmt());
            view.setImage(item.getResId());
            view.setSmsImage(item.getSendYN());

            return view;
        }
    }


    class BackgroundTast extends AsyncTask<Void, Void, String> {

        String target;

        @Override
        protected void onPreExecute(){
            String emp_no = intent.getExtras().getString("emp_no");
            target = server + "CustomerList.php?emp_no=" + emp_no;
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while( (temp = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        @Override
        public void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                int count = 0;
                String cust_no, shop_name, sms_send, repay_amt, deposit_amt, visit, sendYN;

                while(count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);

                    cust_no = object.getString("cust_no");
                    shop_name = object.getString("shop_name");
                    sms_send  = object.getString("sms_send");
                    repay_amt = object.getString("repay_amt");
                    deposit_amt = object.getString("deposit_amt");
                    visit = object.getString("visit");

                    totalAmt += Long.parseLong( deposit_amt.toString().replace(",", "") );

                    int resID;
                    switch (visit) {
                        case "1" :
                            resID = (R.drawable.icon03);
                            break;
                        default :
                            resID = (R.drawable.icon10);
                            break;
                    }
                    adapter.addItem(new CustomerItems(cust_no, shop_name, repay_amt, deposit_amt, resID, sms_send));
                    count++;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(scrollPosition);

            DecimalFormat format = new DecimalFormat("###,###");
            totalAmtView.setText( format.format(totalAmt) );
        }

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(ListActivity.this)
                .setIcon(R.drawable.alert)
                .setTitle("종료")
                .setCancelable(false)
                .setMessage("종료 하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("아니오", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
              return;
        }

        if (requestCode == REQUEST_ACT) {

            totalAmt = 0;

            listView = (ListView) findViewById(R.id.listView);
            adapter = new CustomerAdapter();
            listView.setAdapter(adapter);

            new BackgroundTast().execute();
        }

    }
}
