package woowacourse.shopping.remote.model.response

import retrofit2.http.Header

data class PostCartItemResponse(
    @Header("location") val location: String,
)
