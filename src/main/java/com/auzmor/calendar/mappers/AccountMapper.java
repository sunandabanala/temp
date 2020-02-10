package com.auzmor.calendar.mappers;

import com.auzmor.calendar.models.UserAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccountMapper {
  List<UserAccount> getAccount(@Param("accountId")String accountId);
  void updateAccount(@Param("accountId") String accountId, @Param("cursorId") String cursorId);
  void saveUserAccount(@Param("userAccount") final UserAccount userAccount);
  void desyncUser(@Param("userId")String userId);

}
