package woowacourse.shopping.feature.cart

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.constants.MockData
import woowacourse.shopping.feature.cart.component.CartItemList
import woowacourse.shopping.feature.cart.component.PageNavigator
import woowacourse.shopping.feature.common.component.CommonAppBar
import woowacourse.shopping.feature.common.state.CartItemUiModel
import woowacourse.shopping.feature.common.state.ProductUiModel
import woowacourse.shopping.feature.format.DecimalPriceFormatter

@Composable
fun CartScreen(
    onCloseClick: () -> Unit,
    activityFinish: () -> Unit,
    onToRecommendIntent: (List<CartContentId>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = viewModel(factory = CartViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is CartEvent.FatalError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    activityFinish()
                }
                is CartEvent.RemoveEvent -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is CartEvent.MinusEvent -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    CartScreenContent(
        uiState = uiState,
        onToRecommendIntent = {
            onToRecommendIntent(
                uiState.checkMap.entries.filter { it.value }.map {
                    CartContentId(it.key)
                },
            )
        },
        onCloseClick = onCloseClick,
        onDelete = viewModel::deleteCartItem,
        onIncrease = viewModel::increase,
        onDecrease = viewModel::decrease,
        onPrev = viewModel::moveToPreviousPage,
        onNext = viewModel::moveToNextPage,
        canPrev = !uiState.isFirstPage,
        canNext = !uiState.isLastPage,
        onChecked = viewModel::cartItemCheck,
        onTotalCheck = viewModel::totalCheck,
        modifier = modifier,
    )
}

@Composable
fun CartScreenContent(
    uiState: CartUiState,
    onToRecommendIntent: () -> Unit,
    onChecked: (Long) -> Unit,
    onTotalCheck: () -> Unit,
    onCloseClick: () -> Unit,
    onDelete: (Long) -> Unit,
    onIncrease: (Long) -> Unit,
    onDecrease: (Long) -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    canPrev: Boolean,
    canNext: Boolean,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        containerColor = Color.White,
        modifier =
            modifier
                .fillMaxSize(),
        topBar = {
            CommonAppBar(
                title = "Cart",
                onCloseClick = onCloseClick
            )
        },
    ) { innerPadding ->
        Box {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(innerPadding),
            ) {
                CartItemList(
                    checkMap = uiState.checkMap,
                    isLoading = uiState.isLoading,
                    cartContents = uiState.paginatedCartContents,
                    modifier = Modifier.weight(1f),
                    onDelete = onDelete,
                    onIncrease = onIncrease,
                    onDecrease = onDecrease,
                    onChecked = onChecked,
                )
                Spacer(modifier = Modifier.height(10.dp))
                PageNavigator(
                    page = uiState.page,
                    onLeftClick = onPrev,
                    onRightClick = onNext,
                    canMoveToPreviousPage = canPrev,
                    canMoveToNextPage = canNext,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(78.dp)
                            .background(Color(0xff555555)),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(horizontal = 20.dp),
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Checkbox(
                                checked = uiState.checkMap.all { it.value },
                                onCheckedChange = {
                                    onTotalCheck()
                                },
                                colors =
                                    CheckboxDefaults.colors().copy(
                                        checkedBoxColor = Color(0xFF04C09E),
                                    ),
                                modifier = Modifier.size(24.dp),
                            )
                            Text("전체", color = Color.White, fontSize = 12.sp)
                        }
                        Text(
                            DecimalPriceFormatter().format(uiState.totalPrice),
                            fontWeight = FontWeight.W700,
                            fontSize = 18.sp,
                            color = Color.White,
                        )
                    }
                    TextButton(
                        onClick = {
                            onToRecommendIntent()
                        },
                        modifier =
                            Modifier
                                .width(122.dp)
                                .fillMaxHeight()
                                .background(Color(0xff04C09E)),
                    ) {
                        Text(
                            "주문하기(${uiState.totalCount})",
                            fontWeight = FontWeight.W700,
                            fontSize = 18.sp,
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun CartScreenContentPreview() {
    CartScreenContent(
        uiState =
            CartUiState(
                isLoading = false,
                paginatedCartContents =
                    MockData.MOCK_PRODUCTS.take(2).map {
                        CartItemUiModel(
                            contentId = 1,
                            ProductUiModel(
                                name = it.name,
                                price = it.priceAmount(),
                                imageUrl = it.imageUrl,
                                id = it.id,
                                quantity = 1,
                            ),
                        )
                    },
            ),
        onDelete = {},
        onIncrease = {},
        onDecrease = {},
        onPrev = {},
        onNext = {},
        canPrev = true,
        canNext = true,
        onCloseClick = {},
        onChecked = { _ -> },
        onTotalCheck = {},
        onToRecommendIntent = {},
    )
}
