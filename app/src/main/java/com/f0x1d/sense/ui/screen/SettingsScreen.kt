package com.f0x1d.sense.ui.screen

import androidx.annotation.StringRes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.f0x1d.sense.R
import com.f0x1d.sense.ui.widget.ErrorAlertDialog
import com.f0x1d.sense.ui.widget.NavigationBackIcon
import com.f0x1d.sense.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val viewModel = hiltViewModel<SettingsViewModel>()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    
    Column {
        LargeTopAppBar(
            title = { Text(text = stringResource(id = R.string.settings)) },
            navigationIcon = { NavigationBackIcon(navController = navController) },
            scrollBehavior = scrollBehavior
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 10.dp)
            ) {
                Spacer(modifier = Modifier.size(10.dp))

                SettingsTextField(
                    value = viewModel.endpoint,
                    onValueChange = { viewModel.endpoint = it },
                    labelResource = R.string.endpoint,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.size(10.dp))

                SettingsTextField(
                    value = viewModel.apiKey,
                    onValueChange = { viewModel.apiKey = it },
                    labelResource = R.string.api_key,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.size(10.dp))

                SettingsTextField(
                    value = viewModel.model,
                    onValueChange = { viewModel.model = it },
                    labelResource = R.string.model,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.size(10.dp))

                SettingsTextField(
                    value = viewModel.temperature,
                    onValueChange = { viewModel.temperature = it },
                    labelResource = R.string.temperature,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    viewModel = viewModel
                )
            }

            androidx.compose.animation.AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .navigationBarsPadding(),
                visible = viewModel.changesMade,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        viewModel.save {
                            navController.popBackStack()
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_done),
                        contentDescription = null
                    )
                }
            }
        }
        
        ErrorAlertDialog(viewModel = viewModel)
    }
}

@Composable
private fun SettingsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes labelResource: Int,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    viewModel: SettingsViewModel
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = {
            onValueChange(it)
            viewModel.changesMade = true
        },
        label = { Text(text = stringResource(id = labelResource)) },
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(12.dp)
    )
}