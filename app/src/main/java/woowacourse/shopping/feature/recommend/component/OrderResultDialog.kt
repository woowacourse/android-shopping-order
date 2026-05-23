package woowacourse.shopping.feature.recommend.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import woowacourse.shopping.feature.recommend.OrderDialogUiState

@Composable
fun OrderResultDialog(
    state: OrderDialogUiState,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
) {
    when (state) {
        OrderDialogUiState.None -> Unit

        OrderDialogUiState.AuthExpired -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("인증 정보가 만료되었습니다") },
            text = { Text("인증 정보를 갱신해주세요") },
            confirmButton = {
                TextButton(onClick = onDismiss) { Text("확인") }
            },
        )

        OrderDialogUiState.ServerError -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("주문에 실패했어요") },
            text = { Text("일시적인 오류가 발생했습니다. 다시 시도해 주세요.") },
            confirmButton = {
                TextButton(onClick = onRetry) { Text("다시 시도") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("닫기") }
            },
        )

        OrderDialogUiState.NetworkError -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("네트워크 연결을 확인해 주세요") },
            text = { Text("인터넷 연결을 확인하고 다시 시도해 주세요.") },
            confirmButton = {
                TextButton(onClick = onRetry) { Text("다시 시도") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("닫기") }
            },
        )
    }
}

@Preview
@Composable
private fun OrderResultDialogPreviewAuth() {
    OrderResultDialog(
        state = OrderDialogUiState.AuthExpired,
        onDismiss = { },
        onRetry = { },
    )
}

@Preview
@Composable
private fun OrderResultDialogPreviewServer() {
    OrderResultDialog(
        state = OrderDialogUiState.ServerError,
        onDismiss = { },
        onRetry = { },
    )
}
