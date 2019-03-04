package com.example.fang.model;

public class Captcha {

    /**
     * yzm_url : /media/yzm/BE939C62AA194C917D17C37A912A00E8.jpg
     * yzm_cookie : JSESSIONID=BE939C62AA194C917D17C37A912A00E8.tomcat2; Path=/, sto-id-20480=IFHJCKNCFAAA; Expires=Wed, 06-Sep-2028 02:31:47 GMT; Path=/
     */

    private String yzm_url;
    private String yzm_cookie;

    public String getYzm_url() {
        return yzm_url;
    }

    public void setYzm_url(String yzm_url) {
        this.yzm_url = yzm_url;
    }

    public String getYzm_cookie() {
        return yzm_cookie;
    }

    public void setYzm_cookie(String yzm_cookie) {
        this.yzm_cookie = yzm_cookie;
    }
}
