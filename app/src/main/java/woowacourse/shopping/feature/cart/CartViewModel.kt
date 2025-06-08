package woowacourse.shopping.feature.cart

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.carts.AddItemResult
import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.CartFetchResult
import woowacourse.shopping.data.carts.CartUpdateResult
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.data.goods.repository.GoodsRepository
import woowacourse.shopping.data.payment.CouponFetchResult
import woowacourse.shopping.data.payment.repository.PaymentRepository
import woowacourse.shopping.data.util.mapper.toCartItems
import woowacourse.shopping.data.util.mapper.toCouponItems
import woowacourse.shopping.data.util.mapper.toDomain
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponService
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData
import kotlin.Int
import kotlin.collections.Map
import kotlin.math.min

@Suppress("ktlint:standard:backing-property-naming")
class CartViewModel(
    private val cartRepository: CartRepository,
    private val goodsRepository: GoodsRepository,
    private val paymentRepository: PaymentRepository,
    private val couponService: CouponService,
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

    val selectedCartsList: LiveData<List<CartItem>> =
        _carts.map { map ->
            map.values
                .filter {
                    it.isSelected
                }.toList()
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

    private val _appBarTitle = MutableLiveData<String>()
    val appBarTitle: LiveData<String> = _appBarTitle

    private val _coupons = MutableLiveData<List<Coupon>>()

    val validCoupons: LiveData<List<Coupon>> =
        MediatorLiveData<List<Coupon>>().apply {
            fun update() {
                val coupons = _coupons.value ?: emptyList()
                val selectedCarts = selectedCartsList.value ?: emptyList()
                value =
                    coupons.filter { coupon ->
                        couponService.isValid(coupon, selectedCarts)
                    }
            }

            addSource(_coupons) { update() }
            addSource(selectedCartsList) { update() }
        }

    val selectedCoupon: LiveData<Coupon?> =
        MediatorLiveData<Coupon?>().apply {
            fun update() {
                val coupons = validCoupons.value ?: emptyList()
                value = coupons.find { it.isSelected }
            }
            addSource(validCoupons) { update() }
        }

    val discountAmount: LiveData<Int> =
        MediatorLiveData<Int>().apply {
            fun update() {
                val coupon = selectedCoupon.value
                value = -getDiscountAmount(coupon)
            }
            addSource(selectedCoupon) { update() }
            addSource(selectedCartsList) { update() }
            addSource(totalPrice) { update() }
        }

    private fun getDiscountAmount(coupon: Coupon?): Int =
        when (coupon) {
            is Coupon.BonusGoods -> {
                val discountedAmount =
                    coupon.calculateBonusGoods.getDiscountedPrice(
                        selectedCartsList.value ?: emptyList(),
                    )
                discountedAmount.discountPrice
            }
            is Coupon.Fixed -> coupon.discountedAmount.discountPrice
            is Coupon.FreeShipping -> 0
            is Coupon.Percentage -> coupon.discountedAmount.rateDiscountAmount(totalPrice.value ?: 0)
            null -> 0
        }

    init {
        updateWholeCarts()
    }

    private fun updateWholeCarts() {
        viewModelScope.launch {
            when (val result = cartRepository.fetchAllCartItems()) {
                is CartFetchResult.Success -> {
                    val allItems = result.data.toCartItems()
                    _carts.value = allItems.associateBy { it.id }
                }

                is CartFetchResult.Error -> { // todo 에러 구현 필요
                }
            }
        }
    }

    fun updateWholeCoupons() {
        viewModelScope.launch {
            when (val result = paymentRepository.fetchAllCoupons()) {
                is CouponFetchResult.Success -> {
                    val allItems = result.data.toCouponItems()
                    _coupons.value = allItems
                }

                is CouponFetchResult.Error -> { // todo 에러 구현 필요
                }
            }
        }
    }

    fun toggleCouponCheck(targetCoupon: Coupon) {
        val currentCoupons = _coupons.value ?: return

        val newCoupons =
            currentCoupons.map { coupon ->
                createCouponWithSelection(
                    coupon = coupon,
                    isSelected = coupon.id == targetCoupon.id && !targetCoupon.isSelected,
                )
            }
        _coupons.value = newCoupons
    }

    private fun createCouponWithSelection(
        coupon: Coupon,
        isSelected: Boolean,
    ): Coupon =
        when (coupon) {
            is Coupon.Fixed -> coupon.copy().apply { this.isSelected = isSelected }
            is Coupon.BonusGoods -> coupon.copy().apply { this.isSelected = isSelected }
            is Coupon.FreeShipping -> coupon.copy().apply { this.isSelected = isSelected }
            is Coupon.Percentage -> coupon.copy().apply { this.isSelected = isSelected }
        }

    fun updateAppBarTitle(newTitle: String) {
        _appBarTitle.value = newTitle
    }

    fun loadRecommendedGoods() {
        viewModelScope.launch {
            val goods = goodsRepository.fetchMostRecentGoods()
            if (goods != null) {
                viewModelScope.launch {
                    try {
                        val goodsResponse = goodsRepository.fetchCategoryGoods(10, goods.category)
                        val categoryGoods = goodsResponse.content.map { CartItem(it.toDomain(), 0) }
                        filterAndSetRecommendedGoods(categoryGoods)
                    } catch (e: Exception) {
                        loadDefaultRecommendedGoods()
                    }
                }
            } else {
                loadDefaultRecommendedGoods()
            }
        }
    }

    private fun filterAndSetRecommendedGoods(
        recommendGoodsList: List<CartItem>,
        pageOffset: Int = 0,
    ) {
        viewModelScope.launch {
            when (val result = cartRepository.fetchAllCartItems()) {
                is CartFetchResult.Success -> {
                    val cartItems = result.data.toCartItems()
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
                }

                is CartFetchResult.Error -> {
                    // todo 에러 처리 필요
                }
            }
        }
    }

    private fun loadDefaultRecommendedGoods(pageOffset: Int = 0) {
        viewModelScope.launch {
            try {
                val goodsResponse = goodsRepository.fetchPageGoods(10, pageOffset * 10)
                val allGoodsList = goodsResponse.content.map { CartItem(it.toDomain(), 0) }
                filterAndSetRecommendedGoods(allGoodsList, pageOffset)
            } catch (e: Exception) {
                _recommendedGoods.value = emptyList()
            }
        }
    }

    fun addCartItemOrIncreaseQuantityFromRecommend(cartItem: CartItem) {
        when {
            _carts.value?.contains(cartItem.id) == true -> {
                updateRecommendItem(cartItem.copy(quantity = cartItem.quantity + 1))
                increaseCartItemQuantity(cartItem)
            }

            else -> {
                addCartItemFromRecommend(cartItem)
            }
        }
    }

    private fun addCartItemFromRecommend(cartItem: CartItem) {
        viewModelScope.launch {
            val result = cartRepository.addCartItem(cartItem.goods, quantity = 1)
            when (result) {
                is CartFetchResult.Error -> TODO()
                is CartFetchResult.Success -> {
                    addRecommendItemToLocalVariables(cartItem, result)
                }
            }
        }
    }

    private fun addRecommendItemToLocalVariables(
        cartItem: CartItem,
        result: CartFetchResult.Success<AddItemResult>,
    ) {
        val newItem =
            cartItem.copy(
                quantity = cartItem.quantity + 1,
                id = result.data.cartId,
                isSelected = true,
            )

        updateRecommendItem(newItem)
        addLocalCartItem(newItem)
    }

    private fun updateRecommendItem(newRecommendItem: CartItem) {
        val currentList = _recommendedGoods.value ?: return
        val updatedList =
            currentList.map { item ->
                if (item.goods.id == newRecommendItem.goods.id) {
                    newRecommendItem
                } else {
                    item
                }
            }
        _recommendedGoods.value = updatedList
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

    fun increaseCartItemQuantity(cartItem: CartItem) {
        viewModelScope.launch {
            val result =
                cartRepository.updateQuantity(cartItem.id, CartQuantity(cartItem.quantity + 1))
            when (result) {
                is CartUpdateResult.Success -> {
                    updateCartItem(cartItem.id) { item ->
                        item.copy(quantity = item.quantity + 1)
                    }
                }

                is CartUpdateResult.Error -> TODO()
            }
        }
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
        if (cartItem.quantity == 1) {
            delete(cartItem)
        } else {
            decreaseCartQuantity(cartItem)
        }
    }

    fun delete(cartItem: CartItem) {
        viewModelScope.launch {
            when (cartRepository.delete(cartItem.id)) {
                is CartFetchResult.Success -> {
                    val currentMap = _carts.value ?: return@launch
                    val newMap = currentMap.toMutableMap()
                    newMap.remove(cartItem.id)
                    _carts.value = newMap

                    val newEndPage = maxOf((newMap.size + PAGE_SIZE - 1) / PAGE_SIZE, 1)
                    val currentPage = _page.value ?: MINIMUM_PAGE
                    if (currentPage > newEndPage) {
                        _page.value = newEndPage
                    }
                }

                is CartFetchResult.Error -> TODO()
            }
        }
    }

    private fun decreaseCartQuantity(cartItem: CartItem) {
        viewModelScope.launch {
            val result =
                cartRepository.updateQuantity(cartItem.id, CartQuantity(cartItem.quantity - 1))
            when (result) {
                is CartUpdateResult.Success -> {
                    updateCartItem(cartItem.id) { item ->
                        item.copy(quantity = (item.quantity - 1))
                    }
                }

                is CartUpdateResult.Error -> TODO()
            }
        }
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
