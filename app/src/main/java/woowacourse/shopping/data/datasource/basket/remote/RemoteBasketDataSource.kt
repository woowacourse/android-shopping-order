package woowacourse.shopping.data.datasource.basket.remote

import woowacourse.shopping.data.datasource.basket.BasketDataSource
import woowacourse.shopping.data.model.DataBasketProduct

class RemoteBasketDataSource : BasketDataSource.Remote {
    override fun getAll(): List<DataBasketProduct> {
        TODO("Not yet implemented")
    }

    override fun getByProductId(productId: Int): DataBasketProduct? {
        TODO("Not yet implemented")
    }

    override fun add(basketProduct: DataBasketProduct) {
        TODO("Not yet implemented")
    }

    override fun minus(basketProduct: DataBasketProduct) {
        TODO("Not yet implemented")
    }

    override fun overWriteUpdate(basketProduct: DataBasketProduct) {
        TODO("Not yet implemented")
    }

    override fun remove(basketProduct: DataBasketProduct) {
        TODO("Not yet implemented")
    }
}
