package woowacourse.shopping.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import woowacourse.shopping.R
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.domain.PurchaseProducts
import woowacourse.shopping.ui.common.layout.CommonFrame
import woowacourse.shopping.ui.cart.component.CartBottomBar
import woowacourse.shopping.ui.cart.component.CartItem

@Composable
fun CartScreen(
    cart: PurchaseProducts,
    currentPage: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit,
    onAdd: (Long, Int) -> Unit,
    onMinus: (Long, Int) -> Unit,
    onDelete: (Long) -> Unit,
    isPageable: Boolean,
    previousEnable: Boolean,
    nextEnable: Boolean,
    isLoading: Boolean,
    onCheckedChanged: (Long) -> Unit,
    totalPrice: Int,
    totalCount: Int,
    isChecked: (Long) -> Boolean,
    onSelectAllClick: () -> Unit,
    onOrderClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        CommonFrame(
            headerContent = { CartHeader(onClose) },
            bodyContent = {
                CartBody(
                    cart = cart,
                    onAdd = onAdd,
                    onMinus = onMinus,
                    onDelete = onDelete,
                    currentPage = currentPage,
                    onPrevious = onPrevious,
                    isPageable = isPageable,
                    previousEnable = previousEnable,
                    nextEnable = nextEnable,
                    onNext = onNext,
                    isLoading = isLoading,
                    isChecked = isChecked,
                    onCheckedChanged = onCheckedChanged
                )
            },
            modifier = Modifier.weight(1f),
        )
        CartBottomBar(
            totalPrice = totalPrice,
            totalCount = totalCount,
            onOrderClick = onOrderClick,
            showSelectAll = true,
            onSelectAllClick = onSelectAllClick
        )
    }
}

@Composable
private fun CartHeader(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_left),
            contentDescription = "뒤로가기 버튼",
            modifier =
                Modifier
                    .size(40.dp)
                    .clickable(onClick = onClose),
            tint = Color.White,
        )
        Spacer(Modifier.padding(12.dp))
        Text(
            text = "Cart",
            fontWeight = FontWeight(500),
            fontSize = 20.sp,
            color = Color.White,
        )
    }
}

@Composable
private fun CartBody(
    cart: PurchaseProducts,
    currentPage: Int,
    onAdd: (Long, Int) -> Unit,
    onMinus: (Long, Int) -> Unit,
    onDelete: (Long) -> Unit,
    isPageable: Boolean,
    previousEnable: Boolean,
    nextEnable: Boolean,
    isLoading: Boolean,
    onPrevious: () -> Unit = {},
    onNext: () -> Unit = {},
    onCheckedChanged: (Long) -> Unit,
    isChecked: (Long) -> Boolean,
    modifier: Modifier = Modifier,

    ) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(
                    state = rememberScrollState(),
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isLoading) {
            (1..5).forEach { _ ->
                CartLoadingScreen()
            }
        } else {
            val products = cart.purchaseProducts
            products.forEach {
                CartItem(
                    product = it,
                    onAdd = onAdd,
                    onMinus = onMinus,
                    onDelete = onDelete,
                    onCheckedChanged = onCheckedChanged,
                    isChecked = isChecked(it.id),
                    modifier = Modifier.padding(top = 24.dp),
                )
            }
        }
        if (isPageable) {
            PagingBtn(
                currentPage = currentPage,
                onPrevious = onPrevious,
                previousEnable = previousEnable,
                nextEnable = nextEnable,
                onNext = onNext,
            )
        }
    }
}

@Composable
fun PagingBtn(
    currentPage: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    previousEnable: Boolean,
    nextEnable: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .padding(vertical = 30.dp)
                .width(129.dp)
                .height(42.dp)
                .clip(
                    RoundedCornerShape(4.dp),
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "<",
            fontSize = 22.sp,
            fontWeight = FontWeight(500),
            color = Color.White,
            modifier =
                Modifier
                    .background(color = btnAvailable(previousEnable))
                    .fillMaxHeight()
                    .width(42.dp)
                    .clickable(
                        onClick = onPrevious,
                        enabled = previousEnable,
                    )
                    .wrapContentSize(Alignment.Center),
        )
        Text(
            text = (currentPage + 1).toString(),
            fontSize = 22.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF555555),
            modifier =
                Modifier
                    .fillMaxHeight()
                    .width(42.dp)
                    .wrapContentSize(Alignment.Center),
        )
        Text(
            text = ">",
            fontSize = 22.sp,
            fontWeight = FontWeight(500),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .fillMaxHeight()
                    .width(42.dp)
                    .clickable(
                        onClick = onNext,
                        enabled = nextEnable,
                    )
                    .background(color = btnAvailable(nextEnable))
                    .wrapContentHeight(Alignment.CenterVertically),
        )
    }
}

private fun btnAvailable(btnFlag: Boolean): Color = if (btnFlag) Color(0xFF04C09E) else Color(0xFFAAAAAA)

@Preview
@Composable
private fun CartScreenPreview() {
    CartScreen(
        currentPage = 0,
        onClose = {},
        onAdd = { id, type -> },
        onMinus = { id, type -> },
        onDelete = {},
        onPrevious = {},
        onNext = {},
        previousEnable = false,
        nextEnable = false,
        cart =
            PurchaseProducts(
                listOf(
                    PurchaseProduct(
                        product = Product(
                            imageUri = "uri",
                            name = "무엘사",
                            price = 10000000,
                            category = "asd",
                            id = 1L,
                        ),
                        id = 1L,
                        count = 1,
                    ),
                    PurchaseProduct(
                        product = Product(
                            imageUri = "uri",
                            name = "무엘사",
                            price = 10000000,
                            category = "asd",
                            id = 1L,
                        ),
                        id = 1L,
                        count = 1,
                    ),
                    PurchaseProduct(
                        product = Product(
                            imageUri = "uri",
                            name = "무엘사",
                            price = 10000000,
                            category = "asd",
                            id = 1L,
                        ),
                        id = 1L,
                        count = 1,
                    ),
                    PurchaseProduct(
                        product = Product(
                            imageUri = "uri",
                            name = "무엘사",
                            price = 10000000,
                            category = "ads",
                            id = 1L,
                        ),
                        id = 1L,
                        count = 1,
                    ),
                ),
            ),
        isPageable = true,
        isLoading = true,
        onCheckedChanged = {},
        totalPrice = 10000,
        totalCount = 10000,
        isChecked = { true },
        onSelectAllClick = {  },
        onOrderClick = {}
    )
}
