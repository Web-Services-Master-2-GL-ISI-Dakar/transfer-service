package sn.ondmoney.txe.service.dto;

import java.math.BigDecimal;

public class TransferRequestDTO {

    private String senderUserId;
    private String receiverUserId;
    private BigDecimal amount;
    private String requestId;
    private String externalRef;


    public String getSenderUserId(){
        return senderUserId;
    }
    public void setSenderUserId(String v){
        this.senderUserId=v;
    }
    public String getReceiverUserId(){
        return receiverUserId;
    }
    public void setReceiverUserId(String v){
        this.receiverUserId=v;
    }
    public BigDecimal getAmount(){
        return amount;
    }
    public void setAmount(BigDecimal amount){
        this.amount=amount;
    }
    public String getRequestId(){
        return requestId;
    }
    public void setRequestId(String requestId){
        this.requestId=requestId;
    }
    public String getExternalRef(){
        return externalRef;
    }
    public void setExternalRef(String externalRef){
        this.externalRef=externalRef;
    }
}
