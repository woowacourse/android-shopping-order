package woowacourse.shopping.feature.common.state

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ErrorDialog(
    error: AppError?,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
) {
    if (error == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("오류가 발생했습니다") },
        text = { Text(error.toUserMessage()) },
        confirmButton = {
            if (onRetry != null) {
                TextButton(onClick = onRetry) { Text("다시 시도") }
            } else {
                TextButton(onClick = onDismiss) { Text("확인") }
            }
        },
        dismissButton = if (onRetry != null) {
            { TextButton(onClick = onDismiss) { Text("닫기") } }
        } else {
            null
        },
    )
}
