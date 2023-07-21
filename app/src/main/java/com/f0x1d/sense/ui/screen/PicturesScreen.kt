package com.f0x1d.sense.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.f0x1d.sense.R
import com.f0x1d.sense.ui.widget.ErrorAlertDialog
import com.f0x1d.sense.ui.widget.NavigationBackIcon
import com.f0x1d.sense.viewmodel.PicturesViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PicturesScreen(navController: NavController) {
    val viewModel = hiltViewModel<PicturesViewModel>()

    val images by viewModel.generatedImages.collectAsStateWithLifecycle(initialValue = emptyList())

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Column {
        LargeTopAppBar(
            title = { Text(text = stringResource(id = R.string.pictures)) },
            navigationIcon = { NavigationBackIcon(navController = navController) },
            scrollBehavior = scrollBehavior
        )

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .animateItemPlacement(),
                        value = viewModel.query,
                        onValueChange = { viewModel.query = it },
                        enabled = !viewModel.loading,
                        label = { Text(text = stringResource(R.string.picture)) },
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = {
                            viewModel.generate()
                        })
                    )
                }

                if (viewModel.loading) item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .animateItemPlacement()
                    )
                }

                items(
                    images,
                    key = { it.id }
                ) { image ->
                    val imageRequest = ImageRequest.Builder(LocalContext.current)
                        .data(image.url)
                        .transformations(RoundedCornersTransformation(with(LocalDensity.current) { 12.dp.toPx() }))
                        .crossfade(true)
                        .build()

                    SubcomposeAsyncImage(
                        modifier = Modifier.animateItemPlacement(),
                        model = imageRequest,
                        imageLoader = viewModel.imageLoader,
                        contentDescription = null
                    ) {
                        when (val state = painter.state) {
                            is AsyncImagePainter.State.Error -> Text(text = state.result.throwable.localizedMessage ?: "")

                            is AsyncImagePainter.State.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f / 1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            else -> {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    this@SubcomposeAsyncImage.SubcomposeAsyncImageContent(
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    )

                                    IconButton(
                                        modifier = Modifier.padding(top = 10.dp),
                                        onClick = { viewModel.download(image.url) }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_download),
                                            tint = MaterialTheme.colorScheme.primary,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        ErrorAlertDialog(viewModel = viewModel)
    }
}