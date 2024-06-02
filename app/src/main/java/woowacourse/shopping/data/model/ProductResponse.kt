package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.domain.model.ProductDomain
import woowacourse.shopping.domain.model.RemoteProductItemDomain

data class ProductResponse(
    @SerializedName("content")
    val products: List<Product>,
    @SerializedName("empty")
    val empty: Boolean,
    @SerializedName("first")
    val first: Boolean,
    @SerializedName("last")
    val last: Boolean,
    @SerializedName("number")
    val number: Int,
    @SerializedName("numberOfElements")
    val numberOfElements: Int,
    @SerializedName("pageable")
    val pageable: Pageable,
    @SerializedName("size")
    val size: Int,
    @SerializedName("sort")
    val sort: Sort,
    @SerializedName("totalElements")
    val totalElements: Int,
    @SerializedName("totalPages")
    val totalPages: Int,
)

fun ProductResponse.toProductDomain(): ProductDomain =
    ProductDomain(
        products = products.map(Product::toProductItemDomain),
        last = last
    )

fun Product.toProductItemDomain(): RemoteProductItemDomain =
    RemoteProductItemDomain(
        category = category,
        id = id,
        imageUrl = imageUrl,
        name = name,
        price = price
    )
