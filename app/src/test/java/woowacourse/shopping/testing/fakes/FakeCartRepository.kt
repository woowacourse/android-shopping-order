package woowacourse.shopping.testing.fakes

import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.model.CartPage
import woowacourse.shopping.domain.model.PurchaseProduct
import woowacourse.shopping.domain.model.PurchaseProducts

class FakeCartRepository : CartRepository {
    private val _db = mutableListOf<PurchaseProduct>()
    var shouldFail: Boolean = false
    val pageRequests = mutableListOf<PageRequest>()

    override suspend fun insert(purchaseProduct: PurchaseProduct) {
        if (shouldFail) throw Exception("Network Error")
        val existingIndex = _db.indexOfFirst { it.product.id == purchaseProduct.product.id }
        if (existingIndex != -1) {
            val existing = _db[existingIndex]
            _db[existingIndex] = existing.copy(count = existing.count + purchaseProduct.count)
        } else {

            _db.add(purchaseProduct)
        }
    }

    override suspend fun deleteCartItem(purchaseProductId: Long) {
        if (shouldFail) throw Exception("Network Error")
        _db.removeIf { it.id == purchaseProductId }
    }

    override suspend fun updateCount(cartItemId: Long, newQuantity: Int) {
        if (shouldFail) throw Exception("Network Error")
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
        if (shouldFail) throw Exception("Network Error")
        return _db.sumOf { it.count }
    }

    override suspend fun getCartPage(page: Int, size: Int): CartPage {
        if (shouldFail) throw Exception("Network Error")
        pageRequests += PageRequest(page, size)
        val start = page * size
        if (start >= _db.size) {
            return CartPage(
                items = PurchaseProducts(emptyList()),
                isLast = true,
            )
        }
        val end = minOf(start + size, _db.size)
        val items = _db.subList(start, end).toList()
        return CartPage(
            items = PurchaseProducts(items),
            isLast = end >= _db.size || items.size < size,
        )
    }

    override suspend fun getAllCartItems(pageSize: Int): PurchaseProducts {
        require(pageSize > 0) { "페이지 크기는 1 이상이어야 합니다." }

        val allItems = mutableListOf<PurchaseProduct>()
        var page = 0

        while (true) {
            val cartPage = getCartPage(page, pageSize)
            allItems += cartPage.items.purchaseProducts

            if (cartPage.isLast) break
            page++
        }

        return PurchaseProducts(allItems)
    }

    override suspend fun findCartItemByProductId(
        productId: Long,
        pageSize: Int,
    ): PurchaseProduct? {
        require(pageSize > 0) { "페이지 크기는 1 이상이어야 합니다." }

        var page = 0

        while (true) {
            val cartPage = getCartPage(page, pageSize)
            val foundItem = cartPage.items.findById(productId)

            if (foundItem != null) return foundItem
            if (cartPage.isLast) return null

            page++
        }
    }

    data class PageRequest(
        val page: Int,
        val size: Int,
    )
}
