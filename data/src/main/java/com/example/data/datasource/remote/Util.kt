package com.example.data.datasource.remote

import com.example.domain.datasource.DataResponse
import retrofit2.Call

fun <T : Any> Call<DataResponse<T>>.executeForDataResponse(): DataResponse<T> {
    return this.execute().body() ?: DataResponse.Failure(
        DataResponse.NULL_BODY_ERROR_CODE,
        DataResponse.NULL_BODY_ERROR_STRING,
    )
}
