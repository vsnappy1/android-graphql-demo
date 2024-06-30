package com.randos.graphqldemo.ui.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.randos.graphqldemo.GetPostsQuery
import com.randos.graphqldemo.apollo.apolloClient
import com.randos.graphqldemo.type.PageQueryOptions
import com.randos.graphqldemo.type.PaginateOptions

private const val TAG = "AllPostsScreen"

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AllPostsScreen(
    onAdd: () -> Unit
) {

    var response: ApolloResponse<GetPostsQuery.Data>? by remember { mutableStateOf(null) }
    var posts by remember { mutableStateOf(emptyList<GetPostsQuery.Data1>()) }
    var pageNumber by remember { mutableIntStateOf(1) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(pageNumber) {
        isLoading = true
        try {
            response = apolloClient.query(
                query = GetPostsQuery(
                    Optional.present(
                        PageQueryOptions(
                            paginate = Optional.present(
                                PaginateOptions(
                                    limit = Optional.present(10),
                                    page = Optional.present(pageNumber)
                                )
                            )
                        )
                    )
                )
            ).execute()
            posts = posts + response?.data?.posts?.data?.filterNotNull().orEmpty()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch posts", e)
        }
        isLoading = false
    }
    AllPostsScreen(
        onAdd = onAdd,
        posts = posts,
        next = response?.data?.posts?.links?.next,
        onReachingEndOfList = { pageNumber = it },
        isLoading = isLoading
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun AllPostsScreen(
    onAdd: () -> Unit,
    posts: List<GetPostsQuery.Data1>,
    next: GetPostsQuery.Next?,
    onReachingEndOfList: (Int) -> Unit,
    isLoading: Boolean
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onAdd() }) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add")
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(text = "GraphQL Demo", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(4.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(posts) { post ->
                    PostItem(post = post)
                }
                item {
                    next?.apply {
                        if (page != null) {
                            onReachingEndOfList(page)
                        }
                    }
                    if (isLoading) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun PostItem(post: GetPostsQuery.Data1) {
    Card {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = post.user?.username.orEmpty(),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = post.title.orEmpty(),
                style = MaterialTheme.typography.titleMedium
            )
            HorizontalDivider()
            Text(
                text = post.body.orEmpty(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}