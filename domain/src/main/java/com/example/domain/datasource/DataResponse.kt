package com.example.domain.datasource

import com.example.domain.datasource.DataResponse.Companion.NULL_BODY_ERROR_CODE
import com.example.domain.datasource.DataResponse.Companion.NULL_BODY_ERROR_STRING
import com.example.domain.datasource.DataResponse.Companion.ZIP_FAILURE_CODE
import com.example.domain.datasource.DataResponse.Companion.ZIP_FAILURE_STRING
import java.io.IOException

sealed class DataResponse<out T : Any> {
    data class Success<T : Any>(val body: T?) : DataResponse<T>()

    data class Failure(val code: Int, val error: String?) : DataResponse<Nothing>()

    data class Error(val exception: IOException) : DataResponse<Nothing>()

    data class Unexpected(val t: Throwable?) : DataResponse<Nothing>()

    companion object {
        const val NULL_BODY_ERROR_CODE = 999
        const val NULL_BODY_ERROR_STRING = "Null body failure"
        const val ZIP_FAILURE_CODE = 1000
        const val ZIP_FAILURE_STRING = "Zip failure"
    }
}

fun <T : Any, R : Any> DataResponse<T>.map(functor: (T) -> R?): DataResponse<R> {
    val body =
        when (this) {
            is DataResponse.Success ->
                this.body ?: return DataResponse.Failure(
                    NULL_BODY_ERROR_CODE,
                    NULL_BODY_ERROR_STRING,
                )

            is DataResponse.Failure -> return DataResponse.Failure(code, error)
            is DataResponse.Error -> return DataResponse.Error(exception)
            is DataResponse.Unexpected -> return DataResponse.Unexpected(t)
        }
    val ret =
        functor(body)
            ?: return DataResponse.Failure(
                NULL_BODY_ERROR_CODE,
                NULL_BODY_ERROR_STRING,
            )
    return DataResponse.Success(ret)
}

fun <T : Any, P : DataResponse<T>, R : Any> DataResponse<P>.chain(functor: (T) -> R?): DataResponse<R> {
    val response =
        when (this) {
            is DataResponse.Success ->
                this.body ?: return DataResponse.Failure(
                    NULL_BODY_ERROR_CODE,
                    NULL_BODY_ERROR_STRING,
                )

            is DataResponse.Failure -> return DataResponse.Failure(code, error)
            is DataResponse.Error -> return DataResponse.Error(exception)
            is DataResponse.Unexpected -> return DataResponse.Unexpected(t)
        } as DataResponse<T>
    val body =
        when (response) {
            is DataResponse.Success ->
                response.body ?: return DataResponse.Failure(
                    NULL_BODY_ERROR_CODE,
                    NULL_BODY_ERROR_STRING,
                )

            is DataResponse.Failure -> return DataResponse.Failure(response.code, response.error)
            is DataResponse.Error -> return DataResponse.Error(response.exception)
            is DataResponse.Unexpected -> return DataResponse.Unexpected(response.t)
        }
    return DataResponse.Success(functor(body))
}

fun <S : Any, T : Any, R : Any> zip(
    first: DataResponse<S>,
    second: DataResponse<T>,
    functor: (S, T) -> R,
): DataResponse<R> {
    if (
        first is DataResponse.Success &&
        second is DataResponse.Success
    ) {
        if (first.body == null || second.body == null) {
            return DataResponse.Failure(
                NULL_BODY_ERROR_CODE,
                NULL_BODY_ERROR_STRING,
            )
        }
        val body = functor(first.body, second.body)
        return DataResponse.Success(body)
    }
    return DataResponse.Failure(ZIP_FAILURE_CODE, ZIP_FAILURE_STRING)
}

fun <T : Any> DataResponse<T>.onSuccess(func: (body: T) -> Unit): DataResponse<T> {
    if (this is DataResponse.Success && this.body != null) func(body)
    return this
}

fun <T : Any> DataResponse<T>.onFailure(func: (code: Int, error: String?) -> Unit): DataResponse<T> {
    if (this is DataResponse.Failure) func(code, error)
    return this
}
