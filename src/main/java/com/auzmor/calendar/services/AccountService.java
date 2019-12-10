package com.auzmor.calendar.services;

import com.auzmor.calendar.models.UserAccount;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {

  UserAccount getAccount(String nylasAccountId);

  void addNylasAccount(final UserAccount userAccount);

}
