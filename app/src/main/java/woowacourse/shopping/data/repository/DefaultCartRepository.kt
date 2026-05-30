package woowacourse.shopping.data.repository

import kotlinx.coroutines.CancellationException
import woowacourse.shopping.data.source.remote.datasource.CartRemoteDataSource
import woowacourse.shopping.data.source.remote.dto.cart.CartContent
import woowacourse.shopping.domain.model.AddItemResult
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Money
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductName
import woowacourse.shopping.domain.model.RemoveItemResult
import woowacourse.shopping.domain.model.UpdateItemResult
import woowacourse.shopping.domain.repository.CartRepository

class DefaultCartRepository(
    private val remoteDataSource: CartRemoteDataSource,
) : CartRepository {
    private suspend fun getCartContents(): List<CartContent> {
        val all = mutableListOf<CartContent>()
        var page = 0
        while (true) {
            val response = remoteDataSource.getCartItems(page, PAGE_SIZE)
            all += response.cartContent
            if (response.last) break
            page++
        }
        return all
    }

    override suspend fun getCart(): Cart {
        val cartContents = getCartContents()

        val items =
            cartContents.map {
                CartItem(
                    id = it.id,
                    product =
                        Product(
                            id = it.product.id,
                            name =
                                ProductName(
                                    it.product.name,
                                ),
                            price =
                                Money(
                                    it.product.price.toLong(),
                                ),
                            imageUrl = it.product.imageUrl,
                            category = it.product.category,
                        ),
                    quantity = it.quantity,
                )
            }

        return Cart(items)
    }

    override suspend fun addItem(
        id: Long,
        quantity: Int,
    ): AddItemResult {
        val cartBefore = getCart()
        val existing = cartBefore.items.find { it.product.id == id }

        return try {
            if (existing == null) {
                remoteDataSource.addItem(id = id, quantity = quantity)
                AddItemResult.NewAdded(getCart())
            } else {
                val newQuantity = existing.quantity + quantity
                remoteDataSource.changeQuantity(
                    id = existing.id,
                    quantity = newQuantity,
                )
                AddItemResult.Incremented(getCart())
            }
        } catch (err: CancellationException) {
            throw err
        } catch (err: Exception) {
            AddItemResult.Error("장바구니 담기에 실패했습니다.")
        }
    }

    override suspend fun deleteItem(productId: Long): RemoveItemResult {
        return try {
            val cartContent = getCartContents().find { it.product.id == productId } ?: return RemoveItemResult.NotFoundItem
            remoteDataSource.deleteItem(cartContent.id)
            RemoveItemResult.Success(getCart())
        } catch (err: CancellationException) {
            throw err
        } catch (err: Exception) {
            RemoveItemResult.Error("삭제에 실패했습니다. 다시 시도헤주세요.")
        }
    }

    override suspend fun changeCartItem(
        productId: Long,
        amount: Int,
    ): UpdateItemResult {
        return try {
            val cartContent =
                getCartContents().find { it.product.id == productId }
                    ?: return UpdateItemResult.Error("상품을 찾을 수 없습니다.")
            remoteDataSource.changeQuantity(cartContent.id, amount)
            UpdateItemResult.Success(getCart())
        } catch (err: CancellationException) {
            throw err
        } catch (err: Exception) {
            UpdateItemResult.Error("수량 변경에 실패했습니다.")
        }
    }

    companion object {
        private const val PAGE_SIZE = 50
    }
}
