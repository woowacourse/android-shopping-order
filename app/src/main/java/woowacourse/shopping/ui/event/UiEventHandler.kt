package woowacourse.shopping.ui.event

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun UiEventHandler(
    uiEvent: Flow<UiEvent>,
    snackbarHostState: SnackbarHostState,
) {
    LaunchedEffect(uiEvent, snackbarHostState) {
        uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        withDismissAction = true,
                    )
                }
            }
        }
    }
}
