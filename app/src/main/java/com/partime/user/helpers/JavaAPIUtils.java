package com.partime.user.helpers;


/**
 * Created by vipra on 7/3/17.
 */


public class JavaAPIUtils {

    private JavaAPIUtils() {

    }

    public static JavaAPIService getAPIService() {
        return JavaRetrofitClient.getClient("https://www.partime.org/partime/public/index.php/api/").create(JavaAPIService.class);
    }
}
