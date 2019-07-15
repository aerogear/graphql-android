package org.aerogear.offixoffline

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Operation

/* This class helps in creating an array list of mutations and callbacks associated with the apollo call.
 */
class Singleton {

    /* Array List to store mutation objects.
        */
    val offlineArrayList =
        arrayListOf<com.apollographql.apollo.api.Mutation<Operation.Data, Any, Operation.Variables>>()

    /* Array List to store callbacks objects.
     */
    val callbacksList = arrayListOf<ApolloCall.Callback<Any>>()

    companion object {
        var apClient: ApolloClient? = null
        private var instance: Singleton? = null

        /* Returns an existing instance of the Singleton class if it exists, else returns a new instance
         */
        @JvmStatic
        fun getInstance(): Singleton {
            if (instance == null) {
                instance = Singleton()
            }
            return instance!!
        }
    }
}