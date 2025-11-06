package com.gooodh.common.user.service;

public interface SmsService {
    void sendMsg(String phone,String code, String type);
}
