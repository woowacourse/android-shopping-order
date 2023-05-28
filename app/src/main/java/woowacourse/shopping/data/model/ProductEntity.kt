package woowacourse.shopping.data.model

import com.example.domain.model.Price
import com.example.domain.model.Product
import com.google.gson.annotations.SerializedName

data class ProductEntity(
    val id: Int,
    val name: String,
    val price: Int,
    @SerializedName("imageUrl")
    val imgUrl: String
)

fun ProductEntity.toDomain(): Product = Product(id.toLong(), name, imgUrl, Price(price))
