package com.auzmor.calendar.daos;

import com.auzmor.calendar.models.Account;
import org.springframework.stereotype.Component;

@Component
public interface AccountDao {
  Account getAccount(String nylasAccountId);
  void updateAccount(String nylasAccountId, String cursorId);
}
