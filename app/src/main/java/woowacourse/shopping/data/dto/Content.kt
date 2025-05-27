package woowacourse.shopping.data.dto

import com.google.gson.annotations.SerializedName

data class Content(
        @SerializedName("id")
        val id: Long,
        @SerializedName("product")
        val product: Product,
        @SerializedName("quantity")
        val quantity: Int
    ) {

    }
