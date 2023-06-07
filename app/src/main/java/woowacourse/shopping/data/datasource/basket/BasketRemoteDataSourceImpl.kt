package woowacourse.shopping.data.datasource.basket

import woowacourse.shopping.data.NetworkModule.AUTHORIZATION_FORMAT
import woowacourse.shopping.data.NetworkModule.basketProductService
import woowacourse.shopping.data.NetworkModule.encodedUserInfo
import woowacourse.shopping.data.datasource.getResult
import woowacourse.shopping.data.datasource.getResultOnHeaders
import woowacourse.shopping.data.datasource.response.BasketProductEntity
import woowacourse.shopping.data.datasource.response.ProductEntity

class BasketRemoteDataSourceImpl : BasketRemoteDataSource {

    override fun getAll(): Result<List<BasketProductEntity>> {
        val response = basketProductService.requestBasketProducts(
            authorization = AUTHORIZATION_FORMAT.format(encodedUserInfo)
        ).execute()

        return response.getResult(BASKET_PRODUCTS_ERROR)
    }

    override fun add(product: ProductEntity): Result<Long> {
        val response = basketProductService.addBasketProduct(
            authorization = AUTHORIZATION_FORMAT.format(encodedUserInfo),
            productId = product.id
        ).execute()

        return response.getResultOnHeaders(FAILED_TO_ADD_BASKET)
    }

    override fun update(basketProduct: BasketProductEntity): Result<Unit> {
        val response = basketProductService.updateBasketProduct(
            authorization = AUTHORIZATION_FORMAT.format(encodedUserInfo),
            cartItemId = basketProduct.id.toString(),
            quantity = basketProduct.count
        ).execute()

        if (response.isSuccessful) {
            return Result.success(Unit)
        }
        return Result.failure(Throwable(FAILED_TO_UPDATE_COUNT))
    }

    override fun remove(basketProduct: BasketProductEntity): Result<Unit> {
        val response = basketProductService.removeBasketProduct(
            authorization = AUTHORIZATION_FORMAT.format(encodedUserInfo),
            cartItemId = basketProduct.id.toString(),
        ).execute()

        if (response.isSuccessful) {
            return Result.success(Unit)
        }
        return Result.failure(Throwable(FAILED_TO_REMOVE_PRODUCT))
    }

    companion object {

        private const val BASKET_PRODUCTS_ERROR = "장바구니 상품을 불러올 수 없습니다."
        private const val FAILED_TO_ADD_BASKET = "장바구니 상품을 불러올 수 없습니다."
        private const val FAILED_TO_UPDATE_COUNT = "장바구니 상품의 수량을 변경에 실패했습니다."
        private const val FAILED_TO_REMOVE_PRODUCT = "장바구니 상품 삭제에 실패했습니다."
    }
}
