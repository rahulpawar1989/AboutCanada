package com.assesment.aboutcanada.app_interface;

import com.assesment.aboutcanada.model.CityInfo;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface RequestInterface {
    @GET("s/2iodh4vg0eortkl/facts.json")
     Observable<CityInfo> register() ;


}
