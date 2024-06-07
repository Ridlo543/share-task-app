package com.l0122138.ridlo.sharetaskapp.util

import com.l0122138.ridlo.sharetaskapp.model.ClassCreateRequest
import com.l0122138.ridlo.sharetaskapp.model.ClassData
import com.l0122138.ridlo.sharetaskapp.model.ClassUpdateRequest
import com.l0122138.ridlo.sharetaskapp.model.TaskData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
//    class
    @GET("classes/{code}")
    fun getClassByCode(@Path("code") code: String): Call<ClassData>

    @POST("classes")
    fun createClass(@Body request: ClassCreateRequest): Call<ClassData>

    @PATCH("classes/{code}")
    fun updateClass(@Path("code") code: String, @Body request: ClassUpdateRequest): Call<ClassData>

    @DELETE("classes/{code}")
    fun deleteClass(@Path("code") code: String): Call<ResponseBody>

//    Task
    @GET("classes/{code}/tasks")
    fun getTasksByClassCode(@Path("code") code: String): Call<List<TaskData>>

    @POST("classes/{code}/tasks")
    suspend fun addTask(
        @Path("code") code: String,
        @Body task: TaskData
    ): TaskData

    @PATCH("classes/{code}/tasks/{taskId}")
    suspend fun updateTask(
        @Path("code") code: String,
        @Path("taskId") taskId: String,
        @Body task: TaskData
    ): TaskData

    @DELETE("classes/{code}/tasks/{taskId}")
    suspend fun deleteTask(
        @Path("code") code: String,
        @Path("taskId") taskId: String
    )

//    @GET("api/classes/{code}/tasks")
//    fun getTasksByClassCode(@Path("code") code: String): Call<List<TaskData>>
}
