package org.channel.tester.web.vo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public final class ResponseView {

    private static final String KEY_CODE = "code";
    private static final String KEY_ERROR_CODE = "errorCode";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_RESULT = "result";
    private static final String KEY_QUERY = "query";
    private static final String KEY_RESULT_LIST = "list";
    private static final Integer CODE_SUCCESS = 0;

    private ResponseView() {
    }

    @SuppressWarnings("unchecked")
    private static ResponseEntity<?> buildView(Object resultObject, Integer code, String message) {
        return buildView(resultObject, code, code, message);
    }

    private static ResponseEntity<?> buildView(Object resultObject, Integer code, Integer errorCode, String message) {
        Map<String, Object> resultMap = new HashMap<>(3);
        resultMap.put(KEY_CODE, code);
        resultMap.put(KEY_ERROR_CODE, errorCode);
        resultMap.put(KEY_MESSAGE, message);
        resultMap.put(KEY_RESULT, resultObject);
        return new ResponseEntity(resultMap, HttpStatus.OK);
    }

    /**
     * 成功状况下返回json.
     * result对应的对象需自己构造.
     * added by gezhicheng to support h5 page assembling since 20170712.
     *
     * @param resultObject 构造完成的result对象.
     * @return
     */
    public static ResponseEntity<?> buildViewByConstructedObject(Object resultObject) {
        Map<String, Object> resultMap = new HashMap<>(3);
        resultMap.put(KEY_CODE, CODE_SUCCESS);
        resultMap.put(KEY_MESSAGE, "");
        resultMap.put(KEY_RESULT, resultObject);
        return new ResponseEntity(resultMap, HttpStatus.OK);

    }

    /**
     * 返回成功操作.
     *
     * @param resultObject 返回结果.
     * @return
     */
    public static ResponseEntity<?> success(Object resultObject) {
        return success(resultObject, "");
    }

    /**
     * 返回成功操作.
     *
     * @param resultObject 返回结果.
     * @param message      描述信息.
     * @return
     */
    public static ResponseEntity<?> success(Object resultObject, String message) {
        return buildView(resultObject, CODE_SUCCESS, message);
    }

    /**
     * 返回失败操作.
     *
     * @param errorCode 前端要求的状态码.
     * @param message   错误信息.
     * @return
     */
    public static ResponseEntity<?> fail(Integer errorCode, String message) {
        return buildView(null, errorCode, message);
    }

    /**
     * 返回失败操作.
     *
     * @param code      前端要求的状态码.
     * @param errorCode 前端要求的状态码.
     * @param message   错误信息.
     * @return
     */
    public static ResponseEntity<?> fail(Integer code, Integer errorCode, String message) {
        return buildView(null, code, errorCode, message);
    }

    /**
     * 返回失败操作.
     *
     * @param code         前端要求的状态码.
     * @param errorCode    错误码.
     * @param message      错误信息.
     * @param resultObject 错误结果.
     * @return
     */
    public static ResponseEntity<?> fail(Integer code, Integer errorCode, String message, Object resultObject) {
        return buildView(resultObject, code, errorCode, message);
    }

}
