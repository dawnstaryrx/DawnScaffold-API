package com.gooodh.common.user.service;

import com.gooodh.model.dto.EmailDTO;

public interface EmailService {
    void sendMsg(EmailDTO emailDTO);
}
