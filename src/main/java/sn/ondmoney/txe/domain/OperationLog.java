package sn.ondmoney.txe.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "operation_log", uniqueConstraints = {@UniqueConstraint(columnNames = {"request_id"})})
public class OperationLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false, unique = true)
    private String requestId;

    private String operationType; // DEBIT / CREDIT / TRANSFER
    private String userId;
    private BigDecimal amount;
    private Instant processedAt;
    private String resultCode;
    private String details;


    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getRequestId(){return requestId;}
    public void setRequestId(String requestId){this.requestId=requestId;}
    public String getOperationType(){return operationType;}
    public void setOperationType(String operationType){this.operationType=operationType;}
    public String getUserId(){return userId;}
    public void setUserId(String userId){this.userId=userId;}
    public BigDecimal getAmount(){return amount;}
    public void setAmount(BigDecimal amount){this.amount=amount;}
    public Instant getProcessedAt(){return processedAt;}
    public void setProcessedAt(Instant processedAt){this.processedAt=processedAt;}
    public String getResultCode(){return resultCode;}
    public void setResultCode(String resultCode){this.resultCode=resultCode;}
    public String getDetails(){return details;}
    public void setDetails(String details){this.details=details;}
}
