package radar.radar;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kenneth on 16/10/17.
 */

public class RetrofitFactory {
    private static Retrofit retrofit;

    public static Retrofit getRetrofit() {
        if (retrofit != null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://radar.fadhilanshar.com/api/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
