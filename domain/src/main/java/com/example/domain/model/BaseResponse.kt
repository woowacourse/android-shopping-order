package com.example.domain.model

sealed class BaseResponse<T> {
    data class SUCCESS<T>(val response: T) : BaseResponse<T>()
    data class FAILED<T>(val code: Int, val message: String) : BaseResponse<T>()
    class NETWORK_ERROR<T> : BaseResponse<T>()
}
