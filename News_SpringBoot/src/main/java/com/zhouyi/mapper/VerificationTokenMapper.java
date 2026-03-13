package com.zhouyi.mapper;

import com.zhouyi.entity.VerificationToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Verification token mapper interface for database operations
 */
@Mapper
public interface VerificationTokenMapper {

    /**
     * Insert a new verification token
     * 
     * @param token verification token object
     * @return number of affected rows
     */
    int insertToken(VerificationToken token);

    /**
     * Find token by user ID and token type
     * 
     * @param userId    user ID
     * @param tokenType token type (EMAIL or PHONE)
     * @return verification token object or null
     */
    VerificationToken selectTokenByUserIdAndType(@Param("userId") Integer userId, @Param("tokenType") String tokenType);

    /**
     * Find token by user ID, token type and token value
     * 
     * @param userId    user ID
     * @param tokenType token type (EMAIL or PHONE)
     * @param token     token value
     * @return verification token object or null
     */
    VerificationToken selectTokenByUserIdTypeAndToken(@Param("userId") Integer userId,
            @Param("tokenType") String tokenType,
            @Param("token") String token);

    /**
     * Delete token by user ID and token type
     * 
     * @param userId    user ID
     * @param tokenType token type (EMAIL or PHONE)
     * @return number of affected rows
     */
    int deleteTokenByUserIdAndType(@Param("userId") Integer userId, @Param("tokenType") String tokenType);

    /**
     * Delete expired tokens
     * 
     * @return number of affected rows
     */
    int deleteExpiredTokens();

    /**
     * Update token for existing user and type
     * 
     * @param token verification token object
     * @return number of affected rows
     */
    int updateToken(VerificationToken token);

    /**
     * Find token by new_value (e.g. email) and token type
     *
     * @param newValue  email or phone
     * @param tokenType token type
     * @return verification token object or null
     */
    VerificationToken selectTokenByNewValueAndType(@Param("newValue") String newValue, @Param("tokenType") String tokenType);

    /**
     * Delete token by new_value and token type
     *
     * @param newValue  email or phone
     * @param tokenType token type
     * @return number of affected rows
     */
    int deleteTokenByNewValueAndType(@Param("newValue") String newValue, @Param("tokenType") String tokenType);

    /**
     * Update token finding by new_value and type
     *
     * @param token verification token object
     * @return number of affected rows
     */
    int updateTokenByNewValueAndType(VerificationToken token);
}
