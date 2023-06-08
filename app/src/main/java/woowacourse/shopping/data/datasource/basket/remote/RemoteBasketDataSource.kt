package woowacourse.shopping.data.datasource.basket.remote

import woowacourse.shopping.data.datasource.basket.BasketDataSource
import woowacourse.shopping.data.httpclient.RetrofitModule
import woowacourse.shopping.data.httpclient.request.BasketAddRequest
import woowacourse.shopping.data.httpclient.request.BasketUpdateRequest
import woowacourse.shopping.data.model.DataBasketProduct
import woowacourse.shopping.data.model.DataProduct
import woowacourse.shopping.support.framework.data.httpclient.getRetrofitCallback

class RemoteBasketDataSource : BasketDataSource.Remote {
    override fun getAll(onReceived: (List<DataBasketProduct>) -> Unit) {
        RetrofitModule.basketService.getAll().enqueue(
            getRetrofitCallback<List<DataBasketProduct>>(
                failureLogTag = this::class.java.name,
                onResponse = { _, response -> onReceived(response.body() ?: emptyList()) }
            )
        )
    }

    override fun add(product: DataProduct, onReceived: (Int) -> Unit) {
        RetrofitModule.basketService.add(BasketAddRequest(product.id)).enqueue(
            getRetrofitCallback<Unit>(
                failureLogTag = this::class.java.name,
                onResponse = { _, response ->
                    val basketId = response.headers()["Location"]?.split("/")?.last()?.toInt()
                    basketId?.let { onReceived(it) }
                }
            )
        )
    }

    override fun update(basketProduct: DataBasketProduct) {
        RetrofitModule.basketService.update(
            cartItemId = basketProduct.id,
            body = BasketUpdateRequest(basketProduct.count.value)
        ).enqueue(getRetrofitCallback<Unit>())
    }

    override fun delete(basketProduct: DataBasketProduct) {
        RetrofitModule.basketService.delete(cartItemId = basketProduct.id)
            .enqueue(getRetrofitCallback<Unit>())
    }
}
