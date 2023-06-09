package woowacourse.shopping.data.repository

import woowacourse.shopping.data.dataSource.CartDataSource
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.model.CartProducts
import woowacourse.shopping.repository.CartRepository

class CartRepositoryImpl(
    private val remoteDatabase: CartDataSource,
) : CartRepository {
    private val cartItems = CartProducts(emptyList())

    override fun getPage(
        index: Int,
        size: Int,
        onSuccess: (CartProducts) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        remoteDatabase.getAll({ cartDtos ->
            cartItems.replaceAll(cartDtos.map { it.toDomain() })
            onSuccess(cartItems)
        }, onFailure)
    }

    override fun getAll(
        onSuccess: (CartProducts) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        remoteDatabase.getAll({ cartDtos ->
            cartItems.replaceAll(cartDtos.map { it.toDomain() })
            onSuccess(CartProducts(cartDtos.map { it.toDomain() }))
        }, onFailure)
    }

    override fun remove(
        id: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        remoteDatabase.deleteItem(
            id,
            onSuccess,
            onFailure,
        )
    }

    override fun updateCount(
        id: Int,
        count: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        if (cartItems.isEmpty()) {
            remoteDatabase.getAll(
                { cartDtos ->
                    cartItems.replaceAll(cartDtos.map { it.toDomain() })
                },
                onFailure,
            )
        }

        val cartItem = cartItems.findByProductId(id)
        when {
            cartItem == null && count == 1 -> {
                remoteDatabase.postItem(id, {
                    getAll({}, {})
                    onSuccess()
                }, onFailure)
            }

            cartItem == null -> {
                return
            }

            count == 0 ->
                remoteDatabase.deleteItem(cartItem.id, {
                    getAll({}, {})
                    onSuccess()
                }, onFailure)

            count < 1 -> {
                return
            }

            else ->
                remoteDatabase.patchItemQuantity(cartItem.id, count, {
                    getAll({}, {})
                    onSuccess()
                }, onFailure)
        }
    }

    override fun hasNextPage(index: Int, size: Int): Boolean {
        return index < cartItems.size / size
    }

    override fun hasPrevPage(index: Int, size: Int): Boolean {
        return index > 0
    }

    override fun getTotalCount(): Int {
        return cartItems.totalQuantity
    }

    override fun getTotalSelectedCount(): Int {
        return cartItems.totalCheckedQuantity
    }

    override fun getTotalPrice(): Int {
        return cartItems.totalCheckedPrice
    }

    override fun getCheckedIds(): List<Int> {
        return cartItems.checkedIds
    }

    override fun insert(productId: Int) {
        if (cartItems.findByProductId(productId) != null) {
            return
        }
        remoteDatabase.postItem(productId, {
            getAll({}, {})
        }, {})
    }

    override fun updateChecked(id: Int, checked: Boolean) {
        cartItems.changeChecked(id, checked)
    }
}
