package sn.ondmoney.txe.service.mapper;

import org.mapstruct.*;
import sn.ondmoney.txe.domain.Wallet;
import sn.ondmoney.txe.service.dto.WalletDTO;

/**
 * Mapper for the entity {@link Wallet} and its DTO {@link WalletDTO}.
 */
@Mapper(componentModel = "spring")
public interface WalletMapper extends EntityMapper<WalletDTO, Wallet> {}
