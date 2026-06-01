package woowacourse.shopping.ui.event

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun UiEventHandler(
    uiEvent: Flow<UiEvent>,
    snackbarHostState: SnackbarHostState,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(uiEvent, snackbarHostState, lifecycleOwner) {
        uiEvent
            .flowWithLifecycle(
                lifecycle = lifecycleOwner.lifecycle,
                minActiveState = Lifecycle.State.STARTED,
            ).collect { event ->
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
