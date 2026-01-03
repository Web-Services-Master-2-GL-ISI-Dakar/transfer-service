package sn.ondmoney.txe.service.mapper;

import org.mapstruct.*;
import sn.ondmoney.txe.domain.Transfer;
import sn.ondmoney.txe.domain.Wallet;
import sn.ondmoney.txe.service.dto.TransferDTO;
import sn.ondmoney.txe.service.dto.WalletDTO;

/**
 * Mapper for the entity {@link Transfer} and its DTO {@link TransferDTO}.
 */
@Mapper(componentModel = "spring", uses = { WalletMapper.class })
public interface TransferMapper extends EntityMapper<TransferDTO, Transfer> {

    @Override
    @Mapping(target = "sender", source = "sender", qualifiedByName = "walletId")
    @Mapping(target = "receiver", source = "receiver", qualifiedByName = "walletId")
    TransferDTO toDto(Transfer s);

    @Override
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    Transfer toEntity(TransferDTO dto);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    void partialUpdate(@MappingTarget Transfer entity, TransferDTO dto);

    @Named("walletId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WalletDTO toDtoWalletId(Wallet wallet);
}
