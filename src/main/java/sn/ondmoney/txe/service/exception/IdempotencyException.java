package sn.ondmoney.txe.service.exception;

public class IdempotencyException extends RuntimeException {
    //public IdempotencyException(String msg){ super(msg); }
    public IdempotencyException(String message) {
        super(message);
    }

    public IdempotencyException(String message, Throwable cause) {
        super(message, cause);
    }
}
