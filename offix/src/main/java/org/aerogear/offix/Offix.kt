@file:JvmName("Offix")

package org.aerogear.offix

import android.util.Log
import androidx.work.*
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Mutation
import com.apollographql.apollo.api.Operation
import org.json.JSONObject
import org.aerogear.offix.MutationDataCheck

/*
conflictedMutationClass variable stores the name of the mutation class in which conflict has occurred.
 */
var conflictedMutationClass=""

/*
 This function takes in an object of Mutation<D,T,V> and returns an object of com.aerogear.offix.persistence.Mutation.
 */
fun getPersistenceMutation(mutation: Mutation<Operation.Data, Any, Operation.Variables>): org.aerogear.offix.persistence.Mutation {

    val operationId = MutationDataCheck.checkOperationID(mutation.operationId())
    val operationDoc = MutationDataCheck.checkOperationDoc(mutation.queryDocument())
    val operationName = MutationDataCheck.checkOperationName(mutation.name())
    val valMap = mutation.variables().valueMap()
    val jsonObject = MutationDataCheck.checkJSONObject(JSONObject(valMap))
    val responseClassName = mutation.javaClass.name

    /* Make an object of com.aerogear.offix.persistence.Mutation
     */
    val mutationDbObj = org.aerogear.offix.persistence.Mutation(
        operationId,
        operationDoc,
        operationName,
        jsonObject,
        responseClassName
    )
    return mutationDbObj
}

/*
 @param storedmutation : (org.aerogear.offixoffline.persistence)Mutation
 @return Mutation<Operation.Data, Operation.Data, Operation.Variables>
 */
fun getMutation(storedmutation: org.aerogear.offix.persistence.Mutation): Mutation<Operation.Data, Operation.Data, Operation.Variables> {

    //Get the class name of the mutation to which it has to be mapped
    val responseClassName = storedmutation.responseClassName

    val classReflected: Class<*> = Class.forName(responseClassName)

    //Get the constructor of the class, and as apollo generated classes have only one constructor, so take the first one.
    val constructor = classReflected.constructors.first()

    //Get the parameterTypes
    val parameters = constructor.parameterTypes
    val jsonValues = arrayListOf<Any>()

    //Get the json object i.e the variables map given as input by the user
    val jsonObj = storedmutation.valueMap

    //Put all the json values into a list
    val iter = jsonObj.keys()
    iter.forEach { key ->
        jsonValues.add(jsonObj.get(key))
    }

    Log.d("jsonValuesList ", " ${jsonValues.size}")

    //Check if the input parameter is of type Input<*>, if yes typecast it to be of the type Input<*>
    parameters.forEachIndexed { index, clazz ->

        Log.e("parameters : ", " ${clazz.name}")
        if (inputTypeChecker(clazz.name)) {
            jsonValues[index] = Input.optional(jsonValues[index])
            Log.e("parameters **: ", " ${jsonValues[index].javaClass.name}")
        }
    }

    //Make an object of mutation (done by reflection)
    val obj = constructor.newInstance(*jsonValues.toArray())
    return obj as Mutation<Operation.Data, Operation.Data, Operation.Variables>
}

/*
 Get the mutationDao of the database.
 @return Database Dao.
 */
fun getDao() = Offline.getDb()?.mutationDao()

/*
   To check if the string provided in the function matches apollo Input class or not.
 */
fun inputTypeChecker(string: String) = string.equals("com.apollographql.apollo.api.Input")
