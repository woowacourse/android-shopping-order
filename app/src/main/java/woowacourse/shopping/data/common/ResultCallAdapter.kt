//package woowacourse.shopping.data.common

//import retrofit2.Call
//import retrofit2.CallAdapter
//import java.lang.reflect.Type
//
//class ResultCallAdapter(
//    private val resultType: Type
//): CallAdapter<Type, Call<ResponseResult<Type>>> {
//    override fun responseType(): Type = resultType
//
//    override fun adapt(call: Call<Type>): Call<ResponseResult<Type>> {
//        return NetworkResultCall(call)
//    }
//}