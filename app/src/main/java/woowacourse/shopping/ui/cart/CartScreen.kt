package woowacourse.shopping.ui.cart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import woowacourse.shopping.data.model.Cart
import woowacourse.shopping.data.model.CartItem
import woowacourse.shopping.data.model.Money
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.model.Products
import woowacourse.shopping.ui.cart.component.CartBody
import woowacourse.shopping.ui.cart.component.CartBottomBar
import woowacourse.shopping.ui.cart.component.CartHeader
import woowacourse.shopping.ui.cart.component.CartScreenSkeleton

@Composable
fun CartScreen(
    viewModel: CartViewModel,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        CartScreen(
            cart = Cart(uiState.pagedItems),
            currentPage = uiState.currentPage,
            totalPages = uiState.totalPages,
            showPagination = uiState.showPagination,
            selectedItemIds = uiState.selectedItemIds,
            onBackClick = onBackClick,
            onDeleteClick = { viewModel.delete(it.product) },
            onPreviousClick = { viewModel.previousPage() },
            onNextClick = { viewModel.nextPage() },
            onAddClick = { viewModel.increase(it.product) },
            onRemoveClick = { viewModel.decrease(it.product) },
            onCheckedChange = { id, isSelected ->
                viewModel.toggleItemSelection(
                    id.id ?: throw IllegalArgumentException(), isSelected
                )
            },
            selectedItemCount = uiState.totalSelectedCount,
            totalPrice = uiState.totalPrice,
            onAllCheckboxChanged = { isSelected -> viewModel.toggleAllItemsSelection(isSelected) },
            checked = uiState.isAllSelected,
            modifier = Modifier,
        )

        if (uiState.isLoading) CartScreenSkeleton()
    }
}

@Composable
fun CartScreen(
    cart: Cart,
    currentPage: Int,
    totalPages: Int,
    showPagination: Boolean,
    selectedItemIds: Set<Long>,
    selectedItemCount: Int,
    totalPrice: Long,
    checked: Boolean,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onDeleteClick: (CartItem) -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onAddClick: (CartItem) -> Unit,
    onRemoveClick: (CartItem) -> Unit,
    onCheckedChange: (CartItem, Boolean) -> Unit,
    onAllCheckboxChanged: (Boolean) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        CartHeader(onBackClick = onBackClick)

        CartBody(
            cart = cart,
            showPagination = showPagination,
            currentPage = currentPage,
            totalPages = totalPages,
            modifier =
                Modifier
                    .padding(top = 8.dp, start = 18.dp, end = 18.dp)
                    .weight(1f),
            onDeleteClick = onDeleteClick,
            onPreviousClick = onPreviousClick,
            onNextClick = onNextClick,
            onAddClick = onAddClick,
            onRemoveClick = onRemoveClick,
            onCheckedChange = onCheckedChange,
            selectedItemIds = selectedItemIds,
        )

        CartBottomBar(
            useCheckbox = true,
            count = selectedItemCount,
            checked = checked,
            price = totalPrice,
            onCheckedChanged = onAllCheckboxChanged,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
@Preview(showBackground = true, name = "상품 5개 넘을 때")
private fun CartScreenPreview1() {
    val products =
        listOf(
            Product(name = "1번", price = Money(1000), imageUrl = ""),
            Product(name = "2번", price = Money(1000), imageUrl = ""),
            Product(name = "3번", price = Money(1000), imageUrl = ""),
            Product(name = "4번", price = Money(1000), imageUrl = ""),
            Product(name = "5번", price = Money(1000), imageUrl = ""),
        )
    val cart = Cart(products.map { CartItem(product = it, quantity = 1) })

    CartScreen(
        cart = cart,
        currentPage = 1,
        totalPages = 2,
        showPagination = true,
        selectedItemIds = emptySet(),
        selectedItemCount = 4,
        totalPrice = 50000,
        checked = true,
        modifier = Modifier,
        onBackClick = {},
        onDeleteClick = {},
        onPreviousClick = {},
        onNextClick = {},
        onAddClick = {},
        onRemoveClick = {},
        onCheckedChange = { _, _ -> },
        onAllCheckboxChanged = {},
    )
}

@Composable
@Preview(showBackground = true, name = "상품 없을 때")
private fun CartScreenPreview2() {
    val cart = Cart(emptyList())

    CartScreen(
        cart = cart,
        currentPage = 1,
        totalPages = 2,
        showPagination = false,
        selectedItemIds = emptySet(),
        selectedItemCount = 0,
        totalPrice = 0,
        checked = false,
        modifier = Modifier,
        onBackClick = {},
        onDeleteClick = {},
        onPreviousClick = {},
        onNextClick = {},
        onAddClick = {},
        onRemoveClick = {},
        onCheckedChange = { _, _ -> },
        onAllCheckboxChanged = {},
    )
}
