package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.remote.CartRemoteDataSource
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
    private suspend fun getCartContents(): List<CartContent> = remoteDataSource.getCartItems(0, 10000)

    override suspend fun getCart(): Cart {
        val cartContents = getCartContents()

        val items =
            cartContents.map {
                CartItem(
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
                    it.quantity,
                )
            }

        return Cart(items)
    }

    override suspend fun addItem(
        id: Long,
        quantity: Int,
    ): AddItemResult {
        val cartBefore = getCart()
        val isAlreadyInCart = cartBefore.items.any { it.product.id == id }

        return try {
            remoteDataSource.addItem(
                id = id,
                quantity = quantity,
            )
            val updatedCart = getCart()
            if (isAlreadyInCart) {
                AddItemResult.Incremented(updatedCart)
            } else {
                AddItemResult.NewAdded(updatedCart)
            }
        } catch (err: Exception) {
            AddItemResult.Error("장바구니 담기에 실패했습니다.")
        }
    }

    override suspend fun deleteItem(productId: Long): RemoveItemResult {
        val cartContent = getCartContents().find { it.product.id == productId } ?: return RemoveItemResult.NotFoundItem
        return try {
            remoteDataSource.deleteItem(cartContent.id)
            RemoveItemResult.Success(getCart())
        } catch (err: Exception) {
            RemoveItemResult.Error("삭제에 실패했습니다. 다시 시도헤주세요.")
        }
    }

    override suspend fun changeCartItem(
        productId: Long,
        amount: Int,
    ): UpdateItemResult {
        val cartContent =
            getCartContents().find { it.product.id == productId }
                ?: return UpdateItemResult.Error("상품을 찾을 수 없습니다.")

        return try {
            remoteDataSource.changeQuantity(cartContent.id, amount)
            UpdateItemResult.Success(getCart())
        } catch (err: Exception) {
            UpdateItemResult.Error("수량 변경에 실패했습니다.")
        }
    }
}
