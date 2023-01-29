package com.taehyeon.korail.common.response;


import org.springframework.http.HttpStatus;

public interface CommonResponseType {
    String getMsg();
    String getCode();
    HttpStatus getHttpStatus();
}
