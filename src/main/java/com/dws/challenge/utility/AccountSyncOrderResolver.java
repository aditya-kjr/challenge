package com.dws.challenge.utility;

import com.dws.challenge.domain.Account;
import lombok.Data;

@Data
public class AccountSyncOrderResolver {

    private final Account first;
    private final Account second;

    public static AccountSyncOrderResolver resolve(Account a1, Account a2) {
        return a1.getAccountId().compareTo(a2.getAccountId()) > 0
                ? new AccountSyncOrderResolver(a1, a2)
                : new AccountSyncOrderResolver(a2, a1);
    }


}
