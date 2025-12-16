package sn.ondmoney.txe.service.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String msg){ super(msg); }
}
