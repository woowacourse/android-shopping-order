package com.example.data.datasource.remote.retrofit

import com.example.domain.datasource.DataResponse
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class DataResponseCallAdapter<R : Any>(
    private val successType: Type,
) : CallAdapter<R, Call<DataResponse<R>>> {
    override fun responseType(): Type = successType

    override fun adapt(call: Call<R>): Call<DataResponse<R>> = DataResponseCall(call)
}
