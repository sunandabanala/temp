package com.auzmor.calendar.daos;

import com.auzmor.calendar.models.UserAccount;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public interface AccountDao {
  List<UserAccount> getAccount(String nylasAccountId);
  void updateAccount(String nylasAccountId, String cursorId);

  void addNylasAccount(final UserAccount userAccount);

  void desync(final String userId);

  Set<String> getAccountIds(Set<String> accountIds);

}
