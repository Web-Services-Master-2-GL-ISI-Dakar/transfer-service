package sn.ondmoney.txe.service.mapper;

import org.mapstruct.*;
import sn.ondmoney.txe.domain.Transfer;
import sn.ondmoney.txe.service.dto.TransferDTO;

/**
 * Mapper for the entity {@link Transfer} and its DTO {@link TransferDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransferMapper extends EntityMapper<TransferDTO, Transfer> {}
