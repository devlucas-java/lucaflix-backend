//package com.lucaflix.service;
//
//import com.lucaflix.dto.stripe.PaymentRequest;
//import com.lucaflix.dto.stripe.PaymentResponse;
//
//import com.lucaflix.model.User;
//import com.lucaflix.repository.UserRepository;
//import com.stripe.exception.StripeException;
//import com.stripe.model.checkout.Session;
//import com.stripe.param.checkout.SessionCreateParams;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//
//@Service
//public class StripeService {
//
//    @Autowired
//    private UserRepository userRepository;
//    private static final Logger logger = LoggerFactory.getLogger(StripeService.class);
//    private static final String VERIFICATION_PRODUCT_ID = "prod_S83e8EiaRwwhYs";
//    private static final String PUBLIC_PROFILE_PRODUCT_ID = "prod_S83crcper3xEP6";
//
//    public PaymentResponse createVerificationPayment(PaymentRequest request, User currentUser) {
//        return createPayment(request, currentUser, VERIFICATION_PRODUCT_ID, "Verificar perfil por 30 dias");
//    }
//
//
//    public PaymentResponse createPublicProfilePayment(PaymentRequest request, User currentUser) {
//        return createPayment(request, currentUser, PUBLIC_PROFILE_PRODUCT_ID, "Deixar o perfil publico por 30 dias");
//    }
//
//    private PaymentResponse createPayment(PaymentRequest request, User currentUser, String productId, String productName) {
//        try {
//            if (currentUser == null || currentUser.getEscortProfile() == null) {
//                throw new ResourceNotFoundException("Escort profile not found for current user");
//            }
//
//            Map<String, String> metadata = new HashMap<>();
//            metadata.put("userId", currentUser.getId().toString());
//            metadata.put("escortProfileId", currentUser.getEscortProfile().getId().toString());
//            metadata.put("productId", productId);
//
//            // Create checkout session parameters
//            SessionCreateParams params = SessionCreateParams.builder()
//                    .setMode(SessionCreateParams.Mode.PAYMENT)
//                    .setSuccessUrl(request.getReturnUrl() + "?success=true&productId=" + productId)
//                    .setCancelUrl(request.getReturnUrl() + "?canceled=true")
//                    .setCustomerEmail(currentUser.getEmail())
//                    .addLineItem(SessionCreateParams.LineItem.builder()
//                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
//                                    .setCurrency(request.getCurrency())
//                                    .setUnitAmount(request.getAmount())
//                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                                            .setName(productName)
//                                            .build())
//                                    .build())
//                            .setQuantity(1L)
//                            .build())
//                    .putAllMetadata(metadata)
//                    .build();
//
//            // Create the checkout session
//            Session session = Session.create(params);
//
//            // Return response with redirect URL
//            return PaymentResponse.builder()
//                    .paymentId(session.getId())
//                    .paymentUrl(session.getUrl())
//                    .status("pending")
//                    .message("Payment session created successfully")
//                    .build();
//        } catch (StripeException e) {
//            return PaymentResponse.builder()
//                    .status("error")
//                    .message("Error creating payment: " + e.getMessage())
//                    .build();
//        } catch (ResourceNotFoundException e) {
//            return PaymentResponse.builder()
//                    .status("error")
//                    .message(e.getMessage())
//                    .build();
//        } catch (Exception e) {
//            return PaymentResponse.builder()
//                    .status("error")
//                    .message("Error processing request: " + e.getMessage())
//                    .build();
//        }
//    }
//
//    public PaymentResponse getPaymentStatus(String paymentId) {
//        try {
//            // Retrieve the checkout session to check status
//            Session session = Session.retrieve(paymentId);
//
//            String status = session.getPaymentStatus();
//
//            return PaymentResponse.builder()
//                    .paymentId(paymentId)
//                    .status(status)
//                    .message("Payment status retrieved successfully")
//                    .build();
//        } catch (StripeException e) {
//            return PaymentResponse.builder()
//                    .paymentId(paymentId)
//                    .status("error")
//                    .message("Error retrieving payment status: " + e.getMessage())
//                    .build();
//        }
//    }
//
//    @Transactional
//    public boolean handleSuccessfulPayment(String sessionId) {
//        try {
//            Session session = Session.retrieve(sessionId);
//            logger.info("Processing payment for session ID: {}, payment status: {}",
//                    sessionId, session.getPaymentStatus());
//
//            boolean isSuccessfulPayment = "complete".equals(session.getPaymentStatus()) ||
//                    "paid".equals(session.getPaymentStatus());
//            if (!isSuccessfulPayment) {
//                logger.warn("Payment not successful. Status: {}", session.getPaymentStatus());
//                return false;
//            }
//
//            Map<String, String> metadata = session.getMetadata();
//            String userId = metadata.get("userId");
//            String escortProfileId = metadata.get("escortProfileId");
//            String productId = metadata.get("productId");
//
//            if (userId == null || escortProfileId == null || productId == null) {
//                logger.warn("Missing metadata in session: userId={}, escortProfileId={}, productId={}",
//                        userId, escortProfileId, productId);
//                return false;
//            }
//
//            UUID profileUuid;
//            try {
//                profileUuid = UUID.fromString(escortProfileId);
//            } catch (IllegalArgumentException e) {
//                logger.error("Invalid escort profile UUID format: {}", escortProfileId, e);
//                return false;
//            }
//
//            // Check if profile exists before attempting to retrieve
//            Optional<EscortProfile> profileOptional = escortProfileRepository.findById(profileUuid);
//            if (profileOptional.isEmpty()) {
//                logger.error("Escort profile not found with ID: {}", escortProfileId);
//                return false;
//            }
//
//            EscortProfile profile = profileOptional.get();
//
//            // Rest of the method remains the same...
//            // Set 30 days from now
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.DAY_OF_MONTH, 30);
//            Date expiryDate = calendar.getTime();
//
//            if (VERIFICATION_PRODUCT_ID.equals(productId)) {
//                profile.setVerified(true);
//                profile.setVerificationExpiryDate(expiryDate);
//                logger.info("Updated profile {} verification status to true with expiry date {}",
//                        profile.getId(), expiryDate);
//            } else if (PUBLIC_PROFILE_PRODUCT_ID.equals(productId)) {
//                profile.setPublic(true);
//                profile.setPublicExpiryDate(expiryDate);
//                logger.info("Updated profile {} public status to true with expiry date {}",
//                        profile.getId(), expiryDate);
//            } else {
//                logger.warn("Unknown product ID: {}", productId);
//                return false;
//            }
//
//            escortProfileRepository.save(profile);
//            logger.info("Successfully saved updated profile for ID: {}", profile.getId());
//            return true;
//        } catch (Exception e) {
//            logger.error("Error processing payment: {}", e.getMessage(), e);
//            return false;
//        }
//    }
//}