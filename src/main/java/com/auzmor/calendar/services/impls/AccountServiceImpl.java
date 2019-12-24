package com.auzmor.calendar.services.impls;

import com.auzmor.calendar.daos.AccountDao;
import com.auzmor.calendar.models.UserAccount;
import com.auzmor.calendar.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

  @Autowired
  private AccountDao accountDao;

  @Override
  public List<UserAccount> getAccount(String nylasAccountId) {
    return accountDao.getAccount(nylasAccountId);
  }

  @Override
  public void addNylasAccount(UserAccount userAccount) {
    String uuid = UUID.randomUUID().toString().replace("-", "");
    userAccount.setUuid(uuid);
    accountDao.addNylasAccount(userAccount);
  }

}
