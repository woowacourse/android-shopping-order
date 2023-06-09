package woowacourse.shopping.ui.cart

import woowacourse.shopping.data.cart.CartItem
import woowacourse.shopping.data.cart.CartItemRepository
import woowacourse.shopping.data.discount.DiscountInfoRepository
import woowacourse.shopping.data.discount.DiscountResult
import woowacourse.shopping.data.order.OrderRepository
import woowacourse.shopping.ui.cart.uistate.CartItemUIState
import woowacourse.shopping.ui.cart.uistate.OrderPriceInfoUIState

class CartPresenter(
    private val view: CartContract.View,
    private val cartItemRepository: CartItemRepository,
    private val orderRepository: OrderRepository,
    private val discountInfoRepository: DiscountInfoRepository,
    private var currentPage: Int = 1,
    private var selectedCartItems: Set<CartItem> = emptySet()
) : CartContract.Presenter {

    init {
        require(currentPage >= 1) { "장바구니 아이템 화면의 페이지는 1 이상이어야 합니다." }
    }

    override fun onLoadCartItemsOfFirstPage() {
        cartItemRepository.getCartItems().thenAccept { result ->
            result.onSuccess { cartItems ->
                currentPage = 1
                setCartItemsOnView(cartItems, currentPage, PAGE_SIZE, true)
                setPageUiOnView(cartItems.size, currentPage, PAGE_SIZE)
                setAllSelectionUiOnView(cartItems, currentPage, PAGE_SIZE)
            }.onFailure {
                view.showMessage("장바구니 아이템을 불러오는 데 실패했습니다.")
            }
        }
    }

    override fun onLoadCartItemsOfLastPage() {
        cartItemRepository.getCartItems().thenAccept { result ->
            result.onSuccess { cartItems ->
                val lastPage = calculateLastPage(cartItems.size, PAGE_SIZE)
                currentPage = lastPage
                setCartItemsOnView(cartItems, currentPage, PAGE_SIZE, true)
                setPageUiOnView(cartItems.size, currentPage, PAGE_SIZE)
                setAllSelectionUiOnView(cartItems, currentPage, PAGE_SIZE)
            }.onFailure {
                view.showMessage("장바구니 아이템을 불러오는 데 실패했습니다.")
            }
        }
    }

    override fun onLoadCartItemsOfPreviousPage() {
        if (currentPage <= 1) {
            view.showMessage("현재 페이지가 1페이지라서 이전 페이지를 요청할 수 없습니다.")
            return
        }

        cartItemRepository.getCartItems().thenAccept { result ->
            result.onSuccess { cartItems ->
                currentPage--
                setCartItemsOnView(cartItems, currentPage, PAGE_SIZE, true)
                setPageUiOnView(cartItems.size, currentPage, PAGE_SIZE)
                setAllSelectionUiOnView(cartItems, currentPage, PAGE_SIZE)
            }.onFailure {
                view.showMessage("장바구니 아이템을 불러오는 데 실패했습니다.")
            }
        }
    }

    override fun onLoadCartItemsOfNextPage() {
        cartItemRepository.getCartItems().thenAccept { result ->
            result.onSuccess { cartItems ->
                val lastPage = calculateLastPage(cartItems.size, PAGE_SIZE)
                currentPage = if (lastPage > currentPage) currentPage + 1 else lastPage
                setCartItemsOnView(cartItems, currentPage, PAGE_SIZE, true)
                setPageUiOnView(cartItems.size, currentPage, PAGE_SIZE)
                setAllSelectionUiOnView(cartItems, currentPage, PAGE_SIZE)
            }.onFailure {
                view.showMessage("장바구니 아이템을 불러오는 데 실패했습니다.")
            }
        }
    }

    override fun onDeleteCartItem(cartItemId: Long) {
        cartItemRepository.deleteCartItem(cartItemId).thenAccept { result ->
            result.onSuccess {
                cartItemRepository.getCartItems().thenAccept { result ->
                    result.onSuccess { cartItems ->
                        val cartItemsOfCurrentPage = cartItems.of(currentPage, PAGE_SIZE)
                        if (cartItemsOfCurrentPage.isEmpty() && currentPage > 1) currentPage--
                        setCartItemsOnView(cartItems, currentPage, PAGE_SIZE, false)
                        setPageUiOnView(cartItems.size, currentPage, PAGE_SIZE)
                        setAllSelectionUiOnView(cartItems, currentPage, PAGE_SIZE)
                        if (selectedCartItems.any { it.id == cartItemId }) {
                            selectedCartItems =
                                selectedCartItems.filter { it.id != cartItemId }.toSet()
                            view.setCanSeeOrderPriceInfo(
                                cartItems.of(currentPage, PAGE_SIZE).any { it in selectedCartItems }
                            )
                            view.setOrderCount(selectedCartItems.size)
                            view.setCanOrder(selectedCartItems.isNotEmpty())
                            val totalPrice = selectedCartItems.sumOf { it.price }
                            discountInfoRepository.getDiscountInfo(totalPrice)
                                .thenAccept { result ->
                                    result.onSuccess { discountInfo ->
                                        val discountedPrice = discountInfo.discountResults
                                            .map(DiscountResult::discountPrice)
                                            .fold(totalPrice) { total, discountPrice ->
                                                total - discountPrice
                                            }.takeIf { it > 0 } ?: 0
                                        view.setOrderPrice(discountedPrice)
                                    }.onFailure {
                                        view.showMessage("주문 금액을 계산할 수 없습니다.")
                                        view.setOrderPrice(-1)
                                    }
                                }
                        }
                    }.onFailure {
                        view.showMessage("장바구니 아이템을 삭제했지만 갱신된 장바구니 아이템들을 불러오는 데 실패했습니다.")
                    }
                }
            }.onFailure {
                view.showMessage("해당 장바구니 아이템을 삭제하는 데 실패했습니다.")
            }
        }
    }

    override fun onChangeSelectionOfCartItem(cartItemId: Long, isSelected: Boolean) {
        cartItemRepository.getCartItems().thenAccept { result ->
            result.onSuccess { cartItems ->
                val cartItem = cartItems.find { it.id == cartItemId }
                if (cartItem == null) {
                    selectedCartItems = selectedCartItems.filter { it.id != cartItemId }.toSet()
                    view.showMessage("해당 장바구니 아이템은 사라졌습니다.")
                    view.refresh()
                    return@thenAccept
                }
                selectedCartItems = if (isSelected) {
                    selectedCartItems + cartItem
                } else {
                    selectedCartItems - cartItem
                }
                setAllSelectionUiOnView(cartItems, currentPage, PAGE_SIZE)
                view.setOrderCount(selectedCartItems.size)
                view.setCanSeeOrderPriceInfo(selectedCartItems.isNotEmpty())
                view.setCanOrder(selectedCartItems.isNotEmpty())
                val totalPrice = selectedCartItems.sumOf { it.price }
                discountInfoRepository.getDiscountInfo(totalPrice).thenAccept { result ->
                    result.onSuccess { discountInfo ->
                        val discountedPrice = discountInfo.discountResults
                            .map(DiscountResult::discountPrice)
                            .fold(totalPrice) { total, discountPrice ->
                                total - discountPrice
                            }.takeIf { it > 0 } ?: 0
                        view.setOrderPrice(discountedPrice)
                    }.onFailure {
                        view.showMessage("주문 금액을 계산할 수 없습니다.")
                        view.setOrderPrice(-1)
                    }
                }
            }.onFailure {
                view.showMessage("장바구니 아이템을 불러오는 데 실패했습니다.")
            }
        }
    }

    override fun onChangeSelectionOfAllCartItems(isSelected: Boolean) {
        cartItemRepository.getCartItems().thenAccept { result ->
            result.onSuccess { cartItems ->
                val cartItemsOfCurrentPage = cartItems.of(currentPage, PAGE_SIZE)
                selectedCartItems = if (isSelected) {
                    selectedCartItems + cartItemsOfCurrentPage
                } else {
                    selectedCartItems - cartItemsOfCurrentPage.toSet()
                }
                val cartItemUIStates = cartItemsOfCurrentPage.map {
                    CartItemUIState.create(it, it in selectedCartItems)
                }
                view.setCartItems(cartItemUIStates, false)
                view.setOrderCount(selectedCartItems.size)
                view.setCanSeeOrderPriceInfo(selectedCartItems.isNotEmpty())
                view.setCanOrder(selectedCartItems.isNotEmpty())
                val totalPrice = selectedCartItems.sumOf { it.price }
                discountInfoRepository.getDiscountInfo(totalPrice).thenAccept { result ->
                    result.onSuccess { discountInfo ->
                        val discountedPrice = discountInfo.calculateDiscountedPrice(totalPrice)
                        view.setOrderPrice(discountedPrice)
                    }.onFailure {
                        view.showMessage("주문 금액을 계산할 수 없습니다.")
                        view.setOrderPrice(-1)
                    }
                }
            }.onFailure {
                view.showMessage("장바구니 아이템을 불러오는 데 실패했습니다.")
            }
        }
    }

    override fun onPlusCount(cartItemId: Long) {
        cartItemRepository.getCartItems().thenAccept { result ->
            result.onSuccess { cartItems ->
                val cartItem = cartItems.find { it.id == cartItemId }
                if (cartItem == null) {
                    view.showMessage("수량을 증가하려는 장바구니 아이템이 존재하지 않습니다.")
                    view.refresh()
                    return@thenAccept
                }
                cartItemRepository.updateCartItemQuantity(cartItem.id, cartItem.quantity + 1)
                    .thenAccept { result ->
                        result.onSuccess {
                            cartItemRepository.getCartItems().thenAccept { result ->
                                result.onSuccess { cartItems ->
                                    val cartItem = cartItems.find { it.id == cartItemId }
                                    if (cartItem == null) {
                                        view.showMessage("수량을 증가한 장바구니 아이템이 사라졌습니다.")
                                        view.refresh()
                                        return@thenAccept
                                    }
                                    selectedCartItems = selectedCartItems.replaced(cartItem)
                                    setCartItemsOnView(cartItems, currentPage, PAGE_SIZE, false)
                                    val totalPrice = selectedCartItems.sumOf { it.price }
                                    discountInfoRepository.getDiscountInfo(totalPrice)
                                        .thenAccept { result ->
                                            result.onSuccess { discountInfo ->
                                                val discountedPrice =
                                                    discountInfo.calculateDiscountedPrice(totalPrice)
                                                view.setOrderPrice(discountedPrice)
                                            }.onFailure {
                                                view.showMessage("주문 금액을 계산할 수 없습니다.")
                                                view.setOrderPrice(-1)
                                            }
                                        }
                                }.onFailure {
                                    view.showMessage("장바구니 아이템을 불러오는 데 실패했습니다.")
                                }
                            }
                        }.onFailure {
                            view.showMessage("해당 장바구니 아이템의 수량을 증가하는 데 실패했습니다.")
                        }
                    }
            }.onFailure {
                view.showMessage("장바구니 아이템을 불러오는 데 실패했습니다.")
            }
        }
    }

    override fun onMinusCount(cartItemId: Long) {
        cartItemRepository.getCartItems().thenAccept { result ->
            result.onSuccess { cartItems ->
                val cartItem = cartItems.find { it.id == cartItemId }
                if (cartItem == null) {
                    view.showMessage("수량을 증가하려는 장바구니 아이템이 존재하지 않습니다.")
                    view.refresh()
                    return@thenAccept
                }
                if (cartItem.quantity <= 1) {
                    return@thenAccept
                }
                cartItemRepository.updateCartItemQuantity(cartItem.id, cartItem.quantity - 1)
                    .thenAccept { result ->
                        result.onSuccess {
                            cartItemRepository.getCartItems().thenAccept { result ->
                                result.onSuccess { cartItems ->
                                    val cartItem = cartItems.find { it.id == cartItemId }
                                    if (cartItem == null) {
                                        view.showMessage("수량을 감소한 장바구니 아이템이 사라졌습니다.")
                                        view.refresh()
                                        return@thenAccept
                                    }
                                    selectedCartItems = selectedCartItems.replaced(cartItem)
                                    setCartItemsOnView(cartItems, currentPage, PAGE_SIZE, false)
                                    val totalPrice = selectedCartItems.sumOf { it.price }
                                    discountInfoRepository.getDiscountInfo(totalPrice)
                                        .thenAccept { result ->
                                            result.onSuccess { discountInfo ->
                                                val discountedPrice =
                                                    discountInfo.calculateDiscountedPrice(totalPrice)
                                                view.setOrderPrice(discountedPrice)
                                            }.onFailure {
                                                view.showMessage("주문 금액을 계산할 수 없습니다.")
                                                view.setOrderPrice(-1)
                                            }
                                        }
                                }.onFailure {
                                    view.showMessage("장바구니 아이템을 불러오는 데 실패했습니다.")
                                }
                            }
                        }.onFailure {
                            view.showMessage("해당 장바구니 아이템의 수량을 감소하는 데 실패했습니다.")
                        }
                    }
            }.onFailure {
                view.showMessage("장바구니 아이템을 불러오는 데 실패했습니다.")
            }
        }
    }

    override fun onOrderSelectedCartItems() {
        orderRepository.createOrder(selectedCartItems.toList()).thenAccept { result ->
            result.onSuccess { orderId ->
                view.showOrderResult(orderId)
                currentPage = 1
                selectedCartItems = emptySet()
                view.setCanSeeOrderPriceInfo(false)
                view.setCanOrder(false)
                view.setOrderPrice(0)
                view.setOrderCount(0)
                cartItemRepository.getCartItems().thenAccept { result ->
                    result.onSuccess { cartItems ->
                        setCartItemsOnView(cartItems, currentPage, PAGE_SIZE, true)
                        setPageUiOnView(cartItems.size, currentPage, PAGE_SIZE)
                        setAllSelectionUiOnView(cartItems, currentPage, PAGE_SIZE)
                    }.onFailure {
                        view.showMessage("장바구니 아이템을 새로고침하는 데 실패했습니다.")
                    }
                }
            }.onFailure {
                view.showMessage("선택한 장바구니 아이템을 주문하는 데 실패했습니다.")
            }
        }
    }

    override fun onLoadOrderPriceInfo() {
        val totalPrice = selectedCartItems.sumOf(CartItem::price)
        discountInfoRepository.getDiscountInfo(totalPrice).thenAccept { result ->
            result.onSuccess { discountInfo ->
                val orderPriceInfoUIState = OrderPriceInfoUIState.create(discountInfo, totalPrice)
                view.showOrderPriceInfo(orderPriceInfoUIState)
            }.onFailure {
                view.showMessage("주문 금액 정보를 불러오는 데 실패했습니다.")
            }
        }
    }

    private fun setCartItemsOnView(
        cartItems: List<CartItem>,
        page: Int,
        pageSize: Int,
        initScroll: Boolean
    ) {
        val cartItemsOfCurrentPage = cartItems.of(page, pageSize)
        val cartItemUIStates = cartItemsOfCurrentPage.map {
            CartItemUIState.create(it, it in selectedCartItems)
        }
        view.setCartItems(cartItemUIStates, initScroll)
    }

    private fun List<CartItem>.of(page: Int, pageSize: Int): List<CartItem> {
        val offset = (page - 1) * pageSize
        return this.slice(offset until this.size).take(pageSize)
    }

    private fun setPageUiOnView(cartItemsSize: Int, page: Int, pageSize: Int) {
        val lastPage = calculateLastPage(cartItemsSize, pageSize)
        view.setPageUIVisibility(lastPage > 1)
        view.setPage(page)
        view.setStateThatCanRequestPreviousPage(page > 1)
        view.setStateThatCanRequestNextPage(page < lastPage)
    }

    private fun setAllSelectionUiOnView(cartItems: List<CartItem>, page: Int, pageSize: Int) {
        val cartItemsOfCurrentPage = cartItems.of(page, pageSize)
        view.setStateOfAllSelection(
            cartItemsOfCurrentPage.all { it in selectedCartItems } && cartItemsOfCurrentPage.isNotEmpty()
        )
    }

    private fun calculateLastPage(cartItemsSize: Int, pageSize: Int): Int =
        (cartItemsSize - 1) / pageSize + 1

    private fun Set<CartItem>.replaced(cartItem: CartItem): Set<CartItem> {
        if (cartItem in this) {
            val result = this - cartItem
            return result + cartItem
        }
        return this
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}
