package com.sylvaingoutouly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@SpringBootApplication
@EnableAsync
public class DeferredApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeferredApplication.class, args);
	}

    @Service
    public static class MonService {
        public void count() {
            for (int i = 0; i<10000000; i++) {
                System.out.println(i);
            }
        }
    }

	@RestController(value = "/coucou")
	public static class MonController {

        @Autowired MonService service;

		@RequestMapping(method = RequestMethod.GET)
		public DeferredResult<String> test() throws InterruptedException {
			DeferredResult d = new DeferredResult(2000L, "timeout");
            d.onTimeout(() -> System.err.println("expired"));
            service.count();

			d.setResult("coucou");
			return d;
		}

	}


}
