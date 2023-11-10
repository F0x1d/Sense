package com.f0x1d.sense.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.f0x1d.sense.R
import com.f0x1d.sense.ui.widget.Audio
import com.f0x1d.sense.ui.widget.ErrorAlertDialog
import com.f0x1d.sense.ui.widget.NavigationBackIcon
import com.f0x1d.sense.viewmodel.AudioViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AudioScreen(navController: NavController) {
    val viewModel = hiltViewModel<AudioViewModel>()

    val audios by viewModel.generatedAudios.collectAsStateWithLifecycle(initialValue = emptyList())

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Column {
        LargeTopAppBar(
            title = { Text(text = stringResource(id = R.string.audio)) },
            navigationIcon = { NavigationBackIcon(navController = navController) },
            scrollBehavior = scrollBehavior
        )

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    start = 10.dp,
                    end = 10.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Column {
                        OutlinedTextField(
                            modifier = Modifier
                                .padding(top = 3.dp)
                                .fillMaxWidth()
                                .animateItemPlacement(),
                            value = viewModel.input,
                            onValueChange = { viewModel.input = it },
                            enabled = !viewModel.loading,
                            label = { Text(text = stringResource(R.string.text)) },
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(onGo = {
                                viewModel.generate()
                            })
                        )

                        SuggestionsTextField(
                            modifier = Modifier.padding(top = 10.dp),
                            options = viewModel.models,
                            value = viewModel.model,
                            setValue = { viewModel.model = it },
                            hint = R.string.model,
                            viewModel = viewModel
                        )

                        SuggestionsTextField(
                            modifier = Modifier.padding(top = 10.dp),
                            options = viewModel.voices,
                            value = viewModel.voice,
                            setValue = { viewModel.voice = it },
                            hint = R.string.voice,
                            viewModel = viewModel
                        )

                        SuggestionsTextField(
                            modifier = Modifier.padding(top = 10.dp),
                            options = viewModel.formats,
                            value = viewModel.format,
                            setValue = { viewModel.format = it },
                            hint = R.string.file_format,
                            viewModel = viewModel
                        )

                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 10.dp,
                                    bottom = 5.dp
                                )
                                .animateItemPlacement(),
                            value = viewModel.speed,
                            onValueChange = { viewModel.speed = it },
                            enabled = !viewModel.loading,
                            label = { Text(text = stringResource(R.string.speed)) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                if (viewModel.loading) item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .animateItemPlacement()
                    )
                }

                items(
                    audios,
                    key = { it.id }
                ) {
                    val exportFileActivityResult = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.CreateDocument(it.mimeType),
                        onResult = { uri -> viewModel.exportTo(uri, it) }
                    )

                    Audio(
                        audio = it,
                        click = { viewModel.play(it) },
                        export = { exportFileActivityResult.launch("ai-speech") },
                        delete = { viewModel.delete(it) }
                    )
                }
            }
        }

        ErrorAlertDialog(viewModel = viewModel)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun LazyItemScope.SuggestionsTextField(
    modifier: Modifier = Modifier.padding(10.dp),
    options: List<String>,
    value: String,
    setValue: (String) -> Unit,
    @StringRes hint: Int,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    viewModel: AudioViewModel
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier
            .fillMaxWidth()
            .animateItemPlacement(),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            value = value,
            onValueChange = setValue,
            enabled = !viewModel.loading,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            label = { Text(text = stringResource(hint)) },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        setValue(selectionOption)
                        expanded = false
                    },
                    text = { Text(text = selectionOption) }
                )
            }
        }
    }
}