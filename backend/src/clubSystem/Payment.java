package clubSystem;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class Payment {
    private int paymentId;
    private String paymentCode;
    private int memId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String method;
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public int getMemId() { return memId; }
    public void setMemId(int memId) { this.memId = memId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
}
