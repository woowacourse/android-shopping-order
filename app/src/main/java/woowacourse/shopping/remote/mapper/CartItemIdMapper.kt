package woowacourse.shopping.remote.mapper

import retrofit2.Response
import woowacourse.shopping.data.model.remote.CartItemIdDto

fun <T> Response<T>.toCartItemIdDto(): CartItemIdDto =
    CartItemIdDto(
        id =
            this.headers()["location"]?.split("/")?.last()?.toInt()
                ?: throw IllegalArgumentException(),
    )
