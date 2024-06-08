package com.example.domain.datasource

import java.io.IOException

sealed class DataResponse<out T : Any> {
    data class Success<T : Any>(val body: T?) : DataResponse<T>()

    data class Failure(val code: Int, val error: String?) : DataResponse<Nothing>()

    data class Error(val exception: IOException) : DataResponse<Nothing>()

    data class Unexpected(val t: Throwable?) : DataResponse<Nothing>()

    companion object {
        const val NULL_BODY_ERROR_CODE = 999
        const val NULL_BODY_ERROR_STRING = "Null body failure"
    }
}

fun <T : Any, R : Any> DataResponse<T>.map(functor: (T) -> R?): DataResponse<R> {
    val body =
        when (this) {
            is DataResponse.Success ->
                this.body ?: return DataResponse.Failure(
                    DataResponse.NULL_BODY_ERROR_CODE,
                    DataResponse.NULL_BODY_ERROR_STRING,
                )

            is DataResponse.Failure -> return DataResponse.Failure(code, error)
            is DataResponse.Error -> return DataResponse.Error(exception)
            is DataResponse.Unexpected -> return DataResponse.Unexpected(t)
        }
    val ret =
        functor(body)
            ?: return DataResponse.Failure(
                DataResponse.NULL_BODY_ERROR_CODE,
                DataResponse.NULL_BODY_ERROR_STRING,
            )
    return DataResponse.Success(ret)
}

fun <T : Any, P : DataResponse<T>, R : Any> DataResponse<P>.chain(functor: (T) -> R): DataResponse<R> {
    val response =
        when (this) {
            is DataResponse.Success ->
                this.body ?: return DataResponse.Failure(
                    DataResponse.NULL_BODY_ERROR_CODE,
                    DataResponse.NULL_BODY_ERROR_STRING,
                )

            is DataResponse.Failure -> return DataResponse.Failure(code, error)
            is DataResponse.Error -> return DataResponse.Error(exception)
            is DataResponse.Unexpected -> return DataResponse.Unexpected(t)
        } as DataResponse<T>
    val body =
        when (response) {
            is DataResponse.Success ->
                response.body ?: return DataResponse.Failure(
                    DataResponse.NULL_BODY_ERROR_CODE,
                    DataResponse.NULL_BODY_ERROR_STRING,
                )
            is DataResponse.Failure -> return DataResponse.Failure(response.code, response.error)
            is DataResponse.Error -> return DataResponse.Error(response.exception)
            is DataResponse.Unexpected -> return DataResponse.Unexpected(response.t)
        }
    return DataResponse.Success(functor(body))
}