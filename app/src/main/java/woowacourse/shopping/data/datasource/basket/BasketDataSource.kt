package woowacourse.shopping.data.datasource.basket

import woowacourse.shopping.data.model.DataBasketProduct
import woowacourse.shopping.data.model.DataProduct

interface BasketDataSource {

    interface Remote {
        fun getAll(onReceived: (List<DataBasketProduct>) -> Unit)

        fun add(product: DataProduct, onReceived: (Int) -> Unit)

        fun update(basketProduct: DataBasketProduct)

        fun remove(basketProduct: DataBasketProduct)
    }
}
