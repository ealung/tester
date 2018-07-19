package org.channel.tester.vo;

/**
 * @author zhangchanglu
 * @since 2018/05/08 21:13.
 */
public class WebTestParam {
    private String k;
    private Object v;

    public WebTestParam(String k, Object v) {
        this.k = k;
        this.v = v;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public Object getV() {
        return v;
    }

    public void setV(Object v) {
        this.v = v;
    }
}
