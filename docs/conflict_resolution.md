## Conflict Resolution 

### Steps to support Conflict Resolution in your app:

- Create a class, let say `UserConflictResolutionHandler` which extends ConfliceResolutionInterface provided by the library.
- Override the `resolveConflict()` method of the interface in your class.
- You get the **`server state`** and the **`client state`** data associated with that mutation in which conflict occurred.
- You also get the **`operation type`** of the conflicted mutation. Run a switch case on the operation type to detect which type of mutation is it in which conflict occured and accordingly create an object of that mutation while resolving conflicts.
- You can resolve conflicts based on your business logic and again make a call to the server.


#### The [conflict protocol/structure](https://offix.dev/#/ref-conflict-server?id=structure-of-the-conflict-error) is descibed on [offix.dev](https://offix.dev/)



### Code:

```kotlin
/*
UserConflictResolutionHandler extends ConflictResolutionInterface.
Here the user provides the custom implementation of resolving conflicts.
 */
class UserConflictResolutionHandler(val context: Context) : ConflictResolutionInterface {
    val TAG = javaClass.simpleName

    /*User get the server state and the client state.
     This function resolves the conflicts based on the user business logic.
     */
    override fun resolveConflict(
        serverState: Map<String, Any>,
        clientState: Map<String, Any>,
        operationType: String
    ) {
        /*Version based approach of Conflict Resolution, for instance.
        */
        val serverMap = serverState
        val containsVersion = serverMap.containsKey("version")

        if (containsVersion) {
            var versionAfterConflict = serverMap.get("version") as Int + 1

            /* You can run a switch case on the operation type to detect which type of mutation is it in which conflict occured
               and accordingly create an object of that mutation, resolve conflict and make a server call with it.
             */
            when (operationType) {
                /*
                According to the schema structure, used a version based approach of resolving conflicts.
                If operationType is "UpdateTaskMutation" perform the following steps to resolve conflicts.
                 */
                "UpdateTaskMutation" -> {
                 
                    val input = TaskInput.builder().title(clientState["title"].toString()).version(versionAfterConflict)
                        .description(clientState["description"].toString()).status("test").build()
                   
                    var mutation = UpdateTaskMutation.builder().id(clientState["id"].toString()).input(input).build()
                   
                    val mutationCall = Utils.getApolloClient(context)?.mutate(mutation)

                    val callback = object : ApolloCall.Callback<UpdateTaskMutation.Data>() {
                        override fun onFailure(e: ApolloException) {                       
                            e.printStackTrace()
                        }

                        override fun onResponse(response: Response<UpdateTaskMutation.Data>) {
                            Log.e("onResponse() updateTask", "${response.data()?.updateTask()?.title()}")
                            val result = response.data()?.updateTask()
                            //In case of conflicts, data returned from the server is null.
                            result?.let {
                                Log.e(TAG, "onResponse-UpdateTask- $it")
                            }
                        }
                    }
                    mutationCall?.enqueue(callback)
                }
                
                "UpdateUserMutation" -> {
                    /* Similar for UpdateUserMutation
                    1. Get all the fields from the clientState.
                    2. Create an object of mutation.
                    3. Again make a call to the server.
                    */
                }
            }
        }
    }
}
```
