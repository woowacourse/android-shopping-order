package woowacourse.shopping.data.repository

import com.example.domain.model.CartProduct
import com.example.domain.repository.CartRepository
import woowacourse.shopping.data.datasource.remote.shoppingcart.ShoppingCartDataSource
import woowacourse.shopping.mapper.toDomain
import java.io.IOException

class CartRepositoryImpl(
    private val shoppingCartDataSource: ShoppingCartDataSource,
) : CartRepository {

    override fun getAllProductInCart(): Result<List<CartProduct>> {
        val result = shoppingCartDataSource.getAllProductInCart()
        return if (result.isSuccess) {
            val productsDomain = result.getOrNull()?.map { productDto -> productDto.toDomain() }
            Result.success(productsDomain ?: emptyList())
        } else {
            Result.failure(Throwable(result.exceptionOrNull()?.message))
        }
    }

    override fun insert(
        id: Long,
        quantity: Int,
    ): Result<Unit> {
        val result = shoppingCartDataSource.postProductToCart(id, quantity)
        return if (result.isSuccess) {
            Result.success(Unit)
        } else {
            Result.failure(Throwable(result.exceptionOrNull()?.message))
        }
    }

    override fun updateCount(
        id: Long,
        count: Int,
    ): Result<Unit> {
        val result = shoppingCartDataSource.patchProductCount(id, count)
        return if (result.isSuccess) {
            Result.success(Unit)
        } else {
            Result.failure(IOException("Response unsuccessful"))
        }
    }

    override fun remove(id: Long): Result<Unit> {
        val result = shoppingCartDataSource.deleteProductInCart(id)
        return if (result.isSuccess) {
            Result.success(Unit)
        } else {
            Result.failure(Throwable(result.exceptionOrNull()?.message))
        }
    }

    override fun findById(id: Long): Result<CartProduct?> {
        val result = getAllProductInCart()
        return if (result.isSuccess) {
            val cartProduct = result.getOrNull()?.find { it.product.id == id }
            Result.success(cartProduct)
        } else {
            Result.failure(Throwable(result.exceptionOrNull()?.message))
        }
    }

    override fun getSubList(offset: Int, step: Int): Result<List<CartProduct>> {
        val result = getAllProductInCart()
        return if (result.isSuccess) {
            val limitedProducts = result.getOrThrow().subList(
                offset.coerceAtMost(result.getOrThrow().size),
                (offset + step).coerceAtMost(result.getOrThrow().size),
            )
            Result.success(limitedProducts)
        } else {
            Result.failure(Throwable(result.exceptionOrNull()?.message))
        }
    }
}
