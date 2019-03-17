package com.ytzl.gotrip.rpc.api;

/**
 * 短信发生RPC
 *
 */
public interface RpcSendMessageService {
    /**
     * 发生短信
     * @param phone        手机号   多手机号使用
     * @param templateId   摸版Id  未上线应用填写
     * @param code         短信验证码
     */
    public void sendMessage(String phone,String templateId,String code);
}
