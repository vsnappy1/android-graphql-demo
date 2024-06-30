package com.randos.graphqldemo.ui.screen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.randos.graphqldemo.CreatePostMutation
import com.randos.graphqldemo.apollo.apolloClient
import com.randos.graphqldemo.type.CreatePostInput
import kotlinx.coroutines.launch

private const val TAG = "PublishPostScreen"

@Composable
fun PublishPostScreen(onPostPublished: () -> Unit) {

    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(8.dp),
    ) {
        Text(text = "Publish Post", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(text = "Title") })
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text(text = "Body") },
                    minLines = 4
                )
                val scope = rememberCoroutineScope()
                val context = LocalContext.current
                Button(
                    modifier = Modifier.size(100.dp, 40.dp),
                    enabled = !isLoading,
                    onClick = {
                        isLoading = true
                        scope.launch {
                            publishPost(title, body, context)
                            isLoading = false
                            onPostPublished()
                        }
                    }) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text(text = "Submit")
                    }
                }
            }
        }
    }
}

private suspend fun publishPost(
    title: String,
    body: String,
    context: Context
) {
    try {
        val response = apolloClient.mutation(
            CreatePostMutation(CreatePostInput(title = title, body = body))
        ).execute()
        response.data?.createPost?.apply {
            Toast.makeText(context, "Post published with title: ${this.title}", Toast.LENGTH_LONG)
                .show()
        } ?: {
            Toast.makeText(context, "Count not publish post", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Log.e(TAG, "Failed to publish post: ", e)
        Toast.makeText(context, "Operation failed.", Toast.LENGTH_SHORT).show()
    }
}