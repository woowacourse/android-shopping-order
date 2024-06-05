package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.domain.model.ProductDomain
import woowacourse.shopping.domain.model.ProductItemDomain

data class ProductResponse(
    @SerializedName("content")
    val products: List<Product>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: Sort,
    val totalElements: Int,
    val totalPages: Int,
)

fun ProductResponse.toProductDomain(): ProductDomain =
    ProductDomain(
        products = products.map(Product::toProductItemDomain),
        last = last,
    )

fun Product.toProductItemDomain(): ProductItemDomain =
    ProductItemDomain(
        category = category,
        id = id,
        imageUrl = imageUrl,
        name = name,
        price = price,
    )
