package sn.ondmoney.txe.service.api;
import java.math.BigDecimal;
import sn.ondmoney.txe.service.dto.BalanceDTO;
import sn.ondmoney.txe.service.dto.OperationResultDTO;

public interface AccountService {

    BalanceDTO getBalance(String userId);
    OperationResultDTO debit(String userId, BigDecimal amount, String requestId);
    OperationResultDTO credit(String userId, BigDecimal amount, String requestId);
}
