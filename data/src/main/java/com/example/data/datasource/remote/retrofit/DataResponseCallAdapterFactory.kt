package com.example.data.datasource.remote.retrofit

import com.example.domain.datasource.DataResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class DataResponseCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) {
            return null
        }
        check(returnType is ParameterizedType) {
            "return type must be like A<B<>>"
        }

        val responseType = getParameterUpperBound(0, returnType)
        if (getRawType(responseType) != DataResponse::class.java) {
            return null
        }
        check(responseType is ParameterizedType) {
            "response type must be like DataResponse<A>>"
        }
        val successBodyType = getParameterUpperBound(0, responseType)
        return DataResponseCallAdapter<Any>(successBodyType)
    }
}
