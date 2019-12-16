package com.auzmor.calendar.daos;

import com.auzmor.calendar.models.UserAccount;
import org.springframework.stereotype.Component;

@Component
public interface AccountDao {
  UserAccount getAccount(String nylasAccountId);
  void updateAccount(String nylasAccountId, String cursorId);

  void  addNylasAccount(final UserAccount userAccount);

}
