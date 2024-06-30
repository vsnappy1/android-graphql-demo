package com.randos.graphqldemo.apollo

import com.apollographql.apollo3.ApolloClient

val apolloClient by lazy {
    ApolloClient.Builder()
        .serverUrl("https://graphqlzero.almansi.me/api")
        .build()
}