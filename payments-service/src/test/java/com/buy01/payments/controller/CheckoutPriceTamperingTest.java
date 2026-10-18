package com.buy01.payments.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.buy01.payments.dto.CheckoutSessionResponse;
import com.buy01.payments.dto.CreateCheckoutSessionRequest;
import com.buy01.payments.service.StripeCheckoutService;

class CheckoutPriceTamperingTest {

    @Test
    void ignoresPriceFieldsSentByTheBrowser() throws Exception {
        AtomicReference<CreateCheckoutSessionRequest> received = new AtomicReference<>();
        StripeCheckoutService service = new StripeCheckoutService("", "") {
            @Override
            public CheckoutSessionResponse createCheckoutSession(
                    CreateCheckoutSessionRequest request, String userId) {
                received.set(request);
                return new CheckoutSessionResponse("cs_1", "https://checkout.test");
            }
        };
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new PaymentController(service)).build();

        mvc.perform(post("/api/payments/checkout-sessions")
                        .header("X-User-Id", "user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":"9f834ed8-c8a8-4e68-8647-a3e12bcb61f2","quantity":1,
                                 "price":0.01,"unitAmount":1,"currency":"jpy","name":"Free"}
                                """))
                .andExpect(status().isOk());

        // Only the buyer's choices reach the service; the charged amount is
        // looked up from products-service, never taken from the request.
        assertEquals(new CreateCheckoutSessionRequest("9f834ed8-c8a8-4e68-8647-a3e12bcb61f2", 1),
                received.get());
    }
}
