package sn.ondmoney.txe.service.mapper;

import org.mapstruct.*;
import sn.ondmoney.txe.domain.Wallet;
import sn.ondmoney.txe.service.dto.WalletDTO;

/**
 * Mapper for the entity {@link Wallet} and its DTO {@link WalletDTO}.
 */
@Mapper(componentModel = "spring")
public interface WalletMapper extends EntityMapper<WalletDTO, Wallet> {

    @Override
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "keycloakId", ignore = true)
    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "debits", ignore = true)
    @Mapping(target = "credits", ignore = true)
    @Mapping(target = "transfersSents", ignore = true)
    @Mapping(target = "transfersReceiveds", ignore = true)
    Wallet toEntity(WalletDTO dto);

    @Override
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "keycloakId", ignore = true)
    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "debits", ignore = true)
    @Mapping(target = "credits", ignore = true)
    @Mapping(target = "transfersSents", ignore = true)
    @Mapping(target = "transfersReceiveds", ignore = true)
    void partialUpdate(@MappingTarget Wallet entity, WalletDTO dto);
}
