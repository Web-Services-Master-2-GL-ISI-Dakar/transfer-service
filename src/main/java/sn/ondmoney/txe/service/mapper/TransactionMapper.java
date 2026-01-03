package sn.ondmoney.txe.service.mapper;

import org.mapstruct.*;
import sn.ondmoney.txe.domain.Transaction;
import sn.ondmoney.txe.domain.Wallet;
import sn.ondmoney.txe.service.dto.TransactionDTO;
import sn.ondmoney.txe.service.dto.WalletDTO;

/**
 * Mapper for the entity {@link Transaction} and its DTO {@link TransactionDTO}.
 */
@Mapper(componentModel = "spring", uses = { WalletMapper.class })
public interface TransactionMapper extends EntityMapper<TransactionDTO, Transaction> {

    @Override
    @Mapping(target = "debitedAccount", source = "debitedAccount", qualifiedByName = "walletId")
    @Mapping(target = "creditedAccount", source = "creditedAccount", qualifiedByName = "walletId")
    TransactionDTO toDto(Transaction s);

    @Override
    @Mapping(target = "debitedAccount", ignore = true)
    @Mapping(target = "creditedAccount", ignore = true)
    Transaction toEntity(TransactionDTO dto);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "debitedAccount", ignore = true)
    @Mapping(target = "creditedAccount", ignore = true)
    void partialUpdate(@MappingTarget Transaction entity, TransactionDTO dto);

    @Named("walletId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WalletDTO toDtoWalletId(Wallet wallet);
}
