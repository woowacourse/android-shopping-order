package woowacourse.shopping.ui.shopping

import com.example.domain.model.ProductItem
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.ProductUIModel

sealed interface ProductsItemType {
    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
        const val TYPE_FOOTER = 2
    }
}

data class RecentProductsItem(val product: List<ProductUIModel>) : ProductsItemType
data class ProductItemModel(val product: ProductUIModel, val count: Int) : ProductsItemType
object ProductReadMore : ProductsItemType

fun ProductItem.toUIModel(): ProductItemModel {
    return ProductItemModel(product.toUIModel(), count)
}
