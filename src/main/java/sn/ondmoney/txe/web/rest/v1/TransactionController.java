package sn.ondmoney.txe.web.rest.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import sn.ondmoney.txe.service.P2PTransferService;
import sn.ondmoney.txe.service.dto.P2PTransferRequest;
import sn.ondmoney.txe.service.dto.P2PTransferResponse;
import sn.ondmoney.txe.service.dto.WalletBalanceResponse;
import sn.ondmoney.txe.web.rest.errors.BadRequestAlertException;

import java.util.UUID;

/**
 * REST controller for transaction operations.
 * Provides API v1 endpoints for P2P transfers and wallet operations.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Transactions", description = "API pour les transactions et opérations de wallet")
public class TransactionController {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionController.class);

    private final P2PTransferService p2pTransferService;

    public TransactionController(P2PTransferService p2pTransferService) {
        this.p2pTransferService = p2pTransferService;
    }

    /**
     * POST /api/v1/transactions/transfer : Initiate a P2P transfer.
     *
     * @param jwt the JWT token containing user info
     * @param request the transfer request
     * @return the transfer response
     */
    @PostMapping("/transactions/transfer")
    @Operation(summary = "Effectuer un transfert P2P", description = "Transfère de l'argent vers un autre utilisateur")
    public ResponseEntity<P2PTransferResponse> initiateTransfer(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody P2PTransferRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        
        LOG.debug("REST request to initiate P2P transfer: {}", request);

        // Extract phone number from JWT claims
        String senderPhone = extractPhoneFromJwt(jwt);
        
        // Generate correlation ID if not provided
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = "corr_" + UUID.randomUUID().toString();
        }

        P2PTransferResponse response = p2pTransferService.initiateTransfer(senderPhone, request, correlationId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/wallet/balance : Get wallet balance.
     *
     * @param jwt the JWT token containing user info
     * @return the wallet balance
     */
    @GetMapping("/wallet/balance")
    @Operation(summary = "Consulter le solde", description = "Récupère le solde du wallet de l'utilisateur connecté")
    public ResponseEntity<WalletBalanceResponse> getWalletBalance(@AuthenticationPrincipal Jwt jwt) {
        LOG.debug("REST request to get wallet balance");

        String phoneNumber = extractPhoneFromJwt(jwt);
        WalletBalanceResponse response = p2pTransferService.getWalletBalance(phoneNumber);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/wallet : Get wallet details.
     *
     * @param jwt the JWT token containing user info
     * @return the wallet details
     */
    @GetMapping("/wallet")
    @Operation(summary = "Détails du wallet", description = "Récupère les détails complets du wallet de l'utilisateur connecté")
    public ResponseEntity<WalletBalanceResponse> getWalletDetails(@AuthenticationPrincipal Jwt jwt) {
        LOG.debug("REST request to get wallet details");

        String phoneNumber = extractPhoneFromJwt(jwt);
        WalletBalanceResponse response = p2pTransferService.getWalletDetails(phoneNumber);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Extract phone number from JWT claims.
     * Tries multiple claim names as different OAuth providers use different conventions.
     */
    private String extractPhoneFromJwt(Jwt jwt) {
        if (jwt == null) {
            throw new BadRequestAlertException("Authentication required", "auth", "AUTH_REQUIRED");
        }

        // Try different claim names
        String phone = jwt.getClaimAsString("phone_number");
        if (phone == null || phone.isBlank()) {
            phone = jwt.getClaimAsString("phone");
        }
        if (phone == null || phone.isBlank()) {
            phone = jwt.getClaimAsString("preferred_username");
        }
        if (phone == null || phone.isBlank()) {
            phone = jwt.getSubject();
        }

        if (phone == null || phone.isBlank()) {
            throw new BadRequestAlertException("Phone number not found in token", "auth", "PHONE_NOT_IN_TOKEN");
        }

        return phone;
    }
}
