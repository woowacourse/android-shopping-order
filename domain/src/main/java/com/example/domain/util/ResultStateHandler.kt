package com.example.domain.util

sealed class CustomResult<out T : Any> {
    data class SUCCESS<out T : Any>(val data: T) : CustomResult<T>()
    data class FAIL(val error: Error) : CustomResult<Nothing>()
}

sealed class Error(val errorMessage: String) {
    object NoSuchId : Error("해당 ID가 없습니다")
    object Disconnect : Error("서버가 연결되지 않았습니다")
}
