package woowacourse.shopping.fixture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import woowacourse.shopping.data.local.cart.repository.LocalCartRepository
import woowacourse.shopping.domain.model.CartProduct

class FakeCartRepository : LocalCartRepository {
    private val cartList = mutableListOf<CartProduct>()
    private val cartLiveData = MutableLiveData<List<CartProduct>>()
    private var sizeLiveData: Int = 0
    var savedCart: CartProduct? = null

    init {
        updateLiveData()
    }

    private fun updateLiveData() {
        cartLiveData.value = cartList.toList()
        sizeLiveData = cartList.size
    }

    override fun insert(cart: CartProduct) {
        cartList.add(cart)
        savedCart = cart
        updateLiveData()
    }

    override fun insertAll(cart: CartProduct) {
        cartList.add(cart)
        savedCart = cart
        updateLiveData()
    }

    override fun delete(cart: CartProduct) {
        cartList.removeIf { it.goods.id == cart.goods.id }
        updateLiveData()
    }

    override fun deleteAll(cart: CartProduct) {
        TODO("Not yet implemented")
    }

    override fun getAll(callback: (Carts) -> Unit) {
        val totalQuantity = cartList.sumOf { it.quantity }
        callback(Carts(cartList.toList(), totalQuantity))
    }

    override fun getPage(
        limit: Int,
        offset: Int,
    ): LiveData<Carts> {
        val page = cartList.drop(offset).take(limit)
        val totalQuantity = cartList.sumOf { it.quantity }
        return MutableLiveData(Carts(page, totalQuantity))
    }

    override fun getAllItemsSize(callback: (Int) -> Unit) {
        callback(sizeLiveData)
    }

    override fun getTotalQuantity(callback: (Int) -> Unit) {
        val total = cartList.sumOf { it.quantity }
        callback(total)
    }
}
