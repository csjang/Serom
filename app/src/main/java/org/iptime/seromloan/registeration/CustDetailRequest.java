package org.iptime.seromloan.registeration;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static org.iptime.seromloan.registeration.Common.server;

/**
 * Created by csjan on 2017-10-16.
 */

public class CustDetailRequest extends StringRequest {

    final static private String URL = server + "CustomerDetail.php";
    private Map<String, String> parameters;

    public CustDetailRequest(String custNo, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("custNo", custNo);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
