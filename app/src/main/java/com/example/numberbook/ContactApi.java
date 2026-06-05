package com.example.numberbook;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ContactApi {

    @POST("insertContact.php")
    Call<ApiResponse> postNewContact(@Body Contact contact);

    @GET("getAllContacts.php")
    Call<List<Contact>> fetchAllContacts();

    @GET("searchContact.php")
    Call<List<Contact>> queryContacts(@Query("keyword") String searchQuery);
}
