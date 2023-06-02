package woowacourse.shopping.ui.model.mapper

import woowacourse.shopping.ui.model.ProductModel
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.URL

object ProductMapper : Mapper<Product, ProductModel> {
    override fun Product.toView(): ProductModel {
        return ProductModel(
            id,
            picture.value,
            title,
            price
        )
    }

    override fun ProductModel.toDomain(): Product {
        return Product(
            id,
            URL(picture),
            title,
            price
        )
    }
}
