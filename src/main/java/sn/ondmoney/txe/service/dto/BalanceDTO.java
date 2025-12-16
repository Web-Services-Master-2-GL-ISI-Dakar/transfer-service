package sn.ondmoney.txe.service.dto;

import java.math.BigDecimal;
import java.time.Instant;


public class BalanceDTO {

    private String userId;
    private BigDecimal balance;
    private Instant updatedAt;

    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId=userId;
    }
    public BigDecimal getBalance(){
        return balance;
    }
    public void setBalance(BigDecimal balance){
        this.balance=balance;
    }
    public Instant getUpdatedAt(){
        return updatedAt;
    }
    public void setUpdatedAt(Instant updatedAt){
        this.updatedAt=updatedAt;
    }
}
