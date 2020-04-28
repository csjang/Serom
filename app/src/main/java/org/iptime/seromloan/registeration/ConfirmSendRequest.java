package org.iptime.seromloan.registeration;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static org.iptime.seromloan.registeration.Common.server;

/**
 * Created by csjan on 2017-10-16.
 */

public class ConfirmSendRequest extends StringRequest {

    final static private String URL = server + "Deposit.php";
    private Map<String, String> parameters;

    public ConfirmSendRequest(String userID, String depositName, String depositAmt, String sendYN, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        String temp = depositAmt.toString().replaceAll(",", "").trim();

        parameters = new HashMap<>();
        parameters.put("custNo", userID);
        parameters.put("custName", depositName);
        parameters.put("depositAmt", temp);
        parameters.put("sendYN", sendYN);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
