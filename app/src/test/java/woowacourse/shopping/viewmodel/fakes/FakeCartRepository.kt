package woowacourse.shopping.viewmodel.fakes

import woowacourse.shopping.data.remote.server.repository.CartRepository
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.domain.PurchaseProducts

class FakeCartRepository : CartRepository {
    private val _db = mutableListOf<PurchaseProduct>()

    override suspend fun insert(purchaseProduct: PurchaseProduct) {
        val existingIndex = _db.indexOfFirst { it.product.id == purchaseProduct.product.id }
        if (existingIndex != -1) {
            val existing = _db[existingIndex]
            _db[existingIndex] = existing.copy(count = existing.count + purchaseProduct.count)
        } else {

            _db.add(purchaseProduct)
        }
    }

    override suspend fun deleteCartItem(purchaseProductId: Long) {
        _db.removeIf { it.id == purchaseProductId }
    }

    override suspend fun updateCount(cartItemId: Long, newQuantity: Int) {
        val index = _db.indexOfFirst { it.id == cartItemId }
        if (index != -1) {
            if (newQuantity <= 0) {
                _db.removeAt(index)
            } else {
                _db[index] = _db[index].copy(count = newQuantity)
            }
        }
    }

    override suspend fun getProductCount(): Int {
        return _db.sumOf { it.count }
    }

    override suspend fun getPagedCart(page: Int, size: Int): PurchaseProducts {
        val start = page * size
        if (start >= _db.size) return PurchaseProducts(emptyList())
        val end = minOf(start + size, _db.size)
        return PurchaseProducts(_db.subList(start, end))
    }

    override suspend fun getCartItemCount(): Int {
        return _db.size
    }
}
