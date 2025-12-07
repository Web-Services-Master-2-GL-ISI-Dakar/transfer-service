package sn.ondmoney.txe.domain.enumeration;

/**
 * Types de transaction pris en charge par le TXE (Débit/Crédit)
 */
public enum TransactionType {
    TRANSFER_P2P,
    CARD_RECHARGE,
    BILL_PAYMENT,
    MERCHANT_PAYMENT,
    AIRTIME_PURCHASE,
    WALLET2BANK,
    BANK2WALLET,
}
