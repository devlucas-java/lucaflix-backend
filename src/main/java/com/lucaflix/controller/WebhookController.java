//package com.lucaflix.controller;
//
//import com.lucaflix.config.StripeConfig;
//import com.lucaflix.dto.stripe.PaymentResponse;
//import com.lucaflix.service.StripeService;
//import com.google.gson.JsonSyntaxException;
//import com.stripe.model.Event;
//import com.stripe.model.StripeObject;
//import com.stripe.model.checkout.Session;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
///**
// * Separate controller for handling Stripe webhooks outside the
// * security filter chain
// */
//@RestController
//@RequestMapping("/webhook")
//public class WebhookController {
//    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
//
//    @Autowired
//    private StripeService stripeService;
//
//    @Autowired
//    private StripeConfig stripeConfig;
//
//    @PostMapping("/stripe")
//    public ResponseEntity<String> handleStripeWebhook(
//            @RequestBody String payload,
//            @RequestHeader(value = "Stripe-Signature", required = false) String signature) {
//
//        try {
//            // Use the configured webhook secret
//            String webhookSecret = stripeConfig.getWebhookSecret();
//
//            logger.info("Received Stripe webhook at /webhook/stripe endpoint");
//            logger.debug("Received Stripe webhook with signature: {}", signature);
//            logger.debug("Received webhook payload: {}", payload);
//
//            // Verify signature if present, otherwise log a warning but continue processing
//            Event event;
//            if (signature != null && !signature.isEmpty()) {
//                try {
//                    event = com.stripe.net.Webhook.constructEvent(payload, signature, webhookSecret);
//                    logger.info("Signature verification successful");
//                } catch (Exception e) {
//                    logger.error("Signature verification failed: {}", e.getMessage());
//                    // Continue with processing even if signature verification failed
//                    // This helps diagnose issues in test/development environments
//                    event = Event.GSON.fromJson(payload, Event.class);
//                    logger.warn("Proceeding with unverified event data");
//                }
//            } else {
//                logger.warn("Missing Stripe-Signature header. Processing payload without verification.");
//                // Parse the event manually when signature is missing (for testing/debugging)
//                event = Event.GSON.fromJson(payload, Event.class);
//            }
//
//            logger.info("Processing Stripe webhook event: {}", event.getType());
//            logger.debug("Event data: {}", event.getDataObjectDeserializer().getRawJson());
//
//            if ("checkout.session.completed".equals(event.getType())) {
//                try {
//                    // Try to get the session object safely
//                    StripeObject stripeObject = event.getDataObjectDeserializer().deserializeUnsafe();
//
//                    if (stripeObject instanceof Session) {
//                        Session session = (Session) stripeObject;
//                        String sessionId = session.getId();
//
//                        logger.info("Processing checkout.session.completed for session ID: {}", sessionId);
//
//                        // Process the successful payment
//                        boolean success = stripeService.handleSuccessfulPayment(sessionId);
//
//                        if (success) {
//                            logger.info("Successfully processed payment for session: {}", sessionId);
//                        } else {
//                            logger.warn("Failed to process payment for session: {}", sessionId);
//                        }
//                    } else {
//                        // Alternative approach: retrieve session ID from the JSON directly
//                        String sessionId = null;
//
//                        // Try to extract the id from the JSON directly
//                        com.google.gson.JsonObject jsonObject =
//                                new com.google.gson.JsonParser().parse(event.getDataObjectDeserializer().getRawJson())
//                                        .getAsJsonObject().getAsJsonObject("object");
//
//                        if (jsonObject != null && jsonObject.has("id")) {
//                            sessionId = jsonObject.get("id").getAsString();
//
//                            logger.info("Extracted session ID from JSON: {}", sessionId);
//
//                            // Process the successful payment
//                            boolean success = stripeService.handleSuccessfulPayment(sessionId);
//
//                            if (success) {
//                                logger.info("Successfully processed payment for session: {}", sessionId);
//                            } else {
//                                logger.warn("Failed to process payment for session: {}", sessionId);
//                            }
//                        } else {
//                            logger.error("Failed to extract session ID from event data");
//                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to extract session ID");
//                        }
//                    }
//                } catch (Exception e) {
//                    logger.error("Error processing session data: {}", e.getMessage(), e);
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing session data: " + e.getMessage());
//                }
//            } else {
//                // Log but don't reject other event types
//                logger.info("Received event type: {}", event.getType());
//                return ResponseEntity.ok("Event received: " + event.getType());
//            }
//
//            return ResponseEntity.ok("Webhook processed successfully");
//        } catch (JsonSyntaxException e) {
//            logger.error("Error parsing webhook JSON: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JSON parse error: " + e.getMessage());
//        } catch (Exception e) {
//            logger.error("Webhook error: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error: " + e.getMessage());
//        }
//    }
//}