package sn.ondmoney.txe.service;

import sn.ondmoney.txe.domain.Transfer;
import sn.ondmoney.txe.service.dto.TransferRequestDTO;

public interface TransferBusinessService {

    Transfer executeP2P(TransferRequestDTO request);
}
