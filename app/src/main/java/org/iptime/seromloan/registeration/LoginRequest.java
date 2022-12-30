package org.iptime.seromloan.registeration;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static org.iptime.seromloan.registeration.Common.server;

/**
 * Created by csjan on 2017-10-16.
 */

public class LoginRequest extends StringRequest {

    final static private String URL = server + "UserLogin.php";

    private Map<String, String> parameters;

    public LoginRequest(String userID, String userPassword, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        try {
            parameters = new HashMap<>();
            parameters.put("userID", userID);
            parameters.put("userPassword", userPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
