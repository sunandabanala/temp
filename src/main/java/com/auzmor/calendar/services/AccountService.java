package com.auzmor.calendar.services;

import com.auzmor.calendar.models.UserAccount;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountService {

  List<UserAccount> getAccount(String nylasAccountId);

  void addNylasAccount(final UserAccount userAccount);

  void desync(final String userId);

}
