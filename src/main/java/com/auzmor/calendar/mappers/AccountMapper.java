package com.auzmor.calendar.mappers;

import com.auzmor.calendar.models.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountMapper {
  Account getAccount(@Param("accountId")String accountId);
  void updateAccount(@Param("accountId") String accountId, @Param("cursorId") String cursorId);
}
