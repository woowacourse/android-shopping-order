package woowacourse.shopping.data.network

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ResponseAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        if (Call::class.java != getRawType(returnType)) return null
        check(returnType is ParameterizedType) { ERROR_RETURN_GENERIC_TYPE }

        val responseType = getParameterUpperBound(0, returnType)
        if (getRawType(responseType) != Result::class.java) return null
        check(responseType is ParameterizedType) { ERROR_RESULT_GENERIC_TYPE }

        val successType = getParameterUpperBound(0, responseType)

        return ResponseAdapter<Any>(successType)
    }

    companion object {
        private const val ERROR_RETURN_GENERIC_TYPE = "반환 타입은 제네릭 타입이어야 합니다"
        private const val ERROR_RESULT_GENERIC_TYPE = "Result는 제네릭 타입이어야 합니다"
    }
}
