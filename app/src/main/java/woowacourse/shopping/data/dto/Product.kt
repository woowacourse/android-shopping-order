package woowacourse.shopping.data.dto

import com.google.gson.annotations.SerializedName

data class Product(
            @SerializedName("category")
            val category: String,
            @SerializedName("id")
            val id: Long,
            @SerializedName("imageUrl")
            val imageUrl: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("price")
            val price: Int
        )
