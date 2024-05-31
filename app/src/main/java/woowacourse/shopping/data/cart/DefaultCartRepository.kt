package woowacourse.shopping.data.cart

import woowacourse.shopping.data.cart.datasource.CartDataSource
import woowacourse.shopping.data.cart.model.CartItemData
import woowacourse.shopping.data.cart.model.CartPageData
import woowacourse.shopping.data.cart.order.OrderDataSource
import woowacourse.shopping.data.shopping.product.datasource.ProductDataSource
import woowacourse.shopping.domain.entity.CartProduct
import woowacourse.shopping.domain.repository.CartRepository
import java.util.concurrent.ConcurrentHashMap

class DefaultCartRepository(
    private val cartDataSource: CartDataSource,
    private val productDataSource: ProductDataSource,
    private val orderDataSource: OrderDataSource,
) : CartRepository {
    private var cartPageData: CartPageData? = null
    private val cartProductMapByProductId = ConcurrentHashMap<Long, CartItemData>()

    init {
        totalCartProducts()
    }

    override fun cartProducts(
        currentPage: Int,
        pageSize: Int,
    ): Result<List<CartProduct>> {
        return cartDataSource
            .loadCarts(currentPage, pageSize)
            .toCartProducts()
    }

    override fun totalCartProducts(): Result<List<CartProduct>> {
        return cartDataSource.loadTotalCarts().toCartProducts()
    }

    override fun filterCartProducts(productIds: List<Long>): Result<List<CartProduct>> {
        return runCatching {
            productIds
                .asSequence()
                .filter { productId -> cartProductMapByProductId.containsKey(productId) }
                .mapNotNull { cartProductMapByProductId[it]?.toDomain() }
                .toList()
        }
    }

    override fun updateCartProduct(
        productId: Long,
        count: Int,
    ): Result<Unit> {
        if (count < 1) return Result.failure(IllegalArgumentException("Count(=$count) 는 0이상 이여야 합니다."))
        if (cartProductMapByProductId.containsKey(productId).not()) {
            // Create CartProduct
            val product = productDataSource.productById(productId).getOrThrow()
            return cartDataSource.createCartProduct(productId, count).onSuccess { cartId ->
                cartProductMapByProductId[productId] = CartItemData(cartId, count, product)
            }.mapCatching { Unit }
        }
        // patch CartProduct
        val cartDetailData =
            cartProductMapByProductId[productId] ?: return Result.failure(
                NoSuchElementException("No has productId($productId)"),
            )
        val cartId = cartDetailData.cartId
        return cartDataSource.updateCartCount(cartId, count).onSuccess {
            cartProductMapByProductId[productId] = cartDetailData.copy(count = count)
        }
    }

    override fun deleteCartProduct(productId: Long): Result<Unit> {
        val cartDetailData =
            cartProductMapByProductId[productId] ?: return Result.failure(
                NoSuchElementException("there's no any CartProducts response to $productId"),
            )
        val cartId = cartDetailData.cartId
        return cartDataSource.deleteCartProduct(cartId).onSuccess {
            cartProductMapByProductId.remove(productId)
            val totalPage = cartPageData?.totalPageSize ?: 0
            cartPageData = cartPageData?.copy(totalProductSize = totalPage - 1)
        }
    }

    override fun canLoadMoreCartProducts(
        currentPage: Int,
        pageSize: Int,
    ): Result<Boolean> {
        if (currentPage < 0) return Result.success(false)
        val cartPageData =
            cartPageData ?: return Result.failure(
                NoSuchElementException("there's no any CartProducts"),
            )
        val minSize = currentPage * pageSize
        return Result.success(cartPageData.totalProductSize > minSize)
    }

    override fun orderCartProducts(productIds: List<Long>): Result<Unit> {
        val cartIds =
            productIds.mapNotNull {
                cartProductMapByProductId[it]?.cartId
            }
        return orderDataSource.orderProducts(cartIds).onSuccess {
            cartProductMapByProductId.clear()
            totalCartProducts()
        }
    }

    private fun Result<CartPageData>.toCartProducts(): Result<List<CartProduct>> {
        return mapCatching {
            cartPageData = it
            it.cartItems.forEach { cartItem ->
                val productId = cartItem.product.id
                cartProductMapByProductId[productId] = cartItem
            }
            it.toDomain()
        }
    }
}
