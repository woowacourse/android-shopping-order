package woowacourse.shopping.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.Money
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductName

@Serializable
data class Content(
    @SerialName("category")
    val category: String,
    @SerialName("id")
    val id: Long,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int,
)

fun Content.toDomain(): Product =
    Product(
        id = this.id,
        name =
            ProductName(
                this.name,
            ),
        price =
            Money(
                this.price.toLong(),
            ),
        imageUrl = this.imageUrl,
    )
