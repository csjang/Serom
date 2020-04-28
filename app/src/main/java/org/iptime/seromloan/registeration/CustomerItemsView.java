package org.iptime.seromloan.registeration;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by csjan on 2017-10-16.
 */

public class CustomerItemsView extends LinearLayout {

    TextView custnoView;
    TextView shopnameView;
    TextView repayamtView;
    TextView depositamtView;
    ImageView imageView;
    ImageView sendView;

    public CustomerItemsView(Context context) {
        super(context);
        init(context);
    }

    public CustomerItemsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custommer_items, this, true);

        custnoView = (TextView) findViewById(R.id.cust_noView);
        shopnameView = (TextView) findViewById(R.id.shop_nameView);
        repayamtView = (TextView) findViewById(R.id.repay_amtView);
        depositamtView = (TextView) findViewById(R.id.deposit_amtView);

        imageView = (ImageView) findViewById(R.id.imageView);
        sendView = (ImageView) findViewById(R.id.sendView);
    }


    public void setCustno(String custno) { custnoView.setText(custno); }

    public void setShopname(String shopname) {
        shopnameView.setText(shopname);
    }

    public void setRepayAmt(String repayamt) {
        repayamtView.setText(repayamt);
    }

    public void setDepositAmt(String depositamt) {
        depositamtView.setText(depositamt);
    }

    public void setImage(int resId) { imageView.setImageResource(resId); }

    public void setSmsImage(String sndYN) {

        if ( sndYN.equals("Y") ) {
            sendView.setImageResource(R.drawable.sms);
        } else {
            sendView.setImageResource(R.drawable.blank);
        }
    }
}
