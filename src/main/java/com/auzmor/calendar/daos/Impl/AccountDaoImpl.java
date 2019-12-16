package com.auzmor.calendar.daos.Impl;

import com.auzmor.calendar.daos.AccountDao;
import com.auzmor.calendar.mappers.AccountMapper;
import com.auzmor.calendar.models.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountDaoImpl implements AccountDao {

  @Autowired
  private AccountMapper accountMapper;

  @Override
  public UserAccount getAccount(String nylasAccountId) {
    return accountMapper.getAccount(nylasAccountId);
  }

  @Override
  public void updateAccount(String nylasAccountId, String cursorId) {
    accountMapper.updateAccount(nylasAccountId, cursorId);
  }

  @Override
  public void addNylasAccount(UserAccount userAccount) {
    accountMapper.saveUserAccount(userAccount);
  }

}
