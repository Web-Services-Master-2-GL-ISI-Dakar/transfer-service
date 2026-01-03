package sn.ondmoney.txe.service;

import sn.ondmoney.txe.service.dto.P2PTransferRequest;
import sn.ondmoney.txe.service.dto.P2PTransferResponse;
import sn.ondmoney.txe.service.dto.WalletBalanceResponse;

/**
 * Service interface for P2P transfers.
 */
public interface P2PTransferService {

    /**
     * Initiate a P2P transfer from the authenticated user to another user.
     *
     * @param senderPhone the sender's phone number (from JWT token)
     * @param request the transfer request
     * @param correlationId the correlation ID for tracing
     * @return the transfer response
     */
    P2PTransferResponse initiateTransfer(String senderPhone, P2PTransferRequest request, String correlationId);

    /**
     * Get the wallet balance for a user.
     *
     * @param phoneNumber the user's phone number
     * @return the wallet balance response
     */
    WalletBalanceResponse getWalletBalance(String phoneNumber);

    /**
     * Get wallet details for a user.
     *
     * @param phoneNumber the user's phone number
     * @return the wallet details
     */
    WalletBalanceResponse getWalletDetails(String phoneNumber);
}
