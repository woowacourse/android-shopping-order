package woowacourse.shopping.data.calladapter

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import retrofit2.CallAdapter
import retrofit2.Retrofit

class ApiResultCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != ApiResult::class.java) return null
        if (returnType !is ParameterizedType) return null

        val bodyType = getParameterUpperBound(0, returnType)
        return ApiResultCallAdapter<Any>(bodyType)
    }
}
