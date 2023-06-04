package woowacourse.shopping.data.remote.request

import com.google.gson.annotations.SerializedName

data class OrderProductDTO(
    @SerializedName("productResponse")
    val product: ProductDTO,
    val quantity: Int,
)
