package com.auzmor.calendar.mappers;

import com.auzmor.calendar.models.UserAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountMapper {
  UserAccount getAccount(@Param("accountId")String accountId);
  void updateAccount(@Param("accountId") String accountId, @Param("cursorId") String cursorId);
  void saveUserAccount(@Param("UserAccount") final UserAccount userAccount);

}
