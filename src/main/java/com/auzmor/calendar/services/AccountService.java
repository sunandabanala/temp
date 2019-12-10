package com.auzmor.calendar.services;

import com.auzmor.calendar.models.Account;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {

  Account getAccount(String nylasAccountId);
}
