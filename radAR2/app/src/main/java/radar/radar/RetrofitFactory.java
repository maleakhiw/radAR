package radar.radar;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;



class LoggingInterceptor implements Interceptor {
    @Override public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        System.out.println(String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));

        okhttp3.Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        System.out.println(String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        return response;
    }
}

public class RetrofitFactory {
    private static final boolean DEBUG = false;
    private static Retrofit.Builder retrofit;

    public static Retrofit.Builder getRetrofitBuilder() {
        if (retrofit == null) {
            if (DEBUG) {
                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(new LoggingInterceptor())
                        .build();

                retrofit = new Retrofit.Builder()
                        .client(client)
                        .baseUrl("https://radar.fadhilanshar.com/api/")
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create());
                return retrofit;
            } else {
                retrofit = new Retrofit.Builder()
                        .baseUrl("https://radar.fadhilanshar.com/api/")
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create());
                return retrofit;
            }

        }
        return retrofit;

    }
}
