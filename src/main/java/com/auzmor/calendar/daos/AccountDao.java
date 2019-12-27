package com.auzmor.calendar.daos;

import com.auzmor.calendar.models.UserAccount;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AccountDao {
  List<UserAccount> getAccount(String nylasAccountId);
  void updateAccount(String nylasAccountId, String cursorId);

  void addNylasAccount(final UserAccount userAccount);

  void desync(final String userId);

}
