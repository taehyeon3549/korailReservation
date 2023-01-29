package com.taehyeon.korail.domain.reservation

import com.taehyeon.korail.common.BaseResponse
import com.taehyeon.korail.common.response.BaseResponseType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ReservationController {

    @GetMapping
    fun hello() : BaseResponse {
        return BaseResponse(BaseResponseType.SEND_OK);
    }
}