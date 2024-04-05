package com.frontleaves.greenchaincarbonledger.models.voData.returnData;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BackOpenAnAccount {
    public BackUserVO organize;
    public AccountOpen accountOpen;

    @Data
    @Accessors(chain = true)
    public static class AccountOpen {
        public String accountBank;
        public String accountNumber;
    }
}
