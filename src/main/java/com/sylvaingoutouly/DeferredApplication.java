package com.sylvaingoutouly;

import io.jmnarloch.spring.boot.rxjava.async.SingleDeferredResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;
import rx.Single;

@SpringBootApplication
@EnableAsync
public class DeferredApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeferredApplication.class, args);
	}

    @Service
    public static class MonService {
        public ListenableFuture<ResponseEntity<String>> count() {
			return new AsyncRestTemplate().getForEntity("http://ping.sylvaingoutouly.com", String.class);
        }
    }

	@RestController
	public static class MonController {

        @Autowired MonService service;

		@RequestMapping(value = "deferred", method = RequestMethod.GET)
		public DeferredResult<String> test() throws InterruptedException {
			DeferredResult d = new DeferredResult(2000L, "timeout");
            d.onTimeout(() -> System.err.println("expired"));

			service.count()
					.addCallback(stringResponseEntity -> d.setResult(stringResponseEntity.getBody()), Throwable::printStackTrace);

			return d;
		}

		@RequestMapping(value = "/observable", method = RequestMethod.GET)
		public Single<String> single() {
			return Single.just("coucou");
		}


		@RequestMapping(value = "/observableDeferred", method = RequestMethod.GET)
		public SingleDeferredResult<String> singleDeferred() {
			return new SingleDeferredResult<String>(2000L, Single.just("coucou"));
		}

	}


}
