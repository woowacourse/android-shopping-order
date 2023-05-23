package woowacourse.shopping.data

import okhttp3.OkHttpClient
import okhttp3.Request
import woowacourse.shopping.ShoppingApplication

object ShoppingOkHttpClient : OkHttpClient() {

    fun buildRequest(url: String, method: String): Request = Request
        .Builder()
        .header("Authorization", "Basic ${ShoppingApplication.pref.getToken()}")
        .url(url)
        .method(method, null)
        .build()
}
