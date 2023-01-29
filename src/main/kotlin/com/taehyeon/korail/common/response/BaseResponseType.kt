package com.taehyeon.korail.common.response

import org.springframework.http.HttpStatus


enum class BaseResponseType(val code: String
                            , val msg: String
                            , val httpStatus: HttpStatus){

    SEND_OK("2001", "OK", HttpStatus.OK)
    , SEND_ERROR("5001", "Server Error :: %s", HttpStatus.INTERNAL_SERVER_ERROR)
    , PARAM_ERROR("4001", "Bad Request :: %s", HttpStatus.BAD_REQUEST)
    , DECODE_ERROR("4001", "Wrong Identity Request :: %s", HttpStatus.BAD_REQUEST)
    , BIGQUERY_ERROR("5001", "BigQuery Error :: %s", HttpStatus.INTERNAL_SERVER_ERROR)
    , NOT_FOUND_ERROR("4001", "Not Found Error :: %s", HttpStatus.NOT_FOUND)
    , EXCEL_ERROR("5001", "Excel Error :: %s", HttpStatus.INTERNAL_SERVER_ERROR)

}