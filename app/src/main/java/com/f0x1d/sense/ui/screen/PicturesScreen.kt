package com.f0x1d.sense.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.f0x1d.sense.R
import com.f0x1d.sense.ui.widget.NavigationBackIcon
import com.f0x1d.sense.viewmodel.PicturesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PicturesScreen(navController: NavController) {
    val viewModel = hiltViewModel<PicturesViewModel>()

    val query by viewModel.query.observeAsState()
    val loading by viewModel.loading.observeAsState()
    val pictureUrl by viewModel.pictureUrl.observeAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Column {
        LargeTopAppBar(
            title = { Text(text = stringResource(id = R.string.pictures)) },
            navigationIcon = { NavigationBackIcon(navController = navController) },
            scrollBehavior = scrollBehavior
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 5.dp,
                            start = 10.dp,
                            end = 10.dp,
                            bottom = 20.dp
                        ),
                    value = query ?: "",
                    onValueChange = { viewModel.updateQuery(it) },
                    enabled = loading != true,
                    label = { Text(text = stringResource(R.string.picture)) },
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                    keyboardActions = KeyboardActions(onGo = {
                        viewModel.generate()
                    })
                )

                if (loading == true) {
                    LoadingIndicator()
                } else if (pictureUrl != null) {
                    val painter = rememberAsyncImagePainter(model = ImageRequest.Builder(LocalContext.current)
                        .data(pictureUrl)
                        .transformations(RoundedCornersTransformation(with(LocalDensity.current) { 12.dp.toPx() }))
                        .crossfade(true)
                        .build()
                    )
                    val painterState = painter.state

                    if (painterState is AsyncImagePainter.State.Loading) {
                        LoadingIndicator()
                    }

                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.0f / 1.0f)
                            .padding(horizontal = 10.dp),
                        painter = painter,
                        contentDescription = null
                    )

                    when (painterState) {
                        is AsyncImagePainter.State.Error -> Text(text = painterState.result.throwable.localizedMessage ?: "")

                        is AsyncImagePainter.State.Success -> IconButton(
                            modifier = Modifier.padding(vertical = 10.dp),
                            onClick = { viewModel.download() }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_download),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    CircularProgressIndicator(modifier = Modifier.padding(vertical = 10.dp))
}