package woowacourse.shopping.data.network

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthentication : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val header = basicAuthenticationHeader()

        val newRequest =
            request.newBuilder()
                .addHeader("Authorization", header)
                .build()

        return chain.proceed(newRequest)
    }

    private fun basicAuthenticationHeader(): String {
        val userId = woowacourse.shopping.BuildConfig.USER_ID
        val userPassword = woowacourse.shopping.BuildConfig.USER_PASSWORD

        val valueToEncode = "$userId:$userPassword"
        val encode = Base64.encodeToString(valueToEncode.toByteArray(), Base64.NO_WRAP)
        return "Basic $encode"
    }
}
