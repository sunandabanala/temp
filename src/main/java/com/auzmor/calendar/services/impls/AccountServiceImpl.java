package com.auzmor.calendar.services.impls;

import com.auzmor.calendar.daos.AccountDao;
import com.auzmor.calendar.models.Account;
import com.auzmor.calendar.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

  @Autowired
  private AccountDao accountDao;

  @Override
  public Account getAccount(String nylasAccountId) {
    return accountDao.getAccount(nylasAccountId);
  }
}
