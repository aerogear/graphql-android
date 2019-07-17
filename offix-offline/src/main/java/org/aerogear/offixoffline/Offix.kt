@file:JvmName("Offix")
package org.aerogear.offixoffline

import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Mutation
import com.apollographql.apollo.api.Operation

/* Extension function on ApolloClient which will be used by the user while making a call request.
   @receiver parameter is ApolloClient on which the call will be made by the user.
   @param mutation which will be stored in the list if network connection is not there.
   @param callback which will be stored in the list if network connection is not there.
 */
fun ApolloClient.enqueue(
    mutation: Mutation<Operation.Data, Any, Operation.Variables>,
    callback: ApolloCall.Callback<Any>
) {
    /* Set apollo client given by the user.
     */
    OfflineList.apClient = this

    /* Check is the network is available or not.
     */
    if (Offline.isNetwork()) {
        Log.d("Extension", " Network connected")
        this.mutate(mutation).enqueue(callback)
    } else {
        Log.d("Extension", "Network not connected")

        /* If the user is offline: (Case 1: When the app is in foreground, i.e. in-memory)
           1. Store the mutation object and callback in an array-list.
         */
        Log.d("Extension", " mutation : ${mutation.variables().valueMap()}")
        OfflineList.getInstance().offlineArrayList.add(mutation)
        OfflineList.getInstance().callbacksList.add(callback)
    }
}

/*  Extension function for ApolloClient Builder
    @receiver param: ApolloClient.Buidler, which can be used by the user for creating a custom client.
    @return ApolloClient.Buidler
 */
fun ApolloClient.Builder.OfflineClientBuilder(): ApolloClient.Builder = this


