package com.taehyeon.korail.common

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.taehyeon.korail.common.response.BaseResponseType


@JsonInclude(JsonInclude.Include.NON_NULL)
data class BaseResponse(
    @JsonProperty("code") val code: String
    , @JsonProperty("message") val message: String
    , @JsonProperty("data") val data: Any?
){
    constructor(baseResponseType: BaseResponseType) : this(baseResponseType.code, baseResponseType.msg, null)
}