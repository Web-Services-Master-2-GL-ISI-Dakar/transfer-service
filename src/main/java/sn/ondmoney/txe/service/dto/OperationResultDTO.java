package sn.ondmoney.txe.service.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class OperationResultDTO {

    private String userId;
    private boolean success;
    private String code;
    private String message;
    private BigDecimal newBalance;
    private Instant processedAt;
    private String requestId;


    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId=userId;
    }
    public boolean isSuccess(){
        return success;
    }
    public void setSuccess(boolean success){
        this.success=success;
    }
    public String getCode(){
        return code;
    }
    public void setCode(String code){
        this.code=code;
    }
    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message=message;
    }
    public BigDecimal getNewBalance(){
        return newBalance;
    }
    public void setNewBalance(BigDecimal newBalance){
        this.newBalance=newBalance;
    }
    public Instant getProcessedAt(){
        return processedAt;
    }
    public void setProcessedAt(Instant processedAt){
        this.processedAt=processedAt;}

    public String getRequestId(){
        return requestId;
    }
    public void setRequestId(String requestId){
        this.requestId=requestId;
    }
}
