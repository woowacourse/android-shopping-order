package woowacourse.shopping.feature.cart

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.data.goods.repository.GoodsRepository
import woowacourse.shopping.data.util.mapper.toCartItems
import woowacourse.shopping.data.util.mapper.toDomain
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData
import kotlin.Int
import kotlin.collections.Map
import kotlin.math.min

@Suppress("ktlint:standard:backing-property-naming")
class CartViewModel(
    private val cartRepository: CartRepository,
    private val goodsRepository: GoodsRepository,
) : ViewModel() {
    @VisibleForTesting
    internal fun setTestPage(page: Int) {
        _page.value = page
    }

    private val _page = MutableLiveData(MINIMUM_PAGE)
    val page: LiveData<Int> get() = _page

    @VisibleForTesting
    internal fun setTestCarts(items: List<CartItem>) {
        _carts.value = items.associateBy { it.id }
    }

    private val _carts = MutableLiveData<Map<Int, CartItem>>(emptyMap())

    private val _recommendedGoods = MutableLiveData<List<CartItem>>()
    val recommendedGoods: LiveData<List<CartItem>> = _recommendedGoods

    val cartsList: LiveData<List<CartItem>> =
        _carts.map { map ->
            map.values.toList()
        }

    val isMultiplePages: LiveData<Boolean> =
        cartsList.map { cartList ->
            cartList.size > PAGE_SIZE
        }

    val currentPageCarts: LiveData<List<CartItem>> =
        MediatorLiveData<List<CartItem>>().apply {
            fun update() {
                val allItems = cartsList.value ?: emptyList()
                val currentPageIndex = (_page.value ?: 1) - 1
                val startIndex = currentPageIndex * PAGE_SIZE
                val endIndex = min(startIndex + PAGE_SIZE, allItems.size)

                value =
                    if (startIndex < allItems.size) {
                        allItems.subList(startIndex, endIndex)
                    } else {
                        emptyList()
                    }
            }
            addSource(cartsList) { update() }
            addSource(_page) { update() }
        }

    val isLeftPageEnable: LiveData<Boolean> =
        _page.map { currentPage ->
            currentPage > 1
        }

    val isRightPageEnable: LiveData<Boolean> =
        MediatorLiveData<Boolean>().apply {
            fun update() {
                val entireSize = cartsList.value?.size ?: 0
                val currentPage = _page.value ?: MINIMUM_PAGE
                value = (PAGE_SIZE * currentPage) < entireSize
            }

            addSource(cartsList) { update() }
            addSource(_page) { update() }
        }

    val isAllSelected: LiveData<Boolean> =
        cartsList.map { cartList ->
            cartList.isNotEmpty() && cartList.all { it.isSelected }
        }

    val totalPrice: LiveData<Int> =
        cartsList.map { cartList ->
            cartList.filter { it.isSelected }.sumOf { it.totalPrice }
        }

    val selectedItemCount: LiveData<Int> =
        cartsList.map { cartList ->
            cartList.filter { it.isSelected }.sumOf { it.quantity }
        }

    private val _loginErrorEvent = MutableSingleLiveData<CartFetchError>()
    val loginErrorEvent: SingleLiveData<CartFetchError> get() = _loginErrorEvent

    private val _removeItemEvent = MutableSingleLiveData<CartItem>()
    val removeItemEvent: SingleLiveData<CartItem> get() = _removeItemEvent

    init {
        updateWholeCarts()
    }

    fun updateWholeCarts() {
        cartRepository.fetchAllCartItems({ cartResponse ->
            val allItems: List<CartItem> = cartResponse.toCartItems()
            _carts.value = allItems.associateBy { it.id }.toMutableMap()
        }, { })
    }

    fun loadRecommendedGoods() {
        goodsRepository.fetchMostRecentGoods { goods ->
            if (goods != null) {
                goodsRepository.fetchCategoryGoods(
                    10,
                    goods.category,
                    { goodsResponse ->
                        val categoryGoods = goodsResponse.content.map { CartItem(it.toDomain(), 0) }

                        filterAndSetRecommendedGoods(categoryGoods)
                    },
                    {
                        loadDefaultRecommendedGoods()
                    },
                )
            } else {
                loadDefaultRecommendedGoods()
            }
        }
    }

    private fun filterAndSetRecommendedGoods(
        recommendGoodsList: List<CartItem>,
        pageOffset: Int = 0,
    ) {
        cartRepository.fetchAllCartItems(
            { cartResponse: CartResponse ->
                val cartItems = cartResponse.toCartItems()
                val cartGoodsIds = cartItems.map { it.goods.id }.toSet()

                val cartFilteredGoodsList =
                    recommendGoodsList.filter { recommendItem ->
                        !cartGoodsIds.contains(recommendItem.goods.id)
                    }

                if (cartFilteredGoodsList.isNotEmpty()) {
                    _recommendedGoods.value = cartFilteredGoodsList
                } else {
                    loadDefaultRecommendedGoods(pageOffset + 1)
                }
            },
            {
                _recommendedGoods.value = recommendGoodsList
            },
        )
    }

    private fun loadDefaultRecommendedGoods(pageOffset: Int = 0) {
        goodsRepository.fetchPageGoods(
            10,
            pageOffset * 10,
            { response ->
                val allGoodsList = response.content.map { CartItem(it.toDomain(), 0) }

                filterAndSetRecommendedGoods(allGoodsList, pageOffset)
            },
            {
                _recommendedGoods.value = emptyList()
            },
        )
    }

    fun addCartItemOrIncreaseQuantityFromRecommend(cartItem: CartItem) {
        when {
            _carts.value?.contains(cartItem.id) == true -> {
                val currentList = _recommendedGoods.value ?: return
                val updatedList =
                    currentList.map { item ->
                        if (item.goods.id == cartItem.goods.id) {
                            item.copy(quantity = item.quantity + 1)
                        } else {
                            item
                        }
                    }
                _recommendedGoods.value = updatedList
                increaseQuantity(cartItem)
            }
            else -> {
                cartRepository.addCartItem(cartItem.goods, 1, { resultCode: Int, cartId: Int ->
                    val currentList = _recommendedGoods.value ?: return@addCartItem
                    val newItem = cartItem.copy(quantity = cartItem.quantity + 1, id = cartId, isSelected = true)
                    val updatedList =
                        currentList.map { item ->
                            if (item.goods.id == cartItem.goods.id) {
                                newItem
                            } else {
                                item
                            }
                        }
                    _recommendedGoods.value = updatedList
                    addLocalCartItem(newItem)
                }, { })
            }
        }
    }

    private fun addLocalCartItem(cartItem: CartItem) {
        val addedItem: List<CartItem> = listOf(cartItem) + (cartsList.value ?: emptyList())
        _carts.value = addedItem.associateBy { it.id }.toMutableMap()
        _page.value = 1
    }

    private fun updateCartItem(
        itemId: Int,
        updater: (CartItem) -> CartItem,
    ) {
        val currentMap = _carts.value ?: return
        val newMap = currentMap.toMutableMap()

        currentMap[itemId]?.let { existingItem ->
            newMap[itemId] = updater(existingItem)
        }

        _carts.value = newMap
    }

    fun toggleCartItemCheck(cartItem: CartItem) {
        updateCartItem(cartItem.id) { item ->
            item.copy(isSelected = !item.isSelected)
        }
    }

    fun getPosition(cartItem: CartItem): Int? = currentPageCarts.value?.indexOf(cartItem)?.takeIf { it >= 0 }

    fun increaseQuantity(cartItem: CartItem) {
        cartRepository.updateQuantity(cartItem.id, CartQuantity(cartItem.quantity + 1), {
            updateCartItem(cartItem.id) { item ->
                item.copy(quantity = item.quantity + 1)
            }
        }, {})
    }

    fun removeCartItemOrDecreaseQuantityFromRecommend(cartItem: CartItem) {
        val currentList = _recommendedGoods.value ?: return
        val updatedList =
            currentList.map { item ->
                if (item.goods.id == cartItem.goods.id && item.quantity >= 1) {
                    item.copy(quantity = item.quantity - 1)
                } else {
                    item
                }
            }
        _recommendedGoods.value = updatedList
        removeCartItemOrDecreaseQuantity(cartItem)
    }

    fun removeCartItemOrDecreaseQuantity(cartItem: CartItem) {
        if (cartItem.quantity - 1 == 0) return
        cartRepository.updateQuantity(cartItem.id, CartQuantity(cartItem.quantity - 1), {
            updateCartItem(cartItem.id) { item ->
                item.copy(quantity = (item.quantity - 1))
            }
        }, {})
    }

    fun delete(cartItem: CartItem) {
        cartRepository.delete(
            cartItem.id,
            {
                val currentMap = _carts.value ?: return@delete
                val newMap = currentMap.toMutableMap()
                newMap.remove(cartItem.id)
                _carts.value = newMap

                val newEndPage = maxOf((newMap.size + PAGE_SIZE - 1) / PAGE_SIZE, 1)
                val currentPage = _page.value ?: MINIMUM_PAGE
                if (currentPage > newEndPage) {
                    _page.value = newEndPage
                }
            },
            {},
        )
    }

    fun plusPage() {
        _page.value = _page.value?.plus(1)
    }

    fun minusPage() {
        _page.value = _page.value?.minus(1)
    }

    fun selectAllItems() {
        val currentMap = _carts.value ?: return
        val newAllValue = isAllSelected.value != true

        val newMap =
            currentMap.mapValues { (_, item) ->
                item.copy(isSelected = newAllValue)
            }

        _carts.value = newMap
    }

    companion object {
        private const val MINIMUM_PAGE = 1
        private const val PAGE_SIZE = 5
    }
}
