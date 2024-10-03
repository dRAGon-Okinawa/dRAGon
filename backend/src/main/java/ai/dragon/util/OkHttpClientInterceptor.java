package ai.dragon.util;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpClientInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        System.out.println(String.format("Sending request on %s with query string %s",
                request.url(), request.url().query()));

        System.out.println("Headers :");
        System.out.println(request.headers());

        System.out.println("Body :");
        if (request.body() != null && request.body() instanceof FormBody formBody) {
            for (int i = 0; i < formBody.size(); i++) {
                System.out.println(formBody.name(i) + ": " + formBody.value(i));
            }
        }

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        System.out.println(String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        // System.out.println(response.body().string());

        return response;
    }
}
