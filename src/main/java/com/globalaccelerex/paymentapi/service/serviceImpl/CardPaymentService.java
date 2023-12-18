package com.globalaccelerex.paymentapi.service.serviceImpl;

import com.flutterwave.bean.CardRequest;
import com.flutterwave.bean.Response;
import com.flutterwave.services.CardCharge;
import com.flutterwave.utility.Environment;
import com.globalaccelerex.paymentapi.dtos.request.PaymentRequest;
import com.globalaccelerex.paymentapi.dtos.response.ApiResponse;
import com.globalaccelerex.paymentapi.dtos.response.PaymentResponseDTO;
import com.globalaccelerex.paymentapi.enums.DeviceType;
import com.globalaccelerex.paymentapi.enums.TransactionStatus;
import com.globalaccelerex.paymentapi.exceptions.DuplicateTransactionException;
import com.globalaccelerex.paymentapi.model.CustomerInfo;
import com.globalaccelerex.paymentapi.entity.Payment;
import com.globalaccelerex.paymentapi.entity.PaymentTransaction;
import com.globalaccelerex.paymentapi.repository.PaymentRepository;
import com.globalaccelerex.paymentapi.repository.PaymentTransactionRepository;
import com.globalaccelerex.paymentapi.service.PaymentGateway;
import com.globalaccelerex.paymentapi.service.PaymentService;
import com.globalaccelerex.paymentapi.utils.DeviceDetector;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.globalaccelerex.paymentapi.enums.PaymentMethod.CARD;

@Service
@RequiredArgsConstructor
public class CardPaymentService extends PaymentService implements PaymentGateway {

    @Value("${PUBLIC_KEY}")
    private String publicKey;
    @Value("${SECRET_KEY}")
    private String secretKey;
    @Value("${ENCRYPTION_KEY}")
    private String encryptionKey;


    private final CardCharge cardCharge;
    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository transactionRepository;


    @Override
    @Transactional
    public ApiResponse<PaymentResponseDTO> makePayment(PaymentRequest paymentRequest, String userAgent) {
        DeviceType deviceType = DeviceDetector.detect(userAgent);
        return processPayment(paymentRequest, deviceType);
    }

    @Override
    public ApiResponse<PaymentResponseDTO> processPayment(PaymentRequest paymentRequest, DeviceType deviceType) {
        Payment newPayment;
        try {
            newPayment = paymentRepository.save(buildPayment(paymentRequest, deviceType));
        } catch (DuplicateTransactionException e) {
            throw new DuplicateTransactionException("Transaction Reference Already Exist");
        }
        Environment.setPublicKey(publicKey);
        Environment.setSecretKey(secretKey);
        Environment.setEncryptionKey(encryptionKey);

        Response authorizeResponse = cardCharge.runTransaction(buildCardRequest(paymentRequest));
        if(authorizeResponse.getStatus().equals("error")){
            throw new DuplicateTransactionException("Transaction Reference Already Exist");
        }
        Map<String, String> customerInfo = parseCustomerInfo(authorizeResponse);
        Map<String, String> transactionResponse = parseTransactionResponse(authorizeResponse);

        PaymentResponseDTO responseDTO = new PaymentResponseDTO();
        BeanUtils.copyProperties(buildPayment(paymentRequest, deviceType, customerInfo), responseDTO);
        PaymentTransaction newTransaction = transactionRepository.save(buildTransaction(transactionResponse, customerInfo, newPayment));
        responseDTO.setTransactions(Collections.singletonList(newTransaction));
        return new ApiResponse<>(HttpStatus.OK.value(), responseDTO, authorizeResponse.getMessage());
    }

    private Payment buildPayment(PaymentRequest paymentRequest, DeviceType deviceType) {
        return Payment.builder()
                .paymentMethod(CARD)
                .createdAt(LocalDateTime.now())
                .lastUpdatedAt(LocalDateTime.now())
                .deviceType(deviceType)
                .customer(CustomerInfo.builder()
                        .customerId(paymentRequest.getTransactionReference())
                        .email(paymentRequest.getEmail())
                        .name(paymentRequest.getCardHolderName())
                        .phoneNumber(paymentRequest.getPhoneNumber())
                        .build())
                .build();
    }

    private PaymentTransaction buildTransaction( Map<String, String> transactionResponse, Map<String, String> customerInfo, Payment newPayment) {
        return PaymentTransaction.builder()
                .id(Long.valueOf(transactionResponse.get("transactionId")))
                .amount(BigDecimal.valueOf(Long.parseLong(transactionResponse.get("transactionAmount"))))
                .createdAt(LocalDateTime.parse(customerInfo.get("created_at").replace("Z", "")))
                .currency(transactionResponse.get("transactionCurrency"))
                .txRef(transactionResponse.get("paymentTxRef"))
                .transactionStatus(TransactionStatus.valueOf(transactionResponse.get("status").toUpperCase()))
                .payment(newPayment)
                .build();

    }

    private Payment buildPayment(PaymentRequest paymentRequest, DeviceType deviceType, Map<String, String> customerInfo) {
        System.out.println("This is :" + customerInfo.get("id"));
        Payment payment = buildPayment(paymentRequest, deviceType);
        payment.setCustomer(CustomerInfo.builder()
                .email(paymentRequest.getEmail())
                .name(paymentRequest.getCardHolderName())
                .phoneNumber(paymentRequest.getPhoneNumber())
                .customerId(customerInfo.get("id"))
                .build());
        return payment;
    }


    private CardRequest buildCardRequest(PaymentRequest paymentRequest) {
        return new CardRequest(
                paymentRequest.getCardNumber(),
                paymentRequest.getCvv(),
                paymentRequest.getExpiryMonth(),
                paymentRequest.getExpiryYear(),
                paymentRequest.getCurrency(),
                paymentRequest.getAmount(),
                paymentRequest.getCardHolderName(),
                paymentRequest.getEmail(),
                paymentRequest.getTransactionReference(),
                null,
                null
        );
    }
    public static Map<String, String> parseTransactionResponse(Response authorizeResponse) {

        Map<String, String> transactionInfo = new HashMap<>();
        JSONObject jsonObject = new JSONObject(authorizeResponse);
        JSONObject data = jsonObject.getJSONObject("data");
        transactionInfo.put("transactionId", String.valueOf(data.getInt("id")));
        transactionInfo.put("paymentTxRef", data.getString("tx_ref"));
        transactionInfo.put("transactionAmount", String.valueOf(data.getInt("amount")));
        transactionInfo.put("transactionCurrency", data.getString("currency"));
        transactionInfo.put("parsedStatus", data.getString("status").toLowerCase());

        String parsedStatus = transactionInfo.get("parsedStatus");
        TransactionStatus status = switch (parsedStatus) {
            case "successful" -> TransactionStatus.SUCCESSFUL;
            case "failed" -> TransactionStatus.FAILED;
            case "pending" -> TransactionStatus.PENDING;
            case "cancelled" -> TransactionStatus.CANCELLED;
            default -> throw new IllegalStateException("Unexpected value: " + parsedStatus);
        };

        transactionInfo.put("status", status.name().toLowerCase());

        return transactionInfo;
    }

    private static Map<String, String> parseCustomerInfo(Response response) {
        String customerString = response.getData().getCustomerString();
        String[] keyValuePairs = customerString.substring(1, customerString.length() - 1).split(", ");

        Map<String, String> customerMap = new HashMap<>();

        for (String pair : keyValuePairs) {
            String[] entry = pair.split("=");
            String key = entry[0];
            String value = entry.length > 1 ? entry[1] : null;

            customerMap.put(key.trim(), value != null ? value.trim() : null);
        }
        return customerMap;
    }

    @Override
    public ApiResponse<List<PaymentResponseDTO>> getPayments(Integer offset, Integer limit, DeviceType deviceType) {
        List<PaymentResponseDTO> responses;

        if (offset != null && limit != null && offset >= 0 && limit >= 1) {
            responses = fetchPaymentsPaginated(deviceType, offset, limit);
        } else {
            responses = fetchPaymentsAll(deviceType);
        }

        return new ApiResponse<>(HttpStatus.OK.value(), responses, "Payments retrieved successfully");
    }

    private List<PaymentResponseDTO> fetchPaymentsPaginated(DeviceType deviceType, Integer offset, Integer limit) {
        Page<Payment> paymentEntitiesPage;
        if (deviceType != null) {
            paymentEntitiesPage = paymentRepository.findPaymentsByDeviceType(deviceType, PageRequest.of(offset, limit));
        } else {
            paymentEntitiesPage = paymentRepository.findAll(PageRequest.of(offset, limit));
        }
        return paymentEntitiesPage.getContent()
                .stream()
                .map(this::buildPaymentResponseDTO)
                .toList();
    }

    private List<PaymentResponseDTO> fetchPaymentsAll(DeviceType deviceType) {
        List<Payment> paymentEntities;
        if (deviceType != null) {
            paymentEntities = paymentRepository.findPaymentsByDeviceType(deviceType);
        } else {
            paymentEntities = paymentRepository.findAll();
        }
        return paymentEntities
                .stream()
                .map(this::buildPaymentResponseDTO)
                .toList();
    }

    private PaymentResponseDTO buildPaymentResponseDTO(Payment entity) {
        List<PaymentTransaction> paymentTransaction = transactionRepository.findAllByPayment(entity);

        return PaymentResponseDTO.builder()
                .paymentMethod(entity.getPaymentMethod())
                .deviceType(entity.getDeviceType())
                .lastUpdatedAt(entity.getLastUpdatedAt())
                .createdAt(entity.getCreatedAt())
                .customer(entity.getCustomer())
                .transactions(paymentTransaction)
                .build();
    }
}