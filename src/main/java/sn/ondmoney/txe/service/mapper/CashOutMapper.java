package sn.ondmoney.txe.service.mapper;

import org.mapstruct.*;
import sn.ondmoney.txe.domain.CashOut;
import sn.ondmoney.txe.service.dto.CashOutDTO;

/**
 * Mapper for the entity {@link CashOut} and its DTO {@link CashOutDTO}.
 */
@Mapper(componentModel = "spring")
public interface CashOutMapper extends EntityMapper<CashOutDTO, CashOut> {}
