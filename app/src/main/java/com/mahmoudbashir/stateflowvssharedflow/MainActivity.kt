package com.mahmoudbashir.stateflowvssharedflow

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.mahmoudbashir.stateflowvssharedflow.ui.theme.StateFlowVsSharedFlowTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<CounterViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
            val localLifeCycle = LocalLifecycleOwner.current
            val context = LocalContext.current
            LaunchedEffect(viewModel.uiEvent) {
                viewModel.uiEvent.flowWithLifecycle(localLifeCycle.lifecycle)
                    .collectLatest { event->
                        when(event){
                            is UiEvent.ShowToast -> {
                                // Show toast message
                               Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            }

            StateFlowVsSharedFlowTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CounterScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        uiState = uiState,
                        onAction = viewModel::onAction
                    )
                }
            }
        }
    }
}

@Composable
fun CounterScreen(
    modifier: Modifier,
    uiState: UiState,
    onAction: (Action) -> Unit
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(uiState.counter.toString())
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    onAction.invoke(Action.IncrementCounter)
                }
            ) {
                Text("Increase value")
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator()
        }
    }
}